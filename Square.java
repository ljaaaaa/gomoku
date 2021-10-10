package gomoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Square extends JButton{
	public int posX;
	public int posY;
	public String icon;

	public Square(String icon, int posX, int posY) {
		super(icon);
		this.icon = icon;
		this.posX = posX;
		this.posY = posY;
	}
}
