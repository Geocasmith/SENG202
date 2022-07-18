# SENG202 Project Team 10 - Insight


## What is Insight?

Insight is a GUI-based crime data visualisation tool. Users will be able to import existing crime data from csv files and view it in a table. The user will also be able to modify existing data by creating, editing or deleting records. The user will be able to filter the data to view subsets of the data based on filters like date ranges, crime types, locations, or other parts of the record. The user will also be able to sort by any of the fields. The user will be able to search records by their case number.

The user will be able to perform basic analyses of the data, such as calculating the distance or time between two crimes. They will also be able to view tables of the locations with the highest crime rates, lowest crime rates, most common types of crimes and least common types of crimes. In addition to this there will be an integrated web search feature which will limit users’ searches to news or government sites. 

The data will be saved persistently so the user can access it after reopening the application. The data can be saved into different lists and the user will be able to navigate between these lists by selecting them in the file viewer. The user will also be able to export the data in the table to csv files.

Insight’s visualisation features will include mapping and graphing capabilities. Users will be able to apply the same filters to mapped data as to tabulated data. Insight will be able to create graphs about the crime data, such as the number, type, or location of crimes over time.

Insight will initially only support crime datasets from the United States of America (USA), because the sample data is from the USA, and it is the largest English-speaking country. 


## Instructions for importing project into an editor

### Intellij

1. Unzip the project .zip file to a safe location
2. Open Intellij
3. Click File -> Open
4. Navigate to the project folder you extracted in step 1
5. Click OK


### Eclipse

1. Unzip the project .zip file to a safe location
2. Open Eclipse
3. Click File -> Import
4. Select General -> Projects from Folder or Archive
5. Click Next
6. Click the Folder... button in top right
7. Navigate to project folder you extracted in step 1
8. Click Open
9. Ensure only the folder detected as a Maven project is selected to be imported
10. Click Finish

## Instructions for running application in an editor

### Eclipse

1. Import the project into Eclipse as specified above
2. Click the arrow on the right side of the run button -> Run Configurations
3. Click Java Application on the left hand side
4. Click the new configuration in the top left corner
5. Type "gui.Main" in the Main class text box
6. Click Run

### Intellij

1. Import the project into Intellij as specified above
2. Click Add Configuration in the top toolbar of the screen
3. Click the + in the top left
4. Select Application
5. Ensure Java 14 is selected in the top left dropdown box
6. If not, select 14 (2) version 14.0.2
7. Type gui.Main in the Main class text box
8. Click Apply
9.  Click OK
10. Click the run button in the top toolbar of the screen
    

## Instructions for running application from .jar file

1. Unzip the project .zip file to a safe location
2. Navigate to the newly extracted folder
3. Open the folder in Terminal or Command Prompt
4. Run the following command: `java -jar seng202_2021_team10_phase2.jar`
5. **Note**: When running the program, ensure that the .jar file is in  
the same location as the Files folder as it includes dependencies for the application

## Instructions for building program .jar file

1. Ensure Maven is installed
2. Unzip the project .zip file to a safe location
3. Open the newly extracted folder in Terminal or Command Prompt
4. Run the following command: `mvn clean package`
5. Navigate to the target folder in File Explorer
6. SENG202Project-1.0-SNAPSHOT.jar is the newly created .jar file

## App startup
When the program is first opened, there will be no data in it.  
Click the File menu in the top left to see the options for adding data  
  
The program comes with a sample crimes database and csv file in the Files folder.  
These are called crimeRecords.db and crimeRecords.csv respectively

## Warnings 
When the Java Swing elements are created by the program, a warning is outputted in the run terminal. The warning only occurs on Linux Mint and does not affect the program running. The warning is as follows: "(java:10179): Gdk-WARNING **: 17:15:57.883: XSetErrorHandler() called with a GDK error trap pushed. Don't do that."


