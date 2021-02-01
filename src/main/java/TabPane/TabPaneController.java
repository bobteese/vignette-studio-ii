/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package TabPane;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelper.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import SaveAsFiles.Images;
import Utility.Utility;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.ConnectPages;
import Vignette.Page.PageMenu;
import Vignette.Page.VignettePage;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/** @author Asmita Hari
 * This class is used to initilaze the left panel of list of images
 *  ,handles the drag and drop functionality and creates a new vignette page for each image dropped
 * **/
public class TabPaneController implements Initializable {

    @FXML
    ListView<String> imageListView; // list of image view for the left panel
    @FXML
    AnchorPane rightAnchorPane; // the right anchor pane where the images are dropped
    @FXML
    Tab pagesTab;
    @FXML
    Tab vignetteTab;
    @FXML
    TabPane tabPane;
    @FXML
    TextArea htmlSourceCode;
    @FXML
    Button addImage;
    @FXML
    ComboBox selectNextPage;
    @FXML
    ScrollPane scrollPane;
    @FXML
    ComboBox branchingType;
    @FXML
    ComboBox defaultNextPage;
    @FXML
    TextField numberOfAnswerChoice;

    SimpleStringProperty numberofAnswerChoiceValue = new SimpleStringProperty();
    // image sources
    private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream(ConstantVariables.IMAGE_RESOURCE_PATH));


    private Image[] listOfImages = {IMAGE_SINGLEPAGE};
    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();

    private List<String> pageNameList = new ArrayList<String>();
    private int firstPageCount = 0;

    private HashMap<String,VignettePage> pageViewList = Main.getVignette().getPageViewList();
    private HashMap<String, HTMLEditorContent> htmlEditorContent = new HashMap<>();
    private ConstantVariables variables = new ConstantVariables();
    HTMLEditorContent content;

    Button one;
    Button two;
    VignettePage pageOne;
    VignettePage pageTwo;
    Boolean isConnected= false;

    HashMap<String, ArrayList<Group>> listOfLineConnector;
    List<Images> imagesList = new ArrayList<>();
    List<String> bindPageList = new SimpleListProperty<>();

    /**
     * This method initialize the list when the controller loads
     * **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.getVignette().setController(this);

        numberOfAnswerChoice.textProperty().bindBidirectional(numberofAnswerChoiceValueProperty());
        //splitPane.setDividerPositions(0.3);
        listOfLineConnector = new HashMap<>();
        ObservableList<String> items = FXCollections.observableArrayList (
                ConstantVariables.QUESTION_PAGE_TYPE);
        imageListView.setItems(items);
        imageListView.setStyle("-fx-background-insets: 0 ;");
        imageListView.setMaxWidth(100);

        imageListView.setCellFactory(lv -> {
                    ListCell<String> cell = new ListCell<String>() {
                        private ImageView imageView = new ImageView();
                        @Override
                        public void updateItem(String name, boolean empty) {
                            super.updateItem(name, empty);
                            if (empty) {
                            } else {
                                 if(name.equals(ConstantVariables.QUESTION_PAGE_TYPE)) {
                                    imageView.setImage(listOfImages[0]);
                                }
                                setGraphic(imageView);
                            }
                        }
                    };
                    selectNextPage = new ComboBox(FXCollections
                            .observableArrayList(pageNameList));
                    cell.setOnDragDetected(event -> {
                        if (!cell.isEmpty()) {
                            Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putString(cell.getItem());
                            db.setContent(cc);
                            dragSource.set(cell);
                        }
                    });


                    return cell;
        });

        for(int i=0;i<ConstantVariables.PAGE_TYPE_ARRAY.length;i++){
            String str= ConstantVariables.PAGE_TYPE_ARRAY[i];
            ConstantVariables.PAGE_TYPE_LINK_MAP.put(str,ConstantVariables.PAGE_TYPE_SOURCE_ARRAY[i]);
        }

        branchingType.getItems().addAll("No Question", "Multiple-Choice (Radio button)", "Multiple-Select (Checkbox)");



    }
    /**
     * This method is called when an image is dropped into the anchor pane.
     * the method is called in resources/FXML tabs.fxml
     * ***/
    public void imageDropped(DragEvent event) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        System.out.println("imagre dropped");
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) { // if the dragboard has text accept it
            String imageType = db.getString();
            Image imageValue = null;
            double posX = event.getScreenX();
            double posY = event.getScreenY();
            String type=null;
            switch (imageType){ // checks for the type of the image and assigns the image source
                case  ConstantVariables.QUESTION_PAGE_TYPE:
                       imageValue  = listOfImages[0];
                       break;
            }

            ClipboardContent content = new ClipboardContent(); // put the type of the image in clipboard
            content.putString(imageType);
            db.setContent(content); // set the content in the dragboard
            ImageView droppedView = new ImageView(imageValue); // create a new image view

            VignettePage page = createNewPageDialog(false, null); /* when an image is dropped create a dialog pane to accept user
                                                               input for the page name */

            // add the dropped node to the anchor pane. Here a button is added with image and text.

            if(page != null ) {
                Button pageViewButton = createVignetteButton(page,droppedView,posX,posY,page.getPageType());
                success = true;
            }


        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }
    /**
     * This method is required to get the drag and drop to work as it accepts the incoming drag from another node
     * the method is called in resources/FXML tabs.fxml
     * ***/
    public void imageDragOver(DragEvent event) {
        /* show to the user that it is an actual gesture target */
        System.out.println("drag over");
        if (event.getDragboard().hasString() || event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    /**
     * This method creates a new dialog pane by accepting the input from the user which is the page name
     *
     * ***/
    public VignettePage createNewPageDialog(boolean pastePage, String pageType){
        GridPaneHelper  newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = firstPageCount > 0? true: false;

        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        ComboBox dropDownPageType = newPageDialog.addDropDown(ConstantVariables.listOfPageTypes,1,2);
        TextField pageName = newPageDialog.addTextField(1,3, 400);

        dropDownPageType.setOnAction(event -> {
            String value = (String) dropDownPageType.getValue();
            if(value.equals(ConstantVariables.LOGIN_PAGE_TYPE)) pageName.setText("login");
            if(value.equals(ConstantVariables.QUESTION_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.WHAT_LEARNED_PAGE)) pageName.setText("whatLearned");
            if(value.equals(ConstantVariables.RESPONSE_CORRECT_PAGE)) pageName.setText("q");
            if(value.equals(ConstantVariables.RESPONSE_INCORRECT_PAGE)) pageName.setText("q");
            if(value.equals(ConstantVariables.CREDIT_PAGE_TYPE)) pageName.setText("credits");
            if(value.equals(ConstantVariables.COMPLETION_PAGE_TYPE)) pageName.setText("Completion");
        });
        if(pastePage && pageType!=null){
            dropDownPageType.setValue(pageType);
            dropDownPageType.setDisable(true);
        }
        boolean cancelClicked = newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");
        if(!cancelClicked) return null;
        // if page ids exists  or if the text is empty
        boolean isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0 && !dropDownPageType.getValue().equals("Please select page type");
        while (!isValid){

            String message = pageName.getText().length() == 0? "Page id should not be empty":
                             pageNameList.contains(pageName.getText())?" All page id must be unique"
                                                              :dropDownPageType.getValue().equals("Please select page type")?"Select Page Type":"";
            DialogHelper helper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                                       message,false);
            if(helper.getOk()) { cancelClicked = newPageDialog.showDialog(); }
            isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0 && !dropDownPageType.getValue().equals("Please select page type");
            if(!cancelClicked) return null;

        }
        boolean check = checkBox.isSelected();
        if(check){ firstPageCount++;}
        VignettePage page = new VignettePage(pageName.getText().trim(), check, dropDownPageType.getValue().toString());
        pageNameList.add(pageName.getText());
        dropDownPageType.setDisable(false);
        return page;
    }
    /**
     * This method creates a vignette button on dropped
     * @Params page  creates a vignette page class for each page
     * @Param droppedView Image view
     * @param posX contains the mouse position
     * @param posY contains the mouse position
     * **/
    public Button createVignetteButton(VignettePage page, ImageView droppedView, double posX, double posY,String type){

        Button vignettePageButton = new Button(page.getPageName(), droppedView);

//        vignettePageButton.setLayoutX( posX); // setting the button position at the position where image is dropped
//        vignettePageButton.setLayoutY(posY);
        vignettePageButton.relocate(posX,posY);

        final double[] delatX = new double[1]; // used when the image is dragged to a different position
        final double[] deltaY = new double[1];
        vignettePageButton.setAlignment(Pos.CENTER); // center the text
        vignettePageButton.setTextAlignment(TextAlignment.CENTER);
        vignettePageButton.setContentDisplay(ContentDisplay.CENTER);
        vignettePageButton.setWrapText(true); // wrap to reduce white space

        //----- start of mouse event methods----------
        vignettePageButton.setOnMousePressed(mouseEvent -> {
                delatX[0] = vignettePageButton.getLayoutX() - mouseEvent.getSceneX();
                deltaY[0] = vignettePageButton.getLayoutY() - mouseEvent.getSceneY();
                vignettePageButton.setCursor(Cursor.MOVE);
        });
        vignettePageButton.setOnMouseReleased(mouseEvent -> {
                vignettePageButton.setCursor(Cursor.HAND);
        });
        vignettePageButton.setOnMouseDragged(mouseEvent -> {

            vignettePageButton.setLayoutX(mouseEvent.getSceneX() + delatX[0]); // set it to mew postion
            vignettePageButton.setLayoutY(mouseEvent.getSceneY() + deltaY[0] );
            page.setPosX(mouseEvent.getSceneX() + delatX[0]);
            page.setPosY(mouseEvent.getSceneY() + deltaY[0]);
            pageViewList.put(page.getPageName(),page);
            Main.getVignette().setPageViewList(pageViewList);

        });
        vignettePageButton.setOnMouseClicked(mouseEvent -> {
            String text = null;
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    openPage(page, type);

                }
            }
            if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                PageMenu pageMenu = new PageMenu(page, vignettePageButton, this);
                vignettePageButton.setContextMenu(pageMenu);
                pageMenu.setOnAction(e -> {
                    if(((MenuItem)e.getTarget()).getText().equals("Connect")){
                        isConnected = true;
                        one = vignettePageButton;
                        pageOne = page;
                    }
                });
            }
            else if(isConnected) {
                connectPages(mouseEvent);
            }
        });
        vignettePageButton.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.DELETE)){
                if(page.isFirstPage()) firstPageCount =0;
                this.pageNameList.remove(page.getPageName());


                DialogHelper confirmation = new DialogHelper(Alert.AlertType.CONFIRMATION,
                                                        "Delete Page",
                                                    null,
                                                   "Are you sure you want to delete this page?",
                                                   false);
                if(confirmation.getOk()) {

                    if(this.listOfLineConnector.containsKey(vignettePageButton.getText())) {
                        ArrayList<Group> connections = this.listOfLineConnector.get(vignettePageButton.getText());

                        connections.stream().forEach(connection-> {
                            this.rightAnchorPane.getChildren().remove(connection);
                        });

                    }
                    this.listOfLineConnector.remove(vignettePageButton.getText());
                    this.rightAnchorPane.getChildren().remove(vignettePageButton);
                    pageViewList.remove(vignettePageButton.getText());

                }
            }
        });
        this.rightAnchorPane.getChildren().add(vignettePageButton);
        page.setPosX(posX);
        page.setPosY(posY);
        pageViewList.put(page.getPageName(),page);
        Main.getInstance().addUndoStack(vignettePageButton);

        // -------end of mouse event methods-------
        return vignettePageButton;
    }

    public void openPage(VignettePage page, String type) {
        String text;
        pagesTab.setDisable(false);
        tabPane.getSelectionModel().select(pagesTab);
        if(htmlEditorContent.containsKey(page.getPageName())){
            content = htmlEditorContent.get(page.getPageName());
        }
        else{
            content = new HTMLEditorContent(htmlSourceCode, type, page,pageNameList, defaultNextPage,numberofAnswerChoiceValue);
            htmlEditorContent.put(page.getPageName(),content);
        }

        if(page.getPageData()==null){
            try {
                text =content.addTextToEditor();
                page.setPageData(text);
                pageViewList.put(page.getPageName(), page);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            text = content.setText(page.getPageData());
            page.setPageData(text);
            pageViewList.put(page.getPageName(), page);
        }
        Main.getVignette().setPageViewList(pageViewList);
    }

    private void connectPages(MouseEvent event) {
        two = ((Button) event.getSource());
        pageTwo = pageViewList.get(two.getText());

        if(two.getText().equals(one.getText())){
            DialogHelper helper = new DialogHelper(Alert.AlertType.ERROR,"Cannot Connect Pages",
                        null,"Pages May not connect to itself", false);
            isConnected = false;
        }
        else {
            if(this.listOfLineConnector!= null && this.listOfLineConnector.containsKey(pageOne.getPageName()) && pageOne.getConnectedTo()!=null){

                if(pageViewList.containsKey(pageOne.getPageName())){
                    VignettePage page = pageViewList.get(pageOne.getPageName());
                    String connectedTo = page.getConnectedTo();
                    if (connectedTo!=null)
                       rightAnchorPane.getChildren().remove(this.listOfLineConnector.get(connectedTo).get(0));
                    if(this.listOfLineConnector.containsKey(connectedTo)) {
                        ArrayList<Group> list = this.listOfLineConnector.get(connectedTo);
                        list.remove(0);
                        this.listOfLineConnector.replace(connectedTo,list);
                    }
                   if(this.listOfLineConnector.containsKey(pageOne.getPageName())) this.listOfLineConnector.remove(pageOne.getPageName());

                   pageOne.removeNextPages(connectedTo);
                   pageViewList.get(connectedTo).removeNextPages(pageOne.getPageName());


                }

            }
            pageOne.setConnectedTo(two.getText());
            Utility utility = new Utility();
            String text = null;
            if(pageOne.getPageData()!=null) {
                 text = utility.replaceNextPage(pageOne.getPageData(), pageOne);
            }
            if(pageOne.getPageData() != null) pageOne.setPageData(text);
            ConnectPages connect = new ConnectPages(one, two, rightAnchorPane, this.listOfLineConnector);
            Group grp = connect.connectSourceAndTarget();
            pageOne.setNextPages(two.getText(), grp);
            pageTwo.setNextPages(pageOne.getPageName(),grp);

            isConnected = false;

        }

    }

    public List<String> getPageNameList() {
        return pageNameList;
    }

    public void setPageNameList(List<String> pageNameList) {
        this.pageNameList = pageNameList;
    }

    public int getFirstPageCount() {
        return firstPageCount;
    }

    public void setFirstPageCount(int firstPageCount) {
        this.firstPageCount = firstPageCount;
    }

    public void addImage(ActionEvent actionEvent) {
        imagesList.add(content.addImageTag());
        Main.getVignette().setImagesList(imagesList);
    }

    public void NextPageAnswersButtonAction(ActionEvent actionEvent) {
        content.editNextPageAnswers();
    }

    public void pageSettingsButtonAction(ActionEvent actionEvent) {
       content.editPageSettings();
    }

    public void addVideoToEditor(ActionEvent actionEvent) {
        content.addVideo();
    }

    public void addInputFieldToEditor(ActionEvent actionEvent) {
        content.addInputFields(false);
    }
    public void addImageInputField(ActionEvent actionEvent) {
        content.addInputFields(true);
    }



    public  static class DraggableImage extends ImageView {
        private double mouseX ;
        private double mouseY ;
        public DraggableImage(Image image) {
            super(image);

            setOnMousePressed(event -> {
                mouseX = event.getSceneX() ;
                mouseY = event.getSceneY() ;
            });

            setOnMouseDragged(event -> {
                double deltaX = event.getSceneX()  ;
                double deltaY = event.getSceneY()  ;
                relocate(deltaX,  deltaY);
            });
        }


    }
    public HashMap<String, VignettePage> getPageViewList() {
        return pageViewList;
    }

    public void setPageViewList(HashMap<String, VignettePage> pageViewList) {
        this.pageViewList = pageViewList;
    }
    public AnchorPane getRightAnchorPane() { return rightAnchorPane; }

    public HashMap<String, ArrayList<Group>> getListOfLineConnector() {
        return listOfLineConnector;
    }

    public void setListOfLineConnector(HashMap<String, ArrayList<Group>> listOfLineConnector) {
        this.listOfLineConnector = listOfLineConnector;
    }
    public AnchorPane getAnchorPane(){
        return this.rightAnchorPane;
    }
    public Tab getPagesTab() { return pagesTab;  }
    public void setPagesTab(Tab pagesTab) { this.pagesTab = pagesTab; }
    public Tab getVignetteTab() { return vignetteTab; }

    public String getNumberofAnswerChoiceValue() { return numberofAnswerChoiceValue.get(); }
    public Property<String> numberofAnswerChoiceValueProperty() { return numberofAnswerChoiceValue; }
    public void setNumberofAnswerChoiceValue(String numberofAnswerChoiceValue) {
        this.numberofAnswerChoiceValue.set(numberofAnswerChoiceValue);
    }
    public void setVignetteTab(Tab vignetteTab) { this.vignetteTab = vignetteTab; }
    public TabPane getTabPane() { return tabPane; }
    public void setTabPane(TabPane tabPane) { this.tabPane = tabPane; }

}

