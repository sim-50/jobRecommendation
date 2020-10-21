package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import entity.Job;

public class MySQLConnection {
	private Connection conn;
	
	public MySQLConnection() {
		try {
			//connect to database 
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//close connection
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//add favorite job on this user
	// why here is Job with all fields? 
	//The job has to exist in items table in order to let history table refer the item_id.  
	public void setFavoriteItems(String userId, Job item) {
		if(conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//MAYBE insert item to items table(job table)
		saveItem(item);
		//method 1: using %s and format.
		//String sql = "INSERT INTO history (user_id, item_id) VALUES('%s', '%s')";
		//String.format(sql, userId, item.getItemId());
		//why not method 1? 
		//Not really security if use the input parameter directly. For instance, the input has logical expression. The whole table might have risk!!
		//To avoid the disadvantage:
		//String sql = "INSERT INTO history (user_id, item_id) VALUES(('%s'), ('%s'))";
		//method 2: using ? 
		String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES(?, ?)"; //if duplicate job, ignore this query.
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			//setString here can make sure the input is string 
			statement.setString(1, userId);
			statement.setString(2, item.getItemId());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	//undo favorite on this user
	//why here is only itemId in parameter? Based on the history table.
	public void unsetFavoriteItems(String userId, String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//whether maybe we need to remove item to items table?
		//Can do this, but by period instead of remove immediately.
		String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, itemId);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	//helper function --> save item to items table
	public void saveItem(Job item) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		//because of the IGNORE in the query, it achieve the purpose for MAYBE insert item
		//set items
		String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setString(3, item.getAddress());
			statement.setString(4, item.getImageUrl());
			statement.setString(5, item.getUrl());
			statement.executeUpdate();
			//set keywords
			sql = "INSERT IGNORE INTO keywords VALUES (?, ?)";
            statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			for (String keyword : item.getKeywords()) {
				statement.setString(2, keyword);
				statement.executeUpdate();
				//there is no need with return value on executeUpdate()
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public Set<String> getFavoriteItemIds(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}

		Set<String> favoriteItems = new HashSet<>();

		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			//ResultSet is used for store the result from SQL query
			ResultSet rs = statement.executeQuery();
			//iterate the whole ResultSet 
			//iterator --> next() is only moving the point, think it as hasNext()(iterable)
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favoriteItems;
	}
	//get Job object based on favoriteItems with item_id
	public Set<Job> getFavoriteItems(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return new HashSet<>();
		}
		Set<Job> favoriteItems = new HashSet<>();
		Set<String> favoriteItemIds = getFavoriteItemIds(userId);

		String sql = "SELECT * FROM items WHERE item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			//iterate the whole favoriteItemsIds
			for (String itemId : favoriteItemIds) {
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();
				//item_id is primary key which is unique.
				//Therefore, using if condition is good enough.
				if (rs.next()) {
					favoriteItems.add(Job.builder()
							.itemId(rs.getString("item_id"))
							.name(rs.getString("name"))
							.address(rs.getString("address"))
							.imageUrl(rs.getString("image_url"))
							.url(rs.getString("url"))
							.keywords(getKeywords(itemId))
							.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}
	
	public Set<String> getKeywords(String itemId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		Set<String> keywords = new HashSet<>();
		String sql = "SELECT keyword from keywords WHERE item_id = ? ";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String keyword = rs.getString("keyword");
				keywords.add(keyword);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return keywords;
	}
	
	public String getFullName(String userId) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return null;
		}
		String name = "";
		String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return name;
	}
	//check whether the user is existed in the user table --> verify log in status
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			return rs.next();
//			same as the following statements 
//			if (rs.next()) {
//				return true;
//			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	//used for 
	public boolean addUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}
		String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			statement.setString(3, firstname);
			statement.setString(4, lastname);
			return statement.executeUpdate() == 1; // 1: add success; 0: add unsuccessfully, the user is existed 
//			same as the following statements 
//			if (rs.next()) {
//				return true;
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
}
