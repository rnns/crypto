package sockets.udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import cryptography.Criptografia;
import cryptography.MD5;

public class UDPClient extends Thread {
	
	boolean crypto;
	
	public UDPClient (boolean c) {
		crypto = c;
	}
	public void run() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));

		DatagramSocket clientSocket;
		InetAddress IPAddress;
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		Criptografia cifra = new Criptografia();
		MD5 hash = new MD5();

		while (true) {
			try {
				clientSocket = new DatagramSocket();
				IPAddress = InetAddress.getByName("localhost");
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		while (true) {
			System.out.println("Cliente preparado para enviar: ");
			try {
				// L� entrada do usu�rio
				String sentence = inFromUser.readLine();
				
				// se crypto for true, usa cifra, se nao hash
				if (crypto) {
					System.out.println("Texto: "+sentence);
					System.out.println("Cifra: "+cifra.encrypt(sentence));
					sendData = cifra.encrypt(sentence);
				} else {
					System.out.println("Texto: "+sentence);
					System.out.println("Hash: "+hash.encode(sentence));
					sendData = hash.encode(sentence).getBytes();
				}
				
				// Cria pacote udp
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, IPAddress, 9876);
				// envia ao servidor
				clientSocket.send(sendPacket);
				// Recebe resposta do servidor
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				clientSocket.receive(receivePacket);
				
				if (crypto) {
					byte[] message = receivePacket.getData();
					System.out.print("Cifra Recebida no Server: ");
					for (int i = 0; i < message.length; i++)
						System.out.print(new Integer(message[i]) + " ");
					System.out.println();
					
					String msg = cifra.decrypt(message);
					System.out.println("Texto Resposta do Server: "+msg);
					
				} else {
					String modifiedSentence = new String(receivePacket.getData());
					
					if (hash.encode("OI") == modifiedSentence) {
						System.out.println("Texto Resposta do Server: OI");
					} else if (hash.encode("TESTE") == modifiedSentence) {
						System.out.println("Texto Resposta do Server: TESTE");
					} else if (hash.encode("MENSAGEM") == modifiedSentence) {
						System.out.println("Texto Resposta do Server: MENSAGEM");
					} else {
						System.out.println("Server não reconheceu a hash e retornou o que foi enviado.");
					}
					System.out.println("Hash Resposta do servidor UDP:"
							+ modifiedSentence);
				}
				// Fecha conex�o: clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}