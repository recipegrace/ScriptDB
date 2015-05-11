package com.recipegrace.hadooprunner.db;

import com.google.gson.reflect.TypeToken;
import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;

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
public class ClusterDAO extends AbstractDAO<Cluster> {


    public void checkClusterExists(String clusterName) throws FileNotFoundException, HadoopRunnerException {
        if (getCluster(clusterName) != null) throw new HadoopRunnerException("Cluster already exists");
    }


    public void createCluster(Cluster cluster) throws IOException, HadoopRunnerException {

        checkClusterExists(cluster.getClusterName());
        List<Cluster> clusters = getAll();
        clusters.add(cluster);
        saveAsJSON(clusters);

    }


    public void saveCluster(Cluster cluster) throws IOException, HadoopRunnerException {

        List<Cluster> clusters = getAll().stream()
                .filter(f -> !f.getClusterName().equals(cluster.getClusterName()))
                .collect(Collectors.toList());
        clusters.add(cluster);
        saveAsJSON(clusters);

    }


    public Cluster getCluster(String clusterName) throws FileNotFoundException {

        Stream<Cluster> selectedClusters = getAll().stream().filter(f -> f.getClusterName().equals(clusterName));
        return selectedClusters.findFirst().orElse(null);
    }

    @Override
    protected String getFile() {
        return ".db/clusters.json";
    }

    protected Type getType() {
        return new TypeToken<ArrayList<Cluster>>() {
        }.getType();
    }
}
