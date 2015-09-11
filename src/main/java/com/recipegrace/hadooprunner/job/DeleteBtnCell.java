package com.recipegrace.hadooprunner.job;

import com.recipegrace.hadooprunner.dialogs.Argument;
import com.sun.prism.impl.Disposer.Record;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

/**
 * Created by fjacob on 4/13/15.
 */
public class DeleteBtnCell extends TableCell<Record, Boolean> {


    private ObservableList<Argument> data;
    final Button cellButton = new Button("Delete");

    public DeleteBtnCell(ObservableList<Argument> data) {
        this.data = data;
        //Action when the button is pressed
        cellButton.setOnAction(t -> {
            // get Selected Item
            Argument currentPair = (Argument) DeleteBtnCell.this.getTableView().getItems().get(DeleteBtnCell.this.getIndex());
            //remove selected item from the table list
            data.remove(currentPair);

        });
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);
        if (!empty) {
            setGraphic(cellButton);
        } else {
            setGraphic(null);
        }
    }
}