
public class MessageFactory {
	
	private static MessageFactory instance;
	
	public static enum Type{
		POPUP,
		WARNING,
		HINT
	};
	
	//Singleton mode
	public static MessageFactory getInstance()
	{
		if(instance == null)
			instance = new MessageFactory();
		
		return instance;
	}
	
	public Message createMessage(Type type , String text)
	{
		switch(type){
		case POPUP:
			return new Message(text , Mancala.getWIDTH() / 2 - text.length()/2*12 , Mancala.getHEIGHT()/2);
		case WARNING:
			return new Message(text , 12 , Mancala.getHEIGHT() - 20);
		default:
			return null;
		}
	}
}
