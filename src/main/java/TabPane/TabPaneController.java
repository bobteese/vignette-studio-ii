/* ******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package TabPane;

import Application.Main;
import ConstantVariables.BranchingConstants;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import MenuBar.File.FileMenuItem;
import Utility.Utility;
import Vignette.Framework.ReadFramework;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.ConnectPages;
import Vignette.Page.PageMenu;
import Vignette.Page.Questions;
import Vignette.Page.VignettePage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import org.apache.commons.io.IOUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


/**
 * This class is used to initialaze the left panel of list of images
 * ,handles the drag and drop functionality and creates a new vignette page for each image dropped
 **/
//public class TabPaneController implements Initializable  {
public class TabPaneController extends ContextMenu implements Initializable {

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
    Button addImage;
    @FXML
    Button addVideo;
    @FXML
    Button addInputField;
    @FXML
    Button addImageInputField;

    @FXML
    Slider fontSlider;
    @FXML
    ScrollPane scrollPane;
    @FXML
    Button nextPageAnswers;
    @FXML
    Label pageName;
    @FXML
    Button lastPageOptions;
    @FXML
    Button deleteQuestions;
    @FXML
    Button showHideScript;

    SimpleStringProperty numberofAnswerChoiceValue = new SimpleStringProperty();
    SimpleStringProperty branchingTypeProperty = new SimpleStringProperty();

    public TabPaneController() {
    }

    Image defaultImage = new Image(ConstantVariables.DEFAULT_RESOURCE_PATH);
    HashMap<String, String> pageIds = new HashMap<>();
    HashMap<String, Image> imageMap = new HashMap<>();

    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();

    private List<String> pageNameList = new ArrayList<>();


    private final HashMap<String, Boolean> lastPageValueMap = Main.getVignette().getLastPageValueMap();

    private int firstPageCount = 0;
    private final HashMap<String, VignettePage> pageViewList = Main.getVignette().getPageViewList();
    private final HashMap<String, HTMLEditorContent> htmlEditorContent = new HashMap<>();

    HTMLEditorContent content;
    Button one;
    Button two;
    VignettePage pageOne;
    VignettePage pageTwo;
    Boolean isConnected = false;

    HashMap<String, ArrayList<Group>> listOfLineConnector;
    HashMap<String, Button> buttonPageMap = new HashMap<>();
    RightClickMenu rightClickMenu;
    private EditorRightClickMenu editorRightClickMenu;
    private boolean isScriptHidden = true;
    private Features featureController;

    public Tab getPagesTab() {
        return pagesTab;
    }

    public HashMap<String, ArrayList<Group>> getListOfLineConnector() {
        return listOfLineConnector;
    }


    /**
     * This method initialize the list when the controller loads
     **/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.getVignette().setController(this);

        //==============Read a framework====================
        if (Main.getVignette().getHtmlFiles() != null && Main.getVignette().getHtmlFiles().size() != 0) {
            Main.getVignette().getHtmlFiles().clear();
            Main.getVignette().setHtmlFiles(new ArrayList<>());
        }
        //=============================================
        ReadFramework.read(ReadFramework.getUnzippedFrameWorkDirectory());
        //=============================================

        ArrayList<Label> labels = new ArrayList<>();
        //==============Read a framework====================
//        MenuBarController menuBarController = new MenuBarController();
        //==============Read a framework====================

        // VirtualizedScrollPane<InlineCssTextArea> vsPane = new VirtualizedScrollPane<>(htmlSourceCode);
        this.htmlSourceCode = new CodeArea();
        this.htmlSourceCode.setId("styled-text-area");
        pageNameList.add("No Link");

        //coupling virtual scroll pane because default inline
        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(htmlSourceCode);

        //making sure the vspane stretches to fit the anchorpane
        AnchorPane.setTopAnchor(vsPane, 0.0);
        AnchorPane.setRightAnchor(vsPane, 0.0);
        AnchorPane.setBottomAnchor(vsPane, 0.0);
        AnchorPane.setLeftAnchor(vsPane, 0.0);

        //adding the vsPane that contains the textarea as a child of the anchorpane
        anchorPANE.getChildren().add(vsPane);

        for (int i = 0; i < Main.getVignette().getHtmlFiles().size(); i++) {
            labels.add(new Label(Main.getVignette().getHtmlFiles().get(i)));

        }
        String style = htmlSourceCode.getStyle();
        //Slider for adjusting font size
        this.fontSlider.setMin(1);
        this.fontSlider.setMax(26);
        this.fontSlider.setValue(12);
        fontSlider.setShowTickLabels(true);
        fontSlider.setShowTickMarks(true);
        fontSlider.setBlockIncrement(1);
        if (!style.equalsIgnoreCase("")) {
            String target = "(?<=:)(.*?)(?=px)";
            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(style);
            if (m.find()) {
                String match = m.group(0);
                double size = Double.parseDouble(match);
                fontSlider.setValue(size);
//                fontText.setStyle(buttonStyle+size+"px;");
            }
        } else {
            fontSlider.setValue(11);
//            fontText.setStyle(buttonStyle + defaultSize);
        }
        fontSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                fontText.setStyle(buttonStyle+ newValue+ "px;");
                htmlSourceCode.setStyle("-fx-font-size: " + fontSlider.getValue() + "px;");
            }
        });
        rightAnchorPane.addEventHandler(KeyEvent.ANY, event -> {
            KeyCombination controlV = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.CONTROL_DOWN);
            if (controlV.match(event)) {
                if (getPageToCopy() != null) {
                    PageMenu.copyPasteVignettePage(pageToCopy, buttonPageMap.get(getPageToCopy().getPageName()));
                } else {
                    System.out.println("NO PAGE TO COPY AND PASTE");
                }
            }
        });


        //creating the features controller
        this.featureController = new Features(this);

        //-------------------------- ADDING RIGHT CLICK MENUS-----------------------------------------------------------
        // Adding right click functionality to the IVET editor drag and drop right anchor pane
        this.rightClickMenu = new RightClickMenu(this);
        rightClickMenu.setAutoHide(true);

        rightAnchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isSecondaryButtonDown()) {
                    double posX = event.getX();
                    double posY = event.getY();

                    rightClickMenu.setXY(posX, posY);
                    rightClickMenu.show(rightAnchorPane, event.getScreenX(), event.getScreenY());
                } else {
                    rightClickMenu.hide();
                }
            }
        });

        // Adding right click functionality to the InlineCssTextArea
        this.editorRightClickMenu = new EditorRightClickMenu(this, htmlSourceCode);
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

        //------------------------------------------------------------------------------------------------------------------
