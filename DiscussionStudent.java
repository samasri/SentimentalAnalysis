import java.util.ArrayList;
import java.util.HashMap;

//Students that are linked to discussion forum information
public class DiscussionStudent extends Student {

	ArrayList<String> postAnswerID;
	ArrayList<String> postQuestionID;
	
	public DiscussionStudent(String studentID) {
		super(studentID);
		postAnswerID = new ArrayList<String>();
		postQuestionID = new ArrayList<String>();
	}
	
	//Use a map that links each answer to an ID to compute the student's average sentiment based on their posts
	public double computeSentiment(HashMap<String, Double> postAnswerID2Sentiment) {
		double sentiment = 0;
		int count = 0;
		for(String answerID : postAnswerID) {
			count++;
			sentiment += postAnswerID2Sentiment.get(answerID);
		}
		if(count == 0) return -10;
		return sentiment/count;
	}
}
