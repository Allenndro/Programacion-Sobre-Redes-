package batallanaval;

import java.io.IOException;
import java.net.Socket;

final class Jugador {
    final String nombre;
    final Tablero tablero = new Tablero();
    final Conexion conexion;
    final Socket socket;
    Sala sala;
    int barcosColocados;

    Jugador(String nombre, Conexion conexion, Socket socket) {
        this.nombre = nombre;
        this.conexion = conexion;
        this.socket = socket;
    }

    void enviar(Mensaje mensaje) {
        try {
            conexion.enviar(mensaje);
        } catch (IOException e) {
            cerrar();
        }
    }

    void cerrar() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión de " + nombre + ": " + e.getMessage());
        }
    }
}
