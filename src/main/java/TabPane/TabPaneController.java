/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package TabPane;

import Application.Main;
import ConstantVariables.ConstantVariables;
import ConstantVariables.BranchingConstants;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import MenuBar.File.FileMenuItem;
import MenuBar.Vignette.VignetteMenuItem;
import SaveAsFiles.Images;
import Utility.Utility;
import Vignette.Framework.ReadFramework;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.ConnectPages;
import Vignette.Page.PageMenu;
import Vignette.Page.VignettePage;

import Vignette.Vignette;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import MenuBar.MenuBarController;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


import javafx.stage.Popup;
import org.apache.commons.io.IOUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


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
    CodeArea htmlSourceCode;

    @FXML
    AnchorPane anchorPANE;

    @FXML
    Label questionAndBranchingTitle;

    @FXML
    Button addImage;
    @FXML
    Button addVideo;
    @FXML
    Button addInputField;
    @FXML
    Button addImageInputField;
    @FXML
    Button lastPage;



    @FXML
    Button addProblemStatement;
    @FXML
    ComboBox selectNextPage;
    @FXML
    ScrollPane scrollPane;
    @FXML
    ComboBox branchingType;

    @FXML
    Button nextPageAnswers;
    @FXML
    Label pageName;
    @FXML
    Label numAnswers;
    @FXML
    TextField numberOfAnswerChoice;
    @FXML
    Button lastPageOptions;


    @FXML
    Button showHideScript;

    @FXML
    Button format;

    SimpleStringProperty numberofAnswerChoiceValue = new SimpleStringProperty();
    SimpleStringProperty branchingTypeProperty = new SimpleStringProperty();

    public TabPaneController(){ }
    // image sources
    Image defaultImage = new Image(ConstantVariables.DEFAULT_RESOURCE_PATH);


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
    private HashMap<String, Boolean> lastPageValueMap = new HashMap<>();



    private int firstPageCount = 0;
    public String getNumberofAnswerChoiceValue() { return numberofAnswerChoiceValue.get(); }
    public Property<String> numberofAnswerChoiceValueProperty() { return numberofAnswerChoiceValue; }
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
    private EditorRightClickMenu editorRightClickMenu;

    private int scriptTagIndex;
    private boolean isScriptHidden = false;
    private Slider slider;
    private Features featureController;



    public String getBranchingTypeProperty() { return branchingTypeProperty.get(); }
    public SimpleStringProperty branchingTypeProperty() { return branchingTypeProperty; }

    public Tab getPagesTab() { return pagesTab;  }
    public void setPagesTab(Tab pagesTab) { this.pagesTab = pagesTab; }
    /**
     * This method initialize the list when the controller loads
     * **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.getVignette().setController(this);
        this.menuBarController = new MenuBarController();

        //==============Read a framework====================
        if(Main.getVignette().getHtmlFiles()!=null && Main.getVignette().getHtmlFiles().size()!=0){
            Main.getVignette().getHtmlFiles().clear();
            Main.getVignette().setHtmlFiles(new ArrayList<>());
        }
        //=============================================
        ReadFramework.read(ReadFramework.getUnzippedFrameWorkDirectory());
        //=============================================
        boolean imageMapWasEmpty = false;
        if(imageMap == null || imageMap.size()==0){
            imageMapWasEmpty = true;
        }
        ArrayList<Label> labels = new ArrayList<>();
        //==============Read a framework====================
        this.menuBarController = new MenuBarController();
        //==============Read a framework====================

       // VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(htmlSourceCode);
        this.htmlSourceCode = new CodeArea();
        this.htmlSourceCode.setId("styled-text-area");

        this.htmlSourceCode.textProperty().addListener((obs, oldText, newText) -> {
            htmlSourceCode.setStyleSpans(0, computeHighlighting(newText));
            defaultStyle();

            //check if page already has the last page function
            //check if page already has the last page function
            Pattern pattern = Pattern.compile("setLastPage\\(\\);");
            Matcher matcher = pattern.matcher(htmlSourceCode.getText());

            //if the user has added it to the html editor manually, add it to the map
            if(matcher.find()) {
                System.out.println("User has typed in setLastPage");
                lastPageValueMap.put(Main.getVignette().getCurrentPage().getPageName(),true);
            }
        });
        //coupling virtual scroll pane because default inline
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(htmlSourceCode);

        //
        AnchorPane.setTopAnchor(vsPane,0.0);
        AnchorPane.setRightAnchor(vsPane,0.0);
        AnchorPane.setBottomAnchor(vsPane,0.0);
        AnchorPane.setLeftAnchor(vsPane,0.0);

        anchorPANE.getChildren().add(vsPane);


        this.slider = new Slider();
        this.slider.setMin(1);
        this.slider.setMax(40);
        this.slider.setValue(12);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(1);

        for(int i=0;i<Main.getVignette().getHtmlFiles().size();i++){
            labels.add(new Label(Main.getVignette().getHtmlFiles().get(i)));
//            System.out.println("COMPLEX TEXT: ");s
//            if(imageMapWasEmpty){
//                String path = ReadFramework.getUnzippedFrameWorkDirectory()+
//                        "/"+
//                        Main.getVignette().getImagesPathForHtmlFiles().get(Main.getVignette().getHtmlFiles().get(i).replace(".html", ""));
//                System.out.println(path);
//                path = path.trim();
//                if(path.endsWith("null")){
//                    imageMap.put(Main.getVignette().getHtmlFiles().get(i).replace(".html", ""), defaultImage);
//                }else{
//                    imageMap.put(Main.getVignette().getHtmlFiles().get(i).replace(".html", ""), new Image(path.trim()));
//                }
//            }
        }

        rightAnchorPane.addEventHandler(KeyEvent.ANY, event -> {
            KeyCombination controlV = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN);
            if(controlV.match(event)){
                if(getPageToCopy()!=null){
                    VignettePage pageToCopy = Main.getVignette().getController().getPageToCopy();
                    VignettePage newPage = createNewPageDialog(true, pageToCopy.getPageType());
                    if (newPage!=null && pageToCopy.getPageData() != null) {
                        newPage.setPageData(pageToCopy.getPageData());
                    }
                    HashMap<String,Image> imageMap = Main.getVignette().getController().getImageMap();
                    System.out.println("imageMap:: "+imageMap);
                    ImageView imageView = new ImageView(imageMap.get(newPage.getPageType()));
                    createVignetteButton(newPage,imageView, 500, 500, pageToCopy.getPageType());
                }else{
                    System.out.println("NO PAGE TO COPY AND PASTE");
                }
            }
        });


        this.featureController = new Features(this);

        //-------------------------- ADDING RIGHT CLICK MENUS-----------------------------------------------------------
        // Adding right click functionality to the IVET editor drag and drop right anchor pane
        /**
         * Add right click functionality
         */
