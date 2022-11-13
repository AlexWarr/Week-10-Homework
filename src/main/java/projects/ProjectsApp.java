package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// @formatter:off
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List Projects",
			"3) Select a project"
			);
	// @formatter:on
	
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject = new Project();
	
	
	
	public static void main(String[] args) {

		new ProjectsApp().proccessUserSelections();
		

	}


	private void proccessUserSelections() {
		boolean done = false;
		
		while(!done) {
			try{
				int selection = getUserSelection();
				switch (selection) {
				case -1:
					done = exitMenu();
					break;
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch(Exception e) {
				System.out.println("\nError: " + e + " Try again");
				proccessUserSelections();
			}
		}
		
	}


	private void selectProject() {
		listProjects();
		Integer pId = getIntInput("Enter a project ID to select a project");
		
		/* clears current project*/
		curProject = null;
		
		/* will throw exception if incorrect option selected */
		curProject = projectService.fetchProjectById(pId);
		
		if (curProject == null){
			System.out.println("Invalid project ID selected.");
		}
		
	}


	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() +  ": " + project.getProjectName()));	
	}


	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbproject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbproject);
		
		
		
	}


	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
		} 
			try{
				return new BigDecimal(input).setScale(2);
			} catch(NumberFormatException e){
				throw new DbException(input + " is not a valid decimal number.");
			}
		
	}


	private boolean exitMenu() {
		System.out.println("Exiting the menu");
		return true;
	}


	private int getUserSelection() {
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		
		return Objects.isNull(input) ? -1 : input;
		
	}


	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) {
			return null;
		}
			try{
				return Integer.valueOf(input);
			} catch(NumberFormatException e){
				throw new DbException(input + " is not a valid number.");
			}
		
	}


	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}


	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit.");
		operations.forEach(line -> System.out.println(" " + line));
		if (curProject == null || curProject.getProjectId() == null) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

}
