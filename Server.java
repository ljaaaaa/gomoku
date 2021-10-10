import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

	private ArrayList<ClientHandler> clients = new ArrayList<>();
	public String[] scores = new String[] {null, null}; //Clients will edit their scores onto here

	public boolean scoresSent = false;
	public Lock lock = new ReentrantLock();
	public Condition allScoresReceived = lock.newCondition();

	public Server() throws IOException, InterruptedException{
		ServerSocket listener = new ServerSocket(2345);

		while (true) {
			System.out.println("Listening for clients");
			Socket socket = listener.accept();
			System.out.println("Found a client!");
			ClientHandler clientThread = new ClientHandler(clients.size(), this, socket);

			clients.add(clientThread); //Add new client thread to clients
			clientThread.thread.start();

			while (!clientThread.nameSet) { //Wait for name to be sent
				try {
					clientThread.lock.lock();
					clientThread.nameSetUp.await();
				} finally {
					clientThread.lock.unlock();
				}
			}

			if (clients.size() == 2){ //Two players have been reached
				System.out.println("NEW GAME");

				String candyType = String.valueOf((int)(Math.random() * 5) + 1);

				for (int x = 0; x < clients.size(); x++) { 	//Sends start message and candy type to clients
					clients.get(x).sendMessage("START GAME");
					clients.get(x).sendMessage(candyType);
				}
				System.out.println("Send Start Message");

				while (!scoresSent) {
					try {
						lock.lock();
						allScoresReceived.await();
					} finally {
						lock.unlock();
					}
				}
				System.out.println("Scores received!"); //After scores have been received

				for (int x = 0; x < clients.size(); x++) {
					clients.get(x).sendMessage("RESULTS"); //Sends message to phone to start game

					clients.get(x).sendMessage(clients.get(0).name); //Sends player names and scores
					clients.get(x).sendMessage(clients.get(0).score);

					clients.get(x).sendMessage(clients.get(1).name);
					clients.get(x).sendMessage(clients.get(1).score);
				}
				System.out.println("Sent Results");

				clients.clear(); //Reset variables for new game

				scores = new String[] {null, null}; //Clients will edit their scores onto here
				scoresSent = false;
			}
		}
	}
}
