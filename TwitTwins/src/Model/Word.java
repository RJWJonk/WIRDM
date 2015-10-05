package Model;


public class Word 
{
	public static final int WORD = 1;
	public static final int HASHTAG = 2;
	public static final int AT = 3;
	public static final int WEBLINK = 4;

	private String word;
	private int type;
	private int getFrequency;
	
	public Word(String word, int Type)
	{
		this.setWord(word);
		this.setType(Type);
		this.setFrequency(1); //First word is counted
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	// return name of the type
	public String getType() 
	{
		switch(type)
		{
			case WORD: return "Word";
			case HASHTAG: return "#";
			case AT: return "@";
			case WEBLINK: return "Weblink";
		}
		return "not defined!";
	}
	
	public int getRealType()
	{
		return this.type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getGetFrequency() {
		return getFrequency;
	}
	public void setFrequency(int getFrequency) {
		this.getFrequency = getFrequency;
	}
	public void incrementFreq() {
		this.getFrequency += 1;
	}

}
