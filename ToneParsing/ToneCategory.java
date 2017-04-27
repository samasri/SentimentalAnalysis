package ToneParsing;

//Represents a ToneCategory object in the resultant JSON from IBM's sentimental analysis
public class ToneCategory {
	public String id;
	public String name;
	public Tone[] tones;
	
	//Calculates the average tone in a ToneCategory
	public double getAverageTone() {
		double avg = 0;
		for(Tone current : tones) {
			if(current.id.equals("anger")) avg -= current.score;
			if(current.id.equals("disgust")) avg -= current.score;
			if(current.id.equals("fear")) avg -= current.score;
			if(current.id.equals("joy")) avg += current.score;
			if(current.id.equals("sadness")) avg -= current.score;
			if(current.id.equals("analytical")) avg += fixScore(current.score);
			if(current.id.equals("confident")) avg += fixScore(current.score);
			if(current.id.equals("tentative")) avg -= fixScore(current.score);
			if(current.id.equals("openness_big5")) avg += fixScore(current.score);
			if(current.id.equals("conscientiousness_big5")) avg += fixScore(current.score);
			if(current.id.equals("extraversion_big5")) avg += fixScore(current.score);
			if(current.id.equals("agreeableness_big5")) avg += fixScore(current.score);
			if(current.id.equals("emotional_range_big5")) avg -= fixScore(current.score);
		}
		return avg;
	}
	
	public double fixScore(double score) {
		if(score >= 0.5) return Math.abs(score - 0.5);
		else return -1 * Math.abs(score - 0.5);
	}
}
