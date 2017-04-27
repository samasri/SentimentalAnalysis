import java.util.ArrayList;

//Students that are linked to demographic info
public class DemographicStudent extends Student {

	ArrayList<DemographicAnswer> demographicAnswers;
	
	public DemographicStudent(String studentID) {
		super(studentID);
		demographicAnswers = new ArrayList<DemographicAnswer>();
	}

}
