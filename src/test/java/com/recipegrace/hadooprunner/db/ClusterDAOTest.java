package com.recipegrace.hadooprunner.db;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjacob on 4/11/15.
 */
public class ClusterDAOTest extends AbstractDAOTest {


    @Test(expected = HadoopRunnerException.class)
    public void testSameCreateCluster() throws IOException, HadoopRunnerException {
        ClusterDAO rgDAO = new ClusterDAO();
        createCluster("clusterName", "username", "password");
        createCluster("clusterName", "username", "password");
    }

    @Test
    public void testSaveCluster() throws IOException, HadoopRunnerException {
        ClusterDAO rgDAO = new ClusterDAO();
        createCluster("clusterName", "username", "password");
        Cluster result = rgDAO.getCluster("clusterName");
        assertEquals(result.getUserName(), "username");
        assertEquals(result.getPassWord(), "password");
    }

    private void createCluster(String clusterName, String userName, String password) throws IOException, HadoopRunnerException {
        ClusterDAO rgDAO = new ClusterDAO();
        Cluster cluster = new Cluster();
        cluster.setClusterName(clusterName);
        cluster.setUserName(userName);
        cluster.setPassWord(password);
        rgDAO.createCluster(cluster);
    }
}
