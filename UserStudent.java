import java.util.ArrayList;

//Students that are linked to grades information
public class UserStudent extends Student {

	int grade;
	ArrayList<CourseItem> courseItems;
	
	public UserStudent(String studentID) {
		super(studentID);
		grade = -1;
		courseItems = new ArrayList<CourseItem>();
	}

}
