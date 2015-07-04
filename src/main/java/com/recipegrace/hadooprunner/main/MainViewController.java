package com.recipegrace.hadooprunner.main;

import com.recipegrace.hadooprunner.core.*;
import com.recipegrace.hadooprunner.db.ClusterDAO;
import com.recipegrace.hadooprunner.db.JobDAO;
import com.recipegrace.hadooprunner.db.ProjectDAO;
import com.recipegrace.hadooprunner.db.TemplateDAO;
import com.recipegrace.hadooprunner.dialogs.ClusterDialog;
import com.recipegrace.hadooprunner.dialogs.KeyValueDialog;
import com.recipegrace.hadooprunner.dialogs.ProjectDialog;
import com.recipegrace.hadooprunner.dialogs.TemplateAddDialog;
import com.recipegrace.hadooprunner.job.ButtonCell;
import com.recipegrace.hadooprunner.tree.TreeCellImpl;
import com.recipegrace.hadooprunner.template.ScriptGenerator;
import com.recipegrace.hadooprunner.tree.NavigatorTreeContent;
import com.recipegrace.hadooprunner.wizard.RunSSHCommandWizard;
import com.recipegrace.hadooprunner.wizard.TemplateEditWizard;
import com.sun.prism.impl.Disposer.Record;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by fjacob on 4/10/15.
 */
public class MainViewController {
    static Logger log = Logger.getLogger(MainViewController.class.getName());
    private TreeItem<NavigatorTreeContent> rootNode;


    @FXML
    private TabPane mainTab;

    @FXML
    private TextArea consoleTextArea;

    private Console console;

    @FXML
    private ComboBox<String> cmbProjects;

    @FXML
    private ComboBox<String> cmbTemplates;

    @FXML
    private Button btnSaveJob;


    @FXML
    private Button btnNewVMArguments;


    @FXML
    private TextField txtMainClass;

    @FXML
    private TableColumn tblColDeleteProgramArgs;

    @FXML
    private TableColumn tblColDeleteVMArguments;


    @FXML
    private Button btnNewProgramArguments;
    @FXML
    private TableView<Pair<String, String>> tblVMArguments;
    @FXML
    private TableView<Pair<String, String>> tblProgramArguments;


    @FXML
    private TableColumn<Pair<String, String>, String> tblColKeyVMArguments;


    @FXML
    private TableColumn<Pair<String, String>, String> tblColValueProgramArgs;

    @FXML
    private TableColumn<Pair<String, String>, String> tblColValueVMArguments;

    @FXML
    private TableColumn<Pair<String, String>, String> tblColKeyProgramArgs;


    @FXML
    private TextArea txtGeneratedScript;

    @FXML
    void initialize() {
        console = new Console(consoleTextArea);
        initJobTree();
        initProjects();


        initJobTables();
    }

    private void initJobTables() {
        List<Pair<String, String>> pair = new ArrayList<Pair<String, String>>();
        initTblColumns();
        initTable(tblVMArguments, pair);
        initTable(tblProgramArguments, pair);
    }

    private void initTable(TableView<Pair<String, String>> tableView, List<Pair<String, String>> pairs) {

        ObservableList<Pair<String, String>> data = FXCollections.observableArrayList(pairs);
        tableView.setItems(data);
    }

