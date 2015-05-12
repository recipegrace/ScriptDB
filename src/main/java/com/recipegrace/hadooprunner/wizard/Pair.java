package com.recipegrace.hadooprunner.wizard;

/**
 * Created by fjacob on 5/12/15.
 */
public class Pair {

    private String fullName;
    private String shortName;

    @Override
    public boolean equals(Object obj) {
        if(!( obj instanceof Pair)) return false;
        Pair pair = (Pair)obj;
       return  pair.fullName.equals(fullName)     ;
    }

    @Override
    public String toString() {
        return shortName;
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

    public Pair(String fullName) {
        this.fullName = fullName;
        this.shortName = shorten(fullName);
    }

    public String shorten(String clusterName) {
        return clusterName.split("\\.")[0];
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
