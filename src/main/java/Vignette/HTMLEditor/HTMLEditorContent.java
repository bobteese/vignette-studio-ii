package Vignette.HTMLEditor;

import Application.Main;
import ConstantVariables.BranchingConstants;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import SaveAsFiles.Images;
import TabPane.TabPaneController;
import Utility.Utility;
import Vignette.Branching.BranchingImpl;
import Vignette.Framework.ReadFramework;
import Vignette.HTMLEditor.InputFields.InputFields;
import Vignette.Page.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;
import org.reactfx.value.Val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTMLEditorContent {

    private final CodeArea htmlSourceCode;

    private final String type;
    private final VignettePage page;
    private int countOfAnswer = 0;
    private String editConnectionString;

    public int defaultTextFieldIndex = -1;
    public boolean defaultTextFieldAdded = false;


    public void setPageNameList(List<String> pageNameList) {
        this.pageNameList = pageNameList;
    }

    private List<String> pageNameList;
    private final List<TextField> answerChoice;
    private final List<ComboBox> answerPage;

    BufferedImage image;
    Button add;
    private final Logger logger = LoggerFactory.getLogger(HTMLEditorContent.class);
    BranchingImpl branching;
    List<InputFields> inputFieldsListNonBranching;
    List<InputFields> inputFieldsListBranching;
    private final StringProperty questionText = new SimpleStringProperty();

    public String getQuestionTextNonBranching() {
        return questionTextNonBranching.get();
    }

    public StringProperty questionTextNonBranchingProperty() {
        return questionTextNonBranching;
    }

    public void setQuestionTextNonBranching(String questionTextNonBranching) {
        this.questionTextNonBranching.set(questionTextNonBranching);
    }

    private final StringProperty questionTextNonBranching = new SimpleStringProperty();

    public String getNumberOfAnswerChoiceValue() {
        return numberOfAnswerChoiceValue.get();
    }

    public SimpleStringProperty numberOfAnswerChoiceValueProperty() {
        return numberOfAnswerChoiceValue;
    }

    public void setNumberOfAnswerChoiceValue(String numberOfAnswerChoiceValue) {
        this.numberOfAnswerChoiceValue.set(numberOfAnswerChoiceValue);
    }

    public String getBranchingType() {
        return branchingType.get();
    }

    public SimpleStringProperty branchingTypeProperty() {
        return branchingType;
    }

    public void setBranchingType(String branchingType) {
        this.branchingType.set(branchingType);
    }

    SimpleStringProperty numberOfAnswerChoiceValue;
    SimpleStringProperty branchingType;
    Button nextPageAnswerButton;
    private String inputTypeProperty;
    private final StringProperty inputNameProperty = new SimpleStringProperty();

    String defaultNextPage = null;
    HashMap<String, String> optionEntries = new HashMap<>();
    HashSet<String> removeQueue = new HashSet<>();
    HashSet<TextField> removeQueueAC = new HashSet<TextField>();
    HashSet<ComboBox> removeQueueAP = new HashSet<ComboBox>();

    public String getHtmlDataForPage() {
        return htmlDataForPage.get();
    }

    public StringProperty htmlDataForPageProperty() {
        return htmlDataForPage;
    }

    public void setHtmlDataForPage(String htmlDataForPage) {
        this.htmlDataForPage.set(htmlDataForPage);
    }

    private final StringProperty htmlDataForPage = new SimpleStringProperty();
    private final StringProperty imageSourceForQuestion = new SimpleStringProperty();

    public String getImageSourceForQuestion() {
        return imageSourceForQuestion.get();
    }

    public StringProperty imageSourceForQuestionProperty() {
        return imageSourceForQuestion;
    }

    public void setImageSourceForQuestion(String imageSourceForQuestion) {
        this.imageSourceForQuestion.set(imageSourceForQuestion);
    }

    private boolean hasBranchingQuestion;
    private Tab pageTab;

    public Tab getPageTab() {
        return pageTab;
    }

    public void setPageTab(Tab pageTab) {
        this.pageTab = pageTab;
    }

    public static boolean matchSingleTonTag(String inputTag) {
        String[] tags = {"<area", "<base", "<br", "<col", "<embed", "<hr", "<img", "<input", "<link", "<meta", "<param", "<source", "<track", "<wbr"};
        for (String tag : tags) {
            if (inputTag.startsWith(tag))
                return true;
        }
        return false;
    }

    public HTMLEditorContent(CodeArea htmlSourceCode,
                             String type, VignettePage page,
                             List<String> pageNameList, Label pageName, Button nextPageAnswerButton) {
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.page = page;
        this.pageNameList = pageNameList;
        answerChoice = new ArrayList<>();
        answerPage = new ArrayList<>();
        this.branching = new BranchingImpl(this.page);
        inputFieldsListBranching = new ArrayList<>();
        inputFieldsListNonBranching = new ArrayList<>();
        this.nextPageAnswerButton = nextPageAnswerButton;
        pageName.setAlignment(Pos.CENTER);
        pageName.setWrapText(true);
        pageName.setTextAlignment(TextAlignment.JUSTIFY);
        pageName.setMaxWidth(300);
        pageName.setTranslateX(0);
        pageName.setTranslateY(0);
        updateOptionEntries();
        this.numberOfAnswerChoiceValue = new SimpleStringProperty();
        this.branchingType = new SimpleStringProperty();
        this.numberOfAnswerChoiceValueProperty().set("0");
        this.branchingTypeProperty().set(page.getQuestionType());
        this.htmlSourceCode.setWrapText(true);
        this.htmlSourceCode.setParagraphGraphicFactory(LineNumberFactory.get(this.htmlSourceCode));
        removeQueue.clear();
        removeQueueAC.clear();
        removeQueueAP.clear();
    }

    public void updateOptionEntries() {
        for (HashMap.Entry<String, String> entry : page.getPagesConnectedTo().entrySet()) {
            String[] temp = entry.getValue().split(",");
            for (String x : temp)
                this.optionEntries.put(x.trim(), entry.getKey());
        }
    }

    /**
     * Sets the Text for TextArea displayed on the right to show the HTML content for a vignette page
     *
     * @return String
     */
    public String addTextToEditor() {
        logger.info("> {HTMLEditorContent}::addTextToEditor: HTML Files "+ Main.getVignette().getHtmlFiles());
        String text;
        text = Main.getVignette().getController().getPageDataWithPageType(page, type);
        htmlSourceCode.replaceText(0, htmlSourceCode.getText().length(), text);
        //replacing text is undoable in richtextfx, we don't want the user to have this in the undo/redo stack
        htmlSourceCode.getUndoManager().forgetHistory();
        //after opening the page, first it will set the initial text. Print statement below onKeyRelease will be executed
        //and if you type anything it will be recognized because of this event handler.
        htmlSourceCode.setOnKeyReleased(event -> page.setPageData(htmlSourceCode.getText()));
        logger.info("< {HTMLEditorContent}::addTextToEditor ");
        return text;
    }


    /**
     * Setting the text in an non undoable, manner.
     *
     * @param text text to set
     * @return Text String
     */
    public String setText(String text) {

        htmlSourceCode.replaceText(0, htmlSourceCode.getText().length(), text);
        //replacing text is undoable in richtextfx, we don't want the user to have this in the undo/redo stack
        htmlSourceCode.getUndoManager().forgetHistory();
        htmlSourceCode.setOnKeyReleased(event -> {
            // htmlEditor.setHtmlText(htmlSourceCode.getText());
            page.setPageData(htmlSourceCode.getText());
        });
        return text;
    }

    /**
     * read a predefined file based on the page type from /vignette-studio-ii/src/main/resources/HTMLResources/pages
     *
     * @param file
     * @return
     */
    public String readFile(InputStream file) {

        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
                stringBuffer.append("\n");
            }
        } catch (Exception ex) {
            logger.error("{HTMLEditorContent}::readFile : Exception while writing " ,ex);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException exp) {
                logger.error("{HTMLEditorContent}::Exception while closing buff reader ",exp);
            }
        }

        return stringBuffer.toString();
    }


    public void addVideo() {
        logger.info("> {HTMLEditorContent}::addVideo");
        boolean scriptWasHidden = false;
        if (Main.getVignette().getController().getScriptIsHidden()) {
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        GridPaneHelper helper = new GridPaneHelper();
        helper.addLabel("Video Link:", 1, 1);
        TextField text = helper.addTextField(2, 1, 400, 400);
        String[] videoOptions = {ConstantVariables.VIMEO_VIDEO_OPTION, ConstantVariables.YOUTUBE_VIDEO_OPTION};

        ComboBox video = helper.addDropDown(videoOptions, 0, 1);
        video.setValue(ConstantVariables.VIMEO_VIDEO_OPTION);
        boolean isSaved = helper.createGrid("Video Link", null, "ok", "Cancel");
        if (isSaved) {
            Pattern pattern = Pattern.compile("(.*?)<div id=\"player\"></div>\n(.*?)");
            if (!pattern.matcher(htmlSourceCode.getText()).find() && "Custom".equalsIgnoreCase(page.getPageType())) {
                String videoHTMLTag = "    <!-- //////// Video option //////// -->\n" +
                        "<div class=\"video container mb-2\" id=\"video\">\n" +
                        "        <div class=\"embed-responsive embed-responsive-16by9 container\" style=\" padding-top: 25px; \">\n" +
                        "            <div class=\"row\">\n" +
                        "                <div id=\"player\"></div>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>";
                htmlSourceCode.append(videoHTMLTag, "");
            } else {
                System.out.println("Page image already exists!");
                logger.info("> {HTMLEditorContent}::addVideo: Page image already exists!");
            }
            //-----------adding the script to the HTML page-----------
            String videoType = video.getValue().toString();
            String videoScript = "//VideoSettings([\\S\\s]*?)//VideoSettings";
            ;
            Pattern videoPattern = Pattern.compile(videoScript);
            String getText = htmlSourceCode.getText();
            Matcher videoMatcher = videoPattern.matcher(getText);
            if (videoMatcher.find()) {
                String vimeoVideoSource = "var vimeoVideoSource = \"(.*?)\";\n";
                String youtubeVideoID = "var youtubeVideoID = \"(.*?)\";\n";
                if (ConstantVariables.VIMEO_VIDEO_OPTION.equalsIgnoreCase(videoType)) {
                    Pattern vimeoPattern = Pattern.compile(vimeoVideoSource);
                    Matcher vimeoMatcher = vimeoPattern.matcher(videoMatcher.group(0));
                    if (vimeoMatcher.find()) {
                        String videoText = text.getText().trim();
                        String videoID;
                        if (videoText.trim().startsWith("https://"))
                            videoID = videoText.split("/")[videoText.split("/").length - 1];
                        else
                            videoID = videoText;
                        videoID = videoID.replaceAll("&.*$", "");
                        String videoURL = "\tvar vimeoVideoSource = \"https://player.vimeo.com/video/" + videoID + "\";\n";
                        logger.info("> {HTMLEditorContent}::addVideo: vimeoVideoSource URL "+videoURL);
                        htmlSourceCode.selectRange(videoMatcher.start() + vimeoMatcher.end() - vimeoMatcher.group(0).length(), videoMatcher.start() + vimeoMatcher.end());
                        htmlSourceCode.replaceSelection(videoURL);
                        Matcher playerChoice = Pattern.compile(BranchingConstants.PLAYER_CHOICE_TARGET).matcher(htmlSourceCode.getText());
                        if (playerChoice.find()) {
                            htmlSourceCode.selectRange(playerChoice.start(), playerChoice.end());
                            htmlSourceCode.replaceSelection(BranchingConstants.PLAYER_CHOICE + " = 0; ");
                        }
                    }

                } else if (ConstantVariables.YOUTUBE_VIDEO_OPTION.equalsIgnoreCase(videoType)) {
                    Pattern youtubePattern = Pattern.compile(youtubeVideoID);
                    Matcher youtubeMatcher = youtubePattern.matcher(videoMatcher.group(0));
                    if (youtubeMatcher.find()) {
                        String videoText = text.getText().trim();
                        String videoID;
                        if (videoText.trim().startsWith("https://"))
                            videoID = videoText.split("=")[1];
                        else
                            videoID = videoText;
                        videoID = videoID.replaceAll("&.*$", "");
                        String videoURL = "\tvar youtubeVideoID = \"" + videoID + "\";\n";
                        logger.info("> {HTMLEditorContent}::addVideo: youtubeVideoID URL "+videoURL);
                        htmlSourceCode.selectRange(videoMatcher.start() + youtubeMatcher.end() - youtubeMatcher.group(0).length(), videoMatcher.start() + youtubeMatcher.end());
                        htmlSourceCode.replaceSelection(videoURL);
                        Matcher playerChoice = Pattern.compile(BranchingConstants.PLAYER_CHOICE_TARGET).matcher(htmlSourceCode.getText());
                        if (playerChoice.find()) {
                            htmlSourceCode.selectRange(playerChoice.start(), playerChoice.end());
                            htmlSourceCode.replaceSelection(BranchingConstants.PLAYER_CHOICE + " = 1; ");
                        }
                    }
                }
            } else {
                System.out.println("NO VIDEO SETTING FOUND");
                logger.info("> {HTMLEditorContent}::addVideo: NO VIDEO SETTING FOUND");
            }
            page.setPageData(htmlSourceCode.getText());
        }
        if (scriptWasHidden)
            Main.getVignette().getController().hideScript();
    }

    public String getImageToDisplay() {
        return imageToDisplay.get();
    }

    public StringProperty imageToDisplayProperty() {
        return imageToDisplay;
    }

    public void setImageToDisplay(String imageToDisplay) {
        this.imageToDisplay.set(imageToDisplay);
    }


    StringProperty imageToDisplay = new SimpleStringProperty();

    public Image readImage(String... imageSaved) {
        logger.info("> {HTMLEditorContent}::readImage");
        File f;
        if (imageSaved.length > 0)
            f = new File(imageSaved[0]);
        else
            f = new File(getImageToDisplay());
        try {
            BufferedImage bimg = ImageIO.read(f);
            return SwingFXUtils.toFXImage(bimg, null);
        } catch (IOException e) {
            logger.error("> {HTMLEditorContent}::readImage: Exception while reading image ",e);
        }
        return null;
    }


    public List<Images> copyNewImageToClipBoard() {
        logger.info("> {HTMLEditorContent}::copyNewImageToClipBoard");
        boolean scriptWasHidden = false;
        if (Main.getVignette().getController().getScriptIsHidden()) {
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        GridPaneHelper helper = new GridPaneHelper();
        helper.setPrefSize(500, 500);
        helper.setResizable(true);
        //Adding labels and textfields to gridpane-------------------------------------
        helper.add(new Label("Width of Image"), 0, 3, 1, 1);
        TextField widthofImage = new TextField();
        helper.add(widthofImage, 1, 3, 1, 1);
        widthofImage.setText("50%");
        StringProperty widthOfImageProperty = new SimpleStringProperty(widthofImage.getText());
        widthofImage.textProperty().bindBidirectional(widthOfImageProperty);
        helper.add(new Label("Image Class Name"), 0, 4, 1, 1);
        TextField className = new TextField();
        StringProperty classNameProperty = new SimpleStringProperty(className.getText());
        className.textProperty().bindBidirectional(classNameProperty);
        helper.add(className, 1, 4, 1, 1);
        className.setText("img-fluid");
        //------------------------------------------------------------------------------
        //creating Click to add Image button
        // and adding to an hBox so that its centered on the gridPane-----------------
        String htmlText = htmlSourceCode.getText();
        Button addImage = new Button("Click to add Image");
        Image addImageIcon = null;

        ImageView addImageIconView = new ImageView(addImageIcon);
        addImageIconView.setPreserveRatio(true);

        addImageIconView.setFitHeight(300);
        addImageIconView.setFitWidth(300);

        addImage.setGraphic(addImageIconView);
        addImage.setTextAlignment(TextAlignment.CENTER);
        addImage.setContentDisplay(ContentDisplay.TOP);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(addImage);
        //--------------------------------------------------------------------------------------------------------------
        //adding the hBox to the gridPane
        helper.add(hBox, 0, 0, 2, 1);
        final String[] fileName = {null};
        StringProperty imageText = new SimpleStringProperty("");
        List<Images> actualList = new ArrayList<>();
        EventHandler eventHandler = event -> {
            Stage stage = (Stage) helper.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(false);
            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images()", "*.JPG", "*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);
            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            List<File> fileList = fileHelper.openMultipleFileChooser(filterList);
            if (fileList != null) {
                try {
                    setImageToDisplay(fileList.get(0).getAbsolutePath());
                    for (File file : fileList) {
                        image = ImageIO.read(file);
                        //Once the image is uploaded, change the button graphic------
                        Image i = new Image(ConstantVariables.MULTIPLE_FILE_ICON);
                        Image img = SwingFXUtils.toFXImage(image, null);
                        ImageView img1 = null;
                        if (fileList.size() == 1) {
                            img1 = new ImageView(img);
                        } else {
                            img1 = new ImageView(i);
                        }
                        img1.setPreserveRatio(true);
                        img1.setFitHeight(addImage.getHeight());
                        img1.setFitWidth(addImage.getWidth());
                        addImage.setGraphic(img1);
                        Images images = new Images(file.getName().replaceAll("\\s", "-"), image);
                        actualList.add(images);
                    }
                    //------------------------------------------------------------
                } catch (IOException e) {
                    logger.error("> {HTMLEditorContent}::copyNewImageToClipBoard: Exception while copying image ",e);
                    e.printStackTrace();
                }
            }
            widthofImage.requestFocus();
            try {
                stage.setAlwaysOnTop(true);
            } catch (NullPointerException ex) {
                if (helper.isVisible())
                    helper.closeDialog();
            }
        };
        addImage.setOnAction(eventHandler);

        boolean clicked = helper.createGridWithoutScrollPane("Image", null, "Ok", "Cancel");
        boolean isValid = false;
        if (clicked) {
            isValid = fileName.length > 0 && fileName[0] != null;
            if (actualList.size() == 0) {
                String displaymessage = fileName.length > 0 && fileName[0] == null ? "File Name Cannot be empty" : "";
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION, "Message", null,
                        displaymessage, false);
                if (dialogHelper.getOk()) {
                    clicked = helper.showDialog();
                }
                imageText.set("");
            } else {
                for (Images file : actualList) {
                    String imageFileName = file.getImageName();
                    System.out.println("Image file Name: " + imageFileName);
                    imageText.set(imageText.get() + "<img class=\"" + classNameProperty.get() + "\" style='width:" + widthOfImageProperty.get() + ";' src=\"" + ConstantVariables.imageResourceFolder + file.getImageName() + "\" alt=\"IMG_DESCRIPTION\">\n");
                }
            }
        } else {
            imageText.set("");
        }
        Popup tp = new Popup();
        Label message = new Label();
        message.textProperty().bindBidirectional(imageText);
        tp.getContent().add(message);
        StringProperty finalImageText = imageText;
        message.setStyle(" -fx-background-color: darkgray; -fx-font-weight: bold; -fx-font-size: 14px; -fx-color-label-visible: white; -fx-padding: 10; -fx-text-alignment: right;");
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        htmlSourceCode.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!"".equalsIgnoreCase(imageText.get())) {
                    tp.show(Main.getStage());
                }
            }
        });

        htmlSourceCode.setOnMouseClicked(mouseEvent -> {
            if (!"".equals(finalImageText.get())) {
                int offset = htmlSourceCode.getCaretPosition();
                TwoDimensional.Position pos = htmlSourceCode.offsetToPosition(offset, null);
                GridPaneHelper confirmation = new GridPaneHelper();
                confirmation.setResizable(false);
                Label pageNameLabel = new Label("Adding Image source text to line: " + (pos.getMajor() + 1));
                pageNameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                confirmation.add(pageNameLabel, 0, 0, 1, 1);
                Boolean clickedOk = confirmation.createGrid("Add Image ", null, "ok", "Cancel");
                if (clickedOk) {
                    htmlSourceCode.insertText(htmlSourceCode.getCaretPosition(), "\n" + finalImageText.get());
                    imageText.set("");
                    tp.hide();
                    page.setPageData(htmlSourceCode.getText());
                }
            }
        });


        if (scriptWasHidden)
            Main.getVignette().getController().hideScript();


        return actualList;
    }

    /**
     * Identify multiple file uploads and add them as an image tag to the source HTMl code
     *
     * @return
     */
    public Images addImageTag() {
        logger.info("> {HTMLEditorContent}::addImageTag:  ");
        boolean scriptWasHidden = false;
        if (Main.getVignette().getController().getScriptIsHidden()) {
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }
        GridPaneHelper helper = new GridPaneHelper();
        helper.setPrefSize(500, 500);
        helper.setResizable(true);
        //Adding labels and textfields to gridpane-------------------------------------
        helper.add(new Label("Width of Image"), 0, 3, 1, 1);
        TextField widthofImage = new TextField();
        helper.add(widthofImage, 1, 3, 1, 1);
        widthofImage.setText("50%");
        helper.add(new Label("Image Class Name"), 0, 4, 1, 1);
        TextField className = new TextField();
        helper.add(className, 1, 4, 1, 1);
        className.setText("img-fluid");
        //------------------------------------------------------------------------------

        //creating Click to add Image button
        // and adding to an hBox so that its centered on the gridPane-----------------
        String htmlText = htmlSourceCode.getText();
        Button addImage = new Button("Click to add Image");
        Image addImageIcon = null;
        if (Main.getVignette().getFolderPath() != null || !("".equalsIgnoreCase(Main.getVignette().getFolderPath()))) {
            String imagePatter = ".*<img id=\"textOption\" class=\"(.*?)\" style='(.*?)' src=\"(.*?)\" alt=\"(.*?)\">\n";
            Pattern pattern = Pattern.compile(imagePatter);
            String tempData = page.getPageData();
            Matcher matcher = pattern.matcher(tempData);
            if (matcher.find()) {
                String imageSaved = Main.getVignette().getFolderPath() + "/" + matcher.group(3).trim();
                List<Images> imagesList = Main.getVignette().getImagesList();
                try {
                    for (Images img : imagesList) {
                        if (img != null) {
                            String fileName = img.getImageName();
                            String imageName = matcher.group(3).trim().split("/")[matcher.group(3).trim().split("/").length - 1];
                            if (fileName.equalsIgnoreCase(imageName)) {
                                addImageIcon = SwingFXUtils.toFXImage(image, null);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("{HTMLEditorContent}::addImageTag: Exception while adding image tag",e);
                }
            }
            if (addImageIcon == null && "".equalsIgnoreCase(getImageToDisplay()))
                addImageIcon = new Image("/Images/insertImage.png");
        } else if (getImageToDisplay() == null || "".equalsIgnoreCase(getImageToDisplay()))
            addImageIcon = new Image("/Images/insertImage.png");
        else
            addImageIcon = readImage();

        ImageView addImageIconView = new ImageView(addImageIcon);
        addImageIconView.setPreserveRatio(true);

        addImageIconView.setFitHeight(300);
        addImageIconView.setFitWidth(300);

        addImage.setGraphic(addImageIconView);
        addImage.setTextAlignment(TextAlignment.CENTER);
        addImage.setContentDisplay(ContentDisplay.TOP);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(addImage);
        //--------------------------------------------------------------------------------------------------------------
        //adding the hBox to the gridPane8
        helper.add(hBox, 0, 0, 2, 1);
        final String[] fileName = {null};
        EventHandler eventHandler = event -> {
            Stage stage = (Stage) helper.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(false);
            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images()", "*.JPG", "*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);
            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if (file != null) {
                fileName[0] = file.getName();
                try {
                    setImageToDisplay(file.getAbsolutePath());
                    image = ImageIO.read(file);
                    Main.getVignette().getImagesList().add(new Images(fileName[0], image));
                    //Once the image is uploaded, change the button graphic------
                    Image img = SwingFXUtils.toFXImage(image, null);
                    ImageView img1 = new ImageView(img);
                    img1.setPreserveRatio(true);
                    img1.setFitHeight(addImage.getHeight());
                    img1.setFitWidth(addImage.getWidth());
                    addImage.setGraphic(img1);
                    Images images = new Images(file.getName().replaceAll("\\s", "-"), image);
                    //------------------------------------------------------------
                } catch (IOException e) {
                    logger.error("{HTMLEditorContent}::addImageTag: Exception while adding image tag",e);
                }
            }
            widthofImage.requestFocus();
            stage.setAlwaysOnTop(true);
        };

        addImage.setOnAction(eventHandler);

        boolean clicked = helper.createGridWithoutScrollPane("Image", null, "Ok", "Cancel");
        boolean isValid = false;
        if (clicked) {

            String imagePatter = ".*<img id=\"textOption\"(.*?)>\n";
            Pattern pattern = Pattern.compile(imagePatter);
            if (!pattern.matcher(htmlSourceCode.getText()).find() && "Custom".equalsIgnoreCase(page.getPageType())) {
                String addImageTag = "       <div class=\"center\">\n" +
                        "                        <img id=\"textOption\" class=\"img-fluid\" width=\"50%\" src=\"Images/image1.png\" alt=\"IMG_DESCRIPTION\">\n" +
                        "                    </div>";
                htmlSourceCode.append(addImageTag, "");
            } else {
                System.out.println("Page image already exists!");
            }
            isValid = fileName.length > 0 && fileName[0] != null;
            while (!isValid) {
                String message = fileName.length > 0 && fileName[0] == null ? "File Name Cannot be empty" : "";
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION, "Message", null,
                        message, false);
                if (dialogHelper.getOk()) {
                    clicked = helper.showDialog();
                }
                isValid = fileName[0] != null;
                if (!clicked) break;
            }
            fileName[0] = fileName[0].replaceAll("\\s", "-");
            String imageText = "<img id=\"textOption\" class=\"" + className.getText() + "\" style='width:" + widthofImage.getText() + ";' src=\"" + ConstantVariables.imageResourceFolder + fileName[0] + "\" alt=\"IMG_DESCRIPTION\">\n";
            // This replaces the image tag on the page in a javafx undo/redoable manner
            Matcher matcher = pattern.matcher(htmlSourceCode.getText());
            if (matcher.find()) {
                System.out.println(matcher.group(0));
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(imageText);
                //saving manipulated page data
                page.setPageData(htmlSourceCode.getText());
                Main.getVignette().getPageViewList().put(page.getPageName(), page);
            }
        } else {
            System.out.println("NO IMAGE SELECTED");
            logger.info("{HTMLEditorContent}::addImageTag: NO IMAGE SELECTED");
        }
        if (scriptWasHidden)
            Main.getVignette().getController().hideScript();

        Images images = new Images(fileName[0], image);
        logger.info("< {HTMLEditorContent}::addImageTag");
        return images;
    }

    public String addProblemStatmentToQuestion() {
        boolean scriptWasHidden = false;
        if (Main.getVignette().getController().getScriptIsHidden()) {
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        GridPaneHelper helper = new GridPaneHelper();
        ComboBox problemStatementBox = null;
        List<String> psOptions = new ArrayList<>();
        pageNameList.stream().forEach(pageForType -> {
            VignettePage p = Main.getVignette().getController().getPageViewList().get(pageForType);
            if (p.getPageType().equalsIgnoreCase(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE)) {
                psOptions.add(pageForType);
            }
        });
        if (psOptions.size() > 0) {
            Object[] obj = psOptions.toArray();
            String[] psArray = Arrays.copyOf(obj, obj.length, String[].class);
            problemStatementBox = helper.addDropDown(psArray, 0, 1);
            Boolean clickedOk = helper.createGrid("Next Answer Page ", null, "ok", "Cancel");
            if (clickedOk) {
                System.out.println("PS SELECTED: " + problemStatementBox.getSelectionModel().getSelectedItem().toString());
                Pattern branchPattern = Pattern.compile("//problemStatement\n(.*?)//problemStatement\n", Pattern.DOTALL);
                Matcher matcher;
                String htmlCodeInString = htmlSourceCode.getText();
                matcher = branchPattern.matcher(htmlCodeInString);
                //If there already is a branching question, find it and replace it in an undoable manner
                if (matcher.find()) {
                    String problemStatement = "";
                    problemStatement += "//problemStatement\n";
                    String psPage = problemStatementBox.getSelectionModel().getSelectedItem().toString();
                    problemStatement += BranchingConstants.PROBLEM_STATEMENT + " = '" + psPage + "'\n";
                    problemStatement += "//problemStatement\n";
                    htmlCodeInString = htmlCodeInString.replaceAll(matcher.group(0), problemStatement);


                    // htmlSourceCode.setText(htmlCodeInString);
                    htmlSourceCode.replaceText(0, htmlSourceCode.getText().length(), htmlCodeInString);


                    page.setProblemStatementPage(psPage);
                }
                // otherwise insert it at the user provided position
                else {
                    System.out.println("No Problem Statement regex found");
                }
            }
        } else {
            System.out.println("NO PS OPTIONS");
        }
        if (scriptWasHidden) {
            Main.getVignette().getController().hideScript();
        }
        return "";
    }

    /**
     * @param editNextPageAnswers
     * @param noquestionSelected
     * @return
     */
    public ArrayList<String> inputValueChoices = new ArrayList<>();

    public String createNextPageAnswersDialog(Boolean editNextPageAnswers, Boolean noquestionSelected) {
        logger.info("> {HTMLEditorContent}::createNextPageAnswersDialog: editNextPageAnswers "+ editNextPageAnswers);
        GridPaneHelper helper = new GridPaneHelper();
        ComboBox defaultNextPageBox = null;
        page.clearNextPagesList();
        // Move this here to avoid multiple sorting calls in for loops below
        ArrayList<String> pageList = new ArrayList<>(pageNameList);
        pageList.remove(page.getPageName());
        pageList.sort(String.CASE_INSENSITIVE_ORDER);
        pageList.remove("No Link");
        pageList.add(0,"No Link");
        String[] pageListArray = pageList.toArray(new String[0]);
        branchingType.setValue(page.getQuestionType());
        if (!branchingType.getValue().equals(BranchingConstants.SIMPLE_BRANCH) && (numberOfAnswerChoiceValue.get().equals("") || Integer.parseInt(numberOfAnswerChoiceValue.get()) <= 0)) {
            DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR, "Cannot Connect Pages",
                    null, "Not possible to connect things", false);
            return "";
        }
        if (branchingType.getValue().equals(BranchingConstants.SIMPLE_BRANCH)) {
            logger.info("> {HTMLEditorContent}::createNextPageAnswersDialog: Branch Type SIMPLE_BRANCH");
            helper.addLabel("Default Next Page", 0, 0);
            if (optionEntries.size() > 0) {
                defaultNextPageBox = helper.addDropDownWithDefaultSelection(pageListArray, 0, 1, optionEntries.get("default"));
            } else {
                defaultNextPageBox = helper.addDropDown(pageListArray, 0, 1);
            }
            defaultNextPageBox.setVisibleRowCount(25);
        } else {
            int size;
            String defaultVal = optionEntries.get("default");
            optionEntries.remove("default");
            Object[] keys = optionEntries.keySet().toArray();
            Arrays.sort(keys);
            optionEntries.put("default", defaultVal);
            if (branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)) {
                logger.info("> {HTMLEditorContent}::createNextPageAnswersDialog: Branch Type CHECKBOX_QUESTION");
                size = optionEntries.size() - 1;
                System.out.println(Arrays.toString(pageListArray));
                for (int i = 0; i < size; i++) {
                    addNextPageTextFieldToGridPane(this.countOfAnswer++, helper, editNextPageAnswers, true,pageListArray,keys);
                }
                defaultTextFieldIndex = this.countOfAnswer + 1;
                add = helper.addButton("+", 0, defaultTextFieldIndex + 1);
                add.setOnAction(addToGridPane(helper, add, true,pageListArray,keys));
                addDefaultToNextPageGridPane(helper,pageListArray);
                defaultTextFieldAdded = true;
            } else if (branchingType.getValue().equals(BranchingConstants.RADIO_QUESTION)) {
                logger.info("> {HTMLEditorContent}::createNextPageAnswersDialog: Branch Type RADIO_QUESTION");
                if (editNextPageAnswers) {
                    size = answerChoice.size();
                } else {
                    size = numberOfAnswerChoiceValue.getValue() == null ? 0 : Integer.parseInt(numberOfAnswerChoiceValue.getValue());
                }
                System.out.println(Arrays.toString(pageListArray));
                for (int i = 0; i < size; i++) {
                    addNextPageTextFieldToGridPane(this.countOfAnswer++, helper, editNextPageAnswers, false,pageListArray,keys);
                }
            }
        }
        String answerNextPage = getNextPageAnswersString(helper, defaultNextPageBox);
        logger.info("> {HTMLEditorContent}::createNextPageAnswersDialog: answerNextPage "+answerNextPage);
        if (answerNextPage.equalsIgnoreCase("{") || answerNextPage.equalsIgnoreCase("No Link")) {
            return "{}";
        } else
            return answerNextPage;
    }

    public String getNextPageAnswersString(GridPaneHelper helper, ComboBox defaultNextPageBox) {
        logger.info("> {HTMLEditorContent}::getNextPageAnswersString");
        Boolean clickedOk = helper.createGrid("Next Answer Page ", null, "ok", "Cancel");
        String answerNextPage;
        if (clickedOk) {
            if (!removeQueueAC.isEmpty()) {
                logger.info("> {HTMLEditorContent}::getNextPageAnswersString : removing from Answer Choice Queue");
                answerChoice.remove(answerChoice.size() - 1);
                for (TextField removeTF : removeQueueAC) {
                    answerChoice.remove(removeTF);
                }
            }
            if (!removeQueueAP.isEmpty()) {
                logger.info("> {HTMLEditorContent}::getNextPageAnswersString : removing from Answer Page Queue");
                answerPage.remove(answerPage.size() - 1);
                for (ComboBox removeCB : removeQueueAP) {
                    answerPage.remove(removeCB);
                }
            }
            if (!removeQueue.isEmpty()) {
                logger.info("> {HTMLEditorContent}::getNextPageAnswersString : removing from Remove Queue");
                for (String remove : removeQueue) {
                    String pageTwo = optionEntries.get(remove);
                    optionEntries.remove(remove);
                    TabPaneController pane = Main.getVignette().getController();
                    Button one = pane.getButtonPageMap().get(page.getPageName());
                    Button two = pane.getButtonPageMap().get(pageTwo);
                    ConnectPages connect = new ConnectPages(one, two, pane.getAnchorPane(), pane.getListOfLineConnector());
                    boolean pageDisconnected = connect.disconnectPages(page.getPageName(), pageTwo);
                    logger.info("> {HTMLEditorContent}::getNextPageAnswersString : "+ page.getPageName() + " and " + pageTwo +" disconnect ? "+ pageDisconnected);
                }
            }
            if (branchingType.getValue().equals(BranchingConstants.SIMPLE_BRANCH)) {
                defaultNextPage = (String) defaultNextPageBox.getSelectionModel().getSelectedItem();

                if (!defaultNextPage.equalsIgnoreCase("No Link")) {
                    if (!defaultNextPage.equalsIgnoreCase(page.getPageName())) {
                        VignettePage pageTwo = Main.getVignette().getPageViewList().get(defaultNextPage);

                        if (connectPages(pageTwo, "default")) {
                            TabPaneController paneController = Main.getVignette().getController();
                            System.out.println(page.getPagesConnectedTo());
                            paneController.makeFinalConnection(page);
                            updateOptionEntries();
                            logger.info("< {HTMLEditorContent}::getNextPageAnswersString : return "+defaultNextPage);
                            return "{'default':'" + defaultNextPage + "'}";
                        }
                        logger.info("< {HTMLEditorContent}::getNextPageAnswersString : return {'default':'general'}");
                        return "{'default':'general'}";


                    } else {
                        DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR, "Cannot Connect Pages",
                                null, "Pages May not connect to itself", false);
                    }
                } else {

                    if (page.getConnectedTo() != null) {
                        TabPaneController pane = Main.getVignette().getController();
                        Button one = pane.getButtonPageMap().get(page.getPageName());
                        Button two = pane.getButtonPageMap().get(page.getConnectedTo());
                        ConnectPages connect = new ConnectPages(one, two, pane.getAnchorPane(), pane.getListOfLineConnector());
                        boolean pageDisconnected = connect.disconnectPages(page.getPageName(), page.getConnectedTo());
                        logger.info("> {HTMLEditorContent}::getNextPageAnswersString : "+ page.getPageName() + " and " + page.getConnectedTo() +" disconnect ? "+ pageDisconnected);
                    }
                    logger.info("< {HTMLEditorContent}::getNextPageAnswersString : return No Link");
                    return "No Link";
                }

            }
            if (branchingType.getValue().equalsIgnoreCase(BranchingConstants.CHECKBOX_QUESTION)) {
                if (answerPage.get(answerPage.size() - 1) == null ) {
                    DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR, "Select default branching",
                            null, "For a checkbox question type there has to be a default branching out!", false);
                    logger.error("< {HTMLEditorContent}::getNextPageAnswersString : For a checkbox question type there has to be a default branching out!");
                    return "";
                }

            }
            AtomicBoolean selfConnection = new AtomicBoolean(false);
            for (ComboBox e : answerPage) {
                if (e != null && e.getValue() != null && e.getValue().toString().equalsIgnoreCase(page.getPageName())) {
                    DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR, "Cannot Connect Pages",
                            null, "Pages May not connect to itself", false);
                    logger.error("< {HTMLEditorContent}::getNextPageAnswersString : Pages May not connect to itself");
                    selfConnection.set(true);
                    break;
                }
            }
            if (selfConnection.get()) {
                answerChoice.clear();
                answerPage.clear();
                this.countOfAnswer = 0;
                this.defaultTextFieldIndex = -1;
                logger.error("< {HTMLEditorContent}::getNextPageAnswersString : RETURNING SINCE FOUND A SELF CONNECTION!");
                System.out.println("RETURNING SINCE FOUND A SELF CONNECTION!");
                return "";
            }

            for (int i = 0; i < answerChoice.size(); i++) {
                if(answerPage.get(i) != null && answerPage.get(i).getValue() != null){
                    String answerChoiceText =  answerChoice.get(i).getText();
//                    answerChoiceText = answerChoiceText.equalsIgnoreCase("default") ? answerChoiceText : answerChoiceText.toUpperCase();
                    optionEntries.put(answerChoiceText, answerPage.get(i).getValue().toString());
                }
            }

            for (String answer : optionEntries.keySet()) {
                String pageTo = optionEntries.get(answer);
                if (pageTo != null && !pageTo.equalsIgnoreCase(page.getPageName())) {
                    VignettePage pageTwo = Main.getVignette().getPageViewList().get(pageTo);
                    if (connectPages(pageTwo, answer)) {
                        logger.info("> {HTMLEditorContent}::getNextPageAnswersString : connected pages "+pageTwo.getPageName() + " and "+ answer);
                        optionEntries.replace(answer, pageTo);
                        inputValueChoices.add(answer);
                        updateOptionEntries();
                    }
                }
            }
            String opEStr = optionEntries.toString();
            opEStr = opEStr.replaceAll("=", "':'").replaceAll(" ", "").
                    replace("{", "{'").replaceAll("}", "'}").replaceAll(",", "','");
            answerNextPage = opEStr;

            if (branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)) {
                int size = answerPage.size();
                defaultNextPage = (String) answerPage.get(size - 1).getValue();
            }
            //pane object to highlight all the connection visually on UI
            TabPaneController pane = Main.getVignette().getController();
            pane.makeFinalConnection(page);
            updateOptionEntries();
            answerNextPage = answerNextPage.replaceAll(",$", "");
            this.editConnectionString = answerNextPage;
        } else {
            answerNextPage = "";
        }
        answerChoice.clear();
        answerPage.clear();
        this.countOfAnswer = 0;
        this.defaultTextFieldIndex = -1;
        logger.info("< {HTMLEditorContent}::getNextPageAnswersString : return "+ answerNextPage);
        return answerNextPage;
    }

    /**
     * @param helper grid helper
     * @param addLocal add button
     * @param addeDefault to add default or not
     * @return  Even handler
     */
    public EventHandler addToGridPane(GridPaneHelper helper, Button addLocal, boolean addeDefault,String[] pageListArray, Object[] keys) {
        return event -> {
            if (defaultTextFieldAdded) {
                helper.getGrid().getChildren().removeAll(helper, answerChoice.get(answerChoice.size() - 1), answerPage.get(answerPage.size() - 1));
                helper.getGrid().getChildren().remove(addLocal);
                defaultTextFieldAdded = false;
            }
            addNextPageTextFieldToGridPane(defaultTextFieldIndex - 1, helper, false, addeDefault,pageListArray,keys);
            if (addeDefault) {
                addDefaultToNextPageGridPane(helper,pageListArray);
                add = helper.addButton("+", 0, defaultTextFieldIndex + 1);
                add.setOnAction(addToGridPane(helper, add, true,pageListArray,keys));
            }
            helper.getDialogPane().getScene().getWindow().sizeToScene();
        };
    }

    public EventHandler removeFromGridPane(boolean addDefault, GridPaneHelper helper, TextField text, ComboBox dropdown, Button remove,String[] pageListArray,Object[] keys) {

        return event -> {
            if (defaultTextFieldAdded) {
                helper.getGrid().getChildren().removeAll(helper, answerChoice.get(answerChoice.size() - 1), answerPage.get(answerPage.size() - 1));
                helper.getGrid().getChildren().remove(add);
                defaultTextFieldAdded = false;
            }
            removeQueueAC.add(text);
            removeQueueAP.add(dropdown);
            removeQueue.add(text.getText());
            helper.getGrid().getChildren().removeAll(text, dropdown, remove);
            defaultTextFieldIndex = defaultTextFieldIndex - 1;
            if (addDefault) {
                addDefaultToNextPageGridPane(helper,pageListArray);
                add = helper.addButton("+", 0, defaultTextFieldIndex + 1);
                add.setOnAction(addToGridPane(helper, add, true,pageListArray,keys));
            }

        };
    }


    public void addDefaultToNextPageGridPane(GridPaneHelper helper,String[] pageListArray) {
        TextField text = helper.addTextField(0, defaultTextFieldIndex);
        text.setText("default");
        text.setEditable(false);
        ComboBox dropdown = helper.addDropDown(pageListArray, 1, defaultTextFieldIndex);
        if (optionEntries.size() > 0){
            dropdown.setValue(optionEntries.get("default"));
        }else{
            dropdown.setValue(pageListArray[0]);
        }
        answerChoice.add(text);
        answerPage.add(dropdown);
        defaultTextFieldIndex += 1;
        defaultTextFieldAdded = true;
    }

    /**
     * this function is used to support the '+' functionality to add pages textField in the next answer page dialog box
     *
     * @param index
     * @param helper
     * @param editNextPageAnswers
     * @param addDefault
     */

    public void addNextPageTextFieldToGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers, Boolean addDefault,String[] pageListArray,Object[] keys) {

        if (!editNextPageAnswers) {

            String textField = "";
            if (keys.length > index) {
                textField = keys[index].toString();
            }
            logger.info("> {HTMLEditorContent}:: addNextPageTextFieldToGridPane: textField "+textField);
            TextField text = helper.addTextField(0, index);
            text.setText(textField);
            ComboBox dropdown = helper.addDropDown(pageListArray, 1, index);
            dropdown.setVisibleRowCount(25);
            if (optionEntries.get(textField) != null) {
                dropdown.setValue(optionEntries.get(textField));
            } else if (!page.getPageName().equalsIgnoreCase(pageListArray[0])) {
                dropdown.setValue(pageListArray[0]);
            }
            logger.info("> {HTMLEditorContent}:: addNextPageTextFieldToGridPane: dropdown "+dropdown.getValue());
            answerChoice.add(text);
            answerPage.add(dropdown);
            Button remove = helper.addButton("-", 2, index);
            remove.setOnAction(removeFromGridPane(addDefault, helper, text, dropdown, remove,pageListArray,keys));
        } else {
            helper.addExistingTextField(answerChoice.get(index), 0, index);
            helper.addExistingDropDownField(answerPage.get(index), 1, index);
            Button remove = helper.addButton("-", 3, index);
            remove.setOnAction(removeFromGridPane(addDefault, helper, answerChoice.get(countOfAnswer), answerPage.get(countOfAnswer), remove,pageListArray,keys));
        }
    }

    public void editNextPageAnswers() {
        boolean scriptWasHidden = false;
        if (!page.getPageType().equals(BranchingConstants.SIMPLE_BRANCH)) {
            if (page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() > 0)
                numberOfAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() + "");
        }
        if (Main.getVignette().getController().getScriptIsHidden()) {
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }
        String htmlText = "";
        String nextPageAnswers = "";
        nextPageAnswers = createNextPageAnswersDialog(false, false);
        if (!"".equalsIgnoreCase(nextPageAnswers)) {
            Utility utility = new Utility();
            String questionType = BranchingConstants.QUESTION_TYPE + " = '" + utility.checkPageType(branchingType.getValue()) + "';";
            htmlText = htmlSourceCode.getText();
            Pattern p = Pattern.compile(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET);
            Matcher m = p.matcher(htmlSourceCode.getText());
            if (m.find()) {
                htmlSourceCode.selectRange(m.start(), m.end());
                htmlText =
                        htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET, BranchingConstants.NEXT_PAGE_ANSWER + "="
                                + nextPageAnswers + ";");

                Pattern questionPattern = Pattern.compile(BranchingConstants.QUESTION_TYPE_TARGET);
                String questionHtmlText = htmlSourceCode.getText();
                Matcher questionMatcher = questionPattern.matcher(questionHtmlText);
                if (questionMatcher.find()) {
                    htmlSourceCode.selectRange(questionMatcher.start(), questionMatcher.end());
                    htmlSourceCode.replaceSelection(BranchingConstants.QUESTION_TYPE + " = '" + page.getQuestionType() + "';");
                }
                page.setNextPageAnswerNames(BranchingConstants.NEXT_PAGE_ANSWER + "=" + nextPageAnswers + ";");
            }

            htmlSourceCode.replaceText(0, htmlSourceCode.getText().length(), htmlText);
            page.setPageData(htmlSourceCode.getText());
            Main.getVignette().getPageViewList().put(page.getPageName(), page);
        }
        if (scriptWasHidden) {
            Main.getVignette().getController().hideScript();
        }
    }


    /**
     * page setting button pane that appears on left toolbox
     */
    public void editPageSettings() {
        GridPaneHelper helper = new GridPaneHelper();

        //------------------------------------ HTML button CSS ---------------------------------------------------------
        String buttonStyle = "-fx-text-align: center;" + "-fx-background-color: transparent;" + "-fx-font-size: 25px;" +
                "-fx-border-radius: 7;" + "-fx-border-width: 3 3 3 3;";
        String pageColor = "-fx-text-fill:  #007bff;" + "-fx-color: #007bff;" + "-fx-border-color: #007bff;";
        String optionsColor = "-fx-text-fill:  #6c757d;" + "-fx-color: #6c757d;" + "-fx-border-color: #6c757d;";
        String probStatementColor = "-fx-text-fill:  #17a2b8;" + "-fx-color: #17a2b8;" + "-fx-border-color: #17a2b8;";
        String opacityCSS = "-fx-opacity:";
        //--------------------------------------------------------------------------------------------------------------
        boolean scriptWasHidden = false;
        if (Main.getVignette().getController().getScriptIsHidden()) {
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        String target = "//Settings([\\S\\s]*?)//settings";
        String htmlText = htmlSourceCode.getText();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);
        HashMap<String, Boolean> checkboxDisabled = new HashMap<>();
        HashMap<String, Double> opacityForButtons = new HashMap<>();


        //----------------------------------- Get current page settings ------------------------------------------------
        if (m.find()) {
            String[] defaultSettings = m.group(1).trim().split("\n");
            for (String s : defaultSettings) {
                Pattern defaultSettingPattern = Pattern.compile("^settings\\(\"([\\S\\s]*?)\",([\\S\\s]*?),([\\S\\s]*?)\\);$");
                s = s.trim();
                Matcher extractTags = defaultSettingPattern.matcher(s);
                if (extractTags.find()) {
                    String type = extractTags.group(1).trim();
                    checkboxDisabled.put(type, Boolean.parseBoolean(extractTags.group(2).trim()));
                    opacityForButtons.put(type, Double.parseDouble(extractTags.group(3).trim()));
                } else {
                    System.out.println("NOT FOUND!");
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //------------------------------------- EDIT OPTIONS -----------------------------------------------------------
        helper.addLabel("Options: ", 1, 1);
        CheckBox disabledOptions = helper.addCheckBox("Disable", 2, 1, true);
        disabledOptions.setSelected(checkboxDisabled.get(ConstantVariables.OPTION_PAGE_SETTING));
        helper.addLabel("Opacity", 3, 1);
        Spinner optionsSpinner = new Spinner(0.0, 1.0, opacityForButtons.get(ConstantVariables.OPTION_PAGE_SETTING), 0.1);


        //dealing with disabling the options setting
//        if(checkboxDisabled.get(ConstantVariables.OPTION_PAGE_SETTING))
//            optionsSpinner.setDisable(true);
//        else
//            optionsSpinner.setDisable(false);


        AtomicReference<Double> originalOptions = new AtomicReference<>((double) optionsSpinner.getValueFactory().getValue());

        disabledOptions.setOnAction(event -> {
            SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory = null;
            if (disabledOptions.isSelected()) {
                originalOptions.set((Double) optionsSpinner.getValueFactory().getValue());
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 0.5);
                optionsSpinner.getValueFactory().setValue(0.0);
            } else {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0);
                optionsSpinner.getValueFactory().setValue(originalOptions.get());
            }
            optionsSpinner.setVisible(true);
            spinnerValueFactory.setAmountToStepBy(0.1);
            optionsSpinner.setValueFactory(spinnerValueFactory);
        });


        helper.addSpinner(optionsSpinner, 4, 1);
        AtomicReference<Double> optionsOpacity = new AtomicReference<>((double) 1);
        Button optionsButton = new Button("Options");

        optionsButton.setStyle(buttonStyle + optionsColor + opacityCSS + opacityForButtons.get(ConstantVariables.OPTION_PAGE_SETTING) + ";");

        optionsSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            optionsOpacity.set((Double) newValue);
            optionsButton.setStyle(buttonStyle + optionsColor + opacityCSS + optionsOpacity + ";");
        });
        helper.addButton(optionsButton, 5, 1);
        //-------------------------------------------------------------------------------------------------------------

        //-------------------------------- EDIT PROBLEM STATEMENT ------------------------------------------------------
        helper.addLabel("Problem Statement: ", 1, 2);
        CheckBox disabledProblemStatement = helper.addCheckBox("Disable", 2, 2, true);
        disabledProblemStatement.setSelected(checkboxDisabled.get(ConstantVariables.PROBLEM_PAGE_SETTING));
        helper.addLabel("Opacity", 3, 2);
        Spinner problemStatementSpinner = new Spinner(0.0, 1.0, opacityForButtons.get(ConstantVariables.PROBLEM_PAGE_SETTING), 0.1);

        AtomicReference<Double> originalProblemStatement = new AtomicReference<>((double) problemStatementSpinner.getValueFactory().getValue());

        disabledProblemStatement.setOnAction(event -> {
            SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory = null;
            if (disabledProblemStatement.isSelected()) {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 0.5);
                originalProblemStatement.set((Double) problemStatementSpinner.getValueFactory().getValue());
                problemStatementSpinner.getValueFactory().setValue(0.0);
            } else {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0);
                problemStatementSpinner.getValueFactory().setValue(originalProblemStatement.get());
            }
            problemStatementSpinner.setVisible(true);
            spinnerValueFactory.setAmountToStepBy(0.1);
            problemStatementSpinner.setValueFactory(spinnerValueFactory);
        });

        helper.addSpinner(problemStatementSpinner, 4, 2);


        AtomicReference<Double> probOpacity = new AtomicReference<>((double) 0.1);
        Button probButton = new Button("Show Problem Statement");

        probButton.setStyle(buttonStyle + probStatementColor + opacityCSS + opacityForButtons.get(ConstantVariables.PROBLEM_PAGE_SETTING) + ";");

        problemStatementSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            probOpacity.set((Double) newValue);
            probButton.setStyle(buttonStyle + probStatementColor + opacityCSS + probOpacity + ";");
        });
        helper.addButton(probButton, 5, 2);
        //-------------------------------------------------------------------------------------------------------------

        //----------------------------------- EDIT PREV PAGE -----------------------------------------------------------

        helper.addLabel("Prev Page: ", 1, 3);
        CheckBox disabledPrevPage = helper.addCheckBox("Disable", 2, 3, true);
        disabledPrevPage.setSelected(checkboxDisabled.get(ConstantVariables.PREV_PAGE_PAGE_SETTING));
        helper.addLabel("Opacity", 3, 3);
        Spinner prevPageSpinner = new Spinner(0.0, 1.0, opacityForButtons.get(ConstantVariables.PREV_PAGE_PAGE_SETTING), 0.1);


        //dealing with disabling the previous page settings
