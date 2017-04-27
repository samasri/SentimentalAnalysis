package ToneParsing;
import org.json.JSONArray;
import org.json.JSONObject;

public class ToneParser {
	
	//These information are provided by IBM's Watson ToneAnalyzer service
	final static String URL = "https://gateway.watsonplatform.net/tone-analyzer/api";
	final static String USERNAME = "b5b7a5dc-0966-48ae-a089-acfc5676a0e9";
	final static String PASSWORD = "jbdcYsMaTnsd";
	
	//This method gives an example use of IBM's ToneAnalyzer
	//A sample string is created, the service is called, and the JSON file is parsed using the helper methods
	public static void main(String[] args) throws Exception {
		/*ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
		service.setUsernameAndPassword(USERNAME, PASSWORD);
		
		String text =
				  "I know the times are difficult! Our sales have been "
				      + "disappointing for the past three quarters for our data analytics "
				      + "product suite. We have a competitive data analytics product "
				      + "suite in the industry. But we need to do our job selling it! "
				      + "We need to acknowledge and fix our sales challenges. "
				      + "We can’t blame the economy for our lack of execution! "
				      + "We are missing critical sales opportunities. "
				      + "Our product is in no way inferior to the competitor products. "
				      + "Our clients are hungry for analytical tools to improve their "
				      + "business outcomes. Economy has nothing to do with it.";
		
		// Call the service and get the tone
		String tone = service.getTone(text, null).execute().toString();
		System.out.println(tone);
		
		//Parse JSON string
		JSONObject obj = new JSONObject(tone);
		Document d = new Document();
		d.generalTone = getGeneralTone(obj);
		d.sentences = getSentences(obj);
		
		for(ToneCategory category : d.sentences[5].toneCategories) {
			System.out.println(category.name + ":");
			for(Tone t : category.tones) {
				System.out.println("\t" + t.name + " --> " + t.score);
			}
		}*/
	}
	
	//Take a JSON object, which is basically the JSON tone file returned from IBM's ToneAnalyzer but
	//parsed to a JSON object, and convert that object to a DocumentTone object
	//(DocumentTone is easier to navigate than JSON object)
	public static DocumentTone getGeneralTone(JSONObject obj) {
		JSONObject documentTone = obj.getJSONObject("document_tone");
		DocumentTone d = new DocumentTone();
		JSONArray toneCategories = documentTone.getJSONArray("tone_categories");
		d.toneCategories = getCategories(toneCategories);
		return d;
	}
	
	//Helper method, takes a JSON array found inside the resultant JSON file, and returns that data in
	//a ToneCategory array
	public static ToneCategory[] getCategories(JSONArray toneCategories) {
		ToneCategory[] t = new ToneCategory[toneCategories.length()];
		for(int i = 0; i < toneCategories.length(); i++) {
			JSONObject current = toneCategories.getJSONObject(i);
			t[i] = new ToneCategory();
			t[i].id = current.getString("category_id");
			t[i].name = current.getString("category_name");
			JSONArray tones = current.getJSONArray("tones");
			t[i].tones = new Tone[tones.length()];
			
			for(int j = 0; j < tones.length(); j++) {
				current = tones.getJSONObject(j);
				t[i].tones[j] = new Tone();
				t[i].tones[j].id = current.getString("tone_id");
				t[i].tones[j].name = current.getString("tone_name");
				t[i].tones[j].score = current.getDouble("score");
			}
		}
		return t;
	}
	
	//Takes the resultant JSON object from IBM's ToneAnalyzer and returns the tones of each sentence
	public static Sentence[] getSentences(JSONObject obj) {
		JSONArray sentences = obj.getJSONArray("sentences_tone");
		Sentence[] s = new Sentence[sentences.length()];
		
		for(int i = 0; i < sentences.length(); i++) {
			JSONObject current = sentences.getJSONObject(i);
			s[i] = new Sentence();
			s[i].id = current.getInt("sentence_id");
			s[i].inputFrom = current.getInt("input_from");
			s[i].inputTo = current.getInt("input_to");
			s[i].text= current.getString("text");
			JSONArray toneCategories = current.getJSONArray("tone_categories");
			s[i].toneCategories = getCategories(toneCategories);
		}
		
		return s;
	}
}