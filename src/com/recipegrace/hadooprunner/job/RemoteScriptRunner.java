package com.recipegrace.hadooprunner.job;

import com.jcraft.jsch.*;
import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.ssh.NewUserInfo;
import com.recipegrace.hadooprunner.ssh.SSHCommand;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fjacob on 4/14/15.
 */
public class RemoteScriptRunner<Void>  extends Service<Void>{

    private Console console;
    private Cluster cluster;
    private String scriptPath;
    public RemoteScriptRunner(Console console, Cluster cluster, String scriptPath) {
        this.console = console;
        this.cluster=cluster;
        this.scriptPath=scriptPath;
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
    private void executeSSHCommand() throws IOException {
        try {
            JSch jsch = new JSch();

            String command = "sh hola.sh";

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
                    appendToConsole(ee);
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




    @Override
    protected Task createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                transferFile(new File(scriptPath), "hola.sh");
                updateProgress(1, 2);
                executeSSHCommand();
                updateProgress(1,2);
                return null;
            }
            private void transferFile(File file, String outFile) throws IOException,
                    JSchException {

                JSch jsch = new JSch();


                Session session = jsch.getSession(cluster.getUserName(), cluster.getClusterName(), 22);

                UserInfo ui = new NewUserInfo(cluster.getPassWord());
                session.setUserInfo(ui);
                session.connect();
                FileInputStream fis = null;

                boolean ptimestamp = true;
                long filesize = file.length();

                byte[] buf = new byte[1024];
                int blocks = (int) (filesize + buf.length - 1) / buf.length;
                int totalProgress = blocks + 2;
                double currentProgress = 0.0;

                updateProgress(currentProgress++, totalProgress);

                // exec 'scp -t rfile' remotely
                String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + outFile;
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                // get I/O streams for remote scp
                OutputStream out = channel.getOutputStream();
                InputStream in = channel.getInputStream();

                channel.connect();

                if (checkAck(in) != 0) {
                    return;
                }


                if (ptimestamp) {
                    command = "T " + (file.lastModified() / 1000) + " 0";
                    // The access time should be sent here,
                    // but it is not accessible with JavaAPI ;-<
                    command += (" " + (file.lastModified() / 1000) + " 0\n");
                    out.write(command.getBytes());
                    out.flush();
                    if (checkAck(in) != 0) {
                        return;
                    }
                }
                updateProgress(currentProgress++, totalProgress);

                // send "C0644 filesize filename", where filename should not include '/'

                command = "C0644 " + filesize + " ";

                command += file.getName();
                command += "\n";
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    return;
                }

                updateProgress(currentProgress++,totalProgress);

                // send a content of lfile
                fis = new FileInputStream(file);


                while (true) {

                    int len = fis.read(buf, 0, buf.length);
                    if (len <= 0)
                        break;
                    out.write(buf, 0, len); // out.flush();
                    updateProgress(currentProgress++,totalProgress);

                }
                fis.close();
                fis = null;
                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                if (checkAck(in) != 0) {
                    return;
                }
                out.close();

                channel.disconnect();
                session.disconnect();
            }
            private int checkAck(InputStream in) throws IOException {
                int b = in.read();
                // b may be 0 for success,
                // 1 for error,
                // 2 for fatal error,
                // -1
                if (b == 0)
                    return b;
                if (b == -1)
                    return b;

                if (b == 1 || b == 2) {
                    StringBuffer sb = new StringBuffer();
                    int c;
                    do {
                        c = in.read();
                        sb.append((char) c);
                    } while (c != '\n');
                    if (b == 1) { // error
                        System.out.print(sb.toString());
                    }
                    if (b == 2) { // fatal error
                        System.out.print(sb.toString());
                    }
                }
                return b;
            }
        };

    }


    }

