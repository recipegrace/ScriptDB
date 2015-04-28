package com.recipegrace.hadooprunner.main;

import javafx.scene.control.TextArea;

/**
 * Created by fjacob on 4/18/15.
 */
public class Console {
    private TextArea console;

    public Console(TextArea textArea) {
        this.console = textArea;
    }

    public void appendToConsole(String msg) {
        console.appendText("\n" + msg);
    }

    public void appendToConsole(Exception ex) {
        appendToConsole("ERROR: " + ex.getMessage());
    }

    public void clear() {
        console.clear();
    }
}
