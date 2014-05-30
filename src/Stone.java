import java.awt.Color;
import java.util.Random;


public class Stone {
	
	private static Random rnd;
	
	//Where is the stone
	private int index;
	
	//Temp : use image later
	private Color color;
	
	//Position relative to bin
	private int rx;
	private int ry;
	
	//index always zero when initialization , given a relative position in bin
	public Stone()
	{
		index = 0;
		//Randomly choose image(color) , relative position
		if(rnd == null)
			rnd = new Random();
		
		rx = rnd.nextInt( Mancala.SIZE_BIN - 2*Mancala.OFFSET);
		ry = rnd.nextInt( Mancala.SIZE_BIN - 2*Mancala.OFFSET);
		
		switch(rnd.nextInt(3)){
		case 0:
			color = Color.LIGHT_GRAY;
			break;
		case 1:
			color = Color.ORANGE;
			break;
		case 2:
			color = Color.BLUE;
			break;
		}
		
	}
	
	//accessor
	public int getIndex()
	{
		return index;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public int getRX()
	{
		return rx;
	}
	
	public int getRY()
	{
		return ry;
	}
	
	//Setters
	public void setIndex(int i)
	{
		index = i;
	}
	
	//@param : start - start index
	//@param : step - how many bins will be passed
	public void move(int start , int step)
	{
		int end = start + step;
		
		index = end;
		
		if(end > 14)
			move(0 , step - (14 - start));
	}
	
}
