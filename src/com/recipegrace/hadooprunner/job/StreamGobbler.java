package com.recipegrace.hadooprunner.job;

import com.recipegrace.hadooprunner.main.Console;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by fjacob on 4/28/15.
 */
public class StreamGobbler extends Thread {

    private InputStream is;
    private String type;
    private Console console;

    public StreamGobbler(InputStream is, String type, Console console) {
        this.is = is;
        this.type = type;
        this.console=console;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                // System.out.println(type + "> " + line);
                String str = line;
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        console.appendToConsole(str);

                    }
                });
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

