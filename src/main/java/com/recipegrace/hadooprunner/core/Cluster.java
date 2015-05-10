package com.recipegrace.hadooprunner.core;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * Created by fjacob on 4/18/15.
 */
public class Cluster {

    private String clusterName;
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() throws IOException {
        return new String(dec.decodeBuffer(passWord), DEFAULT_ENCODING);

    }

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static BASE64Encoder enc = new BASE64Encoder();
    private BASE64Decoder dec = new BASE64Decoder();

    public void setPassWord(String passWord) {

        this.passWord = enc.encode(passWord.getBytes());
    }

    public String getClusterName() {
        return clusterName;

    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
