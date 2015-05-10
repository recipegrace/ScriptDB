package com.recipegrace.hadooprunner.wizard;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.Wizard.WizardPane;

public class WizardTest extends Application {

    private final ComboBox<StageStyle> styleCombobox = new ComboBox<>();
    private final ComboBox<Modality> modalityCombobox = new ComboBox<>();
    private final CheckBox cbUseBlocking = new CheckBox();
    private final CheckBox cbCloseDialogAutomatically = new CheckBox();
    private final CheckBox cbShowMasthead = new CheckBox();
    private final CheckBox cbSetOwner = new CheckBox();
    private final CheckBox cbCustomGraphic = new CheckBox();

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                showLinearWizard();
            }
        });


        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);

    }

    private void showLinearWizard() {
        // define pages to show

        Wizard wizard = new Wizard();
        wizard.setTitle("Linear Wizard");

        // --- page 1
        int row = 0;

        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        page1Grid.add(new Label("First Name:"), 0, row);
        TextField txFirstName = createTextField("firstName");
        // wizard.getValidationSupport().registerValidator(txFirstName, Validator.createEmptyValidator("First Name is mandatory"));
        page1Grid.add(txFirstName, 1, row++);

        page1Grid.add(new Label("Last Name:"), 0, row);
        TextField txLastName = createTextField("lastName");
        //wizard.getValidationSupport().registerValidator(txLastName, Validator.createEmptyValidator("Last Name is mandatory"));
        page1Grid.add(txLastName, 1, row);

        Wizard.WizardPane page1 = new Wizard.WizardPane();
        page1.setHeaderText("Please Enter Your Details");
        page1.setContent(page1Grid);

        // --- page 2
        final Wizard.WizardPane page2 = new Wizard.WizardPane() {
            @Override
            public void onEnteringPage(Wizard wizard) {
                String firstName = (String) wizard.getSettings().get("firstName");
                String lastName = (String) wizard.getSettings().get("lastName");

                setContentText("Welcome, " + firstName + " " + lastName + "! Let's add some newlines!\n\n\n\n\n\n\nHello World!");
            }
        };
        page2.setHeaderText("Thanks For Your Details!");

        // --- page 3
        WizardPane page3 = new WizardPane();
        page3.setHeaderText("Goodbye!");
        page3.setContentText("Page 3, with extra 'help' button!");

        ButtonType helpDialogButton = new ButtonType("Help", ButtonBar.ButtonData.HELP_2);
        page3.getButtonTypes().add(helpDialogButton);
        Button helpButton = (Button) page3.lookupButton(helpDialogButton);
        helpButton.addEventFilter(ActionEvent.ACTION, actionEvent -> {
            actionEvent.consume(); // stop hello.dialog from closing
            System.out.println("Help clicked!");
        });

        // create wizard
        wizard.setFlow(new Wizard.LinearFlow(page1, page2, page3));

        System.out.println("page1: " + page1);
        System.out.println("page2: " + page2);
        System.out.println("page3: " + page3);

        // show wizard and wait for response
        wizard.showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                System.out.println("Wizard finished, settings: " + wizard.getSettings());
            }
        });
    }

    private TextField createTextField(String id) {
        TextField textField = new TextField();
        textField.setId(id);
        GridPane.setHgrow(textField, Priority.ALWAYS);
        return textField;
    }

}