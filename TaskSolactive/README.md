TaskSolactive
====================================
 
Prerequisites
------------------------------------
Java 8
Maven
Spring Boot


Building Steps
------------------------------------ 
The source code can be cloned using [git] from GitHub:

git clone https://github.com/ramazanerecir/TickStatisticsService.git

1.	Go to TaskSolactive project directory.
2.	Run ‘mvn clean install’ to build project
3.	Jar file will be created under project\TaskSolactive\target directory.


Running TaskSolactive
------------------------------------
Configuration file path can be given as a parameter while running application.
If it is not provided, current directory will be taken as a configuration file path.

java -jar target/TaskSolactive-0.0.1-SNAPSHOT.jar

Configuration file path can have these files.
-	taskSolactive.cfg file : Configuration file includes application parameters
-	log4j.properties : Log file configuration

Application is running with default parameters, it can be reached from http://localhost:8080/
After starting application following services will be available.
1.	POST /ticks
Accepts the incoming ticks if its in sliding time interval and returns Created, otherwise returns No Content.
2.	GET /statistics 
Returns the aggregated statistics.
3.	GET /statistics/{instrument_identifier}
Returns the statistics for a given instrument.


Document
------------------------------------
Application document can be found under document directory.