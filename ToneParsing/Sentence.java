package ToneParsing;

//Represents a Sentence object in the resultant JSON from IBM's sentimental analysis
public class Sentence {
	public int id;
	public int inputFrom;
	public int inputTo;
	public String text;
	public ToneCategory[] toneCategories;
}
