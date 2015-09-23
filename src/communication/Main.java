package communication;

import javax.swing.JOptionPane;

import sockets.tcp.TCPClient;
import sockets.tcp.TCPServer;
import sockets.udp.UDPClient;
import sockets.udp.UDPServer;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Object[] possibleValues = { "TCP", "UDP" };
		Object communication = JOptionPane.showInputDialog(null, "Defina o protocolo de comunicação:",
				"Protocolo de Comunicação", JOptionPane.INFORMATION_MESSAGE, null, possibleValues,
				possibleValues[0]);
		
		Object[] cryptoValues = { "Cifra", "Hash" };
		Object crypto = JOptionPane.showInputDialog(null, "Defina a técnica de criptografia:",
				"Criptografia", JOptionPane.INFORMATION_MESSAGE, null, cryptoValues,
				possibleValues[0]);
		
		if (communication == "TCP") {
			
			TCPClient cliente = new TCPClient(crypto == "Cifra");
			TCPServer server = new TCPServer(crypto == "Cifra");
			server.start();
			cliente.start();
			
		} else {
			
			TCPClient cliente = new TCPClient(crypto == "Cifra");
			TCPServer server = new TCPServer(crypto == "Cifra");
			server.start();
			cliente.start();
			
		}

	}

}
