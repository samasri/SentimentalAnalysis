import java.util.HashMap;

public class ForumAnswer {
	String answerID; //ID of Forum answer
	String studentID; //ID of student who posted it
	int postScore; //How many upvotes did this post or answer get from students
	String content; //Content of the post
	double sentiment; //The sentiment of this post; this may change based on the way we're calculating it
	double manualSentiment; //Manually judging the sentiment (used for comparison to measure accuracy)
	
	public ForumAnswer(String answerID, String studentID, String content) {
		this.answerID = answerID;
		this.studentID = studentID;
		this.content = content;
		postScore = 0;
		sentiment = -1;
	}
	
	public void getSentiment(HashMap<String, Double> sentimentMap) {
		sentiment = sentimentMap.get(answerID);
	}
}