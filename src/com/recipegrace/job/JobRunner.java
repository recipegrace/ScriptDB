package com.recipegrace.job;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fjacob on 4/14/15.
 */
public class JobRunner {

    private TextArea console;
    public JobRunner(TextArea console) {
        this.console= console;
    }

    Process process;
    BufferedReader stdInput, stdError;
    OutputStream out;
    public void run(String mainClass, String job) throws IOException, InterruptedException {


        console.clear();
        String[] command = {"/bin/bash", "/Users/fjacob/workspaces/workspace-tailorswift/HadoopRunner/.tests/script.sh", "Argument1"};
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

                    StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");

                    // any output?
                    StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");

                    // start gobblers
                    outputGobbler.start();
                    errorGobbler.start();

                    process.waitFor();

                } catch (IOException ex) {
                    Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(JobRunner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();


    }
    private class StreamGobbler extends Thread {

        InputStream is;
        String type;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                   // System.out.println(type + "> " + line);
                    String str =line;
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            console.appendText( str + "\n");

                        }
                    });
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
