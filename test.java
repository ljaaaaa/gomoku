import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class test {

	public static void main (String[] args){
		new test();
	}

	public test(){
		JFrame frame = new JFrame("test");

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(19, 19));

		JLabel[][] grid = new JLabel[19][19];

		for (int x = 0; x < 19; x++){
			for (int y = 0; y < 19; y++){
				grid[x][y] = new JLabel(new ImageIcon("tile.png"));
				panel.add(grid[x][y]);
			}
		}

		frame.add(panel);

		grid[10][5].setIcon(new ImageIcon("black_tile.png"));
		grid[17][3].setIcon(new ImageIcon("white_tile.png"));

		frame.setSize(655, 655);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.setResizable(false);
                frame.setVisible(true);

	}

}
