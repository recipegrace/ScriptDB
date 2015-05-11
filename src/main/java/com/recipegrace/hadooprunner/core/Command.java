package com.recipegrace.hadooprunner.core;

/**
 * Created by fjacob on 5/10/15.
 */
public class Command {
    public String getCommmand() {
        return commmand;
    }



    private String commmand;

    public Long getCreatedTime() {
        return createdTime;
    }



    private Long createdTime;

    public Command(String command) {
        this.commmand=command;
        this.createdTime=System.currentTimeMillis();
    }
}
