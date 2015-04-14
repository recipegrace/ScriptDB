package com.recipegrace.dialogs;

import com.recipegrace.core.Template;
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
public class TemplateDialog extends Dialog<Template> {


    private boolean editMode =false;
    public TemplateDialog() {
        this.setTitle("New Template");
        this.setHeaderText("Create a new template!");
        createTemplateForm();
        validations();
        Platform.runLater(() -> txtTemplateName.requestFocus());
        convertResultToTemplate();


    }
    public TemplateDialog(Template template) {
        this.editMode=true;
        this.setTitle("Edit Template");
        this.setHeaderText("Edit an existing template!");
        createTemplateForm();
        validations();

        Platform.runLater(() -> txtTemplateName.requestFocus());
        convertResultToTemplate();

        this.txtTemplate.setText(template.getTemplate());


    }

    private void convertResultToTemplate() {
        // Convert the result to a txtTemplateName-btnTemplateLocation-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                Template template =new Template();
                template.setTemplate(txtTemplate.getText());
                template.setTemplateName(txtTemplateName.getText());
                return template;
            }
            return null;
        });
    }

    private void validations() {
        // Enable/Disable login button depending on whether a txtTemplateName was entered.
        Node loginButton = this.getDialogPane().lookupButton(buttonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        txtTemplateName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        txtTemplate.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
    }

    private ButtonType buttonType;
    private TextField txtTemplateName;

    private TextArea txtTemplate;
    private void createTemplateForm() {
        // Set the button types.
        if(editMode)
            buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        else
            buttonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

// Create the txtTemplateName and btnTemplateLocation labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

         txtTemplateName = new TextField();
        if(editMode) txtTemplateName.setDisable(true);
        txtTemplate = new TextArea();
        grid.add(new Label("Template name:"), 0, 0);
        grid.add(txtTemplateName, 1, 0);
        grid.add(new Label("Template :"), 0, 1);
        grid.add(txtTemplate, 1, 1);

        this.getDialogPane().setContent(grid);

    }

}
