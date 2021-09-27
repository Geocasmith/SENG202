# SENG202 Project Team 10 - Insight

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
2. Navigate to the "Runnable Program" folder inside the newly extracted folder
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




