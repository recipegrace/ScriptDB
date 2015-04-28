package com.recipegrace.hadooprunner.ssh;

import com.jcraft.jsch.JSchException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class SSHCommandTest extends BasicSSHTest {


    @Test
    public void test() throws IOException, JSchException {
        List<String> userInfo = getLocalConnectionInfo();
        String userName = userInfo.get(0);
        String hostName = userInfo.get(1);
        String passsWord = userInfo.get(2);


        //	SSHCommand sshCommand2 = new SSHCommand(userName,hostName, passsWord);
        //	   boolean ptimestamp = true;

        // exec 'scp -t rfile' remotely
        //    String command="scp " + (ptimestamp ? "-p" :"") +" -t "+".cbproperties";
        //   sshCommand2.execute(command);
        SSHCommand sshCommand = new SSHCommand(userName, hostName, passsWord);
        sshCommand.execute("/bin/sh runOnHadoop.sh");
        sshCommand.execute("ls");
    }

}