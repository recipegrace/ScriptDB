package com.recipegrace.hadooprunner.db;

import com.google.gson.reflect.TypeToken;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Job;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fjacob on 4/12/15.
 */
public class JobDAO extends AbstractDAO<Job> {

    public void createJob(String projectName, String templateName, String mainClass, Map<String, String> vmArguments, Map<String, String> programArguments) throws IOException, HadoopRunnerException {
        checkJobExist(projectName, mainClass);
        Job job = getJob(projectName, templateName, mainClass, vmArguments, programArguments,true);
        List<Job> jobs = getAll();
        jobs.add(job);
        saveAsJSON(jobs);

    }

    private Job getJob(String projectName, String templateName, String mainClass, Map<String, String> vmArguments, Map<String, String> programArguments,boolean isCreate) {
        Job job = new Job();
        if(isCreate)
        job.setId(System.currentTimeMillis()+"");
        job.setMainClassName(mainClass);
        job.setTemplateName(templateName);
        job.setProjectName(projectName);
        job.setVmArguments(vmArguments);
        job.setProgramArguments(programArguments);
        return job;
    }

    public void saveJob(String projectName, String templateName, String mainClass, Map<String, String> vmArguments, Map<String, String> programArguments) throws IOException, HadoopRunnerException {

        Job job = getJob(projectName, templateName, mainClass, vmArguments, programArguments,false);
        List<Job> jobs = getAll().stream()
                .filter(f -> !(f.getProjectName().equals(projectName) && f.getMainClassName().equals(mainClass)))
                .collect(Collectors.toList());
        jobs.add(job);
        saveAsJSON(jobs);

    }

    private void checkJobExist(String projectName, String mainClass) throws FileNotFoundException, HadoopRunnerException {

        long size =
                getAll()
                        .stream().filter(f -> f.getProjectName().equals(projectName) && f.getMainClassName().equals(mainClass))
                        .count();
        if (size > 0) throw new HadoopRunnerException("Job already exists");

    }

    public List<Job> getJobs(String projectName) throws IOException {

        List<Job> jobs = getAll();

        List<Job> idJobs= addIDIfRequired(jobs);
        return idJobs.stream().filter(f -> f.getProjectName().equals(projectName)).collect(Collectors.toList());

    }

    private List<Job> addIDIfRequired(List<Job> jobs) throws IOException {

        List<Job> jobsWithID = new ArrayList<>();
        if(jobs.size()>0 && jobs.get(0).getId()==null){



            for(Job job: jobs){
                job.setId(jobsWithID.size()+"");
                jobsWithID.add(job);
            }
            saveAsJSON(jobsWithID);
           return jobsWithID;
        }
        return jobs;


    }

    @Override
    protected String getFile() {
        return ".db/jobs.json";
    }

    protected Type getType() {
        return new TypeToken<ArrayList<Job>>() {
        }.getType();
    }

    public Job getJob(String jobID) throws FileNotFoundException {
        Stream<Job> selectedProjects = getAll().stream().filter(f -> f.getId().equals(jobID));
        return selectedProjects.findFirst().orElse(null);
    }
    public Job getJob(String projectName, String mainClass) throws FileNotFoundException {
        Stream<Job> selectedProjects = getAll().stream().filter(f -> f.getProjectName().equals(projectName)
                && f.getMainClassName().equals(mainClass));
        return selectedProjects.findFirst().orElse(null);
    }
}
