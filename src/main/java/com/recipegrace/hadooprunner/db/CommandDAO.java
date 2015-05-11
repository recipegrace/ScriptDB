package com.recipegrace.hadooprunner.db;

import com.google.gson.reflect.TypeToken;
import com.recipegrace.hadooprunner.core.Command;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fjacob on 4/11/15.
 */
public class CommandDAO extends AbstractDAO<Command> {


    public void createCommand(Command command) throws IOException, HadoopRunnerException {

        List<Command> commands = getAll();
        List<Command> removedCurrent =
                commands.stream().filter(f -> !f.getCommmand().trim().equals(command.getCommmand()))
                        .collect(Collectors.toList());
        removedCurrent.add(command);
        Collections.sort(removedCurrent, new Comparator<Command>() {
            @Override
            public int compare(Command o1, Command o2) {
                return -1 * o1.getCreatedTime().compareTo(o2.getCreatedTime());
            }
        });
        List<Command> toBeSaved = removedCurrent;
        if (removedCurrent.size() >= 24) {
            toBeSaved = removedCurrent.subList(0, 24);
        }
        saveAsJSON(toBeSaved);

    }


    @Override
    protected String getFile() {
        return ".db/commands.json";
    }

    protected Type getType() {
        return new TypeToken<ArrayList<Command>>() {
        }.getType();
    }
}
