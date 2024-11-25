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
    
    //d command, can take name (first and last) and a number of results
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

    public void circuitsSearch(String args){
        try{//todo
            String sql = "Select first, last, id from people where first  LIKE ? or last LIKE ?";
			PreparedStatement statement = connection.prepareStatement(sql);
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
    //bunch of functions or whatever here just like in A3Q3 for our searching of da stuff :)
}
