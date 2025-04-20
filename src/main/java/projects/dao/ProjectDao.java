package projects.dao;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import util.DaoBase;

public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	/**
	 * Insert a project row into the project table
	 * 
	 * @param project the project object to insert
	 * @return the Project object with the primary key
	 * @throws DbException Thrown if an error occurs inserting the row
	 */

	public Project insertProject(Project project) {
		// @formatter:off
	    String sql = ""
	        + "INSERT INTO " + PROJECT_TABLE + " "
	        + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
	        + "VALUES "
	        + "(?, ?, ?, ?, ?)";
	    // @formatter:on

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();

				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);

		}
	}

	public List<Project> fetchAllProjects() {
		// Write the SQL statement to return all projects not including materials,
		// steps, or categories. Order the results by project name.
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		// Add a try-with-resource statement to obtain the Connection object.
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) { // executes the SQL statement and stores rows from db in
															// ResultSet i.e gets the data
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						Project project = new Project();

						project.setActualHours(rs.getBigDecimal("actual_hours"));
						project.setDifficulty(rs.getObject("difficulty", Integer.class));
						project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
						project.setNotes(rs.getString("notes"));
						project.setProjectId(rs.getObject("project_id", Integer.class));
						project.setProjectName(rs.getString("project_name"));
						projects.add(project);
					}

					return projects;

				}
			} catch (Exception e) {
				rollbackTransaction(conn); // Inside the try block, start a new transaction.
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		// Write the SQL statement to return all columns from the project table in the
		// row that matches the given projectId.
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		// Obtain a Connection object in a try-with-resource statement
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try {
				Project project = null; // Inside the try block, create a variable of type Project and set it to null.
				// Inside the inner try block, obtain a PreparedStatement from the Connection
				// object in a try-with-resource statement.
				// Pass the SQL statement in the method call to prepareStatement(). Add the
				// projectId method parameter as a parameter to the PreparedStatement.
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
							// Obtain a ResultSet in a try-with-resource statement.
							// If the ResultSet has a row in it (rs.next()) set the Project variable to a
							// new Project object and set all fields from values in the ResultSet.
							// You can call the extract() method for this.
						}
					}
				}
				// Below the try-with-resource statement that obtains the PreparedStatement but
				// inside the try block that manages the rollback,
				// add three method calls to obtain the list of materials, steps, and categories
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				commitTransaction(conn);

				return Optional.ofNullable(project); // Return the Project object as an Optional object using
														// Optional.ofNullable()
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "" + "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "" + "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}

				return materials;
			}

		}
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {

		String sql = "" + "SELECT c.* FROM " + CATEGORY_TABLE + " c " + " JOIN " + PROJECT_CATEGORY_TABLE
				+ " pc USING (category_id) " + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}

				return categories;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {

		String sql = "" + "UPDATE " + PROJECT_TABLE + " SET " + "project_name = ?, " + "estimated_hours = ?, "
				+ "actual_hours = ?, " + "difficulty = ?, " + "notes = ? " + " WHERE project_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);

				boolean modified = stmt.executeUpdate() == 1;
				commitTransaction(conn);

				return modified;

			} 
				catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
				}
		} 
		catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		String sql = "" +  "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
				
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {	
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;
				commitTransaction(conn); 
				
				return deleted;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
			
	
		  }
	}
}