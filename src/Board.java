import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


public class Board extends Canvas{
	
	//Not final , maybe revise when resizing
	private int width;
	private int height;
	
	//Use array instead arraylist because the length is fixed
	private Stone[] stones;
	
	//Opponent and Player
	private Player opponent;
	private Player player;
	
	//Indicate mouse focus on which bin
	private Player currentPlayer;
	
	//Graphic Utility
	private BufferStrategy buffer;
	private Timer renderer;
	
	//UI info
	private Font font;
	private Font msgFont;
	private Font hintFont;
	private int[] cbinStone = {0 , 4 , 4 , 4 , 4 , 4 , 4 , 0
							 	 , 4 , 4 , 4 , 4 , 4 , 4 , 0};
	private Stack<Message> msgStack;
	
	//Accsssor and Setters
	public Stone[] getStones()
	{
		return stones;
	}
	
	//Maybe restart the game , so do not write in constructor
	public void initializeState(int w , int h)
	{
		//reset the screen
		width = w;
		height = h;
		
		//reset the stones position
		for(int c = 0 ; c < Mancala.TOTAL ; c++){
			int index = (c / 4) + 1;
			
			//Skip player side M B
			if(index >= 7)
				index++;
			
			stones[c].setIndex(index);
		}
		currentPlayer = player;
	}
	
