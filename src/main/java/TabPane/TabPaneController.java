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
import Vignette.Framework.ReadFramework;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.ConnectPages;
import Vignette.Page.PageMenu;
import Vignette.Page.VignettePage;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
//import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import MenuBar.MenuBarController;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
    Button addProblemStatement;
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


    @FXML
    Button format;

    SimpleStringProperty numberofAnswerChoiceValue = new SimpleStringProperty();
    SimpleStringProperty branchingTypeProperty = new SimpleStringProperty();

    public TabPaneController(){}
    // image sources
    //private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream(ConstantVariables.IMAGE_RESOURCE_PATH));
    private Node pageContents;
    HashMap<String, Tab> pagesTabOpened = new HashMap<>();
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

    public HashMap<String, String> getPageIds() {
        return pageIds;
    }

    public void setPageIds(HashMap<String, String> pageIds) {
        this.pageIds = pageIds;
    }

    public void setImageMap(HashMap<String, Image> imageMap) {
        this.imageMap = imageMap;
    }

    HashMap<String, String> pageIds = new HashMap<>();
    HashMap<String, Image> imageMap = new HashMap<>();


    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();

    private List<String> pageNameList = new ArrayList<String>();
    private int firstPageCount = 0;

    private HashMap<String,VignettePage> pageViewList = Main.getVignette().getPageViewList();
    private HashMap<String, HTMLEditorContent> htmlEditorContent = new HashMap<>();
    private ConstantVariables variables = new ConstantVariables();

    private MenuBarController menuBarController;

    HTMLEditorContent content;

    Button one;
    Button two;
    VignettePage pageOne;
    VignettePage pageTwo;
    Boolean isConnected = false;

    HashMap<String, ArrayList<Group>> listOfLineConnector;
    List<Images> imagesList = new ArrayList<>();
    List<String> bindPageList = new SimpleListProperty<>();

    HashMap<String, Button> buttonPageMap = new HashMap<>();


    RightClickMenu rightClickMenu;


    private Slider slider;
    private Features featureController;




    /**
     * This method initialize the list when the controller loads
     * **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.getVignette().setController(this);
        this.menuBarController = new MenuBarController();

        //==============Read a framework====================
        if(Main.getVignette().getHtmlFiles().size()!=0)
            Main.getVignette().getHtmlFiles().clear();
        if(Main.getFrameworkZipFile()==null || "".equalsIgnoreCase(Main.getFrameworkZipFile()))
            ReadFramework.read("/Users/ashnilvazirani/programming/vignette-studio-ii/src/main/resources/HTMLResources/framework.zip");
        else
            ReadFramework.read(Main.getFrameworkZipFile());
        System.out.println(Main.getVignette().getHtmlFiles());
        System.out.println(Main.getVignette().getImagesPathForHtmlFiles());
        ArrayList<Label> labels = new ArrayList<>();
        for(int i=0;i<Main.getVignette().getHtmlFiles().size();i++){
            labels.add(new Label(Main.getVignette().getHtmlFiles().get(i)));
        }

            //==============Read a framework====================


        this.slider = new Slider();
        this.slider.setMin(1);
        this.slider.setMax(40);
        this.slider.setValue(12);
        //this.slider.blockIncrementProperty().setValue(1);

        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(1);


        this.featureController = new Features(this);



        /**
         * Add right click functionality
         */
