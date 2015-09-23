package sockets.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import cryptography.Criptografia;
import cryptography.MD5;

public class UDPServer extends Thread {
	
	boolean crypto;
	
	public UDPServer (boolean c) {
		crypto = c;
	}
	public void run() {
		// Cria um servidor UDP na porta 9876
		DatagramSocket serverSocket;
		// Sockets apenas enviam bytes
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		Criptografia cifra = new Criptografia();
		MD5 hash = new MD5();
		
		while (true) {
			try {
				serverSocket = new DatagramSocket(9876);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (true) {
			System.out.println("Servidor UDP ouvindo...");
			// Recebe as mensagens dos clientes
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);

			try {
				serverSocket.receive(receivePacket);
				
				if (crypto) {
					
					byte[] message = receivePacket.getData();
					
					// print das cifras recebidas
					System.out.print("Cifra Recebida no Server: ");
					for (int i = 0; i < message.length; i++)
						System.out.print(new Integer(message[i]) + " ");
					System.out.println();
					
					String sentence = cifra.decrypt(message);
					System.out.println("Texto Recebido no Server: "+sentence);
					
					// resposta sendo a msg recebida em uppeCase
					sendData = cifra.encrypt(sentence.toUpperCase());
					
					// envio do packet de retorno ao mesmo IP e porta recebidos
					InetAddress IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();
					
					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);

					for (int i = 0; i < receiveData.length; i++)
						receiveData[i] = 0;
					
					
					
					
				} else {
					String sentence = new String(receivePacket.getData());
					System.out.println("Hash Recebido no Server: " + sentence);
					
					// A resposta ser� a mesma mensagem, por�m captalizada.
					if (hash.encode("oi").equals(sentence)) {
						System.out.println("Texto Recebido no Server: oi");
						sentence = hash.encode("OI");
					} else if (hash.encode("teste").equals(sentence)) {
						System.out.println("Texto Recebido no Server: teste");
						sentence = hash.encode("TESTE");
					} else if (hash.encode("mensagem").equals(sentence)) {
						System.out.println("Texto Recebido no Server: mensagem");
						sentence = hash.encode("MENSAGEM");
					} else {
						System.out.println("Hash não reconhecido! (Hashs reconhecidos: oi, teste, mensagem)");
					}
					
					// Responde ao mesmo IP e porta recebidos
					InetAddress IPAddress = receivePacket.getAddress();
					int port = receivePacket.getPort();
					sendData = sentence.getBytes();
					
					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);

					for (int i = 0; i < receiveData.length; i++)
						receiveData[i] = 0;
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}