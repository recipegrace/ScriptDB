package com.recipegrace.hadooprunner.ssh;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;

public class SSHCommand {


    private String userName;
    private String passWord;
    private String hostName;

    public SSHCommand(String userName, String hostName, String passsWord) {
        this.userName = userName;
        this.hostName = hostName;
        this.passWord = passsWord;
    }


    public void execute(String command) throws JSchException, IOException {
        JSch jsch = new JSch();


        Session session = jsch.getSession(userName, hostName, 22);

        UserInfo ui = new NewUserInfo(passWord);
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
                System.out.print(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if (in.available() > 0) continue;
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
        session.disconnect();

    }
}