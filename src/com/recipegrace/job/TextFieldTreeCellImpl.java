package com.recipegrace.job;

import com.recipegrace.core.HadoopRunnerException;
import com.recipegrace.core.Project;
import com.recipegrace.db.ProjectDAO;
import com.recipegrace.dialogs.ProjectDialog;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by fjacob on 4/12/15.
 */
public class TextFieldTreeCellImpl extends TreeCell<String> {
    private ContextMenu addMenu = new ContextMenu();

    public TextFieldTreeCellImpl() {
        MenuItem addMenuItem = new MenuItem("Edit");
        addMenu.getItems().add(addMenuItem);
        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Project project = new ProjectDAO().getProject(getTreeItem().getValue());
                    ProjectDialog dialog = new ProjectDialog(project);
                    Optional<Project> result = dialog.showAndWait();
                    result.ifPresent(currentProject -> {
                        ProjectDAO dao = new ProjectDAO();
                        try {
                            dao.saveProject(currentProject);
                        } catch (IOException |HadoopRunnerException e) {
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
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
                //if (
                 //       !getTreeItem().isLeaf()&&getTreeItem().getParent()!= null
                  //      ){
                    setContextMenu(addMenu);
               // }
            }
    }


    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
