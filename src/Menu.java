import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout.Constraints;

public class Menu extends JPanel {

	private int index = 0;
	
	private JButton btn1;
	private JButton btn2;
	private JButton btn21;
	private JButton btn22;
	
	public Menu(final Mancala main)
	{
		setLayout(new FlowLayout(FlowLayout.CENTER));
		
		btn1 = new JButton("AI vs Player");
		btn1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.enterGame();
			}
			
		});
		btn2 = new JButton("Player vs Player");
		
		add(btn1);
		add(btn2);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Mancala.getWIDTH(), Mancala.getHEIGHT());
	}
	
}
