package external;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {
	private static final String API_KEY = "af0502b0116929bef9a6ef42a072e9a3a156b5b2";
	private static final String MODEL = "ex_YCya9nrn";   //model id is used for identifying what kind of function in Monkey Learn

	// public method for extract keywords from array of jobs (here is why the input
	// is String array)
	// input: List of "job descriptions".
	// output: List of "keywords list" after extracting.
	public static List<List<String>> extractKeywords(String[] text) {
		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}

		MonkeyLearn ml = new MonkeyLearn(API_KEY); // here is your API key in monkey learn API
		ExtraParam[] extraParams = { new ExtraParam("max_keywords", "3") };
		MonkeyLearnResponse response;

		try {
			response = ml.extractors.extract(MODEL, text, extraParams);
			JSONArray resultArray = response.arrayResult;
			return getKeyWords(resultArray);
		} catch (MonkeyLearnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	// input json array of response from monkey learn, covert to list<list<String>>
	// input example:(JSON file)
	// {
	// {"tesla","car","machine leaning"},
	// {"facebook","hire", "social network"},
	// {"Google", "search", "AI"}
	// }
	// output:(convert to list of list)
	// list of list 
	private static List<List<String>> getKeyWords(JSONArray mlResponse) {
		List<List<String>> topKeyWords = new ArrayList<>();
		// iterate the JSONArray and get keyword list
		for (int i = 0; i < mlResponse.size(); i++) {
			List<String> keywords = new ArrayList<>();
			JSONArray keywordsArray = (JSONArray) mlResponse.get(i);
			for (int j = 0; j < keywordsArray.size(); j++) {
				JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
				String keyword = (String) keywordObject.get("keyword");
				keywords.add(keyword);
			}
			topKeyWords.add(keywords);
		}
		return topKeyWords;
	}

	public static void main(String[] args) {

		String[] textList = {
				"Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", };
		List<List<String>> words = extractKeywords(textList);
		for (List<String> ws : words) {
			for (String w : ws) {
				System.out.println(w);
			}
			System.out.println();
		}
	}

}
