package com.recipegrace.hadooprunner.db;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Job;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjacob on 4/12/15.
 */
public class JobDAOTest extends AbstractDAOTest {
    @Test
    public void testCreateJob() throws IOException, HadoopRunnerException {
        JobDAO dao = new JobDAO();
        Map<String, String> vmArguments = new HashMap<String, String>();
        Job job = new Job();
        job.setMainClassName("mainClass1");
        job.setProjectName("projectName");
        Job job1 = new Job();
        job.setMainClassName("mainClass2");
        job.setProjectName("projectName");
        dao.createJob(job);
        dao.createJob(job1);
        List<Job> jobs = dao.getJobs("projectName");
        assertEquals(jobs.size(), 2);
    }

    @Test(expected = HadoopRunnerException.class)
    public void testSameCreateJob() throws IOException, HadoopRunnerException {
        JobDAO dao = new JobDAO();
        Job job = new Job();
        job.setMainClassName("mainClass1");
        job.setProjectName("projectName");
        Job job1 = new Job();
        job.setMainClassName("mainClass1");
        job.setProjectName("projectName");
        dao.createJob(job);
        dao.createJob(job1);

        dao.createJob(job);
        dao.createJob(job1);
    }

    @Test
    public void testJobDetails() throws IOException, HadoopRunnerException {
        JobDAO dao = new JobDAO();
        Job job = new Job();
        job.setMainClassName("mainClass1");
        job.setProjectName("projectName");
        Map<String,String> map = new HashMap<String,String>();
        map.put("key", "value");
        job.setVmArguments(map);
        Job job1 = dao.getJob("projectName", "mainClass1");
        assertEquals(job.getVmArguments().get("key"), "value");
    }
}
