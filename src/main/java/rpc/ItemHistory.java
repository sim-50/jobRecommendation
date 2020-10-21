package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Job;

import java.util.HashSet;
import java.util.Set;

/**
 * Servlet implementation class ItemHistory
 *  ItemHistory is used for getting my favorite jobs of the user
 */

public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//session here is used for ensure user has logged in.
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		String userId = request.getParameter("user_id");

		MySQLConnection connection = new MySQLConnection();
		Set<Job> items = connection.getFavoriteItems(userId);
		connection.close();

		JSONArray array = new JSONArray();
		for (Job item : items) {
			JSONObject obj = item.toJSONObject();
			//Add a field in order to let front end know the state of "favorite" and show it!
			obj.put("favorite", true);
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		MySQLConnection connection = new MySQLConnection();
		// read from request body then convert to JSONObject
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = obj.getString("user_id");
		Job item = RpcHelper.parseFavoriteItem(obj.getJSONObject("favorite"));
		connection.setFavoriteItems(userId, item);
		connection.close();
		// used for testing whether post successfully
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}

		MySQLConnection connection = new MySQLConnection();
		// read from request body then convert to JSONObject
		JSONObject obj = new JSONObject(IOUtils.toString(request.getReader()));
		String userId = obj.getString("user_id");
		Job item = RpcHelper.parseFavoriteItem(obj.getJSONObject("favorite"));
		connection.unsetFavoriteItems(userId, item.getItemId());
		connection.close();
		// used for testing whether post successfully
		RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));

	}

}
