package com.recipegrace.hadooprunner.wizard;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.Command;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.db.ClusterDAO;
import com.recipegrace.hadooprunner.db.CommandDAO;
import com.recipegrace.hadooprunner.job.RemoteCommandRunner;
import com.recipegrace.hadooprunner.main.Console;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.validation.Validator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fjacob on 4/17/15.
 */
public class RunSSHCommandWizard extends Wizard {

    private ComboBox<String> cmbCommand;

    private ComboBox<String> cmbTemplates;
    private ClusterDAO clusterDAO = new ClusterDAO();

    public RunSSHCommandWizard(Console console) throws FileNotFoundException {
        setTitle("Execute SSH Command Wizard");

        // --- page 1
        int row = 0;

        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        page1Grid.add(new Label("Cluster name:"), 0, row);
        try {
            cmbTemplates = createClusterCombo();
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }
        cmbTemplates.setId("clusterNames");
        getValidationSupport().registerValidator(cmbTemplates, Validator.createEmptyValidator(" Cluster name is mandatory"));

        page1Grid.add(cmbTemplates, 1, row++);


        WizardPane page1 = new WizardPane();
        page1.setHeaderText("Please select the cluster to run");
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
                    Service<Void> service = new RemoteCommandRunner<Void>(console, cluter, cmbCommand.getSelectionModel().getSelectedItem());

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

        List<String> clusterNames = clusterDAO.getAll().stream().map(f -> f.getClusterName()).collect(Collectors.toList());
        cmbClusters.setItems(FXCollections.observableList(clusterNames));
        return cmbClusters;
    }

    CommandDAO commandDAO = new CommandDAO();

    private GridPane getCommandGridPane() throws FileNotFoundException {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        cmbCommand = new ComboBox<String>();
        List<String> commands = commandDAO.getAll().stream().map(f -> f.getCommmand()).collect(Collectors.toList());
        cmbCommand.setItems(FXCollections.observableList(commands));
        cmbCommand.setEditable(true);
        cmbCommand.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                try {
                    commandDAO.createCommand(new Command(t1));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (HadoopRunnerException e) {
                    e.printStackTrace();
                }
            }
        });
        grid.add(new Label("Command to execute"), 0, 0);
        grid.add(cmbCommand, 1, 0);

        cmbCommand.setId("clusterName");
        return grid;
    }
}
