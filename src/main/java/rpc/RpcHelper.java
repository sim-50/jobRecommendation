package rpc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Job;

public class RpcHelper {
	// new two methods for json array or json object
	// writes a JSONArray to http response
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		// identify the type of data in code for front end
		response.setContentType("application/json");
		response.getWriter().print(array);
	}

	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {
		response.setContentType("application/json");
		response.getWriter().print(obj);
	}

	// Convert a JSON Object to Job Object
	public static Job parseFavoriteItem(JSONObject favoriteItem) {
		Set<String> keywords = new HashSet<>();
		JSONArray array = favoriteItem.getJSONArray("keywords");
		for (int i = 0; i < array.length(); i++) {
			keywords.add(array.getString(i));
		}
		return Job.builder().itemId(favoriteItem.getString("item_id")).name(favoriteItem.getString("name"))
				.address(favoriteItem.getString("address")).url(favoriteItem.getString("url"))
				.imageUrl(favoriteItem.getString("image_url")).keywords(keywords).build();
	}

}
