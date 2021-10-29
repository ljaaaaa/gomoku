import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
	private ArrayList<Game> games = new ArrayList<>();

	public static void main(String[] args) throws IOException, InterruptedException{
		Server server = new Server();
	}

	public Server() throws IOException, InterruptedException{
		ServerSocket listener = new ServerSocket(1234);

		while (true) {
			Game game = new Game(new ClientHandler[2]);

                	Socket socket = listener.accept();
                	game.clients[0] = new ClientHandler("x", game, socket);
			game.clients[0].thread.start();

                	socket = listener.accept();
			try {

				try {
		                        game.clients[0].bw.write("CONNECTION CHECK");
					game.clients[0].bw.newLine();
                        		game.clients[0].bw.flush();
					System.out.println("wrote message?");
                		} catch (IOException e) {
                        		System.out.println("ERROR SENDING MESSAGE");
                        		e.printStackTrace();
                		}
				game.lock.lock();
				while (!game.connectionChecked){
					System.out.println("awaiting...");
					game.waitingConnectionCheck.await();
				}
				System.out.println("waited!");
				game.connectionChecked = false;

				if (game.clients[0].connected){
					System.out.println("game is not over");
					game.clients[1] = new ClientHandler("o", game, socket);
		                        game.startGame();
                		        games.add(game);
				} else {
					System.out.println("game abandoned");
				}
			} finally {
				game.lock.unlock();
			}
		}
	}
}
