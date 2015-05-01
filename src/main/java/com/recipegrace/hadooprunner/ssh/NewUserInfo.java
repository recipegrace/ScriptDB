package com.recipegrace.hadooprunner.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class NewUserInfo implements UserInfo, UIKeyboardInteractive {

    private String passWord;

    public NewUserInfo(String passWord) {
        this.passWord = passWord;
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getPassphrase() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return passWord;
    }

    @Override
    public boolean promptPassphrase(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean promptPassword(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean promptYesNo(String arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void showMessage(String arg0) {
        System.out.println(arg0);

    }

    @Override
    public String[] promptKeyboardInteractive(String arg0, String arg1,
                                              String arg2, String[] arg3, boolean[] arg4) {
        // TODO Auto-generated method stub
        return new String[]{passWord};
    }

}