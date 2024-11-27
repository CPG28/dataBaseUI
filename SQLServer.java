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
                if(parts.length > 1){
                    printDetailedHelp(parts[1]);
                }
                else{
				    printHelp();
                }
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
            //TODO
            if(parts[0].equals("consDrivers")){
                try {
					if (parts.length >= 2){
					    //to do function
                    }
					else{
						System.out.println("This command requires an argument (constructorID)");
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

    public static void printDetailedHelp(String help){
        System.out.println("HELP MENU FOR COMMAND " + help);
        System.out.println();
        if(help.equalsIgnoreCase("help")){
            System.out.println("help [command]");
            System.out.println();
            System.out.println("This is the help command for the system. \nUsing it without an argument prints the help menu, all commands and a basic description of their functionality.\nUsing it with an argument in place of [command] yields the detail command menu for that command");
            System.out.println("Example:");
            System.out.println("db> help help");
            System.out.println("This command would result in the page you see before you");
            ;
            
        }
        else if(help.equalsIgnoreCase("clear")){
            System.out.println("clear");
            System.out.println();
            System.out.println("This command has the functionality of clearing the terminal screen of text. It may be useful to you after viewing a large lable, or to provide a clear screen to load a help menu or to take photographic documentation for a report. It was initially provided to allow for easier debugging of the program, but has been left here for your use.\n\n Happy clearing!");
        }
        else if(help.equalsIgnoreCase("d")){
            System.out.println("d [name] [number]\n");
            System.out.println("This command allows for a searching of every driver in the database by name. A user may choose to enter a first and last name, seperated by a space and a number to limit results. The user also has the options to omit any of these arguments. This command is especially useful for finding driverID's to be used in other commands\n");
            System.out.println("[name] - The user entered name they would like to search for. If the user wishes to search with both a first and a last name, they must be seperated by a space. A user may choose not to enter a name, and will then recieve drivers starting with the most points");
            System.out.println("[number] - This number will limit the number of outputted results that the user would see. If a user chooses to omit this, they will see every driver in the database that matches the entered name.\n");
            System.out.println("Example:");
            System.out.println("db> d lew ham 5");
            System.out.println("driverID  | First Name          | Last Name           | Point Total    | Race Count| Win Count | Pole Count\r\n" + //
                                "--------------------------------------------------------------------------------------------------------------\r\n" + //
                                "1         | Lewis               | Hamilton            | 4749           | 344       | 104       | 107        \r\n" + //
                                "356       | Jack                | Brabham             | 261            | 129       | 14        | 0\r\n" + //
                                "463       | Jay                 | Chamberlain         | 0              | 3         | 0         | 0\r\n" + //
                                "777       | Philip              | Fotheringham-Parker | 0              | 1         | 0         | 0\r\n" + //
                                "101       | David               | Brabham             | 0              | 30        | 0         | 0");
        }
        else if(help.equalsIgnoreCase("circuits")){
            System.out.println("circuits [n/s]\n");
            System.out.println("This command allows the yser to view existing circuits in the database. This may be useful for gathering circuitIDs to be used in other queries. Users may choose to enter a hemisphere of n or s, corresponding to north or south. Omitting this returns all circuits in the system.\n");
            System.out.println("[n/s] - This argument allows a user to specify if they want to view circuits in the northern or southern hemisphere. Omitting this argument will return all circuits\n");
            System.out.println("Example:");
            System.out.println("db > circuits s\r\n" + //
                                "circuitID | Circuit Name                            | Circuit Country     | Latitude            | Longitude\r\n" + //
                                "------------------------------------------------------------------------------------------------------------------------\r\n" + //
                                "1         | Albert Park Grand Prix Circuit          | Australia           | 145                 | -38\r\n" + //
                                "25        | Autódromo Juan y Oscar Gálvez           | Argentina           | -58                 | -35\r\n" + //
                                "29        | Adelaide Street Circuit                 | Australia           | 139                 | -35                 \r\n" + //
                                "56        | Prince George Circuit                   | South Africa        | 28                  | -33\r\n" + //
                                "30        | Kyalami                                 | South Africa        | 28                  | -26\r\n" + //
                                "18        | Autódromo José Carlos Pace              | Brazil              | -47                 | -24\r\n" + //
                                "36        | Autódromo Internacional Nelson Piquet   | Brazil              | -43                 | -23");

        }
        else if(help.equalsIgnoreCase("dChamp")){
            System.out.println("dChamp [year]\n");
            System.out.println("This command allows the user to view the drivers championship for a specified year. If the user chooses not to specify the year, the most recent season in the database is returned. This may also be useful for a user to view what drivers were succesful during what seasons, which may lead the user toward more interesting commands.\n");
            System.out.println("[year] - The user may choose to enter a year they would like to see the drivers championship from. Omit this argument to see the championship from the most recent season.\n");
            System.out.println("Example:");
            System.out.println("db>dChamp 2002");
            System.out.println("Place| First Name          | Last Name           | Team Name           | Points\r\n" + //
                                "----------------------------------------------------------------------------------------------------\r\n" + //
                                "1    | Michael             | Schumacher          | Ferrari             | 144       \r\n" + //
                                "2    | Rubens              | Barrichello         | Ferrari             | 77        \r\n" + //
                                "3    | Juan                | Pablo Montoya       | Williams            | 50        \r\n" + //
                                "4    | Ralf                | Schumacher          | Williams            | 42   ");
        }
        else if(help.equalsIgnoreCase("cChamp")){
            System.out.println("cChamp [year]\n");
            System.out.println("This command allows the user to view the constructors championship for a specified year. If the user chooses not to specify the year, the most recent season in the database is returned. This may also be useful for a user to view what teams were succesful during what seasons, which may lead the user toward more interesting commands.\n");
            System.out.println("[year] - The user may choose to enter a year they would like to see the constructors championship from. Omit this argument to see the championship from the most recent season.\n");
            System.out.println("Example:");
            System.out.println("db>cChamp 2002");
            System.out.println("Place| Team Name           | Points\r\n" + //
                                "----------------------------------------------------------------------------------------------------\r\n" + //
                                "1    | Ferrari             | 221       \r\n" + //
                                "2    | Williams            | 92        \r\n" + //
                                "3    | McLaren             | 65\r\n" + //
                                "4    | Renault             | 23        \r\n" + //
                                "5    | Sauber              | 11");
        }
        else if(help.equalsIgnoreCase("youngestWin")){
            System.out.println("youngestWin [number]\n");
            System.out.println("This command returns the youngest driver wins in the sports history, in order of age. The user may enter a number to limit results.Users may find this interesting as it may provide them information about the youngest drivers, and the seasons they raced in.\n");
            System.out.println("[number] - Use this argument to limit the amount of results you would like to see. Beware, omitting this will return every race win in the sports history, in ascending order of age at win.\n");
            System.out.println("Example:");
            System.out.println("db>youngestWin 5");
            System.out.println("driverID  | First Name          | Last Name           | Race Name                | Age at Win\r\n" + //
                                "----------------------------------------------------------------------------------------------------\r\n" + //
                                "830       | Max                 | Verstappen          | Spanish Grand Prix       | 19\r\n" + //
                                "830       | Max                 | Verstappen          | Malaysian Grand Prix     | 20\r\n" + //
                                "830       | Max                 | Verstappen          | Mexican Grand Prix       | 20\r\n" + //
                                "20        | Sebastian           | Vettel              | Italian Grand Prix       | 21\r\n" + //
                                "20        | Sebastian           | Vettel              | Italian Grand Prix       | 21");
        }
        else if(help.equalsIgnoreCase("wins")){
            System.out.println("wins [dID] [year]\n");
            System.out.println("This command returns a list of all the wins that a driver has had over their career. Use this to get information about specific races, and the dominance of a single driver over a season. A user must enter a driverID, but may omit the season paramter to view wins for a driver across their entire career.\n");
            System.out.println("[dID] - This is the driverID of the driver that the user would like to see all their wins of. Users may get driverIDs using the d command, or from other methods in the database. Users must input a driverID for this command.");
            System.out.println("[year] - A user may enter this to only see a drivers wins from a single season. Not entering a year argument will return wins from the drivers entire career\n");
            System.out.println("Example:");
            System.out.println("db>wins 3 2016");
            System.out.println("\r\n" + //
                                "\r\n" + //
                                "Race Name                     | Race Date      | RaceType\r\n" + //
                                "--------------------------------------------------------------------------------\r\n" + //
                                "Australian Grand Prix         | 2016-03-20     | GP\r\n" + //
                                "Bahrain Grand Prix            | 2016-04-03     | GP        \r\n" + //
                                "Chinese Grand Prix            | 2016-04-17     | GP\r\n" + //
                                "Russian Grand Prix            | 2016-05-01     | GP\r\n" + //
                                "European Grand Prix           | 2016-06-19     | GP        \r\n" + //
                                "Belgian Grand Prix            | 2016-08-28     | GP\r\n" + //
                                "Italian Grand Prix            | 2016-09-04     | GP\r\n" + //
                                "Singapore Grand Prix          | 2016-09-18     | GP        \r\n" + //
                                "Japanese Grand Prix           | 2016-10-09     | GP");
            
        }
        else if(help.equalsIgnoreCase("circuitDriverWins")){
            
        }
        else if(help.equalsIgnoreCase("circuitConsWins")){
            
        }
        else if(help.equalsIgnoreCase("posToWin")){
            
        }
        else if(help.equalsIgnoreCase("curDrivers")){
            
        }
        else if(help.equalsIgnoreCase("curCons")){
            
        }
        else if(help.equalsIgnoreCase("dFrom")){
            
        }
        else if(help.equalsIgnoreCase("mostGained")){
            
        }
        else if(help.equalsIgnoreCase("driverCircuitWinRate")){
            
        }
        else if(help.equalsIgnoreCase("drivers")){
            
        }
        else if(help.equalsIgnoreCase("dChampAfter")){
            
        }
        else if(help.equalsIgnoreCase("conChampAfter")){
            
        }
        else if(help.equalsIgnoreCase("quali")){
            
        }
        else if(help.equalsIgnoreCase("gp")){
            
        }
        else if(help.equalsIgnoreCase("driversCon")){
            
        }
        else if(help.equalsIgnoreCase("consDrivers")){
            
        }
        else{
            System.out.println("That is not a valid command");
        }
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
