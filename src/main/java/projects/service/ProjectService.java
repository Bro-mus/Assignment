package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;


public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();
	
	
public Project addProject(Project project) {
	return projectDao.insertProject(project);

	}

// This method will simply return the results of the method call to the DAO class. 
// The service class in our small application does not do very much. 
// But it allows us to properly separate concerns of input/output, business logic, and database reads and writes. 
// If you always structure your code like this it will be much easier to understand and make changes if needed.
public List<Project> fetchAllProjects() {
	return projectDao.fetchAllProjects();
	// tied to listProjects
}

public Project fetchProjectById(Integer projectId) {
	// TODO Auto-generated method stub
	return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist."));
	
}

public void modifyProjectDetails(Project project) {
	// Call projectDao.modifyProjectDetails(). Pass the Project object as a parameter. 
	// The DAO method returns a boolean that indicates whether the UPDATE operation was successful. 
	// Check the return value. If it is false, throw a DbException with a message that says the project does not exist.
	if(!projectDao.modifyProjectDetails(project)) {
		throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		
				//project.GetProjectId() = Give me the ID of this project or in this instance get the ID of this project
	}
}

public void deleteProject(Integer projectId) {
	if(!projectDao.deleteProject(projectId))  {
		throw new DbException("Project with ID=" + projectId + " does not exist.");
	}
}





}