    private void initTblColumns() {
        tblColKeyVMArguments.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getKey()));
        tblColValueVMArguments.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getValue()));
        tblColKeyProgramArgs.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getKey()));
        tblColValueProgramArgs.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getValue()));
        tblColDeleteProgramArgs.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Record, Boolean>,
                        ObservableValue<Boolean>>() {

                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Record, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });

        //Adding the Button to the cell
        tblColDeleteProgramArgs.setCellFactory(
                p -> new ButtonCell(tblProgramArguments.getItems()));
        tblColDeleteVMArguments.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Record, Boolean>,
                        ObservableValue<Boolean>>() {

                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Record, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });

        //Adding the Button to the cell
        tblColDeleteVMArguments.setCellFactory(
                p -> new ButtonCell(tblVMArguments.getItems()));

    }


    private void initProjects() {
        ProjectDAO dao = new ProjectDAO();
        List<String> projectNames = null;
        try {
            projectNames = dao.getAll().stream().map(f -> f.getProjectName()).collect(Collectors.toList());
            cmbProjects.setItems(FXCollections.observableList(projectNames));
            TemplateDAO tempDAO = new TemplateDAO();
            List<String> templateNames = tempDAO.getAll().stream().map(f -> f.getTemplateName()).collect(Collectors.toList());
            cmbTemplates.setItems(FXCollections.observableList(templateNames));
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }

    }

    private void initJobTree() {
        rootNode = new NavigatorTreeContent().newRootNode();

        try {
            for (Project project : new ProjectDAO().getAll()) {
                TreeItem<NavigatorTreeContent> projectNode = new NavigatorTreeContent().newProjectNode(project.getProjectName());
                for (Job job : new JobDAO().getJobs(project.getProjectName())) {

                    TreeItem<NavigatorTreeContent> jobNode = new NavigatorTreeContent().newJobNode((job.getMainClassName()));
                    projectNode.getChildren().add(jobNode);
                }
                rootNode.getChildren().add(projectNode);
            }
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }

        navigatorTree.setRoot(rootNode);
        navigatorTree.setCellFactory(new Callback<TreeView<NavigatorTreeContent>, TreeCell<NavigatorTreeContent>>() {
            @Override
            public TreeCell<NavigatorTreeContent> call(TreeView<NavigatorTreeContent> p) {
                return new TreeCellImpl(cmbProjects, cmbTemplates, txtMainClass, tblVMArguments, tblProgramArguments, console);
            }
        });

    }

    @FXML
    private TreeView<NavigatorTreeContent> navigatorTree;

    @FXML
    private BorderPane mainScene;

    @FXML
    private MenuBar mainMenuBar;


    @FXML
    private TextArea txtTemplateScript;

    @FXML
    void close(ActionEvent event) {


        Stage stage = (Stage) mainMenuBar.getScene().getWindow();
        stage.close();

    }

    public void newJob(ActionEvent actionEvent) throws IOException {

        txtMainClass.setDisable(false);
        txtMainClass.setText("");
        cmbProjects.getSelectionModel().clearSelection();
        cmbTemplates.getSelectionModel().clearSelection();
        cmbProjects.setDisable(false);
        initJobTables();
    }


    public void newProject(ActionEvent actionEvent) {

        ProjectDialog dialog = new ProjectDialog();
        Optional<Project> result = dialog.showAndWait();

        result.ifPresent(project -> {
            ProjectDAO dao = new ProjectDAO();
            try {
                dao.createProject(project);
                console.appendToConsole("created " + project.getProjectName() + " project");
                navigatorTree.getRoot().getChildren().add(new NavigatorTreeContent().newProjectNode(project.getProjectName()));
                cmbProjects.getItems().add(project.getProjectName());
            } catch (IOException | HadoopRunnerException e) {
                console.appendToConsole(e);
            }
        });
    }


    public void newVMArguments(ActionEvent actionEvent) {
        addToTable(tblVMArguments);

    }

    private void addToTable(TableView<Pair<String, String>> tableView) {
        KeyValueDialog dialog = new KeyValueDialog();
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            ObservableList<Pair<String, String>> pairs = tableView.getItems();
            pairs.add(pair);
            tableView.setItems(pairs);
        });

    }

    public void newProgramArguments(ActionEvent actionEvent) {
        addToTable(tblProgramArguments);
    }

    public void saveJob(ActionEvent actionEvent) {

        if (txtMainClass.isDisabled()) {
            updateJob();
        } else createJob();


    }

    private void updateJob() {
        Map<String, String> programArguments = tableToMap(tblProgramArguments);
        Map<String, String> vMArguments = tableToMap(tblVMArguments);
        JobDAO dao = new JobDAO();
        try {
            dao.saveJob(cmbProjects.getSelectionModel().getSelectedItem(),
                    cmbTemplates.getSelectionModel().getSelectedItem(),
                    txtMainClass.getText(), vMArguments, programArguments
            );
        } catch (IOException | HadoopRunnerException e) {
            console.appendToConsole(e);
        }
        console.appendToConsole("updated " + txtMainClass.getText() + " job");
    }

    private void createJob() {
        Map<String, String> programArguments = tableToMap(tblProgramArguments);
        Map<String, String> vMArguments = tableToMap(tblVMArguments);
        JobDAO dao = new JobDAO();
        String projectName = cmbProjects.getSelectionModel().getSelectedItem();
        String templateName = cmbTemplates.getSelectionModel().getSelectedItem();
        String mainClass =  txtMainClass.getText();
        try {

            dao.createJob(projectName,
                    templateName,mainClass
                  , vMArguments, programArguments
            );
        } catch (IOException | HadoopRunnerException e) {
            console.appendToConsole(e);
        }
        console.appendToConsole("created " + txtMainClass.getText() + " job");

        TreeItem<NavigatorTreeContent> projectNode = null;
        for(TreeItem<NavigatorTreeContent> node: rootNode.getChildren()){
            if(node.getValue().getFullName().equals(projectName)) projectNode= node;
        }

            TreeItem<NavigatorTreeContent> jobNode = new NavigatorTreeContent().newJobNode((mainClass));
            projectNode.getChildren().add(jobNode);



        txtMainClass.setDisable(true);
        cmbProjects.setDisable(true);
    }

    private Map<String, String> tableToMap(TableView<Pair<String, String>> tableview) {

        Map<String, String> map = new HashMap<String, String>();
        tableview.getItems().forEach(f -> map.put(f.getKey(), f.getValue()));
        return map;
    }

    public void newTemplate(ActionEvent actionEvent) {
        TemplateAddDialog dialog = new TemplateAddDialog();
        Optional<Template> result = dialog.showAndWait();
        result.ifPresent(template -> {
            TemplateDAO dao = new TemplateDAO();
            try {
                dao.createTemplate(template);
                console.appendToConsole("created " + template.getTemplateName() + " template");
                cmbTemplates.getItems().add(template.getTemplateName());
            } catch (IOException | HadoopRunnerException e) {
                console.appendToConsole(e.getMessage());
            }
        });
    }

    public void openTemplateScript(Event event) {
        TemplateDAO dao = new TemplateDAO();
        try {
            Template template = dao.getTemplate(cmbTemplates.getSelectionModel().getSelectedItem());
            if (template == null)
                txtTemplateScript.setText("No template selected");
            else
                txtTemplateScript.setText(template.getTemplate());
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }
    }

    public void openGeneratedScript(Event event) {

        try {
            ScriptGenerator generator = new ScriptGenerator(txtMainClass.getText(), cmbProjects.getSelectionModel().getSelectedItem());
            txtGeneratedScript.setText(generator.generateScriptText());
        } catch (IOException | HadoopRunnerException |NullPointerException e) {
            console.appendToConsole(e);
        }


    }

    public void editTemplate(ActionEvent actionEvent) {
        TemplateEditWizard wizard = new TemplateEditWizard(console);

    }

    public void newCluster(ActionEvent actionEvent) {
        ClusterDialog dialog = new ClusterDialog();
        Optional<Cluster> result = dialog.showAndWait();
        result.ifPresent(cluster -> {
            ClusterDAO dao = new ClusterDAO();
            try {
                dao.createCluster(cluster);
                console.appendToConsole("created " + cluster.getClusterName() + " cluster");
            } catch (IOException | HadoopRunnerException e) {
                console.appendToConsole(e.getMessage());
            }
        });
    }

    public void runCommand(ActionEvent actionEvent) {
        try {
            RunSSHCommandWizard wizard = new RunSSHCommandWizard(console);
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }
    }

    TemplateDAO tempDAO = new TemplateDAO();

    public void saveTemplate(ActionEvent actionEvent) {
        Template template = new Template();
        template.setTemplateName(cmbTemplates.getSelectionModel().getSelectedItem());
        template.setTemplate(txtTemplateScript.getText());
        try {
            tempDAO.saveTemplate(template);
            console.appendToConsole("template " + template.getTemplateName() + " saved");
        } catch (IOException | HadoopRunnerException e) {
            console.appendToConsole(e);
        }
    }
}
