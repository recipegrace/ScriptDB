package com.recipegrace.db;

import org.junit.Before;

import java.io.File;

/**
 * Created by fjacob on 4/12/15.
 */
public class AbstractDAOTest {
    @Before
    public void initProject() {
        File file1 = new File("projects.json");
        file1.delete();
        File file2 = new File("jobs.json");
        file2.delete();
    }
}

