package lilja.kiiski.test;

public class Server {

	private ClientHandler[] clients = new ClientHandler[2];

	public boolean gameOver = false;

	public static void main(String[] args) {
		Server server = new Server();
		server.startProgram();
	}

	public void startProgram() {
                clients[0] = new ClientHandler(this);

	}
}
