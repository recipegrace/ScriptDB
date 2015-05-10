package com.recipegrace.hadooprunner.dialogs;

import com.recipegrace.hadooprunner.core.Cluster;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 * Created by fjacob on 4/13/15.
 */
public class ClusterDialog extends Dialog<Cluster> {


    private boolean editMode = false;

    public ClusterDialog() {
        this.setTitle("New key-value pair");
        this.setHeaderText("Create a key-value pair");
        createClusterForm();
        validations();
        Platform.runLater(() -> txtClusterName.requestFocus());
        convertResultToCluster();


    }

    public ClusterDialog(Pair<String, String> pair) {
        this.editMode = true;
        this.setTitle("Edit key-value pair");
        this.setHeaderText("Edit a key-value pair!");
        createClusterForm();
        validations();

        Platform.runLater(() -> txtClusterName.requestFocus());
        convertResultToCluster();

        this.txtUserName.setText(pair.getValue());
        this.txtClusterName.setText(pair.getKey());


    }

    private void convertResultToCluster() {
        this.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                Cluster cluster = new Cluster();
                cluster.setClusterName(txtClusterName.getText());
                cluster.setPassWord(txtPassword.getText());
                cluster.setUserName(txtUserName.getText());
                return cluster;
            }
            return null;
        });
    }

    private void validations() {
        Node loginButton = this.getDialogPane().lookupButton(buttonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        txtClusterName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        txtUserName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

    }

    private ButtonType buttonType;
    private TextField txtClusterName;

    private TextField txtUserName;
    private PasswordField txtPassword;

    private void createClusterForm() {
        // Set the button types.
        if (editMode)
            buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        else
            buttonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

// Create the txtClusterName and btnClusterLocation labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        txtClusterName = new TextField();
        if (editMode) txtClusterName.setDisable(true);
        txtClusterName.setPromptText("Key name");
        txtUserName = new TextField();
        txtPassword = new PasswordField();
        grid.add(new Label("Cluster name:"), 0, 0);
        grid.add(txtClusterName, 1, 0);
        grid.add(new Label("User name:"), 0, 1);
        grid.add(txtUserName, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(txtPassword, 1, 2);

        this.getDialogPane().setContent(grid);

    }
}
