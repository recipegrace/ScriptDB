package com.recipegrace.hadooprunner.ssh;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicSSHTest {


    protected void createFile(String content, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(content);
        writer.close();
    }

    protected List<String> getLocalConnectionInfo() throws IOException {


        String fileName = ".localproperties";
        return getConnnectionInfo(fileName);


    }

    protected List<String> getConnnectionInfo(String fileName)
            throws IOException {

        java.util.List<String> elements = getAllLines(fileName);
        return elements;
    }

    protected java.util.List<String> getAllLines(String fileName)
            throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
        java.util.List<String> elements = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            elements.add(line);
        }
        reader.close();
        return elements;
    }

}