//        pageContents = pagesTab.getContent();
        this.rightClickMenu = new RightClickMenu(this);
        rightClickMenu.setAutoHide(true);
        rightAnchorPane.setOnMousePressed(new EventHandler<MouseEvent>(){

            @Override public void handle(MouseEvent event)
            {

                if(event.isSecondaryButtonDown())
                {
                    double posX=event.getX();
                    double posY=event.getY();

                    rightClickMenu.setXY(posX,posY);
                    rightClickMenu.show(rightAnchorPane, event.getScreenX(), event.getScreenY());
                }
                else {
                    rightClickMenu.hide();
                }
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
        pageIds.put(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE,"problemStatment");

        //----------------------------------------------------------------------
        ZipEntry entry = null;
        InputStream stream = null;
//        try{
//            ZipFile zipFile = new ZipFile(Main.getFrameworkZipFile());
//            Enumeration<? extends ZipEntry> entries = zipFile.entries();
//            while (entries.hasMoreElements()){
//                entry = entries.nextElement();
//                if(entry.isDirectory() && entry.getName().startsWith("pages/images/"))
//                    break;
//            }
//            stream = zipFile.getInputStream(entry);
//            readContents(stream);
//        }catch (Exception e ){
//            e.printStackTrace();
//        }

//        String imageFilePath = Main.getFrameworkZipFile().replaceAll(".zip$", "")+"/pages/images/";
//        /Users/ashnilvazirani/programming/vignette-studio-ii/src/main/resources/HTMLResources/framework/pages/images/problemstatement.png
//         /Users/ashnilvazirani/programming/vignette-studio-ii/src/main/resources/HTMLResources/framework/pages/images/problemstatement.png
        String imageFilePath = ReadFramework.getUnzippedFrameWorkDirectory()+"pages/images/"; //+entry.getName()+"";
        for(String htmlFile: Main.getVignette().getHtmlFiles()){
            for(String imageFile: Main.getVignette().getImagesPathForHtmlFiles()){
                if(htmlFile.substring(0, htmlFile.toLowerCase().indexOf(".")).equalsIgnoreCase(imageFile.toLowerCase().substring(0,imageFile.indexOf(".")))){
                    System.out.println("Image file page: "+imageFilePath+imageFile);
                    try{
                        InputStream imageStream = new FileInputStream((imageFilePath + imageFile));
                        Image image = new Image(imageStream);
                        imageMap.put(htmlFile, image);
                    }catch (NullPointerException e){
                        System.out.println(e.getMessage());
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                }
            }
        }
        System.out.println(imageMap);
        //hashmap with PageTypes as the key and the Image associated with it as the value
//        imageMap.put(ConstantVariables.LOGIN_PAGE_TYPE, IMAGE_LOGINPAGE);
//        imageMap.put(ConstantVariables.QUESTION_PAGE_TYPE, IMAGE_QUESTIONPAGE);
//        imageMap.put(ConstantVariables.PROBLEM_PAGE_TYPE, IMAGE_PROBLEMPAGE);
//        imageMap.put(ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE, IMAGE_RESPONSECORRECT);
//        imageMap.put(ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE, IMAGE_RESPONSEINCORRECT);
//        imageMap.put(ConstantVariables.WHAT_LEARNED_PAGE_TYPE, IMAGE_WHATLEARNEDPAGE);
//        imageMap.put(ConstantVariables.CREDIT_PAGE_TYPE, IMAGE_CREDITS);
//        imageMap.put(ConstantVariables.COMPLETION_PAGE_TYPE, IMAGE_COMPLETION);
//        imageMap.put(ConstantVariables.CUSTOM_PAGE_TYPE, IMAGE_CUSTOM);
//        imageMap.put(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE,IMAGE_PROBLEMSTATEMENT);
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
        ObservableList<String> newItems = FXCollections.observableArrayList();

        imageListView.setItems(FXCollections.observableList(Main.getVignette().getHtmlFiles()));
//        imageListView.setItems(items);
        imageListView.setStyle("-fx-background-insets: 0 ;");
        imageListView.setMaxWidth(100);

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

        for (int i = 0; i < ConstantVariables.PAGE_TYPE_ARRAY.length; i++) {
            String str = ConstantVariables.PAGE_TYPE_ARRAY[i];
            ConstantVariables.PAGE_TYPE_LINK_MAP.put(str, ConstantVariables.PAGE_TYPE_SOURCE_ARRAY[i]);
        }
        numberOfAnswerChoice.textProperty().addListener((observable,oldValue,newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberOfAnswerChoice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        branchingType.getItems().addAll(BranchingConstants.NO_QUESTION, BranchingConstants.RADIO_QUESTION,
                BranchingConstants.CHECKBOX_QUESTION);


//        nextPageAnswers.disableProperty().bind(
//                numberOfAnswerChoice.textProperty().isEmpty()
//                        .or( branchingType.valueProperty().isNull() )
//                         );

    }
    public void readZipStream(InputStream in) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(in);
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            System.out.println(entry.getName());
            readContents(zipIn);
            zipIn.closeEntry();
        }
    }

    private void readContents(InputStream contentsIn) throws IOException {
        byte contents[] = new byte[4096];
        int direct;
        while ((direct = contentsIn.read(contents, 0, contents.length)) >= 0) {
            System.out.println("Read " + direct + "bytes content.");
        }
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

            /**
             * When you drag and drop the page icon from the left, the following code decides what image is used for the
             * page after drag and dropping.
             */
            imageValue = imageMap.get(imageType);


            ClipboardContent content = new ClipboardContent(); // put the type of the image in clipboard
            content.putString(imageType);
            db.setContent(content); // set the content in the dragboard
            ImageView droppedView = new ImageView(imageValue); // create a new image view

           VignettePage page = createPage(event);

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
     * This function creates a Vignette page at position (X,Y) on the right Anchor pane
     * Used in RightClickMenu.java
     * @param page the vignette page created
     * @param posX X coordinate of right click
     * @param posY Y coordinate of right click
     */
    public void createPageFromRightClick(VignettePage page,double posX, double posY)
    {

        if(page!=null)
        {
            Image imageValue = imageMap.get(page.getPageType());
            ImageView droppedView = new ImageView(imageValue); // create a new image view

            if (page != null) {
                Button pageViewButton = createVignetteButton(page, droppedView, posX, posY, page.getPageType());
            }
        }
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
     *  This function makes use of DragEvents and the information stored on the DragBoard to create the required HTML
     *  page in the vignette editor. The images on the listView are associated with the appropriate HTML pages in order
     *  to do so.
     * @return
     */
    public VignettePage createPage(DragEvent event) {


        Dragboard db = event.getDragboard();

        String pageType="";
        //if (db.hasString())
        //    pageType = db.getString().trim();

        pageType = db.getString().trim();
        GridPaneHelper newPageDialog = new GridPaneHelper();

        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        boolean selected = false;
        if(pageType.equalsIgnoreCase(ConstantVariables.LOGIN_PAGE_TYPE)){
            checkBox.setSelected(true);
            checkBox.setDisable(true);
        }
        //textbox to enter page name
        TextField pageName = newPageDialog.addTextField(1, 3, 400);
        //setting the default pageID
        pageName.setText(pageIds.get(pageType));
        String pageTitle = "Create New "+pageType+" Page";
        if(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE.equalsIgnoreCase(pageType) || ConstantVariables.LOGIN_PAGE_TYPE.equalsIgnoreCase(pageType)){
            pageName.setEditable(false);
        }
        boolean cancelClicked = newPageDialog.createGrid(pageTitle, "Please enter the page name", "Ok", "Cancel");
        if (!cancelClicked) return null;
        boolean isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0;

        //checking whether the user has entered a unique pageID
        while (!isValid) {
            String message = pageNameList.contains(pageName.getText()) ? " All page id must be unique"
                    : pageName.getText().length() == 0 ? "Page id should not be empty" : "";


            //creating an information alert to deal--------------
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setContentText(message);
            alert.showAndWait();
            //---------------------------------------------------


            cancelClicked = newPageDialog.showDialog();
            isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0;
            if (!cancelClicked) return null;
        }

        boolean check = checkBox.isSelected();
        if(check){
            firstPageCount++;
            Main.getVignette().setHasFirstPage(true);
        }
        pageNameList.add(pageName.getText());

        //creating a new Vignette page based off user provided information.
        VignettePage page = new VignettePage(pageName.getText().trim(), check, pageType);
        return page;
    }



    /**
     *This was the function used in the original vignette studio ii to create pages after drag and dropping
     * the plain orange icon. Once the icon is dropped it will open a dialog box that prompts you to choose the required
     * page type and type in a valid page id.
     *
     * This is not used for the drag and drop method of creating pages anymore but rather when you use the new option from
     * the right click menu anywhere on the right anchor pane.
     *
     * Called in TabPane.RightClickMenu.java
     */
    public VignettePage createNewPageDialog(boolean pastePage, String pageType){
        GridPaneHelper  newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        ComboBox dropDownPageType = newPageDialog.addDropDown(ConstantVariables.listOfPageTypes,1,2);
        TextField pageName = newPageDialog.addTextField(1,3, 400);

        dropDownPageType.setOnAction(event -> {
            String value = (String) dropDownPageType.getValue();
            if(value.equals(ConstantVariables.LOGIN_PAGE_TYPE)) pageName.setText("login");
            if(value.equals(ConstantVariables.QUESTION_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.WHAT_LEARNED_PAGE_TYPE)) pageName.setText("whatLearned");
            if(value.equals(ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.CREDIT_PAGE_TYPE)) pageName.setText("credits");
            if(value.equals(ConstantVariables.COMPLETION_PAGE_TYPE)) pageName.setText("Completion");
        });
        if(pastePage && pageType!=null){
            dropDownPageType.setValue(pageType);
            dropDownPageType.setDisable(true);
        }

        String pageTitle = "Create New Page";
        boolean cancelClicked = newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");
        if(!cancelClicked) return null;

        // if page ids exists  or if the text is empty
        boolean isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0 && !dropDownPageType.getValue().equals("Please select page type");
        boolean start = !dropDownPageType.getValue().equals("Please select page type");

        while (!isValid){
            String message = dropDownPageType.getValue().equals("Please select page type")?"Select a valid Page Type":
                    pageNameList.contains(pageName.getText())?" All page id must be unique"
                            :pageName.getText().length() == 0? "Page id should not be empty":"";


            //creating an information alert to deal--------------
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setContentText(message);
            alert.showAndWait();
            //---------------------------------------------------


            cancelClicked = newPageDialog.showDialog();
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
        buttonPageMap.put(page.getPageName(), vignettePageButton);

        vignettePageButton.relocate(posX,posY);

        final double[] delatX = new double[1]; // used when the image is dragged to a different position
        final double[] deltaY = new double[1];
        vignettePageButton.setAlignment(Pos.CENTER);
        vignettePageButton.setTextAlignment(TextAlignment.CENTER);
        vignettePageButton.setContentDisplay(ContentDisplay.TOP);
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

            // this is the code the deals with opening the right click menu
            if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {

                //prevents the right click menu from staying open if you right click before you right click the page button
                this.rightClickMenu.hide();

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
                DialogHelper confirmation = new DialogHelper(Alert.AlertType.CONFIRMATION,
                        "Delete Page",
                        null,
                        "Are you sure you want to delete this page?",
                        false);
                if(confirmation.getOk()) {
                    if(page.isFirstPage()) firstPageCount = 0;
                    this.pageNameList.remove(page.getPageName());

                    if(this.listOfLineConnector.containsKey(vignettePageButton.getText())) {
                        ArrayList<Group> connections = this.listOfLineConnector.get(vignettePageButton.getText());
                        connections.stream().forEach(connection-> {
                            this.rightAnchorPane.getChildren().remove(connection);
                        });
                        HashMap<String, String> connectedTo = page.getPagesConnectedTo();
                        System.out.println("LEFT AFTER DELETING: ");
                        page.clearNextPagesList();
                        TabPaneController paneController = Main.getVignette().getController();
                        paneController.getPagesTab().setDisable(true);
                        paneController.makeFinalConnection(page);
                    }
                    this.listOfLineConnector.remove(vignettePageButton.getText());
                    this.rightAnchorPane.getChildren().remove(vignettePageButton);
                    pageViewList.remove(vignettePageButton.getText());
                    this.rightAnchorPane.getChildren().stream().forEach(element->{
                        System.out.println(element);
                    });
                    pagesTab.setDisable(true);
                }
            }
        });


        this.rightAnchorPane.getChildren().add(vignettePageButton);
        page.setPosX(posX);
        page.setPosY(posY);
        pageViewList.put(page.getPageName(),page);

        // -------end of mouse event methods-------
        return vignettePageButton;
    }



    public void openPage(VignettePage page, String type) {
        String text;
        pagesTab.setDisable(false);
        tabPane.getSelectionModel().select(pagesTab);


        pageName.setText(page.getPageName());
//        if(!ConstantVariables.PAGES_TAB_TEXT.equalsIgnoreCase(pagesTab.getText())){
//            System.out.println("WE NEED A NEW TAB NOW! ");
//            Tab newTab  = new Tab(page.getPageName());
//            System.out.println(pagesTab.getProperties());
//            newTab.setContent(pageContents);
//            newTab.setClosable(true);
//            try{
//                newTab.setContent(FXMLLoader.load(getClass().getResource("/FXML/pagesTab.fxml")));
//                newTab.setStyle(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
//                pagesTabOpened.put(page.getPageName(), newTab);
//                Tab t = tabPane.getTabs().get(1);
//                PagesTab p = new PagesTab();
//
//                content = new HTMLEditorContent(p.htmlSourceCode,
//                        type, page, newTab,
//                        pageNameList,
//                        branchingTypeProperty,
//                        numberofAnswerChoiceValue,
//                        pageName);
//                htmlEditorContent.put(page.getPageName(),content);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            getTabPane().getTabs().add(newTab);
//        }else{
//            pagesTab.setText(page.getPageName());
//            content = new HTMLEditorContent(htmlSourceCode,
//                    type, page, pagesTab,
//                    pageNameList,
//                    branchingTypeProperty,
//                    numberofAnswerChoiceValue,
//                    pageName);
//            htmlEditorContent.put(page.getPageName(),content);
//        }

        if(htmlEditorContent.containsKey(page.getPageName())){
            content = htmlEditorContent.get(page.getPageName());
        }
        else{
            Tab t = tabPane.getTabs().get(1);
            content = new HTMLEditorContent(htmlSourceCode,
                    type, page, t,
                    pageNameList,
                    branchingTypeProperty,
                    numberofAnswerChoiceValue,
                    pageName);
            htmlEditorContent.put(page.getPageName(),content);
        }

        // content.addDropDown();
        if(page.getPageData()==null){
            try {
                text = content.addTextToEditor();
                page.setPageData(text);
                pageViewList.put(page.getPageName(), page);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            text = content.setText(page.getPageData());
            page.setPageData(text);
            pageViewList.put(page.getPageName(), page);
        }

        Main.getVignette().setPageViewList(pageViewList);

        HashMap<String, String> connectionEntries = new HashMap<>();
        for (HashMap.Entry<String, String> entry : page.getPagesConnectedTo().entrySet()) {
            String[] temp = entry.getValue().split(",");
            for(String x: temp)
                connectionEntries.put(x.trim(), entry.getKey());
        }
        String questionType="";
//        questionType= 'radio';
        if(page.getQuestionType()==null || "".equalsIgnoreCase(page.getQuestionType())){
            String htmlText = htmlSourceCode.getText();
            Pattern pattern = Pattern.compile("questionType= '(.*?)';\n", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(htmlText);
            if (matcher.find()) {
                questionType = matcher.group(0).split("=")[1].trim().replaceAll("'", "").replaceAll(";", "");
                System.out.println("PAGE QUESTION TYPE FROM MATCHER: "+questionType);
            }else{
                System.out.println("No Question Type Found");
            }
        }else{
            questionType = page.getQuestionType();
        }
        if(BranchingConstants.RADIO_QUESTION.equalsIgnoreCase(questionType)){
            branchingType.setValue(BranchingConstants.RADIO_QUESTION);
        }else if(BranchingConstants.CHECKBOX_QUESTION.equalsIgnoreCase(questionType)){
            branchingType.setValue(BranchingConstants.CHECKBOX_QUESTION);
        }else{
            branchingType.setValue(BranchingConstants.NO_QUESTION);
        }
        if(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()>0)
            numberOfAnswerChoice.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()+"");
        else if(connectionEntries.size()!=0)
            numberOfAnswerChoice.setText(connectionEntries.size()-1+"");
        nextPageAnswers.setDisable(false);



        System.out.println(htmlSourceCode.getScene());



        //-----------------   dealing with keyboard shortcuts  -----------------------------------------
        htmlSourceCode.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination incFont = new KeyCodeCombination(KeyCode.EQUALS,KeyCombination.CONTROL_DOWN);
            final KeyCombination decFont = new KeyCodeCombination(KeyCode.MINUS,KeyCombination.CONTROL_DOWN);
            final KeyCombination search = new KeyCodeCombination(KeyCode.F,KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                //System.out.println(ke);
                if (incFont.match(ke)) {
                    System.out.println("Key Pressed: " + incFont);
                    featureController.increaseFont(slider,htmlSourceCode);
                    ke.consume(); // <-- stops passing the event to next node
                } else if (decFont.match(ke)){
                    System.out.println("Decreasing font size");
                    featureController.decreaseFont(slider,htmlSourceCode);
                    ke.consume();
                }
                else if(search.match(ke))
                {
                    featureController.findAndSelectString(htmlSourceCode);
                }
            }
        });
        //-----------------------------------------------------------------------------------
    }



    private void connectPages(MouseEvent event) {
        two = ((Button) event.getSource());
        pageTwo = pageViewList.get(two.getText());
        checkPageConnection(pageOne,pageTwo,one,two);

    }

    public boolean checkPageConnection(VignettePage pageOne, VignettePage pageTwo, Button one, Button two, String... connectedViaPage ) {
        //no self connections
        if(two.getText().equals(one.getText())){
            isConnected = false;
        }

        else {
            String toConnect = "";
            //if not a first connection to pages
            if(this.listOfLineConnector!= null && this.listOfLineConnector.containsKey(pageOne.getPageName()) && pageOne.getConnectedTo()!=null){
                // if such page exists on the vignette pane
                if(pageViewList.containsKey(pageOne.getPageName())){
                    VignettePage page = pageViewList.get(pageOne.getPageName());
                    HashMap<String,String> listOfPagesConnectedTo = page.getPagesConnectedTo();
                    String connectedTo = page.getConnectedTo();
                    if(this.listOfLineConnector.containsKey(connectedTo)) {
                        ArrayList<Group> list = this.listOfLineConnector.get(connectedTo);
                        list.remove(0);
                        this.listOfLineConnector.replace(connectedTo,list);
                    }
                    if(this.listOfLineConnector.containsKey(pageOne.getPageName())) this.listOfLineConnector.remove(pageOne.getPageName());
                    pageOne.removeNextPages(connectedTo);
                    System.out.println("PAGE NULL: "+pageViewList.get(connectedTo));
//                    pageViewList.get(connectedTo).removeNextPages(pageOne.getPageName());
                }

            }
            if(connectedViaPage.length==1){
                HashMap<String, String> pageConnectionList= pageOne.getPagesConnectedTo();
                if(pageConnectionList.containsKey(pageTwo.getPageName())){
                    pageOne.addPageToConnectedTo(pageTwo.getPageName(), pageConnectionList.get(pageTwo.getPageName()) + ", "+connectedViaPage[0]);
                }else{
                    pageOne.addPageToConnectedTo( pageTwo.getPageName(), connectedViaPage[0]);
//                    pageOne.addPageToConnectedTo(connectedViaPage[0], pageTwo.getPageName());
                }
            }
            pageOne.setPreviousConnection(pageOne.getConnectedTo());
            pageOne.setConnectedTo(two.getText());
            Utility utility = new Utility();
            String text = null;
            if(pageOne.getPageData()!=null) {
                text = utility.replaceNextPage(pageOne.getPageData(), pageOne);
            }
            if(pageOne.getPageData() != null) pageOne.setPageData(text);
            isConnected = true;
        }
        return isConnected;
    }
    public void makeFinalConnection(VignettePage pageOne){
        TabPaneController pane = Main.getVignette().getController();
        Button one = pane.getButtonPageMap().get(pageOne.getPageName());

        HashMap<String, String> pageConnectioList = pageOne.getPagesConnectedTo();
        String toConnect = "";
        for (HashMap.Entry<String, String> entry : pageConnectioList.entrySet()) {
            VignettePage pageTwo = Main.getVignette().getPageViewList().get(entry.getKey());
            Button two = pane.getButtonPageMap().get(entry.getKey());
            ConnectPages connect = new ConnectPages(one, two, rightAnchorPane, this.listOfLineConnector);
            toConnect = entry.getValue().trim();
            String previousConnection = "";
            if(pageOne.getPreviousConnection()!=null && pageOne.getConnectedTo()!=null && !"".equalsIgnoreCase(pageOne.getConnectedTo()) && BranchingConstants.NO_QUESTION.equalsIgnoreCase(pageOne.getQuestionType()))
                previousConnection = pageOne.getPreviousConnection();
            Group grp = connect.connectSourceAndTarget(toConnect, previousConnection);
            if(grp!=null){
                pageOne.setConnectedTo(two.getText());
                pageOne.setNextPages(two.getText(), grp);
                pageTwo.setNextPages(pageOne.getPageName(),grp);
            }
        }


    }


    public void changeFormat()
    {
        featureController.changeFormat(slider,htmlSourceCode);
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
        content.editNextPageAnswers(branchingType.getSelectionModel().getSelectedItem().equals(BranchingConstants.NO_QUESTION));
    }

    public void pageSettingsButtonAction(ActionEvent actionEvent) {
        content.editPageSettings();
    }

    public void addVideoToEditor(ActionEvent actionEvent) {
        content.addVideo();
    }
    public void addProblemStatmentToQuestion(ActionEvent actionEvent) {
        content.addProblemStatmentToQuestion();
    }
    public void addInputFieldToEditor(ActionEvent actionEvent) {
        content.addInputFields(false);
    }
    public void addImageInputField(ActionEvent actionEvent) {
        content.addInputFields(true);
    }

    public void selectBranchingType(ActionEvent actionEvent) {
        String value = (String) branchingType.getSelectionModel().getSelectedItem();
        if(value.equals(BranchingConstants.NO_QUESTION)) {
            //content.editNextPageAnswers(true);
            if("".equalsIgnoreCase(numberOfAnswerChoice.getText())){
                nextPageAnswers.setDisable(false);
            }
            numberOfAnswerChoice.setText("0");
            numberOfAnswerChoice.setDisable(true);
        }
        else{
            numberOfAnswerChoice.setDisable(false);
        }
        if(Integer.parseInt(numberOfAnswerChoice.getText())>0){
            nextPageAnswers.setDisable(false);
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

    public HashMap<String, HTMLEditorContent> getHTMLContentEditor()
    {
        return this.htmlEditorContent;
    }

    public HashMap getImageMap()
    {
        return this.imageMap;
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

