package com.recipegrace.hadooprunner.job;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.ssh.SSHTask;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;

/**
 * Created by fjacob on 4/14/15.
 */
public class RemoteScriptRunner<Void> extends Service<Void> {

    private Console console;
    private Cluster cluster;
    private String scriptPath;

    public RemoteScriptRunner(Console console, Cluster cluster, String scriptPath) {
        this.console = console;
        this.cluster = cluster;
        this.scriptPath = scriptPath;
    }


    /*
        public void run(String scriptPath) throws IOException, InterruptedException, HadoopRunnerException {


            console.clear();
            new Thread(){
                @Override
                public void run() {
                    try {
                        executeSSHCommand();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }

    */




    @Override
    protected Task createTask() {
        return new SSHTask(console, cluster) {

            @Override
            protected Void call() throws Exception {
                transferFileToServer(new File(scriptPath), "hola.sh");
                updateProgress(1, 2);
                executeSSHCommand("sh hola.sh");
                updateProgress(1, 2);
                return null;
            }



        };

    }


}

