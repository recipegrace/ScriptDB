package com.recipegrace.hadooprunner.core;

/**
 * Created by fjacob on 4/12/15.
 */
public class Project {


    private String projectLocation;
    private String projectName;
    private String jarName;

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
}
