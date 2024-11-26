import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDatabase {
    private Connection connection;

	public MyDatabase(String url) {
		try {
			// create a connection to the database
			connection = DriverManager.getConnection(url);
            // Statement statement = connection.createStatement();
            // ResultSet resultSet = null;
            // String selectSql = "SELECT driverFirstName, driverLastName from drivers;";
            // resultSet = statement.executeQuery(selectSql);
            // while (resultSet.next()) {
            //     System.out.println(resultSet.getString(1) + 
            //     " " + resultSet.getString(2));
            // }
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}

    }
    
    //d command, can take name (first and last) and a number of results. Could definitely use a refactor lol.
    public void driverSearch(String args){
        try{
        String[] parts = args.trim().split(" ");
        String firstName = null;
        String lastName = null;
        Integer numberOfResults = null;
        String sql = "";
        PreparedStatement statement = null;
        if(parts.length == 3){
            firstName = parts[0];
            lastName = parts[1];
            numberOfResults = Integer.parseInt(parts[2]);
            sql = "WITH raceNums AS ( \r\n" + //
                                "\r\n" + //
                                "    \tselect drivers.driverID, count(*) as raceCount from drivers  \r\n" + //
                                "\r\n" + //
                                "join results on drivers.driverID = results.driverID  \r\n" + //
                                "\r\n" + //
                                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                                "\r\n" + //
                                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                                "\r\n" + //
                                "GROUP BY drivers.driverID \r\n" + //
                                "\r\n" + //
                                "), \r\n" + //
                                "\r\n" + //
                                "pointCount as( \r\n" + //
                                "\r\n" + //
                                "    \tSELECT \r\n" + //
                                "\r\n" + //
                                "      \tCAST(drivers.driverFirstName AS NVARCHAR(MAX)) AS driverFirstName,  \r\n" + //
                                "\r\n" + //
                                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)) AS driverLastName,  \r\n" + //
                                "\r\n" + //
                                "      \tsum(numPoints) as totalPoints, drivers.driverID \r\n" + //
                                "\r\n" + //
                                "    \tFROM drivers \r\n" + //
                                "\r\n" + //
                                "    \tJOIN results on drivers.driverID = results.driverID \r\n" + //
                                "\r\n" + //
                                "    \tJOIN raceResults on results.resultID = raceResults.resultID \r\n" + //
                                "\r\n" + //
                                "    \tJOIN races on results.raceID = races.raceID \r\n" + //
                                "\r\n" + //
                                "    \tJoin constructors on results.constructorID = constructors.constructorID \r\n" + //
                                "\r\n" + //
                                "    \tgroup BY \r\n" + //
                                "\r\n" + //
                                "      CAST(drivers.driverFirstName AS NVARCHAR(MAX)), \r\n" + //
                                "\r\n" + //
                                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)), \r\n" + //
                                "\r\n" + //
                                "        drivers.driverID   \r\n" + //
                                "\r\n" + //
                                "), \r\n" + //
                                "\r\n" + //
                                "raceWins as( \r\n" + //
                                "\r\n" + //
                                "    \tselect drivers.driverID, count(*) as winCount from drivers  \r\n" + //
                                "\r\n" + //
                                "join results on drivers.driverID = results.driverID  \r\n" + //
                                "\r\n" + //
                                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                                "\r\n" + //
                                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                                "\r\n" + //
                                "and results.finalPos = 1 \r\n" + //
                                "\r\n" + //
                                "    \tGROUP BY drivers.driverID \r\n" + //
                                "\r\n" + //
                                "), \r\n" + //
                                "\r\n" + //
                                "polePositions as( \r\n" + //
                                "\r\n" + //
                                "    \tselect drivers.driverID, count(*) as poleCount from drivers  \r\n" + //
                                "\r\n" + //
                                "join results on drivers.driverID = results.driverID  \r\n" + //
                                "\r\n" + //
                                "JOIN qualifyingResults on results.resultID = qualifyingResults.resultID  \r\n" + //
                                "\r\n" + //
                                "Where results.finalPos = 1 \r\n" + //
                                "\r\n" + //
                                "    \tGROUP BY drivers.driverID \r\n" + //
                                "\r\n" + //
                                ") \r\n" + //
                                "\r\n" + //
                                "select TOP " + numberOfResults +  " raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n" + //
                                "\r\n" + //
                                "join raceNums on pointCount.driverID = raceNums.driverID  \r\n" + //
                                "\r\n" + //
                                "left join raceWins on raceNums.driverID = raceWins.driverID \r\n" + //
                                "\r\n" + //
                                "left join polePositions on raceWins.driverID = polePositions.driverID  \r\n" + //
                                "\r\n" + //
                                "where driverFirstName like ? or driverLastName like ? \r\n" + //
                                "\r\n" + //
                                "order by totalPoints DESC; ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + firstName +"%");
            statement.setString(2, "%" + lastName + "%");
        }
        else if(parts.length == 2){
            if(isNumeric(parts[1])){
                firstName = parts[0];
                numberOfResults = Integer.parseInt(parts[1]);
                sql = "WITH raceNums AS ( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as raceCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "GROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "pointCount as( \r\n" + //
                "\r\n" + //
                "    \tSELECT \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverFirstName AS NVARCHAR(MAX)) AS driverFirstName,  \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)) AS driverLastName,  \r\n" + //
                "\r\n" + //
                "      \tsum(numPoints) as totalPoints, drivers.driverID \r\n" + //
                "\r\n" + //
                "    \tFROM drivers \r\n" + //
                "\r\n" + //
                "    \tJOIN results on drivers.driverID = results.driverID \r\n" + //
                "\r\n" + //
                "    \tJOIN raceResults on results.resultID = raceResults.resultID \r\n" + //
                "\r\n" + //
                "    \tJOIN races on results.raceID = races.raceID \r\n" + //
                "\r\n" + //
                "    \tJoin constructors on results.constructorID = constructors.constructorID \r\n" + //
                "\r\n" + //
                "    \tgroup BY \r\n" + //
                "\r\n" + //
                "      CAST(drivers.driverFirstName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "        drivers.driverID   \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "raceWins as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as winCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "and results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "polePositions as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as poleCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN qualifyingResults on results.resultID = qualifyingResults.resultID  \r\n" + //
                "\r\n" + //
                "Where results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                ") \r\n" + //
                "\r\n" + //
                "select TOP " + numberOfResults +  " raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n" + //
                "\r\n" + //
                "join raceNums on pointCount.driverID = raceNums.driverID  \r\n" + //
                "\r\n" + //
                "left join raceWins on raceNums.driverID = raceWins.driverID \r\n" + //
                "\r\n" + //
                "left join polePositions on raceWins.driverID = polePositions.driverID  \r\n" + //
                "\r\n" + //
                "where driverFirstName like ? or driverLastName like ? \r\n" + //
                "\r\n" + //
                "order by totalPoints DESC; ";
                statement = connection.prepareStatement(sql);
                statement.setString(1, "%" + firstName +"%");
                statement.setString(2, "%" + firstName + "%");
            }
            else{
                firstName = parts[0];
                lastName = parts[1];
                sql = "WITH raceNums AS ( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as raceCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "GROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "pointCount as( \r\n" + //
                "\r\n" + //
                "    \tSELECT \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverFirstName AS NVARCHAR(MAX)) AS driverFirstName,  \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)) AS driverLastName,  \r\n" + //
                "\r\n" + //
                "      \tsum(numPoints) as totalPoints, drivers.driverID \r\n" + //
                "\r\n" + //
                "    \tFROM drivers \r\n" + //
                "\r\n" + //
                "    \tJOIN results on drivers.driverID = results.driverID \r\n" + //
                "\r\n" + //
                "    \tJOIN raceResults on results.resultID = raceResults.resultID \r\n" + //
                "\r\n" + //
                "    \tJOIN races on results.raceID = races.raceID \r\n" + //
                "\r\n" + //
                "    \tJoin constructors on results.constructorID = constructors.constructorID \r\n" + //
                "\r\n" + //
                "    \tgroup BY \r\n" + //
                "\r\n" + //
                "      CAST(drivers.driverFirstName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "        drivers.driverID   \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "raceWins as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as winCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "and results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "polePositions as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as poleCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN qualifyingResults on results.resultID = qualifyingResults.resultID  \r\n" + //
                "\r\n" + //
                "Where results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                ") \r\n" + //
                "\r\n" + //
                "select raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n" + //
                "\r\n" + //
                "join raceNums on pointCount.driverID = raceNums.driverID  \r\n" + //
                "\r\n" + //
                "left join raceWins on raceNums.driverID = raceWins.driverID \r\n" + //
                "\r\n" + //
                "left join polePositions on raceWins.driverID = polePositions.driverID  \r\n" + //
                "\r\n" + //
                "where driverFirstName like ? or driverLastName like ? \r\n" + //
                "\r\n" + //
                "order by totalPoints DESC; ";
                statement = connection.prepareStatement(sql);
                statement.setString(1, "%" + firstName +"%");
                statement.setString(2, "%" + lastName + "%");
            }
        }
        else if(parts.length==1){
            if(isNumeric(lastName)){
                numberOfResults = Integer.parseInt(parts[0]);
                sql = "WITH raceNums AS ( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as raceCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "GROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "pointCount as( \r\n" + //
                "\r\n" + //
                "    \tSELECT \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverFirstName AS NVARCHAR(MAX)) AS driverFirstName,  \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)) AS driverLastName,  \r\n" + //
                "\r\n" + //
                "      \tsum(numPoints) as totalPoints, drivers.driverID \r\n" + //
                "\r\n" + //
                "    \tFROM drivers \r\n" + //
                "\r\n" + //
                "    \tJOIN results on drivers.driverID = results.driverID \r\n" + //
                "\r\n" + //
                "    \tJOIN raceResults on results.resultID = raceResults.resultID \r\n" + //
                "\r\n" + //
                "    \tJOIN races on results.raceID = races.raceID \r\n" + //
                "\r\n" + //
                "    \tJoin constructors on results.constructorID = constructors.constructorID \r\n" + //
                "\r\n" + //
                "    \tgroup BY \r\n" + //
                "\r\n" + //
                "      CAST(drivers.driverFirstName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "        drivers.driverID   \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "raceWins as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as winCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "and results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "polePositions as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as poleCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN qualifyingResults on results.resultID = qualifyingResults.resultID  \r\n" + //
                "\r\n" + //
                "Where results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                ") \r\n" + //
                "\r\n" + //
                "select TOP " + numberOfResults +  " raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n" + //
                "\r\n" + //
                "join raceNums on pointCount.driverID = raceNums.driverID  \r\n" + //
                "\r\n" + //
                "left join raceWins on raceNums.driverID = raceWins.driverID \r\n" + //
                "\r\n" + //
                "left join polePositions on raceWins.driverID = polePositions.driverID  \r\n" +
                "order by totalPoints DESC; ";
                statement = connection.prepareStatement(sql);
            }
            else{
                firstName = parts[0];
                sql = "WITH raceNums AS ( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as raceCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "GROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "pointCount as( \r\n" + //
                "\r\n" + //
                "    \tSELECT \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverFirstName AS NVARCHAR(MAX)) AS driverFirstName,  \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)) AS driverLastName,  \r\n" + //
                "\r\n" + //
                "      \tsum(numPoints) as totalPoints, drivers.driverID \r\n" + //
                "\r\n" + //
                "    \tFROM drivers \r\n" + //
                "\r\n" + //
                "    \tJOIN results on drivers.driverID = results.driverID \r\n" + //
                "\r\n" + //
                "    \tJOIN raceResults on results.resultID = raceResults.resultID \r\n" + //
                "\r\n" + //
                "    \tJOIN races on results.raceID = races.raceID \r\n" + //
                "\r\n" + //
                "    \tJoin constructors on results.constructorID = constructors.constructorID \r\n" + //
                "\r\n" + //
                "    \tgroup BY \r\n" + //
                "\r\n" + //
                "      CAST(drivers.driverFirstName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "      \tCAST(drivers.driverLastName AS NVARCHAR(MAX)), \r\n" + //
                "\r\n" + //
                "        drivers.driverID   \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "raceWins as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as winCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN raceResults on results.resultID = raceResults.resultID  \r\n" + //
                "\r\n" + //
                "where CAST(raceResults.raceType as NVARCHAR(MAX)) like 'GP'  \r\n" + //
                "\r\n" + //
                "and results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                "), \r\n" + //
                "\r\n" + //
                "polePositions as( \r\n" + //
                "\r\n" + //
                "    \tselect drivers.driverID, count(*) as poleCount from drivers  \r\n" + //
                "\r\n" + //
                "join results on drivers.driverID = results.driverID  \r\n" + //
                "\r\n" + //
                "JOIN qualifyingResults on results.resultID = qualifyingResults.resultID  \r\n" + //
                "\r\n" + //
                "Where results.finalPos = 1 \r\n" + //
                "\r\n" + //
                "    \tGROUP BY drivers.driverID \r\n" + //
                "\r\n" + //
                ") \r\n" + //
                "\r\n" + //
                "select raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n" + //
                "\r\n" + //
                "join raceNums on pointCount.driverID = raceNums.driverID  \r\n" + //
                "\r\n" + //
                "left join raceWins on raceNums.driverID = raceWins.driverID \r\n" + //
                "\r\n" + //
                "left join polePositions on raceWins.driverID = polePositions.driverID  \r\n" + //
                "\r\n" + //
                "where driverFirstName like ? or driverLastName like ? \r\n" + //
                "\r\n" + //
                "order by totalPoints DESC; ";
                statement = connection.prepareStatement(sql);
                statement.setString(1, "%" + firstName +"%");
                statement.setString(2, "%" + firstName + "%");
            }
        }
        ResultSet resultSet = statement.executeQuery();
        System.out.println("");
        System.out.printf("%-10s| %-20s| %-20s| %-15s| %-10s| %-10s| %-10s%n", "driverID", "First Name", "Last Name", "Point Total", "Race Count", "Win Count", "Pole Count");
        System.out.println("-".repeat(110));
        while (resultSet.next()) {
            int id = resultSet.getInt("driverID");
            String first = resultSet.getString("driverFirstName");
            String last = resultSet.getString("driverLastName");
            int pointCount = resultSet.getInt("totalPoints");
            int raceCount = resultSet.getInt("raceCount");
            int winCount = resultSet.getInt("winCount");
            int poleCount = resultSet.getInt("poleCount");
            System.out.printf("%-10d| %-20s| %-20s| %-15s| %-10s| %-10s| %-10s %n", id, first, last, pointCount, raceCount, winCount, poleCount);
        }
        System.out.println("");
        resultSet.close();
        statement.close();
    }
    catch(SQLException e){
        e.printStackTrace(System.out);
    }
    }

    //circuits command, takes s,n, or nothing. We may want to add ability to limit results here too
    public void circuitsSearch(String args){
        try{//todo
            String[] parts = args.trim().split(" ");
            String sql = null;
            if(parts.length == 1 && (parts[0].equalsIgnoreCase("n") || parts[0].equalsIgnoreCase("s"))){
                if(parts[0].equalsIgnoreCase("n")){
                    sql = "select circuitCountry, circuitLongitude, circuitLatitude, circuitName, circuitID from circuits where circuitLatitude > 0 order by circuitLatitude;";
                }
                else if(parts[0].equalsIgnoreCase("s")){
                    sql = "select circuitCountry, circuitLongitude, circuitLatitude, circuitName, circuitID from circuits where circuitLatitude < 0 order by circuitLatitude; ";
                }
            }
            else{
                sql = "select circuitCountry, circuitLongitude, circuitLatitude, circuitName, circuitID from circuits order by circuitLatitude; ";
            }
			PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            System.out.printf("%-10s| %-40s| %-20s| %-20s| %-20s%n", "circuitID", "Circuit Name", "Circuit Country", "Latitude", "Longitude");
            System.out.println("-".repeat(120));
            while(resultSet.next()){
                int id = resultSet.getInt("circuitID");
                String name = resultSet.getString("circuitName");
                int longy = resultSet.getInt("circuitLongitude");
                int latty = resultSet.getInt("circuitLatitude");
                String country = resultSet.getString("circuitCountry");
                System.out.printf("%-10d| %-40s| %-20s| %-20d| %-20d%n", id, name, country, longy, latty);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            e.printStackTrace(System.out);
        }
    }
    
    //drivers championship query. takes a year or none. If no year it returns the current season.
    public void dChampSearch(String args){
        try{
            String[] parts = args.trim().split(" ");
            String sql = "SELECT \r\n" + //
                                "\r\n" + //
                                "CAST(drivers.driverFirstName AS NVARCHAR(MAX)) AS driverFirstName,  \r\n" + //
                                "\r\n" + //
                                "CAST(drivers.driverLastName AS NVARCHAR(MAX)) AS driverLastName,  \r\n" + //
                                "\r\n" + //
                                "CAST(constructors.constructorName AS NVARCHAR(MAX)) AS teamName,  \r\n" + //
                                "\r\n" + //
                                "sum(numPoints) AS totalPoints \r\n" + //
                                "\r\n" + //
                                "FROM drivers \r\n" + //
                                "\r\n" + //
                                "JOIN results on drivers.driverID = results.driverID \r\n" + //
                                "\r\n" + //
                                "JOIN raceResults on results.resultID = raceResults.resultID \r\n" + //
                                "\r\n" + //
                                "JOIN races on results.raceID = races.raceID \r\n" + //
                                "\r\n" + //
                                "JOIN constructors on results.constructorID = constructors.constructorID \r\n" + //
                                "\r\n" + //
                                "WHERE races.season = ? \r\n" + //
                                "\r\n" + //
                                "GROUP BY \r\n" + //
                                "\r\n" + //
                                "CAST(drivers.driverFirstName AS NVARCHAR(MAX)), \r\n" + //
                                "\r\n" + //
                                "CAST(drivers.driverLastName AS NVARCHAR(MAX)),   \r\n" + //
                                "\r\n" + //
                                "CAST(constructors.constructorName AS NVARCHAR(MAX)) \r\n" + //
                                "\r\n" + //
                                "order BY \r\n" + //
                                "\r\n" + //
                                "totalPoints DESC; ";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(parts.length == 1 && !parts[0].equals("")){
                statement.setInt(1, Integer.parseInt(parts[0]));
            }
            else{
                statement.setInt(1, 2024);
            }
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-5s| %-20s| %-20s| %-20s| %-10s%n", "Place", "First Name", "Last Name", "Team Name", "Points");
            System.out.println("-".repeat(100));
            int place = 0;
            while(resultSet.next()){
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                String team = resultSet.getString("teamName");
                int points = resultSet.getInt("totalPoints");
                System.out.printf("%-5d| %-20s| %-20s| %-20s| %-10d%n", ++place, first, last, team, points);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            e.printStackTrace(System.out);
        }
    }
    //helper function
    public void cChampSearch(String args){
        try{
            String[] parts = args.trim().split(" ");
            String sql = "SELECT \r\n" + //
                                "\r\n" + //
                                "CAST(constructors.constructorName AS NVARCHAR(MAX)) AS teamName,  \r\n" + //
                                "\r\n" + //
                                "sum(numPoints) as totalPoints \r\n" + //
                                "\r\n" + //
                                "FROM results \r\n" + //
                                "\r\n" + //
                                "JOIN raceResults on raceResults.resultID = results.resultID \r\n" + //
                                "\r\n" + //
                                "JOIN races on results.raceID = races.raceID \r\n" + //
                                "\r\n" + //
                                "JOIN constructors on results.constructorID = constructors.constructorID \r\n" + //
                                "\r\n" + //
                                "WHERE races.season = ? \r\n" + //
                                "\r\n" + //
                                "GROUP BY CAST(constructors.constructorName AS NVARCHAR(MAX)) \r\n" + //
                                "\r\n" + //
                                "ORDER BY totalPoints DESC; \r\n" + //
                                "\r\n" + //
                                " ";
            PreparedStatement statement = connection.prepareStatement(sql);
            if(parts.length == 1 && !parts[0].equals("")){
                statement.setInt(1, Integer.parseInt(parts[0]));
            }
            else{
                statement.setInt(1, 2024);
            }
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-5s| %-20s| %-10s%n", "Place", "Team Name","Points");
            System.out.println("-".repeat(100));
            int place = 0;
            while(resultSet.next()){
                String team = resultSet.getString("teamName");
                int points = resultSet.getInt("totalPoints");
                System.out.printf("%-5d| %-20s| %-10d%n", ++place, team, points);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            e.printStackTrace(System.out);
        }
    }

    public void youngestWin(String arg){
        try{
            String[] parts = arg.trim().split(" ");
            String sql = "SELECT TOP " + Integer.parseInt(parts[0]) +  "d.driverID, d.driverFirstName, d.driverLastName, r.raceName, \r\n" + //
                                "\r\n" + //
                                "r.raceDate, DATEDIFF(YEAR, d.dob, r.raceDate) AS ageAtWin FROM drivers d \r\n" + //
                                "\r\n" + //
                                "JOIN results res ON d.driverID = res.driverID \r\n" + //
                                "\r\n" + //
                                "JOIN races r ON res.raceID = r.raceID \r\n" + //
                                "\r\n" + //
                                "WHERE res.finalPos = 1 \r\n" + //
                                "\r\n" + //
                                "ORDER BY ageAtWin, r.raceDate; ";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s| %-25s| %-20s%n", "driverID", "First Name", "Last Name", "Race Name","Age at Win");
            System.out.println("-".repeat(100));
            int place = 0;
            while(resultSet.next()){
                int id = resultSet.getInt("driverID");
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                String race = resultSet.getString("raceName");
                int age = resultSet.getInt("ageAtWin");
                System.out.printf("%-10d| %-20s| %-20s| %-25s| %-20s%n", id, first, last, race, age);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            e.printStackTrace(System.out);
        }
    }
    //helper function
    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
