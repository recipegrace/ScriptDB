package com.recipegrace.hadooprunner.dialogs;

import com.recipegrace.hadooprunner.core.HadoopRunnerException;
import com.recipegrace.hadooprunner.core.Job;
import com.recipegrace.hadooprunner.db.ProjectDAO;
import com.recipegrace.hadooprunner.db.TemplateDAO;
import com.recipegrace.hadooprunner.job.DeleteBtnCell;
import com.recipegrace.hadooprunner.main.Console;
import com.recipegrace.hadooprunner.template.ScriptGenerator;
import com.sun.prism.impl.Disposer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by fjacob on 4/12/15.
 */
public class JobDialog extends Dialog<Job> {


    public static final String TEMPLATE = "Template";
    public static final String GENERATED = "Generated";
    private boolean editMode = false;
    private Console console;

    public JobDialog(Console console) throws FileNotFoundException {
        this.setTitle("New Project");
        this.setHeaderText("Create a new project!");
        this.console=console;
        createJobForm(new ArrayList<Argument>());
        validations();
        Platform.runLater(() -> txtMainClassName.requestFocus());
        convertResultToJob();


    }

    public JobDialog(Console console,Job job) throws FileNotFoundException {
        this.editMode = true;
        this.setTitle("Edit job");
        this.setHeaderText("Edit an existing job!");
        this.console=console;
        createJobForm(convertToArguments(job.getProgramArguments(), job.getVmArguments()));
        validations();
        this.txtMainClassName.setText(job.getMainClassName());
        cmbProjects.getSelectionModel().select(job.getProjectName());
        cmbTemplates.getSelectionModel().select(job.getTemplateName());

        Platform.runLater(() -> txtMainClassName.requestFocus());
        convertResultToJob();





    }

    private List<Argument> convertToArguments(Map<String, String> programArguments, Map<String, String> vmArguments) {

        List<Argument> arguments = new ArrayList<>();
        if(programArguments!=null)
        for(String key:programArguments.keySet()){
            arguments.add(new Argument(key,programArguments.get(key),false));
        }
        if(vmArguments!=null)
        for(String key:vmArguments.keySet()){
            arguments.add(new Argument(key,vmArguments.get(key),true));
        }
        return arguments;
    }

