package external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import entity.Job;

public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
	private static final String DEFAULT_KEYWORD = "developer";

	public List<Job> search(double lat, double lon, String keyword) {
		// corner case: (keyword is not required parameter)
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		// deal with special characters in string
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // Rick Sun -> Rick+Sun
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// url format based on URL_TEMPLATE
		String url = String.format(URL_TEMPLATE, keyword, lat, lon);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// httpGet store all information about request
		HttpGet httpGet = new HttpGet(url);
		// Create a custom response handler
		ResponseHandler<List<Job>> responseHandler = new ResponseHandler<List<Job>>() {
			@Override
			public List<Job> handleResponse(final HttpResponse response) throws IOException {
				// corner case 1: status of response is error based on status code
				if (response.getStatusLine().getStatusCode() != 200) {
					return new ArrayList<>();
				}
				// corner case 2: search result is null
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					return new ArrayList<>();
				}
				// from github, the response things are still strings but just JSON style.
				String responseBody = EntityUtils.toString(entity);
				// change string to JSON array
				JSONArray array = new JSONArray(responseBody);
				return getJobList(array);
			}
		};

		try {
			return httpclient.execute(httpGet, responseHandler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	// convert jsonArray to Job List with clean fields and data
	private List<Job> getJobList(JSONArray array) {
		List<Job> jobList = new ArrayList<>();
		List<String> descriptionList = new ArrayList<>();
		// iterate the whole JSONArray(search result) from GitHub
		// 1. making String array which is a list of searched Jobs  
		for(int i = 0; i < array.length(); i++) {
			String description = getStringFieldOrEmpty(array.getJSONObject(i),"description");
			//corner case: description is null --> return title directly
			if(description.equals("") ||description.equals("\n")) {
				descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
			}else {
				descriptionList.add(description);
			}
		}
		//Extract keywords from monkeyLearnClient
		List<List<String>> keywordlist = MonkeyLearnClient
				.extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));
		
		// 2. clean data fields to Job class
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Job job = Job.builder().itemId(getStringFieldOrEmpty(object, "id")) // helper method below;
					.name(getStringFieldOrEmpty(object, "title")).address(getStringFieldOrEmpty(object, "location"))
					.url(getStringFieldOrEmpty(object, "url")).imageUrl(getStringFieldOrEmpty(object, "company_logo"))
					.keywords(new HashSet<String>(keywordlist.get(i)))
					.build();
			jobList.add(job);
		}
		return jobList;
	}

	// get value depends on field
	private String getStringFieldOrEmpty(JSONObject obj, String field) {
		return obj.isNull(field) ? "" : obj.getString(field);
	}

}
