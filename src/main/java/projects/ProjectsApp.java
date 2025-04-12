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
	//@formatter:off
	private List<String> operations = List.of(
		"1) Add a project"
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
						createProject();
						break;

				// Add the default case. Print a message: "\n" + selection + " is not a valid selection. Try again.".
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				}
				
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}
	
	private void createProject() {
		String projectName = getStringInput("Enter the project name:");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours:");
	    BigDecimal actualHours = getDecimalInput("Enter the actual hours:");
	    Integer difficulty = getIntInput("Enter the project difficulty (1-5):");
	    String notes = getStringInput("Enter the project notes:");
	    
	    Project project = new Project();

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

	/*
		    Project project = new Project();

		    project.setProjectName(projectName);
		    project.setEstimatedHours(estimatedHours);
		    project.setActualHours(actualHours);
		    project.setDifficulty(difficulty);
		    project.setNotes(notes);

		    Project dbProject = projectService.addProject(project);
		    System.out.println("You have successfully created project: " + dbProject);
	//**
///
 */
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
		System.out.println("\nThese are the available selections. Press the  Enter key to quit:");	
			operations.forEach(line -> System.out.println(" " + line));
			//Every List object must implement the forEach() method. forEach() takes a Consumer interface object as a parameter. Consumer has a single abstract method, accept(). 
			//The accept() method takes a single parameter and returns nothing. 
			//The Lambda expression has a single parameter and System.out.println returns nothing. The Lambda expression thus matches the requirements for the accept() method.
	}
}