import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Database {
	//Path to each of the courses
	final static String COURSE1;
	final static String COURSE2;
	final static String COURSE3;
	final static String COURSE4;
	final static String COURSE5;
	final static String COURSE6;
	//TODO: Fill the above info

	static String FiletoneExntension;
	static HashSet<String> userStudentIDs = new HashSet<String>(); // for faster searching
	static HashSet<String> demographicStudentIDs = new HashSet<String>(); // for faster searching
	static HashSet<String> discussionStudentIDs = new HashSet<String>(); // for faster searching
	static HashSet<String> feedbackStudentIDs = new HashSet<String>(); // for faster searching
	static ArrayList<UserStudent> userStudents = new ArrayList<UserStudent>();
	static ArrayList<DemographicStudent> demographicStudents = new ArrayList<DemographicStudent>();
	static ArrayList<DiscussionStudent> discussionStudents = new ArrayList<DiscussionStudent>();
	static ArrayList<FeedbackStudent> feedbackStudents = new ArrayList<FeedbackStudent>();
	
	static HashMap<String, String> CourseItemID2CourseItemType = new HashMap<String, String>();
	static ArrayList<CourseItem> CourseItems = new ArrayList<CourseItem>();
	static HashMap<String, String> QuestionID2QuestionDescription = new HashMap<String, String>();
	static HashMap<String, ArrayList<QuestionChoice>> QuestionID2QuestionChoice = new HashMap<String, ArrayList<QuestionChoice>>();
	static HashMap<String, String> PostAnswerID2PostAnswerContent = new HashMap<String, String>();
	static HashMap<String, Double> postAnswerID2Sentiment = new HashMap<String, Double>();
	static HashMap<String, Double> postAnswerID2ManualSentiment = new HashMap<String, Double>();
	static HashSet<ForumAnswer> postAnswers = new HashSet<ForumAnswer>();

	//Fills all the maps above, which are used by the rest of the methods to get the statistics
	//The method has a patter of opening each relevant csv file, getting all the necessary information, storing
	//it in the relevant maps, and moving on to the next file
	static void fillMaps(File main) throws Exception { // main is the directory that contains all the csv files
		File courseGrade = new File(main.getAbsolutePath() + "\\course_grades.csv");
		ArrayList<String> lines = scan(courseGrade);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");
			double grade = Double.parseDouble(processed[5]) * 100;
			UserStudent s = (UserStudent) getStudent(processed[1], StudentType.UserStudent);
			if (s != null)
				s.grade = (int) grade;
			else {
				s = new UserStudent(processed[1]);
				s.grade = (int) grade;
				userStudents.add(s);
				userStudentIDs.add(s.id);
			}
		}
		//-----------------------------course_grades.csv processed above------------------------------
		HashMap<Integer, String> CourseItemTypeID2CourseItemType = new HashMap<Integer, String>();
		File courseItemType = new File(main.getAbsolutePath() + "\\course_item_types.csv");
		lines = scan(courseItemType);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");
			CourseItemTypeID2CourseItemType.put(Integer.parseInt(processed[0]), processed[1].replace("\"", ""));
		}
		//-----------------------------course_item_types.csv processed above------------------------------
		File courseItems = new File(main.getAbsolutePath() + "\\course_items.csv");
		lines = scan(courseItems);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");
			String id = processed[1];
			int typeID = Integer.parseInt(processed[4]);
			String type = CourseItemTypeID2CourseItemType.get(typeID);
			CourseItemID2CourseItemType.put(id, type);
		}
		//-----------------------------course_items.csv processed above------------------------------
		File courseItem = new File(main.getAbsolutePath() + "\\course_item_grades.csv");
		lines = scan(courseItem);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");
			double grade = Double.parseDouble(processed[5]) * 100;
			
			CourseItem item = new CourseItem();
			item.courseItemID = processed[1];
			item.grade = grade;
			item.studentID = processed[2];
			item.type = CourseItemID2CourseItemType.get(processed[1]);
			
			UserStudent s = (UserStudent) getStudent(processed[2], StudentType.UserStudent);
			if (s != null) s.courseItems.add(item);
			else {
				//throw new Exception("Student is null, id: " + processed[2]);
				s = new UserStudent(processed[2]);
				s.courseItems.add(item);
				userStudents.add(s);
				userStudentIDs.add(s.id);
			}
		}
		//-----------------------------course_item_grades.csv processed above------------------------------
		File demographicAnswers = new File(main.getAbsolutePath() + "\\demographics_answers.csv");
		lines = scan(demographicAnswers);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");

			String numeric = (processed.length > 4) ? processed[4].trim() : "";
			DemographicAnswer answer = new DemographicAnswer(processed[0], processed[3], numeric);
			DemographicStudent s = (DemographicStudent) getStudent(processed[1], StudentType.DemographicStudent);
			if (s != null) s.demographicAnswers.add(answer);
			else {
				// throw new Exception("Student is null, id: " + processed[1]);
				s = new DemographicStudent(processed[1]);
				s.demographicAnswers.add(answer);
				demographicStudents.add(s);
				demographicStudentIDs.add(s.id);
			}
		}
		//-----------------------------demographics_answers.csv processed above------------------------------
		File demographicQuestions = new File(main.getAbsolutePath() + "\\demographics_questions.csv");
		lines = scan(demographicQuestions);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");
			QuestionID2QuestionDescription.put(processed[0], processed[2]);
		}
		
		File demographicChoices = new File(main.getAbsolutePath() + "\\demographics_choices.csv");
		lines = scan(demographicChoices);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");

			QuestionChoice choice = new QuestionChoice(Integer.parseInt(processed[1]), processed[2].replace("\"", ""));
			ArrayList<QuestionChoice> choices = QuestionID2QuestionChoice.get(processed[0]);
			if (choices != null) {
				choices.add(choice);
			} else {
				choices = new ArrayList<QuestionChoice>();
				choices.add(choice);
				QuestionID2QuestionChoice.put(processed[0], choices);
			}
		}
		//-----------------------------demographics_questions.csv processed above------------------------------
		File discussionAnswers = new File(main.getAbsolutePath() + "\\discussion_answers.csv");
		lines = scan(discussionAnswers);
		lines.remove(0);
		for (String current : lines) {
			//Remove some data with errors
			if(current.contains("#NAME")) continue;
			String[] processed = current.split(",");
			for(int i = 0; i < processed.length; i++) processed[i] = processed[i].replace("\"", "");
			
			String answerID = processed[0];
			String userID = processed[1];
			String answerContent = processed[3];

			//String questionID = processed[4];
			
			DiscussionStudent s = (DiscussionStudent) getStudent(userID, StudentType.DiscussionStudent);
			if (s != null) s.postAnswerID.add(answerID);
			else {
				// throw new Exception("Student is null, id: " + processed[1]);
				s = new DiscussionStudent(userID);
				s.postAnswerID.add(answerID);
				discussionStudents.add(s);
				discussionStudentIDs.add(s.id);
			}
			PostAnswerID2PostAnswerContent.put(processed[0], processed[3]);
			
			postAnswers.add(new ForumAnswer(answerID, userID, answerContent));
		}
		//-----------------------------discussion_answers.csv processed above------------------------------
		File discussionVotes = new File(main.getAbsolutePath() + "\\discussion_answer_votes.csv");
		lines = scan(discussionVotes);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");
			for(int i = 0; i < processed.length; i++) processed[i] = processed[i].replace("\"", "");
			
			//String studentID = processed[0];
			String postAnswerID = processed[2];
			int value = Integer.parseInt(processed[3]);
			
			ForumAnswer answer = getAnswer(postAnswerID);
			if(answer != null) answer.postScore += value;
			else {
				//throw new Exception("Answer is null, id: " + postAnswerID);
				/*answer = new ForumAnswer(postAnswerID, studentID, null);
				answer.postScore += value;*/
			}
		}
		//-----------------------------discussion_answer_votes.csv processed above------------------------------
		File toneMap = new File(main.getAbsolutePath() + "\\FileToneMap." + FiletoneExntension);
		lines = scan(toneMap);
		for (String current : lines) {
			String[] processed = current.split("-->");
			double sentiment = Double.parseDouble(processed[1]);
			postAnswerID2Sentiment.put(processed[0], sentiment);
		}
		//-----------------------------course_grades.csv processed above------------------------------
		File feedbackFile = new File(main.getAbsolutePath() + "\\feedback_course_ratings.csv");
		lines = scan(feedbackFile);
		lines.remove(0);
		for (String current : lines) {
			String[] processed = current.split(",");

			double rating = Double.parseDouble(processed[3]);
			double maxRating = Double.parseDouble(processed[4]);
			Feedback feedback = new Feedback(rating / maxRating, processed[2].replace("\"", ""));

			FeedbackStudent s = (FeedbackStudent) getStudent(processed[1], StudentType.FeedbackStudent);
			if (s != null) s.feedbacks.add(feedback);
			else {
				//throw new Exception("Student is null, id: " + processed[1]);
				s = new FeedbackStudent(processed[1]);
				s.feedbacks.add(feedback);
				feedbackStudents.add(s);
				feedbackStudentIDs.add(s.id);
			}
		}
		//-----------------------------course_grades.csv processed above------------------------------
		//Do some extra work to make sure all objects have all necessary information
		for(DemographicStudent s : demographicStudents) {
			for(DemographicAnswer a : s.demographicAnswers) {
				a.fetchChoice(QuestionID2QuestionChoice);
				a.fetchQuestion(QuestionID2QuestionDescription);
			}
		}
		
		for(ForumAnswer answer : postAnswers) answer.getSentiment(postAnswerID2Sentiment);
	}

	static void specifyToneCalculationMethod(ToneCalculationMethod tone) {
		FiletoneExntension = tone.toString();
	}
	
	static Student getStudent(String studentID, StudentType type) {
		switch (type) {
			case UserStudent:
				if (!userStudentIDs.contains(studentID)) return null;
				for (Student student : userStudents) {
					if (student.id.equals(studentID))
						return student;
				}
				break;
			case DemographicStudent:
				if (!demographicStudentIDs.contains(studentID)) return null;
				for (Student student : demographicStudents) {
					if (student.id.equals(studentID))
						return student;
				}
				break;
			case FeedbackStudent:
				if (!feedbackStudentIDs.contains(studentID)) return null;
				for (Student student : feedbackStudents) {
					if (student.id.equals(studentID))
						return student;
				}
				break;
			case DiscussionStudent:
				if (!discussionStudentIDs.contains(studentID)) return null;
				for (Student student : discussionStudents) {
					if (student.id.equals(studentID))
						return student;
				}
				break;
		}
		return null;
	}

	static ForumAnswer getAnswer(String answerID) {
		for(ForumAnswer answer : postAnswers) {
			if(answer.answerID.equals(answerID)) return answer;
		}
		return null;
	}
	
	static void genderStatistics() {
		int male = 0;
		int female = 0;
		for(DemographicStudent d : demographicStudents) {
			for(DemographicAnswer answer : d.demographicAnswers) {
				if(answer.questionID == 11) {
					ArrayList<QuestionChoice> choices = QuestionID2QuestionChoice.get("" + answer.questionID);
					for(QuestionChoice choice : choices) 
						if(choice.id == answer.choiceID)
							if(choice.description.equals("male")) male++;
							else female++;
				}
			}
		}
		double percentageMale = male/(double)(male+female)*100;
		double percentageFemale = 100-percentageMale;
		System.out.printf("This course has %.2f%% males and %.2f%% females\n", percentageMale, percentageFemale);
	}

	static void printAverageSentiment() {
		double sentiment = 0;
		int count = 0;
		for(DiscussionStudent s : discussionStudents) {
			sentiment += s.computeSentiment(postAnswerID2Sentiment);
			count++;
		}
		System.out.printf("Average sentiment of course: %.2f\n", sentiment/count);
	}

	static void getAverageGrade() {
		double grade = 0;
		int count = 0;
		for(UserStudent student : userStudents) {
			if(student.grade == -1) continue; 
			grade += student.grade;
			count++;
		}
		System.out.printf("Average grade of course: %.2f\n", grade/count);
	}
	
	static void getTopCountries(int numberOfCountries) {
		HashMap<String, Integer> countries = new HashMap<String, Integer>();
		for(DemographicStudent student : demographicStudents) {
			ArrayList<DemographicAnswer> answers = student.demographicAnswers;
			for(DemographicAnswer answer : answers) {
				if(answer.questionID == 14) {
					String country = answer.choice;
					if(countries.containsKey(country)) countries.put(country,  countries.get(country) + 1);
					else countries.put(country, 1);
				}
			}
		}
		HashMap<Integer, String> hits2Country = new HashMap<Integer, String>();
		for(String country : countries.keySet()) {
			int countryHits = countries.get(country);
			if(hits2Country.containsKey(countryHits)) country = hits2Country.get(countryHits) + "/" + country; 
			hits2Country.put(countryHits, country);
		}
		Object[] sortedCountryHits = countries.values().toArray(); 
		Arrays.sort(sortedCountryHits);
		for(int i = 1; i <= numberOfCountries; i++) {
			int ithCountry = (int) sortedCountryHits[sortedCountryHits.length - i];
			System.out.println("Country number " + i + ": " + hits2Country.get(ithCountry));
		}
	}
	
	static void getHispanicePercentage() {
		int hispaniceOrigin = 0;
		for(DemographicStudent student : demographicStudents) {
			ArrayList<DemographicAnswer> answers = student.demographicAnswers;
			for(DemographicAnswer answer : answers) {
				if(answer.questionID == 16) {
					if(answer.choice.equals("yes")) hispaniceOrigin++;
				}
			}
		}
		double percentage = (double) hispaniceOrigin/demographicStudentIDs.size()*100;
		System.out.printf("Hispanic Origin percentage: %.2f%%\n", percentage);
	}

	static void printTopEducationLevel() {
		HashMap<String, Integer> eduHits = new HashMap<String, Integer>();
		for(DemographicStudent student : demographicStudents) {
			ArrayList<DemographicAnswer> answers = student.demographicAnswers;
			for(DemographicAnswer answer : answers) {
				if(answer.questionID == 18) {
					String choice = answer.choice;
					if(eduHits.containsKey(choice)) eduHits.put(choice, eduHits.get(choice) + 1);
					else eduHits.put(choice, 1);
				}
			}
		}
		HashMap<Integer, String> hits2Edu = new HashMap<Integer, String>();
		for(String edu : eduHits.keySet()) {
			int value = eduHits.get(edu);
			if(hits2Edu.containsKey(value)) hits2Edu.put(value, hits2Edu.get(value) + " --> " + edu); 
			else hits2Edu.put(value, edu);
		}
		Object[] sortedEduHits = eduHits.values().toArray();
		Arrays.sort(sortedEduHits);
		for(int i = 1; i <= sortedEduHits.length; i++) {
			String position = (i == 1) ? "1st" : (i == 2) ? "2nd" : (i == 3) ? "3rd" : (i + "th");
			String educationLevel = hits2Edu.get(sortedEduHits[sortedEduHits.length - i]);
			System.out.println(position + ": " + educationLevel);
		}
	}
	
	static void getAverageAge() {
		int averageAge = 0;
		int count = 0;
		for(DemographicStudent student : demographicStudents) {
			ArrayList<DemographicAnswer> answers = student.demographicAnswers;
			for(DemographicAnswer answer : answers) {
				if(answer.questionID == 12) {
					int age = 2016 - answer.numeric;
					if(age == 2017) continue;
					averageAge += age;
					count++;
				}
			}
		}
		System.out.printf("Average age for this course is: %.2f\n", averageAge/(double)count);
	}
	
	static void postScores() {
		HashMap<Integer, Double> score2AvgSentiment = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> score2Counter = new HashMap<Integer, Integer>();
		for(ForumAnswer answer : postAnswers) {
			Double sentiment = score2AvgSentiment.get(answer.postScore);
			Integer count = score2Counter.get(answer.postScore);
			if(sentiment != null) {
				score2AvgSentiment.put(answer.postScore, sentiment + answer.sentiment);
				score2Counter.put(answer.postScore, count + 1);
			}
			else {
				score2AvgSentiment.put(answer.postScore, answer.sentiment);
				score2Counter.put(answer.postScore, 1);
			}
		}
		Object[] sentiments = score2AvgSentiment.values().toArray();
		Object[] scores = score2AvgSentiment.keySet().toArray();
		Object[] counts = score2Counter.values().toArray();
		System.out.println("Score \t AvgSentiment");
		for(int i = 0; i < score2AvgSentiment.size(); i++) {
			int score = (Integer) scores[i];
			double sentiment = (Double) sentiments[i];
			int count = (Integer) counts[i];
			System.out.printf("%d \t %.2f\n", score, sentiment/count);
		}
	}

	static void feedbacks() {
		double avgRating = 0;
		int count = 0;
		for(FeedbackStudent s : feedbackStudents) {
			double rating = -1;
			for(Feedback f : s.feedbacks) {
				if(f.type == FeedbackType.STAR) rating = f.courseRating;
			}
			if(rating == -1) continue;
			avgRating += rating;
			count++;
		}
		System.out.printf("Average rating for this course is: %.2f%%\n", avgRating/count);
	}

	
	
	
	// Transforms a file into an arraylist of strings, each record in the array is one line in the file
	private static ArrayList<String> scan(File f) {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			String availalbe;
			while ((availalbe = br.readLine()) != null) {
				result.add(availalbe);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
