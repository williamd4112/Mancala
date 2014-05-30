
public class Player {
	
	//index : 8 ~ 13 is upper , 1 ~ 6 is lower
	public static enum Side {
		UPPER,
		LOWER
	};
	
	//which the game player is in
	final Board board;
	
	//AIController or PlayerController
	private Controller controller;
	
	//Indicate mouse focus on which bin
	private int focus = 0;
	
	//Indicate which side player is in
	private Side side;
	
	public Player(Board board , Side side)
	{
		this.board = board;
		this.side = side;
	}
	
	public void addController(Controller controller)
	{
		this.controller = controller;
	}
	
	public Controller getController()
	{
		return this.controller;
	}
	
	public int getFocus()
	{
		return focus;
	}

	public Side getSide()
	{
		return side;
	}
	
	public Board getBoard()
	{
		return board;
	}
	
	//Player use this
	public void onFocus(int x , int y)
	{
		if(!board.isMyTurn(this))
			return;
		for(int row = 0 ; row < 2 ; row++){
			for(int col = 0 ; col < 8 ; col++){
				int x1 = 20 + col*(Mancala.SIZE_BIN);
				int y1 = 50 + row*Mancala.SIZE_BIN;
				int x2 = x1 + Mancala.SIZE_BIN;
				int y2 = 50 + (row + 1)*Mancala.SIZE_BIN;
			
				if(x >= x1 && x <= x2
				&& y >= y1 && y <= y2){
					focus =  Math.abs(7*row - (14 - col - 7*row));
					return;
				}
			}
		}
		focus = 0;
	}
	
	//AI use this
	public void onFocus(int index)
	{
		if(!board.isMyTurn(this))
			return;
		focus = index;
	}
	
	public void playRound(boolean isPlayer)
	{
		if(!board.isMyTurn(this))
			return;
		try {
			if(isIllegal())
				board.execute(isPlayer, getFocus());
			else
				System.err.println("Not legal range.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Check if the selection is in the legal side
	private boolean isIllegal()
	{
		if(side == Side.UPPER){
			if(focus >= 8 && focus <= 13)
				return true;
		}
		else{
			if(focus >= 1 && focus <= 6)
				return true;
		}
		return false;
	}
}
