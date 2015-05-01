package com.recipegrace.hadooprunner.job;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.ssh.SSHTask;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;

/**
 * Created by fjacob on 4/14/15.
 */
public class RemoteCommandRunner<Void> extends Service<Void> {

    private Console console;
    private Cluster cluster;
    private String command;

    public RemoteCommandRunner(Console console, Cluster cluster, String command) {
        this.console = console;
        this.cluster = cluster;
        this.command = command;
    }




    @Override
    protected Task createTask() {
        return new SSHTask(console, cluster) {

            @Override
            protected Void call() throws Exception {
                executeSSHCommand(command);
                updateProgress(1, 1);
                return null;
            }



        };

    }


}

