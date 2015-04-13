package com.recipegrace.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.recipegrace.core.Project;

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
        String json =gson.toJson(projects);
        FileWriter writer = new FileWriter(getFile());
        writer.write(json);
        writer.close();
        log.info("Saving to file " + getFile() );
    }
    public List<T> getAll() throws FileNotFoundException {
        Type listType = getType();
        File file = new File(getFile());
        List<T> projects =null;
        if(file.exists()) {
            BufferedReader br = new BufferedReader(
                    new FileReader(getFile()));
            projects = new Gson().fromJson(br, listType);
        }
        else projects = new ArrayList<T>();
        log.info("Searched all objects and returned of size:" + projects.size() + " from file:" + getFile());
        return projects;
    }

    protected  abstract Type getType() ;
}
