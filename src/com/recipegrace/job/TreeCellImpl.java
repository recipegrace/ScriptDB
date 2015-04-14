package com.recipegrace.job;

import com.recipegrace.core.HadoopRunnerException;
import com.recipegrace.core.Job;
import com.recipegrace.core.Project;
import com.recipegrace.db.JobDAO;
import com.recipegrace.db.ProjectDAO;
import com.recipegrace.dialogs.ProjectDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static  com.recipegrace.main.MainViewController.ROOT_TREE_NODE;
/**
 * Created by fjacob on 4/12/15.
 */
public class TreeCellImpl extends TreeCell<String> {

    private ContextMenu addMenu = new ContextMenu();
    private ContextMenu runMenu = new ContextMenu();

    private void createMenu() {
        MenuItem addMenuItem = new MenuItem("Edit");
        addMenu.getItems().add(addMenuItem);
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                TreeItem<String> item = getTreeItem();
                if (item.getParent() != null && item.getParent().getValue().equals(ROOT_TREE_NODE)) {

                    editProject();

                } else if (item.getParent() != null && !item.getParent().getValue().equals(ROOT_TREE_NODE)) {
                    editJob();
                }
            }
        };
        addMenuItem.setOnAction(eventHandler);

        MenuItem runMenuItem = new MenuItem("Run");
        runMenu.getItems().add(runMenuItem);
        MenuItem editMenuItem = new MenuItem("Edit");
        runMenu.getItems().add(editMenuItem);
        editMenuItem.setOnAction(eventHandler);
        runMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<String> item = getTreeItem();
                TreeItem<String>  parentItem = item.getParent();
                try {
                    new JobRunner(console).run(item.getValue(), parentItem.getValue());
                } catch (IOException|InterruptedException e) {
                    console.appendText("\nERROR:" + e.getMessage());
                }
            }
        });
    }


    private ObservableStringValue checkIfJob() {
        TreeItem<String> item = getTreeItem();
        if (item!=null && item.getParent() != null && !item.getParent().getValue().equals(ROOT_TREE_NODE))
           return new SimpleStringProperty("hola");
        return null;
    }


    private ComboBox<String> cmbProjects;
    private ComboBox<String> cmbTemplates;
    private  TableView<Pair<String, String>> tblVMArguments;
    private TableView<Pair<String, String>> tblProgramArguments;
    private TextField txtMainClass;
    private TextArea console;

    public TreeCellImpl(ComboBox<String> cmbProjects,
                        ComboBox<String> cmbTemplates,
                        TextField txtMainClass,
                        TableView<Pair<String, String>> tblVMArguments,
                        TableView<Pair<String, String>> tblProgramArguments,
                        TextArea console) {
       this.cmbProjects=cmbProjects;
        this.cmbTemplates=cmbTemplates;
        this.tblProgramArguments=tblProgramArguments;
        this.tblVMArguments=tblVMArguments;
        this.txtMainClass=txtMainClass;
        this.console= console;
        createMenu();
    }


    private void editJob() {
        String mainClass = getTreeItem().getValue();
        String projectName = getTreeItem().getParent().getValue();
        try {
            Job job = new JobDAO().getJob(projectName, mainClass);
            cmbProjects.getSelectionModel().select(job.getProjectName());
            cmbProjects.setDisable(true);
            txtMainClass.setText(job.getMainClassName());
            txtMainClass.setDisable(true);
            cmbTemplates.getSelectionModel().select(job.getTemplateName());
            List<Pair<String, String>> vMPairs = getPairs(job.getVmArguments());
            tblVMArguments.setItems(FXCollections.observableArrayList(vMPairs));
            List<Pair<String, String>> programPairs = getPairs(job.getProgramArguments());
            tblProgramArguments.setItems(FXCollections.observableArrayList(programPairs));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private List<Pair<String, String>> getPairs(Map<String, String> map) {
        return map.keySet().stream().map(f-> new Pair<String,String>(f, map.get(f))).collect(Collectors.toList());
    }

    private void editProject() {
        try {

            Project project = new ProjectDAO().getProject(getTreeItem().getValue());
            ProjectDialog dialog = new ProjectDialog(project);
            Optional<Project> result = dialog.showAndWait();
            result.ifPresent(currentProject -> {
                ProjectDAO dao = new ProjectDAO();
                try {
                    dao.saveProject(currentProject);
                } catch (IOException | HadoopRunnerException e) {
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
              if(getTreeItem()==null|| getTreeItem().getParent()==null || getTreeItem().getParent().getValue()==null) return;
            if(getTreeItem().isLeaf() && ! getTreeItem().getParent().getValue().equals(ROOT_TREE_NODE)){
                   setContextMenu(runMenu);
               }
               else if(getTreeItem().getParent()!=null)setContextMenu(addMenu);

                //if (
                 //       !getTreeItem().isLeaf()&&getTreeItem().getParent()!= null
                  //      ){
                    //setContextMenu(addMenu);
               // }
            }
    }


    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
