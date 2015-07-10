package com.recipegrace.hadooprunner.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Created by fjacob on 4/13/15.
 */
public class ArgumentDialog extends Dialog<Argument> {




    public ArgumentDialog() {
        this.setTitle("New key-value pair");
        this.setHeaderText("Create a key-value pair");
        createArgumentForm();
        validations();
        Platform.runLater(() -> txtKey.requestFocus());
        convertResultToKeyValue();


    }



    private void convertResultToKeyValue() {
        this.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                return new Argument(txtKey.getText(), txtValue.getText(), chkIsVM.isSelected());
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
    private CheckBox chkIsVM;

    private void createArgumentForm() {
        // Set the button types.

            buttonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

// Create the txtKey and btnProjectLocation labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        txtKey = new TextField();
        txtKey.setPromptText("Key name");
        txtValue = new TextField();
        chkIsVM= new CheckBox();

        grid.add(new Label("Key:"), 0, 0);
        grid.add(txtKey, 1, 0);
        grid.add(new Label("Value:"), 0, 1);
        grid.add(txtValue, 1, 1);
        grid.add(new Label("VM argument"), 0, 2);
        grid.add(chkIsVM, 1, 2);

        this.getDialogPane().setContent(grid);

    }
}
