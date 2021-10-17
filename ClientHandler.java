package lilja.kiiski.gomoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler implements Runnable{
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;

	public boolean gridSet = false;

	public Thread thread;
	public Server server;
	public String player;
	public ClientHandler[] clients;

	public ClientHandler(String player, Server server, Socket socket) throws IOException{
		this.socket = socket;
		this.server = server;
		this.player = player;

		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		thread = new Thread(this);

		System.out.println("hmmmm!");
		server.gameOver = false;

		System.out.println("here...");
		server.gridLock.lock();
		System.out.println("locked");
		server.gridLock.unlock();
		System.out.println("?");
	}

	@Override
	public void run() {
		try {
			while (true) {
				String msgFromClient = br.readLine();

				if (msgFromClient == null) { //Server has disconnected
					break;
				}

				if (msgFromClient.equals("TURN DONE")){
					int posX = Integer.parseInt(br.readLine());
					int posY = Integer.parseInt(br.readLine());

					try { //update server grid
						server.gridLock.lock();
						server.grid[posX][posY] = player;
					} finally {
						server.gridLock.unlock();
					}

					sendMessageToOther("TURN"); //Send message to other player for their turn
				}
			}
			System.out.println("CLOSING SOCKET");
			socket.close();
			br.close();
			bw.close();

		} catch (IOException e) {
			System.out.println("PROBLEM OCCURED");
			e.printStackTrace();
		}
	}

	public void sendMessage(String message){
		try {
			bw.write(message);
	                bw.newLine();
        	        bw.flush();
                	System.out.println("MESSAGE SENT TO: " + message);

		} catch (IOException e){
			System.out.println("IOEXCEPTION SENDING MESSAGE");
			e.printStackTrace();
		}
	}

	public void sendMessageToOther(String message){
		for (int x = 0; x < clients.length; x++){
			if (clients[x] != this){
				clients[x].sendMessage("YOUR TURN");

			}
		}
	}
}