//        pageContents = pagesTab.getContent();

        //-------------------------- ADDING RIGHT CLICK MENUS-----------------------------------------------------------
        // Adding right click functionality to the IVET editor drag and drop right anchor pane
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

        // Adding right click functionality to the InlineCssTextArea
        this.editorRightClickMenu = new EditorRightClickMenu(this,htmlSourceCode);
        editorRightClickMenu.setAutoHide(true);
        htmlSourceCode.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override public void handle(MouseEvent event)
            {
                if(event.isSecondaryButtonDown())
                {
                    double posX=event.getX();
                    double posY=event.getY();
                    editorRightClickMenu.setXY(posX,posY);
                    editorRightClickMenu.checkButtonStatus();
                    editorRightClickMenu.show(htmlSourceCode, event.getScreenX(), event.getScreenY());

                    

                    /**
                    if(editorRightClickMenu.getIsScriptHidden())
                        featureController.setScriptHidden(true);
                    else
                        featureController.setScriptHidden(false);

                     */

                }
                else {
                    editorRightClickMenu.hide();
                }
            }
        });

    //------------------------------------------------------------------------------------------------------------------
        numberOfAnswerChoice.textProperty().bindBidirectional(numberofAnswerChoiceValueProperty());
        branchingType.valueProperty().bindBidirectional(branchingTypeProperty());
    //------------------------------------------------------------------------------------------------------------------


        //splitPane.setDividerPositions(0.3);
        listOfLineConnector = new HashMap<>();



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

