package sockets.tcp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import cryptography.Criptografia;
import cryptography.MD5;

public class TCPServer extends Thread {
	private static ServerSocket welcomeSocket;

	boolean crypto;

	public TCPServer(boolean c) {
		crypto = c;
	}

	public void run() {
		String clientSentence;

		while (true) {
			try {
				welcomeSocket = new ServerSocket(6789);
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		while (true) {
			System.out.println("Servidor TCP ouvindo...");
			
			Criptografia cifra = new Criptografia();
			MD5 hash = new MD5();

			try {
				// Aceitando conex�es de clientes.
				Socket connectionSocket = welcomeSocket.accept();


				// true equivale a cifra e false a hash
				// Lendo dados recebidos.
				if (crypto) {

					DataInputStream inClient = new DataInputStream(
							connectionSocket.getInputStream());

					// Ler tamanho do array de bytes que esta chegando
					int length = inClient.readInt();

					// Le a mensagem
					if (length > 0) {
						byte[] message = new byte[length];
						inClient.read(message, 0, message.length);

						System.out.print("Cifra Recebida no Server: ");
						for (int i = 0; i < message.length; i++)
							System.out.print(new Integer(message[i]) + " ");
						System.out.println();
						
						String msg = cifra.decrypt(message);
						System.out.println("Texto Recebido no Server: "+msg);
						
						message = cifra.encrypt(msg.toUpperCase());
						
						// Abrindo canal de comunica��o para escrita no socket.
						DataOutputStream outToClient = new DataOutputStream(
								connectionSocket.getOutputStream());
						
						outToClient.writeInt(message.length);
						outToClient.write(message);
						
					} else {
						System.out.println("Erro no tamanho recebido. (byte[] length = 0)");
					}

				} else {
					
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(
									connectionSocket.getInputStream()));
					clientSentence = inFromClient.readLine();
					System.out.println("Hash Recebido no Server: " + clientSentence);
					
					// Abrindo canal de comunica��o para escrita no socket.
					DataOutputStream outToClient = new DataOutputStream(
							connectionSocket.getOutputStream());
					
					// A resposta ser� a mesma mensagem, por�m captalizada.
					if (hash.encode("oi").equals(clientSentence)) {
						System.out.println("Texto Recebido no Server: oi");
						outToClient.writeBytes(hash.encode("OI")+'\n');
					} else if (hash.encode("teste").equals(clientSentence)) {
						System.out.println("Texto Recebido no Server: teste");
						outToClient.writeBytes(hash.encode("TESTE")+'\n');
					} else if (hash.encode("mensagem").equals(clientSentence)) {
						System.out.println("Texto Recebido no Server: mensagem");
						outToClient.writeBytes(hash.encode("MENSAGEM")+'\n');
					} else {
						System.out.println("Hash não reconhecido! (Hashs reconhecidos: oi, teste, mensagem)");
						outToClient.writeBytes(clientSentence+'\n');
					}
					
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}