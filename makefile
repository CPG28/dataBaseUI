
build: SQLServer.class

SQLServer.class: SQLServer.java MyDatabase.java
	javac SQLServer.java MyDatabase.java

run: SQLServer.class
	java -cp .:mssql-jdbc-11.2.0.jre11.jar SQLServer

clean:
	rm SQLServer.class MyDatabase.class
