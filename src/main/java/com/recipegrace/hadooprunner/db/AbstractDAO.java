package com.recipegrace.hadooprunner.db;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by fjacob on 4/12/15.
 */
public abstract class AbstractDAO<T> {
    static Logger log = Logger.getLogger(AbstractDAO.class.getName());

    protected abstract String getFile();

    protected void saveAsJSON(List<T> projects) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(projects);
        FileWriter writer = new FileWriter(getFile());
        writer.write(json);
        writer.close();
        log.info("Saving to file " + getFile());
    }

    public List<T> getAll() throws FileNotFoundException {
        Type listType = getType();
        File file = new File(getFile());
        List<T> entities = null;
        if (file.exists()) {
            BufferedReader br = new BufferedReader(
                    new FileReader(getFile()));
            entities = new Gson().fromJson(br, listType);
            if(entities==null) entities =new ArrayList<T>();
        } else entities = new ArrayList<T>();
        log.info("Searched all objects and returned of size:" + entities.size() + " from file:" + getFile());
        return entities;
    }

    protected abstract Type getType();
}
