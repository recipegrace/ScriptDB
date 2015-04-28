package com.recipegrace.hadooprunner.dialogs;

import com.recipegrace.hadooprunner.core.Project;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


/**
 * Created by fjacob on 4/12/15.
 */
public class ProjectDialog extends Dialog<Project> {


    private boolean editMode = false;

    public ProjectDialog() {
        this.setTitle("New Project");
        this.setHeaderText("Create a new project!");
        createProjectForm();
        validations();
        Platform.runLater(() -> txtProjectName.requestFocus());
        convertResultToProject();


    }

    public ProjectDialog(Project project) {
        this.editMode = true;
        this.setTitle("Edit Project");
        this.setHeaderText("Edit an existing project!");
        createProjectForm();
        validations();

        Platform.runLater(() -> txtProjectName.requestFocus());
        convertResultToProject();

        this.txtJarName.setText(project.getJarName());
        this.txtProjectLocation.setText(project.getProjectLocation());
        this.txtProjectName.setText(project.getProjectName());


    }

    private void convertResultToProject() {
        // Convert the result to a txtProjectName-btnProjectLocation-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                Project project = new Project();
                project.setJarName(txtJarName.getText());
                project.setProjectLocation(txtProjectLocation.getText());
                project.setProjectName(txtProjectName.getText());
                return project;
            }
            return null;
        });
    }

    private void validations() {
        // Enable/Disable login button depending on whether a txtProjectName was entered.
        Node loginButton = this.getDialogPane().lookupButton(buttonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        txtProjectName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        txtProjectLocation.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(!new File(newValue.trim()).exists());
        });
        txtJarName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
    }

    private ButtonType buttonType;
    private TextField txtProjectName;

    private Button btnProjectLocation;
    private TextField txtProjectLocation;
    private TextField txtJarName;

    private void createProjectForm() {
        // Set the button types.
        if (editMode)
            buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        else
            buttonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

// Create the txtProjectName and btnProjectLocation labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        txtProjectName = new TextField();
        if (editMode) txtProjectName.setDisable(true);
        txtJarName = new TextField();
        txtProjectName.setPromptText("Project Name");
        btnProjectLocation = new Button();
        btnProjectLocation.setText("Browse");
        btnProjectLocation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Open File");
                File file = chooser.showDialog(new Stage());
                txtProjectLocation.setText(file.getAbsolutePath().toString());
            }
        });
        txtProjectLocation = new TextField();
        grid.add(new Label("Project name:"), 0, 0);
        grid.add(txtProjectName, 1, 0);
        grid.add(new Label("Project location:"), 0, 1);
        grid.add(btnProjectLocation, 2, 1);
        grid.add(txtProjectLocation, 1, 1);
        grid.add(new Label("JAR name:"), 0, 2);
        grid.add(txtJarName, 1, 2);

        this.getDialogPane().setContent(grid);

    }

}
