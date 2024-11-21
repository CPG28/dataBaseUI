import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyUI {
    private Connection connection;

	public MyUI(String url) {
		try {
			// create a connection to the database
			connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            ResultSet resultSet = null;
            String selectSql = "SELECT driverFirstName, driverLastName from drivers;";
            resultSet = statement.executeQuery(selectSql);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + 
                " " + resultSet.getString(2));
            }
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}

    }

    //bunch of functions or whatever here just like in A3Q3 for our searching of stuff
}
