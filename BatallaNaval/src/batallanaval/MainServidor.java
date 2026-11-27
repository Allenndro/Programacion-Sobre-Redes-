package batallanaval;

import java.io.IOException;

public class MainServidor {
    public static final int PUERTO = 7777;

    public static void main(String[] args) {
        try {
            int puerto = args.length > 0 ? Integer.parseInt(args[0]) : PUERTO;
            String codigo = args.length > 1 ? args[1] : "batalla";
            if (puerto < 1 || puerto > 65535 || codigo.isBlank()) {
                throw new IllegalArgumentException("Ingresá un puerto válido y un código de acceso.");
            }
            try (Servidor servidor = new Servidor(puerto, codigo)) {
                servidor.iniciar();
            }
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}
