import java.util.ArrayList;
import java.util.HashMap;

public class DemographicAnswer {
	int questionID; //The question ID for which this answer is for
	int numeric; //If the question should have a numeric answer, the value is stored in this variable
	int choiceID; //If the question is a multiple choice question, the choice ID is stored in this variable
	String choice; //Content of choice
	String question; //Content of question
	
	//Force user to have the below 3 parameters when constructing a demographic answer
	public DemographicAnswer(String questionID, String choiceID, String numeric) {
		this.questionID = Integer.parseInt(questionID);
		this.choiceID = Integer.parseInt(choiceID);
		try {
			this.numeric = Integer.parseInt(numeric);
		}
		catch(Exception e) {
			this.numeric = -1;
		}
	}
	
	//For debugging, to print the object in a readable way
	@Override
	public String toString() {
		return "(" + questionID + ", " + choiceID + ", " + numeric + ")";
	}
	
	public void fetchChoice(HashMap<String, ArrayList<QuestionChoice>> QuestionID2QuestionChoice) {
	
		ArrayList<QuestionChoice> choices = QuestionID2QuestionChoice.get("" + questionID);
		for(QuestionChoice c : choices) {
			if(c.id == choiceID) {
				if(questionID == 14) choice = c.description.substring(8);
				else choice = c.description;
			}
		}
	}
	
	public void fetchQuestion(HashMap<String, String> QuestionID2QuestionDescription) {
		question = QuestionID2QuestionDescription.get("" + questionID);
	}
}
