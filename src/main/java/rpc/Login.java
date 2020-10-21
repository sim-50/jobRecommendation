package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject obj = new JSONObject();
		if (session != null) {
			MySQLConnection connection = new MySQLConnection();
			String userId = session.getAttribute("user_id").toString();
			obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullName(userId));
			connection.close();
		} else {
			obj.put("status", "Invalid Session");
			response.setStatus(403);
		}
		RpcHelper.writeJsonObject(response, obj);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
		String userid = input.getString("user_id");
		String password = input.getString("password");
		MySQLConnection connection = new MySQLConnection();
		JSONObject obj = new JSONObject();
		if(connection.verifyLogin(userid, password)) {
			HttpSession session = request.getSession();
			session.setAttribute("userid", userid);
			//session.setAttribute("password", password);
			obj.put("status", "OK").put("user_id", userid).put("name", connection.getFullName(userid));
		} else {
			obj.put("status", "Login failed, user id does not exist");
			response.setStatus(401);
		}
		connection.close();
		RpcHelper.writeJsonObject(response, obj);
	}

}
