public class Server2 {

	public boolean gameOver = false;

	public ClientHandler2 handler;

	public static void main(String[] args) {
		System.out.println("Creating server");
		Server2 server = new Server2();
		System.out.println("created server");
	}

	public Server2(){
		 handler = new ClientHandler2(this);	
		 handler.accessServer();
	}
}
