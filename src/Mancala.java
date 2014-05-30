import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JApplet;

public class Mancala extends JApplet {
	
	//Number of stones
	//Size of stone
	public static final int TOTAL = 48;
	public static final int SIZE_BIN = 60;
	public static final int SIZE_STONE = 12;
	public static final int OFFSET = (int) (SIZE_BIN - SIZE_BIN / Math.sqrt(2));
	public static final int SCALE_STONE = 5;
	
	private static int WIDTH = 520;
	private static int HEIGHT = 220;
	
	private Board board;
	private Menu menu;
	
	//Set container info
	public void init()
	{
		setSize(520 , 220);
		this.setLayout(new BorderLayout());
		menu = new Menu(this);
		getContentPane().add(menu , BorderLayout.SOUTH);
		
		//left 20 , right 20 is border , up 50 , bottom 50 is UI
		board = new Board(getWIDTH() , getHEIGHT());
	}
	
	public void enterGame()
	{
		getContentPane().add(board);
		board.initializeGraphic();
		
		menu.setVisible(false);
		menu.setEnabled(false);
		
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
	}
	
	public static int getWIDTH() {
		return WIDTH;
	}
	
	public static int getHEIGHT() {
		return HEIGHT;
	}

}
