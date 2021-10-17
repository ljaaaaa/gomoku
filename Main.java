import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;

public class Main implements ActionListener {	
	//Visual
	public JButton[][] grid = new JButton[19][19];
	public JFrame frame;

	//Networking
	public Lock lock = new ReentrantLock();
	public Client client;

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

					//...
					lock.lock();
					
					grid[x][y].setText("x");
	                                frame.setTitle("Waiting...");
					removeButtonListeners();
					
					lock.unlock();	
					
					client.sendMessage("TURN DONE");
					client.sendMessage(String.valueOf(x));
					client.sendMessage(String.valueOf(y));
					//...
					System.out.println("turn done and messages sent");
				}
                        }
                }
	}

	public void setUpGame() {
		client = new Client(this);
		createWindow();
                receiveMessages();
	}

	public void receiveMessages() {
		while (true){
			try {
				String message = client.br.readLine();
				System.out.println("received message: " + message);

				if (message.equals("UPDATE GRID")){
                                        frame.setTitle("Updating Information...");
                                        for (int x = 0; x < grid.length; x++){ //update grid
                                                for (int y = 0; y < grid[x].length; y++){
                                                        grid[x][y].setText(client.br.readLine());
                                                }
                                        }
				}

				if (message.equals("TURN")){	
					lock.lock();
					frame.setTitle("Your Turn!");
					addButtonListeners();
					lock.unlock();					
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
                               panel.add(grid[x][y]);
                       }
                }

		frame.add(panel);
                frame.setSize(855, 855);
		frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
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
