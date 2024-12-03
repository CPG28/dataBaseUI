import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

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
            // System.out.println(resultSet.getString(1) +
            // " " + resultSet.getString(2));
            // }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

    }

    // d command, can take name (first and last) and a number of results. Could
    // definetely use a refactor lol.
    public void driverSearch(String args) {
        try {
            String[] parts = args.trim().split(" ");
            String firstName = null;
            String lastName = null;
            Integer numberOfResults = null;
            String sql = "";
            PreparedStatement statement = null;
            if (parts.length == 3) {
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
                        "select TOP " + numberOfResults
                        + " raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n"
                        + //
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
                statement.setString(1, "%" + firstName + "%");
                statement.setString(2, "%" + lastName + "%");
            } else if (parts.length == 2) {
                if (isPosNumeric(parts[1])) {
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
                            "select TOP " + numberOfResults
                            + " raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n"
                            + //
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
                    statement.setString(1, "%" + firstName + "%");
                    statement.setString(2, "%" + firstName + "%");
                } else {
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
                            "select raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n"
                            + //
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
                    statement.setString(1, "%" + firstName + "%");
                    statement.setString(2, "%" + lastName + "%");
                }
            } else if (parts.length == 1) {
                if (isPosNumeric(lastName)) {
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
                            "select TOP " + numberOfResults
                            + " raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n"
                            + //
                            "\r\n" + //
                            "join raceNums on pointCount.driverID = raceNums.driverID  \r\n" + //
                            "\r\n" + //
                            "left join raceWins on raceNums.driverID = raceWins.driverID \r\n" + //
                            "\r\n" + //
                            "left join polePositions on raceWins.driverID = polePositions.driverID  \r\n" +
                            "order by totalPoints DESC; ";
                    statement = connection.prepareStatement(sql);
                } else {
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
                            "select raceNums.driverID, driverFirstName, driverLastName, totalPoints, raceCount, winCount, poleCount from pointCount  \r\n"
                            + //
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
                    statement.setString(1, "%" + firstName + "%");
                    statement.setString(2, "%" + firstName + "%");
                }
            }
            ResultSet resultSet = statement.executeQuery();
            System.out.println("");
            System.out.printf("%-10s| %-20s| %-20s| %-15s| %-10s| %-10s| %-10s%n", "driverID", "First Name",
                    "Last Name", "Point Total", "Race Count", "Win Count", "Pole Count");
            System.out.println("-".repeat(110));
            while (resultSet.next()) {
                int id = resultSet.getInt("driverID");
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                int pointCount = resultSet.getInt("totalPoints");
                int raceCount = resultSet.getInt("raceCount");
                int winCount = resultSet.getInt("winCount");
                int poleCount = resultSet.getInt("poleCount");
                System.out.printf("%-10d| %-20s| %-20s| %-15s| %-10s| %-10s| %-10s %n", id, first, last, pointCount,
                        raceCount, winCount, poleCount);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // circuits command, takes s,n, or nothing. We may want to add ability to limit
    // results here too
    public void circuitsSearch(String args) {
        try {// todo
            String[] parts = args.trim().split(" ");
            String sql = null;
            if (parts.length == 1 && (parts[0].equalsIgnoreCase("n") || parts[0].equalsIgnoreCase("s"))) {
                if (parts[0].equalsIgnoreCase("n")) {
                    sql = "select circuitCountry, circuitLongitude, circuitLatitude, circuitName, circuitID from circuits where circuitLatitude > 0 order by circuitLatitude;";
                } else if (parts[0].equalsIgnoreCase("s")) {
                    sql = "select circuitCountry, circuitLongitude, circuitLatitude, circuitName, circuitID from circuits where circuitLatitude < 0 order by circuitLatitude; ";
                }
            } else {
                sql = "select circuitCountry, circuitLongitude, circuitLatitude, circuitName, circuitID from circuits order by circuitName; ";
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            System.out.printf("%-10s| %-40s| %-20s| %-20s| %-20s%n", "circuitID", "Circuit Name", "Circuit Country",
                    "Latitude", "Longitude");
            System.out.println("-".repeat(120));
            while (resultSet.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // drivers championship query. takes a year or none. If no year it returns the
    // current season.
    public void dChampSearch(String args) {
        try {
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
            if (parts.length == 1 && !parts[0].equals("")) {
                statement.setInt(1, Integer.parseInt(parts[0]));
            } else {
                statement.setInt(1, 2024);
            }
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-5s| %-20s| %-20s| %-20s| %-10s%n", "Place", "First Name", "Last Name", "Team Name",
                    "Points");
            System.out.println("-".repeat(100));
            int place = 0;
            while (resultSet.next()) {
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                String team = resultSet.getString("teamName");
                int points = resultSet.getInt("totalPoints");
                System.out.printf("%-5d| %-20s| %-20s| %-20s| %-10d%n", ++place, first, last, team, points);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // helper function
    public void cChampSearch(String args) {
        try {
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
            if (parts.length == 1 && !parts[0].equals("")) {
                statement.setInt(1, Integer.parseInt(parts[0]));
            } else {
                statement.setInt(1, 2024);
            }
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-5s| %-20s| %-10s%n", "Place", "Team Name", "Points");
            System.out.println("-".repeat(100));
            int place = 0;
            while (resultSet.next()) {
                String team = resultSet.getString("teamName");
                int points = resultSet.getInt("totalPoints");
                System.out.printf("%-5d| %-20s| %-10d%n", ++place, team, points);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void youngestWin(String arg) {
        try {
            String[] parts = arg.trim().split(" ");
            String sql = "SELECT TOP " + Integer.parseInt(parts[0])
                    + "d.driverID, d.driverFirstName, d.driverLastName, r.raceName, \r\n" + //
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
            System.out.printf("%-10s| %-20s| %-20s| %-25s| %-20s%n", "driverID", "First Name", "Last Name", "Race Name",
                    "Age at Win");
            System.out.println("-".repeat(100));
            while (resultSet.next()) {
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
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void wins(String args) {
        try {
            String[] parts = args.trim().split(" ");
            String sql = "";
            PreparedStatement statement = null;
            if (parts.length == 1) {
                sql = "select races.raceName, races.raceDate, raceResults.raceType from results  \r\n" + //
                        "\r\n" + //
                        "join raceResults on results.resultID = raceResults.resultID  \r\n" + //
                        "\r\n" + //
                        "join races on results.raceID = races.raceID \r\n" + //
                        "\r\n" + //
                        "where driverID = ? and finalPos = 1 \r\n" + //
                        "\r\n" + //
                        "ORDER BY raceDate;";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(parts[0]));
            } else {
                sql = "select races.raceName, races.raceDate, raceResults.raceType from results join raceResults on results.resultID = raceResults.resultID  \r\n"
                        + //
                        "\r\n" + //
                        "join races on results.raceID = races.raceID \r\n" + //
                        "\r\n" + //
                        "where driverID = ? and finalPos = 1 and season = ? \r\n" + //
                        "\r\n" + //
                        "ORDER BY raceDate; ";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, Integer.parseInt(parts[0]));
                statement.setInt(2, Integer.parseInt(parts[1]));
            }
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-30s| %-15s| %-10s%n", "Race Name", "Race Date", "RaceType");
            System.out.println("-".repeat(80));

            while (resultSet.next()) {
                String raceName = resultSet.getString("raceName");
                String raceDate = resultSet.getString("raceDate");
                String raceType = resultSet.getString("raceType");
                System.out.printf("%-30s| %-15s| %-10s%n", raceName, raceDate, raceType);
            }
            System.out.println("");
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void drivers(String arg) {
        String[] parts = arg.trim().split(" ");
        String sql = "SELECT DISTINCT drivers.driverID, CAST(drivers.driverFirstName AS NVARCHAR(MAX)) as firstName, CAST(drivers.driverLastName AS NVARCHAR(MAX)) AS lastName, CAST(drivers.driverNationality AS NVARCHAR(MAX)) AS nationality FROM drivers INNER JOIN raceIn ON drivers.driverID = raceIn.driverID INNER JOIN races ON raceIn.raceID = races.raceID WHERE races.season = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s| %20s\n", "Driver ID", "First Name", "Last Name",
                    "Driver Nationality");
            System.out.println("-".repeat(76));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int driverID = resultSet.getInt("driverID");
                String first = resultSet.getString("firstName");
                String last = resultSet.getString("lastName");
                String nationality = resultSet.getString("nationality");
                System.out.printf("%-10d| %-20s| %-20s| %20s\n", driverID, first, last, nationality);
                returned = true;
            }
            if (!returned) {
                System.out.println("Please enter a valid year.");
            }
            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void cons(String arg) {
        String[] parts = arg.trim().split(" ");
        String sql = "SELECT DISTINCT constructors.constructorID, CAST(constructors.constructorName AS NVARCHAR(MAX)) AS name, CAST(constructors.constructorNationality AS NVARCHAR(MAX)) AS nationality FROM constructors INNER JOIN partakeIn ON constructors.constructorID = partakeIn.constructorID INNER JOIN races ON partakeIn.raceID = races.raceID WHERE races.season = ?; ";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-15s| %-20s| %-25s\n", "Constructor ID", "Constructor Name", "Constructor Nationality");
            System.out.println("-".repeat(62));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int constructorID = resultSet.getInt("constructorID");
                String constructorName = resultSet.getString("name");
                String constructorNationality = resultSet.getString("nationality");
                System.out.printf("%-15d| %-20s| %-25s\n", constructorID, constructorName, constructorNationality);
                returned = true;
            }
            if (!returned) {
                System.out.println("Please enter a valid year.");
            }
            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void dChampAfter(String arg) {
        String[] parts = arg.trim().split(" ");
        String sql = "SELECT driverStandings.driverID, drivers.driverFirstName, drivers.driverLastName, driverStandings.totalPoints, driverStandings.wins FROM driverStandings INNER JOIN drivers on driverStandings.driverID = drivers.driverID WHERE driverStandings.raceID = ? ORDER BY driverStandings.totalPoints DESC;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s| %-15s| %-10s\n", "Driver ID", "First Name", "Last Name",
                    "Total Points", "Num Wins");
            System.out.println("-".repeat(81));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int driverID = resultSet.getInt("driverID");
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                int totalPoints = resultSet.getInt("totalPoints");
                int numWins = resultSet.getInt("wins");
                System.out.printf("%-10d| %-20s| %-20s| %-15d| %-10d\n", driverID, first, last, totalPoints, numWins);
                returned = true;
            }
            if (!returned) {
                System.out.println("No standings for that race ID or invalid race ID.");
            }
            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void conChampAfter(String arg) {
        String[] parts = arg.trim().split(" ");
        String sql = "SELECT constructorStandings.constructorID, constructors.constructorName, constructorStandings.totalPoints, constructorStandings.wins FROM constructorStandings INNER JOIN constructors ON constructorStandings.constructorID = constructors.constructorID WHERE constructorStandings.raceID = ? ORDER BY constructorStandings.totalPoints DESC;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-15s| %-20s| %-15s| %-10s\n", "Constructor ID", "Constructor Name", "Total Points",
                    "Num Wins");
            System.out.println("-".repeat(64));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int constructorID = resultSet.getInt("constructorID");
                String constructorName = resultSet.getString("constructorName");
                int totalPoints = resultSet.getInt("totalPoints");
                int numWins = resultSet.getInt("wins");
                System.out.printf("%-15d| %-20s| %-15d| %-10d\n", constructorID, constructorName, totalPoints, numWins);
                returned = true;
            }
            if (!returned) {
                System.out.println("No standings for that race ID or invalid race ID.");
            }
            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // need to account for nulls?
    public void quali(String arg) {
        String[] parts = arg.trim().split(" ");
        String sql = "SELECT drivers.driverID, drivers.driverFirstName, drivers.driverLastName, constructors.constructorID, constructors.constructorName, results.finalPos, results.carNum, qualifyingResults.q1Time, qualifyingResults.q2Time, qualifyingResults.q3Time FROM qualifyingResults INNER JOIN results ON qualifyingResults.resultID = results.resultID INNER JOIN drivers ON results.driverID = drivers.driverID INNER JOIN constructors ON results.constructorID = constructors.constructorID WHERE results.raceID = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s| %-15s| %-20s| %-10s| %-10s| %-10s| %-10s| %-10s\n", "Driver ID",
                    "First Name", "Last Name", "Constructor ID", "Constructor Name", "Final Pos", "Q1 Time", "Q2 Time",
                    "Q3 Time", "Car Num");
            System.out.println("-".repeat(150));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int driverID = resultSet.getInt("driverID");
                String driverFirst = resultSet.getString("driverFirstName");
                String driverLast = resultSet.getString("driverLastName");
                int constructorID = resultSet.getInt("constructorID");
                String constructorName = resultSet.getString("constructorName");
                int finalPos = resultSet.getInt("finalPos");
                Time q1 = resultSet.getTime("q1Time");
                Time q2 = resultSet.getTime("q2Time");
                Time q3 = resultSet.getTime("q3Time");
                int carNum = resultSet.getInt("carNum");
                System.out.printf("%-10s| %-20s| %-20s| %-15s| %-20s| %-10s| %-10s| %-10s| %-10s| %-10d\n", driverID,
                        driverFirst, driverLast, constructorID, constructorName, finalPos, q1, q2, q3, carNum);
                returned = true;
            }
            if (!returned) {
                System.out.println("No qualifying results for that race ID and race type.");
            }
            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // need to account for nulls?
    public void races(String arg) {
        String[] parts = arg.trim().split(" ");
        String sql = "SELECT drivers.driverID, drivers.driverFirstName, drivers.driverLastName, constructors.constructorID, constructors.constructorName, results.finalPos, results.carNum, raceResults.startPos, raceResults.numPoints FROM raceResults INNER JOIN results ON raceResults.resultID = results.resultID INNER JOIN drivers ON results.driverID = drivers.driverID INNER JOIN constructors ON results.constructorID = constructors.constructorID WHERE results.raceID = ? AND CONVERT(VARCHAR, raceResults.raceType) = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            statement.setString(2, parts[1]);
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s| %-15s| %-20s| %-10s| %-10s| %-11s| %-10s\n", "Driver ID",
                    "First Name", "Last Name", "Constructor ID", "Constructor Name", "Start Pos", "Final Pos",
                    "Num Points", "Car Num");
            System.out.println("-".repeat(139));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int driverID = resultSet.getInt("driverID");
                String driverFirst = resultSet.getString("driverFirstName");
                String driverLast = resultSet.getString("driverLastName");
                int constructorID = resultSet.getInt("constructorID");
                String constructorName = resultSet.getString("constructorName");
                int startPos = resultSet.getInt("startPos");
                int finalPos = resultSet.getInt("finalPos");
                int numPoints = resultSet.getInt("numPoints");
                int carNum = resultSet.getInt("carNum");
                System.out.printf("%-10d| %-20s| %-20s| %-15d| %-20s| %-10d| %-10d| %-11d| %-10d\n", driverID,
                        driverFirst, driverLast, constructorID, constructorName, startPos, finalPos, numPoints, carNum);
                returned = true;
            }
            if (!returned) {
                System.out.println("No results for that race ID and race type.");
            }
            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void driversCon(String arg) {
        String[] parts = arg.trim().split(" ");
        // String sql = "SELECT constructors.constructorName, constructors.constructorID FROM constructors INNER JOIN raceFor ON constructors.constructorID = raceFor.constructorID INNER JOIN drivers ON drivers.driverID = raceFor.driverID WHERE CONVERT(VARCHAR, drivers.driverFirstName) = ? AND CONVERT(VARCHAR, drivers.driverLastName) = ?; ";
        String sql = "SELECT constructors.constructorName, constructors.constructorID FROM constructors INNER JOIN raceFor ON constructors.constructorID = raceFor.constructorID INNER JOIN drivers ON drivers.driverID = raceFor.driverID WHERE drivers.driverID = ?; ";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            // statement.setString(2, parts[1]);
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-15s| %-20s\n", "Constructor ID", "Constructor Name");
            System.out.println("-".repeat(32));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int id = resultSet.getInt("constructorID");
                String name = resultSet.getString("constructorName");
                System.out.printf("%-15d| %-20s\n", id, name);
                returned = true;
            }
            if (!returned) {
                System.out.println("No drivers match that name.");
            }

            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void consDrivers(String arg) {
        String[] parts = arg.trim().split(" ");
        // String sql = "SELECT drivers.driverFirstName, drivers.driverLastName, drivers.driverID FROM constructors INNER JOIN raceFor ON constructors.constructorID = raceFor.constructorID INNER JOIN drivers ON drivers.driverID = raceFor.driverID WHERE CONVERT(VARCHAR, constructors.constructorName) = ?;";
        String sql = "SELECT drivers.driverFirstName, drivers.driverLastName, drivers.driverID FROM constructors INNER JOIN raceFor ON constructors.constructorID = raceFor.constructorID INNER JOIN drivers ON drivers.driverID = raceFor.driverID WHERE constructors.constructorID = ?;";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(parts[0]));
            // statement.setString(1, parts[0]);
            ResultSet resultSet = statement.executeQuery();
            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s\n", "Driver ID", "First Name", "Last Name");
            System.out.println("-".repeat(54));

            // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                int id = resultSet.getInt("driverID");
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                System.out.printf("%-10d| %-20s| %-20s\n", id, first, last);
                returned = true;
            }
            if (!returned) {
                System.out.println("No constructors match that name.");
            }

            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void circuitDriverWins(String arg) {
        if (isPosNumeric(arg)) {
            String sql = "WITH gpResults AS ( \n" + //
                    "\n" + //
                    "SELECT raceResults.resultID  \n" + //
                    "\n" + //
                    "FROM raceResults  \n" + //
                    "\n" + //
                    "WHERE CAST(raceType AS VARCHAR(255)) = 'GP' \n" + //
                    "\n" + //
                    "), \n" + //
                    "\n" + //
                    "gpRaces as ( \n" + //
                    "\n" + //
                    "select driverID, raceID, finalPos from results \n" + //
                    "\n" + //
                    "where resultID in (select resultID from gpResults) \n" + //
                    "\n" + //
                    "), \n" + //
                    "\n" + //
                    "driversWinsAtCircuit as ( \n" + //
                    "\n" + //
                    "select driverID, COUNT(CASE WHEN gpRaces.finalPos = 1 THEN 1 END) as numWins from gpRaces \n" + //
                    "\n" + //
                    "join races on gpRaces.raceID = races.raceID \n" + //
                    "\n" + //
                    "join circuits on races.circuitID = circuits.circuitID \n" + //
                    "\n" + //
                    "where circuits.circuitID = ? \n" + //
                    "\n" + //
                    "GROUP BY driverID \n" + //
                    "\n" + //
                    "having COUNT(CASE WHEN gpRaces.finalPos = 1 THEN 1 END) >= 1 \n" + //
                    "\n" + //
                    ") \n" + //
                    "\n" + //
                    "select drivers.driverID, driverFirstName, driverLastName, numWins from drivers \n" + //
                    "\n" + //
                    "join driversWinsAtCircuit on drivers.driverID = driversWinsAtCircuit.driverID \n" + //
                    "\n" + //
                    "order by numWins desc; ";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, arg);
                ResultSet resultSet = statement.executeQuery();

                System.out.println();
                System.out.printf("%-10s| %-20s| %-20s| %-10s\n", "driverID", "First Name", "Last Name", "Num Wins");
                System.out.println("-".repeat(65));

                // // To determine if an error message should be printed
                boolean returned = false;
                while (resultSet.next()) {
                    String id = resultSet.getString("driverID");
                    String first = resultSet.getString("driverFirstName");
                    String last = resultSet.getString("driverLastName");
                    String wins = resultSet.getString("numWins");
                    System.out.printf("%-10s| %-20s| %-20s| %-10s\n", id, first, last, wins);
                    returned = true;
                }
                if (!returned) {
                    System.out.println("No circuitID's match the input ID.");
                }

                System.out.println();
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        } else {
            System.out.println("Argument must be a circuitID");
        }
    }

    public void circuitConsWins(String arg) {
        if (isPosNumeric(arg)) {
            String sql = "WITH gpResults AS ( \n" + //
                    "\n" + //
                    "SELECT raceResults.resultID  \n" + //
                    "\n" + //
                    "FROM raceResults  \n" + //
                    "\n" + //
                    "WHERE CAST(raceType AS VARCHAR(255)) = 'GP' \n" + //
                    "\n" + //
                    "), \n" + //
                    "\n" + //
                    "gpRaces as ( \n" + //
                    "\n" + //
                    "select constructorID, raceID, finalPos from results \n" + //
                    "\n" + //
                    "where resultID in (select resultID from gpResults) \n" + //
                    "\n" + //
                    "), \n" + //
                    "\n" + //
                    "constructorsWinsAtCircuit as ( \n" + //
                    "\n" + //
                    "select constructorID, COUNT(CASE WHEN gpRaces.finalPos = 1 THEN 1 END) as numWins from gpRaces \n"
                    + //
                    "\n" + //
                    "join races on gpRaces.raceID = races.raceID \n" + //
                    "\n" + //
                    "join circuits on races.circuitID = circuits.circuitID \n" + //
                    "\n" + //
                    "where circuits.circuitID = ? \n" + //
                    "\n" + //
                    "GROUP BY constructorID \n" + //
                    "\n" + //
                    "having COUNT(CASE WHEN gpRaces.finalPos = 1 THEN 1 END) >= 1 \n" + //
                    "\n" + //
                    ") \n" + //
                    "\n" + //
                    "select constructors.constructorID, constructorName, numWins from constructors \n" + //
                    "\n" + //
                    "join constructorsWinsAtCircuit on constructors.constructorID = constructorsWinsAtCircuit.constructorID \n"
                    + //
                    "\n" + //
                    "order by numWins desc;";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, arg);
                ResultSet resultSet = statement.executeQuery();

                System.out.println();
                System.out.printf("%-15s| %-20s| %-10s\n", "constructorID", "Constructor", "Num Wins");
                System.out.println("-".repeat(49));

                // // To determine if an error message should be printed
                boolean returned = false;
                while (resultSet.next()) {
                    String id = resultSet.getString("constructorID");
                    String name = resultSet.getString("constructorName");
                    String wins = resultSet.getString("numWins");
                    System.out.printf("%-15s| %-20s| %-10s\n", id, name, wins);
                    returned = true;
                }
                if (!returned) {
                    System.out.println("No circuitID's match the input ID.");
                }

                System.out.println();
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        } else {
            System.out.println("Argument must be a circuitID");
        }
    }

    public void posToWin() {
        String sql = "WITH gpResults AS ( \n" + //
                "\n" + //
                "    SELECT raceResults.resultID  \n" + //
                "\n" + //
                "    FROM raceResults  \n" + //
                "\n" + //
                "    WHERE CAST(raceType AS VARCHAR(255)) = 'GP' \n" + //
                "\n" + //
                ") \n" + //
                "\n" + //
                "SELECT raceResults.startPos, \n" + //
                "\n" + //
                "(COUNT(CASE WHEN results.finalPos = 1 THEN 1 END) * 1.0 / COUNT(*)) * 100 AS avgConversionRate \n" + //
                "\n" + //
                "FROM results \n" + //
                "\n" + //
                "JOIN raceResults ON results.resultID = raceResults.resultID \n" + //
                "\n" + //
                "WHERE raceResults.resultID IN (SELECT resultID FROM gpResults) \n" + //
                "\n" + //
                "and startPos != 0 \n" + //
                "\n" + //
                "GROUP BY raceResults.startPos \n" + //
                "\n" + //
                "order by startPos; ";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println();
            System.out.printf("%-18s| %-17s\n", "Starting Position", "Win Rate (%)");
            System.out.println("-".repeat(33));

            // // To determine if an error message should be printed
            while (resultSet.next()) {
                String start = resultSet.getString("startPos");
                Float winRate = resultSet.getFloat("avgConversionRate");
                System.out.printf("%-18s| %-17.2f\n", start, winRate);
            }

            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void currDrivers() {
        String sql = "WITH currentDrivers AS ( \n" + //
                "\n" + //
                "    \tSELECT DISTINCT drivers.driverID FROM drivers  \n" + //
                "\n" + //
                "    \tINNER JOIN raceIn ON drivers.driverID = raceIn.driverID  \n" + //
                "\n" + //
                "    \tINNER JOIN races ON raceIn.raceID = races.raceID \n" + //
                "\n" + //
                "    \tWHERE races.season IN (SELECT MAX(races.season) FROM races) \n" + //
                "\n" + //
                ") \n" + //
                "\n" + //
                "SELECT drivers.driverID, drivers.driverFirstName, drivers.driverLastName \n" + //
                "\n" + //
                "FROM drivers  \n" + //
                "\n" + //
                "INNER JOIN currentDrivers ON drivers.driverID = currentDrivers.driverID order by CAST(driverLastName as NVARCHAR(MAX));";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s\n", "driverID", "First Name", "Last Name");
            System.out.println("-".repeat(55));

            // // To determine if an error message should be printed
            while (resultSet.next()) {
                String id = resultSet.getString("driverID");
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                System.out.printf("%-10s| %-20s| %-20s\n", id, first, last);
            }

            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void currCons() {
        String sql = "WITH currentConstructors AS ( \n" + //
                "\n" + //
                "    \tSELECT DISTINCT constructors.constructorID FROM constructors INNER JOIN  \n" + //
                "\n" + //
                "partakeIn ON constructors.constructorID = partakeIn.constructorID  \n" + //
                "\n" + //
                "    \tINNER JOIN races ON partakeIn.raceID = races.raceID \n" + //
                "\n" + //
                "    \tWHERE races.season IN (SELECT MAX(races.season) FROM races) \n" + //
                "\n" + //
                ") \n" + //
                "\n" + //
                "SELECT constructors.constructorID, constructors.constructorName FROM constructors  \n" + //
                "\n" + //
                "INNER JOIN currentConstructors ON constructors.constructorID = currentConstructors.constructorID order by CAST(constructorName as NVARCHAR(MAX)); ";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println();
            System.out.printf("%-14s| %-20s\n", "constructorID", "Constructor");
            System.out.println("-".repeat(35));

            // // To determine if an error message should be printed
            while (resultSet.next()) {
                String id = resultSet.getString("constructorID");
                String name = resultSet.getString("constructorName");
                System.out.printf("%-14s| %-20s\n", id, name);
            }

            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void results(String arg) {
        if (isPosNumeric(arg)) {
            String sql = "SELECT races.raceID, races.raceName, races.raceDate,  \n" + //
                    "\n" + //
                    "races.raceNum, races.circuitID \n" + //
                    "\n" + //
                    "FROM races \n" + //
                    "\n" + //
                    "WHERE races.season = ? order by raceDate;";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, arg);
                ResultSet resultSet = statement.executeQuery();

                System.out.println();
                System.out.printf("%-7s| %-30s| %-11s\n", "raceID", "Race Name", "Race Date");
                System.out.println("-".repeat(53));

                // // To determine if an error message should be printed
                boolean returned = false;
                while (resultSet.next()) {
                    String id = resultSet.getString("raceID");
                    String name = resultSet.getString("raceName");
                    String date = resultSet.getString("raceDate");
                    System.out.printf("%-7s| %-30s| %-11s\n", id, name, date);
                    returned = true;
                }
                if (!returned) {
                    System.out.println("No raceID's match the input year.");
                }

                System.out.println();
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        } else {
            System.out.println("Argument must be a year");
        }
    }

    public void dFrom(String arg) {
        String sql = "SELECT drivers.driverFirstName, drivers.driverLastName, drivers.driverID \n" + //
                "\n" + //
                "FROM drivers  \n" + //
                "\n" + //
                "WHERE CONVERT(VARCHAR, drivers.driverNationality) = ? \n" + //
                "\n" + //
                "ORDER BY CAST(driverLastName as NVARCHAR(MAX)); ";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, arg);
            ResultSet resultSet = statement.executeQuery();

            System.out.println();
            System.out.printf("%-10s| %-20s| %-20s\n", "driverID", "First Name", "Last Name");
            System.out.println("-".repeat(53));

            // // To determine if an error message should be printed
            boolean returned = false;
            while (resultSet.next()) {
                String id = resultSet.getString("driverID");
                String first = resultSet.getString("driverFirstName");
                String last = resultSet.getString("driverLastName");
                System.out.printf("%-10s| %-20s| %-20s\n", id, first, last);
                returned = true;
            }
            if (!returned) {
                System.out.println("No driver's exist for the given nationality");
            }

            System.out.println();
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void mostGained(String arg) {
       
        if (arg == "" || isPosNumeric(arg)) {
            try {
                String sql = "";
                if (arg == "") {
                    sql = "SELECT TOP 10 raceResults.startPos - results.finalPos AS positionsGained, drivers.driverID, drivers.driverFirstName, drivers.driverLastName, races.raceName, races.season \n"
                            + //
                            "\n" + //
                            "FROM raceResults  \n" + //
                            "\n" + //
                            "INNER JOIN results ON raceResults.resultID = results.resultID  \n" + //
                            "\n" + //
                            "INNER JOIN drivers ON results.driverID = drivers.driverID  \n" + //
                            "\n" + //
                            "INNER JOIN races ON races.raceID = results.raceID \n" + //
                            "\n" + //
                            "WHERE CONVERT(VARCHAR, raceResults.raceType) = 'GP' \n" + //
                            "\n" + //
                            "ORDER BY positionsGained DESC; ";
                } else {
                    sql = "SELECT TOP " + Integer.parseInt(arg) + " raceResults.startPos - results.finalPos AS positionsGained, drivers.driverID, drivers.driverFirstName, drivers.driverLastName, races.raceName, races.season \n"
                            + //
                            "\n" + //
                            "FROM raceResults  \n" + //
                            "\n" + //
                            "INNER JOIN results ON raceResults.resultID = results.resultID  \n" + //
                            "\n" + //
                            "INNER JOIN drivers ON results.driverID = drivers.driverID  \n" + //
                            "\n" + //
                            "INNER JOIN races ON races.raceID = results.raceID \n" + //
                            "\n" + //
                            "WHERE CONVERT(VARCHAR, raceResults.raceType) = 'GP' \n" + //
                            "\n" + //
                            "ORDER BY positionsGained DESC; ";
                }

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);

                System.out.println();
                System.out.printf("%-11s| %-9s| %-19s| %-19s| %-30s| %-10s\n", "Pos Gained", "driverID",
                        "First Name", "Last Name", "Race", "Season");
                System.out.println("-".repeat(106));

                // // To determine if an error message should be printed
                boolean returned = false;
                while (resultSet.next()) {
                    String posG = resultSet.getString("positionsGained");
                    String id = resultSet.getString("driverID");
                    String first = resultSet.getString("driverFirstName");
                    String last = resultSet.getString("driverLastName");
                    String race = resultSet.getString("raceName");
                    String season = resultSet.getString("season");
                    System.out.printf("%-11s| %-9s| %-19s| %-19s| %-30s| %-10s\n", posG, id, first, last, race,
                            season);

                    returned = true;
                }
                if (!returned) {
                    System.out.println("0 entered so no results output");
                }

                System.out.println();
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        } else {
            System.out.println("Argument must be a positive integer");
        }

    }

    public void driverCircuitWinRate(String args) {
        String[] parts = args.trim().split(" ");
        if (isPosNumeric(parts[0]) && isPosNumeric(parts[1])) {
            String sql = "WITH circuitsWonAt AS ( \n" + //
                    "\n" + //
                    "    \tSELECT drivers.driverID, COUNT(DISTINCT circuits.circuitID) AS numWins \n" + //
                    "\n" + //
                    "    \tFROM drivers \n" + //
                    "\n" + //
                    "    \tINNER JOIN results ON drivers.driverID = results.driverID  \n" + //
                    "\n" + //
                    "    \tINNER JOIN raceResults ON raceResults.resultID = results.resultID  \n" + //
                    "\n" + //
                    "    INNER JOIN races ON results.raceID = races.raceID  \n" + //
                    "\n" + //
                    "    INNER JOIN circuits ON races.circuitID = circuits.circuitID \n" + //
                    "\n" + //
                    "    WHERE CONVERT(VARCHAR, raceResults.raceType) = 'GP' AND results.finalPos = 1 \n" + //
                    "\n" + //
                    "    GROUP BY drivers.driverID \n" + //
                    "\n" + //
                    "),  \n" + //
                    "\n" + //
                    "circuitsDrivenAt AS ( \n" + //
                    "\n" + //
                    "    \tSELECT drivers.driverID,  \n" + //
                    "\n" + //
                    "COUNT(DISTINCT circuits.circuitID) AS numCircuits FROM drivers  \n" + //
                    "\n" + //
                    "    \tINNER JOIN raceIN ON drivers.driverID = raceIn.driverID \n" + //
                    "\n" + //
                    "    \tINNER JOIN races ON raceIn.raceID = races.raceID \n" + //
                    "\n" + //
                    "    \tINNER JOIN circuits ON races.circuitID = circuits.circuitID \n" + //
                    "\n" + //
                    "    \tGROUP BY drivers.driverID \n" + //
                    "\n" + //
                    "    \tHAVING COUNT(DISTINCT circuits.circuitID) > ? \n" + //
                    "\n" + //
                    ") \n" + //
                    "\n" + //
                    "SELECT TOP " + Integer.parseInt(parts[0])
                    + " drivers.driverID, drivers.driverFirstName, drivers.driverLastName, " +
                    "ROUND(CAST(circuitsWonAt.numWins AS float) / CAST(circuitsDrivenAt.numCircuits AS float) * 100, 2) AS percentageOfCircuitsWonAt \n"
                    + //
                    "\n" + //
                    "FROM drivers  \n" + //
                    "\n" + //
                    "INNER JOIN circuitsWonAt ON drivers.driverID = circuitsWonAt.driverID \n" + //
                    "\n" + //
                    "INNER JOIN circuitsDrivenAt ON drivers.driverID = circuitsDrivenAt.driverID \n" + //
                    "\n" + //
                    "ORDER BY percentageOfCircuitsWonAt DESC;  ";

            try {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, parts[1]);
                ResultSet resultSet = statement.executeQuery();

                System.out.println();
                System.out.printf("%-10s| %-20s| %-20s| %-20s\n", "driverID", "First Name", "Last Name", "Circuits Won At (%)");
                System.out.println("-".repeat(77));

                // // To determine if an error message should be printed
                boolean returned = false;
                while (resultSet.next()) {
                    String id = resultSet.getString("driverID");
                    String first = resultSet.getString("driverFirstName");
                    String last = resultSet.getString("driverLastName");
                    String wins = resultSet.getString("percentageOfCircuitsWonAt");
                    System.out.printf("%-10s| %-20s| %-20s| %-20s\n", id, first, last, wins);
                    returned = true;
                }
                if (!returned) {
                    System.out.println("No driver's exist for the given nationality");
                }

                System.out.println();
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
        } else {
            System.out.println("Arguments must be positive integers");
        }
    }

    public void deleteData(String args) {
        try {
            String[] tableNames = {"raceResults", "qualifyingResults", "results", "constructorStandings", "driverStandings", "partakeIn", "raceIn", "races", "raceFor", "constructors", "drivers", "circuits"};

            for(int i = 0; i < tableNames.length; i++) {
                String sql = "delete from ?";
                PreparedStatement deleteStatement = connection.prepareStatement(sql);
                deleteStatement.setString(1, tableNames[i]);
                deleteStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    public void repopulate(String args) {
        try {
            Scanner insertFile = new Scanner(new File("AllInserts.sql"));

            while(insertFile.hasNextLine()) {
                String line = insertFile.nextLine();
                if(!line.equals("")) {
                    PreparedStatement insert = connection.prepareStatement(line);
                    insert.execute();
                }
            }

            insertFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.out);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

    // helper function
    private static boolean isPosNumeric(String str) {
        try {
            int i = Integer.parseInt(str);
            if (i >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
