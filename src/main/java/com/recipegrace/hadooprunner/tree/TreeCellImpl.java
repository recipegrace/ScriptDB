package com.recipegrace.hadooprunner.tree;

import com.recipegrace.hadooprunner.core.Cluster;
import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Job;
import com.recipegrace.hadooprunner.core.Project;
import com.recipegrace.hadooprunner.db.ClusterDAO;
import com.recipegrace.hadooprunner.db.JobDAO;
import com.recipegrace.hadooprunner.db.ProjectDAO;
import com.recipegrace.hadooprunner.dialogs.JobDialog;
import com.recipegrace.hadooprunner.dialogs.ProjectDialog;
import com.recipegrace.hadooprunner.job.RemoteScriptRunner;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.template.ScriptGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.util.Pair;
import org.controlsfx.dialog.ProgressDialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Created by fjacob on 4/12/15.
 */
public class TreeCellImpl extends TreeCell<NavigatorTreeContent> {

    private ContextMenu addMenu = new ContextMenu();
    private ContextMenu runMenu = new ContextMenu();
    private ClusterDAO clusterDAO = new ClusterDAO();


    private boolean isRoot(NavigatorTreeContent navigatorTreeContent) {
      return navigatorTreeContent .getType().equals(NavigatorTreeContent.TreeItemType.root);
    }
    private void createMenu() throws FileNotFoundException {
        MenuItem addMenuItem = new MenuItem("Edit");
        addMenu.getItems().add(addMenuItem);
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                TreeItem<NavigatorTreeContent> item = getTreeItem();
                if (item.getParent() != null && isRoot(item.getParent().getValue())) {

                    editProject();

                } else if (item.getParent() != null && !isRoot(item.getParent().getValue())) {
                    try {
                        editJob(item);
                    } catch (HadoopRunnerException e) {
                        console.appendToConsole(e);
                    }


                }
            }
        };
        addMenuItem.setOnAction(eventHandler);
        MenuItem editMenuItem = new MenuItem("Edit");
        runMenu.getItems().add(editMenuItem);
        editMenuItem.setOnAction(eventHandler);

        List<Cluster> clusters = clusterDAO.getAll();
        Menu menuRun = new Menu("Run on");
        //final ToggleGroup groupRun = new ToggleGroup();
        for (Cluster cluster : clusters) {

            MenuItem itemEffect = new MenuItem(new com.recipegrace.hadooprunner.wizard.Pair("").shorten(cluster.getClusterName()));
            itemEffect.setUserData(cluster);
            //  itemEffect.setToggleGroup(groupRun);
            itemEffect.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    TreeItem<NavigatorTreeContent> item = getTreeItem();
                    TreeItem<NavigatorTreeContent> parentItem = item.getParent();
                    try {
                        String mainClass = item.getValue().getFullName();
                        String job = parentItem.getValue().getFullName();

                        String scriptPath = new ScriptGenerator(mainClass, job).generateScript();
                        Service<Void> service = new RemoteScriptRunner(console, cluster, scriptPath);

                        ProgressDialog progDiag = new ProgressDialog(service);
                        progDiag.setTitle("Running job");
                        progDiag.initOwner(null);
                        progDiag.setHeaderText("SSH job");
                        progDiag.initModality(Modality.WINDOW_MODAL);
                        service.start();
                    } catch (IOException | HadoopRunnerException e) {
                        console.appendToConsole(e);
                    }
                }
            });
            menuRun.getItems().add(itemEffect);

        }

        runMenu.getItems().add(menuRun);

    }


    private ObservableStringValue checkIfJob() {
        TreeItem<NavigatorTreeContent> item = getTreeItem();
        if (item != null && item.getParent() != null && !isRoot(item.getParent().getValue()))
            return new SimpleStringProperty("hola");
        return null;
    }



    private Console console;

    public TreeCellImpl(
                        Console console) {

        this.console = console;
        try {
            createMenu();
        } catch (FileNotFoundException e) {
            console.appendToConsole(e);
        }
    }


    private void editJob(TreeItem<NavigatorTreeContent> item) throws HadoopRunnerException {

        try {
            String jobID=getTreeItem().getValue().getId();
            Job currentJob = new JobDAO().getJob(jobID);
            JobDialog dialog = new JobDialog(console,currentJob);
            Optional<Job> result = dialog.showAndWait();
            result.ifPresent(job -> {

                try {
                    job.setId(jobID);
                    new JobDAO().saveJob(job);
                    TreeItem<NavigatorTreeContent> project = item.getParent();

                    for(TreeItem<NavigatorTreeContent> proj: project.getParent().getChildren()){
                        if(job.getProjectName().equals(proj.getValue().getFullName())){
                            proj.getChildren().add(new NavigatorTreeContent().newJobNode(job.getMainClassName(),job.getId()));
                        }
                    }
                    project .getChildren().remove(item);

                } catch (IOException | HadoopRunnerException e) {
                    console.appendToConsole(e);
                }
            });



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    private void editProject() {
        try {

            Project project = new ProjectDAO().getProject(getTreeItem().getValue().getFullName());
            ProjectDialog dialog = new ProjectDialog(project);
            Optional<Project> result = dialog.showAndWait();
            result.ifPresent(currentProject -> {
                ProjectDAO dao = new ProjectDAO();
                try {
                    dao.saveProject(currentProject);
                } catch (IOException | HadoopRunnerException e) {
                    console.appendToConsole(e);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(NavigatorTreeContent item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setGraphic(getTreeItem().getGraphic());
            if (getTreeItem() == null || getTreeItem().getParent() == null || getTreeItem().getParent().getValue() == null)
                return;
            if (getTreeItem().isLeaf() && !isRoot(getTreeItem().getParent().getValue())) {
                setContextMenu(runMenu);
            } else if (getTreeItem().getParent() != null) setContextMenu(addMenu);

        }
    }


    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
