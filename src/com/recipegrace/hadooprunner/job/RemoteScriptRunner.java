package com.recipegrace.hadooprunner.job;

import com.jcraft.jsch.*;
import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.ssh.NewUserInfo;
import com.recipegrace.hadooprunner.ssh.SSHCommand;
import javafx.application.Platform;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fjacob on 4/14/15.
 */
public class RemoteScriptRunner {

    private Console console;
    private Cluster cluster;
    BufferedReader stdInput, stdError;
    public RemoteScriptRunner(Console console, Cluster cluster) {
        this.console = console;
        this.cluster=cluster;
    }



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

    private void executeSSHCommand() throws IOException {
        try {
            JSch jsch = new JSch();

            String command = "ls";

            Session session = jsch.getSession(cluster.getUserName(), cluster.getClusterName(), 22);

            UserInfo ui = new NewUserInfo(cluster.getPassWord());
            session.setUserInfo(ui);
            session.connect();


            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // X Forwarding
            // channel.setXForwarding(true);

            //channel.setInputStream(System.in);
            channel.setInputStream(null);

            //channel.setOutputStream(System.out);

            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    appendToConsole(new String(tmp, 0, i));
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    appendToConsole("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            appendToConsole(e);
        }
    }

    private void appendToConsole(String s) {
        Platform.runLater(new Runnable() {

            public void run() {
               console.appendToConsole(s);
            }
        });
    }
    private void appendToConsole(Exception ex) {
        Platform.runLater(new Runnable() {

            public void run() {
                console.appendToConsole(ex);
            }
        });
    }


}
