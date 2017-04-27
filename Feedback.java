public class Feedback {
	double courseRating; //the rating given by a student divided by the maximum rating (resulting in a percentage)
	FeedbackType type; //Rating system used to give this feedback

	public Feedback(double courseRating, String feedbackType) throws Exception {
		switch (feedbackType) {
			case "NPS_FIRST_WEEK":
				this.type = FeedbackType.FirstWeek;
				break;
			case "NPS_END_OF_COURSE":
				this.type = FeedbackType.EndOfCourse;
				break;
			case "STAR":
				this.type = FeedbackType.STAR;
				break;
			default:
				throw new Exception("Feedback type is not identified");
		}
		this.courseRating = courseRating;
	}
	
	@Override
	public String toString() {
		return "(" + courseRating + ", " + type + ")";
	}
}
