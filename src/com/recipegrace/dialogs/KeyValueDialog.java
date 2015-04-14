package com.recipegrace.dialogs;

import com.recipegrace.core.Project;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;

/**
 * Created by fjacob on 4/13/15.
 */
public class KeyValueDialog extends Dialog<Pair<String,String>> {


    private boolean editMode =false;
    public KeyValueDialog() {
        this.setTitle("New key-value pair");
        this.setHeaderText("Create a key-value pair");
        createProjectForm();
        validations();
        Platform.runLater(() -> txtKey.requestFocus());
        convertResultToProject();


    }
    public KeyValueDialog(Pair<String,String> pair) {
        this.editMode=true;
        this.setTitle("Edit key-value pair");
        this.setHeaderText("Edit a key-value pair!");
        createProjectForm();
        validations();

        Platform.runLater(() -> txtKey.requestFocus());
        convertResultToProject();

        this.txtValue.setText(pair.getValue());
        this.txtKey.setText(pair.getKey());


    }

    private void convertResultToProject() {
        this.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                return new Pair<String, String>(txtKey.getText(), txtValue.getText());
            }
            return null;
        });
    }

    private void validations() {
        Node loginButton = this.getDialogPane().lookupButton(buttonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        txtKey.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        txtValue.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

    }

    private ButtonType buttonType;
    private TextField txtKey;

    private TextField txtValue;
    private void createProjectForm() {
        // Set the button types.
        if(editMode)
            buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        else
            buttonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

// Create the txtKey and btnProjectLocation labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        txtKey = new TextField();
        if(editMode) txtKey.setDisable(true);
        txtKey.setPromptText("Key name");
        txtValue = new TextField();
        grid.add(new Label("Key:"), 0, 0);
        grid.add(txtKey, 1, 0);
        grid.add(new Label("Value:"), 0, 1);
        grid.add(txtValue, 1, 1);

        this.getDialogPane().setContent(grid);

    }
}
