package comerciotcp;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class Cliente {
    private static final String STOCK = "ver stock de un producto";
    private static final String NOMBRE_PREDETERMINADO = "Alejandro Alarcón";

    public static void main(String[] args) {
        Consola.encabezado("CLIENTE");
        Consola consola = new Consola();
        try {
            String direccion = consola.leer("IP del servidor [127.0.0.1]: ");
            int puerto = consola.puerto();
            String nombre = consola.leer("Nombre y apellido [" + NOMBRE_PREDETERMINADO + "]: ");
            if (nombre.isEmpty()) {
                nombre = NOMBRE_PREDETERMINADO;
            }
            if (!Servidor.nombreValido(nombre)) {
                throw new IllegalArgumentException("Ingresá tu nombre y apellido.");
            }
            char[] frase = consola.secreto("Frase compartida del canal: ");
            Cifrado cifrado;
            try {
                cifrado = new Cifrado(frase);
            } finally {
                Arrays.fill(frase, '\0');
            }

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(direccion.isBlank() ? "127.0.0.1" : direccion, puerto), 5000);
                socket.setSoTimeout(15_000);
                Consola.evento(Consola.VERDE, "CONEXIÓN", String.valueOf(socket.getRemoteSocketAddress()));
                try (Canal canal = new Canal(socket, cifrado, false)) {
                    canal.negociar(false);
                    Consola.evento(Consola.VERDE, "CANAL CIFRADO", "AES activo en ambos sentidos.");
                    canal.enviar("PRESENTAR", nombre);
                    Consola.evento(Consola.MORADO, "CHAT ENVIADO", "Hola, mi nombre es " + nombre);
                    Canal.Mensaje bienvenida = canal.recibir();
                    mostrar(bienvenida);
                    if (!"ERROR".equals(bienvenida.tipo())) {
                        conversar(canal, consola);
                    }
                }
            }
            Consola.evento(Consola.AMARILLO, "DESCONEXIÓN", "Se cerraron los recursos del cliente.");
        } catch (EOFException e) {
            Consola.evento(Consola.AMARILLO, "DESCONEXIÓN", e.getMessage());
        } catch (IOException | GeneralSecurityException | IllegalArgumentException e) {
            Consola.evento(Consola.ROJO, "ERROR", e.getMessage());
        }
    }

    private static void conversar(Canal canal, Consola consola) throws IOException, GeneralSecurityException {
        while (true) {
            String texto = consola.leer("cliente> ");
            if (texto.isBlank() || texto.length() > 500) {
                Consola.evento(Consola.AMARILLO, "ENTRADA INVÁLIDA", "Escribí entre 1 y 500 caracteres.");
                continue;
            }
            String comando = Inventario.normalizar(texto);
            switch (comando) {
                case "autenticar" -> {
                    char[] clave = consola.secreto("Contraseña de acceso: ");
                    Consola.evento(Consola.MORADO, "COMANDO ENVIADO", "autenticar");
                    try {
                        canal.enviar("AUTENTICAR", new String(clave));
                    } finally {
                        Arrays.fill(clave, '\0');
                    }
                    mostrar(canal.recibir());
                }
                case "subir comprobante" -> {
                    String ruta = consola.leer("Ruta local del comprobante: ");
                    if (ruta.startsWith("\"") && ruta.endsWith("\"") && ruta.length() >= 2) {
                        ruta = ruta.substring(1, ruta.length() - 1);
                    }
                    try {
                        Comprobante.enviar(canal, Path.of(ruta));
                    } catch (InvalidPathException e) {
                        Consola.evento(Consola.ROJO, "ERROR", "Ruta de archivo inválida.");
                    }
                }
                default -> {
                    if (comando.equals(STOCK)) {
                        String producto = consola.leer("Producto: ");
                        if (!producto.isEmpty()) {
                            texto = STOCK + " " + producto;
                        }
                    }
                    canal.enviar("CHAT", texto);
                    Consola.evento(Consola.MORADO, "CHAT ENVIADO", texto);
                    Canal.Mensaje respuesta = canal.recibir();
                    mostrar(respuesta);
                    if ("FIN".equals(respuesta.tipo())) {
                        return;
                    }
                }
            }
        }
    }

    private static void mostrar(Canal.Mensaje mensaje) {
        String color = "ERROR".equals(mensaje.tipo()) ? Consola.ROJO : Consola.CIAN;
        Consola.evento(color, "SERVIDOR", mensaje.texto());
    }
}
