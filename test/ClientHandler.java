package lilja.kiiski.test;

public class ClientHandler {

	public ClientHandler(Server server) {
		System.out.println("trying to access server.gameOver...");
		server.gameOver = false;
	}
}
