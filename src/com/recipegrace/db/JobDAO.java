package com.recipegrace.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recipegrace.core.HadoopRunnerException;
import com.recipegrace.core.Job;
import com.recipegrace.core.Project;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fjacob on 4/12/15.
 */
public class JobDAO extends AbstractDAO<Job>{

    public void createJob(String projectName, String templateName, String mainClass, Map<String, String> vmArguments, Map<String, String> programArguments) throws IOException, HadoopRunnerException {
        checkJobExist(projectName,mainClass);
        Job job = getJob(projectName, templateName, mainClass, vmArguments, programArguments);
       List<Job> jobs = getAll();
        jobs.add(job);
        saveAsJSON(jobs);

    }

    private Job getJob(String projectName, String templateName, String mainClass, Map<String, String> vmArguments, Map<String, String> programArguments) {
        Job job = new Job();
        job.setMainClassName(mainClass);
        job.setTemplateName(templateName);
        job.setProjectName(projectName);
        job.setVmArguments(vmArguments);
        job.setProgramArguments(programArguments);
        return job;
    }

    public void saveJob(String projectName, String templateName, String mainClass, Map<String, String> vmArguments, Map<String, String> programArguments) throws IOException, HadoopRunnerException {

        Job job =getJob(projectName, templateName, mainClass, vmArguments, programArguments);
        List<Job> jobs = getAll().stream()
                .filter(f-> ! (f.getProjectName().equals(projectName) && f.getMainClassName().equals(mainClass)))
                .collect(Collectors.toList());
        jobs.add(job);
        saveAsJSON(jobs);

    }
    private void checkJobExist(String projectName, String mainClass) throws FileNotFoundException, HadoopRunnerException {

        long size=
        getAll()
                .stream().filter(f -> f.getProjectName().equals(projectName) && f.getMainClassName().equals(mainClass) )
                .count();
        if(size>0) throw new HadoopRunnerException("Job already exists");

    }

    public List<Job> getJobs(String projectName) throws FileNotFoundException {

       return  getAll().stream().filter(f -> f.getProjectName().equals(projectName)).collect(Collectors.toList());

    }

    @Override
    protected String getFile() {
        return "jobs.json";
    }
    protected Type getType() {
        return new TypeToken<ArrayList<Job>>() {}.getType();
    }

    public Job getJob(String projectName, String mainClass) throws FileNotFoundException {
        Stream<Job> selectedProjects= getAll().stream().filter(f -> f.getProjectName().equals(projectName)
                && f.getMainClassName().equals(mainClass));
        return selectedProjects.findFirst().orElse(null);
    }
}
