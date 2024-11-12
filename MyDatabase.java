import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private Connection connection;

	public MyDatabase() {
		try {
			String url = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;\"\r\n" + //
                                "                + \"database=cs3380;\"\r\n" + //
                                "                + \"user=\" + username + \";\"\r\n" + //
                                "                + \"password=\"+ password +\";\"\r\n" + //
                                "                + \"encrypt=false;\"\r\n" + //
                                "                + \"trustServerCertificate=false;\"\r\n" + //
                                "                + \"loginTimeout=30;";
			// create a connection to the database
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
    }

    //bunch of functions or whatever here just like in A3Q3 for our searching of stuff
}
