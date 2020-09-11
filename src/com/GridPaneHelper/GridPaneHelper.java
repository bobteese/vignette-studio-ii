package com.GridPaneHelper;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

public class GridPaneHelper<T> extends GridPane {

    Dialog<T> dialog;
    GridPane grid;

    public GridPaneHelper() {
         dialog = new Dialog<>();
         grid = new GridPane();
    }

    public void createGrid(String title, String HeaderText, String button1Text, String button2Text) {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        dialog.setTitle(title);
        dialog.setHeaderText(HeaderText);
        dialog.setResizable(true);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancle = new ButtonType(button2Text, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancle);



        Optional<T> result = dialog.showAndWait();

        if (result.isPresent()) {

            System.out.println("REs");
        }
    }
    public void addLabel(String labelTitle, int row, int col){
        Label label1 = new Label(labelTitle);
        grid.add(label1, row, col);
    }
    public void addTextField( int row, int col){
        TextField textField = new TextField();
        grid.add(textField, row, col);
    }
    public void addTextArea( int row, int col, double width,double height){
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(height);
        textArea.setPrefWidth(width);
        grid.add(textArea, row, col);
    }
    public void addDropDown(String[] list, int row,int col){
        ComboBox comboBox =
                new ComboBox(FXCollections
                        .observableArrayList(list));
        comboBox.getSelectionModel().selectFirst();
        grid.add(comboBox, row, col);
    }
    public Button addButton(String buttonName, int row, int col){

        Button button = new Button(buttonName);
        grid.add(button, row,col);

        return button;
    }
    public void addCheckBox(String buttonName, int row, int col){

       CheckBox checkBox = new CheckBox();
        grid.add(checkBox, row,col);
    }
    public void addNumberSpinner(int initialValue, int to,int from, int row, int col){
        Spinner<Integer> spinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(to, from, initialValue);

        spinner.setValueFactory(valueFactory);

        grid.add(spinner, row,col);

    }
    public void closeDialog(){ this.dialog.close();}
}