//        if(checkboxDisabled.get(ConstantVariables.PREV_PAGE_PAGE_SETTING))
//            prevPageSpinner.setDisable(true);
//        else
//            prevPageSpinner.setDisable(false);

        AtomicReference<Double> originalPrevPage = new AtomicReference<>((double) prevPageSpinner.getValueFactory().getValue());


        disabledPrevPage.setOnAction(event -> {
            SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory = null;
            if (disabledPrevPage.isSelected()) {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 0.5);
                originalPrevPage.set((Double) prevPageSpinner.getValueFactory().getValue());
                prevPageSpinner.getValueFactory().setValue(0.1);
            } else {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0);
                prevPageSpinner.getValueFactory().setValue(originalPrevPage.get());
            }
            prevPageSpinner.setVisible(true);
            spinnerValueFactory.setAmountToStepBy(0.1);
            prevPageSpinner.setValueFactory(spinnerValueFactory);
        });


        helper.addSpinner(prevPageSpinner, 4, 3);
        AtomicReference<Double> prevPageOpacity = new AtomicReference<>((double) 0.1);
        Button prevPageButton = new Button("Back to Previous Page");

        prevPageButton.setStyle(buttonStyle + pageColor + opacityCSS + opacityForButtons.get(ConstantVariables.PREV_PAGE_PAGE_SETTING) + ";");

        prevPageSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            prevPageOpacity.set((Double) newValue);
            prevPageButton.setStyle(buttonStyle + pageColor + opacityCSS + prevPageOpacity + ";");
        });
        helper.addButton(prevPageButton, 5, 3);
        //-------------------------------------------------------------------------------------------------------------


        //----------------------------------- EDIT NEXT PAGE -----------------------------------------------------------

        helper.addLabel("Next Page: ", 1, 4);
        CheckBox disabledNextPage = helper.addCheckBox("Disable", 2, 4, true);
        disabledNextPage.setSelected(checkboxDisabled.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING));
        helper.addLabel("Opacity", 3, 4);
        Spinner nextPageSpinner = new Spinner(0.0, 1.0, opacityForButtons.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING), 0.1);

        AtomicReference<Double> originalNextPage = new AtomicReference<>((double) nextPageSpinner.getValueFactory().getValue());

        disabledNextPage.setOnAction(event -> {
            SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory = null;
            if (disabledNextPage.isSelected()) {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 0.5);
                originalNextPage.set((Double) nextPageSpinner.getValueFactory().getValue());
                nextPageSpinner.getValueFactory().setValue(0.1);
            } else {
                spinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1.0);
                nextPageSpinner.getValueFactory().setValue(originalNextPage.get());
            }
            nextPageSpinner.setVisible(true);
            spinnerValueFactory.setAmountToStepBy(0.1);
            nextPageSpinner.setValueFactory(spinnerValueFactory);
        });

        helper.addSpinner(nextPageSpinner, 4, 4);
        AtomicReference<Double> nextPageOpacity = new AtomicReference<>((double) 1);
        Button nextPageButton = new Button("Continue to Next Page");

        nextPageButton.setStyle(buttonStyle + pageColor + opacityCSS + opacityForButtons.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING) + ";");

        nextPageSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            nextPageOpacity.set((Double) newValue);
            nextPageButton.setStyle(buttonStyle + pageColor + opacityCSS + nextPageOpacity + ";");
        });
        helper.addButton(nextPageButton, 5, 4);
        //-------------------------------------------------------------------------------------------------------------


        boolean clickedOk = helper.createGrid("Page Settings", null, "Ok", "Cancel");
        if (clickedOk) {
            Pattern pattern = Pattern.compile(target);
            Matcher matcher = pattern.matcher(htmlText);

            if (matcher.find()) {
                System.out.println("found");
                String tag1 = "//Settings";
                String options = "\tsettings(\"options\", " + disabledOptions.isSelected() + ", " + optionsSpinner.getValue() + ");";
                String problemStatement = "\tsettings(\"problemStatement\", " + disabledProblemStatement.isSelected() + ", " + problemStatementSpinner.getValue() + ");";
                String prevPage = "\tsettings(\"PrevPage\", " + disabledPrevPage.isSelected() + ", " + prevPageSpinner.getValue() + ");";
                String nextPage = "\tsettings(\"NextPage\", " + disabledNextPage.isSelected() + ", " + nextPageSpinner.getValue() + ");";
                String tag2 = "//settings";
                String settings = tag1 + '\n' + options + '\n' + problemStatement + '\n' + prevPage + '\n' + nextPage + '\n' + tag2;
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(settings);
                //saving page data
                page.setPageData(htmlSourceCode.getText());
                Main.getVignette().getPageViewList().put(page.getPageName(), page);
            } else
                System.out.println("Page Settings not found");
        }
        if (scriptWasHidden) {
            Main.getVignette().getController().hideScript();
        }
    }

    //------------------------------ Adding Input Fields -----------------------

    /**
     * world to add an input field
     *
     * @param isImageField
     */
    public void addInputFields(boolean isImageField) {
        int field;
        field = htmlSourceCode.getCaretPosition();
//        GridPaneHelper helper1 = new GridPaneHelper();

        if (field <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please make sure the cursor in on the HTML editor!");
            alert.setTitle("Message");
            alert.show();
        } else {
            //createInputField(field, isImageField);
            GridPaneHelper helper1 = new GridPaneHelper();
            helper1.setResizable(false);
            //Buttons now manually created and added to allow them to be customizable.
            Button buttonNonBranch = new Button("Non Branching");
            buttonNonBranch.setPrefSize(1000, 60);
            buttonNonBranch.setOnAction(event -> {
                //for sum reason hiding the dialog box makes the screen unclickable
                // helper1.hideDialog();
                inputTypeFieldBranching = false;
                setInputType(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN);
                createInputField(field, isImageField, false);
                helper1.closeDialog();
            });

            helper1.addButton(buttonNonBranch, 0, 0);

            Button buttonAddBranching = new Button("Branching");
            buttonAddBranching.setPrefSize(1000, 60);
            buttonAddBranching.setOnAction(event -> {
                inputTypeFieldBranching = true;
                setInputType(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN);
                createInputField(field, isImageField, true);
                helper1.closeDialog();
            });
            helper1.addButton(buttonAddBranching, 0, 1);

            // Checking the page if there currently is a
            // --------CODE TO REBDER EXISTING BRANCHING QUESTION COMES HERE!!


//            Pattern branchPattern = Pattern.compile("<!-- //////// BranchQ //////// -->\n(.*?)<!-- //////// End BranchQ //////// -->\n", Pattern.DOTALL);
//            Matcher matcher;
//            matcher = branchPattern.matcher(htmlSourceCode.getText());
//
//            if (matcher.find()) {
//                Button buttonEditBranching = new Button("Edit Branching Input Field");
//                buttonEditBranching.setPrefSize(1000,60);
//                buttonEditBranching.setOnAction(event ->{createInputField(field, isImageField, true);});
//                helper1.addButton(buttonEditBranching,0,1);
//            }

            //Customize the GridPane
            helper1.setPrefSize(300, 100);

            //Display
            boolean create = helper1.create("Choose type of Input Field", "", "Cancel");
        }
    }

    public boolean getHasBranching() {
        return hasBranchingQuestion;
    }

    public void setHasBranchingQuestion(boolean value) {
        this.hasBranchingQuestion = value;
    }

    public void manageTextFieldsForInputFieldHelper(GridPaneHelper helper, int field, boolean isImageField, boolean isBranched) {

        if (getInputType().equalsIgnoreCase(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN) || getInputType().equalsIgnoreCase(ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN)) {
            helper.addLabel("Answer Key:", 0, 3);
            helper.addLabel("Input Value:", 1, 3);
            int listSize = 0;
            if (isBranched && numberOfAnswerChoiceValue != null)
                listSize = Integer.parseInt(numberOfAnswerChoiceValue.getValue());
            if (isBranched && listSize < page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size())
                listSize = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size();
            int size = listSize == 0 ? 4 : listSize;
            if (listSize > 0) {
                for (int i = 1; i <= listSize; i++) {
                    addInputFieldsToGridPane(i, helper, true, isImageField, isBranched, true);
                }
            } else {
                for (int i = 1; i <= size; i++) {
                    addInputFieldsToGridPane(i, helper, false, isImageField, isBranched, true);
                }
            }
            System.out.println(size + "  size");
            add = helper.addButton("+", 0, inputFieldsListBranching.size() + 4);
            add.setOnAction(addNewInputFieldToGridPane(helper, isImageField, isBranched,add));
        } else if (getInputType().equalsIgnoreCase(ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN) || getInputType().equalsIgnoreCase(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN)) {
            helper.removeAllFromHelper();
            helper.addLabel("Answer Key:", 0, 4);
            helper.addLabel("Input Value:", 1, 4);
            addStuffToHelper(helper, field, isImageField, isBranched);
            addInputFieldsToGridPane(1, helper, false, isImageField, isBranched, false);
        }
    }

    public EventHandler selectImageForQuestionText(TextArea questionInput) {
        AtomicReference<BufferedImage> image = new AtomicReference<>();

        return event -> {
            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images", "*.JPG", "*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);
            String imageSource = "";
            String imageString = "<img src=" + imageSource + " alt='Question Description' class='text-center' width='300px' height='400px'/>\n";
            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if (file != null) {
                String fileName = file.getName();
                try {
                    image.set(ImageIO.read(file));
                    Images images = new Images(fileName, image.get());
                    this.setImageSourceForQuestion(fileName);
                    Main.getVignette().addToImageList(images);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("PRESSED CANCEL");
            }
        };
    }

    public void addStuffToHelper(GridPaneHelper helper, int field, boolean isImageField, boolean isBranched) {
        String[] dropDownListBranching = {ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN, ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN};
        String[] dropDownListNonBranching = {ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN, ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN, ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN};
        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle("-fx-background-color: black;-fx-text-fill: white;-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        helper.addLabel("Question:", 0, 0);
        helper.addLabel("Input Type:", 2, 0);

        TextArea question = helper.addTextArea(0, 1);
        question.setWrapText(true);
        Button addImageFile = helper.addButton("Image File for question", 1, 0);
        Tooltip tooltip1 = new Tooltip();
        tooltip1.setStyle("-fx-font-size: 14");
        tooltip1.setMaxWidth(400);
        tooltip1.setWrapText(true);
        ImageView imageView = new ImageView();
        Image i = new Image(ConstantVariables.ADD_QUESTION_IMAGE);
        imageView.setImage(i);
        tooltip1.setGraphic(imageView);
        if (page.getVignettePageAnswerFieldsBranching().getQuestion() != null && isBranched)
            questionTextProperty().set(page.getVignettePageAnswerFieldsBranching().getQuestion());

        addImageFile.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (question.getCaretPosition() > 0 && question.getText().length() > 0) {
                    try {
                        String s1 = question.getText().substring(0, question.getCaretPosition());
                        String s2 = question.getText().substring(question.getCaretPosition());
                        tooltip1.setText("Insert Image between: '" + s1 + "' and '" + s2 + "'?");
                    } catch (Exception ex) {
                        System.out.println("SubString error: " + ex.getMessage());
                    }

                } else {
                    tooltip1.setText("Inserting at the start");
                }
                addImageFile.setTooltip(tooltip1);
            }
        });

        addImageFile.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        addImageFile.setTooltip(null);
                    }
                });


        addImageFile.addEventHandler(ActionEvent.ANY, actionEvent -> {
            AtomicReference<BufferedImage> image = new AtomicReference<>();
            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images", "*.JPG", "*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);
            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if (file != null) {
                String fileName = file.getName();
                try {
                    image.set(ImageIO.read(file));
                    Images images = new Images(fileName, image.get());
                    Main.getVignette().addToImageList(images);
                    System.out.println("Image List: " + Main.getVignette().getImagesList());
                    fileName = fileName.replaceAll("\\s", "%20");
                    String imageString = " <img src='Images/" + fileName + " alt='Question Description' class='text-center' width='300px' height='400px'/> ";
                    question.insertText(question.getCaretPosition(), imageString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("PRESSED CANCEL");
            }
        });
        // This prevents the user from selecting textarea and textfield options in branched questions
        ComboBox inputTypeDropDown;
        if (isBranched) {
            inputTypeDropDown = helper.addDropDown(dropDownListBranching, 3, 0);
            if (branchingType != null && !branchingType.getValue().equalsIgnoreCase("none")){
                inputTypeDropDown.setValue(branchingType.getValue());
            }else{
                inputTypeDropDown.setValue(dropDownListBranching[0]);
            }
        } else {
            inputTypeDropDown = helper.addDropDown(dropDownListNonBranching, 3, 0);
        }

        setInputName(page.getPageName());

//        if(branchingType.getValue()!=null && isBranched){
//            if(branchingType.getValue().equalsIgnoreCase(BranchingConstants.CHECKBOX_QUESTION))
//                inputTypeDropDown.setValue(ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN);
//            else if(branchingType.getValue().equalsIgnoreCase(BranchingConstants.RADIO_QUESTION))
//                inputTypeDropDown.setValue(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN);
//        }
//        inputTypeDropDown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//
//        });


        helper.addLabel("Input Name:", 2, 1);
        TextField inputName = helper.addTextField(page.getPageName(), 3, 1);

        inputName.setText(page.getPageName());
        inputName.textProperty().bindBidirectional(getInputName());
        //-----------------------
        //-----------------------
        if (isBranched && page.getVignettePageAnswerFieldsBranching().getQuestion() != null && !"".equalsIgnoreCase(page.getVignettePageAnswerFieldsBranching().getQuestion()))
            questionTextProperty().set(page.getVignettePageAnswerFieldsBranching().getQuestion());
        if (isBranched) {
            question.textProperty().bindBidirectional(questionTextProperty());
        } else {
            question.textProperty().bindBidirectional(questionTextNonBranchingProperty());
        }

        if (this.getInputType() == null) {
            if (isBranched)
                setInputType(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN);
            else
                setInputType(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN);
        }
        inputTypeDropDown.setOnAction(event -> {
            this.setInputType((String) inputTypeDropDown.getValue());
            if (((String) inputTypeDropDown.getValue()).equalsIgnoreCase(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN))
                this.branchingType.set(BranchingConstants.RADIO_QUESTION);
            else if (((String) inputTypeDropDown.getValue()).equalsIgnoreCase(ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN))
                this.branchingType.set(BranchingConstants.CHECKBOX_QUESTION);
            else
                this.branchingType.set(BranchingConstants.SIMPLE_BRANCH);
        });

    }

    public boolean inputTypeFieldBranching = false;

    public void createInputField(int field, boolean isImageField, boolean isBranched) {
        if (Main.getVignette().getController().getScriptIsHidden())
            Main.getVignette().getController().showScript();


        GridPaneHelper helper = new GridPaneHelper();
        // -----ADDING Question TextArea, InputValue TextField and label
        addStuffToHelper(helper, field, isImageField, isBranched);

        //Keep on adding options
        manageTextFieldsForInputFieldHelper(helper, field, isImageField, isBranched);
        CheckBox isRequired = helper.addCheckBox("isRequired", 1, 2, true);
        isRequired.setSelected(true);

        if (isBranched && !BranchingConstants.SIMPLE_BRANCH.equalsIgnoreCase(page.getQuestionType()))
            setInputType(page.getQuestionType());
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(helper);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        Boolean clickedOk = helper.createGrid("Input Field", null, "ok", "Cancel");

        if (clickedOk) {
            //adding question to the pageList!!!
            addInputFieldToHtmlEditor(isImageField, isBranched, isRequired.isSelected());
            if (isBranched) {
                page.getVignettePageAnswerFieldsBranching().setQuestion(questionText.getValue());
                page.getVignettePageAnswerFieldsBranching().setQuestionName(getInputName().get());
            } else {
                page.getVignettePageAnswerFieldsNonBranching().get(page.getVignettePageAnswerFieldsNonBranching().size() - 1).setQuestion(questionText.getValue());
                page.getVignettePageAnswerFieldsNonBranching().get(page.getVignettePageAnswerFieldsNonBranching().size() - 1).setQuestionName(getInputName().get());
            }
            Pattern branchPatternNewToAddTags = Pattern.compile("<!--pageQuestions-->([\\S\\s]*?)<!--pageQuestions-->", Pattern.CASE_INSENSITIVE);

            //Creating HTML string for the page questions
            Questions[] questionArray = new Questions[page.getQuestionList().size()];
            for (int i = 0; i < page.getQuestionList().size(); i++) {
                questionArray[i] = new Questions(page.getQuestionList().get(i));
            }
            if (ReadFramework.getUnzippedFrameWorkDirectory().endsWith("/"))
                ReadFramework.listFilesForFolder(new File(ReadFramework.getUnzippedFrameWorkDirectory() + "pages/questionStyle/"), Questions.getQuestionStyleFileList());
            else
                ReadFramework.listFilesForFolder(new File(ReadFramework.getUnzippedFrameWorkDirectory() + "/pages/questionStyle/"), Questions.getQuestionStyleFileList());
            String questionHTMLTag = Questions.createQuestions(questionArray);
            //Replace existing question
            //creating question String
            String comments = "<!--pageQuestions-->";
            if (isBranched) {
                Pattern branchingQuestionPattern = Pattern.compile("<!--BranchQ-->([\\S\\s]*?)<!--BranchQ-->", Pattern.CASE_INSENSITIVE);
                Matcher findBranchingQuestion = branchingQuestionPattern.matcher(htmlSourceCode.getText());
                String branchQComments = "<!-- BranchQ-->\n";
                if (findBranchingQuestion.find()) {
                    htmlSourceCode.selectRange(findBranchingQuestion.start(), findBranchingQuestion.end());
                    htmlSourceCode.replaceSelection(questionHTMLTag);
                }
            }
            String addingCommentsToHtmlTag = comments + "\n" + questionHTMLTag + comments;
            // end of creating question String

            Matcher matcher;
            matcher = branchPatternNewToAddTags.matcher(htmlSourceCode.getText());
            if (matcher.find()) {
                //question tag exists
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(addingCommentsToHtmlTag);
            } else {
                //question tag to clipboard
                String questionTagToAdd = "<!-- //////// Question //////// -->\n" +
                        "    <div class=\"question_page\">\n" +
                        "       <div class=\"questions mb-2\">\n" +
                        "            <!--pageQuestions-->\n" +
                        questionHTMLTag +
                        "            <!--pageQuestions-->\n" +
                        "       </div>\n" +
                        "    </div>\n";
                StringProperty questionText = new SimpleStringProperty(questionTagToAdd);
                Popup tp = new Popup();
                Label message = new Label();
                questionText.set("Click on the line in the text editor where you want to place this new question.");
                message.textProperty().bindBidirectional(questionText);
                message.setWrapText(true);
                message.setTextAlignment(TextAlignment.RIGHT);
                tp.getContent().add(message);
                message.setStyle(" -fx-background-color: darkgray; -fx-font-weight: bold; -fx-font-size: 14px; -fx-color-label-visible: white; -fx-padding: 10; -fx-text-alignment: right;");
                StringProperty finalQuestionText = new SimpleStringProperty(questionTagToAdd);

                htmlSourceCode.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!"".equalsIgnoreCase(questionText.get())) {
                            tp.show(Main.getStage());
                        }
                    }
                });
                htmlSourceCode.setOnMouseClicked(mouseEvent -> {
                    if (!"".equals(finalQuestionText.get())) {
                        int offset = htmlSourceCode.getCaretPosition();
                        TwoDimensional.Position pos = htmlSourceCode.offsetToPosition(offset, null);
                        GridPaneHelper confirmation = new GridPaneHelper();
                        confirmation.setResizable(false);
                        Label pageNameLabel = new Label("Adding question to line: " + (pos.getMajor() + 1));
                        pageNameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
                        confirmation.add(pageNameLabel, 0, 0, 1, 1);
                        Boolean confirmedInsertion = confirmation.createGrid("Place question at", null, "ok", "Cancel");
                        if (confirmedInsertion) {
                            htmlSourceCode.insertText(htmlSourceCode.getCaretPosition(), "\n" + finalQuestionText.get());
                            questionText.set("");
                            finalQuestionText.set("");
                            tp.hide();
                            page.setPageData(htmlSourceCode.getText());
                        }
                    }
                });

            }

            //Default procedure to do question:
            numberOfAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size() + "");
            branchingType.set(page.getQuestionType());

            if (inputTypeProperty.equalsIgnoreCase("radio"))
                branchingType.set(BranchingConstants.RADIO_QUESTION);
            else if (inputTypeProperty.equalsIgnoreCase("checkbox"))
                branchingType.set(BranchingConstants.CHECKBOX_QUESTION);
            else
                branchingType.set(BranchingConstants.SIMPLE_BRANCH);
            System.out.println("branching Type " + branchingType.getValue());
            if (!isBranched) {
                page.setNumberOfNonBracnchQ(page.getNumberOfNonBracnchQ() + 1);
            } else {
                page.setQuestionType(getInputType());
                //changing question type thing
                Pattern p = Pattern.compile(BranchingConstants.QUESTION_TYPE_TARGET);
                Matcher m = p.matcher(htmlSourceCode.getText());
                int start = 0;
                while(m.find(start)){
                    start =  m.start() + 1;
                    htmlSourceCode.selectRange(m.start(), m.end());
                    htmlSourceCode.replaceSelection(BranchingConstants.QUESTION_TYPE + " = '" + getInputType() + "'");
                    m = p.matcher(htmlSourceCode.getText());
                }
            }
            // default prodecure to add question
            //saving changes
            page.setPageData(htmlSourceCode.getText());
            Main.getVignette().getPageViewList().put(page.getPageName(), page);
        }
        helper.getGrid().getChildren().clear();
        helper.removeAllFromHelper();
        helper.clear();
        setInputType("");
        setQuestionTextNonBranching("");
        setInputName("");
        inputFieldsListBranching.clear();
        optionImagesForImageInputField.clear();
        inputFieldsListNonBranching.clear();
        helper.closeDialog();
    }

    /**
     * add input fields to the HTML content
     *
     * @param index
     * @param helper
     * @param editAnswers
     * @param isImageField
     */
    public void addInputFieldsToGridPane(int index, GridPaneHelper helper, Boolean editAnswers,
                                         Boolean isImageField, boolean isBranched, boolean displayAddRemoveButtons) {
        TextField answerField = null;
        InputFields fields = new InputFields();
        Button file = null;
        if (isImageField) {
            file = helper.addButton("Image File", 0, index + 3, fileChoose(fields));
            fields.answerKeyProperty().set(file.getText());
            file.textProperty().bindBidirectional(fields.answerKeyProperty());
        } else {
            answerField = helper.addTextField("option choice " + index, 0, index + 3);
            answerField.textProperty().bindBidirectional(fields.answerKeyProperty());
        }

        TextField inputValue;
        inputValue = helper.addTextField(1, index + 3);
        inputValue.textProperty().bindBidirectional(fields.inputValueProperty());

        char c = (char) (index + 65 - 1);
        inputValue.setText(c + "");
        inputValue.setEditable(false);
        fields.setId(index);
        fields.setImageField(isImageField);
        fields.setInputType(getInputType());
        fields.setInputName(getInputName().getValue());
        int size = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size();
        if (isBranched && inputTypeFieldBranching && size > 0 && index - 1 < size) {
            if (page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index - 1) != null) {
                AnswerField temp = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index - 1);
                inputValue.setText(temp.getInputValue());
                answerField.setText(temp.getAnswerKey());
            }
        }
        //todo non branching qs cannot use input tags
        int removeIndex;
        if (isBranched) {
            inputFieldsListBranching.add(fields);
            removeIndex = inputFieldsListBranching.size();
        } else {
            inputFieldsListNonBranching.add(fields);
            removeIndex = inputFieldsListNonBranching.size();
        }
        if (displayAddRemoveButtons) {
            Button remove = helper.addButton("-", 2, index + 3);
            remove.setOnAction(removeInputFieldFromGridPane(helper,
                    isImageField, file, answerField, inputValue,
                    add, remove, fields, removeIndex, isBranched));
        }
    }

    /**
     * @param helper
     * @param isImageField
     * @return
     */
    public EventHandler addNewInputFieldToGridPane(GridPaneHelper helper, Boolean isImageField, boolean isBranched,Button addLocal) {
        return event -> {
            helper.getGrid().getChildren().remove(addLocal);
            if (isBranched) {
                addInputFieldsToGridPane(inputFieldsListBranching.size() + 1, helper, false, isImageField, isBranched, true);
            } else {
                addInputFieldsToGridPane(inputFieldsListNonBranching.size() + 1, helper, false, isImageField, isBranched, true);
            }
            add = helper.addButton("+", 0, inputFieldsListBranching.size() + 4);
            add.setOnAction(addNewInputFieldToGridPane(helper, isImageField, isBranched,add));
            helper.getDialogPane().getScene().getWindow().sizeToScene();
        };
    }


    public EventHandler removeInputFieldFromGridPane(GridPaneHelper helper, boolean isImageField, Button file,
                                                     TextField answerKey, TextField inputValue, Button add, Button remove, InputFields fields,
                                                     int index, boolean isBranched) {

        return event -> {
            if (isImageField) {
                helper.getGrid().getChildren().removeAll(file, inputValue, add, remove);
            } else {
                helper.getGrid().getChildren().removeAll(answerKey, inputValue, add, remove);
            }
            System.out.println("REMOVE INDEX : " + index);
            if (isBranched) {
                inputFieldsListBranching.remove(fields);
            } else {
                inputFieldsListNonBranching.remove(fields);
            }
        };

    }

    public void addInputFieldToHtmlEditor(boolean isImageField, boolean isBranched, boolean isRequired) {
        String question = "";
        if (isBranched) {
            question = questionText.getValue();
        } else {
            question = questionTextNonBranching.getValue();
        }
        ArrayList<String> optionsList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        String textValue = inputNameProperty.getValue();
        String name = "";
        if (isBranched) {
            name += "b-" + textValue;
            if ("b-".equalsIgnoreCase(name))
                name += page.getPageName();
        } else {
            name += "nb" + (page.getNumberOfNonBracnchQ() + 1) + "-" + textValue;
            if (("nb" + (page.getNumberOfNonBracnchQ() + 1) + "-").equalsIgnoreCase(name))
                name += "nb" + (page.getNumberOfNonBracnchQ() + 1) + "-" + page.getPageName();
        }
        List<InputFields> inputFieldsList;
        if (isBranched) {
            inputFieldsList = new ArrayList<>(inputFieldsListBranching);
        } else {
            inputFieldsList = new ArrayList<>(inputFieldsListNonBranching);
        }
        VignettePageAnswerFields temp = new VignettePageAnswerFields();
        String type = inputFieldsList.get(0).getInputType();

        for (int i = 0; i < inputFieldsList.size(); i++) {
            InputFields input = inputFieldsList.get(i);
            inputFieldsList.get(i).setInputType(this.inputTypeProperty);
            if (isImageField) {
                String imageName = input.getAnswerKey().split(":")[1].trim();
                if (!"".equalsIgnoreCase(imageName) && imageName != null) {
                    optionsList.add(input.getAnswerKey().split(":")[1].trim());
                    Main.getVignette().getImagesList().add(optionImagesForImageInputField.get(input.getInputValue()));
                }
            } else
                optionsList.add(input.getAnswerKey());
            valueList.add(input.getInputValue());
            AnswerField answerField = new AnswerField();
            answerField.setAnswerKey(input.getAnswerKey());
            answerField.setInputName(input.getInputName());
            answerField.setInputValue(input.getInputValue());
            temp.getAnswerFieldList().add(answerField);
        }
        if (isBranched)
            page.setVignettePageAnswerFieldsBranching(temp);
        else
            page.addAnswerFieldToNonBranching(temp);

        String[] o = new String[optionsList.size()];
        for (int i = 0; i < optionsList.size(); i++)
            o[i] = optionsList.get(i);
        String[] v = new String[valueList.size()];
        for (int i = 0; i < valueList.size(); i++)
            v[i] = valueList.get(i);
        try {
            Questions q = null;
            if (isBranched) {
                q = new Questions(type.trim(), question.trim(), this.getImageSourceForQuestion(), o, v, name, isBranched, isRequired, isImageField);
                if (!page.isHasBranchingQuestion()) {
                    page.addToQuestionList(q);
                    page.setHasBranchingQuestion(true);
                } else {
                    System.out.println("PAGE ALREADY HAS A BRANCHING QUESTION!!");
                    int index = 0;
                    for (Questions tempQuestion : page.getQuestionList()) {
                        if (tempQuestion.getBranchingQuestion())
                            break;
                        index++;
                    }
                    page.getQuestionList().set(index, q);
                }
            } else {
                //Not a branching question
                q = new Questions(type.trim(), question.trim(), this.getImageSourceForQuestion(), o, v, name, isBranched, isRequired, isImageField);
                page.addToQuestionList(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setImageSourceForQuestion("");
        optionsList.clear();
        valueList.clear();
    }

    public transient HashMap<String, Images> optionImagesForImageInputField = new HashMap<>();

    public EventHandler fileChoose(InputFields fields) {
        final String[] fileName = {null};
        AtomicReference<BufferedImage> image = new AtomicReference<>();
        return event -> {

            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images", "*.JPG", "*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);

            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if (file != null) {
                fileName[0] = file.getName();
                try {
                    image.set(ImageIO.read(file));
                    Images images = new Images(fileName[0], image.get());
                    fields.setImages(fileName[0]);
                    optionImagesForImageInputField.put(fields.getInputValue(), images);
                    ((Button) event.getSource()).setText("Image File Selected: " + fileName[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * connects source and target buttons
     */
    public boolean connectPages(VignettePage pageTwo, String... pageKey) {
        TabPaneController pane = Main.getVignette().getController();
        Button source = pane.getButtonPageMap().get(page.getPageName());
        if(!pageTwo.getPageName().equalsIgnoreCase("No Link")){
            Button target = pane.getButtonPageMap().get(pageTwo.getPageName());
            boolean data = pane.checkPageConnection(page, pageTwo, source, target, pageKey);
            return data;
        }
        return false;

    }


    // -----------GETTERS AND SETTERS--------------------
    public VignettePage getPage() {
        return this.page;
    }

    public String getPageData() {
        return this.page.getPageData();
    }

    public String getQuestionText() {
        return questionText.get();
    }

    public StringProperty questionTextProperty() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText.set(questionText);
    }

    public String getInputType() {
        return inputTypeProperty;
    }

    public void setInputType(String inputType) {
        this.inputTypeProperty = inputType;
    }

    public StringProperty getInputName() {
        return inputNameProperty;
    }

    public void setInputName(String inputName) {
        this.inputNameProperty.set(inputName);
    }
}

class ArrowFactory implements IntFunction<Node> {
    private final ObservableValue<Integer> shownLine;

    ArrowFactory(ObservableValue<Integer> shownLine) {
        this.shownLine = shownLine;
    }

    @Override
    public Node apply(int lineNumber) {
        Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
        triangle.setFill(Color.GREEN);

        ObservableValue<Boolean> visible = Val.map(
                shownLine,
                sl -> sl == lineNumber);

        triangle.visibleProperty().bind(((Val<Boolean>) visible).conditionOnShowing(triangle));

        return triangle;
    }
}

class SortIgnoreCase implements Comparator<Object> {
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }
}