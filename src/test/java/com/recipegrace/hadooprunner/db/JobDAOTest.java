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
        dao.createJob("projectName", "projectName", "mainClass1", vmArguments, new HashMap<String, String>());
        dao.createJob("projectName", "projectName", "mainClass2", vmArguments, new HashMap<String, String>());
        List<Job> jobs = dao.getJobs("projectName");
        assertEquals(jobs.size(), 2);
    }

    @Test(expected = HadoopRunnerException.class)
    public void testSameCreateJob() throws IOException, HadoopRunnerException {
        JobDAO dao = new JobDAO();
        Map<String, String> vmArguments = new HashMap<String, String>();
        dao.createJob("projectName", "projectName", "mainClass1", vmArguments, new HashMap<String, String>());
        dao.createJob("projectName", "projectName", "mainClass1", vmArguments, new HashMap<String, String>());
    }

    @Test
    public void testJobDetails() throws IOException, HadoopRunnerException {
        JobDAO dao = new JobDAO();
        Map<String, String> vmArguments = new HashMap<String, String>();
        vmArguments.put("key", "value");
        dao.createJob("projectName", "projectName", "mainClass1", vmArguments, new HashMap<String, String>());
        Job job = dao.getJob("projectName", "mainClass1");
        assertEquals(job.getVmArguments().get("key"), "value");
    }
}