import java.util.ArrayList;

//Students that are linked to Feedback information
public class FeedbackStudent extends Student {

	ArrayList<Feedback> feedbacks;
	
	public FeedbackStudent(String studentID) {
		super(studentID);
		feedbacks = new ArrayList<Feedback>();
	}

}
