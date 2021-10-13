package lilja.kiiski.gomoku;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;

public class Main implements ActionListener {
	//Constants
	final int WIDTH = 855;
	final int HEIGHT = 855;
	
	//Visual
	JButton[][] grid = new JButton[19][19];
	JFrame frame;

	//Networking
	Lock lock = new ReentrantLock();
    	Condition clientInitialized = lock.newCondition();
    	Condition sentMessage = lock.newCondition();

	Client client;

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
					grid[x][y].setText("x");
					frame.setTitle("you clicked!");
				}
                        }
                }
	}

	public void setUpGame() {
		createClient();

		if (client.connected){




			createWindow();
		}
	}

	public void createNetwork() {
		createClient();

		sendClientMessage("");

		//create thing to receive messages




	}

	public void sendClientMessage(String message){
		client.sendMessage(message);

		try {
            		lock.lock();
            		while (!client.sentMessage) {
                		sentMessage.await();
            		}
            		client.sentMessage = false;
        	} catch (InterruptedException e) {
            		System.out.println("INTERRUPTED EXCEPTION");
			e.printStackTrace();
        	} finally {
            		lock.unlock();
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
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
	}

	public void createClient() {
                try {
                        client = new Client(this);
                        lock.lock();
                        while (!client.initialized) {
                                clientInitialized.await();
                        }
                } catch (InterruptedException e) {
                        System.out.println("INTERRUPTED EXCEPTION");
                        e.printStackTrace();
                } finally {
                        lock.unlock();
                }
	}
}
