package sockets.tcp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import cryptography.Criptografia;
import cryptography.MD5;

public class TCPClient extends Thread {

	public boolean crypto;
	
	public TCPClient (boolean c) {
		crypto = c;
	}
	public void run() {
		String sentence;
		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		
		Criptografia cifra = new Criptografia();
		MD5 hash = new MD5();
		
		while (true) {
			System.out.println("Digite algo para enviar ao servidor TCP...");
			// Abre conex�o com destino: local e porta: 6789
			try {
				Socket clientSocket = new Socket("localhost", 6789);
				// L� entrada do usu�rio.
				sentence = inFromUser.readLine();
				// Cria canal de comunica��o com o servidor
				DataOutputStream outToServer = new DataOutputStream(
						clientSocket.getOutputStream());
				
				// true equivale a cifra e false a hash
				// Enviar a mensagem ao servidor.
				if (crypto) {
					
					byte[] cipher = cifra.encrypt(sentence+'\n');
					System.out.println("Texto: "+sentence);
					System.out.print("Cifra: ");
					for (int i = 0; i < cipher.length; i++)
						System.out.print(new Integer(cipher[i]) + " ");
					System.out.println();
					
					outToServer.writeInt(cipher.length);
					outToServer.write(cipher);
					
					// Ler resposta do servidor
					DataInputStream inClient = new DataInputStream(
							clientSocket.getInputStream());

					// Ler tamanho do array de bytes que esta chegando
					int length = inClient.readInt();

					// Le a mensagem
					if (length > 0) {
						byte[] message = new byte[length];
						inClient.readFully(message, 0, message.length);

						System.out.print("Cifra Resposta do Server: ");
						for (int i = 0; i < message.length; i++)
							System.out.print(new Integer(message[i]) + " ");
						System.out.println();
						
						String msg = cifra.decrypt(message);
						System.out.println("Texto Resposta do Server: "+msg);
						
						
					} else {
						System.out.println("Erro no tamanho recebido. (byte[] length = 0)");
					}
					
				} else {
					
					System.out.println("Texto: "+sentence);
					System.out.println("Hash: "+hash.encode(sentence));	
					outToServer.writeBytes(hash.encode(sentence)+'\n');
				
					// L� resposta do servidor.
					BufferedReader inFromServer = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()));
					modifiedSentence = inFromServer.readLine();
					System.out.println("Hash Resposta do Server: "
							+ modifiedSentence);
					
					if (hash.encode("OI").equals(modifiedSentence)) {
						System.out.println("Texto Resposta do Server: OI");
					} else if (hash.encode("TESTE").equals(modifiedSentence)) {
						System.out.println("Texto Resposta do Server: TESTE");
					} else if (hash.encode("MENSAGEM").equals(modifiedSentence)) {
						System.out.println("Texto Resposta do Server: MENSAGEM");
					} else {
						System.out.println("Server não reconheceu a hash e retornou o que foi enviado.");
					}
				
				}
				
				clientSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}