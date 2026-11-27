package comerciotcp;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

final class Consola {
    static final String RESET = "\u001B[0m";
    static final String VERDE = "\u001B[32m";
    static final String CIAN = "\u001B[36m";
    static final String MORADO = "\u001B[35m";
    static final String ROJO = "\u001B[31m";
    static final String AMARILLO = "\u001B[33m";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final PrintStream SALIDA = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final BufferedReader teclado = new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8));

    static void encabezado(String lado) {
        SALIDA.printf("%n%s================ COMERCIO TCP | %s ================%s%n",
                MORADO, lado, RESET);
        SALIDA.println("HORA    \tEVENTO           \tDETALLE");
        SALIDA.println("--------\t-----------------\t--------------------------------------");
    }

    static void evento(String color, String evento, String mensaje) {
        mensaje = mensaje == null ? "Sin detalle." : mensaje;
        String hora = LocalTime.now().format(HORA);
        for (String linea : mensaje.split("\\R", -1)) {
            String visible = linea.replaceAll("\\p{Cntrl}", " ");
            SALIDA.printf("%s\t%s%-17s%s\t%s%n", hora, color, evento, RESET, visible);
        }
    }

    String leer(String etiqueta) throws IOException {
        SALIDA.print(etiqueta);
        SALIDA.flush();
        String respuesta = teclado.readLine();
        if (respuesta == null) {
            throw new EOFException("Se cerró la entrada de consola.");
        }
        return respuesta.trim();
    }

    char[] secreto(String etiqueta) throws IOException {
        java.io.Console terminal = System.console();
        if (terminal == null) {
            return leer(etiqueta).toCharArray();
        }
        char[] respuesta = terminal.readPassword("%s", etiqueta);
        if (respuesta == null) {
            throw new EOFException("Se cerró la entrada de consola.");
        }
        return respuesta;
    }

    int puerto() throws IOException {
        String valor = leer("Puerto [7777]: ");
        int puerto = valor.isEmpty() ? 7777 : Integer.parseInt(valor);
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535.");
        }
        return puerto;
    }
}