	//Initialize graphic 
	public void initializeGraphic()
	{
		//Initialize graphic utility
		//Set up timer-based rendering
		setIgnoreRepaint(true);
		createBufferStrategy(2);
		
		//Set buffer
		buffer = getBufferStrategy();
		if(buffer == null){
			System.err.println("Failed to create buffer.");
			System.exit(1);
		}
		
		//Set up renderer
		renderer = new Timer();
		renderer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				render();
				
			}}, 0, 30);
	}
	
	//Initialize control panel
	public void initializePlayer()
	{
		//Temp : a AI component and a human player
		opponent = new Player(this , Player.Side.UPPER);
		player = new Player(this , Player.Side.LOWER);
		
		//Set up controller
		PlayerController playerController = new PlayerController(player);
		player.addController(playerController);
		
		opponent.addController(new AIController(opponent));
		
		//add mousesensor
		addMouseListener(playerController);
		addMouseMotionListener(playerController);
	}
	
	//Pass dimension to build a panel
	public Board(int w , int h)
	{		
		//Create a message stack
		msgStack = new Stack<Message>();
		
		//load font
		font = new Font(Font.SERIF, Font.PLAIN, 16);
		msgFont = new Font(Font.SERIF, Font.ITALIC, 36);
		hintFont = new Font(Font.SERIF, Font.BOLD , 22);
		
		//Create a stones array (not in init , because this array's content will not be revised later)
		stones = new Stone[Mancala.TOTAL];
		Random rnd = new Random();
		for(int i = 0 ; i < Mancala.TOTAL ; i++)
			stones[i] = new Stone();
		
		initializePlayer();
		initializeState(w , h);
		
	}

	/******************Game Function*******************/
	//Usage : call this function to step to next round
	public void next()
	{
		if(currentPlayer == player)
			currentPlayer = opponent;
		else
			currentPlayer = player;
		
		inform(currentPlayer);
	}
	
	//Usage : player call this method to perform game action
	//@param : isPlayer - check if the caller is a AI or human
	//@param : index - move which bin
	public void execute(boolean isPlayer , int index) throws InterruptedException
	{
		//Avoid player from clicking a empty bin
		boolean isEmpty = false;
		//For last bin operation
		Stone lastStone = null;
		
		//Count of stones in focus bin
		int count = 0;
		//Handling human
		for(Stone stone : stones){
			//in the specific bin
			if(stone.getIndex() == index){
				isEmpty = true;
				count++;
				stone.move(stone.getIndex(), count);
				lastStone = stone;
				Thread.sleep(200);
			}
		}
		
		if(isEmpty)
			afterProcess(lastStone);
		else{
			System.out.println("Empty bin.");
		}
	}
	
	//Usage : refresh count of bins
	public void refresh()
	{
		//Update stone counting
		//Reset counting 
		Arrays.fill(cbinStone, 0, cbinStone.length , 0);
		for(Stone stone : stones)
			cbinStone[stone.getIndex()]++;
	}
	
	//Usage : handle those special case
	//@param : lastStone  - the last stone in the movement
	public void afterProcess(Stone lastStone )
	{
		refresh();	
		//Big capture
		if(cbinStone[lastStone.getIndex()] == 1 && lastStone.getIndex() != 7 && lastStone.getIndex() != 14 
				&& cbinStone[ 14 - lastStone.getIndex()] > 0 ){
			
			pushMessage(MessageFactory.getInstance().createMessage(MessageFactory.Type.POPUP, "Big Capture !"));
			
			for(Stone stone : stones){
				if(currentPlayer == player && stone.getIndex() == ( 14 - lastStone.getIndex())
						&& lastStone.getIndex() >= 1 && lastStone.getIndex() <= 6){
					stone.setIndex(lastStone.getIndex());
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(currentPlayer == opponent && stone.getIndex() == ( 14 - lastStone.getIndex())
						&& lastStone.getIndex() >= 8 && lastStone.getIndex() <= 14){
					stone.setIndex(lastStone.getIndex());
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			refresh();
		}
		
		//Check winner before get to next step
		if(isWin()){
			if(currentPlayer == player)
				pushMessage(MessageFactory.getInstance().createMessage(MessageFactory.Type.POPUP, "Player win!"));
			else
				pushMessage(MessageFactory.getInstance().createMessage(MessageFactory.Type.POPUP, "AI win!"));
			//renderer.cancel();
			return;
		}
		
		//free again
		if((lastStone.getIndex() == 7 && currentPlayer == player)
		|| (lastStone.getIndex() == 14 && currentPlayer == opponent)){
			pushMessage(MessageFactory.getInstance().createMessage(MessageFactory.Type.POPUP, "Free Again !"));
			inform(currentPlayer);
		}
		else{
			next();
		}
	}
	
	//Usage : check if the current is my turn
	//@param : player - player which want to check
	public boolean isMyTurn(Player player)
	{
		if(currentPlayer == player)
			return true;
		else
			return false;
				
	}
	
	//Usage : check the specific bin state (AI will call this to check
	//@param : index - index which want to check
	public boolean isEmptyBin(int index)
	{
		for(Stone stone : stones){
			if(stone.getIndex() == index)
				return false;
		}
		return true;
	}
	
	//Usage : judge win condition
	public boolean isWin()
	{
		//check lower side
		int sideSum = 0;
		for(int i = 1 ; i <= 6 ; i++)
			sideSum += cbinStone[i];
		if(sideSum == 0)
			return true;
		
		//check upper side
		sideSum = 0;
		for(int i = 8 ; i <= 13 ; i++)
			sideSum += cbinStone[i];
		if(sideSum == 0)
			return true;
		
		return false;
	}
	
	//Usage : to inform next player to do action
	//@param : player - player which you want to inform
	public void inform(Player player)
	{
		if(player.getController().getClass().getName() == "AIController"){
			AIController controller = (AIController)player.getController();
			controller.doAction();
		}
	}
	
	//Usage : push a message to stack
	//@param : msg - push message
	public void pushMessage(Message msg)
	{
		msgStack.push(msg);
	}
	
	/*****************Graphics************************/
	//Main render function (this will call the other painting function
	public void render()
	{
		if(currentPlayer == null)
			return;
		
		//Get grahpics
		Graphics g = buffer.getDrawGraphics();
		
		if(g == null)
			return;
		
		//Cast to Graphics2D (for transparency
		Graphics2D g2d = (Graphics2D)g;
		
		//clear buffer
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		
		//fill content
		renderBackground(g2d);
		renderFocus(g2d);
		renderGrid(g2d);
		renderStones(g2d);
		renderBin(g2d);
		renderUI(g2d);
		
		//dispose
		g2d.dispose();
		buffer.show();
		
	}
	//Background
	public void renderBackground(Graphics2D g2d)
	{
		//Background
		//Temp : use tilling picture later
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, width, height);
		
	}
	
	//Assistant
	public void renderGrid(Graphics2D g2d)
	{
		g2d.setColor(Color.white);
		for(int col = 0 ; col < 8 ; col++)
		{
			int x = 20 + col*Mancala.SIZE_BIN;
		
			g2d.drawLine(x, 50, x, 50 + Mancala.SIZE_BIN*2);
		}
		g2d.drawLine(20 + Mancala.SIZE_BIN , 50 + Mancala.SIZE_BIN , 20 + Mancala.SIZE_BIN*7 , 50 + Mancala.SIZE_BIN);
		
	}
	
	//Bin
	public void renderBin(Graphics2D g2d)
	{
		g2d.setColor(Color.WHITE);
		g2d.drawOval(20 , 50, Mancala.SIZE_BIN, Mancala.SIZE_BIN * 2);
		for(int row = 0 ; row < 2 ; row++){
			for(int col = 1 ; col <= 6 ; col++){
				int x = col * Mancala.SIZE_BIN + 20;
				int y = row * Mancala.SIZE_BIN + 50;
				g2d.drawOval(x, y, Mancala.SIZE_BIN, Mancala.SIZE_BIN);
				
			}
		}
		g2d.drawOval(7 * Mancala.SIZE_BIN + 20 , 50, Mancala.SIZE_BIN, Mancala.SIZE_BIN * 2);
	}
	
	//Paint stones in the array
	public void renderStones(Graphics2D g2d)
	{
		for(Stone stone : stones){
		
			if(stone.getIndex() == currentPlayer.getFocus())
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
			
			
			int ox = 20 + indexToCol(stone.getIndex())*Mancala.SIZE_BIN + Mancala.OFFSET ;
			int oy = 50 + indexToRow(stone.getIndex())*Mancala.SIZE_BIN + Mancala.OFFSET ;
			
			int x = stone.getRX() + ox ;
			int y = stone.getRY() + oy ;
			
			g2d.setColor(stone.getColor());
			g2d.fillOval(x, y, Mancala.SIZE_STONE, Mancala.SIZE_STONE);
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
	}
	
	//Paint Focus Rect
	public void renderFocus(Graphics2D g2d)
	{
		if(currentPlayer.getFocus() <= 0 || currentPlayer.getFocus() == 7 || currentPlayer.getFocus() == 14)
			return;
		//Reverse to row and col
		int row = indexToRow(currentPlayer.getFocus());
		int col = indexToCol(currentPlayer.getFocus());

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
		if(currentPlayer.getFocus() > 6)
			g2d.setColor(Color.RED);
		else
			g2d.setColor(Color.CYAN);
		g2d.fillOval(20 + col*Mancala.SIZE_BIN, 50 + row*Mancala.SIZE_BIN, Mancala.SIZE_BIN, Mancala.SIZE_BIN);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		
	}
	
	//UI
	public void renderUI(Graphics2D g2d)
	{
		//Player info
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);
		if(currentPlayer == player)
			g2d.drawString("Your Turn", width - 100, height - 15);
		else
			g2d.drawString("Opponent Turn", 50	, 15);
		
		g2d.drawString(String.valueOf(cbinStone[7]), 20 + 7 * Mancala.SIZE_BIN + Mancala.SIZE_BIN * 0.5f , 50 + g2d.getFont().getSize() + 2*Mancala.SIZE_BIN);
		g2d.drawString(String.valueOf(cbinStone[14]), 20 + Mancala.SIZE_BIN * 0.5f , 50);
		
		//Focus counting
		if(currentPlayer.getFocus() != 7 && currentPlayer.getFocus() != 14 && currentPlayer.getFocus() != 0){
			int row = indexToRow(currentPlayer.getFocus());
			int col = indexToCol(currentPlayer.getFocus());
			g2d.setFont(hintFont);
			g2d.drawString(String.valueOf(cbinStone[currentPlayer.getFocus()]), col * Mancala.SIZE_BIN + Mancala.SIZE_BIN/2 + hintFont.getSize() / 2 , (row + 1) * Mancala.SIZE_BIN);

		}
		
		//if msgStack is not empty
		if(!msgStack.isEmpty()){
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, Mancala.getHEIGHT()/2 - msgFont.getSize(), width, msgFont.getSize() + 20);
		}
		
		g2d.setFont(msgFont);
		g2d.setColor(Color.ORANGE);
		
		for(Message msg : msgStack){
			if(msg != null){
				msg.setAlpha(lerp(msg.getAlpha(), 1.0f, 0.1f));
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,msg.getAlpha()));
				g2d.drawString(msg.getText(), msg.getX(), msg.getY());
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			}
		}
		
		//Check duration
		for(int i = 0 ; i < msgStack.size() ; i++){
			if(System.currentTimeMillis() - msgStack.get(i).getCreateTime() > 1000){
				msgStack.remove(i);
			}
		}
	}
	
	/*********************Misc*********************/
	private int indexToCol(int index)
	{
		int col = 0;
		
		if( index == 14)
			return 0;
		
		//Lower side
		if(index > 0  && index < 8){
			col = index;
		}
		//Upper side
		else{
			col = 6 - (index - 8);
		}
		return col;
	}
	
	private int indexToRow(int index)
	{
		//Reverse to row
		int row = 0;
		
		if(index == 7 || index == 14)
			return 0;
		
		if(index >= 1 && index <= 6){
			row = 1;
		}
		else{
			row = 0;
		}
		return row;
	}
	
	//@param : start - start value
	//@param : end - target value
	//@param : speed - step scale
	private float lerp(float start , float end , float speed)
	{		
		if(start < end){
			float range = end - start;
			float value = start + range * speed;

			if(value >= end)
				return end;
			else
				return value;
		}
		else if(start < end)
		{
			float range = start - end;
			float value = start - range * speed;

			if(value <= end)
				return end;
			else
				return value;
		}
		else 
			return end;
	}
	
	
}
