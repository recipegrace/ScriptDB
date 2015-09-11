package com.recipegrace.hadooprunner.dialogs;

/**
 * Created by fjacob on 7/9/15.
 */
public class Argument {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public boolean isVM() {
        return isVM;
    }

    public Argument(String key, String value, boolean isVM) {
        this.key = key;
        this.value = value;
        this.isVM = isVM;
    }

    private boolean isVM;
}
