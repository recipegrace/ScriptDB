package com.recipegrace.hadooprunner.core;

import java.util.Map;

/**
 * Created by fjacob on 4/12/15.
 */
public class Job {

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    private String Id;
    private String projectName;
    private String mainClassName;
    private Map<String, String> vmArguments;
    private Map<String, String> programArguments;
    private String templateName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getMainClassName() {
        return mainClassName;
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public Map<String, String> getVmArguments() {
        return vmArguments;
    }

    public void setVmArguments(Map<String, String> vmArguments) {
        this.vmArguments = vmArguments;
    }

    public Map<String, String> getProgramArguments() {
        return programArguments;
    }

    public void setProgramArguments(Map<String, String> programArguments) {
        this.programArguments = programArguments;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
