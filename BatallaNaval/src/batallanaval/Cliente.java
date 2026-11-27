package batallanaval;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Cliente {
    private static final Pattern COORDENADA = Pattern.compile("([A-J])\\s*(10|[1-9])");
    private final Conexion conexion;
    private final BufferedReader teclado;
    private final BlockingQueue<String> solicitudes = new LinkedBlockingQueue<>();

    Cliente(Conexion conexion, BufferedReader teclado) {
        this.conexion = conexion;
        this.teclado = teclado;
    }

    void jugar() throws IOException {
        Mensaje bienvenida = conexion.recibir();
        if (!Mensaje.LOGIN.equals(bienvenida.tipo)) {
            throw new IOException("El servidor no inició la sesión.");
        }
        System.out.println(Colores.CIAN + bienvenida.texto + Colores.RESET);
        String apodo = leer("Apodo (1–20 letras, números, _ o -): ");
        String codigo = leer("Código de acceso: ");
        if (apodo == null || codigo == null) {
            return;
        }
        conexion.enviar(new Mensaje(Mensaje.LOGIN, apodo, codigo));

        Thread lector = new Thread(this::leerMovimientos, "Consola-jugador");
        lector.setDaemon(true);
        lector.start();
        try {
            while (true) {
                Mensaje respuesta = conexion.recibir();
                System.out.println(respuesta.texto);
                if (Mensaje.FIN.equals(respuesta.tipo)) {
                    return;
                }
                if (Mensaje.COLOCAR.equals(respuesta.tipo) || Mensaje.DISPARAR.equals(respuesta.tipo)) {
                    solicitudes.add(respuesta.tipo);
                }
            }
        } catch (EOFException e) {
            System.out.println("Se cerró la conexión con el servidor.");
        } finally {
            lector.interrupt();
        }
    }

    private void leerMovimientos() {
        try {
            while (true) {
                String tipo = solicitudes.take();
                Mensaje movimiento = pedirMovimiento(tipo);
                conexion.enviar(movimiento);
                if (Mensaje.SALIR.equals(movimiento.tipo)) {
                    return;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            System.err.println("Error enviando el movimiento: " + e.getMessage());
            try {
                conexion.close();
            } catch (IOException cierre) {
                System.err.println("Error cerrando la conexión: " + cierre.getMessage());
            }
        }
    }

    private Mensaje pedirMovimiento(String tipo) throws IOException {
        boolean colocar = Mensaje.COLOCAR.equals(tipo);
        while (true) {
            String entrada = leer("> ");
            if (entrada == null || "/salir".equalsIgnoreCase(entrada)) {
                return new Mensaje(Mensaje.SALIR, null);
            }
            String jugada = entrada.toUpperCase(Locale.ROOT);
            boolean horizontal = false;
            if (colocar && (jugada.endsWith("H") || jugada.endsWith("V"))) {
                horizontal = jugada.endsWith("H");
                jugada = jugada.substring(0, jugada.length() - 1).trim();
            } else if (colocar) {
                System.out.println("Indicá coordenada y orientación. Ejemplo: A1 H.");
                continue;
            }
            Matcher matcher = COORDENADA.matcher(jugada);
            if (matcher.matches()) {
                int fila = matcher.group(1).charAt(0) - 'A';
                int col = Integer.parseInt(matcher.group(2)) - 1;
                return new Mensaje(tipo, fila, col, horizontal);
            }
            System.out.println("Coordenada inválida. Usá filas A–J y columnas 1–10.");
        }
    }

    private String leer(String mensaje) throws IOException {
        System.out.print(mensaje);
        System.out.flush();
        String texto = teclado.readLine();
        return texto == null ? null : texto.trim();
    }
}
