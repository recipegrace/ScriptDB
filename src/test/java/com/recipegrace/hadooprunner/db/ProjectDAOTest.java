package com.recipegrace.hadooprunner.db;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Project;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjacob on 4/11/15.
 */
public class ProjectDAOTest extends AbstractDAOTest {

    @Test
    public void testCreateProject() throws IOException, HadoopRunnerException {
        ProjectDAO rgDAO = new ProjectDAO();
        rgDAO.createProject("projectName", "projectLocation", "JarName");
        Project project = rgDAO.getProject("projectName");
        assertEquals(project.getProjectLocation(), "projectLocation");
        assertEquals(rgDAO.getAll().size(), 1);
    }

    @Test
    public void testCreateProject2() throws IOException, HadoopRunnerException {
        ProjectDAO rgDAO = new ProjectDAO();
        Project project = new Project();
        project.setJarName("JarName");
        project.setProjectLocation("projectLocation");
        project.setProjectName("projectName");
        rgDAO.createProject(project);
        assertEquals(project.getProjectLocation(), "projectLocation");
        assertEquals(rgDAO.getAll().size(), 1);
    }


    @Test(expected = HadoopRunnerException.class)
    public void testSameCreateProject() throws IOException, HadoopRunnerException {
        ProjectDAO rgDAO = new ProjectDAO();
        rgDAO.createProject("projectName", "projectLocation", "JarName");
        rgDAO.createProject("projectName", "projectLocation", "JarName");
    }

    @Test
    public void testSaveProject() throws IOException, HadoopRunnerException {
        ProjectDAO rgDAO = new ProjectDAO();
        rgDAO.createProject("projectName", "projectLocation", "JarName");
        Project project = new Project();
        project.setProjectName("projectName");
        project.setProjectLocation("projectLocation1");
        project.setJarName("JarName1");
        rgDAO.saveProject(project);
        Project result = rgDAO.getProject("projectName");
        assertEquals(result.getProjectLocation(), "projectLocation1");
        assertEquals(result.getJarName(), "JarName1");

    }
}
