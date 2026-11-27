package batallanaval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainCliente {
    public static void main(String[] args) {
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try {
            String host = args.length > 0 ? args[0] : leer(teclado, "IP del servidor [127.0.0.1]: ");
            String puertoTexto = args.length > 1 ? args[1] : leer(teclado, "Puerto [7777]: ");
            if (host == null || puertoTexto == null) {
                return;
            }
            if (host.isBlank()) {
                host = "127.0.0.1";
            }
            int puerto = puertoTexto.isBlank() ? MainServidor.PUERTO : Integer.parseInt(puertoTexto);
            if (puerto < 1 || puerto > 65535) {
                throw new IllegalArgumentException("Puerto fuera de rango.");
            }
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, puerto), 5000);
                socket.setSoTimeout(5000);
                try (Conexion conexion = new Conexion(socket)) {
                    socket.setSoTimeout(0);
                    new Cliente(conexion, teclado).jugar();
                }
            }
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("No se pudo continuar: " + e.getMessage());
        }
    }

    private static String leer(BufferedReader teclado, String mensaje) throws IOException {
        System.out.print(mensaje);
        System.out.flush();
        String dato = teclado.readLine();
        return dato == null ? null : dato.trim();
    }
}
