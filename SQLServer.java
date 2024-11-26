import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class SQLServer {
    public static final String RED = "\u001B[31m";
    public static final String ORANGE = "\u001B[33m"; // Closest match (ANSI lacks true orange)
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String INDIGO = "\u001B[34m"; // Closest match (ANSI lacks indigo)
    public static final String VIOLET = "\u001B[35m";
    public static final String RESET = "\u001B[0m";

    // Connect to your database.
    public static void main(String[] args) {

        Properties prop = new Properties();
        String fileName = "auth.cfg";
        try {
            FileInputStream configFile = new FileInputStream(fileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config file.");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error reading config file.");
            System.exit(1);
        }
        String username = (prop.getProperty("username"));
        String password = (prop.getProperty("password"));

        if (username == null || password == null){
            System.out.println("Username or password not provided.");
            System.exit(1);
        }

        String connectionUrl =
                "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + username + ";"
                + "password="+ password +";"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";

        MyDatabase myDatabase = new MyDatabase(connectionUrl);
        runConsole(myDatabase);
        //print command line stuffs
    }

    public static void runConsole(MyDatabase db){
        Scanner console = new Scanner(System.in);
        clearScreen();
        //System.out.println("");//make background colour
		System.out.println("Welcome to the FORMULA ONE DATABASE");
        System.out.println();
        System.out.println(RED + ".d888                                    888         d888   \r\n" + //
                        "d88P\"                                     888        d8888   \r\n" + //
                        "888                                       888          888   \r\n" + //
                        "888888 .d88b. 888d88888888b.d88b. 888  888888 8888b.   888   \r\n" + //
                        "888   d88\"\"88b888P\"  888 \"888 \"88b888  888888    \"88b  888   \r\n" + //
                        "888   888  888888    888  888  888888  888888.d888888  888   \r\n" + //
                        "888   Y88..88P888    888  888  888Y88b 888888888  888  888   \r\n" + //
                        "888    \"Y88P\" 888    888  888  888 \"Y88888888\"Y8888888888888\u001B[0m");
        System.out.println();
        System.out.println("Type help for help\n");
		System.out.print("db > ");
		String line = console.nextLine();
		String[] parts;
		String arg = "";
        while (line != null && !line.equals("quit")) {
			parts = line.split("\\s+");
			if (line.indexOf(" ") > 0)
				arg = line.substring(line.indexOf(" ")).trim();

			if (parts[0].equals("help"))
				printHelp();
            if(parts[0].equals("clear")){
                clearScreen();
            }
            
            if(parts[0].equals("d")){
                db.driverSearch(arg);
            }
            if(parts[0].equals("circuits")){
                db.circuitsSearch(arg);
            }
            
            if(parts[0].equals("dChamp")){
                try {
                    db.dChampSearch(arg);
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            
            if(parts[0].equals("cChamp")){
                try {
                    db.cChampSearch(arg);
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            if(parts[0].equals("youngestWin")){
				try {
					if (parts.length >= 1){
					    db.youngestWin(arg);
                    }
					else{
						System.out.println("This command requires an argument (number of results)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("wins")){
                try {
					if (parts.length >= 1){
					    db.wins(arg);
                    }
					else{
						System.out.println("This command requires atleast one argument (driverID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("circuitDriverWins")){
				try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (circuitID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("circuitConsWins")){
				try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (circuitID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("posToWin")){
				try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (position)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("curDrivers")){
                clearScreen();
            }
            //TODO
            if(parts[0].equals("curCons")){
                clearScreen();
            }
            //TODO
            if(parts[0].equals("dFrom")){
				try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (nationality)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be a string");
				}
            }
            //TODO
            if(parts[0].equals("mostGained")){
                clearScreen();
            }
            //TODO
            if(parts[0].equals("driverCircuitWinRate")){
                clearScreen();
            }
            //TODO
            if(parts[0].equals("drivers")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (year)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("cons")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (year)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("dChampAfter")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (raceID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("conChampAfter")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (raceID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("quali")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (raceID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("gp")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (raceID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            //TODO
            if(parts[0].equals("driversCon")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (driverID)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
            else if(parts[0].equals("consDrivers")){
                try {
					if (parts.length >= 2){
                        db.consDrivers(arg);
                    }
					else{
						System.out.println("This command requires an argument (constructorName)");
                    }
				} catch (Exception e) {
					System.out.println("Argument must be an integer");
				}
            }
			System.out.print("db > ");
			line = console.nextLine();
		}
        quitProgram();
		console.close();
    }

    public static void quitProgram(){
        System.out.println();
        System.out.println("+--+--+--+--+\r\n" + //
                        "|  |##|  |##|\r\n" + //
                        "|  |##|  |##|\r\n" + //
                        "+--+--+--+--+\r\n" + //
                        "|##|  |##|  |\r\n" + //
                        "|##|  |##|  |\r\n" + //
                        "+--+--+--+--+\r\n" + //
                        "|  |##|  |##|\r\n" + //
                        "|  |##|  |##|\r\n" + //
                        "+--+--+--+--+\r\n" + //
                        "|##|  |##|  |\r\n" + //
                        "|##|  |##|  |\r\n" + //
                        "+--+--+--+--+\n");
        System.out.println("This program was created using data from: https://www.kaggle.com/datasets/rohanrao/formula-1-world-championship-1950-2020");
        System.out.println("Created by: Fraser Newbury, Elizabeth Stoughton, David Xavier");
        System.out.println("Created for COMP3380, at the University of Manitoba in December 2024");
        System.out.println("Thank You!");
    }

    public static void clearScreen(){
        System.out.println("\u001B[2J" + "\u001B[H");
    }

    public static void printHelp(){
        System.out.println();
        System.out.println("FORMULA ONE DATABASE COMMANDS");
        System.out.println("-------------------------------------");
        System.out.println("Commands:");
        //more commands here
        System.out.println(RED + "d name number - Search for a driver by name, and see stats about them. Inputting no name returns all drivers sorted by points. You may also enter a number to limit results\u001B[0m");
        System.out.println(ORANGE + "circuits n/s - see circuits in northern or southern hemishperes. Entering no hemisphere displays all circuits" + RESET);
        System.out.println(YELLOW + "dChamp year - See the drivers championship results from a enetered year. Entering no year returns the latest season" + RESET);
        System.out.println(GREEN + "cChamp year - See the constructors champsionship results from a entered year. Entering no year returngs the lates season" + RESET);
        System.out.println(BLUE + "youngestWin number - See the youngest race winners. Enter a number to limit the amount of results" + RESET);
        System.out.println(INDIGO + "wins dID year - List all wins that a driver has had over their entire career. Input a driver ID to see results. Enter a year to only see wins from that season" + RESET);
        System.out.println(VIOLET + "circuitDriverWins circuitID - See all drivers who have won a race at a given circuit" + RESET);
        System.out.println(RED + "circuitConsWins circuitID - See all constructors who have won a race at a given circuit" + RESET);
        System.out.println(ORANGE + "posToWin position - Statistics regarding how often a race win occurs from a starting position" + RESET);
        System.out.println(YELLOW + "curDrivers - See all current Drivers" + RESET);
        System.out.println(GREEN + "curCons - See all current Constructors" + RESET);
        System.out.println(BLUE + "dFrom nationality - See all drivers of a inputted nationality" + RESET);
        System.out.println(INDIGO + "mostGained - See statistics for most positions gained in a race");
        System.out.println(VIOLET + "driverCircuitWinRate numberDrivers numberCircuits - Out of all the circuits a driver has raced at, how many have they won at? number of drivers is for how many results, numberCircuits is for total circuits" + RESET);
        System.out.println(RED + "drivers year - See all drivers from a particular year");
        System.out.println(ORANGE + "cons year - See all constructors form a particular year");
        System.out.println(YELLOW + "races year - See all races from a particular year");
        System.out.println(GREEN + "dChampAfter raceID - See the drivers championship after a particular race");
        System.out.println(BLUE + "conChampAfter raceID - See the constructors champsionship after a particular race");
        System.out.println(INDIGO + "quali raceID - See the qualifying results for a particular race");
        System.out.println(VIOLET + "gp raceID - See the race results for a particular race");
        System.out.println(RED + "driversCon driverID - See all constructors a driver has raced for");
        System.out.println(ORANGE + "consDrivers constructorID - See all drivers a constructor has had race for them");
        System.out.println(YELLOW + "clear - Clears the screen and resets the cursor" + RESET);
        System.out.println(INDIGO + "quit - Quit the program" + RESET);
        System.out.println("");
    }
}
