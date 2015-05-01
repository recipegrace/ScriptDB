package com.recipegrace.hadooprunner.job;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.main.Console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fjacob on 4/14/15.
 */
public class LocalScriptRunner {

    private Console console;

    public LocalScriptRunner(Console console) {
        this.console = console;
    }

    Process process;
    BufferedReader stdInput, stdError;
    OutputStream out;

    public void run(String scriptPath) throws IOException, InterruptedException, HadoopRunnerException {


        console.clear();
        String[] command = {"/bin/bash", scriptPath, "Argument1"};
        ProcessBuilder builder = new ProcessBuilder(command);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    process = builder.start();
                    stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    //OutputStream out;
                    out = process.getOutputStream();

                    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", console);

                    // any output?
                    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", console);

                    // start gobblers
                    outputGobbler.start();
                    errorGobbler.start();

                    process.waitFor();

                } catch (IOException ex) {
                    Logger.getLogger(LocalScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LocalScriptRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();


    }


}
