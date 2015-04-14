package com.recipegrace.job;
import com.sun.prism.impl.Disposer.Record;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.util.Pair;

/**
 * Created by fjacob on 4/13/15.
 */
public class ButtonCell extends TableCell<Record, Boolean> {


    private ObservableList<Pair<String,String>> data ;
    final Button cellButton = new Button("Delete");
    public ButtonCell(ObservableList<Pair<String,String>> data){
        this.data=data;
        //Action when the button is pressed
        cellButton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                // get Selected Item
               Pair<String,String> currentPair = (Pair<String,String>) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                //remove selected item from the table list
                data.remove(currentPair);
                System.out.println("Hello world");
            }
        });
    }

    //Display button if the row is not empty
    @Override
    protected void updateItem(Boolean t, boolean empty) {
        super.updateItem(t, empty);
        if(!empty){
            setGraphic(cellButton);
        }
        else{
            setGraphic(null);
        }
    }
}