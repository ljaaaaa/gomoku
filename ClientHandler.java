import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
	private Socket socket;
	public BufferedReader br;
	public BufferedWriter bw;

	public Thread thread;
	public Game game;
	public String player;
	public ClientHandler[] clients;

	public boolean connected = true;

	public ClientHandler(String player, Game game, Socket socket) throws IOException{
		this.socket = socket;
		this.game = game;
		this.player = player;

		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		thread = new Thread(this);
	}

	@Override
	public void run() {
		try {
			System.out.println("started this thread!");
			while (true) {
				String msgFromClient = br.readLine();
				System.out.println("received message: " + msgFromClient);
				System.out.println(msgFromClient);
				if (msgFromClient == null) { //Disconnected from game
					break;
				}

				if (msgFromClient.equals("CONNECTION CHECK")){ //Checking connection
					System.out.println("received connection check!");
					game.lock.lock();
		                        game.gameOver = true;
                		        game.connectionChecked = true;
                     			connected = true;
                        		game.waitingConnectionCheck.signalAll();
                        		System.out.println("signaled");
                        		game.lock.unlock();
				}

				if (msgFromClient.equals("TURN DONE")){
					int posX = Integer.parseInt(br.readLine());
					int posY = Integer.parseInt(br.readLine());

					try { //update game grid
						game.lock.lock();
						game.grid[posX][posY] = player;
					} finally {
						game.lock.unlock();
					}

					sendMessageToOther("UPDATE GRID");
					sendMessage("UPDATE GRID");
					for (int x = 0; x < game.grid.length; x++){ //send entire grid over to user
						for (int y = 0; y < game.grid[x].length; y++){
							sendMessage(game.grid[x][y]);
							sendMessageToOther(game.grid[x][y]);
						}
					}

					if (checkWin()){ //Game over
						sendMessage("RESULTS");
						sendMessage(player);

						sendMessageToOther("RESULTS");
						sendMessageToOther(player);

						try {
							game.lock.lock();
							game.gameOver = true;
						} finally {
							game.lock.unlock();
						}

					} else { //Game continues
						sendMessageToOther("TURN");
					}
				}

			}
			sendMessageToOther("OTHER PLAYER DISCONNECTED");
			socket.close();
			br.close();
			bw.close();
		} catch (Exception e) { //IOException or NullPointerException
			System.out.println("PROBLEM OCCURED");
		} finally {
			game.lock.lock();
                        game.gameOver = true;
			game.connectionChecked = true;
			connected = false;
			game.waitingConnectionCheck.signalAll();
                        System.out.println("signaled");
			game.lock.unlock();
		}
	}

	public void sendMessage(String message){
		try {
			bw.write(message);
	                bw.newLine();
        	        bw.flush();
		} catch (IOException e){ } //stream has closed and messages can't be sent
	}

	public void sendMessageToOther(String message){
		for (int x = 0; x < clients.length; x++){
			if (clients[x] != this){
				clients[x].sendMessage(message);

			}
		}
	}

	public boolean checkWin(){ //check if player has won
		String[][] g = game.grid;

		for (int x = 0; x < g.length; x++){
			for (int y = 0; y < g.length; y++){
				if (y+4 < 19 && g[x][y] == player && //check column 
					g[x][y+1] == player && 
					g[x][y+2] == player && 
					g[x][y+3] == player &&
					g[x][y+4] == player){ 

					return true;
				}

				if (x+4 < 19 && g[x][y] == player && //check row
                                	g[x+1][y] == player &&
                                        g[x+2][y] == player &&
                                        g[x+3][y] == player &&
                                        g[x+4][y] == player){

                                        return true;
                                }

				if (x+4 < 19 && y+4 < 19 && //check diagonal up
					g[x][y] == player &&
                                        g[x+1][y+1] == player &&
                                        g[x+2][y+2] == player &&
                                        g[x+3][y+3] == player &&
                                        g[x+4][y+4] == player){

                                        return true;
				}
					
				if (x+4 < 19 && y-4 > 0 && //check diagonal down
					g[x][y] == player &&
                                        g[x+1][y-1] == player &&
                                        g[x+2][y-2] == player &&
                                        g[x+3][y-3] == player &&
                                        g[x+4][y-4] == player){

                                        return true;
                                }
			}
		}
		return false;
	}
}
