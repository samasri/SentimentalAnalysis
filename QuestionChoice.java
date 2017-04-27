
public class QuestionChoice {
	int id; //ID of question choice
	String description; //Value of that choice (what is the actual answer represented by that choice)
	
	public QuestionChoice(int choiceID, String description) {
		id = choiceID;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return"(" + id + ", " + description + ")";
	}
}
