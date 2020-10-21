package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.MySQLConnection;
import entity.Job;
import external.GitHubClient;

public class Recommendation {
	public List<Job> recommendItems(String userId, double lat, double lon) {
		List<Job> recommendedItems = new ArrayList<>();

		// Step 1: get all favorite item ids from database
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);

		// step2: get all keywords, sorted by count
		Map<String, Integer> allKeywords = new HashMap<>();
		for (String itemId : favoritedItemIds) {
			Set<String> keywords = connection.getKeywords(itemId);
			for (String keyword : keywords) {
				allKeywords.put(keyword, allKeywords.getOrDefault(keyword, 0) + 1);
			}
		}
		connection.close();
		// Rank the keywords by count
		// collection sort: lambda sort.
		// comparator
		List<Entry<String, Integer>> keywordList = new ArrayList<>(allKeywords.entrySet());
		Collections.sort(keywordList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue());
		});
		// cutting down search list only top3
		if (keywordList.size() > 3) {
			keywordList = keywordList.subList(0, 3);
		}

		// step3: search based on keywords, filter out favorite items
		Set<String> visitedItemIds = new HashSet<>();
		GitHubClient client = new GitHubClient();

		for (Entry<String, Integer> keyword : keywordList) {
			//the following is copied from course syllabus
			List<Job> items = client.search(lat, lon, keyword.getKey());

			for (Job item : items) {
				if (!favoritedItemIds.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
					recommendedItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}

		}

		return recommendedItems;
	}
}
