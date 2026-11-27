package comerciotcp;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Servidor {
    private static final int SONDEO_MS = 500;
    private static final int ESPERA_CLIENTE_MS = 300_000;
    private static final int MAX_HISTORIAL = 30;

    private final Inventario inventario = new Inventario();
    private final Chatbot chatbot = new Chatbot();
    private final Cifrado cifrado;
    private final byte[] contrasena;
    private final Path comprobantes = Path.of("comprobantes").toAbsolutePath();

    private Servidor(Cifrado cifrado, byte[] contrasena) {
        this.cifrado = cifrado;
        this.contrasena = contrasena;
    }

    public static void main(String[] args) {
        Consola.encabezado("SERVIDOR");
        Consola consola = new Consola();
        try {
            String direccion = consola.leer("IP local para escuchar [127.0.0.1]: ");
            int puerto = consola.puerto();
            char[] frase = consola.secreto("Frase compartida del canal (mínimo 12 caracteres): ");
            Cifrado cifrado;
            try {
                cifrado = new Cifrado(frase);
            } finally {
                Arrays.fill(frase, '\0');
            }
            char[] clave = consola.secreto("Contraseña para el comando autenticar: ");
            byte[] bytes = new String(clave).getBytes(StandardCharsets.UTF_8);
            Arrays.fill(clave, '\0');
            if (bytes.length == 0 || bytes.length > 128) {
                throw new IllegalArgumentException("Configurá una contraseña de 1 a 128 bytes.");
            }
            try {
                new Servidor(cifrado, bytes).escuchar(direccion.isBlank() ? "127.0.0.1" : direccion, puerto);
            } finally {
                Arrays.fill(bytes, (byte) 0);
            }
        } catch (IOException | GeneralSecurityException | IllegalArgumentException e) {
            Consola.evento(Consola.ROJO, "ERROR", e.getMessage());
        }
    }

    private void escuchar(String direccion, int puerto) throws IOException {
        try (ServerSocket servidor = new ServerSocket()) {
            servidor.bind(new InetSocketAddress(direccion, puerto));
            servidor.setSoTimeout(SONDEO_MS);
            Consola.evento(Consola.VERDE, "ESCUCHANDO", direccion + ":" + puerto + " | atención secuencial");
            Consola.evento(Consola.AMARILLO, "ESPERA ACTIVA", "Esperando conexión. Ctrl+C para apagar el servidor.");
            while (true) {
                Socket cliente;
                try {
                    cliente = servidor.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                atender(cliente);
                Consola.evento(Consola.AMARILLO, "ESPERA ACTIVA", "Sesión cerrada. Esperando al siguiente cliente.");
            }
        }
    }

    private void atender(Socket cliente) {
        Consola.evento(Consola.VERDE, "CONEXIÓN", String.valueOf(cliente.getRemoteSocketAddress()));
        try (Socket socket = cliente) {
            socket.setSoTimeout(15_000);
            try (Canal canal = new Canal(socket, cifrado, true)) {
                canal.negociar(true);
                Consola.evento(Consola.VERDE, "CANAL CIFRADO", "AES activo en ambos sentidos.");
                socket.setSoTimeout(ESPERA_CLIENTE_MS);
                conversar(canal);
            }
        } catch (EOFException e) {
            Consola.evento(Consola.AMARILLO, "DESCONEXIÓN", e.getMessage());
        } catch (SocketTimeoutException e) {
            Consola.evento(Consola.ROJO, "TIEMPO AGOTADO", "Se cerró la conexión por inactividad.");
        } catch (IOException | GeneralSecurityException e) {
            Consola.evento(Consola.ROJO, "ERROR DE SESIÓN", e.getMessage());
        }
    }

    private void conversar(Canal canal) throws IOException, GeneralSecurityException {
        ArrayDeque<String> historial = new ArrayDeque<>();
        Canal.Mensaje presentacion = canal.recibir();
        if (!"PRESENTAR".equals(presentacion.tipo()) || !nombreValido(presentacion.texto())) {
            responder(canal, "ERROR", "El nombre y apellido son obligatorios.");
            return;
        }
        String nombre = presentacion.texto();
        String saludo = "Hola, mi nombre es " + nombre;
        Consola.evento(Consola.CIAN, "CHAT RECIBIDO", saludo);
        guardar(historial, saludo);
        responderYGuardar(canal, historial, "RESPUESTA",
                "Bienvenido, " + nombre + ". Escribí ayuda para ver el menú de soporte.");
        boolean autorizado = false;
        for (;;) {
            Canal.Mensaje pedido = canal.recibir();
            switch (pedido.tipo()) {
                case "AUTENTICAR" -> {
                    Consola.evento(Consola.CIAN, "COMANDO RECIBIDO", "autenticar");
                    byte[] credencial = pedido.texto().getBytes(StandardCharsets.UTF_8);
                    autorizado = MessageDigest.isEqual(contrasena, credencial);
                    Arrays.fill(credencial, (byte) 0);
                    responder(canal, autorizado ? "RESPUESTA" : "ERROR",
                            autorizado ? "Acceso validado para " + nombre + "." : "Contraseña incorrecta.");
                }
                case "ARCHIVO" -> {
                    Consola.evento(Consola.CIAN, "COMANDO RECIBIDO", "subir comprobante");
                    if (autorizado) {
                        String resultado = Comprobante.recibir(canal, pedido.texto(), comprobantes);
                        if (resultado != null) {
                            guardar(historial, "Servidor: " + resultado);
                        }
                    } else {
                        responder(canal, "ERROR", "Primero usá autenticar.");
                    }
                }
                case "CHAT" -> {
                    String texto = pedido.texto().strip();
                    if (texto.isEmpty() || texto.length() > 500 || texto.codePoints().anyMatch(Character::isISOControl)) {
                        responder(canal, "ERROR", "Escribí un mensaje de 1 a 500 caracteres.");
                        continue;
                    }
                    String comando = Inventario.normalizar(texto);
                    Consola.evento(Consola.CIAN, "RECIBIDO", nombre + " → " + texto);
                    if (comando.equals("salir")) {
                        responder(canal, "FIN", "Conexión cerrada. Hasta luego, " + nombre + ".");
                        return;
                    }
                    if (comando.equals("ver historial de chat")) {
                        responder(canal, "RESPUESTA", "Historial de esta sesión:\n" + String.join("\n", historial));
                        continue;
                    }
                    guardar(historial, nombre + ": " + texto);
                    String respuesta;
                    if (comando.equals("ver stock de un producto")) {
                        respuesta = "Indicá el producto con «ver stock de un producto teclado».";
                    } else if (comando.startsWith("ver stock de un producto ")) {
                        if (!autorizado) {
                            responderYGuardar(canal, historial, "ERROR", "Primero usá autenticar.");
                            continue;
                        }
                        respuesta = inventario.consultar(comando.substring("ver stock de un producto ".length()));
                    } else {
                        respuesta = chatbot.responder(texto);
                    }
                    responderYGuardar(canal, historial, "RESPUESTA", respuesta);
                }
                default -> responder(canal, "ERROR", "Comando de protocolo desconocido.");
            }
        }
    }

    static boolean nombreValido(String nombre) {
        return nombre.length() <= 80 && nombre.matches("[\\p{L}\\p{M}][\\p{L}\\p{M}'’-]*(?: +[\\p{L}\\p{M}][\\p{L}\\p{M}'’-]*)+");
    }

    private static void guardar(ArrayDeque<String> historial, String texto) {
        if (historial.size() == MAX_HISTORIAL) {
            historial.removeFirst();
        }
        historial.addLast(texto);
    }

    private static void responderYGuardar(Canal canal, ArrayDeque<String> historial, String tipo, String texto)
            throws IOException, GeneralSecurityException {
        guardar(historial, "Servidor: " + texto);
        responder(canal, tipo, texto);
    }

    private static void responder(Canal canal, String tipo, String texto) throws IOException, GeneralSecurityException {
        canal.enviar(tipo, texto);
        Consola.evento("ERROR".equals(tipo) ? Consola.ROJO : Consola.MORADO, "ENVIADO", texto);
    }
}
