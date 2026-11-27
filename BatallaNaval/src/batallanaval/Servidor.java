package batallanaval;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

final class Servidor implements AutoCloseable {
    private final ServerSocket servidor;
    private final String codigo;
    private final ArrayList<Jugador> jugadores = new ArrayList<>();
    private Jugador esperando;
    private int siguienteSala = 1;

    Servidor(int puerto, String codigo) throws IOException {
        servidor = new ServerSocket(puerto);
        this.codigo = codigo;
    }

    void iniciar() throws IOException {
        System.out.println(Colores.VERDE + "Servidor escuchando en puerto " + servidor.getLocalPort()
                + Colores.RESET);
        while (!servidor.isClosed()) {
            Socket socket = servidor.accept();
            new Thread(() -> atender(socket), "Jugador-" + socket.getPort()).start();
        }
    }

    private void atender(Socket socket) {
        Jugador jugador = null;
        try (Socket cliente = socket) {
            cliente.setSoTimeout(30_000);
            try (Conexion conexion = new Conexion(cliente)) {
                jugador = autenticar(cliente, conexion);
                if (jugador == null) {
                    return;
                }
                cliente.setSoTimeout(0);
                while (!cliente.isClosed()) {
                    Mensaje mensaje = conexion.recibir();
                    if (Mensaje.SALIR.equals(mensaje.tipo)) {
                        break;
                    }
                    Sala sala = obtenerSala(jugador);
                    if (sala == null) {
                        jugador.enviar(new Mensaje(Mensaje.AVISO, "Esperando a otro jugador."));
                    } else {
                        sala.procesar(jugador, mensaje);
                    }
                }
            }
        } catch (EOFException | SocketException e) {
            if (jugador == null) {
                System.err.println("Conexión cerrada antes de iniciar sesión.");
            }
        } catch (IOException e) {
            System.err.println("Error de comunicación: " + e.getMessage());
        } finally {
            if (jugador != null) {
                desconectar(jugador);
            }
        }
    }

    private Jugador autenticar(Socket socket, Conexion conexion) throws IOException {
        conexion.enviar(new Mensaje(Mensaje.LOGIN, "Bienvenido a Batalla Naval."));
        Mensaje login = conexion.recibir();
        if (!Mensaje.LOGIN.equals(login.tipo) || login.texto == null
                || !login.texto.matches("[\\p{L}0-9_-]{1,20}") || !codigo.equals(login.codigo)) {
            conexion.enviar(new Mensaje(Mensaje.FIN, "Apodo inválido o código de acceso incorrecto."));
            return null;
        }
        return registrar(new Jugador(login.texto, conexion, socket));
    }

    private synchronized Jugador registrar(Jugador jugador) throws IOException {
        for (Jugador conectado : jugadores) {
            if (conectado.nombre.equalsIgnoreCase(jugador.nombre)) {
                jugador.conexion.enviar(new Mensaje(Mensaje.FIN, "Ese apodo ya está conectado."));
                return null;
            }
        }
        jugadores.add(jugador);
        if (esperando == null) {
            esperando = jugador;
            jugador.enviar(new Mensaje(Mensaje.AVISO, "Entrada aceptada. Esperando a otro jugador..."));
        } else {
            Sala sala = new Sala(siguienteSala++, esperando, jugador);
            esperando.sala = sala;
            jugador.sala = sala;
            System.out.println("Partida iniciada: " + esperando.nombre + " vs. " + jugador.nombre);
            esperando = null;
            sala.iniciar();
        }
        return jugador;
    }

    private synchronized Sala obtenerSala(Jugador jugador) {
        return jugador.sala;
    }

    private void desconectar(Jugador jugador) {
        Sala sala;
        synchronized (this) {
            jugadores.remove(jugador);
            if (esperando == jugador) {
                esperando = null;
            }
            sala = jugador.sala;
        }
        if (sala != null) {
            sala.desconectar(jugador);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            servidor.close();
        } finally {
            synchronized (this) {
                for (Jugador jugador : jugadores) {
                    jugador.cerrar();
                }
            }
        }
    }
}
