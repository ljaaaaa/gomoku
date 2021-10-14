package lilja.kiiski.gomoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientHandler implements Runnable{
	private Socket socket;
	private BufferedReader br;
	private BufferedWriter bw;

	public String[][] grid = new String[19][19];
	public boolean gridSet = false;

	public Lock lock = new ReentrantLock();

	public Thread thread;
	public Server server;
	public char player;
	public ClientHandler[] clients;

	public ClientHandler(char player, Server server, Socket socket) throws IOException{
		this.socket = socket;
		this.server = server;
		this.player = player;

		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		thread = new Thread(this);
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

					grid[posX][posY] = player;

					

					//Then send message to other player for their turn
				}
			}
			socket.close();
			br.close();
			bw.close();

		} catch (Exception e) {
			System.out.println("PROBLEM OCCURED");
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) throws IOException{
		bw.write(message);
		bw.newLine();
		bw.flush();
		System.out.println("MESSAGE SENT TO: " + message);
	}

	public void sendMessageToOther(String message){

		for (int x = 0; x < clients.length; x++){
			if (clients[x] != this){
				clients[x].sendMessage("YOUR TURN");

			}
	}
}
