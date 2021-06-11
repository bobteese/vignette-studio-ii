/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package TabPane;

import Application.Main;
import ConstantVariables.ConstantVariables;
import ConstantVariables.BranchingConstants;
import DialogHelpers.DialogHelper;
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
import javafx.event.EventHandler;
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
import java.util.*;

/** @author Asmita Hari
 * This class is used to initilaze the left panel of list of images
 *  ,handles the drag and drop functionality and creates a new vignette page for each image dropped
 * **/
//public class TabPaneController implements Initializable  {
public class TabPaneController extends ContextMenu implements Initializable  {

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
    TextField numberOfAnswerChoice;
    @FXML
    Button nextPageAnswers;
    @FXML
    Label pageName;

    SimpleStringProperty numberofAnswerChoiceValue = new SimpleStringProperty();
    SimpleStringProperty branchingTypeProperty = new SimpleStringProperty();


    // image sources
    private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream(ConstantVariables.IMAGE_RESOURCE_PATH));
    private final Image IMAGE_LOGINPAGE = new Image(getClass().getResourceAsStream(ConstantVariables.LOGIN_RESOURCE_PATH));
    private final Image IMAGE_PROBLEMPAGE = new Image(getClass().getResourceAsStream(ConstantVariables.PROBLEM_RESOURCE_PATH));
    private final Image IMAGE_QUESTIONPAGE = new Image(getClass().getResourceAsStream(ConstantVariables.QUESTION_RESOURCE_PATH));
    private final Image IMAGE_WHATLEARNEDPAGE = new Image(getClass().getResourceAsStream(ConstantVariables.WHATLEARNED_RESOURCE_PATH));
    private final Image IMAGE_RESPONSECORRECT = new Image(getClass().getResourceAsStream(ConstantVariables.RESPONSECORRECT_RESOURCE_PATH));
    private final Image IMAGE_RESPONSEINCORRECT = new Image(getClass().getResourceAsStream(ConstantVariables.RESPONSEINCORRECT_RESOURCE_PATH));
    private final Image IMAGE_CREDITS = new Image(getClass().getResourceAsStream(ConstantVariables.CREDITS_RESOURCE_PATH));
    private final Image IMAGE_COMPLETION = new Image(getClass().getResourceAsStream(ConstantVariables.COMPLETION_RESOURCE_PATH));
    private final Image IMAGE_CUSTOM = new Image(getClass().getResourceAsStream(ConstantVariables.CUSTOM_RESOURCE_PATH));
    private final Image IMAGE_PROBLEMSTATEMENT = new Image(getClass().getResourceAsStream(ConstantVariables.PROBLEMSTATEMENT_RESOURCE_PATH));

    HashMap<String, String> pageIds = new HashMap<>();
    HashMap<String, Image> imageMap = new HashMap<>();


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


    HashMap<String, Button> buttonPageMap = new HashMap<>();


    PageCreator creator = new PageCreator(this);
    Stack undoStack = new Stack();



    /**
     * This method initialize the list when the controller loads
     * **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.getVignette().setController(this);

        /**
         * Add right click functionality
         */
        RightClickMenu rightClickMenu = new RightClickMenu(creator);


        rightClickMenu.setAutoHide(true);
        rightAnchorPane.setOnMousePressed(new EventHandler<MouseEvent>(){

            @Override public void handle(MouseEvent event)
            {
                if(event.isSecondaryButtonDown())
                {

                    double posX=event.getX();
                    double posY=event.getY();

                    //this sets the disability in the undo/redo functionality in the right click menu
                    rightClickMenu.setUndoRedoDisability();
                    //setXY sets the position of the vignette page icon
                    rightClickMenu.setXY(posX,posY);
                    //getScreenX() and getScreenY() makes sure that the rightclick context menu appears in the right window
                    rightClickMenu.show(rightAnchorPane, event.getScreenX(), event.getScreenY());
                }
                else
                    rightClickMenu.hide();
            }
            });

        numberOfAnswerChoice.textProperty().bindBidirectional(numberofAnswerChoiceValueProperty());
        branchingType.valueProperty().bindBidirectional(branchingTypeProperty());
        //splitPane.setDividerPositions(0.3);
        listOfLineConnector = new HashMap<>();


        // hashmap with PageTypes as the key and the default PageId as the value
        pageIds.put(ConstantVariables.QUESTION_PAGE_TYPE, "q");
        pageIds.put(ConstantVariables.PROBLEM_PAGE_TYPE, "");
        pageIds.put(ConstantVariables.LOGIN_PAGE_TYPE, "login");
        pageIds.put(ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE, "q");
        pageIds.put(ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE, "q");
        pageIds.put(ConstantVariables.WHAT_LEARNED_PAGE_TYPE, "whatLearned");
        pageIds.put(ConstantVariables.CREDIT_PAGE_TYPE, "credits");
        pageIds.put(ConstantVariables.COMPLETION_PAGE_TYPE, "Completion");
        pageIds.put(ConstantVariables.CUSTOM_PAGE_TYPE, "");
        pageIds.put(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE,"");

        //----------------------------------------------------------------------


        //hashmap with PageTypes as the key and the Image associated with it as the value
        imageMap.put(ConstantVariables.LOGIN_PAGE_TYPE, IMAGE_LOGINPAGE);
        imageMap.put(ConstantVariables.QUESTION_PAGE_TYPE, IMAGE_QUESTIONPAGE);
        imageMap.put(ConstantVariables.PROBLEM_PAGE_TYPE, IMAGE_PROBLEMPAGE);
        imageMap.put(ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE, IMAGE_RESPONSECORRECT);
        imageMap.put(ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE, IMAGE_RESPONSEINCORRECT);
        imageMap.put(ConstantVariables.WHAT_LEARNED_PAGE_TYPE, IMAGE_WHATLEARNEDPAGE);
        imageMap.put(ConstantVariables.CREDIT_PAGE_TYPE, IMAGE_CREDITS);
        imageMap.put(ConstantVariables.COMPLETION_PAGE_TYPE, IMAGE_COMPLETION);
        imageMap.put(ConstantVariables.CUSTOM_PAGE_TYPE, IMAGE_CUSTOM);
        imageMap.put(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE,IMAGE_PROBLEMSTATEMENT);
        //-----------------------------------------------------------------------


        /**
         * In order to put images into the Page type image pane, first you have to identify the page types here.
         * ORDER IS IMPORTANT.
         * After mentioning it here, make changes in setCellFactory.
         */
        ObservableList<String> items = FXCollections.observableArrayList(ConstantVariables.LOGIN_PAGE_TYPE,
                ConstantVariables.PROBLEM_PAGE_TYPE,ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE, ConstantVariables.QUESTION_PAGE_TYPE,
                ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE, ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE,ConstantVariables.WHAT_LEARNED_PAGE_TYPE,
                ConstantVariables.CREDIT_PAGE_TYPE, ConstantVariables.COMPLETION_PAGE_TYPE, ConstantVariables.CUSTOM_PAGE_TYPE);

        imageListView.setItems(items);
        imageListView.setStyle("-fx-background-insets: 0 ;");
        imageListView.setMaxWidth(100);


        /**
         * An Image_ListView is created in which images are associated with each cell in the cellfactory
         * The cell is then set to detect drag movements from the list view to the right anchor pane
         *
         *
         */
        imageListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                private ImageView imageView = new ImageView();

                @Override
                public void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty) {
                    }
                    //THIS displays the images of the page types on the listView
                    imageView.setImage(imageMap.get(name));
                    setGraphic(imageView);
                }
            };

            selectNextPage = new ComboBox(FXCollections
                    .observableArrayList(pageNameList));



            //todo This is where all the dragging and dropping begins

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);


                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem());


                    System.out.println("Cell item= " + cell.getItem());
                    System.out.println("clipboard content = "+cc.toString());

                    db.setContent(cc);

                    //private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();
                    dragSource.set(cell);
                }
            });
            return cell;
        });

        for (int i = 0; i < ConstantVariables.PAGE_TYPE_ARRAY.length; i++) {
            String str = ConstantVariables.PAGE_TYPE_ARRAY[i];
            ConstantVariables.PAGE_TYPE_LINK_MAP.put(str, ConstantVariables.PAGE_TYPE_SOURCE_ARRAY[i]);
        }

        branchingType.getItems().addAll(BranchingConstants.NO_QUESTION, BranchingConstants.RADIO_QUESTION,
                BranchingConstants.CHECKBOX_QUESTION);


