package com.recipegrace.hadooprunner.db;

import com.google.gson.reflect.TypeToken;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Project;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fjacob on 4/11/15.
 */
public class ProjectDAO extends AbstractDAO<Project> {


    public void checkProjectExists(String projectName) throws FileNotFoundException, HadoopRunnerException {
        if (getProject(projectName) != null) throw new HadoopRunnerException("Project already exists");
    }


    public void createProject(String projectName, String projectLocation, String jarName) throws IOException, HadoopRunnerException {

        checkProjectExists(projectName);
        Project project = new Project();
        project.setJarName(jarName);
        project.setProjectLocation(projectLocation);
        project.setProjectName(projectName);
        List<Project> projects = getAll();
        projects.add(project);
        saveAsJSON(projects);

    }

    public void createProject(Project project) throws IOException, HadoopRunnerException {

        checkProjectExists(project.getProjectName());
        List<Project> projects = getAll();
        projects.add(project);
        saveAsJSON(projects);

    }


    public void saveProject(Project project) throws IOException, HadoopRunnerException {

        List<Project> projects = getAll().stream()
                .filter(f -> !f.getProjectName().equals(project.getProjectName()))
                .collect(Collectors.toList());
        projects.add(project);
        saveAsJSON(projects);

    }


    public Project getProject(String projectName) throws FileNotFoundException {

        Stream<Project> selectedProjects = getAll().stream().filter(f -> f.getProjectName().equals(projectName));
        return selectedProjects.findFirst().orElse(null);
    }

    @Override
    protected String getFile() {
        return ".db/projects.json";
    }

    protected Type getType() {
        return new TypeToken<ArrayList<Project>>() {
        }.getType();
    }
}
