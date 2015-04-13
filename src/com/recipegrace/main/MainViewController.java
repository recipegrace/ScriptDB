package com.recipegrace.main;

import com.recipegrace.core.HadoopRunnerException;
import com.recipegrace.core.Job;
import com.recipegrace.core.Project;
import com.recipegrace.db.JobDAO;
import com.recipegrace.db.ProjectDAO;
import com.recipegrace.dialogs.ProjectDialog;
import com.recipegrace.job.TextFieldTreeCellImpl;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Created by fjacob on 4/10/15.
 */
public class MainViewController {
    static Logger log = Logger.getLogger(MainViewController.class.getName());
    TreeItem<String> rootNode =
            new TreeItem<String>("All projects");

    @FXML
    private TabPane mainTab;

    @FXML
    private TextArea console;

    @FXML
    private ComboBox<String> cmbProjects;


    @FXML
    void initialize() throws FileNotFoundException {

        initJobTree();
        initProjects();
    }

    private void initProjects() throws FileNotFoundException {
        ProjectDAO dao = new ProjectDAO();
        List<String> projectNames= dao.getAll().stream().map(f-> f.getProjectName()).collect(Collectors.toList());
        cmbProjects.setItems( FXCollections.observableList(projectNames));
    }

    private void initJobTree() throws FileNotFoundException {
        for (Project project : new ProjectDAO().getAll()) {
            TreeItem<String> projectNode = new TreeItem<String>(project.getProjectName());
            for ( Job job : new JobDAO().getJobs(project.getProjectName())) {

                TreeItem<String> jobNode = new TreeItem<String>(job.getMainClassName());
                projectNode.getChildren().add(jobNode);
            }
            rootNode.getChildren().add(projectNode);
        }

        navigatorTree.setRoot(rootNode);
        navigatorTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>(){
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                return new TextFieldTreeCellImpl();
            }
        });

    }

    @FXML
    private TreeView<String> navigatorTree;

    @FXML
    private BorderPane mainScene;

    @FXML
    private MenuBar mainMenuBar;




    @FXML
    void close(ActionEvent event) {



        Stage stage = (Stage) mainMenuBar.getScene().getWindow();
        stage.close();

    }

    public void newJob(ActionEvent actionEvent) throws IOException {

    }

    public void treeMouseClicked(Event event) throws FileNotFoundException {
       TreeView<String> tree= (TreeView<String>)  event.getSource();
       TreeItem<String>  item =tree.getSelectionModel().getSelectedItem();
        if(item!=null && item.isLeaf()){
            showJobDetails(item.getValue(), item.getParent().getValue());
        }
    }

    private void showJobDetails(String mainClassName, String projectName) throws FileNotFoundException {
              JobDAO dao = new JobDAO();
             Job job= dao.getJob(projectName, mainClassName);
            if(job!=null){

            }
           else
                log.warning("job data is null");
    }

    public void newProject(ActionEvent actionEvent) {

        ProjectDialog dialog = new ProjectDialog();
        Optional<Project> result = dialog.showAndWait();
        result.ifPresent(project -> {
         ProjectDAO dao = new ProjectDAO();
            try {
                dao.createProject(project);
                appendToConsole("created " + project.getProjectName() + " project");
                navigatorTree.getRoot().getChildren().add(new TreeItem<String>(project.getProjectName()));
                cmbProjects.getItems().add(project.getProjectName());
            } catch (IOException |HadoopRunnerException e) {
                appendToConsole(e.getMessage());
            }
        });
    }

    private void appendToConsole(String msg){
        console.appendText(msg);
    }
}
