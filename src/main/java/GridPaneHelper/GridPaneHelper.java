package GridPaneHelper;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class GridPaneHelper extends GridPane {

    Dialog dialog;
    GridPane grid;



    public GridPaneHelper() {
         dialog = new Dialog<>();
         grid = new GridPane();
    }

    public boolean createGrid(String title, String HeaderText, String button1Text, String button2Text) {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        dialog.setTitle(title);
        dialog.setHeaderText(HeaderText);
        dialog.setResizable(true);
        ScrollPane pane = new ScrollPane();
        pane.setContent(grid);
        dialog.getDialogPane().setContent(pane);


        ButtonType buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType(button2Text, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);

        Optional<?> result = dialog.showAndWait();




        if (result.get() == buttonTypeCancel) {
          return false;
        }
        else if (result.get() == buttonTypeOk){

            return  true;
        }
        return false;
    }
    public  void showDialog(){
        dialog.showAndWait();
    }
    public Label addLabel(String labelTitle, int row, int col){
        Label label1 = new Label(labelTitle);
        grid.add(label1, row, col);
        return label1;
    }
    public TextField addTextField( int row, int col, int ... width){

        TextField textField = new TextField();
        if(width.length == 0) {
            grid.add(textField, row, col);
        }
        else {
            textField.setPrefWidth(width[0]);
            grid.add(textField,row,col);
        }
         return textField;
    }
    public TextArea addTextArea( int row, int col, double width,double height){
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(height);
        textArea.setPrefWidth(width);
        grid.add(textArea, row, col);
        return textArea;
    }
    public ComboBox addDropDown(String[] list, int row,int col){
        ComboBox comboBox =
                new ComboBox(FXCollections
                        .observableArrayList(list));
        comboBox.getSelectionModel().selectFirst();
        grid.add(comboBox, row, col);
        return comboBox;
    }
    public Button addButton(String buttonName, int row, int col){

        Button button = new Button(buttonName);
        grid.add(button, row,col);

        return button;
    }
    public Button addButton(String buttonName, int row, int col, EventHandler eventHandler){

        Button button = new Button(buttonName);
        button.setOnAction(eventHandler);
        grid.add(button, row,col);

        return button;
    }
    public CheckBox addCheckBox(String buttonName, int row, int col, boolean setText, boolean ... setAdditional){

       CheckBox checkBox = new CheckBox();
       if (setText) {
           checkBox.setText(buttonName);
       }
       if( setAdditional.length!=0){
           checkBox.setDisable(setAdditional[0]);
       }
        grid.add(checkBox, row,col);
       return checkBox;
    }
    public Spinner<Integer> addNumberSpinner(int initialValue, int to,int from, int row, int col){
        Spinner<Integer> spinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(to, from, initialValue);

        spinner.setValueFactory(valueFactory);

        grid.add(spinner, row,col);

        return spinner;

    }
    public void addExistingTextField(TextField text, int row,int col){ grid.add(text,row,col);}
    public void addExistingDropDownField(ComboBox dropdown, int row,int col){ grid.add(dropdown,row,col);}
    public void closeDialog(){ this.dialog.close();}
    public GridPane getGrid() { return grid; }
    public void setGrid(GridPane grid) { this.grid = grid; }


}
