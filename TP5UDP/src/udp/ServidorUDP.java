package udp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorUDP {

	public static void main(String args[]) {

		PrintStream ps = new PrintStream(System.out);

		try {

			DatagramSocket sock = new DatagramSocket(9876);

			byte[] buf = new byte[1024];
			String texto = "";

			ps.println("Servidor UDP iniciado en el puerto 9876");
			ps.println("Esperando mensajes...");

			while (true) {

				DatagramPacket datos = new DatagramPacket(
						buf, buf.length);

				sock.receive(datos);

				texto = new String(
						datos.getData(),
						datos.getOffset(),
						datos.getLength()
				);

				ps.printf(
						"Recibimos: %s | IP: %s | Puerto: %d %n",
						texto,
						datos.getAddress().getHostAddress(),
						datos.getPort()
				);

				if (texto.equals("FIN")) {
					break;
				}
			}

			sock.close();
			ps.println("Servidor cerrado.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}