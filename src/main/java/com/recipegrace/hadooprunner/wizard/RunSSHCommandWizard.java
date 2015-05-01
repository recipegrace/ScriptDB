package com.recipegrace.hadooprunner.wizard;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.db.ClusterDAO;
import com.recipegrace.hadooprunner.job.RemoteCommandRunner;
import com.recipegrace.hadooprunner.main.Console;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.validation.Validator;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fjacob on 4/17/15.
 */
public class RunSSHCommandWizard extends Wizard {

    private TextField txtCommand;

    private ComboBox<String> cmbTemplates;
    private ClusterDAO clusterDAO = new ClusterDAO();

    public RunSSHCommandWizard(Console console) {
        setTitle("Execute SSH Command Wizard");

        // --- page 1
        int row = 0;

        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        page1Grid.add(new Label("Template name:"), 0, row);
        try {
            cmbTemplates = createClusterCombo();
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }
        cmbTemplates.setId("templateNames");
        getValidationSupport().registerValidator(cmbTemplates, Validator.createEmptyValidator(" Template is mandatory"));

        page1Grid.add(cmbTemplates, 1, row++);


        WizardPane page1 = new WizardPane();
        page1.setHeaderText("Please select the template to edit");
        page1.setContent(page1Grid);

        // --- page 2
        final WizardPane page2 = new WizardPane();
        page2.setHeaderText("Please enter your command");
        page2.setContent(getCommandGridPane());


        // create wizard
        LinearFlow flow = new LinearFlow(page1, page2);

        setFlow(flow);

        showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                try {
                    Cluster cluter = clusterDAO.getCluster(cmbTemplates.getSelectionModel().getSelectedItem());
                    Service<Void> service = new RemoteCommandRunner<Void>(console,cluter, txtCommand.getText());

                    ProgressDialog progDiag = new ProgressDialog(service);
                    progDiag.setTitle("Running job");
                    progDiag.initOwner(null);
                    progDiag.setHeaderText("SSH job");
                    progDiag.initModality(Modality.WINDOW_MODAL);
                    service.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });


        // show wizard and wait for response

    }

    private ComboBox<String> createClusterCombo() throws FileNotFoundException {
        ComboBox<String> cmbClusters = new ComboBox<String>();

        List<String> templateNames = clusterDAO.getAll().stream().map(f -> f.getClusterName()).collect(Collectors.toList());
        cmbClusters.setItems(FXCollections.observableList(templateNames));
        return cmbClusters;
    }

    private GridPane getCommandGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        txtCommand = new TextField();

        grid.add(new Label("Command to execute"), 0, 0);
        grid.add(txtCommand, 1, 0);

        txtCommand.setId("templateName");
        return grid;
    }
}
