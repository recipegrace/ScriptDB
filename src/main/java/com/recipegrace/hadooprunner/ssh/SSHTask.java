package com.recipegrace.hadooprunner.ssh;

import com.jcraft.jsch.*;
import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.ssh.NewUserInfo;
import javafx.concurrent.Task;

import java.io.*;

/**
 * Created by fjacob on 5/1/15.
 */
abstract public class SSHTask<Void> extends Task<Void> {
    protected Console console;
    protected Cluster cluster;
    public SSHTask(Console console, Cluster cluster){
         this.console=console;
        this.cluster=cluster;

     }
    protected void executeSSHCommand(String command) throws IOException {
        try {
            JSch jsch = new JSch();


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


            //FileOutputStream fos=new FileOutputStream("/tmp/stderr");
            //((ChannelExec)channel).setErrStream(fos);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            InputStream err = ((ChannelExec) channel).getErrStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            addToConsole(channel, in, tmp);
            addToConsole(channel, err, tmp);

            channel.disconnect();
            session.disconnect();
        } catch (JSchException e) {
            console.appendToConsole(e);
        }
    }

    private void addToConsole(Channel channel, InputStream in, byte[] tmp) throws IOException {
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
                console.appendToConsole(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) continue;
               //console. appendToConsole("exit-status: " + channel.getExitStatus());
                break;

            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
               console. appendToConsole(ee);
            }
        }
    }

    protected int checkAck(InputStream in) throws IOException {
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
                console.appendToConsole(sb.toString());
            }
            if (b == 2) { // fatal error
                console.appendToConsole(sb.toString());
            }
        }
        return b;
    }
    protected void transferFileToServer(File file, String outFile) throws IOException,
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

        updateProgress(currentProgress++, totalProgress);

        // send a content of lfile
        fis = new FileInputStream(file);


        while (true) {

            int len = fis.read(buf, 0, buf.length);
            if (len <= 0)
                break;
            out.write(buf, 0, len); // out.flush();
            updateProgress(currentProgress++, totalProgress);

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
    protected File transferFromServer(String serverPath, String localFilePath)
            throws IOException, JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(cluster.getUserName(), cluster.getClusterName(), 22);
        UserInfo ui = new NewUserInfo(cluster.getPassWord());
        session.setUserInfo(ui);
        session.connect();
        FileOutputStream fos = null;
        String rfile = serverPath;
        String lfile = localFilePath;
        String command = "scp -f " + rfile;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();

        byte[] buf = new byte[1024];

        // send '\0'
        buf[0] = 0;
        out.write(buf, 0, 1);
        out.flush();

        while (true) {
            int c = checkAck(in);
            if (c != 'C') {
                break;
            }

            // read '0644 '
            in.read(buf, 0, 5);

            long filesize = 0L;
            while (true) {
                if (in.read(buf, 0, 1) < 0) {
                    // error
                    break;
                }
                if (buf[0] == ' ')
                    break;
                filesize = filesize * 10L + (long) (buf[0] - '0');
            }


            String file = null;
            for (int i = 0; ; i++) {
                in.read(buf, i, 1);
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i);
                    break;
                }
            }


            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            // read a content of lfile
            fos = new FileOutputStream(lfile);
            int foo;
            while (true) {
                if (buf.length < filesize)
                    foo = buf.length;
                else
                    foo = (int) filesize;
                foo = in.read(buf, 0, foo);
                if (foo < 0) {
                    // error
                    break;
                }
                fos.write(buf, 0, foo);
                filesize -= foo;
                if (filesize == 0L)
                    break;
            }
            fos.close();
            fos = null;

            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
        }
        // new File()
        channel.disconnect();
        session.disconnect();
        return new File(lfile);

    }


}
