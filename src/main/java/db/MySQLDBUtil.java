package db;
//mySQL Database utility
public class MySQLDBUtil {
	private static final String INSTANCE = "laiproject-instance.cb7zgkpogvvs.us-east-2.rds.amazonaws.com";
	private static final String PORT_NUM = "3306";
	public static final String DB_NAME = "jupiter";
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "";  //encryption?? if you make the project public, use encryption on the password
	public static final String URL = "jdbc:mysql://"
			+ INSTANCE + ":" + PORT_NUM + "/" + DB_NAME
			+ "?user=" + USERNAME + "&password=" + PASSWORD
			+ "&autoReconnect=true&serverTimezone=UTC";

}
