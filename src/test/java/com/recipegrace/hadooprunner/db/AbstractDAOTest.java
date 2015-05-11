package com.recipegrace.hadooprunner.db;

import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by fjacob on 4/12/15.
 */
public class AbstractDAOTest {
    @Before
    public void initProject() throws IOException {
        deleteFile(".db/projects.json");
        deleteFile(".db/jobs.json");
        deleteFile(".db/templates.json");
        deleteFile(".db/clusters.json");
        deleteFile(".db/commands.json");

    }

    private void deleteFile(String pathname) throws IOException {
        Files.deleteIfExists(Paths.get(pathname));
    }
}

