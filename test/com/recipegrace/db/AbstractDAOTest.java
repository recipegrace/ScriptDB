package com.recipegrace.db;

import org.junit.Before;

import java.io.File;

/**
 * Created by fjacob on 4/12/15.
 */
public class AbstractDAOTest {
    @Before
    public void initProject() {
        File file1 = new File("db/projects.json");
        file1.delete();
        File file2 = new File("db/jobs.json");
        file2.delete();
        File file3 = new File("db/templates.json");
        file3.delete();

    }
}

