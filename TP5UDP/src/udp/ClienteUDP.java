package udp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClienteUDP {

	public static void main(String args[]) {

		PrintStream ps = new PrintStream(System.out);
		Scanner teclado = new Scanner(System.in);

		try {

			DatagramSocket sock = new DatagramSocket();

			InetAddress servidor = InetAddress.getByName("localhost");

			String texto = "";

			while (true) {

				ps.print("Ingrese un mensaje: ");
				texto = teclado.nextLine();

				byte[] msg = texto.getBytes();

				DatagramPacket pack = new DatagramPacket(
						msg,
						msg.length,
						servidor,
						9876
				);

				sock.send(pack);

				if (texto.equals("FIN")) {
					break;
				}
			}

			sock.close();
			teclado.close();

			ps.println("Cliente cerrado.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}