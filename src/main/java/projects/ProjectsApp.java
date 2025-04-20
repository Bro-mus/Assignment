package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	//@formatter:off
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List projects",
		"3) Select a project",
		"4) Update project details",
		"5) Delete a project"
	);
	//@formatter:on
	
	public static void main(String[] args) {
		new ProjectsApp().processUserSelection();
	}	
	
	private void processUserSelection() {
		
		// displays menu selections, gets a selection from the user, acts on the selection
		
		boolean done = false;
		while(!done) {
			try	{
				int selection = getUserSelection();
				// Add a switch statement below the method call to getUserSelection(). Create a switch statement to switch on the value in the local variable selection.
				switch(selection) {
				// Add the first case of -1. Inside this case, call exitMenu() and assign the result of the method call to the local variable done. Make sure to add the break statement.
					case -1:
						done = exitMenu();
						break;

					case 1:
						createProject(); //menu option 1 create project
						break;
						
					case 2: 
						listProjects(); //menu option 2, list projects
						break;
						
					case 3:
						selectProject();//menu option 3 Select project
						break;
						
					case 4:
						updateProjectDetails();
						break;
			
					case 5:
						deleteProject();
						break;						

				// Add the default case. Print a message: "\n" + selection + " is not a valid selection. Try again.".
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				}
				
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
				e.printStackTrace();
			}
		}
	}
	
	
	private void deleteProject() {
		listProjects(); //Call method listProjects().
		
		Integer projectId = getIntInput("Enter the ID of the project to delete:"); //Ask the user to enter the ID of the project to delete.
		
		projectService.deleteProject(projectId);
		
		System.out.println("Project " + projectId + " was deleted successfully."); // Print a message stating that the project was deleted. (If it wasn't deleted, an exception is thrown by the service class.)

		//Add a check to see if the project ID in the current project is the same as the ID entered by the user. If so, set the value of curProject to null.
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) { 
			curProject = null;
		}
	}

	private void updateProjectDetails() {
		//check to see if curProject is null. If so,print a message  "\nPlease select a project." and return from the method.
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project:");
			return;
		}
		// For each field in the Project object, print a message along with the current setting in curProject. Here is an example:
		String projectName = getStringInput("Enter the project name: [" + curProject.getProjectName() + "]");
		
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated number of hours: [" + curProject.getEstimatedHours() + "]");
		
		BigDecimal actualHours = getDecimalInput("Enter the actual number of hours: [" + curProject.getActualHours() + "]");
		
		Integer difficulty = getIntInput("Enter the project difficulty (1-5): [" + curProject.getDifficulty() + "]");
		
		String notes = getStringInput("Enter additional project notes: [" + curProject.getNotes() + "]");
		
		//Create a new Project object. If the user input for a value is not null, add the value to the Project object. 
		//If the value is null, add the value from curProject. Repeat for all Project variables.
		Project project = new Project();
		////  Set the project ID field in the Project object to the value in the curProject object.
		project.setProjectId(curProject.getProjectId());
		
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		// Call projectService.modifyProjectDetails(). Pass the Project object as a parameter. Let Eclipse create the method for you in ProjectService.java.
		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
		//Reread the current project to pick up the changes by calling projectService.fetchProjectById(). Pass the project ID obtained from curProject.
		//Look up the latest version of curProject from the service using its current ID, and update curProject with that result
	}
		
		
	private void selectProject() {
		// Call listProjects() to print a List of Projects.
		listProjects();
		
		Integer projectId = getIntInput("Enter a project ID to select a project:");
		
		 //Collect a project ID from the user and assign it to an Integer variable named projectId. Prompt the user with "Enter a project ID to select a project".
		
		 curProject= null; //  Set the instance variable curProject to null to unselect any currently selected project.
		//Call a new method, fetchProjectById() on the projectService object. The method should take a single parameter, the project ID input by the user. 
		 curProject = projectService.fetchProjectById(projectId);
				 //Note that if an invalid project ID is entered, projectService.fetchProjectById() will throw a NoSuchElementException, which is handled by the catch block in processUserSelections().
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");
		// 
		
		projects.forEach(project -> System.out.println(" " + project.getProjectId()
				+ ": " + project.getProjectName()));
		//print the ID and name separated by ": ". Indent each line with a couple of spaces.
	}
		
	

	private void createProject() {
		//main project inputs: name, est hours, actual hours, difficulty, notes
		String projectName = getStringInput("Enter the project name:");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours:");
	    BigDecimal actualHours = getDecimalInput("Enter the actual hours:");
	    Integer difficulty = getIntInput("Enter the project difficulty (1-5):");
	    String notes = getStringInput("Enter the project notes:");
	    
	    Project project = new Project();

	    //setters, not automatic
	    project.setProjectName(projectName);
	    project.setEstimatedHours(estimatedHours);
	    project.setActualHours(actualHours);
	    project.setDifficulty(difficulty);
	    project.setNotes(notes);

	    Project dbProject = projectService.addProject(project);
	    System.out.println("You have successfully created project: " + dbProject);
	}
	

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return new BigDecimal(input).setScale(2);

		} catch (NumberFormatException e) {

			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() {
		System.out.println("Exiting the application.");
		return true;
	}

	private int getUserSelection() {
		// Takes no parameters and returns an int. This method will print the operations and then accept user input as an Integer
		printOperations();
		
		Integer input = getIntInput("Enter a menu selection");
		
		return Objects.isNull(input) ? -1 : input;
	}

	private Integer getIntInput(String prompt) {
		// Assign a local variable named input of type String to the results of the method call getStringInput(prompt).
		String input = getStringInput(prompt);
		// Test the value in the variable input. If it is null, return null. Use Objects.isNull() for the null check.
		if(Objects.isNull(input)) {
		return null;
		}
	
		// Create a try/catch block to test that the value returned by getStringInput() can be converted to an Integer.
		// The catch block should accept a parameter of type NumberFormatException.Convert the value of input, which is a String, to an Integer and return it.
		// If the conversion is not possible, a NumberFormatException is thrown. 
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
		//In the catch block throw a new DbException with the message, input + " is not a valid number. Try again."
			throw new DbException(input + " is not a valid number. Try again.");
		}
	}
	private String getStringInput(String prompt) {
		
		// Create the method that really prints the prompt and gets the input from the user.
		// It should have a single parameter of type String named prompt. This is the lowest level input method.
		// The other input methods call this method and convert the input value to the appropriate type. 
		// This will also be called by methods that need to collect String data from the user. It should return a String. 
		
		System.out.print(prompt + ": "); //to keep the cursor on the same line as the prompt. 
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim(); //Test the value of input. If it is blank return null. Otherwise return the trimmed value.
	}

	private void printOperations() {
		// prints each available selection on a separate line in the console
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");	
			operations.forEach(line -> System.out.println(" " + line));
			//Every List object must implement the forEach() method. forEach() takes a Consumer interface object as a parameter. Consumer has a single abstract method, accept(). 
			//The accept() method takes a single parameter and returns nothing. 
			//The Lambda expression has a single parameter and System.out.println returns nothing. The Lambda expression thus matches the requirements for the accept() method.
	
		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		}
		else {
			System.out.println("\nYou are currently working with project: " + curProject);
			}
	}
}