//        String imageFilePath = ReadFramework.getUnzippedFrameWorkDirectory()+"pages/images/"; //+entry.getName()+"";
//        for(String htmlFile: Main.getVignette().getHtmlFiles()){
//            for(String imageFile: Main.getVignette().getImagesPathForHtmlFiles()){
//                if(htmlFile.substring(0, htmlFile.toLowerCase().indexOf(".")).equalsIgnoreCase(imageFile.toLowerCase().substring(0,imageFile.indexOf(".")))){
//                    try{
//                        InputStream imageStream = new FileInputStream((imageFilePath + imageFile));
//                        Image image = new Image(imageStream);
//                        imageMap.put(htmlFile, image);
//                    }catch (NullPointerException e){
//                        System.out.println(e.getMessage());
//                    }catch (Exception e){
//                        System.out.println(e.getMessage());
//                    }
//                    break;
//                }
//            }
//        }

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

        ObservableList<String> items = FXCollections.observableArrayList(ConstantVariables.LOGIN_PAGE_TYPE,
                ConstantVariables.PROBLEM_PAGE_TYPE,ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE, ConstantVariables.QUESTION_PAGE_TYPE,
                ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE, ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE,ConstantVariables.WHAT_LEARNED_PAGE_TYPE,
                ConstantVariables.CREDIT_PAGE_TYPE, ConstantVariables.COMPLETION_PAGE_TYPE, ConstantVariables.CUSTOM_PAGE_TYPE);
        if(Main.defaultFramework)
            imageListView.setItems(FXCollections.observableList(items));
        else
            imageListView.setItems(FXCollections.observableList(Main.getVignette().getHtmlFiles()));
        imageListView.setMaxWidth(150.0);
        imageListView.setMaxHeight(1000.0);
//        imageListView.setMaxHeight(Main.getStage().getScene().getHeight());
        selectNextPage = new ComboBox(FXCollections.observableArrayList(pageNameList));

        /**
         * In order to put images into the Page type image pane, first you have to identify the page types here.
         * ORDER IS IMPORTANT.
         * After mentioning it here, make changes in setCellFactory.
         */
        imageListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                private ImageView imageView = new ImageView();
                @Override
                public void updateItem(String name, boolean empty) {
                    if(name == null)
                        return;
                    super.updateItem(name, empty);
                    if (empty) {
                    }
                    VBox vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER);
                    //THIS displays the images of the page types on the listView

                    if(name.lastIndexOf(".")>-1){
                        name = name.substring(0,name.lastIndexOf("."));
                    }
                    Image buttonImage = null;
                    if(Main.getVignette().getImagesPathForHtmlFiles()!=null && Main.getVignette().getImagesPathForHtmlFiles().get(name)!=null){
                        try {
                            InputStream is = new FileInputStream(new File(ReadFramework.getUnzippedFrameWorkDirectory()+"/"+Main.getVignette().getImagesPathForHtmlFiles().get(name)));
                            buttonImage = new Image(is);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        buttonImage = new Image(getClass().getResourceAsStream(ConstantVariables.DEFAULT_RESOURCE_PATH));
                    }

                    Popup popup = new Popup();
                    Label popupMsg = new Label();
                    popupMsg.setStyle("-fx-background-color: black; -fx-text-fill: white;-fx-padding: 5;");
                    popup.getContent().add(popupMsg);
                    popupMsg.setText("Drag and drop on right plane range");
                    AtomicInteger x = new AtomicInteger();
                    AtomicInteger y = new AtomicInteger();
                    vbox.hoverProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean show) -> {
                        if (show) {
                            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                            vbox.setStyle("-fx-background-color: lightgray");
                            popup.show(vbox, mouseLocation.getX(), mouseLocation.getY() + 10);
                        }else{
                            vbox.setStyle("");
                            popup.hide();
                        }
                    });
                    imageView.setImage(buttonImage);
