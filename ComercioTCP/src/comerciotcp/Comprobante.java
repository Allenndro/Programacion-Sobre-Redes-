package comerciotcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

final class Comprobante {
    private static final int TAM_BLOQUE = 4096;
    private static final int MAX_ARCHIVO = 10 * 1024 * 1024;

    private Comprobante() {}

    static void enviar(Canal canal, Path ruta) throws IOException, GeneralSecurityException {
        if (!Files.isRegularFile(ruta) || !Files.isReadable(ruta)) {
            Consola.evento(Consola.ROJO, "ERROR", "Elegí un archivo local que se pueda leer.");
            return;
        }
        long bytes = Files.size(ruta);
        if (bytes > MAX_ARCHIVO) {
            Consola.evento(Consola.ROJO, "ERROR", "El archivo supera los 10 MB.");
            return;
        }
        String nombre = ruta.getFileName().toString();
        try (InputStream archivo = Files.newInputStream(ruta)) {
            Consola.evento(Consola.MORADO, "COMANDO ENVIADO", "subir comprobante | " + nombre + " | " + bytes + " bytes");
            canal.enviar("ARCHIVO", bytes + "\t" + nombre);
            Canal.Mensaje respuesta = canal.recibir();
            mostrarRespuesta(respuesta);
            if (!"LISTO".equals(respuesta.tipo())) {
                return;
            }

            long restantes = bytes;
            int numero = 0;
            byte[] bloque = new byte[TAM_BLOQUE];
            while (restantes > 0) {
                int longitud = archivo.readNBytes(bloque, 0, (int) Math.min(bloque.length, restantes));
                if (longitud == 0) {
                    canal.enviar("CANCELAR_ARCHIVO", "");
                    mostrarRespuesta(canal.recibir());
                    return;
                }
                canal.enviar("BLOQUE", Base64.getEncoder().encodeToString(Arrays.copyOf(bloque, longitud)));
                Consola.evento(Consola.MORADO, "BLOQUE ENVIADO", "N.º " + ++numero + " | " + longitud + " bytes");
                Canal.Mensaje acuse = canal.recibir();
                mostrarRespuesta(acuse);
                if (!"RECIBIDO".equals(acuse.tipo())) {
                    return;
                }
                restantes -= longitud;
            }
            canal.enviar("FIN_ARCHIVO", "");
            mostrarRespuesta(canal.recibir());
        }
    }

    static String recibir(Canal canal, String datos, Path destino) throws IOException, GeneralSecurityException {
        String[] campos = datos.split("\t", 2);
        long total;
        try {
            total = Long.parseLong(campos[0]);
        } catch (NumberFormatException e) {
            rechazar(canal, "Tamaño de comprobante inválido.");
            return null;
        }
        if (campos.length != 2 || total < 0 || total > MAX_ARCHIVO || !nombreValido(campos[1])) {
            rechazar(canal, "Comprobante inválido. Límite: 10 MB.");
            return null;
        }
        String nombre = campos[1];
        Path temporal;
        try {
            Files.createDirectories(destino);
            temporal = Files.createTempFile(destino, ".subida-", ".part");
        } catch (IOException e) {
            rechazar(canal, "El servidor no pudo preparar el comprobante.");
            return null;
        }

        try {
            try (OutputStream archivo = Files.newOutputStream(temporal)) {
                canal.enviar("LISTO", "Servidor listo para recibir " + nombre + " (" + total + " bytes).");
                Consola.evento(Consola.VERDE, "ARCHIVO ACEPTADO", nombre + " | " + total + " bytes");
                long recibidos = 0;
                int numero = 0;
                Canal.Mensaje trama = canal.recibir();
                while ("BLOQUE".equals(trama.tipo())) {
                    byte[] bytes;
                    try {
                        bytes = Base64.getDecoder().decode(trama.texto());
                    } catch (IllegalArgumentException e) {
                        rechazar(canal, "El bloque no tiene Base64 válido.");
                        return null;
                    }
                    if (bytes.length == 0 || bytes.length > TAM_BLOQUE || recibidos + bytes.length > total) {
                        rechazar(canal, "El tamaño del bloque no coincide con el comprobante.");
                        return null;
                    }
                    archivo.write(bytes);
                    recibidos += bytes.length;
                    Consola.evento(Consola.CIAN, "BLOQUE RECIBIDO", "N.º " + ++numero + " | " + recibidos + "/" + total + " bytes");
                    canal.enviar("RECIBIDO", "Bloque " + numero + " confirmado.");
                    trama = canal.recibir();
                }
                if (!"FIN_ARCHIVO".equals(trama.tipo()) || recibidos != total) {
                    rechazar(canal, "La transferencia fue cancelada o quedó incompleta.");
                    return null;
                }
            }
            Path finalizado = destino.resolve(UUID.randomUUID() + "_" + nombre);
            String respuesta;
            try {
                Files.move(temporal, finalizado);
                respuesta = "Comprobante guardado: " + finalizado.getFileName() + " (" + total + " bytes).";
            } catch (IOException e) {
                rechazar(canal, "El servidor no pudo guardar el comprobante.");
                return null;
            }
            canal.enviar("RESPUESTA", respuesta);
            Consola.evento(Consola.VERDE, "ARCHIVO GUARDADO", respuesta);
            return respuesta;
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static boolean nombreValido(String nombre) {
        return !nombre.isBlank() && nombre.length() <= 100 && !nombre.equals(".") && !nombre.equals("..")
                && nombre.codePoints().noneMatch(c -> Character.isISOControl(c) || "<>:\"/\\|?*".indexOf(c) >= 0);
    }

    private static void rechazar(Canal canal, String mensaje) throws IOException, GeneralSecurityException {
        Consola.evento(Consola.ROJO, "ARCHIVO RECHAZADO", mensaje);
        canal.enviar("ERROR", mensaje);
    }

    private static void mostrarRespuesta(Canal.Mensaje respuesta) {
        Consola.evento("ERROR".equals(respuesta.tipo()) ? Consola.ROJO : Consola.CIAN,
                "SERVIDOR", respuesta.texto());
    }
}
