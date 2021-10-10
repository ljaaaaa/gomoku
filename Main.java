package gomoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main implements ActionListener{
	public final int WIDTH = 855;
	public final int HEIGHT = 855;
	public final int SIZE = 45;
	public Square[][] grid = new Square[19][19];
	public JFrame frame;

	public static void main(String[] args){
		Main main = new Main();
		main.setUpGame();
	}

	@Override
	public void actionPerformed(ActionEvent e){
		for (int x = 0; x < grid.length; x++){
                        for (int y = 0; y < grid[x].length; y++){
                                if (e.getSource() == grid[x][y]){
					grid[x][y].setText("x");
					frame.setTitle("you clicked!");
				}
                        }
                }
	}

	public void setUpGame(){
		createWindow();

		


	}

	public void createWindow(){
		frame = new JFrame("Gomoku");
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(19, 19));

		for (int x = 0; x < grid.length; x++){
                       for (int y = 0; y < grid[x].length; y++){
			       grid[x][y] = new Square("", x*SIZE, y*SIZE);
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
}
