package com.recipegrace.hadooprunner.main;

import com.recipegrace.hadooprunner.core.*;
import com.recipegrace.hadooprunner.db.ClusterDAO;
import com.recipegrace.hadooprunner.db.JobDAO;
import com.recipegrace.hadooprunner.db.ProjectDAO;
import com.recipegrace.hadooprunner.db.TemplateDAO;
import com.recipegrace.hadooprunner.dialogs.*;
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
    private TextArea consoleTextArea;

    private Console console;



    @FXML
    void initialize() {
        console = new Console(consoleTextArea);

        initJobTree();


    }



    private void initJobTree() {
        rootNode = new NavigatorTreeContent().newRootNode();

        try {
            for (Project project : new ProjectDAO().getAll()) {
                TreeItem<NavigatorTreeContent> projectNode = new NavigatorTreeContent().newProjectNode(project.getProjectName());
                for (Job job : new JobDAO().getJobs(project.getProjectName())) {

                    TreeItem<NavigatorTreeContent> jobNode = new NavigatorTreeContent().newJobNode(job.getMainClassName(), job.getId());
                    projectNode.getChildren().add(jobNode);
                }
                rootNode.getChildren().add(projectNode);
            }
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        } catch (IOException e) {
            console.appendToConsole(e);

        }

        navigatorTree.setRoot(rootNode);
        navigatorTree.setCellFactory(new Callback<TreeView<NavigatorTreeContent>, TreeCell<NavigatorTreeContent>>() {
            @Override
            public TreeCell<NavigatorTreeContent> call(TreeView<NavigatorTreeContent> p) {
                return new TreeCellImpl(console);
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




    public void newProject(ActionEvent actionEvent) {

        ProjectDialog dialog = new ProjectDialog();
        Optional<Project> result = dialog.showAndWait();

        result.ifPresent(project -> {
            ProjectDAO dao = new ProjectDAO();
            try {
                dao.createProject(project);
                console.appendToConsole("created " + project.getProjectName() + " project");
                navigatorTree.getRoot().getChildren().add(new NavigatorTreeContent().newProjectNode(project.getProjectName()));

            } catch (IOException | HadoopRunnerException e) {
                console.appendToConsole(e);
            }
        });
    }










    public void newTemplate(ActionEvent actionEvent) {
        TemplateAddDialog dialog = new TemplateAddDialog();
        Optional<Template> result = dialog.showAndWait();
        result.ifPresent(template -> {
            TemplateDAO dao = new TemplateDAO();
            try {
                dao.createTemplate(template);
                console.appendToConsole("created " + template.getTemplateName() + " template");

            } catch (IOException | HadoopRunnerException e) {
                console.appendToConsole(e.getMessage());
            }
        });
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




    public void newJob(ActionEvent actionEvent) throws FileNotFoundException {

        JobDialog dialog = new JobDialog(console);
        Optional<Job> result = dialog.showAndWait();
        result.ifPresent(job -> {

            try {
                new JobDAO().createJob(job);
                console.appendToConsole("created " + job.getMainClassName() + " job");

                updateTree(job.getProjectName(),job.getMainClassName());
            } catch (IOException | HadoopRunnerException e) {
                console.appendToConsole(e);
            }
        });
    }

    private void updateTree(String projectName, String mainClassName) throws FileNotFoundException, HadoopRunnerException {

        for( TreeItem<NavigatorTreeContent>  item :navigatorTree.getRoot().getChildren() ){
            if(item.getValue().getFullName().equals(projectName)){
                JobDAO jobDAO = new JobDAO();
                Job job =jobDAO.getJob(projectName, mainClassName);
                item.getChildren().add(new NavigatorTreeContent().newJobNode(job.getMainClassName(), job.getId()));
            }
        }
    }
}
