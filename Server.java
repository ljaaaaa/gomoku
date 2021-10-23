import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
	private ClientHandler[] clients = new ClientHandler[2];

	public String[][] grid = new String[19][19];
	public Lock gridLock = new ReentrantLock();

	public static void main(String[] args) throws IOException, InterruptedException{
		Server server = new Server();
	}

	public Server() throws IOException, InterruptedException{
		createBlankGrid(); //create empty grid (non nulls);
		ServerSocket listener = new ServerSocket(1234);

                Socket socket = listener.accept();
               	clients[0] = new ClientHandler("x", this, socket);

                Socket socket2 = listener.accept();
                clients[1] = new ClientHandler("o", this, socket2);

                //GAME STARTS
                clients[0].clients = clients;
                clients[1].clients = clients;

		clients[0].sendMessage("PLAYER");
		clients[0].sendMessage("x");
		clients[1].sendMessage("PLAYER");
		clients[1].sendMessage("o");

                clients[0].thread.start();
                clients[1].thread.start();

                clients[0].sendMessage("TURN");
	}

	public void createBlankGrid(){
		for (int x = 0; x < grid.length; x++){
			for (int y = 0; y < grid.length; y++){
				grid[x][y] = "";
			}
		}
	}
}
