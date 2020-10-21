package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Job;

//import org.json.JSONArray;
//import org.json.JSONObject;

import external.GitHubClient;

/**
 * Servlet implementation class SearchItem
 */
public class SearchJob extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchJob() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//for request part:
//		if(request.getParameter("username") != null) {
//			//new json object
//			JSONObject object = new JSONObject();
//			String username = request.getParameter("username");
//			//put a new field to json object
//			object.put("username", username);
//			RpcHelper.writeJsonObject(response, object);
//		}
//		//new json Array for more objects
//		JSONArray array = new JSONArray();
//		array.put(new JSONObject().put("username", "abcd"));
//		array.put(new JSONObject().put("username", "1234"));
//		RpcHelper.writeJsonArray(response, array);
		//session here is used for ensure user has logged in.
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		//optimize: 
		//String userId = session.getAttribute("user_id").toString();    //here the user id would be the specific one of the session
		String userId = request.getParameter("user_id");                 //here has risk that, the user_id from parameter is not the one from session
		
		MySQLConnection connection = new MySQLConnection();
		Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);
		
		GitHubClient client = new GitHubClient();
		List<Job> jobList = client.search(lat, lon, null);
		//in order to let front end read data in list, need to convert it to JSON again
		JSONArray array = new JSONArray();
		for(Job job: jobList) {
			JSONObject obj = job.toJSONObject();
			obj.put("favorite", favoriteItemIds.contains(job.getItemId()));
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
