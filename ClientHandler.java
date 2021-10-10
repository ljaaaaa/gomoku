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
	private Socket client;
	private BufferedReader br;
	private BufferedWriter bw;

	public Thread thread;
	public String name = "No Name";
	public String score = "0";

	public boolean nameSet = false;
	public boolean scoreSet = false;
	public Lock lock = new ReentrantLock();
	public Condition nameSetUp = lock.newCondition();

	public Server server;
	public int clientNum;

	public ClientHandler(int clientNum, Server server, Socket clientSocket) throws IOException{
		this.client = clientSocket;
		this.server = server;
		this.clientNum = clientNum;

		br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
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

				else if (msgFromClient.contains("NAME:")) { //Username
					name = msgFromClient.substring(5);

					try {
						lock.lock();
						nameSet = true;
						nameSetUp.signalAll();
					} finally {
						lock.unlock();
					}
				} else if (msgFromClient.contains("SCORE:")) { //Score at end of game
					score = msgFromClient.substring(6);

					try {
						server.lock.lock();
						server.scores[clientNum] = score; //Updates clients score onto score sheet

						if (server.scores[0] != null && server.scores[1] != null) { //If both scores have been given (not null)
							server.scoresSent = true;
							server.allScoresReceived.signalAll();
						}
					} finally {
						server.lock.unlock();
					}
				}
			}
			client.close();
			br.close();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg) throws IOException{
		bw.write(msg);
		bw.newLine();
		bw.flush();
		System.out.println("Message sent to " + name + ": " + msg);
	}
}
