
public class Message {
	
	private String text;
	private float alpha;
	private int x ;
	private int y;
	private long createTime;
	
	public String getText()
	{
		return text;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public float getAlpha()
	{
		return alpha;
	}
	public void setAlpha(float f)
	{
		alpha = f;
	}
	
	public Message(String text , int x , int y)
	{
		this.x = x;
		this.y = y;
		this.text = text;
		this.alpha = 0.0f;
		this.setCreateTime(System.currentTimeMillis());
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
}
