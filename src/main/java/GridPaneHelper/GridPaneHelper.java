//package GridPaneHelper;
//
//import javafx.collections.FXCollections;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.stage.FileChooser;
//
//import java.util.Optional;
//
//public class GridPaneHelper extends GridPane {
//
//    Dialog dialog;
//    GridPane grid;
//    ButtonType buttonTypeOk;
//    ButtonType buttonTypeCancel ;
//    boolean save;
//
//
//
//    public GridPaneHelper() {
//         dialog = new Dialog<>();
//         grid = new GridPane();
//    }
//
//    public boolean createGrid(String title, String HeaderText, String button1Text, String button2Text) {
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(10));
//        dialog.setTitle(title);
//        dialog.setHeaderText(HeaderText);
//        dialog.setResizable(true);
//        ScrollPane pane = new ScrollPane();
//        pane.setContent(grid);
//        dialog.getDialogPane().setContent(pane);
//
//
//        buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
//        buttonTypeCancel = new ButtonType(button2Text, ButtonBar.ButtonData.CANCEL_CLOSE);
//        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
//        Optional<?> result = dialog.showAndWait();
//
//
//        //setting the default button result value to that of buttonTypeCancel so that
//        //the X option on the dialog box behaves properly and closes
//        dialog.setResult(buttonTypeCancel);
//
//        if (result.get() == buttonTypeCancel) {
//            save = false;
//          return false;
//        }
//        else if (result.get() == buttonTypeOk){
//           save = true;
//            return  true;
//        }
//        return false;
//    }
//
//
//    public boolean create(String title ,String header)
//    {
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(10));
//        dialog.setTitle(title);
//        dialog.setHeaderText(header);
//        dialog.setResizable(true);
//
//        ScrollPane pane = new ScrollPane();
//        pane.setContent(grid);
//        dialog.getDialogPane().setContent(pane);
//
//
//        //buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
//        buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);
//        Optional<?> result = dialog.showAndWait();
//
//        //setting the default button result value to that of buttonTypeCancel so that
//        //the X option on the dialog box behaves properly and closes
//        dialog.setResult(buttonTypeCancel);
//
//        return false;
//    }
//
//
//    public void hideDialog()
//    {
//        this.dialog.hide();
//    }
//
//
//    public void setResizable(boolean value)
//    {
//        this.dialog.setResizable(value);
//    }
//
//    public  boolean showDialog() {
//
//        //setting the default button result value to that of buttonTypeCancel so that
//        //the X option on the dialog box behaves properly and closes
//        dialog.setResult(buttonTypeCancel);
//
//        Optional<?> result2 = dialog.showAndWait();
//        if (result2.get() == buttonTypeCancel) {
//            return false;
//        } else if (result2.get() == buttonTypeOk) {
//        }
//        return true;
//    }
//    public Label addLabel(String labelTitle, int row, int col){
//        Label label1 = new Label(labelTitle);
//        grid.add(label1, row, col);
//        return label1;
//    }
//    public TextField addTextField( int row, int col, int ... width){
//
//        TextField textField = new TextField();
//        if(width.length == 0) {
//            grid.add(textField, row, col);
//        }
//        else {
//            textField.setPrefWidth(width[0]);
//            grid.add(textField,row,col);
//        }
//         return textField;
//    }
//
//    public TextField addTextField( String defaultText, int row, int col,int ... width){
//
//        TextField textField = new TextField();
//        if(width.length == 0) {
//            grid.add(textField, row, col);
//        }
//        else {
//            textField.setPrefWidth(width[0]);
//            grid.add(textField,row,col);
//        }
//        textField.setText(defaultText);
//        return textField;
//    }
//
//    public TextArea addTextArea( int row, int col){
//        TextArea textArea = new TextArea();
//        grid.add(textArea, row, col);
//        return textArea;
//    }
//
//    public TextArea addTextArea( int row, int col, double width,double height){
//        TextArea textArea = new TextArea();
//        textArea.setPrefHeight(height);
//        textArea.setPrefWidth(width);
//        grid.add(textArea, row, col);
//        return textArea;
//    }
//    public ComboBox addDropDown(String[] list, int row,int col){
//        ComboBox comboBox =
//                new ComboBox(FXCollections
//                        .observableArrayList(list));
//        comboBox.getSelectionModel().selectFirst();
//        grid.add(comboBox, row, col);
//        return comboBox;
//    }
////    public ComboBox addDropDown(List<String> list, int row,int col){
////        ComboBox comboBox =
////                new ComboBox(FXCollections
////                        .observableArrayList());
////        comboBox.getSelectionModel().selectFirst();
////        grid.add(comboBox, row, col);
////        return comboBox;
////    }
//    public ComboBox addDropDownWithDefaultSelection(String[] list, int row,int col, String defaultSelect){
//        ComboBox comboBox =
//                new ComboBox(FXCollections
//                        .observableArrayList(list));
//        comboBox.getSelectionModel().selectFirst();
//        grid.add(comboBox, row, col);
//        comboBox.setValue(defaultSelect);
//        return comboBox;
//    }
//    public Button addButton(String buttonName, int row, int col){
//
//        Button button = new Button(buttonName);
//        grid.add(button, row,col);
//
//        return button;
//    }
//    public Button addButton(String buttonName, int row, int col, EventHandler eventHandler){
//
//        Button button = new Button(buttonName);
//        button.setOnAction(eventHandler);
//        grid.add(button, row,col);
//
//        return button;
//    }
//
//    public Button addButton(Button button, int row, int col)
//    {
//        grid.add(button,row,col);
//        return button;
//    }
//
//
//    public CheckBox addCheckBox(String buttonName, int row, int col, boolean setText, boolean ... setAdditional){
//
//       CheckBox checkBox = new CheckBox();
//       if (setText) {
//           checkBox.setText(buttonName);
//       }
//       if( setAdditional.length!=0){
//           checkBox.setDisable(setAdditional[0]);
//       }
//        grid.add(checkBox, row,col);
//       return checkBox;
//    }
//    public Spinner<Integer> addNumberSpinner(int initialValue, int to,int from, int row, int col){
//        Spinner<Integer> spinner = new Spinner<Integer>();
//        SpinnerValueFactory<Integer> valueFactory = //
//                new SpinnerValueFactory.IntegerSpinnerValueFactory(to, from, initialValue);
//
//        spinner.setValueFactory(valueFactory);
//
//        grid.add(spinner, row,col);
//
//        return spinner;
//    }
//
//
//    public void addSpinner(Spinner spinner,int row, int col)
//    {
//        grid.add(spinner,row,col);
//    }
//    public void addSlider(Slider slider,int row, int col)
//    {
//        grid.add(slider,row,col);
//    }
//    public void removeAllFromHelper(){
//        this.grid.getChildren().clear();
//    }
//    public void setPrefSize(double l,double w)
//    {
//        this.grid.setPrefSize(l,w);
//    }
//
//    public void addExistingTextField(TextField text, int row,int col){ grid.add(text,row,col);}
//    public void addExistingDropDownField(ComboBox dropdown, int row,int col){ grid.add(dropdown,row,col);}
//    public void closeDialog(){ this.dialog.close();}
//    public GridPane getGrid() { return grid; }
//    public void setGrid(GridPane grid) { this.grid = grid; }
//    public void clear() {this.grid.getChildren().clear();}
//    public boolean isSave() { return save; }
//    public void setSave(boolean save) { this.save = save; }
//}