//        nextPageAnswers.disableProperty().bind(
//                numberOfAnswerChoice.textProperty().isEmpty()
//                        .or( branchingType.valueProperty().isNull() )
//                         );

    }

    /**
     * This method is called when an image is dropped into the anchor pane.
     * the method is called in resources/FXML tabs.fxml
     * ***/
    public void imageDropped(DragEvent event) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */

        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) { // if the dragboard has text accept it
            String imageType = db.getString();
            Image imageValue = null;
            double posX = event.getX();
            double posY = event.getY();
            String type=null;


            //todo this is the initial image drag and drop find out where the next one is.
            //System.out.println(posX+" "+posY);


            /**
             * When you drag and drop the page icon from the left, the following code decides what image is used for the
             * page after drag and dropping.
             */
            imageValue = imageMap.get(imageType);


            ClipboardContent content = new ClipboardContent(); // put the type of the image in clipboard
            content.putString(imageType);
            db.setContent(content); // set the content in the dragboard
            ImageView droppedView = new ImageView(imageValue); // create a new image view


           //VignettePage page = createPage(event);
           VignettePage page = creator.createPage(event);



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
        if (event.getDragboard().hasString() || event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
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
        buttonPageMap.put(page.getPageName(), vignettePageButton);

        vignettePageButton.relocate(posX,posY);

        final double[] deltaX = new double[1]; // used when the image is dragged to a different position
        final double[] deltaY = new double[1];
        vignettePageButton.setAlignment(Pos.CENTER); // center the text
        vignettePageButton.setTextAlignment(TextAlignment.CENTER);
        vignettePageButton.setContentDisplay(ContentDisplay.CENTER);
        vignettePageButton.setWrapText(true); // wrap to reduce white space



        //----- start of mouse event methods----------
        vignettePageButton.setOnMousePressed(mouseEvent -> {
            deltaX[0] = vignettePageButton.getLayoutX() - mouseEvent.getSceneX();
            deltaY[0] = vignettePageButton.getLayoutY() - mouseEvent.getSceneY();

            //initial locations
            double initialX = vignettePageButton.getLayoutX();
            double initialY = vignettePageButton.getLayoutY();

            vignettePageButton.setCursor(Cursor.MOVE);

            // deltaX and deltaY is whereever you click on the image.
            //System.out.println("Initial position= "+ deltaX[0]+"  "+deltaY[0]);

            //getLayoutX and Y is the exact initial location of the button
            System.out.println("Page initial button= "+ vignettePageButton.getLayoutX()+" "+vignettePageButton.getLayoutY());

        });

        //Visually changes the cursor to a hand on releasing the click
        vignettePageButton.setOnMouseReleased(mouseEvent -> {
            vignettePageButton.setCursor(Cursor.HAND);
        });


        vignettePageButton.setOnMouseDragged(mouseEvent -> {

            vignettePageButton.setLayoutX(mouseEvent.getSceneX() + deltaX[0]); // set it to mew postion
            vignettePageButton.setLayoutY(mouseEvent.getSceneY() + deltaY[0]);
            page.setPosX(mouseEvent.getSceneX() + deltaX[0]);
            page.setPosY(mouseEvent.getSceneY() + deltaY[0]);


            //  This is the final dropped event
            //  System.out.print(mouseEvent.getSceneX() + deltaX[0]);
            //  System.out.println(" "+mouseEvent.getSceneY() + deltaY[0]);


            // ---------------TODO------------------
            // Store the final moved location for the page button in the stack here



            //--------------------------------------------





            // todo I think this is where the page id is stored to check for duplicates
            // todo or its pagenamelist
            pageViewList.put(page.getPageName(),page);
            Main.getVignette().setPageViewList(pageViewList);
            //

        });

        // following opens the html page editor when you double click the page icon
        vignettePageButton.setOnMouseClicked(mouseEvent -> {
            String text = null;
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    openPage(page, type);

                }
            }

            // this is the code the deals with opening the right click menu
            if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                /*
                Creating the right click vignette page menu and adding it to the button representing the
                page.
                 */
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
//            else if(isConnected) {
//                connectPages(mouseEvent);
//                isConnected=false;
//            }
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



    //---------------- NEWLY ADDED FNS ----------------------------------


    public void move(VignettePage page,Button vignettePageButton, double posx, double posy)
    {
        vignettePageButton.setLayoutX(posx);
        vignettePageButton.setLayoutY(posy);

        page.setPosX(posx);
        page.setPosY(posy);
    }

    // -------------------------------------------------------------------


    public void openPage(VignettePage page, String type) {
        String text;
        pagesTab.setDisable(false);
        tabPane.getSelectionModel().select(pagesTab);


        if(htmlEditorContent.containsKey(page.getPageName())){
            content = htmlEditorContent.get(page.getPageName());

        }
        else{
            content = new HTMLEditorContent(htmlSourceCode,
                    type, page,
                    pageNameList,
                    branchingTypeProperty,
                    numberofAnswerChoiceValue,
                    pageName);
            htmlEditorContent.put(page.getPageName(),content);

        }
        // content.addDropDown();
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

        checkPageConnection(pageOne,pageTwo,one,two);

    }

    public boolean checkPageConnection(VignettePage pageOne, VignettePage pageTwo, Button one, Button two ) {
        //no self connections
        if(two.getText().equals(one.getText())){
            isConnected = false;
            return false;
        }

        else {
            //if not a first connection to pages
            if(this.listOfLineConnector!= null && this.listOfLineConnector.containsKey(pageOne.getPageName()) && pageOne.getConnectedTo()!=null){
                // if such page exists on the vignette pane
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
            isConnected = true;
            return true;
        }
    }

    //TODO I added these

    public HashMap getImageMap()
    {
        return this.imageMap;
    }

    public HashMap getPageIds() { return this.pageIds;}



    ///////////////////////////////






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
        content.editNextPageAnswers(branchingType.getSelectionModel().getSelectedItem().equals(BranchingConstants.NO_QUESTION));
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

    public void selectBranchingType(ActionEvent actionEvent) {
        String value = (String) branchingType.getSelectionModel().getSelectedItem();
        if(value.equals("No Question")) {
            //content.editNextPageAnswers(true);
            nextPageAnswers.setDisable(false);

        }
        else{
            numberOfAnswerChoice.setDisable(false);
            nextPageAnswers.setDisable(true);
        }
    }

    public void onNumberChoiceKeyRelased(KeyEvent keyEvent) {

        try{
            Integer.parseInt(keyEvent.getText());
            nextPageAnswers.setDisable(false);
        }
        catch (Exception e){
            nextPageAnswers.setDisable(true);
        }
    }


//
//    public ChangeListener<String> onDefaultNextPageChange(){
//        return new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if(newValue != null ){
//                    Platform.runLater(() -> defaultNextPage.setValue(newValue));
//                    content.connectPages();
//                }
//            }
//        };
//    }


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

    public HashMap getHTMLContentEditor()
    {
        return this.htmlEditorContent;
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
    public String getBranchingTypeProperty() { return branchingTypeProperty.get(); }
    public SimpleStringProperty branchingTypeProperty() { return branchingTypeProperty; }
    public void setBranchingTypeProperty(String branchingTypeProperty) {
        this.branchingTypeProperty.set(branchingTypeProperty);
    }
    public HashMap<String, Button> getButtonPageMap() {
        return buttonPageMap;
    }
    public void setButtonPageMap(String name, Button button) {
        this.buttonPageMap.put(name,button);
    }

}

