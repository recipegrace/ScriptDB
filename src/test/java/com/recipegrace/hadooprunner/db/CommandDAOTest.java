package com.recipegrace.hadooprunner.db;

import com.recipegrace.hadooprunner.core.Command;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by fjacob on 5/10/15.
 */
public class CommandDAOTest {

    @Test
    public void testCommandTest() throws IOException, HadoopRunnerException {
        CommandDAO commandDAO = new CommandDAO();
        String sampleCommand = "hello";
        commandDAO.createCommand(new Command(sampleCommand));
        Command result = commandDAO.getAll().get(0);
        assertEquals(result.getCommmand(), sampleCommand);
    }


    @Test
    public void testLastCommandSaveTest() throws IOException, HadoopRunnerException {
        CommandDAO commandDAO = new CommandDAO();
        String sampleCommand = "hello";
        for (int i = 0; i < 50; i++)
            commandDAO.createCommand(new Command(sampleCommand + i));

        List<Command> result = commandDAO.getAll();
        assertEquals(24, result.size());
        for (Command command : result) {
            System.out.println(command.getCommmand());
        }
    }

}
