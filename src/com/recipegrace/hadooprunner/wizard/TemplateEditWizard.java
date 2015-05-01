package com.recipegrace.hadooprunner.wizard;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Template;
import com.recipegrace.hadooprunner.db.TemplateDAO;
import com.recipegrace.hadooprunner.main.Console;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.validation.Validator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fjacob on 4/17/15.
 */
public class TemplateEditWizard extends Wizard {

    private TextField txtTemplateName;
    private TextArea txtTemplate;
    private ComboBox<String> cmbTemplates;
    private TemplateDAO tempDAO = new TemplateDAO();

    public TemplateEditWizard(Console console) {
        setTitle("Linear Wizard");

        // --- page 1
        int row = 0;

        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        page1Grid.add(new Label("Template name:"), 0, row);
        try {
            cmbTemplates = createTemplateCombo();
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }
        cmbTemplates.setId("templateNames");
        getValidationSupport().registerValidator(cmbTemplates, Validator.createEmptyValidator(" Template is mandatory"));

        page1Grid.add(cmbTemplates, 1, row++);


        Wizard.WizardPane page1 = new Wizard.WizardPane();
        page1.setHeaderText("Please select the template to edit");
        page1.setContent(page1Grid);

        // --- page 2
        final Wizard.WizardPane page2 = new Wizard.WizardPane() {
            @Override
            public void onEnteringPage(Wizard wizard) {


                try {
                    Template template = tempDAO.getTemplate(cmbTemplates.getSelectionModel().getSelectedItem());
                    txtTemplate.setText(template.getTemplate());
                    txtTemplateName.setText(template.getTemplateName());
                } catch (FileNotFoundException e) {
                    console.appendToConsole(e);
                }
            }
        };
        page2.setHeaderText("Please edit the template");
        page2.setContent(getTemplateGridPane());


        // create wizard
        LinearFlow flow = new Wizard.LinearFlow(page1, page2);

        setFlow(flow);

        showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                Template template = new Template();
                template.setTemplate(txtTemplate.getText());
                template.setTemplateName(txtTemplateName.getText());
                try {
                    tempDAO.saveTemplate(template);
                } catch (IOException | HadoopRunnerException e) {
                    console.appendToConsole(e);
                }
            }
        });


        // show wizard and wait for response

    }

    private ComboBox<String> createTemplateCombo() throws FileNotFoundException {
        ComboBox<String> cmbTemplates = new ComboBox<String>();

        List<String> templateNames = tempDAO.getAll().stream().map(f -> f.getTemplateName()).collect(Collectors.toList());
        cmbTemplates.setItems(FXCollections.observableList(templateNames));
        return cmbTemplates;
    }

    private GridPane getTemplateGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        txtTemplateName = new TextField();
        txtTemplateName.setDisable(true);
        txtTemplate = new TextArea();
        grid.add(new Label("Template name:"), 0, 0);
        grid.add(txtTemplateName, 1, 0);
        grid.add(new Label("Template :"), 0, 1);
        grid.add(txtTemplate, 1, 1);
        txtTemplateName.setId("templateName");
        txtTemplate.setId("template");
        GridPane.setHgrow(txtTemplate, Priority.ALWAYS);
        return grid;
    }
}