package GridPaneHelper;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.util.Optional;
public class GridPaneHelper extends GridPane {
    Dialog dialog;
    GridPane grid;
    ButtonType buttonTypeOk;
    ButtonType buttonTypeCancel ;
    boolean save;
    public GridPaneHelper() {
        dialog = new Dialog<>();
        grid = new GridPane();
    }
    public boolean createGridWithoutScrollPane(String title, String HeaderText, String button1Text, String button2Text) {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        dialog.setTitle(title);
        dialog.setHeaderText(HeaderText);
        dialog.getDialogPane().setContent(grid);
        dialog.setResizable(true);
        grid.setAlignment(Pos.CENTER);
        buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
        buttonTypeCancel = new ButtonType(button2Text, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
        Optional<?> result = dialog.showAndWait();
        //setting the default button result value to that of buttonTypeCancel so that
        //the X option on the dialog box behaves properly and closes
        dialog.setResult(buttonTypeCancel);
        if (result.get() == buttonTypeCancel) {
            save = false;
            return false;
        }
        else if (result.get() == buttonTypeOk){
            save = true;
            return  true;
        }
        return false;
    }
    public boolean createGrid(String title, String HeaderText, String button1Text, String button2Text) {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        dialog.setTitle(title);
        dialog.setHeaderText(HeaderText);
        dialog.setResizable(true);


        //getting rid of scrollpane, dont need that.

        //ScrollPane pane = new ScrollPane();
        //pane.setContent(grid);
        //dialog.getDialogPane().setContent(pane);

        dialog.getDialogPane().setContent(grid);


        buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
        buttonTypeCancel = new ButtonType(button2Text, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeOk, buttonTypeCancel);
        Optional<?> result = dialog.showAndWait();
        //setting the default button result value to that of buttonTypeCancel so that
        //the X option on the dialog box behaves properly and closes
        dialog.setResult(buttonTypeCancel);
        if (result.get() == buttonTypeCancel) {
            save = false;
            return false;
        }
        else if (result.get() == buttonTypeOk){
            save = true;
            return  true;
        }
        return false;
    }


    public boolean create(String title ,String header, String cancelButtonName)
    {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        //dialog.setResizable(true);

        dialog.getDialogPane().setContent(grid);
        //buttonTypeOk = new ButtonType(button1Text, ButtonBar.ButtonData.OK_DONE);
        buttonTypeCancel = new ButtonType(cancelButtonName, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeCancel);

        Optional<?> result = dialog.showAndWait();

        //setting the default button result value to that of buttonTypeCancel so that
        //the X option on the dialog box behaves properly and closes
        dialog.setResult(buttonTypeCancel);
        return false;
    }



    /**
     * Creating
     * @param title
     * @param header
     * @return
     */
    public Stage createGridStage(String title ,String header)
    {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        Stage stage = new Stage();
        Scene scene = new Scene(grid);
        stage.setScene(scene);
        stage.show();

        return stage;
    }


    public void removeAllFromHelper(){
        this.grid.getChildren().clear();
    }
    public void hideDialog()
    {
        this.dialog.hide();
    }
    public void setResizable(boolean value)
    {
        this.dialog.setResizable(value);
    }
    public  boolean showDialog() {
        //setting the default button result value to that of buttonTypeCancel so that
        //the X option on the dialog box behaves properly and closes
        dialog.setResult(buttonTypeCancel);
        Optional<?> result2 = dialog.showAndWait();
        if (result2.get() == buttonTypeCancel) {
            return false;
        } else if (result2.get() == buttonTypeOk) {
        }
        return true;
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
        public TextField addTextField( String defaultText, int row, int col,int ... width){

        TextField textField = new TextField();
        if(width.length == 0) {
            grid.add(textField, row, col);
        }
        else {
            textField.setPrefWidth(width[0]);
            grid.add(textField,row,col);
        }
        textField.setText(defaultText);
        return textField;
    }
    public TextArea addTextArea( int row, int col){
        TextArea textArea = new TextArea();
        grid.add(textArea, row, col);
        return textArea;
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
    public ComboBox addDropDownWithDefaultSelection(String[] list, int row,int col, String defaultSelect){
        ComboBox comboBox =
                new ComboBox(FXCollections
                        .observableArrayList(list));
        comboBox.getSelectionModel().selectFirst();
        grid.add(comboBox, row, col);
        comboBox.setValue(defaultSelect);
        return comboBox;
    }
    public Button addButton(String buttonName, int col, int row){
        Button button = new Button(buttonName);
        grid.add(button, col,row);
        return button;
    }
    public Button addButton(String buttonName, int col, int row, EventHandler eventHandler){
        Button button = new Button(buttonName);
        button.setOnAction(eventHandler);
        grid.add(button, col,row);
        return button;
    }
    public Button addButton(Button button, int col, int row)
    {
        grid.add(button,col,row);
        return button;
    }
    public Button addButton(Button button, int col, int row, int colSpan, int rowSpan)
    {
        grid.add(button,col,row,colSpan,rowSpan);
        return button;
    }
    public void add(Node node, int col, int row, int colSpan, int rowSpan)
    {
        grid.add(node,col,row,colSpan,rowSpan);
    }
    public void addImage(ImageView image, int row, int col)
    {
        grid.add(image,row,col);
    }
    public void addImagePane(TilePane imagePane, int row, int col)
    {
        grid.add(imagePane,row,col);
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
    public void addSpinner(Spinner spinner,int row, int col)
    {
        grid.add(spinner,row,col);
    }
    public void setPrefSize(double l,double w)
    {
        this.grid.setPrefSize(l,w);
    }
    public void addExistingTextField(TextField text, int row,int col){ grid.add(text,row,col);}
    public void addExistingDropDownField(ComboBox dropdown, int row,int col){ grid.add(dropdown,row,col);}
    public void closeDialog(){ this.dialog.close();}
    public GridPane getGrid() { return grid; }
    public void setGrid(GridPane grid) { this.grid = grid; }
    public void clear() {this.grid.getChildren().clear();}
    public boolean isSave() { return save; }
    public void setSave(boolean save) { this.save = save; }
}