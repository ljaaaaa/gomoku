import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

public class Main implements ActionListener {	
	public JButton[][] grid = new JButton[19][19];
	public JFrame frame;
	public Client client;
	public String player = "";

	public static void main(String[] args) {
		new Main().setUpGame();	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int x = 0; x < grid.length; x++){
                        for (int y = 0; y < grid[x].length; y++){
                                if (e.getSource() == grid[x][y] && 
						!grid[x][y].getText().equals("x") && 
						!grid[x][y].getText().equals("o")){	
					removeButtonListeners();
					
					client.sendMessage("TURN DONE");
					client.sendMessage(String.valueOf(x));
					client.sendMessage(String.valueOf(y));
				}
                        }
                }
	}

	public void setUpGame() {
		client = new Client(this);
		createWindow();

		if (!client.connected){
			setFrameTitle("", "not connected");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		} else {
			receiveMessages();
		}
	}

	public void receiveMessages() {
		while (true){
			try {
				String message = "";
				if (client.br != null){
					message = client.br.readLine();
				}

				if (message == null){
					System.out.println("MESSAGE WAS NULL");
					break;
				}

				if (message.equals("PLAYER")){
					player = client.br.readLine();

					if (player.equals("o")){
						setFrameTitle("", "wait");
					}
				}

				if (message.equals("UPDATE GRID")){
                                        for (int x = 0; x < grid.length; x++){
                                                for (int y = 0; y < grid[x].length; y++){
							setGridImage(x, y, client.br.readLine());
                                                }
                                        }
					setFrameTitle("", "wait");
				}

				if (message.equals("TURN")){	
					setFrameTitle(player, "turn");
					addButtonListeners();					
				}

				if (message.equals("RESULTS")){
					String result = client.br.readLine();
					setFrameTitle(result, "win");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					break;
				}

			} catch (IOException e){
				System.out.println("ERROR READING MESSAGES");
				e.printStackTrace();
			}
		}
	}

	public void createWindow() {
		frame = new JFrame("Gomoku");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(19, 19));
		
		for (int x = 0; x < grid.length; x++){
                       for (int y = 0; y < grid[x].length; y++){
			       	grid[x][y] = new JButton("");
			       	grid[x][y].setFocusPainted(false);
			       	grid[x][y].setMargin(new Insets(0, 0, 0, 0));
				grid[x][y].setContentAreaFilled(false);
        			grid[x][y].setBorderPainted(false);
        			grid[x][y].setOpaque(false);
				setGridImage(x, y, "");
                               panel.add(grid[x][y]);
		
		       }
                }

		frame.add(panel);
		frame.setIconImage(new ImageIcon("images/tile.png").getImage());
                frame.setSize(655, 655);
		frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void setFrameTitle(String player, String message){
		if (player.equals("x")){
			player = "black";
		} else if (player.equals("o")){
			player = "white";
		} else {
			player = "";
		}

		switch (message){
			case "win":
				frame.setTitle("Player " + player + " wins!");
				break;

			case "turn":
				frame.setTitle("Your turn! You are player " + player);
				break;

			case "wait":
				frame.setTitle("Please wait for your turn...");
				break;

			case "not connected":
				frame.setTitle("Not connected to server");
				break;
		}
	}

	public void setGridImage(int x, int y, String text){
		switch (text){
			case "":
				grid[x][y].setIcon(new ImageIcon("images/tile.png"));
				break;

			case "x":
				grid[x][y].setIcon(new ImageIcon("images/black_tile.png"));
				break;

			case "o":
				grid[x][y].setIcon(new ImageIcon("images/white_tile.png"));
				break;
		}
	}

	public void removeButtonListeners(){ //When not users turn, remove listeners so buttons can't be clicked
		for (int x = 0; x < grid.length; x++){
			for (int y = 0; y < grid[x].length; y++){
				grid[x][y].removeActionListener(this);
			}
		}
	}

	public void addButtonListeners(){ //add button listeners
		for (int x = 0; x < grid.length; x++){
                        for (int y = 0; y < grid[x].length; y++){
                                grid[x][y].addActionListener(this);
                        }
                }

	}


}
