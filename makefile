
build: SQLServer.class

SQLServer.class: SQLServer.java MyUI.java
	javac SQLServer.java MyUI.java

run: SQLServer.class
	java -cp .;mssql-jdbc-11.2.0.jre11.jar SQLServer

clean:
	rm SQLServer.class MyUI.class
