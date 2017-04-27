import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;

import ToneParsing.DocumentTone;
import ToneParsing.ToneCategory;
import ToneParsing.ToneParser;

public class ForumPostsSentimentalAnalysis{
	//Info obtained from IBM's ToneAnalyzer service to
	final static String URL;
	final static String USERNAME;
	final static String PASSWORD;
	
	//Path to each of the courses
	final static String COURSE1;
	final static String COURSE2;
	final static String COURSE3;
	final static String COURSE4;
	final static String COURSE5;
	final static String COURSE6;
	//TODO: Fill the above info
	
	static String EXTENSION = "socialTone";
	
	//Takes discussion_answers csv file and returns a map between the "discussion answer id"
	//and the post content
	public static HashMap<String, String> getPosts(File f) {
		ArrayList<String> set = scan(f);
		set.remove(0);
		HashMap<String, String> idToPost = new HashMap<String, String>();
		
		for(String current : set) {
			String id = current.substring(0, current.indexOf(',')-1).replace("\"", "");
			
			current = current.substring(current.indexOf(',') + 1);
			current = current.substring(current.indexOf(',') + 1);
			current = current.substring(current.indexOf(',') + 1);
			current = current.replaceFirst("\"", "");
			
			String post = current.substring(0, current.indexOf('\"'));
			post = removeHiphens(post);
			
			idToPost.put(id, post);
		}
		
		return idToPost;
	}
	
	//Iterates the map, send every post to IBM Machine and stores the resulting tone in a file
	//named the id of that post, the generated files are placed inside outputDir which is passed as a param
	public static void generateToneFiles(File outputDir, HashMap<String, String> idToPost) throws Exception {
		ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
		service.setUsernameAndPassword(USERNAME, PASSWORD);
		
		PrintWriter p;
		double totalFiles = idToPost.keySet().size();
		double counter = 0;
		int percentage = 0;
				
		@SuppressWarnings("rawtypes")
		Iterator it = idToPost.entrySet().iterator();
		while (it.hasNext()) {
			counter++;
			@SuppressWarnings({ "rawtypes" })
			Map.Entry entry = (Map.Entry) it.next();
			String id = (String) entry.getKey();
			String post = (String) entry.getValue();

			p = new PrintWriter(outputDir.getAbsolutePath() + "\\" + id);
			String tone = service.getTone(post, null).execute().toString();
			p.print(tone);
			p.close();
			
			int newPercentage = (int) ((counter/totalFiles)*100);
			if(newPercentage != percentage) {
				percentage = newPercentage;
				System.out.println(newPercentage + "% is done");
			}
		}
	}
	
	//Takes a directory full of files, each file includes a post, the filename is a number
	//Creates a map (file) between filename and sentiment of the inside post
	public static void createIDTonesMap(File outputDir, File TonesDir) throws Exception {		
		Scanner s;
		StringBuilder result = new StringBuilder();
		for(File current : TonesDir.listFiles()) {
			//Load current JSON file to a string
			s = new Scanner(current);
			StringBuilder toneStr = new StringBuilder();
			while(s.hasNextLine()) toneStr.append(s.nextLine() + "\n");
			s.close();
			//Parse JSON string to get average tone
			JSONObject obj = new JSONObject(toneStr.toString());
			DocumentTone tone = ToneParser.getGeneralTone(obj);
			double avgTone = 0;
			for(ToneCategory t : tone.toneCategories) {
				avgTone += t.getAverageTone();
			}
			avgTone /= tone.toneCategories.length;
			result.append( current.getName() + "-->" + avgTone + "\n");
		}

		File output = new File(outputDir.getAbsolutePath() + "\\FileToneMap." + EXTENSION);
		if(output.exists()) throw new Exception("FileToneMap file already exists, please delete it and try again");
		PrintWriter p = new PrintWriter(output);
		p.print(result.toString());
		p.close();
	}
	
	public static void renameFile(String dirPath, String oldName, String newName) {		
		File oldFile = new File(dirPath + "\\" + oldName);
		File newFile = new File(dirPath + "\\" + newName);
		oldFile.renameTo(newFile);
	}
	
	//Transforms a file into an arraylist of strings, each record in the array is one line
	//in the file
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
	
	private static String removeHiphens(String input) {
		StringBuilder result = new StringBuilder();
		
		boolean write = true;
		for(int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if(c == '<') write = false;
			if (c == '>') write = true;
			
			if(write) result.append(c);
		}
		
		return result.toString().replace(">", ".").replace("..", ".");
	}
}
