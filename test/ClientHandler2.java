public class ClientHandler2 {

	public Server2 myServer;

	public ClientHandler2(Server2 server) {
		myServer = server;
	}

	public void accessServer(){
		System.out.println(myServer);
                System.out.println("trying to access server.gameOver...");
                myServer.gameOver = false;
		System.out.println("accessed");
	}
}