//=====================================================================================================================
//                    if(Main.getVignette().getImagesPathForHtmlFiles().get(name)!=null) {
//                        try {
//                            File f = new File(ReadFramework.getUnzippedFrameWorkDirectory()+"/"+Main.getVignette().getImagesPathForHtmlFiles().get(name));
//                            imageView.setImage(new Image(f.toURI().toString()));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            System.out.println(ReadFramework.getUnzippedFrameWorkDirectory()+Main.getVignette().getImagesPathForHtmlFiles().get(name));
//                        }
//                    }
//                    else
//                        imageView.setImage(new Image(getClass().getResourceAsStream(ConstantVariables.DEFAULT_RESOURCE_PATH)));
// =====================================================================================================================

                    Label label = new Label(name);
                    if(label!=null){
                        label.setAlignment(Pos.BOTTOM_CENTER);
                        vbox.getChildren().add(imageView);
                        vbox.getChildren().add(label);
                        setGraphic(vbox);
                    }
                }
            };

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
        imageListView.prefHeightProperty().bind(tabPane.heightProperty());
        imageListView.setStyle(" -fx-padding: 20px 0px 0px 0px;");
        numberOfAnswerChoice.textProperty().addListener((observable,oldValue,newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberOfAnswerChoice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        /** todo THIS WAS THE PREVIOUS WAY WE SET VALUES IN THE COMBO BOX
        branchingType.getItems().addAll(BranchingConstants.SIMPLE_BRANCH, BranchingConstants.RADIO_QUESTION,
                BranchingConstants.CHECKBOX_QUESTION);
        */

//        nextPageAnswers.disableProperty().bind(
//                numberOfAnswerChoice.textProperty().isEmpty()
//                        .or( branchingType.valueProperty().isNull() )
//                         );


        if(Main.getVignette().getPageViewList()!=null && Main.getVignette().getPageViewList().size()>0){
            this.getAnchorPane().getChildren().clear();
            FileMenuItem.addButtonToPane(Main.getVignette(), this);
            for (Map.Entry<String, VignettePage> e : Main.getVignette().getPageViewList().entrySet()) {
                this.makeFinalConnection(e.getValue());
            }
        }
        if(Main.getVignette().getSettings()==null){
            (new VignetteMenuItem()).editVignetteSettings();
        }
    }


   // "|(?<SCRIPT><!-- //////// Do Not Change content in this block //////// -->[^<>]+<!-- //////// Do Not Change content in this block //////// -->)")

    private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
            +"|(?<COMMENT><!--[^<>]+-->)");
    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=)(\\h*\"[^\"]+\")");

    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;


    /**
     * Computes how tags should be highlighted in the CodeArea
     * @param text
     * @return
     */
    private StyleSpans<Collection<String>> computeHighlighting(String text) {

        Matcher matcher = XML_TAG.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            if (matcher.group("COMMENT") != null) {
                spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
            }
            else {
                if (matcher.group("ELEMENT") != null) {
                    String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);

                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
                    spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

                    if (!attributesText.isEmpty()) {

                        lastKwEnd = 0;

                        Matcher amatcher = ATTRIBUTES.matcher(attributesText);
                        while (amatcher.find()) {
                            spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
                            spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
                            spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
                            spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
                            lastKwEnd = amatcher.end();
                        }
                        if (attributesText.length() > lastKwEnd)
                            spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
                    }
                    lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);
                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
                }
            }
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void scriptStyle()
    {
        String target = "<script>([\\S\\s]*?)</script>";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);
        if(m.find())
            htmlSourceCode.setStyleClass(m.start(),m.end(),"script");
    }

    public void defaultStyle()
    {
        htmlSourceCode.setStyleSpans(0, computeHighlighting(htmlSourceCode.getText()));
        scriptStyle();
    }


    public void readZipStream(InputStream in) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(in);
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
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
            pageIds.put(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE,"problemStatement");
            //----------------------------------------------------------------------

            ClipboardContent content = new ClipboardContent(); // put the type of the image in clipboard
            content.putString(imageType);
            db.setContent(content); // set the content in the dragboard

            VignettePage page = createPage(event);
            ImageView droppedView = null;
            if(page!=null){
                if(Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType())!=null){
                    File f = new File(ReadFramework.getUnzippedFrameWorkDirectory()+"/"+Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                    droppedView = new ImageView(f.toURI().toString());
                }
                else{
                    droppedView = new ImageView(defaultImage); // create a new image view
                }
            }
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
            ImageView droppedView = null;
            System.out.println("TYPE: "+page.getPageType());
            if(Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType())!=null){
                System.out.println("IMAGES: "+ReadFramework.getUnzippedFrameWorkDirectory()+Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                File f  = new File(ReadFramework.getUnzippedFrameWorkDirectory()+Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                droppedView = new ImageView(f.toURI().toString());
            }else{
                droppedView = new ImageView(defaultImage); // create a new image view
            }

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

        pageType = db.getString().trim();
        GridPaneHelper newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        boolean selected = false;
        if(pageType.lastIndexOf(".")>-1)
            pageType = pageType.substring(0, pageType.lastIndexOf("."));
        if(pageType.equalsIgnoreCase(ConstantVariables.LOGIN_PAGE_TYPE)){
            checkBox.setSelected(true);
            checkBox.setDisable(true);
        }
        //textbox to enter page name
        TextField pageName = newPageDialog.addTextField(1, 3, 400);
        //setting the default pageID

        pageName.setText(pageIds.get(pageType));
        //System.out.println("page map:  "+pageIds);


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

        //newly created page doesn't have the setLastPage(); function
        lastPageValueMap.put(pageName.getText(),false);



        //creating a new Vignette page based off user provided information.
        VignettePage page = new VignettePage(pageName.getText().trim(), check, pageType);
        String text = Main.getVignette().getController().getPageDataWithPageType(page, pageType);
        page.setPageData(text);
        return page;
    }


    public String getPageDataWithPageType(VignettePage page, String pageType){
        String text="";
        try{
            if(!pageType.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) {
                ZipFile zipFile = new ZipFile(Main.getFrameworkZipFile());
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                ZipEntry entry = null;
                while(entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    if(entry.getName().equalsIgnoreCase(page.getPageType()))
                        break;
                }
                if(entry!=null){
                    InputStream stream = new FileInputStream(ReadFramework.getUnzippedFrameWorkDirectory()+"/pages/"+ pageType +".html");
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
                    text = writer.toString() + "\n\n";
                }else{
                    System.out.println("NO ENTRY FOUND");
                }
            }
            else{
                text= ConstantVariables.SCRIPT_FOR_CUSTOM_PAGE;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return text;
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
            if(value.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) pageName.setText("");
            if(value.equals(ConstantVariables.PROBLEM_PAGE_TYPE)) pageName.setText("");
            if(value.equals(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE)) pageName.setText("problemStatement");
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

        //newly created page doesn't have the setLastPage(); function
        lastPageValueMap.put(pageName.getText(),false);


        dropDownPageType.setDisable(false);
        return page;
    }


    private void focusState(boolean value) {
        if (value) {
            System.out.println("Focus GAINED TO button");
        }
        else {
            System.out.println("Focus LOST FROM button");
        }
    }
    public VignettePage pageToCopy;
    public VignettePage getPageToCopy() {
        return pageToCopy;
    }

    public void setPageToCopy(VignettePage pageToCopy) {
        this.pageToCopy = pageToCopy;
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


        vignettePageButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vignettePageButton.addEventFilter(KeyEvent.ANY, event -> {
                KeyCombination controlC = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN);
                if(controlC.match(event)){
                    pageToCopy = page;
                }
            });
        });

        if(page.getQuestionType().equalsIgnoreCase("radio")){
            branchingTypeProperty.set(BranchingConstants.RADIO_QUESTION);
        }else if(page.getQuestionType().equalsIgnoreCase("checkbox")){
            branchingTypeProperty.set(BranchingConstants.CHECKBOX_QUESTION);
        }else{
            branchingTypeProperty.set(BranchingConstants.SIMPLE_BRANCH);
        }
        numberofAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()+"");

        content = new HTMLEditorContent(htmlSourceCode,
                type, page,
                pageNameList,
                branchingTypeProperty,
                numberofAnswerChoiceValue,
                pageName);

        htmlEditorContent.put(page.getPageName(), content);


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

                    //removing page from the map
                    lastPageValueMap.remove(page.getPageName());

                    if(this.listOfLineConnector.containsKey(vignettePageButton.getText())) {
                        ArrayList<Group> connections = this.listOfLineConnector.get(vignettePageButton.getText());
                        connections.stream().forEach(connection-> {
                            this.rightAnchorPane.getChildren().remove(connection);
                        });
                        HashMap<String, String> connectedTo = page.getPagesConnectedTo();
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

public void addKeyEvent(KeyEvent event){

}

    public void openPage(VignettePage page, String type) {
        String text;
        pagesTab.setDisable(false);
        tabPane.getSelectionModel().select(pagesTab);
        page.setPageType(type);
        pageName.setText(page.getPageName());


        if (htmlEditorContent.containsKey(page.getPageName())) {
            content = htmlEditorContent.get(page.getPageName());
        }

        this.htmlSourceCode.textProperty().addListener((obs, oldText, newText) -> {
            htmlSourceCode.setStyleSpans(0, computeHighlighting(newText));
            defaultStyle();
        });

        //setting the current page
        Main.getVignette().setCurrentPage(page);


        /**
         *
         *
         *  TODO editorRightClick Menu
         *
         *
            this.editorRightClickMenu = new EditorRightClickMenu(this,htmlSourceCode);
            editorRightClickMenu.setAutoHide(true);
            htmlSourceCode.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.isSecondaryButtonDown()) {
                        double posX = event.getX();
                        double posY = event.getY();
                        editorRightClickMenu.setXY(posX, posY);
                        editorRightClickMenu.checkButtonStatus();
                        editorRightClickMenu.show(htmlSourceCode, event.getScreenX(), event.getScreenY());
                    } else {
                        editorRightClickMenu.hide();
                    }
                }
            });
        */
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

            htmlSourceCode.setStyleSpans(0, computeHighlighting(htmlSourceCode.getText()));
            defaultStyle();
            pageViewList.put(page.getPageName(), page);
        }

            Main.getVignette().setPageViewList(pageViewList);

            HashMap<String, String> connectionEntries = new HashMap<>();
            for (HashMap.Entry<String, String> entry : page.getPagesConnectedTo().entrySet()) {
                String[] temp = entry.getValue().split(",");
                for (String x : temp)
                    connectionEntries.put(x.trim(), entry.getKey());
            }
//            String questionType = "";
//            if (page.getQuestionType() == null || "".equalsIgnoreCase(page.getQuestionType())) {
//                String htmlText = htmlSourceCode.getText();
//                Pattern pattern = Pattern.compile("questionType= '(.*?)';\n", Pattern.DOTALL);
//                Matcher matcher = pattern.matcher(htmlText);
//                if (matcher.find()) {
//                    questionType = matcher.group(0).split("=")[1].trim().replaceAll("'", "").replaceAll(";", "");
//                    System.out.println("PAGE QUESTION TYPE FROM MATCHER: " + questionType);
//                }
//            } else {
//                questionType = page.getQuestionType();
//            }

        branchingType.setValue(page.getQuestionType());
            if(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()>0)
                numberofAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()+"");
            else
                numberofAnswerChoiceValue.set(connectionEntries.size()+"");

        branchingTypeProperty.set(page.getQuestionType());

