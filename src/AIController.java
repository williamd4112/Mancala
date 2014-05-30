import java.util.Random;


public class AIController extends Controller {

	private Random rnd;
	
	public AIController(Player player) {
		super(player);
		rnd = new Random();
	}
	
	//get inform from board
	public void doAction()
	{
		int index;
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(player.getBoard().isEmptyBin((index = rnd.nextInt(6) + 8 ))){
			
		}
		
		player.onFocus(index);
		player.playRound(false);
	}

}
