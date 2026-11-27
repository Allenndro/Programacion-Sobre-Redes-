package comerciotcp;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

final class Canal implements Closeable {
    private static final int MAX_TRAMA = 131_072;
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;
    private final Cifrado cifrado;
    private final byte dirSalida;
    private final byte dirEntrada;
    private long enviados;
    private long recibidos;
    private byte[] sesion = new byte[0];

    Canal(Socket socket, Cifrado cifrado, boolean servidor) throws IOException {
        this.socket = socket;
        this.cifrado = cifrado;
        dirSalida = servidor ? (byte) 1 : (byte) 2;
        dirEntrada = servidor ? (byte) 2 : (byte) 1;
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII), true);
    }

    void negociar(boolean servidor) throws IOException, GeneralSecurityException {
        if (servidor) {
            byte[] reto = Cifrado.nuevoNonce();
            enviar("CANAL", Base64.getEncoder().encodeToString(reto));
            sesion = reto;
            Mensaje mensaje = recibir();
            if (!"CONFIRMAR_CANAL".equals(mensaje.tipo())) {
                throw new GeneralSecurityException("Confirmación de canal inválida.");
            }
            sesion = unirNonces(reto, leerNonce(mensaje.texto()));
        } else {
            Mensaje mensaje = recibir();
            if (!"CANAL".equals(mensaje.tipo())) {
                throw new GeneralSecurityException("Inicio de canal inválido.");
            }
            byte[] reto = leerNonce(mensaje.texto());
            byte[] cliente = Cifrado.nuevoNonce();
            sesion = reto;
            enviar("CONFIRMAR_CANAL", Base64.getEncoder().encodeToString(cliente));
            sesion = unirNonces(reto, cliente);
        }
    }

    void enviar(String tipo, String texto) throws IOException, GeneralSecurityException {
        byte[] trama = cifrado.cifrar(tipo + "\t" + texto, dirSalida, enviados, sesion);
        String linea = Base64.getEncoder().encodeToString(trama);
        if (linea.length() > MAX_TRAMA) {
            throw new IOException("El mensaje supera el tamaño permitido.");
        }
        salida.println(linea);
        if (salida.checkError()) {
            throw new IOException("La conexión se cerró al enviar.");
        }
        enviados++;
    }

    Mensaje recibir() throws IOException, GeneralSecurityException {
        StringBuilder linea = new StringBuilder();
        int caracter;
        while ((caracter = entrada.read()) != '\n') {
            if (caracter == -1) {
                throw new EOFException("El otro extremo cerró la conexión.");
            }
            if (caracter != '\r') {
                if (linea.length() == MAX_TRAMA) {
                    throw new IOException("Trama demasiado extensa.");
                }
                linea.append((char) caracter);
            }
        }
        try {
            byte[] trama = Base64.getDecoder().decode(linea.toString());
            String mensaje = cifrado.descifrar(trama, dirEntrada, recibidos, sesion);
            int separador = mensaje.indexOf('\t');
            if (separador <= 0) {
                throw new GeneralSecurityException("Formato de mensaje inválido.");
            }
            recibidos++;
            return new Mensaje(mensaje.substring(0, separador), mensaje.substring(separador + 1));
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException("La trama Base64 no es válida.", e);
        }
    }

    private static byte[] leerNonce(String base64) throws GeneralSecurityException {
        try {
            byte[] nonce = Base64.getDecoder().decode(base64);
            if (nonce.length == 32) {
                return nonce;
            }
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException("Identificador de sesión inválido.", e);
        }
        throw new GeneralSecurityException("Identificador de sesión inválido.");
    }

    private static byte[] unirNonces(byte[] uno, byte[] dos) {
        byte[] resultado = new byte[uno.length + dos.length];
        System.arraycopy(uno, 0, resultado, 0, uno.length);
        System.arraycopy(dos, 0, resultado, uno.length, dos.length);
        return resultado;
    }

    @Override
    public void close() throws IOException {
        try {
            socket.close();
        } finally {
            try {
                entrada.close();
            } finally {
                salida.close();
            }
        }
    }

    record Mensaje(String tipo, String texto) {}
}
