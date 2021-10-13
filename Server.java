import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
	private ClientHandler[] clients = new ClientHandler[2];
	public String[] grid = new String[2];
	public boolean gameOver;

	public Lock lock = new ReentrantLock();
	public Condition gameFinished = lock.newCondition();

	public Server() throws IOException, InterruptedException{
		ServerSocket listener = new ServerSocket(2021);

		int num = 0;
		while (true) {
			System.out.println("LISTENING");
			Socket socket = listener.accept();
			System.out.println("FOUND CLIENT");
			ClientHandler clientThread = new ClientHandler(clients.size(), this, socket);

			clients[num] = clientThread; //Add new client thread to clients
			clientThread.thread.start();

			//GAME STARTS
			if (clients[0] != null && clients[1] != null){
				clients[0].sendMessage("TURN"); //Tell PlayerX to go

				while (!sgameOver) {
					try {
						lock.lock();
						gameFinished.await();
					} finally {
						lock.unlock();
					}
				}

				clients[0].sendMessage("Game Over");
	                        clients[1].sendMessage("Game Over");
			}

			//SET UP FOR NEW GAME
			clinets = new ClientHandler[2];
			grid = new String[];
			gameOver = false;
		}
	}
}
