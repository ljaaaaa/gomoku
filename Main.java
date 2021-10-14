package lilja.kiiski.gomoku;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.IOException;

public class Main implements ActionListener {
	//Constants
	public final int WIDTH = 855;
	public final int HEIGHT = 855;
	
	//Visual
	public JButton[][] grid = new JButton[19][19];
	public JFrame frame;

	//Networking
	public Lock lock = new ReentrantLock();
	public Client client;

	//Game
	public boolean myTurn = false;

	public static void main(String[] args) {
		Main main = new Main();
		Scanner in = new Scanner(System.in);  
		
		System.out.println("Play a game? (Y/N)");
		char play = in.next().charAt(0);   

		if (play == 'Y' || play == 'y'){
			 main.setUpGame();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (int x = 0; x < grid.length; x++){
                        for (int y = 0; y < grid[x].length; y++){
                                if (e.getSource() == grid[x][y]){
				
					lock.lock();	
					if (myTurn){ //!!! lock here too
						grid[x][y].setText("x");
						grid[x][y].removeActionListener(this);
	                                        frame.setTitle("Waiting...");
					}
					lock.unlock();	
					client.sendMessage("TURN DONE");
					client.sendMessage(String.valueOf(x);
					client.sendMessage(String.valueOf(y);
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

				if (message.equals("TURN")){ ///!!! need to use locks here so that when getting myTurn no data race	
					lock.lock();
					myTurn = true;
					frame.setTitle("Your Turn!");
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
                               grid[x][y].addActionListener(this);
                               panel.add(grid[x][y]);
                       }
                }

		frame.add(panel);
                frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
	}
}
