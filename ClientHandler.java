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

	public Thread thread;
	public String score = "0";

	public Square[] grid;
	public boolean gridSet = false;

	public Lock lock = new ReentrantLock();
	public Condition nameSetUp = lock.newCondition();

	public Server server;
	public int clientNum;

	public ClientHandler(int clientNum, Server server, Socket socket) throws IOException{
		this.socket = socket;
		this.server = server;
		this.clientNum = clientNum;

		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		thread = new Thread(this, name);
	}

	@Override
	public void run() {
		try {
			while (true) {
				String msgFromClient = br.readLine();

				if (msgFromClient == null) { //Server has disconnected
					break;
				}
			}
			client.close();
			br.close();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) throws IOException{
		bw.write(msg);
		bw.newLine();
		bw.flush();
		System.out.println("MESSAGE SENT TO: " + name + ": " + message);
	}
}