//        numberOfAnswerChoice.textProperty().bindBidirectional(numberofAnswerChoiceValueProperty());
//        branchingType.valueProperty().bindBidirectional(branchingTypeProperty());
        //------------------------------------------------------------------------------------------------------------------

        listOfLineConnector = new HashMap<>();

        //-----------------------------------------------------------------------
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        if (t1.getText().equalsIgnoreCase("Pages")) {
                            if (pageViewList.size() > 1 || pageNameList.size() > 1)
                                nextPageAnswers.setDisable(false);
                        }
                    }
                }
        );
        FXCollections.observableArrayList(ConstantVariables.LOGIN_PAGE_TYPE,
                ConstantVariables.PROBLEM_PAGE_TYPE, ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE, ConstantVariables.QUESTION_PAGE_TYPE,
                ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE, ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE, ConstantVariables.WHAT_LEARNED_PAGE_TYPE,
                ConstantVariables.CREDIT_PAGE_TYPE, ConstantVariables.COMPLETION_PAGE_TYPE, ConstantVariables.CUSTOM_PAGE_TYPE);
//        if(Main.defaultFramework)
//            imageListView.setItems(FXCollections.observableList(items));
//        else

        Collections.sort(Main.getVignette().getHtmlFiles());
        imageListView.setItems(FXCollections.observableList(Main.getVignette().getHtmlFiles()));
        imageListView.setMaxWidth(150.0);
        imageListView.setMaxHeight(1000.0);

        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle("-fx-background-color: black; -fx-text-fill: white;-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        popupMsg.setText("Drag and drop on right plane range");
        imageListView.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popup.show(imageListView, event.getScreenX(), event.getScreenY() + 10);
            }
        });

        imageListView.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popup.hide();
            }
        });

        AtomicLong totalHeight = new AtomicLong(0);
        /* *
         * In order to put images into the Page type image pane, first you have to identify the page types here.
         * ORDER IS IMPORTANT.
         * After mentioning it here, make changes in setCellFactory.
         */
        imageListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                private final ImageView imageView = new ImageView();

                @Override
                public void updateItem(String name, boolean empty) {
                    if (name == null)
                        return;
                    super.updateItem(name, empty);

                    VBox vbox = new VBox();
                    vbox.setAlignment(Pos.CENTER);
                    //THIS displays the images of the page types on the listView

                    if (name.lastIndexOf(".") > -1) {
                        name = name.substring(0, name.lastIndexOf("."));
                    }
                    Image buttonImage = null;
                    if (Main.getVignette().getImagesPathForHtmlFiles() != null && Main.getVignette().getImagesPathForHtmlFiles().get(name) != null) {
                        try {
                            InputStream is = new FileInputStream(ReadFramework.getUnzippedFrameWorkDirectory() + "/" + Main.getVignette().getImagesPathForHtmlFiles().get(name));
                            buttonImage = new Image(is);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        buttonImage = new Image(getClass().getResourceAsStream(ConstantVariables.DEFAULT_RESOURCE_PATH));
                    }

                    vbox.setOnMouseMoved(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            vbox.setStyle("-fx-background-color: lightgray");
                        }
                    });

                    vbox.setOnMouseExited(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            vbox.setStyle("");
                        }
                    });

                    imageView.setImage(buttonImage);
                    Label label = new Label(name);

                    label.setAlignment(Pos.BOTTOM_CENTER);
                    vbox.getChildren().add(imageView);
                    vbox.getChildren().add(label);
                    setGraphic(vbox);

                    totalHeight.set(totalHeight.get() + ((long) vbox.getHeight()));
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

        imageListView.setStyle(" -fx-padding: 20px 0px 30px 0px;");
        imageListView.prefHeightProperty().bind(tabPane.heightProperty());

        /*  todo THIS WAS THE PREVIOUS WAY WE SET VALUES IN THE COMBO BOX
         branchingType.getItems().addAll(BranchingConstants.SIMPLE_BRANCH, BranchingConstants.RADIO_QUESTION,
         BranchingConstants.CHECKBOX_QUESTION);
         */

        if (Main.getVignette().getPageViewList() != null && Main.getVignette().getPageViewList().size() > 0) {
            this.getAnchorPane().getChildren().clear();
            FileMenuItem.addButtonToPane(Main.getVignette(), this);
            for (Map.Entry<String, VignettePage> e : Main.getVignette().getPageViewList().entrySet()) {
                this.makeFinalConnection(e.getValue());
            }
        }
    }


    // "|(?<SCRIPT><!-- //////// Do Not Change content in this block //////// -->[^<>]+<!-- //////// Do Not Change content in this block //////// -->)")

    private static final Pattern XML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
            + "|(?<COMMENT><!--[^<>]+-->)");
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
     *
     * @param text takes the text
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
            } else {
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

    /**
     * This function greys out all the text within the script tags
     */
    public void scriptStyle() {
        String target = "<script>([\\S\\s]*?)</script>";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);
        if (m.find())
            htmlSourceCode.setStyleClass(m.start(), m.end(), "script");
    }

    /**
     * This function is called to revert the formatting of the textarea back to its original format
     */
    public void defaultStyle() {
        htmlSourceCode.setStyleSpans(0, computeHighlighting(htmlSourceCode.getText()));
        scriptStyle();
    }


    public void readZipStream(InputStream in) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(in);
        while (zipIn.getNextEntry() != null) {
            readContents(zipIn);
            zipIn.closeEntry();
        }
    }

    private void readContents(InputStream contentsIn) throws IOException {
        byte[] contents = new byte[4096];
        int direct;
        while ((direct = contentsIn.read(contents, 0, contents.length)) >= 0) {
            System.out.println("Read " + direct + "bytes content.");
        }
    }

    /**
     * This method is called when an image is dropped into the anchor pane.
     * the method is called in resources/FXML tabs.fxml
     ***/
    public void imageDropped(DragEvent event) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */

        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString() || true) { // if the dragboard has text accept it
            Clipboard c = Clipboard.getSystemClipboard();
            String imageType = c.getString();
            double posX = event.getX();
            double posY = event.getY();

            /*
             * When you drag and drop the page icon from the left, the following code decides what image is used for the
             * page after drag and dropping.
             */


            for (String s : Main.getVignette().getHtmlFiles()) {
                pageIds.put(s.split("\\.")[0], s.split("\\.")[0]);
            }


            VignettePage page = createPage(imageType);
            ImageView droppedView = null;
            if (page != null) {
                if (Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()) != null) {
                    File f = new File(ReadFramework.getUnzippedFrameWorkDirectory() + "/" + Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                    droppedView = new ImageView(f.toURI().toString());
                } else {
                    droppedView = new ImageView(defaultImage); // create a new image view
                }
            }
            // add the dropped node to the anchor pane. Here a button is added with image and text.
            if (page != null) {
                Button pageViewButton = createVignetteButton(page, droppedView, posX, posY, page.getPageType());
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
     *
     * @param page the vignette page created
     * @param posX X coordinate of right click
     * @param posY Y coordinate of right click
     */
    public void createPageFromRightClick(VignettePage page, double posX, double posY) {

        if (page != null) {
            ImageView droppedView;
            System.out.println("TYPE: " + page.getPageType());
            if (Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()) != null) {
                System.out.println("IMAGES: " + ReadFramework.getUnzippedFrameWorkDirectory() + Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                File f = new File(ReadFramework.getUnzippedFrameWorkDirectory() + Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                droppedView = new ImageView(f.toURI().toString());
            } else {
                droppedView = new ImageView(defaultImage); // create a new image view
            }
            Button pageViewButton = createVignetteButton(page, droppedView, posX, posY, page.getPageType());
        }
    }


    /**
     * This method is required to get the drag and drop to work as it accepts the incoming drag from another node
     * the method is called in resources/FXML tabs.fxml
     ***/
    public void imageDragOver(DragEvent event) {
        /* show to the user that it is an actual gesture target */
        if (event.getDragboard().hasString() || event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * This function makes use of DragEvents and the information stored on the DragBoard to create the required HTML
     * page in the vignette editor. The images on the listView are associated with the appropriate HTML pages in order
     * to do so.
     *
     * @return
     */
    public VignettePage createPage(String newPageType) {
        String pageType = newPageType;
        GridPaneHelper newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        CheckBox checkBox = new CheckBox("First Page"); //newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        if (pageType.lastIndexOf(".") > -1)
            pageType = pageType.substring(0, pageType.lastIndexOf("."));
        if (pageType.equalsIgnoreCase(ConstantVariables.LOGIN_PAGE_TYPE)) {
            checkBox.setSelected(true);
            checkBox.setDisable(true);
        }
        //textbox to enter page name
        TextField pageName = newPageDialog.addTextField(1, 2, 400);
        //setting the default pageID

        pageName.setText(pageIds.get(pageType));
        //System.out.println("page map:  "+pageIds);


        String pageTitle = "Create New " + pageType + " Page";
        if (ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE.equalsIgnoreCase(pageType) || ConstantVariables.LOGIN_PAGE_TYPE.equalsIgnoreCase(pageType)) {
            pageName.setEditable(false);
        }
        boolean cancelClicked = newPageDialog.createGrid(pageTitle, "Please enter the page name", "Ok", "Cancel");

        if (!cancelClicked) return null;
        final String regexForPageName = "^[a-zA-Z0-9_-]*$";
        String pageText = pageName.getText();
//                .trim().replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "-");

        boolean isValid = !pageNameList.contains(pageText) && pageText.length() > 0 && pageText.matches(regexForPageName)
                && !pageText.equalsIgnoreCase("No Link");
        //checking whether the user has entered a unique pageID
        while (!isValid) {
            String message = pageText.equalsIgnoreCase("No Link") ? "Page Name cannot be 'No Link'" :
                    pageNameList.contains(pageText) ? " All page names must be unique"
                            : pageText.length() == 0 ? "Page id should not be empty" :
                            !pageText.matches(regexForPageName) ? "Page name can be alphanumeric with underscores and hyphens" :
                                    "";
            //creating an information alert to deal--------------
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setContentText(message);
            alert.showAndWait();
            pageName.setText(pageText.replaceAll("[^a-zA-Z0-9\\-_]", "-"));
            //---------------------------------------------------
            cancelClicked = newPageDialog.showDialog();
            pageText = pageName.getText();
            isValid = !pageNameList.contains(pageText) && pageText.length() > 0 && pageText.matches(regexForPageName);
            if (!cancelClicked) return null;
        }

        boolean check = checkBox.isSelected();
        if (check) {
            firstPageCount++;
            Main.getVignette().setHasFirstPage(true);
        }
        pageNameList.add(pageText);

        pageNameList = pageNameList.stream().sorted().collect(Collectors.toList());
        //newly created page doesn't have the setLastPage(); function
        lastPageValueMap.put(pageText, false);
        Main.getVignette().setLastPageValueMap(lastPageValueMap);
        //creating a new Vignette page based off user provided information.
        VignettePage page = new VignettePage(pageText.trim(), check, pageType);
        String text = Main.getVignette().getController().getPageDataWithPageType(page, pageType);
        page.setPageData(text);
        page.setPageName(pageText);
        if (pageNameList.size() > 1)
            nextPageAnswers.setDisable(false);
        return page;
    }


    public String getPageDataWithPageType(VignettePage page, String pageType) {
        String text = "";
        try {
            ZipFile zipFile = new ZipFile(Main.getFrameworkZipFile());
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry entry = null;
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                if (entry.getName().equalsIgnoreCase(page.getPageType()))
                    break;
            }
            if (entry != null) {
                InputStream stream = new FileInputStream(ReadFramework.getUnzippedFrameWorkDirectory() + "/pages/" + pageType + ".html");
                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
                text = writer + "\n\n";
            } else {
                System.out.println("NO ENTRY FOUND");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * This was the function used in the original vignette studio ii to create pages after drag and dropping
     * the plain orange icon. Once the icon is dropped it will open a dialog box that prompts you to choose the required
     * page type and type in a valid page id.
     * <p>
     * This is not used for the drag and drop method of creating pages anymore but rather when you use the new option from
     * the right click menu anywhere on the right anchor pane.
     * <p>
     * Called in TabPane.RightClickMenu.java
     */
    public VignettePage createNewPageDialog(boolean pastePage, String pageType) {
        GridPaneHelper newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        ComboBox dropDownPageType = newPageDialog.addDropDown(ConstantVariables.listOfPageTypes, 1, 2);
        TextField pageName = newPageDialog.addTextField(1, 3, 400);

        dropDownPageType.setOnAction(event -> {
            String value = (String) dropDownPageType.getValue();
            if (value.equals(ConstantVariables.LOGIN_PAGE_TYPE)) pageName.setText("login");
            if (value.equals(ConstantVariables.QUESTION_PAGE_TYPE)) pageName.setText("q");
            if (value.equals(ConstantVariables.WHAT_LEARNED_PAGE_TYPE)) pageName.setText("whatLearned");
            if (value.equals(ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE)) pageName.setText("q");
            if (value.equals(ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE)) pageName.setText("q");
            if (value.equals(ConstantVariables.CREDIT_PAGE_TYPE)) pageName.setText("credits");
            if (value.equals(ConstantVariables.COMPLETION_PAGE_TYPE)) pageName.setText("Completion");
            if (value.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) pageName.setText("");
            if (value.equals(ConstantVariables.PROBLEM_PAGE_TYPE)) pageName.setText("");
            if (value.equals(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE)) pageName.setText("problemStatement");
        });
        if (pastePage && pageType != null) {
            dropDownPageType.setValue(pageType);
            dropDownPageType.setDisable(true);
        }

        boolean cancelClicked = newPageDialog.createGrid("Create New page", "Please enter the page name", "Ok", "Cancel");
        if (!cancelClicked) return null;
        final String regexForPageName = "^[a-zA-Z0-9_-]*$";

        // if page ids exists  or if the text is empty
        boolean isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0 && !dropDownPageType.getValue().equals("Please select page type")
                && pageName.getText().matches(regexForPageName)
                && !pageName.getText().equalsIgnoreCase("No Link");
        while (!isValid) {
            String message =  pageName.getText().equalsIgnoreCase("No Link")? "Page Name cannot be 'No Link'" :
                    dropDownPageType.getValue().equals("Please select page type") ? "Select a valid Page Type" :
                    pageNameList.contains(pageName.getText()) ? " There is already a page with this name. Every page name must be unique."
                            : pageName.getText().length() == 0 ? "Page id should not be empty" :
                            !pageName.getText().matches(regexForPageName) ? "Page name can be alphanumeric with underscores and hyphens" :"";


            //creating an information alert to deal--------------
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setContentText(message);
            alert.showAndWait();
            //---------------------------------------------------
            pageName.setText(pageName.getText().replaceAll("[^a-zA-Z0-9\\-_]", "-"));

            cancelClicked = newPageDialog.showDialog();
            isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0
                    && !dropDownPageType.getValue().equals("Please select page type")
                    && pageName.getText().matches(regexForPageName)
                    && !pageName.getText().equalsIgnoreCase("No Link");
            if (!cancelClicked) return null;

        }

        String pageNameString = pageName.getText();
        VignettePage page = new VignettePage(pageNameString, false, dropDownPageType.getValue().toString());
        pageNameList.add(pageNameString);
        pageViewList.put(pageNameString, page);
        pageNameList = pageNameList.stream().sorted().collect(Collectors.toList());
        //newly created page doesn't have the setLastPage(); function
        lastPageValueMap.put(pageName.getText(), false);
        Main.getVignette().setLastPageValueMap(lastPageValueMap);
        dropDownPageType.setDisable(false);
        return page;
    }


    private void focusState(boolean value) {
        if (value) {
            System.out.println("Focus GAINED TO button");
        } else {
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
     *
     * @param posX        contains the mouse position
     * @param posY        contains the mouse position
     * @param page        creates a vignette page class for each page
     * @param droppedView Image view
     **/
    public Button createVignetteButton(VignettePage page, ImageView droppedView, double posX, double posY, String type) {
        String buttonText = page.getPageName().trim();
        Button vignettePageButton = new Button(buttonText, droppedView);
        buttonPageMap.put(page.getPageName(), vignettePageButton);

        vignettePageButton.relocate(posX, posY);
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
            vignettePageButton.setLayoutY(mouseEvent.getSceneY() + deltaY[0]);
            page.setPosX(mouseEvent.getSceneX() + delatX[0]);
            page.setPosY(mouseEvent.getSceneY() + deltaY[0]);
            pageViewList.put(page.getPageName(), page);
            Main.getVignette().setPageViewList(pageViewList);
        });


        vignettePageButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vignettePageButton.addEventFilter(KeyEvent.ANY, event -> {
                KeyCombination controlC = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.CONTROL_DOWN);
                if (controlC.match(event)) {
                    pageToCopy = page;
                }
            });
        });

        if (page.getQuestionType().equalsIgnoreCase("radio")) {
            branchingTypeProperty.set(BranchingConstants.RADIO_QUESTION);
        } else if (page.getQuestionType().equalsIgnoreCase("checkbox")) {
            branchingTypeProperty.set(BranchingConstants.CHECKBOX_QUESTION);
        } else {
            branchingTypeProperty.set(BranchingConstants.SIMPLE_BRANCH);
        }
        numberofAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() + "");

        content = new HTMLEditorContent(htmlSourceCode,
                type, page,
                pageNameList,
                pageName, nextPageAnswers);

        htmlEditorContent.put(page.getPageName(), content);


        vignettePageButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    openPage(page, type);
                }
            }

            // this is the code the deals with opening the right click menu
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                //prevents the right click menu from staying open if you right click before you right click the page button
                this.rightClickMenu.hide();
                /*
                Creating the right click vignette page menu and adding it to the button representing the page.
                 */
                PageMenu pageMenu = new PageMenu(page, vignettePageButton, this);
                vignettePageButton.setContextMenu(pageMenu);
                pageMenu.setOnAction(e -> {
                    if (((MenuItem) e.getTarget()).getText().equals("Connect")) {
                        isConnected = true;
                        one = vignettePageButton;
                        pageOne = page;
                    }
                });
            }
        });

        vignettePageButton.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                DialogHelper confirmation = new DialogHelper(Alert.AlertType.CONFIRMATION,
                        "Delete Page",
                        null,
                        "Are you sure you want to delete this page?",
                        false);
                if (confirmation.getOk()) {
                    if (page.isFirstPage()) firstPageCount = 0;
                    this.pageNameList.remove(page.getPageName());

                    //removing page from the map
                    lastPageValueMap.remove(page.getPageName());
                    Main.getVignette().setLastPageValueMap(lastPageValueMap);


                    if (this.listOfLineConnector.containsKey(vignettePageButton.getText())) {
                        ArrayList<Group> connections = this.listOfLineConnector.get(vignettePageButton.getText());
                        connections.stream().forEach(connection -> {
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
                    this.rightAnchorPane.getChildren().stream().forEach(element -> {
                        System.out.println(element);
                    });
                    pagesTab.setDisable(true);
                }
            }
        });
        this.rightAnchorPane.getChildren().add(vignettePageButton);
        page.setPosX(posX);
        page.setPosY(posY);
        pageViewList.put(page.getPageName(), page);
        // -------end of mouse event methods-------
        return vignettePageButton;
    }

    /**
     * Functionality for opening a vignette page. Button status' and so on are set here.
     *
     * @param page page
     * @param type type
     */
    public void openPage(VignettePage page, String type) {
        String text;
        pagesTab.setDisable(false);
        tabPane.getSelectionModel().select(pagesTab);
        page.setPageType(type);
        pageName.setText("Page Name: " + page.getPageName());

        if (htmlEditorContent.containsKey(page.getPageName())) {
            content = htmlEditorContent.get(page.getPageName());
            content.numberOfAnswerChoiceValueProperty().set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() + "");
            content.branchingTypeProperty().set(page.getQuestionType());
            content.setPageNameList(pageNameList);
        }

        //adding a style listener to the textarea on opening
        this.htmlSourceCode.textProperty().addListener((obs, oldText, newText) -> {
            htmlSourceCode.setStyleSpans(0, computeHighlighting(newText));
            defaultStyle();
        });

        //setting the current page
        Main.getVignette().setCurrentPage(page);
        if (page.getPageData() == null) {
            try {
                text = content.addTextToEditor();
                page.setPageData(text);
                pageViewList.put(page.getPageName(), page);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            text = content.setText(page.getPageData());
            page.setPageData(text);

            //adding style
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

        branchingTypeProperty.set(page.getQuestionType());
        nextPageAnswers.setDisable(false);

        //-----------------   dealing with keyboard shortcuts  -----------------------------------------
        if (htmlSourceCode.getScene() == null)
            System.out.println("Scene is null for some reason");
        htmlSourceCode.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

            //htmlSourceCode.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination incFont = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN);
            final KeyCombination decFont = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
            final KeyCombination search = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                //System.out.println(ke);
                if (incFont.match(ke)) {
                    featureController.increaseFont(fontSlider, htmlSourceCode);
                    ke.consume(); // <-- stops passing the event to next node
                } else if (decFont.match(ke)) {
                    System.out.println("Decreasing font size");
                    featureController.decreaseFont(fontSlider, htmlSourceCode);
                    ke.consume();
                } else if (search.match(ke)) {
                    featureController.findAndSelectString(htmlSourceCode);
                }
            }
        });
        //-----------------------------------------------------------------------------------

        String pageType = page.getPageType();
        //disabling buttons according to page type
        switch (pageType) {
            case "Problem":
            case "response_correct":
            case "response_incorrect":
                addVideo.setDisable(false);
                addInputField.setDisable(true);
                addImageInputField.setDisable(true);
                break;

            case "login":
            case "whatLearned":
            case "Credit":
            case "completion":
                addVideo.setDisable(true);
                addInputField.setDisable(true);
                addImageInputField.setDisable(true);
                break;
            case "problemStatement":
                addVideo.setDisable(true);
                addInputField.setDisable(true);
                addImageInputField.setDisable(true);
                break;
            default:
                addVideo.setDisable(false);
                addInputField.setDisable(false);
                addImageInputField.setDisable(false);
        }


        /*
         *  Disables the next page links button based on the number of pages that have been dragged and dropped.
         *  If theres only 1 page it will automatically be disabled
         */

        nextPageAnswers.setDisable(pageNameList.size() == 1);

        AtomicBoolean lastPageboolean = new AtomicBoolean();
        if (Main.getVignette().getLastPageValueMap() != null && Main.getVignette().getLastPageValueMap().get(page.getPageName()) != null)
            lastPageboolean.set(Main.getVignette().getLastPageValueMap().get(page.getPageName()));
        this.htmlSourceCode.textProperty().addListener((obs, oldText, newText) -> {
            if (lastPageboolean.get()) {
                //check if user sets last page to be 1 or 0
                Pattern pattern = Pattern.compile("lastPage\\s?=\\s?0\\s?;");
                Matcher matcher = pattern.matcher(newText);
                //if the user has added it to the html editor manually, add it to the map
                if (matcher.find()) {
                    lastPageValueMap.put(Main.getVignette().getCurrentPage().getPageName(), false);
                    Main.getVignette().setLastPageValueMap(lastPageValueMap);
                    lastPageboolean.set(false);
                }
            } else {
                Pattern pattern2 = Pattern.compile("lastPage\\s?=\\s?1\\s?;");
                Matcher matcher2 = pattern2.matcher(newText);
                //if the user has added it to the html editor manually, add it to the map
                if (matcher2.find()) {
                    lastPageValueMap.put(Main.getVignette().getCurrentPage().getPageName(), true);
                    Main.getVignette().setLastPageValueMap(lastPageValueMap);
                    lastPageboolean.set(true);
                }
            }
        });


        /*
         * Allows the user to set the lastPage values on every page that has been drag and dropped.
         * When selecting the checkbox, the page is set to become the last page by setting the variable lastPage = 0; to lastPage = 1;
         *
         */
        lastPageOptions.setOnAction(event -> {
            GridPaneHelper lastPageGrid = new GridPaneHelper();
            lastPageGrid.setResizable(false);
            Label pageNameLabel = new Label("Page Name");
            Label hasLastPageFn = new Label("Include last page function?");
            lastPageGrid.add(pageNameLabel, 0, 0, 1, 1);
            lastPageGrid.add(hasLastPageFn, 5, 0, 1, 1);
            //add all options to the gridpane
            for (int i = 0; i < pageNameList.size(); i++) {
                int pos = i;
                //create a label with the name of the page
                Label pageLabel = new Label(pageNameList.get(i));
                lastPageGrid.add(pageLabel, 0, i + 1, 4, 1);

                //create a checkbox associated with that page
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(Main.getVignette().getLastPageValueMap().get(pageNameList.get(i)));
                lastPageGrid.add(checkBox, 5, i + 1, 1, 1);

                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                        String currentPageName = pageNameList.get(pos);
                        //box has been ticked
                        if (newValue) {
                            boolean status = setLastPageVariable(currentPageName);
                            if (!status) {
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
            }
            lastPageGrid.create("Select Last Page(s) ", "", "Close");
        });

        deleteQuestions.setOnAction(actionEvent -> {
            GridPaneHelper deleteQustionGrid = new GridPaneHelper();
            deleteQustionGrid.setResizable(false);
            ColumnConstraints column = new ColumnConstraints(200);
            deleteQustionGrid.getColumnConstraints().add(column);
            //todo add titles
            Label pageNameLabel = new Label("Question Name");
            Label deletePageLabel = new Label("Delete question?");
            deleteQustionGrid.add(pageNameLabel, 0, 0, 1, 1);
            deleteQustionGrid.add(deletePageLabel, 5, 0, 1, 1);
            ArrayList<Label> questionLabelsList = new ArrayList<>();
            ArrayList<CheckBox> questionCheckboxList = new ArrayList<>();
            int pos = 1;
            for (int i = 0; i < page.getQuestionList().size(); i++) {
                String qn = page.getQuestionList().get(i).getQuestionName();
                Label questionLabel = new Label(qn);
                CheckBox checkBox = new CheckBox();
                deleteQustionGrid.add(questionLabel, 0, i + 1 + pos, 4, 1);
                deleteQustionGrid.add(checkBox, 10, i + 1 + pos, 1, 1);
                questionCheckboxList.add(checkBox);
                questionLabelsList.add(questionLabel);
                pos++;
            }
            boolean clickedOk = deleteQustionGrid.createGrid("Question List to be deleted ", null, "ok", "Cancel");
            HashMap<String, Integer> questionNameToDelete = new HashMap<>();
            if (clickedOk) {
                if (page.getQuestionList().size() == 0)
                    return;
                for (int i = 0; i < questionCheckboxList.size(); i++) {
                    if (questionCheckboxList.get(i).isSelected())
                        questionNameToDelete.put(questionLabelsList.get(i).getText(), i);
                }

                int valuesRemovedFromNonBranching = 0;

                if (questionNameToDelete.size() > 0 && page.getQuestionList().size() > 0) {
                    int initSize = page.getQuestionList().size();
                    for (int i = 0; i < initSize; i++) {
                        if (questionNameToDelete.containsKey(page.getQuestionList().get(i - valuesRemovedFromNonBranching).getQuestionName())) {
                            int index = questionNameToDelete.get(page.getQuestionList().get(i - valuesRemovedFromNonBranching).getQuestionName());
                            if (page.getQuestionList().get(i - valuesRemovedFromNonBranching).getBranchingQuestion()) {
                                Pattern p = Pattern.compile(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET);
                                String htmlText = htmlSourceCode.getText();
                                Matcher m = p.matcher(htmlText);
                                boolean htmlTextFixed = false;
                                if (m.find()) {
                                    htmlSourceCode.selectRange(m.start(), m.end());
                                    htmlSourceCode.replaceSelection(BranchingConstants.NEXT_PAGE_ANSWER + "=" + "{}" + ";");
                                    Pattern questionPattern = Pattern.compile(BranchingConstants.QUESTION_TYPE_TARGET);
                                    String questionHtmlText = htmlSourceCode.getText();
                                    Matcher questionMatcher = questionPattern.matcher(questionHtmlText);
                                    if (questionMatcher.find()) {
                                        htmlSourceCode.selectRange(questionMatcher.start(), questionMatcher.end());
                                        htmlSourceCode.replaceSelection(BranchingConstants.QUESTION_TYPE + " = '" + "none" + "';");
                                        htmlTextFixed = true;
                                    }
                                }
                                // Deleting existing branching question
                                if (htmlTextFixed) {
                                    if (this.listOfLineConnector.containsKey(page.getPageName())) {
                                        ArrayList<Group> connections = this.listOfLineConnector.get(page.getPageName());
                                        for (Group g : connections) {
                                            rightAnchorPane.getChildren().remove(g);
                                        }
                                        this.listOfLineConnector.get(page.getPageName()).clear();
                                        page.getVignettePageAnswerFieldsBranching().setQuestionName("");
                                        page.getVignettePageAnswerFieldsBranching().setQuestion("");
                                        page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().clear();
                                        page.getPagesConnectedTo().clear();
                                        page.setConnectedTo("");
                                        page.setHasBranchingQuestion(false);
                                        page.setQuestionType(BranchingConstants.SIMPLE_BRANCH);
                                    }
                                }
                                // Done Deleting existing branching question
                            } else {
                                if (i >= page.getVignettePageAnswerFieldsNonBranching().size() && i - valuesRemovedFromNonBranching != 0)
                                    page.getVignettePageAnswerFieldsNonBranching().remove(i - 1 - valuesRemovedFromNonBranching);
                                else
                                    page.getVignettePageAnswerFieldsNonBranching().remove(i - valuesRemovedFromNonBranching);
                                page.setNumberOfNonBracnchQ(page.getNumberOfNonBracnchQ() - 1);
                            }
                            Questions q = page.getQuestionList().remove(index - valuesRemovedFromNonBranching);
                            if (q != null) {
                                valuesRemovedFromNonBranching++;
                            }
                        }
                    }
                } else {
                    System.out.println("NO NON BRANCHING QUESTION FOR THE PAGE");
                }
                Questions[] questionArray = new Questions[page.getQuestionList().size()];
                for (int i = 0; i < page.getQuestionList().size(); i++) {
                    questionArray[i] = new Questions(page.getQuestionList().get(i));
                }
                ReadFramework.listFilesForFolder(new File(ReadFramework.getUnzippedFrameWorkDirectory() + "pages/questionStyle/"), Questions.getQuestionStyleFileList());
                String questionHTMLTag = Questions.createQuestions(questionArray);
                Pattern branchPatternNewToAddTags = Pattern.compile("<!--pageQuestions-->([\\S\\s]*?)<!--pageQuestions-->", Pattern.CASE_INSENSITIVE);
                Matcher matcher;
                matcher = branchPatternNewToAddTags.matcher(htmlSourceCode.getText());
                if (matcher.find()) {
                    String comments = "<!--pageQuestions-->";
                    String addingCommentsToHtmlTag = comments + "\n" + questionHTMLTag + comments;
                    htmlSourceCode.selectRange(matcher.start(), matcher.end());
                    htmlSourceCode.replaceSelection(addingCommentsToHtmlTag);
                } else {
                    System.out.println("DELETING QUESTION DIDNT FIND THE REGEX!! ");
                }
                page.setPageData(htmlSourceCode.getText());
            }
        });
        showOrHideScript();
    }

    private void connectPages(MouseEvent event) {
        two = ((Button) event.getSource());
        pageTwo = pageViewList.get(two.getText());
        checkPageConnection(pageOne, pageTwo, one, two);

    }

    public boolean checkPageConnection(VignettePage pageOne, VignettePage pageTwo, Button one, Button two, String... connectedViaPage) {
        if (one == null || two == null) return false;
        //no self connections
        if (two.getText().equals(one.getText())) {
            isConnected = false;
        } else {
            //if not a first connection to pages
            if (this.listOfLineConnector != null && this.listOfLineConnector.containsKey(pageOne.getPageName()) && pageOne.getConnectedTo() != null) {
                // if such page exists on the vignette pane
                if (pageViewList.containsKey(pageOne.getPageName())) {
                    VignettePage page = pageViewList.get(pageOne.getPageName());
                }

            }
            if (connectedViaPage.length == 1) {
                HashMap<String, String> pageConnectionList = pageOne.getPagesConnectedTo();
                if (pageConnectionList.containsKey(pageTwo.getPageName())) {

                    pageOne.addPageToConnectedTo(pageTwo.getPageName(), pageConnectionList.get(pageTwo.getPageName()) + ", " + connectedViaPage[0]);
                } else {

                    pageOne.addPageToConnectedTo(pageTwo.getPageName(), connectedViaPage[0]);
                }
            }
            pageOne.setPreviousConnection(pageOne.getConnectedTo());
            pageOne.setConnectedTo(two.getText());
            Utility utility = new Utility();
            String text = null;
            if (pageOne.getPageData() != null) {
                text = utility.replaceNextPage(pageOne.getPageData(), pageOne);
            }
            if (pageOne.getPageData() != null) pageOne.setPageData(text);
            isConnected = true;
        }
        return isConnected;
    }

    public void makeFinalConnection(VignettePage pageOne) {
        TabPaneController pane = Main.getVignette().getController();
        Button one = pane.getButtonPageMap().get(pageOne.getPageName());
        HashMap<String, String> pageConnectionList = pageOne.getPagesConnectedTo();
        String toConnect = "";
        for (HashMap.Entry<String, String> entry : pageConnectionList.entrySet()) {
            VignettePage pageTwo = Main.getVignette().getPageViewList().get(entry.getKey());
            Button two = pane.getButtonPageMap().get(entry.getKey());
            ConnectPages connect = new ConnectPages(one, two, rightAnchorPane, this.listOfLineConnector);
            toConnect = entry.getValue().trim();
            String previousConnection = "";
            if (pageOne.getPreviousConnection() != null && pageOne.getConnectedTo() != null && !"".equalsIgnoreCase(pageOne.getConnectedTo()) && BranchingConstants.SIMPLE_BRANCH.equalsIgnoreCase(pageOne.getQuestionType())) {
                previousConnection = pageOne.getPreviousConnection();
            }
            Group grp = connect.connectSourceAndTarget(toConnect, previousConnection);
            if (grp != null) {
                pageOne.setConnectedTo(two.getText());
                pageOne.setNextPages(two.getText(), grp);
                pageTwo.setNextPages(pageOne.getPageName(), grp);
                connect.setListOfLineConnectors(this.listOfLineConnector);
            }
        }
    }


    /**
     * This function shows or hides the script based on whether is currently hidden or not.
     */
    public void showOrHideScript() {
        if (getScriptIsHidden()) {
            showScript();
        } else {
            hideScript();
        }
    }


    /**
     * This function looks for the lastPage variable and sets it to 1;
     *
     * @param pageName
     */
    public boolean setLastPageVariable(String pageName) {
        HTMLEditorContent currentPageContent = htmlEditorContent.get(pageName);
        Pattern pattern = Pattern.compile("lastPage\\s?=\\s?0\\s?;");
        Matcher matcher;

        //dealing with the page we are currently on
        if (pageName.equals(Main.getVignette().getCurrentPage().getPageName())) {
            //show script in case its hidden if the page youre updating is the current page
            boolean wasScriptHidden;
            if (getScriptIsHidden()) {
                wasScriptHidden = true;
                showScript();
            } else
                wasScriptHidden = false;

            matcher = pattern.matcher(htmlSourceCode.getText());
            if (matcher.find()) {
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection("lastPage = 1;");

                currentPageContent.getPage().setPageData(htmlSourceCode.getText());
                //hide script if required
                if (wasScriptHidden)
                    hideScript();

                //change the value in the map
                lastPageValueMap.put(pageName, true);
                Main.getVignette().setLastPageValueMap(lastPageValueMap);

                return true;
            } else
                System.out.println("did not find last page Comment");
        }

        //dealing with pages that arent currently open
        else {
            String otherPageData = currentPageContent.getPageData();
            matcher = pattern.matcher(otherPageData);
            if (matcher.find()) {
                //System.out.println("found lastPage Comment ");
                otherPageData = otherPageData.replaceAll("lastPage\\s?=\\s?0\\s?;", "lastPage = 1;");
                currentPageContent.getPage().setPageData(otherPageData);

                //change the value in the map
                lastPageValueMap.put(pageName, true);
                Main.getVignette().setLastPageValueMap(lastPageValueMap);
                return true;
            } else
                System.out.println("did not find last page Comment");
        }
        return false;
    }


    /**
     * This function is called to set the lastPage variable from 1 to 0 on the specified page.
     *
     * @param pageName
     */
    public void unsetLastPageVariable(String pageName) {
        //removing value from map
        lastPageValueMap.put(pageName, false);
        Main.getVignette().setLastPageValueMap(lastPageValueMap);
        //set present page to be the new last page
        System.out.println("Removing last Page function from = " + pageName);
        VignettePage currentPage = Main.getVignette().getCurrentPage();
        HTMLEditorContent otherPageContent = htmlEditorContent.get(pageName);

        Pattern pattern = Pattern.compile("lastPage\\s?=\\s?1\\s?;");
        Matcher matcher;
        //if we're removing the function from the page we're on
        if (pageName.equals(currentPage.getPageName())) {
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

            if (wasScriptHidden)
                hideScript();
        }

        //any page not currently open
        else {
            matcher = pattern.matcher(otherPageContent.getPageData());
            if (matcher.find()) {
                String otherPageData = otherPageContent.getPageData();
                otherPageData = otherPageData.replaceAll("lastPage\\s?=\\s?1\\s?", "lastPage = 0;");
                otherPageContent.getPage().setPageData(otherPageData);
            }
        }
    }

    public List<String> getPageNameList() {
        return pageNameList;
    }

    public void replaceImage(ActionEvent actionEvent) {
        Main.getVignette().getImagesList().add(content.addImageTag());
    }

    public void addNewImage(ActionEvent actionEvent) {
        content.copyNewImageToClipBoard().stream().forEach(images -> {
            System.out.println("adding image: " + images.getImageName());
            Main.getVignette().getImagesList().add(images);
        });
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

    public void onNumberChoiceKeyRelased(KeyEvent keyEvent) {

        try {
            Integer.parseInt(keyEvent.getText());
            nextPageAnswers.setDisable(false);
        } catch (Exception e) {
            System.out.println("Exception at onNumberChoiceKeyRelased: " + e.getMessage());
        }
    }

    public static class DraggableImage extends ImageView {
        private double mouseX;
        private double mouseY;

        public DraggableImage(Image image) {
            super(image);

            setOnMousePressed(event -> {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            });

            setOnMouseDragged(event -> {
                double deltaX = event.getSceneX();
                double deltaY = event.getSceneY();
                relocate(deltaX, deltaY);
            });
        }


    }


    /**
     * This changes the value of the show or hide script button
     *
     * @param value val
     */
    public void setScriptIsHidden(boolean value) {
        this.isScriptHidden = value;
        if (value)
            showHideScript.setText("Show Script");
        else
            showHideScript.setText("Hide Script");
    }

    /**
     * Finds out whether the script is currently hidden or not
     *
     * @return boolean
     */
    public boolean getScriptIsHidden() {
        return this.isScriptHidden;
    }

    /**
     * This function hides the script content when called.
     */
    public void hideScript() {
        String target = "<!--Do Not Change content in this block-->([\\S\\s]*?)<!--Do Not Change content in this block-->";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);

        if (m.find()) {
            setScriptIsHidden(true);
            htmlSourceCode.foldText(m.start(), m.end());
        }
    }

    /**
     * This function shows the script content when called.
     */
    public void showScript() {
        //String target = "<script>([\\S\\s]*?)</script>";
        String target = "<!--Do Not Change content in this block-->([\\S\\s]*?)<!--Do Not Change content in this block-->";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);

        if (m.find()) {
            setScriptIsHidden(false);
            htmlSourceCode.unfoldText(m.start());
        }
    }

    public EditorRightClickMenu getEditorRightClickMenu() {
        return this.editorRightClickMenu;
    }

    public HashMap<String, HTMLEditorContent> getHTMLContentEditor() {
        return this.htmlEditorContent;
    }

    public HashMap<String, Image> getImageMap() {
        return this.imageMap;
    }

    public HashMap<String, VignettePage> getPageViewList() {
        return pageViewList;
    }

    public AnchorPane getAnchorPane() {
        return this.rightAnchorPane;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public HashMap<String, Button> getButtonPageMap() {
        return buttonPageMap;
    }

}

