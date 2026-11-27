package batallanaval;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

final class Conexion implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;

    Conexion(Socket socket) throws IOException {
        this.socket = socket;
        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        entrada = new ObjectInputStream(socket.getInputStream());
        entrada.setObjectInputFilter(info -> {
            if (info.depth() > 2 || info.references() > 20_000 || info.streamBytes() > 2_000_000) {
                return ObjectInputFilter.Status.REJECTED;
            }
            Class<?> clase = info.serialClass();
            if (clase == null) {
                return ObjectInputFilter.Status.UNDECIDED;
            }
            return clase == Mensaje.class || clase == String.class
                    ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED;
        });
    }

    Mensaje recibir() throws IOException {
        try {
            Object objeto = entrada.readObject();
            if (objeto instanceof Mensaje mensaje && mensaje.tipo != null) {
                return mensaje;
            }
            throw new IOException("Mensaje inválido.");
        } catch (ClassNotFoundException e) {
            throw new IOException("Tipo de mensaje desconocido.", e);
        }
    }

    synchronized void enviar(Mensaje mensaje) throws IOException {
        salida.writeObject(mensaje);
        salida.reset();
        salida.flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