//            if (page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() > 0)
//                numberOfAnswerChoice.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() + "");
//            else if (connectionEntries.size() != 0)
//                numberOfAnswerChoice.setText(connectionEntries.size() - 1 + "");
            nextPageAnswers.setDisable(false);

            //-----------------   dealing with keyboard shortcuts  -----------------------------------------
            if (htmlSourceCode.getScene() == null)
                System.out.println("Scene is null for some reason");


//            System.out.println("====================================================================");
//            System.out.println("Branching type: "+branchingType.getValue());
//            System.out.println("Branching type property: "+branchingTypeProperty.getValue());
//            System.out.println("Page question type: "+page.getQuestionType());
//            System.out.println("====================================================================");
            htmlSourceCode.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

            //htmlSourceCode.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination incFont = new KeyCodeCombination(KeyCode.EQUALS,KeyCombination.CONTROL_DOWN);
            final KeyCombination decFont = new KeyCodeCombination(KeyCode.MINUS,KeyCombination.CONTROL_DOWN);
            final KeyCombination search = new KeyCodeCombination(KeyCode.F,KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                //System.out.println(ke);
                if (incFont.match(ke)) {
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

        String pageType = page.getPageType();
        System.out.println(pageType);

        //disabling buttons according to page type



        switch(pageType)
        {
            case "Problem":
            case "response_correct":
            case "response_incorrect":
                addImage.setDisable(false);
                addVideo.setDisable(false);
                addInputField.setDisable(true);
                addImageInputField.setDisable(true);
                break;

            case "login":
            case "whatLearned":
            case "Credit":
            case "completion":
                addImage.setDisable(true);
                addVideo.setDisable(true);
                addInputField.setDisable(true);
                addImageInputField.setDisable(true);
                break;


            case "problemStatement":
                addImage.setDisable(false);
                addVideo.setDisable(true);
                addInputField.setDisable(true);
                addImageInputField.setDisable(true);
                break;

            default:
                addImage.setDisable(false);
                addVideo.setDisable(false);
                addInputField.setDisable(false);
                addImageInputField.setDisable(false);
        }

        branchingType.getItems().add(BranchingConstants.SIMPLE_BRANCH);
        numAnswers.setDisable(true);
        numberOfAnswerChoice.setDisable(true);


        if(branchingType.getItems().size()>1) {
            int size = branchingType.getItems().size();
            branchingType.getItems().remove(1, size);
        }

        if (pageType.equalsIgnoreCase("q") || pageType.equalsIgnoreCase("Custom")) {
            branchingType.getItems().addAll(BranchingConstants.RADIO_QUESTION,
                    BranchingConstants.CHECKBOX_QUESTION);
            numAnswers.setDisable(false);
            numberOfAnswerChoice.setDisable(false);
        }




        lastPageOptions.setOnAction(event -> {
            GridPaneHelper lastPageGrid = new GridPaneHelper();

            lastPageGrid.setResizable(false);

            //todo add titles
            Label pageNameLabel = new Label("Page Name");
            Label hasLastPageFn = new Label("Include last page function?");
            lastPageGrid.add(pageNameLabel,0,0,1,1);
            lastPageGrid.add(hasLastPageFn,5,0,1,1);


            //add all options to the gridpane
            for(int i=0;i<pageNameList.size();i++)
            {

                int pos = i;
                //create a label with the name of the page
                Label pageLabel = new Label(pageNameList.get(i));
                lastPageGrid.add(pageLabel,0,i+1,4,1);


                //create a checkbox associated with that page
                CheckBox checkBox = new CheckBox();



                checkBox.setSelected(lastPageValueMap.get(pageNameList.get(i)));


                //todo add event handler
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                        String currentPageName = pageNameList.get(pos);
                        //box has been ticked
                        if(newValue) {
                            boolean  status = setLastPageVariable(currentPageName);
                            if(!status) {
                                checkBox.setIndeterminate(true);
                                checkBox.setSelected(false);
                            }
                        }
                        //box has been UNticked
                        else {
                            if (!checkBox.isIndeterminate())
                                unsetLastPageVariable(currentPageName);
                        }
                    }
                });
                lastPageGrid.add(checkBox,5,i+1,1,1);
            }
            lastPageGrid.create("Select Last Page(s) ","","Close");

        });
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
            if(pageOne.getPreviousConnection()!=null && pageOne.getConnectedTo()!=null && !"".equalsIgnoreCase(pageOne.getConnectedTo()) && BranchingConstants.SIMPLE_BRANCH.equalsIgnoreCase(pageOne.getQuestionType()))
                previousConnection = pageOne.getPreviousConnection();
            Group grp = connect.connectSourceAndTarget(toConnect, previousConnection);
            if(grp!=null){
                pageOne.setConnectedTo(two.getText());
                pageOne.setNextPages(two.getText(), grp);
                pageTwo.setNextPages(pageOne.getPageName(),grp);
            }
        }


    }


    public void showOrHideScript() {
        String target = "<!--Do Not Change content in this block-->([\\S\\s]*?)<!--Do Not Change content in this block-->";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);

        if (m.find()) {
            if (getScriptIsHidden()) {
                //showHideScript.setText("Show Script");
                htmlSourceCode.unfoldText(m.start());
                setScriptIsHidden(false);
            } else {
                //showHideScript.setText("Hide Script");
                htmlSourceCode.foldText(m.start(),m.end());
                setScriptIsHidden(true);
            }
        }
    }


    /**
     *
     * todo change to setLastPage = 0
     * @param pageName
     */
    public boolean setLastPageVariable(String pageName)
    {

        System.out.println("Setting new last Page");
        HTMLEditorContent currentPageContent = htmlEditorContent.get(pageName);


        System.out.println("selected page is? = "+pageName);
        System.out.println("currentPageContent is null? = "+currentPageContent);


        Pattern pattern = Pattern.compile("lastPage = 0;");
        Matcher matcher;


        //dealing with the page we are currently on
        if(pageName.equals(Main.getVignette().getCurrentPage().getPageName())) {
            //show script in case its hidden if the page youre updating is the current page
            boolean wasScriptHidden;
            if (getScriptIsHidden()) {
                wasScriptHidden = true;
                showScript();
            } else
                wasScriptHidden = false;

            matcher = pattern.matcher(htmlSourceCode.getText());
            if(matcher.find()) {
                //System.out.println("found lastPage Comment ");

                htmlSourceCode.selectRange(matcher.start(),matcher.end());
                htmlSourceCode.replaceSelection("lastPage = 1;");

                currentPageContent.getPage().setPageData(htmlSourceCode.getText());
                //hide script if required
                if(wasScriptHidden)
                    hideScript();

                //change the value in the map
                lastPageValueMap.put(pageName, true);

                return true;
            }
            else
                System.out.println("did not find last page Comment");
        }

        //dealing with pages that arent currently open
        else
        {
            String otherPageData = currentPageContent.getPageData();
            matcher = pattern.matcher(otherPageData);
            if(matcher.find()) {
                //System.out.println("found lastPage Comment ");


                otherPageData = otherPageData.replaceAll("lastPage = 0;","lastPage = 1;");
                currentPageContent.getPage().setPageData(otherPageData);

                //change the value in the map
                lastPageValueMap.put(pageName, true);

                return true;
            }
            else
                System.out.println("did not find last page Comment");
        }
        return false;
    }


    public void unsetLastPageVariable(String pageName)
    {

        //removing value from map
        lastPageValueMap.put(pageName,false);

        //set present page to be the new last page
        System.out.println("Removing last Page function from = "+pageName);


        VignettePage currentPage = Main.getVignette().getCurrentPage();
        HTMLEditorContent otherPageContent = htmlEditorContent.get(pageName);

        Pattern pattern = Pattern.compile("lastPage = 1;");
        Matcher matcher;

        //if we're removing the function from the page we're on
        if(pageName.equals(currentPage.getPageName())) {

            boolean wasScriptHidden;
            if (getScriptIsHidden()) {
                wasScriptHidden = true;
                showScript();
            } else
                wasScriptHidden = false;

            matcher = pattern.matcher(htmlSourceCode.getText());
            if (matcher.find()) {
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection("lastPage = 0;");
                currentPage.setPageData(htmlSourceCode.getText());
            }

            if(wasScriptHidden)
                hideScript();
        }


        //any page not currently open
        else
        {

            matcher = pattern.matcher(otherPageContent.getPageData());
            if (matcher.find()) {

                String otherPageData = otherPageContent.getPageData();
                otherPageData = otherPageData.replaceAll("lastPage = 1;","lastPage = 0;");

                otherPageContent.getPage().setPageData(otherPageData);
            }
        }
    }




    public void createLastPageOptionDialog()
    {
        GridPaneHelper helper = new GridPaneHelper();


       // ArrayList lastPages = Main.getVignette().getLastPageList();
       // int size = lastPages.size();


       // for (int i = 0; i < size; i++) {
       //     addNextPageTextFieldToGridPane(this.countOfAnswer++, helper, editNextPageAnswers, false);
       // }

        //defaultNextPageBox = helper.addDropDownWithDefaultSelection(pageNameList.stream().toArray(String[]::new), 0,1, optionEntries.get("default"));


    }



















    public void createNextPageAnswersDialog(Boolean editNextPageAnswers, Boolean noquestionSelected) {
        //GridPaneHelper helper = new GridPaneHelper();
        ComboBox defaultNextPageBox = null;

       // page.clearNextPagesList();
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
        content.editNextPageAnswers(branchingType.getSelectionModel().getSelectedItem().toString());
    }
    public void editNextPageLinks(ActionEvent actionEvent){
        if(!"".equalsIgnoreCase(numberOfAnswerChoice.getText()) && Integer.parseInt(numberOfAnswerChoice.getText())>0){
            nextPageAnswers.fire();
            content.createNextPageAnswersDialog(false, false);
        }
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
        if(value == null)
            branchingType.setValue(BranchingConstants.SIMPLE_BRANCH);
        value = (String) branchingType.getSelectionModel().getSelectedItem();
        if(value.equals(BranchingConstants.SIMPLE_BRANCH)) {
            if("".equalsIgnoreCase(numberOfAnswerChoice.getText())){
                nextPageAnswers.setDisable(false);
            }
            numberOfAnswerChoice.setText("0");
            numberOfAnswerChoice.setDisable(false);
        }
        else{
            numberOfAnswerChoice.setDisable(false);
//            if(Integer.parseInt(numberOfAnswerChoice.getText())<=0)
//                nextPageAnswers.setDisable(true);
        }
    }

    public void onNumberChoiceKeyRelased(KeyEvent keyEvent) {

        try{
            Integer.parseInt(keyEvent.getText());
            nextPageAnswers.setDisable(false);
        }
        catch (Exception e){
            System.out.println("Exception at onNumberChoiceKeyRelased: "+e.getMessage());
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

    public void setScriptIsHidden(boolean value)
    {
        this.isScriptHidden = value;
        if(value)
            showHideScript.setText("Show Script");
        else
            showHideScript.setText("Hide Script");
    }

    public boolean getScriptIsHidden()
    {
        return this.isScriptHidden;
    }

    public void hideScript()
    {
        String target = "<!--Do Not Change content in this block-->([\\S\\s]*?)<!--Do Not Change content in this block-->";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);

        if(m.find()) {
            setScriptIsHidden(true);
            htmlSourceCode.foldText(m.start(), m.end());
        }
    }

    public void showScript()
    {
        //String target = "<script>([\\S\\s]*?)</script>";
        String target = "<!--Do Not Change content in this block-->([\\S\\s]*?)<!--Do Not Change content in this block-->";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);

        if(m.find()) {
            setScriptIsHidden(false);
            htmlSourceCode.unfoldText(m.start());
        }
    }

    public void setScriptIndex(int index)
    {
        this.scriptTagIndex = index;
        setScriptIsHidden(false);
    }

    public int getScriptTagIndex()
    {
        return this.scriptTagIndex;
    }



    public CodeArea getHtmlSourceCode()
    {
        return this.htmlSourceCode;
    }

    public EditorRightClickMenu getEditorRightClickMenu()
    {
        return this.editorRightClickMenu;
    }

    public HashMap getHTMLContentEditor()
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
    public Tab getVignetteTab() { return vignetteTab; }

    public void setNumberofAnswerChoiceValue(String numberofAnswerChoiceValue) {
        this.numberofAnswerChoiceValue.set(numberofAnswerChoiceValue);
    }
    public void setVignetteTab(Tab vignetteTab) { this.vignetteTab = vignetteTab; }
    public TabPane getTabPane() { return tabPane; }
    public void setTabPane(TabPane tabPane) { this.tabPane = tabPane; }
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