    private void convertResultToJob() {
        // Convert the result to a txtMainClassName-cmbProjects-pair when the login button is clicked.
        this.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                Job job = new Job();

                job.setMainClassName(txtMainClassName.getText());
                job.setProjectName(cmbProjects.getSelectionModel().getSelectedItem());
                job.setTemplateName(cmbTemplates.getSelectionModel().getSelectedItem());
                List<Argument> programArgs= tblArguments.getItems().stream().filter(f-> !f.isVM()).collect(Collectors.toList());
                job.setProgramArguments(convertToArgs(programArgs));
                List<Argument> vmArgs= tblArguments.getItems().stream().filter(f-> f.isVM()).collect(Collectors.toList());
                job.setVmArguments(convertToArgs(vmArgs));

                return job;
            }
            return null;
        });
    }

    private Map<String, String> convertToArgs(List<Argument> items) {
        Map<String,String> map = new HashMap<String,String>();
        for(Argument argument : items){
            map.put(argument.getKey(), argument.getValue());
        }
        return map;

    }

    private void validations() {
        // Enable/Disable login button depending on whether a txtMainClassName was entered.
        Node loginButton = this.getDialogPane().lookupButton(buttonType);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        txtMainClassName.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

    }

    private ButtonType buttonType;
    private TextField txtMainClassName;

    private ComboBox<String> cmbProjects;
    private ComboBox<String> cmbTemplates;
    private TableView<Argument> tblArguments =new TableView<Argument> ();
    private Button btnAddArgument = new Button("Add");


    private TableColumn<Argument, Boolean> tblColIsVM=new  TableColumn<Argument, Boolean>();
    private TableColumn<Argument, String> tblColValue =new  TableColumn<Argument, String>();
    private TableColumn<Argument, String> tblColKey =new  TableColumn<Argument, String>();
    private TableColumn tblColDelete =new TableColumn<Argument, String>();





    private void createJobForm(List<Argument> argumentList) throws FileNotFoundException {
        // Set the button types.
        if (editMode)
            buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        else
            buttonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

// Create the txtMainClassName and cmbProjects labels and fields.
        GridPane grid = createGridPane(argumentList);


        TabPane tabPane = new TabPane();
        Tab tab1 = new Tab();
        tab1.setText("Job");
        tab1.setContent(grid);

        Tab tab2 = new Tab();
        tab2.setText(TEMPLATE);
        tab2.setContent(createTemplateGridPane());


        Tab tab3 = new Tab();
        tab3.setContent(createGeneratedGridPane());
        tab3.setText(GENERATED);



        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);

        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (ov, t, t1) -> {
                    if (t1.getText().equals(GENERATED)) {
                        String mainClass = txtMainClassName.getText();
                        String projectName =cmbProjects.getSelectionModel().getSelectedItem();
                        if (mainClass != null && mainClass.length() > 0 &&
                                projectName != null && projectName.length() > 0 ) {
                            try {
                                ScriptGenerator generator = new ScriptGenerator(mainClass,projectName);
                                txtGenerated.setText(generator.generateScriptText());
                            } catch (FileNotFoundException e) {
                                console.appendToConsole(e);
                            } catch (HadoopRunnerException e) {
                                console.appendToConsole(e);
                            } catch (IOException e) {
                                console.appendToConsole(e);
                            }
                        }

                    } else if (t1.getText().equals(TEMPLATE)) {
                        String selectedTemplate = cmbTemplates.getSelectionModel().getSelectedItem();
                        if (selectedTemplate != null && selectedTemplate.length() > 0) {
                            TemplateDAO dao = new TemplateDAO();
                            try {
                                txtTemplate.setText(dao.getTemplate(selectedTemplate).getTemplate());
                            } catch (FileNotFoundException e) {
                                console.appendToConsole(e);
                            }
                        }
                    }
                }
        );

        this.getDialogPane().setContent(tabPane);

    }

    private GridPane createGridPane(List<Argument> argumentList) throws FileNotFoundException {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 30, 10, 10));
        txtMainClassName = new TextField();
        initTblColumns(argumentList);
        txtMainClassName.setPromptText("Project Name");
        cmbProjects = createProjectCombo();
        cmbTemplates =createTemplateCombo();
        btnAddArgument.setOnAction(e -> {
            ArgumentDialog dialog = new ArgumentDialog();
            Optional<Argument> result = dialog.showAndWait();
            result.ifPresent(argument -> {
                ObservableList<Argument> arguments = tblArguments.getItems();
                arguments.add(argument);
                tblArguments.setItems(arguments);
            });
        });
        grid.add(new Label("Main class:"), 0, 0);
        grid.add(txtMainClassName, 1, 0);
        grid.add(new Label("Project "), 0, 1);
        grid.add(cmbProjects, 1, 1);
        grid.add(new Label("Template "), 0, 2);
        grid.add(cmbTemplates, 1, 2);
        grid.add(new Label("Arguments "), 0, 3);
        grid.add(tblArguments, 1, 3);
        grid.add(btnAddArgument, 2, 3);
        return grid;
    }
    private  TextArea txtTemplate = new TextArea();
    private  TextArea txtGenerated = new TextArea();
    private GridPane createTemplateGridPane() throws FileNotFoundException {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 30, 10, 10));


        grid.add(txtTemplate, 0, 0);

        return grid;
    }
    private GridPane createGeneratedGridPane() throws FileNotFoundException {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 30, 10, 10));

       txtGenerated.setEditable(false);

        grid.add(txtGenerated, 0, 0);

        return grid;
    }


    private ComboBox<String> createTemplateCombo() throws FileNotFoundException {
        ComboBox<String> cmbTemplates = new ComboBox<>();

        List<String> templateNames = new TemplateDAO().getAll().stream().map(f -> f.getTemplateName()).collect(Collectors.toList());
        cmbTemplates.setItems(FXCollections.observableList(templateNames));
        return cmbTemplates;
    }

    private ComboBox<String> createProjectCombo() throws FileNotFoundException {
        ComboBox<String> cmbProjects = new ComboBox<String>();

        List<String> projectNames = new ProjectDAO().getAll().stream().map(f -> f.getProjectName()).collect(Collectors.toList());
        cmbProjects.setItems(FXCollections.observableList(projectNames));
        return cmbProjects;
    }


    private void initTblColumns(List<Argument> input) {

        tblColKey.setText("Key");
        tblColValue.setText("Value");
        tblColIsVM.setText("Is VM?");
        tblColKey.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getKey()));
        tblColValue.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().getValue()));
        tblColIsVM.setCellValueFactory(f -> new SimpleBooleanProperty(f.getValue().isVM()));
        tblColDelete.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>,
                        ObservableValue<Boolean>>() {

                    @Override
                    public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Disposer.Record, Boolean> p) {
                        return new SimpleBooleanProperty(p.getValue() != null);
                    }
                });


        //Adding the Button to the cell
        tblColDelete.setCellFactory(
                p -> new DeleteBtnCell(tblArguments.getItems()));

        ObservableList<Argument> data = FXCollections.observableArrayList(input);
        tblArguments.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblArguments.getColumns().add(tblColKey);
        tblArguments.getColumns().add(tblColValue);
        tblArguments.getColumns().add(tblColIsVM);
        tblArguments.getColumns().add(tblColDelete);

        tblArguments.setItems(data);


    }

}
