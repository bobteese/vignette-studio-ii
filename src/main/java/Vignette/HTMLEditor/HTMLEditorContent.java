package Vignette.HTMLEditor;

import Application.Main;
import Vignette.Framework.ReadFramework;
import Vignette.Page.Questions;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import DialogHelpers.DialogHelper;
import DialogHelpers.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import Utility.Utility;
import Vignette.Branching.BranchingImpl;
import Vignette.HTMLEditor.InputFields.InputFields;
import Vignette.Page.AnswerField;
import Vignette.Page.VignettePage;
import ConstantVariables.ConstantVariables;
import Vignette.Page.VignettePageAnswerFields;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import org.apache.commons.io.IOUtils;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.reactfx.value.Val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import TabPane.TabPaneController;
import ConstantVariables.BranchingConstants;


public class HTMLEditorContent {

    private CodeArea htmlSourceCode;

    String style =" ";



    private String type;
    private VignettePage page;
    private int countOfAnswer = 0;
    private List<String> pageNameList;
    private  List<TextField> answerChoice;
    private List<ComboBox> answerPage;
    String nextPageAnswers ;
    BufferedImage image;
    private Logger logger = LoggerFactory.getLogger(SaveAsVignette.class);
    BranchingImpl branching;
    List<InputFields> inputFieldsListNonBranching;
    List<InputFields> inputFieldsListBranching;
    private final StringProperty questionText = new SimpleStringProperty();
    SimpleStringProperty numberOfAnswerChoiceValue;
    SimpleStringProperty branchingType;
    private String inputTypeProperty;
    private StringProperty inputNameProperty = new SimpleStringProperty();;
    String defaultNextPage = null;
    private String editConnectionString="";
    HashMap<String, String> optionEntries = new HashMap<>();

    public String getHtmlDataForPage() {
        return htmlDataForPage.get();
    }

    public StringProperty htmlDataForPageProperty() {
        return htmlDataForPage;
    }

    public void setHtmlDataForPage(String htmlDataForPage) {
        this.htmlDataForPage.set(htmlDataForPage);
    }

    private StringProperty htmlDataForPage = new SimpleStringProperty();
    private StringProperty imageSourceForQuestion = new SimpleStringProperty();

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

    public static boolean matchSingleTonTag(String inputTag){
        String[] tags = {"<area", "<base", "<br", "<col", "<embed", "<hr", "<img", "<input", "<link", "<meta", "<param", "<source", "<track", "<wbr"};
        for(String tag:tags){
            if(inputTag.startsWith(tag))
                return true;
        }
        return false;
    }
    public HTMLEditorContent(CodeArea htmlSourceCode,
                             String type, VignettePage page,
                             List<String> pageNameList,
                             SimpleStringProperty branchingType,
                             SimpleStringProperty numberOfAnswerChoiceValue, Label pageName){
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.page = page;
        this.pageNameList = pageNameList;
        answerChoice= new ArrayList<>();
        answerPage = new ArrayList<>();
        this.branching = new BranchingImpl(this.page);
        inputFieldsListBranching =  new ArrayList<>();
        inputFieldsListNonBranching =  new ArrayList<>();
        this.numberOfAnswerChoiceValue = numberOfAnswerChoiceValue;
        this.branchingType = branchingType;
        this.pageTab = pageTab;
        pageName.setAlignment(Pos.CENTER);
        pageName.setText("Current Page: "+page.getPageName());
        pageName.setWrapText(true);
        pageName.setTextAlignment(TextAlignment.JUSTIFY);
        pageName.setMaxWidth(300);
        pageName.setTranslateX(0);
        pageName.setTranslateY(0);
        updateOptionEntries();
        this.htmlSourceCode.setWrapText(true);
        if(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()>0)
            branchingType.set(page.getQuestionType());
        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle("-fx-background-color: black;-fx-text-fill: white;-fx-padding: 5;");
        popup.getContent().add(popupMsg);
        Pattern youtubeScriptPattern = Pattern.compile("YouTubeVideoScript");
        Matcher match =  youtubeScriptPattern.matcher(htmlSourceCode.getText());
        this.htmlSourceCode.setMouseOverTextDelay(Duration.ofMillis(100));
        this.htmlSourceCode.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            Point2D pos = e.getScreenPosition();
            if(htmlSourceCode.getSelectedText().equals("YouTubeVideoScript")){
                popupMsg.setText("Youtube Script comes here");
            }else if(htmlSourceCode.getSelectedText().equals("VimeoVideoScript")){
                popupMsg.setText("Vimeo video Script comes here");
            }else if(htmlSourceCode.getSelectedText().equals("pageQuestions")){
                popupMsg.setText("All Branching and NonBranching Question comes here");
            }else{
                popupMsg.setText("Nothing to note!");
            }
            popup.show(htmlSourceCode, pos.getX(), pos.getY() + 10);
        });
        this.htmlSourceCode.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            popup.hide();
        });

        this.htmlSourceCode.setParagraphGraphicFactory(LineNumberFactory.get(this.htmlSourceCode));
        IntFunction<Node> numberFactory = LineNumberFactory.get(this.htmlSourceCode);
        IntFunction<Node> arrowFactoryStart = new ArrowFactory(this.htmlSourceCode.currentParagraphProperty());
        IntFunction<Node> arrowFactoryEnd = new ArrowFactory(this.htmlSourceCode.currentParagraphProperty());

        this.htmlSourceCode.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            this.htmlSourceCode.setParagraphGraphicFactory(null);
            this.htmlSourceCode.setParagraphGraphicFactory(LineNumberFactory.get(this.htmlSourceCode));
        });
        final BooleanProperty shiftPressed = new SimpleBooleanProperty(false);
        final BooleanProperty directionKeyPresses = new SimpleBooleanProperty(false);
        final BooleanBinding shiftAndArrowKeyPressed = shiftPressed.and(directionKeyPresses);
        shiftAndArrowKeyPressed.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                System.out.println("TIME TO REMOVE ARROWS!!");
            }
        });
        this.htmlSourceCode.addEventHandler(KeyEvent.ANY, keyEvent -> {
            if(keyEvent.getCode()==KeyCode.SHIFT && keyEvent.getCode()==KeyCode.DOWN){
                this.htmlSourceCode.setParagraphGraphicFactory(null);
                this.htmlSourceCode.setParagraphGraphicFactory(LineNumberFactory.get(this.htmlSourceCode));
            }
        });
        this.htmlSourceCode.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.SHIFT) {
                    shiftPressed.set(true);
                } else if (ke.getCode() == KeyCode.RIGHT || ke.getCode() == KeyCode.LEFT || ke.getCode() == KeyCode.UP || ke.getCode() == KeyCode.DOWN) {
                    directionKeyPresses.set(true);
                }
            }
        });
        this.htmlSourceCode.setOnMouseClicked(evt -> {
//            System.out.println("shiftAndArrowKeyPressed.get():"+shiftAndArrowKeyPressed.get());
            if(this.htmlSourceCode.getSelectedText().length()==0 && !shiftAndArrowKeyPressed.get()){
                if (evt.getButton() == MouseButton.PRIMARY) {
                    Main.getVignette().getController().defaultStyle();
                    // check, if click was inside the content area
                    Node n = evt.getPickResult().getIntersectedNode();
                    while (n != this.htmlSourceCode) {
                        if (htmlSourceCode.getCaretPosition()>0) {
                            int caretPosition = this.htmlSourceCode.getCaretPosition();
                            String text = this.htmlSourceCode.getText();
                            int lineBreak1 = text.lastIndexOf('\n', caretPosition - 1);
                            int lineBreak2 = text.indexOf('\n', caretPosition);
                            if (lineBreak2 < 0) {
                                lineBreak2 = text.length();
                            }
                            String selectedText = this.htmlSourceCode.getText(lineBreak1+1, lineBreak2);
                            System.out.println(selectedText);
                            this.htmlSourceCode.deselect();
                            Pattern htmlClosingPattern  = Pattern.compile("</(.*)>");
                            Pattern htmlOpeningPattern  = Pattern.compile("<([a-z]+) *[^/]*?>");
                            if((htmlOpeningPattern.matcher(selectedText).find() && htmlClosingPattern.matcher(selectedText).find())){
                                //HTML with opening and closing on the same line
                                IntFunction<Node> arrowFactoryEndSingle = new ArrowFactory(this.htmlSourceCode.currentParagraphProperty());
                                IntFunction<Node> graphicFactory = line -> {
                                    HBox hbox = new HBox(
                                            arrowFactoryEndSingle.apply(line));
                                    hbox.setAlignment(Pos.CENTER_LEFT);
                                    return hbox;
                                };
                                this.htmlSourceCode.setParagraphGraphicFactory(graphicFactory);
                                evt.consume();
                                return;
                            }
                            String getClosingTagtext = "";
                            int index;int endArrowIndex = 0;
                            if(htmlClosingPattern.matcher(selectedText).find()){
                                getClosingTagtext = this.htmlSourceCode.getText().substring(0, lineBreak2+1);
                                String totalLines[] = getClosingTagtext.split("\n");
                                Matcher m = htmlClosingPattern.matcher(selectedText);
                                String openingTag="";
                                String closingTag="";
                                if(m.find()){
                                    openingTag = "<"+m.group(1);
                                    closingTag = "</"+m.group(1)+">";
                                }
                                ArrayList<String> temp  = new ArrayList<>() ;
                                int stackPointer = 0;
                                index=totalLines.length-1;
                                temp.add(stackPointer++, totalLines[index--]);
                                int pushedCount = 1; int poppedCount = 0;
                                while (temp.size()>0 || index>0){
                                    totalLines[index]=totalLines[index].trim();
                                    if(Pattern.compile(">(.*?)</(.*?)").matcher(totalLines[index].trim()).find() || matchSingleTonTag(totalLines[index].trim())){
                                        index--;
                                        continue;
                                    }
                                    if(htmlClosingPattern.matcher(totalLines[index]).find()){
                                        temp.add(stackPointer++, totalLines[index]);
                                        pushedCount++;
                                    }
                                    else if(htmlOpeningPattern.matcher(totalLines[index]).find()){
                                        temp.remove(--stackPointer);
                                        poppedCount++;
                                    }
                                    if(temp.size()==0)
                                        break;
                                    index--;
                                }
                                if(index==-1)
                                    index++;
                                endArrowIndex = (totalLines.length-1 - index)*-1;
                            }else{
                                getClosingTagtext = this.htmlSourceCode.getText().substring(lineBreak1+1, this.htmlSourceCode.getText().length()-1);
                                String totalLines[] = getClosingTagtext.split("\n");
                                Matcher m = htmlOpeningPattern.matcher(selectedText);
                                String openingTag="";
                                String closingTag="";
                                if(m.find()){
                                    openingTag = "<"+m.group(1);
                                    closingTag = "</"+m.group(1)+">";
                                }
                                index=1;
                                ArrayList<String> temp  = new ArrayList<>() ;
                                int stackPointer = 0;
                                temp.add(stackPointer++, totalLines[0]);
                                int selectionIndex = htmlSourceCode.getCaretPosition() - openingTag.indexOf(htmlSourceCode.getText().charAt(htmlSourceCode.getCaretPosition())) + 1;
                                int pushedCount = 1; int poppedCount = 0;
                                while (temp.size()>0 && index<totalLines.length){
                                    if(Pattern.compile(">(.*?)</(.*?)").matcher(totalLines[index].trim()).find()){
                                        index++;
                                        continue;
                                    }
                                    if(totalLines[index].trim().startsWith(openingTag)){
                                        temp.add(stackPointer++, totalLines[index]);
                                        pushedCount++;
                                    }
                                    else if(Pattern.compile(closingTag).matcher(totalLines[index].trim()).find()){
                                        temp.remove(--stackPointer);
                                        poppedCount++;
                                    }
                                    if(temp.size()==0)
                                        break;
                                    index++;
                                }
                                if(index==totalLines.length)
                                    index--;
                                endArrowIndex = index;
                            }
                            final int secondLineIndex = endArrowIndex;
                            IntFunction<Node> graphicFactory = line -> {
                                HBox hbox = new HBox(
                                        numberFactory.apply(line),
                                        arrowFactoryStart.apply(line),
                                        arrowFactoryEnd.apply(line-secondLineIndex));
                                hbox.setAlignment(Pos.CENTER_LEFT);
                                return hbox;
                            };
                            this.htmlSourceCode.setParagraphGraphicFactory(graphicFactory);
                            evt.consume();
                            break;
                        }
                        n = n.getParent();
                    }
                }
            }
        });

    }
    public void updateOptionEntries(){
        for (HashMap.Entry<String, String> entry : page.getPagesConnectedTo().entrySet()) {
            String[] temp = entry.getValue().split(",");
            for(String x: temp)
                this.optionEntries.put(x.trim(), entry.getKey());
        }
    }
//    public void addDropDown(){
//        defaultNextPage.getItems().clear();
//        defaultNextPage.getItems().addAll(pageNameList);
//    }

    /**
     * Sets the Text for TextArea displayed on the right to show the HTML content for a vignette page
     * @return String
     * @throws URISyntaxException
     * @throws FileNotFoundException
     */
    public String addTextToEditor() throws URISyntaxException, IOException {
        System.out.println(Main.getVignette().getHtmlFiles());
        String text = null;
        InputStream inputStream = null;
//        if(Main.defaultFramework){
//            System.out.println("IT IS A DEFAULT FRAMEWORK!!");
//            if(!type.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) {
//                inputStream = getClass().getResourceAsStream(ConstantVariables.PAGE_TYPE_LINK_MAP.get(type));
//                text = readFile(inputStream);
//            }
//            else{
//                text= ConstantVariables.SCRIPT_FOR_CUSTOM_PAGE;
//            }
//        }else {
            if(!type.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) {
                ZipFile zipFile = new ZipFile(Main.getFrameworkZipFile());
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                ZipEntry entry = null;
                while(entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    if(entry.getName().equalsIgnoreCase(page.getPageType()))
                        break;
                }
                if(entry!=null){
                    InputStream stream = new FileInputStream(ReadFramework.getUnzippedFrameWorkDirectory()+"/pages/"+ this.type +".html");
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
//        }
        htmlSourceCode.replaceText(0,htmlSourceCode.getText().length(),text);
        //replacing text is undoable in richtextfx, we don't want the user to have this in the undo/redo stack
        htmlSourceCode.getUndoManager().forgetHistory();



        //after opening the page, first it will set the initial text. Print statement below onKeyRelease will be executed
        //and if you type anything it will be recognized because of this event handler.
        htmlSourceCode.setOnKeyReleased(event -> {
//            setHtmlDataForPage();
            page.setPageData(htmlSourceCode.getText());
            //page.setPageData(htmlDataForPageProperty().getValue());
        });
        if(page.getPageType()==ConstantVariables.LOGIN_PAGE_TYPE){

        }
        return text;
    }


    public String setText(String text){

        htmlSourceCode.replaceText(0,htmlSourceCode.getText().length(),text);
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
     * @param file
     * @return
     */
    public String readFile(InputStream file){
        //String nextPageAnswers = createNextPageAnswersDialog(false);
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(file));
            String text;
            while ((text = bufferedReader.readLine()) != null) {
                //if(text.contains("NextPageName")) text = "NextPageName=\""+page.getConnectedTo() +"\";";
                stringBuffer.append(text);
                stringBuffer.append("\n");
            }
        } catch (FileNotFoundException ex) {
            logger.error("{HTML Editor Content}", ex);
            ex.printStackTrace();
            System.out.println("{HTML Editor Content}"+ ex);
        } catch (IOException ex) {
            logger.error("{HTML Editor Content}", ex);
            ex.printStackTrace();
            System.out.println("{HTML Editor Content}"+ ex);
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException exp) {
                logger.error("{HTML Editor Content Buffered}", exp);
                exp.printStackTrace();
                System.out.println("{HTML Editor Content}"+ exp);
            }
        }

        return stringBuffer.toString();
    }


    public void addVideo() {
        boolean scriptWasHidden =  false;
        if(Main.getVignette().getController().getScriptIsHidden()){
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        GridPaneHelper helper = new GridPaneHelper();
        helper.addLabel("Video Link:", 1, 1);
        TextField text = helper.addTextField(2, 1, 400, 400);
        String[] videoOptions = {ConstantVariables.VIMEO_VIDEO_OPTION, ConstantVariables.YOUTUBE_VIDEO_OPTION};

        ComboBox video  = helper.addDropDown(videoOptions,0,1);
        video.setValue(ConstantVariables.VIMEO_VIDEO_OPTION);
        boolean isSaved = helper.createGrid("Video Link", null, "ok", "Cancel");
        if (isSaved) {
            //-----------adding the script to the HTML page-----------
            String videoType = video.getValue().toString();
            String videoScript = "//VideoSettings([\\S\\s]*?)//VideoSettings";;
            Pattern videoPattern = Pattern.compile(videoScript);
            String getText = htmlSourceCode.getText();
            Matcher videoMatcher = videoPattern.matcher(getText);
            if(videoMatcher.find()){
//                var vimeoVideoSource = "https://player.vimeo.com/video/554566606"
//                var youtubeVideoID = "Tn6-PIqc4UM";s
                String vimeoVideoSource =  "var vimeoVideoSource = \"(.*?)\";\n";
                String youtubeVideoID =  "var youtubeVideoID = \"(.*?)\";\n";
                if(ConstantVariables.VIMEO_VIDEO_OPTION.equalsIgnoreCase(videoType)){
                    Pattern vimeoPattern = Pattern.compile(vimeoVideoSource);
                    Matcher vimeoMatcher = vimeoPattern.matcher(videoMatcher.group(0));
                    if(vimeoMatcher.find()){
                        String videoText = text.getText().trim();
                        String videoID;
                        if(videoText.trim().startsWith("https://"))
                            videoID = videoText.split("/")[videoText.split("/").length-1];
                        else
                            videoID = videoText;
                        videoID = videoID.replaceAll("&.*$", "");
                        String videoURL = "\tvar vimeoVideoSource = \"https://player.vimeo.com/video/"+videoID+"\";\n";
                        htmlSourceCode.selectRange(videoMatcher.start() + vimeoMatcher.end() - vimeoMatcher.group(0).length(), videoMatcher.start() + vimeoMatcher.end());
                        htmlSourceCode.replaceSelection(videoURL);
                        Matcher playerChoice  = Pattern.compile(BranchingConstants.PLAYER_CHOICE_TARGET).matcher(htmlSourceCode.getText());
                        if(playerChoice.find()){
                            htmlSourceCode.selectRange(playerChoice.start(), playerChoice.end());
                            htmlSourceCode.replaceSelection(BranchingConstants.PLAYER_CHOICE +" = 0; ");
                        }
                    }

                }else if(ConstantVariables.YOUTUBE_VIDEO_OPTION.equalsIgnoreCase(videoType)){
                    Pattern youtubePattern = Pattern.compile(youtubeVideoID);
                    Matcher youtubeMatcher = youtubePattern.matcher(videoMatcher.group(0));
                    if(youtubeMatcher.find()){
                        String videoText = text.getText().trim();
                        String videoID;
                        if(videoText.trim().startsWith("https://"))
                            videoID = videoText.split("=")[1];
                         else
                            videoID = videoText;
                        videoID = videoID.replaceAll("&.*$", "");
                        String videoURL = "\tvar youtubeVideoID = \""+videoID+"\";\n";
                        htmlSourceCode.selectRange(videoMatcher.start() + youtubeMatcher.end() - youtubeMatcher.group(0).length(), videoMatcher.start() + youtubeMatcher.end());
                        htmlSourceCode.replaceSelection(videoURL);
                        Matcher playerChoice  = Pattern.compile(BranchingConstants.PLAYER_CHOICE_TARGET).matcher(htmlSourceCode.getText());
                        if(playerChoice.find()){
                            htmlSourceCode.selectRange(playerChoice.start(), playerChoice.end());
                            htmlSourceCode.replaceSelection(BranchingConstants.PLAYER_CHOICE +" = 1; ");
                        }
                    }
                }
            }else{
                System.out.println("NO VIDEO SETTING FOUND");
            }
            page.setPageData(htmlSourceCode.getText());
        }
        if(scriptWasHidden)
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
        File f;
        if(imageSaved.length>0)
            f = new File(imageSaved[0]);
        else
            f = new File(getImageToDisplay());
        try {
            BufferedImage bimg = ImageIO.read(f);
            return SwingFXUtils.toFXImage(bimg, null);
        }
        catch( IOException e ) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    /**
     * Identify multiple file uploads and add them as an image tag to the source HTMl code
     * @return
     */
    public Images addImageTag(){
        boolean scriptWasHidden = false;
        if(Main.getVignette().getController().getScriptIsHidden()){
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        GridPaneHelper helper = new GridPaneHelper();
        helper.setPrefSize(500,500);
        helper.setResizable(true);
        //creating Click to add Image button
        // and adding to an hBox so that its centered on the gridPane-----------------
        String htmlText = htmlSourceCode.getText();
        Button addImage = new Button("Click to add Image");
        Image addImageIcon = null;
        if(Main.getVignette().getFolderPath()!=null || !("".equalsIgnoreCase(Main.getVignette().getFolderPath()))){
            String imagePatter = ".*<img class=\"(.*?)\" style='(.*?)' src=\"(.*?)\" alt=\"(.*?)\">\n";
            Pattern pattern = Pattern.compile(imagePatter);
            String tempData = page.getPageData();
            Matcher matcher = pattern.matcher(tempData);
            if(matcher.find()){
                String imageSaved = Main.getVignette().getFolderPath()+"/" + matcher.group(3).trim();
                System.out.println("Image Saved: "+imageSaved);
                addImageIcon = readImage(imageSaved);
            }
//            if(addImageIcon==null && "".equalsIgnoreCase(getImageToDisplay()))
//                addImageIcon = new Image("/images/insertImage.png");
        }else if(getImageToDisplay()==null || "".equalsIgnoreCase(getImageToDisplay()))
            addImageIcon = new Image("/images/insertImage.png");
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
        //adding the hBox to the gridPane
        helper.add(hBox,0,0,2,1);
        final String[] fileName = {null};
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images()", "*.JPG","*.PNG","*.JPEG","*.GIF");
                filterList.add(extFilterJPG);
                FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
                File file = fileHelper.openFileChooser(filterList);
                if(file !=null){
                    fileName[0] = file.getName();
                    try {
                        setImageToDisplay(file.getAbsolutePath());
                        image = ImageIO.read(file);
                        Main.getVignette().getImagesList().add(new Images(fileName[0], image));
                        //Once the image is uploaded, change the button graphic------
                        Image img = SwingFXUtils.toFXImage(image, null);
                        ImageView img1 = new ImageView(img);
                        img1.setPreserveRatio(true);
                        img1.setFitHeight(400);
                        img1.setFitWidth(400);
                        addImage.setGraphic(img1);
                        //------------------------------------------------------------
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        addImage.setOnAction(eventHandler);
        //Adding labels and textfields to gridpane-------------------------------------
        helper.add(new Label("Width of Image"),0,3,1,1);
        TextField widthofImage = new TextField();
        helper.add(widthofImage,1,3,1,1);
        widthofImage.setText("50");
        helper.add(new Label("Image Class Name"),0,4,1,1);
        TextField className = new TextField();
        helper.add(className,1,4,1,1);
        className.setText("img-fluid");
        //------------------------------------------------------------------------------
        boolean clicked = helper.createGridWithoutScrollPane("Image",null,"Ok","Cancel");
        boolean isValid = false;
        if(clicked) {
            System.out.println(fileName[0]);
            isValid = fileName.length>0 && fileName[0] != null;
            while (!isValid){
                String message =fileName.length>0 && fileName[0] == null? "File Name Cannot be empty":"";
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                        message,false);
                if(dialogHelper.getOk()) {clicked = helper.showDialog(); }
                isValid = fileName[0] != null;
                if(!clicked) break;
            }
            int field;
            field = htmlSourceCode.getCaretPosition();
            System.out.println(field);
            String imageText ="<img class=\""+className.getText()+"\" style='width:"+widthofImage.getText()+"%;' src=\""+ConstantVariables.imageResourceFolder+fileName[0]+"\" alt=\"IMG_DESCRIPTION\">\n";
//            String imagePatter = ".*<img(.*?)>\n";

            // This replaces the image tag on the page in a javafx undo/redoable manner
            String imagePatter = ".*<img(.*?)>\n";
            Pattern pattern = Pattern.compile(imagePatter);
            Matcher matcher = pattern.matcher(htmlText);
            if(matcher.find()){
                System.out.println(matcher.group(0));
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(imageText);
                //saving manipulated page data
                page.setPageData(htmlSourceCode.getText());
                Main.getVignette().getPageViewList().put(page.getPageName(),page);
            }
            else {
                System.out.println("IMAGE TAG NOT FOUND");
            }
        }else{
            System.out.println("NO IMAGE SELECTED");
        }
        if(scriptWasHidden)
            Main.getVignette().getController().hideScript();
        Images images = new Images(fileName[0],image);
        return images;
    }

    public String addProblemStatmentToQuestion(){
        boolean scriptWasHidden = false;
        if(Main.getVignette().getController().getScriptIsHidden()){
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }

        GridPaneHelper helper = new GridPaneHelper();
        ComboBox problemStatementBox = null;
        List<String> psOptions = new ArrayList<>();
        pageNameList.stream().forEach(pageForType->{
            VignettePage p = Main.getVignette().getController().getPageViewList().get(pageForType);
            if(p.getPageType().equalsIgnoreCase(ConstantVariables.PROBLEMSTATEMENT_PAGE_TYPE)){
                psOptions.add(pageForType);
            }
        });
        if(psOptions.size()>0){
            Object[] obj = psOptions.toArray();
            String[] psArray = Arrays.copyOf(obj, obj.length, String[].class);
            problemStatementBox = helper.addDropDown(psArray, 0,1);
            Boolean clickedOk = helper.createGrid("Next Answer Page ",null, "ok","Cancel");
            if(clickedOk){
                System.out.println("PS SELECTED: "+problemStatementBox.getSelectionModel().getSelectedItem().toString());
                Pattern branchPattern = Pattern.compile("//problemStatement\n(.*?)//problemStatement\n", Pattern.DOTALL);
                Matcher matcher;
                String htmlCodeInString = htmlSourceCode.getText();
                matcher = branchPattern.matcher(htmlCodeInString);
                //If there already is a branching question, find it and replace it in an undoable manner
                if (matcher.find()) {
                    String problemStatement="";
                    problemStatement+="//problemStatement\n";
                    String psPage = problemStatementBox.getSelectionModel().getSelectedItem().toString();
                    problemStatement += BranchingConstants.PROBLEM_STATEMENT+" = '"+psPage+"'\n";
                    problemStatement+="//problemStatement\n";
                    htmlCodeInString = htmlCodeInString.replaceAll(matcher.group(0), problemStatement);


                   // htmlSourceCode.setText(htmlCodeInString);
                    htmlSourceCode.replaceText(0,htmlSourceCode.getText().length(),htmlCodeInString);


                    page.setProblemStatementPage(psPage);
                }
                // otherwise insert it at the user provided position
                else {
                    System.out.println("No Problem Statement regex found");
                }
            }
        }else{
            System.out.println("NO PS OPTIONS");
        }
        if(scriptWasHidden){
            Main.getVignette().getController().hideScript();
        }
        return "";
    }
    /**
     *
     * @param editNextPageAnswers
     * @param noquestionSelected
     * @return
     */
    public String createNextPageAnswersDialog(Boolean editNextPageAnswers, Boolean noquestionSelected){
        GridPaneHelper helper = new GridPaneHelper();
        String answerNextPage = "{";
        ComboBox defaultNextPageBox = null;

        page.clearNextPagesList();
        if(branchingType.getValue().equals(BranchingConstants.SIMPLE_BRANCH)){
            helper.addLabel("Default Next Page", 0,0);
            if(optionEntries.size()>0)
                defaultNextPageBox = helper.addDropDownWithDefaultSelection(pageNameList.stream().toArray(String[]::new), 0,1, optionEntries.get("default"));
            else
                defaultNextPageBox = helper.addDropDown(pageNameList.stream().toArray(String[]::new), 0,1);
        }
        else {
            int size = editNextPageAnswers ? answerChoice.size() :
                    numberOfAnswerChoiceValue.getValue() == null ? 0 : Integer.parseInt(numberOfAnswerChoiceValue.getValue());
            if(branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)){
                for (int i = 0; i < size; i++) {
                    addNextPageTextFieldToGridPane(this.countOfAnswer++, helper, editNextPageAnswers, true);
                }
                defaultTextFieldIndex = this.countOfAnswer+1;
                addDefaultToNextPageGridPane(helper);
                defaultTextFieldAdded = true;
            }else if(branchingType.getValue().equals(BranchingConstants.RADIO_QUESTION)){
                for (int i = 0; i < size; i++) {
                    addNextPageTextFieldToGridPane(this.countOfAnswer++, helper, editNextPageAnswers, false);
                }
            }

//            for (int i = 0; i < size; i++) {
//                addNextPageTextFieldToGridPane(this.countOfAnswer++, helper, editNextPageAnswers, false);
//            }
//            if(branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)){
//                    addDefaultToNextPageGridPane(helper);
//                addNextPageTextFieldToGridPane(-1,helper, editNextPageAnswers, true);
//                Separator s = new Separator();
//                s.setMaxWidth(helper.widthProperty().get());
//                helper.add(s,countOfAnswer, countOfAnswer);
//                helper.add(s,countOfAnswer, countOfAnswer);
//                helper.setHgrow(s, Priority.ALWAYS);
//                addNextPageTextFieldToGridPane(Integer.MAX_VALUE,helper, editNextPageAnswers, true);
//            }

        }
        Boolean clickedOk = helper.createGrid("Next Answer Page ",null, "ok","Cancel");
        if(clickedOk){
            if(branchingType.getValue().equals(BranchingConstants.SIMPLE_BRANCH)){
                defaultNextPage = (String) defaultNextPageBox.getSelectionModel().getSelectedItem();
                if(!defaultNextPage.equalsIgnoreCase(page.getPageName())){
                    VignettePage pageTwo = Main.getVignette().getPageViewList().get(defaultNextPage);
                    if(connectPages(pageTwo, "default")){
                        System.out.println("APPROVED CHECK PAGE CONNECTION");
                        TabPaneController paneController = Main.getVignette().getController();
                        System.out.println(page.getPagesConnectedTo());
                        paneController.makeFinalConnection(page);
                        updateOptionEntries();
                        return "{'default':'"+defaultNextPage+"'}";
                    }
                    return "{'default':'general'}";
                }else{
                    DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR,"Cannot Connect Pages",
                            null,"Pages May not connect to itself", false);
                }
            }
            AtomicBoolean selfConnection = new AtomicBoolean(false);
            answerPage.stream().forEach(e->{
                if(e.getValue().toString().equalsIgnoreCase(page.getPageName())){
                    DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR,"Cannot Connect Pages",
                            null,"Pages May not connect to itself", false);
                    selfConnection.set(true);
                    return;
                }
            });
            if(!selfConnection.get()){
                if(branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)){
                    if(answerChoice.get(answerChoice.size()-1).getText().equals(BranchingConstants.CHECKBOX_CORRECT_OPTION)){
                        DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR,"Invalid Correct Answer",
                                null,"CheckBox question should have a correct answer choice", false);
                        answerChoice.clear();
                        answerPage.clear();
                        this.countOfAnswer = 0;
                        this.defaultTextFieldIndex = -1;
                        return "";
                    }
                }

                for(int i =0;i<answerChoice.size();i++){
                    if(!answerChoice.get(i).getText().equals("")){
                        if(!answerPage.get(i).getValue().toString().equalsIgnoreCase(page.getPageName())){
                            VignettePage pageTwo = Main.getVignette().getPageViewList().get(answerPage.get(i).getValue().toString());
                            if(connectPages(pageTwo, answerChoice.get(i).getText()))
                                answerNextPage += " "+"'"+answerChoice.get(i).getText()+"'"+ ":" + "'"+answerPage.get(i).getValue()+"'" +",";
                        }
                    }
                }
            }else{
                answerChoice.clear();
                answerPage.clear();
                this.countOfAnswer = 0;
                this.defaultTextFieldIndex = -1;
                return "";
            }
            HashMap<String, String> pageConnectionList = page.getPagesConnectedTo();
            if(branchingType.getValue().equals(BranchingConstants.RADIO_QUESTION)) {
                defaultNextPage = (String) answerPage.get(0).getValue();
                answerNextPage+=" 'default': '"+ defaultNextPage+"' ,";
                if(pageConnectionList.containsKey(defaultNextPage)){
                    page.addPageToConnectedTo(defaultNextPage, pageConnectionList.get(defaultNextPage)+", default");
                }else{
                    page.addPageToConnectedTo(defaultNextPage, "default");
                }
                VignettePage pageTwo = Main.getVignette().getPageViewList().get(defaultNextPage);
            }
            if(branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)) {
                int size = answerPage.size();
                defaultNextPage = (String) answerPage.get(size-1).getValue();
            }
            page.setQuestionType(branchingType.getValue());
            //pane object to highlight all the connection visually on UI
            TabPaneController pane = Main.getVignette().getController();
            pane.makeFinalConnection(page);
            updateOptionEntries();
            answerNextPage = answerNextPage.replaceAll(",$", "");
            answerNextPage+="}";
            this.editConnectionString = answerNextPage;
        }
        answerChoice.clear();
        answerPage.clear();
        this.countOfAnswer = 0;
        this.defaultTextFieldIndex = -1;
        if(answerNextPage.equalsIgnoreCase("{"))
            return "{}";
        else
            return answerNextPage;
    }

    /**
     *
     * @param helper
     * @return
     */
    public EventHandler addToGridPane(GridPaneHelper helper, boolean addeDefault){
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                if(defaultTextFieldAdded){
                    helper.getGrid().getChildren().removeAll(helper,answerChoice.get(answerChoice.size()-1),answerPage.get(answerPage.size()-1));
                    defaultTextFieldAdded  = false;
                }
               addNextPageTextFieldToGridPane(countOfAnswer++, helper, false, addeDefault);
               if(addeDefault)
                   addDefaultToNextPageGridPane(helper);
            }
        };
        return eventHandler;
    }
    public EventHandler removeFromGridPane(boolean addDefault, GridPaneHelper helper, TextField text,ComboBox dropdown,Button add,Button remove){

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                if(defaultTextFieldAdded){
                    helper.getGrid().getChildren().removeAll(helper,answerChoice.get(answerChoice.size()-1),answerPage.get(answerPage.size()-1));
                    defaultTextFieldAdded  = false;
                }
              helper.getGrid().getChildren().removeAll(text,dropdown,add,remove);
              defaultTextFieldIndex = countOfAnswer--;
                if(addDefault)
                    addDefaultToNextPageGridPane(helper);
            }
        };
        return eventHandler;
    }
    public int defaultTextFieldIndex = -1;
    public boolean defaultTextFieldAdded =  false;
    public void addDefaultToNextPageGridPane(GridPaneHelper helper){

//        if(defaultTextFieldAdded){
//            System.out.println("REMOVING!!");
//            helper.getGrid().getChildren().removeAll(helper,answerChoice.get(this.countOfAnswer),answerPage.get(this.countOfAnswer));
//            defaultTextFieldAdded  = false;
//        }
        TextField text = helper.addTextField(0, defaultTextFieldIndex);
        text.setText(BranchingConstants.CHECKBOX_CORRECT_OPTION);
        text.focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue()) {
                    focusGained(text);
                } else {
                    focusLost(text);
                }
            }

            private void focusGained(TextField text){
                text.setText("");
            }

            private void focusLost(TextField text){
                if("".equalsIgnoreCase(text.getText()))
                    text.setText(BranchingConstants.CHECKBOX_CORRECT_OPTION);
            }
        });


        String[] pageList = pageNameList.toArray(new String[0]);
        ComboBox dropdown = helper.addDropDown(pageList, 1, defaultTextFieldIndex);
        if(optionEntries.size()>0)
            dropdown.setValue(optionEntries.get("default"));
        answerChoice.add(text);
        answerPage.add(dropdown);
        defaultTextFieldIndex+=1;
        defaultTextFieldAdded = true;
    }
    /**
     * this function is used to support the '+' functionality to add pages textField in the next answer page dialog box
     * @param index
     * @param helper
     * @param editNextPageAnswers
     * @param addDefault
     */

    public void addNextPageTextFieldToGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers, Boolean addDefault){
        char answerAlphabet = '-';
        if(index>=0){
            answerAlphabet = ((char) (65+index));
        }else{
            index = 0;
        }
        if(!editNextPageAnswers) {
            TextField text = helper.addTextField(0, index);
            text.setText(""+answerAlphabet);
            String[] pageList = pageNameList.toArray(new String[0]);
            ComboBox dropdown = helper.addDropDown(pageList, 1, index);
            if(optionEntries.size()>0)
                dropdown.setValue(optionEntries.get(answerAlphabet+""));
            Button add= helper.addButton("+", 2, index, addToGridPane(helper, addDefault));
            Button remove =  helper.addButton("-", 3, index);
            remove.setOnAction(removeFromGridPane(addDefault, helper,text,dropdown,add,remove));
            answerChoice.add(text);
            answerPage.add(dropdown);
        }
        else {
           helper.addExistingTextField(answerChoice.get(index),0,index);
           helper.addExistingDropDownField(answerPage.get(index),1,index);
           Button add = helper.addButton("+", 2, index, addToGridPane(helper,addDefault));
           Button remove =  helper.addButton("-", 3, index);
           remove.setOnAction(removeFromGridPane(addDefault, helper,answerChoice.get(countOfAnswer),answerPage.get(countOfAnswer),add,remove));
        }
    }
    public void editNextPageAnswers(String noBranchingSelected){
        boolean scriptWasHidden = false;
        if(!page.getPageType().equals(BranchingConstants.SIMPLE_BRANCH)){
            if(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()>0)
                numberOfAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()+"");
//            else
//                System.out.println("USER IS TRYING TO INIT CONNECTIONS FIRST!! ");
        }
        if(Main.getVignette().getController().getScriptIsHidden()){
            scriptWasHidden = true;
            Main.getVignette().getController().showScript();
        }
        String htmlText ="";
        String nextPageAnswers = "";
        nextPageAnswers = createNextPageAnswersDialog(false, false);

        if(!"".equalsIgnoreCase(nextPageAnswers)){
            Utility utility = new Utility();
            String questionType = BranchingConstants.QUESTION_TYPE+"= '" + utility.checkPageType(branchingType.getValue()) + "';";
            htmlText = htmlSourceCode.getText();
            Pattern p = Pattern.compile(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET);
            Matcher m  = p.matcher(htmlText);

            if(m.find()){
                htmlText = !nextPageAnswers.equals("{}") ?
                        htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET, BranchingConstants.NEXT_PAGE_ANSWER+"="
                                + nextPageAnswers + ";") :
                        htmlText;
            }else{
                System.out.println("NOT FOUND!!!!!!");
            }

            String questionTypeText = "";
            if( htmlText.contains(BranchingConstants.QUESTION_TYPE)){
                htmlText = htmlText.replaceFirst(BranchingConstants.QUESTION_TYPE_TARGET, questionType);
                page.setQuestionType(branchingType.getValue());
                htmlText = htmlText.replaceFirst(BranchingConstants.QUESTION_TYPE, questionTypeText);
            } else{
                questionTypeText+=questionType+"\n";
            }

            htmlText = htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_NAME_TARGET, questionTypeText+
                    BranchingConstants.NEXT_PAGE_NAME +"='"+
                    defaultNextPage+"';");


            //htmlSourceCode.setText(htmlText);
            htmlSourceCode.replaceText(0,htmlSourceCode.getText().length(),htmlText);

            page.setPageData(htmlSourceCode.getText());
            Main.getVignette().getPageViewList().put(page.getPageName(), page);
        }
        if(scriptWasHidden){
            Main.getVignette().getController().hideScript();
        }
    }


        /**
         * page setting button pane that appears on left toolbox
         */
    public void editPageSettings(){
        GridPaneHelper helper = new GridPaneHelper();

        //------------------------------------ HTML button CSS ---------------------------------------------------------
        String buttonStyle= "-fx-text-align: center;"+ "-fx-background-color: transparent;" + "-fx-font-size: 25px;" +
                "-fx-border-radius: 7;" + "-fx-border-width: 3 3 3 3;";
        String pageColor= "-fx-text-fill:  #007bff;" + "-fx-color: #007bff;" + "-fx-border-color: #007bff;";
        String optionsColor= "-fx-text-fill:  #6c757d;" + "-fx-color: #6c757d;" + "-fx-border-color: #6c757d;";
        String probStatementColor= "-fx-text-fill:  #17a2b8;" + "-fx-color: #17a2b8;" + "-fx-border-color: #17a2b8;";
        String opacityCSS= "-fx-opacity:";
        //--------------------------------------------------------------------------------------------------------------
        boolean scriptWasHidden = false;
        if(Main.getVignette().getController().getScriptIsHidden()){
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
        if(m.find()){
            String[] defaultSettings = m.group(1).trim().split("\n");
            for(String s:defaultSettings){
                Pattern defaultSettingPattern  = Pattern.compile("^settings\\(\"([\\S\\s]*?)\",([\\S\\s]*?),([\\S\\s]*?)\\);$");
                s = s.trim();
                Matcher extractTags = defaultSettingPattern.matcher(s);
                if(extractTags.find()){
                    String type = extractTags.group(1).trim();
                    checkboxDisabled.put(type, Boolean.parseBoolean(extractTags.group(2).trim()));
                    opacityForButtons.put(type, Double.parseDouble(extractTags.group(3).trim()));
                }else{
                    System.out.println("NOT FOUND!");
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //------------------------------------- EDIT OPTIONS -----------------------------------------------------------
        helper.addLabel("Options: ",1,1);
        CheckBox disabledOptions = helper.addCheckBox("Disable",2,1,true);
        disabledOptions.setSelected(checkboxDisabled.get(ConstantVariables.OPTION_PAGE_SETTING));
        helper.addLabel("Opacity",3,1);
        Spinner optionsSpinner = new Spinner(0.0,1.0,opacityForButtons.get(ConstantVariables.OPTION_PAGE_SETTING),0.1);


        //dealing with disabling the options setting
        if(checkboxDisabled.get(ConstantVariables.OPTION_PAGE_SETTING))
            optionsSpinner.setDisable(true);
        else
            optionsSpinner.setDisable(false);

        disabledOptions.setOnAction(event -> {
            if(disabledOptions.isSelected())
            {
                optionsSpinner.setDisable(true);
                optionsSpinner.getValueFactory().setValue(0.1);
            }
            else
                optionsSpinner.setDisable(false);
        });


        helper.addSpinner(optionsSpinner,4,1);
        AtomicReference<Double> optionsOpacity = new AtomicReference<>((double) 1);
        Button optionsButton = new Button("Options");

        optionsButton.setStyle(buttonStyle+optionsColor+opacityCSS+opacityForButtons.get(ConstantVariables.OPTION_PAGE_SETTING)+";");

        optionsSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            optionsOpacity.set((Double) newValue);
            optionsButton.setStyle(buttonStyle+optionsColor+opacityCSS+optionsOpacity+";");
        });
        helper.addButton(optionsButton,5,1);
        //-------------------------------------------------------------------------------------------------------------

        //-------------------------------- EDIT PROBLEM STATEMENT ------------------------------------------------------
        helper.addLabel("Problem Statement: ",1,2);
        CheckBox disabledProblemStatement = helper.addCheckBox("Disable",2,2,true);
        disabledProblemStatement.setSelected(checkboxDisabled.get(ConstantVariables.PROBLEM_PAGE_SETTING));
        helper.addLabel("Opacity",3,2);
        Spinner problemStatementSpinner = new Spinner(0.0,1.0,opacityForButtons.get(ConstantVariables.PROBLEM_PAGE_SETTING),0.1);


        //dealing with disabling the problems statement settings
        if(checkboxDisabled.get(ConstantVariables.PROBLEM_PAGE_SETTING))
            problemStatementSpinner.setDisable(true);
        else
            problemStatementSpinner.setDisable(false);

        disabledProblemStatement.setOnAction(event -> {
            if(disabledProblemStatement.isSelected())
            {
                problemStatementSpinner.setDisable(true);
                problemStatementSpinner.getValueFactory().setValue(0.1);
            }
            else
                problemStatementSpinner.setDisable(false);
        });

        helper.addSpinner(problemStatementSpinner,4,2);


        AtomicReference<Double> probOpacity = new AtomicReference<>((double) 1);
        Button probButton = new Button("Show Problem Statement");

        probButton.setStyle(buttonStyle+probStatementColor+opacityCSS+opacityForButtons.get(ConstantVariables.PROBLEM_PAGE_SETTING)+";");

        problemStatementSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            probOpacity.set((Double) newValue);
            probButton.setStyle(buttonStyle+probStatementColor+opacityCSS+probOpacity+";");
        });
        helper.addButton(probButton,5,2);
        //-------------------------------------------------------------------------------------------------------------

        //----------------------------------- EDIT PREV PAGE -----------------------------------------------------------

        helper.addLabel("Prev Page: ",1,3);
        CheckBox disabledPrevPage = helper.addCheckBox("Disable",2,3,true);
        disabledPrevPage.setSelected(checkboxDisabled.get(ConstantVariables.PREV_PAGE_PAGE_SETTING));
        helper.addLabel("Opacity",3,3);
        Spinner prevPageSpinner = new Spinner(0.0,1.0,opacityForButtons.get(ConstantVariables.PREV_PAGE_PAGE_SETTING),0.1);



        //dealing with disabling the previous page settings
        if(checkboxDisabled.get(ConstantVariables.PREV_PAGE_PAGE_SETTING))
            prevPageSpinner.setDisable(true);
        else
            prevPageSpinner.setDisable(false);

        disabledPrevPage.setOnAction(event -> {
            if(disabledPrevPage.isSelected())
            {
                prevPageSpinner.setDisable(true);
                prevPageSpinner.getValueFactory().setValue(0.1);
            }
            else
                prevPageSpinner.setDisable(false);
        });


        helper.addSpinner(prevPageSpinner,4,3);
        AtomicReference<Double> prevPageOpacity = new AtomicReference<>((double) 1);
        Button prevPageButton = new Button("Back to Previous Page");

        prevPageButton.setStyle(buttonStyle+pageColor+opacityCSS+opacityForButtons.get(ConstantVariables.PREV_PAGE_PAGE_SETTING)+";");

        prevPageSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            prevPageOpacity.set((Double) newValue);
            prevPageButton.setStyle(buttonStyle+pageColor+opacityCSS+prevPageOpacity+";");
        });
        helper.addButton(prevPageButton,5,3);
        //-------------------------------------------------------------------------------------------------------------


        //----------------------------------- EDIT NEXT PAGE -----------------------------------------------------------

        helper.addLabel("Next Page: ",1,4);
        CheckBox disabledNextPage = helper.addCheckBox("Disable",2,4,true);
        disabledNextPage.setSelected(checkboxDisabled.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING));
        helper.addLabel("Opacity",3,4);
        Spinner nextPageSpinner = new Spinner(0.0,1.0,opacityForButtons.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING),0.1);


        //dealing with disabling the nect page settings
        if(checkboxDisabled.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING))
            nextPageSpinner.setDisable(true);
        else
            nextPageSpinner.setDisable(false);
        disabledNextPage.setOnAction(event -> {
            if(disabledNextPage.isSelected())
            {
                nextPageSpinner.setDisable(true);
                nextPageSpinner.getValueFactory().setValue(0.1);
            }
            else
                nextPageSpinner.setDisable(false);
        });

        helper.addSpinner(nextPageSpinner,4,4);
        AtomicReference<Double> nextPageOpacity = new AtomicReference<>((double) 1);
        Button nextPageButton = new Button("Continue to Next Page");

        nextPageButton.setStyle(buttonStyle+pageColor+opacityCSS+opacityForButtons.get(ConstantVariables.NEXT_PAGE_PAGE_SETTING)+";");

        nextPageSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            nextPageOpacity.set((Double) newValue);
            nextPageButton.setStyle(buttonStyle+pageColor+opacityCSS+nextPageOpacity+";");
        });
        helper.addButton(nextPageButton,5,4);
        //-------------------------------------------------------------------------------------------------------------



        boolean clickedOk = helper.createGrid("Page Settings", null,"Ok","Cancel");
        if(clickedOk) {
            Pattern pattern = Pattern.compile(target);
            Matcher matcher = pattern.matcher(htmlText);

            if(matcher.find())
            {
                System.out.println("found");
                String tag1 = "//Settings";
                String options = "\tsettings(\"options\", "+disabledOptions.isSelected()+", "+optionsSpinner.getValue()+");";
                String problemStatement = "\tsettings(\"problemStatement\", "+disabledProblemStatement.isSelected()+", "+problemStatementSpinner.getValue()+");";
                String prevPage = "\tsettings(\"PrevPage\", "+disabledPrevPage.isSelected()+", "+prevPageSpinner.getValue()+");";
                String nextPage = "\tsettings(\"NextPage\", "+disabledNextPage.isSelected()+", "+nextPageSpinner.getValue()+");";
                String tag2 = "//settings";
                String settings = tag1+'\n'+options+'\n'+problemStatement+'\n'+prevPage+'\n'+nextPage+'\n'+tag2;
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(settings);
                //saving page data
                page.setPageData(htmlSourceCode.getText());
                Main.getVignette().getPageViewList().put(page.getPageName(),page);
            }
            else
                System.out.println("Page Settings not found");
        }
        if(scriptWasHidden){
            Main.getVignette().getController().hideScript();
        }
    }

 //------------------------------ Adding Input Fields -----------------------

    /**
     * world to add an input field
     * @param isImageField
     */
    public void addInputFields(boolean isImageField){
        int field;
        field = htmlSourceCode.getCaretPosition();
//        GridPaneHelper helper1 = new GridPaneHelper();

        if(field<=0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please make sure the cursor in on the HTML editor!");
            alert.setTitle("Message");
            alert.show();
        }else {
            //createInputField(field, isImageField);
            GridPaneHelper helper1 = new GridPaneHelper();
            helper1.setResizable(false);
            //Buttons now manually created and added to allow them to be customizable.
            Button buttonNonBranch = new Button("Non Branching");
            buttonNonBranch.setPrefSize(1000,60);
            buttonNonBranch.setOnAction(event -> {
                //for sum reason hiding the dialog box makes the screen unclickable
                // helper1.hideDialog();
                inputTypeFieldBranching = false;
                setInputType(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN);
                createInputField(field,isImageField,false);
                helper1.closeDialog();
            });

            helper1.addButton(buttonNonBranch,0,0);

            Button buttonAddBranching = new Button("Branching");
            buttonAddBranching.setPrefSize(1000,60);
            buttonAddBranching.setOnAction(event->{
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
            helper1.setPrefSize(300,100);

            //Display
            boolean create = helper1.create("Choose type of Input Field","");
        }
    }
    public boolean getHasBranching() {return hasBranchingQuestion;}
    public void setHasBranchingQuestion(boolean value){this.hasBranchingQuestion = value;}

    public void manageTextFieldsForInputFieldHelper(GridPaneHelper helper, int field, boolean isImageField, boolean isBranched){

        if(getInputType().equalsIgnoreCase(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN) || getInputType().equalsIgnoreCase(ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN)){
            helper.addLabel("Answer Key:",0,3);
            helper.addLabel("Input Value:",1,3);
            //------------------------------------------------------------------------

            int listSize=0;
            if(isBranched && numberOfAnswerChoiceValue!=null)
                listSize = Integer.parseInt(numberOfAnswerChoiceValue.getValue());
            else if(isBranched)
                listSize = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size();
            int size = listSize==0 ? 4 : listSize;
            //inputFieldsListNonBranching.clear();
            inputFieldsListBranching.clear();
            if(listSize >0){
                for (int i = 1; i <= listSize; i++) {
                    //addInputFieldsToGridPane(i, helper, true, isImageField);
                    addInputFieldsToGridPane(i,helper,true, isImageField,isBranched, true);
                }
            }
            else {
                for (int i = 1; i <= size; i++) {
                    //addInputFieldsToGridPane(i, helper, false, isImageField);
                    addInputFieldsToGridPane(i,helper,false, isImageField,isBranched, true);
                }
            }
        }else if(getInputType().equalsIgnoreCase(ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN) || getInputType().equalsIgnoreCase(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN)){
            helper.removeAllFromHelper();
            helper.addLabel("Answer Key:",0,4);
            helper.addLabel("Input Value:",1,4);
            addStuffToHelper(helper, field, isImageField, isBranched);
            addInputFieldsToGridPane(1,helper,false,isImageField,isBranched, false);
        }
        helper.setScaleShape(true);
    }
    public  EventHandler selectImageForQuestionText( TextArea questionInput) {
        AtomicReference<BufferedImage> image = new AtomicReference<>();

        return event -> {
            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images", "*.JPG","*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);
            String imageSource ="";
            String imageString = "<img src=" + imageSource + " alt='Question Description' class='text-center' width='300px' height='400px'/>\n";
            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if(file !=null){
                String fileName = file.getName();
                try {
                    image.set(ImageIO.read(file));
                    Images images = new Images(fileName, image.get());
                    this.setImageSourceForQuestion(fileName);
                    Main.getVignette().addToImageList(images);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                System.out.println("PRESSED CANCEL");
            }
        };
    }

    public void addStuffToHelper(GridPaneHelper helper, int field, boolean isImageField, boolean isBranched){
        String[] dropDownListBranching = {ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN, ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN};
        String[] dropDownListNonBranching = {ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN, ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN, ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN};

        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle("-fx-background-color: black;-fx-text-fill: white;-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        helper.addLabel("Question:",0,0);
        helper.addLabel("Input Type:", 2,0);

        TextArea question = helper.addTextArea(0,1);
        Button addImageFile = helper.addButton("Image File for question",1,0);
        Tooltip tooltip1 = new Tooltip();
        tooltip1.setStyle("-fx-font-size: 14");
        tooltip1.setMaxWidth(400);
        tooltip1.setWrapText(true);
        ImageView imageView = new ImageView();
        Image i = new Image(ConstantVariables.ADD_QUESTION_IMAGE);
        imageView.setImage(i);
        tooltip1.setGraphic(imageView);
        tooltip1.setShowDelay(javafx.util.Duration.millis(100));
        System.out.println("page.getVignettePageAnswerFieldsBranching().getQuestion()::: "+page.getVignettePageAnswerFieldsBranching().getQuestion());
        if(page.getVignettePageAnswerFieldsBranching().getQuestion()!=null)
            questionTextProperty().set(page.getVignettePageAnswerFieldsBranching().getQuestion());

        addImageFile.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if(question.getCaretPosition()>0 && question.getText().length()>0){
                    try{
                        String s1 = question.getText().substring(0, question.getCaretPosition());
                        String s2 = question.getText().substring(question.getCaretPosition());
                        tooltip1.setText("Insert Image between: '"+s1+"' and '"+s2+"'?");
                    }catch (Exception ex){
                        System.out.println("SubString error: "+ex.getMessage());
                    }

                }else{
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
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images", "*.JPG","*.PNG", "*.JPEG", "*.GIF");
            filterList.add(extFilterJPG);
            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if(file !=null){
                String fileName = file.getName();
                try {
                    image.set(ImageIO.read(file));
                    Images images = new Images(fileName, image.get());
//                    this.setImageSourceForQuestion(fileName);
                    Main.getVignette().addToImageList(images);
                    System.out.println("Image List: "+ Main.getVignette().getImagesList());
                    fileName = fileName.replaceAll("\\s", "%20");
                    String imageString = " <img src=images/" + fileName + " alt='Question Description' class='text-center' width='300px' height='400px'/> ";
                    question.insertText(question.getCaretPosition(), imageString);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else{
                System.out.println("PRESSED CANCEL");
            }
        });
        question.setPrefColumnCount(30);
        question.setWrapText(true);
        // This prevents the user from selecting textarea and textfield options in branched questions
        ComboBox inputTypeDropDown;
        if(isBranched){
            inputTypeDropDown = helper.addDropDown(dropDownListBranching, 3, 0);
            setInputName("b-"+page.getPageName());
        }
        else{
            inputTypeDropDown = helper.addDropDown(dropDownListNonBranching, 3, 0);
            setInputName("nb"+(page.getNumberOfNonBracnchQ()+1)+"-"+page.getPageName());
        }
        if(branchingType.getValue()!=null){
            System.out.println("branchingType.getValue()::"+branchingType.getValue());
            if(branchingType.getValue().equalsIgnoreCase(BranchingConstants.CHECKBOX_QUESTION))
                inputTypeDropDown.setValue(ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN);
            else if(branchingType.getValue().equalsIgnoreCase(BranchingConstants.RADIO_QUESTION))
                inputTypeDropDown.setValue(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN);
        }
//        inputTypeDropDown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
//
//        });
        helper.addLabel("Input Name:",2,1);
        TextField inputName = helper.addTextField(page.getPageName(), 3,1);

//        InputFields fields = new InputFields();
        inputName.setText(page.getPageName());
        inputName.textProperty().bindBidirectional(getInputName());

        inputName.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                System.out.println("MOUSE EXITED");
                String text = inputName.getText();
                if(isBranched){
                    if(!text.startsWith("b-"))
                        inputName.setText("b-"+text);
                }else{
                    if(!text.startsWith("nb"))
                        inputName.setText("nb"+(page.getNumberOfNonBracnchQ()+1)+"-"+text);
                }
            }
        });
        //-----------------------
        //-----------------------
        question.textProperty().bindBidirectional(questionTextProperty());
        if(this.getInputType()==null){
            if(isBranched)
                setInputType(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN);
            else
                setInputType(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN);
        }
        inputTypeDropDown.setOnAction(event -> {
            this.setInputType((String) inputTypeDropDown.getValue());
            System.out.println("getInputType(): "+getInputType());
            if(((String) inputTypeDropDown.getValue()).equalsIgnoreCase(ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN))
                this.branchingType.set(BranchingConstants.RADIO_QUESTION);
            else if(((String) inputTypeDropDown.getValue()).equalsIgnoreCase(ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN))
                this.branchingType.set(BranchingConstants.CHECKBOX_QUESTION);
            else
                this.branchingType.set(BranchingConstants.SIMPLE_BRANCH);
            manageTextFieldsForInputFieldHelper(helper, field, isImageField, isBranched);
        });

        helper.setScaleShape(true);
    }
    public boolean inputTypeFieldBranching = false;
    public void createInputField(int field, boolean isImageField, boolean isBranched) {
        if(Main.getVignette().getController().getScriptIsHidden())
            Main.getVignette().getController().showScript();

        GridPaneHelper helper = new GridPaneHelper();
        // -----ADDING Question TextArea, InputValue TextField and label
        addStuffToHelper(helper, field, isImageField, isBranched);

        //Keep on adding options
        manageTextFieldsForInputFieldHelper(helper, field, isImageField, isBranched);
        CheckBox isRequired = helper.addCheckBox("isRequired", 1, 2, true);

        Boolean clickedOk = helper.createGrid("Input Field ", null, "ok", "Cancel");
        if (clickedOk) {
            //adding question to the pageList!!!
            addInputFieldToHtmlEditor(isImageField,isBranched, isRequired.isSelected());
            //Creating HTML string for the page questions
            Questions[] questionArray = new Questions[page.getQuestionList().size()];
            for (int i = 0; i < page.getQuestionList().size(); i++){
                questionArray[i] = new Questions(page.getQuestionList().get(i));
            }
            ReadFramework.listFilesForFolder(new File(ReadFramework.getUnzippedFrameWorkDirectory()+"pages/questionStyle/"), Questions.getQuestionStyleFileList());

//            if(!Main.defaultFramework){
//                ReadFramework.listFilesForFolder(new File(ReadFramework.getUnzippedFrameWorkDirectory()+"questionStyle/"), Questions.getQuestionStyleFileList());
//            }else{
//                Questions.getQuestionStyleForDefaultFramework();
//            }
            String questionHTMLTag = Questions.createQuestions(questionArray);
            String htmlCodeInString = htmlSourceCode.getText();
            //Replace existing question
            Pattern branchPatternNewToAddTags = Pattern.compile("<!--pageQuestions-->([\\S\\s]*?)<!--pageQuestions-->", Pattern.CASE_INSENSITIVE);
            Matcher matcher;
            matcher = branchPatternNewToAddTags.matcher(htmlCodeInString);
            if(matcher.find()){
                String comments ="<!--pageQuestions-->";
                if(isBranched){
                    Pattern branchingQuestionPattern = Pattern.compile("<!--BranchQ-->([\\S\\s]*?)<!--BranchQ-->", Pattern.CASE_INSENSITIVE);
                    Matcher findBranchingQuestion = branchingQuestionPattern.matcher(htmlSourceCode.getText());
                    String branchQComments = "<!-- BranchQ-->\n";
                    questionHTMLTag = branchQComments + questionHTMLTag + branchQComments;
                    if(findBranchingQuestion.find()){
                        System.out.println("FOUND AN EXISTING QUESTION!!!");
                        htmlSourceCode.selectRange(findBranchingQuestion.start(), findBranchingQuestion.end());
                        htmlSourceCode.replaceSelection(questionHTMLTag);
                    }else{
                        System.out.println("ThIS IS THE FIRST BRANCHING QUESTION FOR THE PAGE");
                    }
                }
                String addingCommentsToHtmlTag = comments + "\n" + questionHTMLTag +comments;
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(addingCommentsToHtmlTag);
                numberOfAnswerChoiceValue.set(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()+"");
                if(inputTypeProperty.equalsIgnoreCase("radio"))
                    branchingType.set(BranchingConstants.RADIO_QUESTION);
                else if(inputTypeProperty.equalsIgnoreCase("checkbox"))
                    branchingType.set(BranchingConstants.CHECKBOX_QUESTION);
                else
                    branchingType.set(BranchingConstants.SIMPLE_BRANCH);
//                branchingType.set(page.getQuestionType());
//                if(isBranched){
//                    page.setQuestionType();
//                }
                if(!isBranched){
                    page.setNumberOfNonBracnchQ(page.getNumberOfNonBracnchQ()+1);
                }
            }else{
                System.out.println("comments not found");
            }
            //saving changes
            page.setPageData(htmlSourceCode.getText());
            Main.getVignette().getPageViewList().put(page.getPageName(), page);
        }
        helper.getGrid().getChildren().clear();
        helper.removeAllFromHelper();
        helper.clear();
        setInputType("");
        setQuestionText("");
        setInputName("");
        helper.closeDialog();
        inputFieldsListBranching.clear();
        inputFieldsListNonBranching.clear();
    }

    /**
     * add input fields to the HTML content
     * @param index
     * @param helper
     * @param editAnswers
     * @param isImageField
     */
    public void addInputFieldsToGridPane(int index, GridPaneHelper helper, Boolean editAnswers,
                                         Boolean isImageField, boolean isBranched, boolean displayAddRemoveButtons){


        TextField answerField = null;
        InputFields fields = new InputFields();
        Button file = null;
        if(isImageField){
           file = helper.addButton("File",0,index+3,fileChoose(fields));
        }else {
            answerField = helper.addTextField("option choice "+index,0, index + 3);
            answerField.textProperty().bindBidirectional(fields.answerKeyProperty());
//            if(editAnswers){
//                answerField.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index-1).getAnswerKey());
//            }
        }
        // this sets the input type of the question to the page id
//        TextField inputName = helper.addTextField(page.getPageName(), 1,index+2);
//        inputName.textProperty().bindBidirectional(fields.inputNameProperty());


        TextField inputValue;
        inputValue = helper.addTextField(1, index + 3);
        inputValue.textProperty().bindBidirectional(fields.inputValueProperty());

        char c=(char)(index + 65 - 1);
        inputValue.setText(c+"");
//        if (editAnswers) {
//            inputValue.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index - 1).getInputValue());
//        }
        inputValue.setEditable(false);
        fields.setId(index);
        fields.setImageField(isImageField);
        fields.setInputType(getInputType());
        fields.setInputName(getInputName().getValue());
        int size = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size();
        if(isBranched && inputTypeFieldBranching && size>0 && index-1<size){
//             && index-1>size
                if(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index-1)!=null){
                    AnswerField temp = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index-1);
                    inputValue.setText(temp.getInputValue());
                    answerField.setText(temp.getAnswerKey());
                }
        }
        //todo non branching qs cannot use input tags
        int removeIndex;
        if(isBranched){
            inputFieldsListBranching.add(fields);
            removeIndex = inputFieldsListBranching.size();
        }
        else{
            inputFieldsListNonBranching.add(fields);
            removeIndex = inputFieldsListNonBranching.size();
        }

        if(displayAddRemoveButtons){

            // the +, - buttons on the GridPane
            Button add =  helper.addButton("+", 2, index+3, addNewInputFieldToGridPane(helper,isImageField, isBranched));
            Button remove = helper.addButton("-", 3, index+3);

    //       page.setVignettePageAnswerFields(page.getVignettePageAnswerFields().getAnswerFieldList().add());
    //       remove.setOnAction(removeInputFieldFromGridPane(helper,
    //               isImageField, file, answerField, inputName, inputValue,
    //               add, remove, fields, removeIndex, isBranched));

            remove.setOnAction(removeInputFieldFromGridPane(helper,
                    isImageField, file, answerField, inputValue,
                    add, remove, fields, removeIndex, isBranched));
        }
    }

    /**
     *
     * @param helper
     * @param isImageField
     * @return
     */
    public EventHandler addNewInputFieldToGridPane(GridPaneHelper helper, Boolean isImageField, boolean isBranched){
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                //addInputFieldsToGridPane(inputFieldsList.size(),helper, false, isImageField);
                if(isBranched){
                    addInputFieldsToGridPane(inputFieldsListBranching.size()+1,helper, false, isImageField,isBranched, true);
                }else{
                    addInputFieldsToGridPane(inputFieldsListNonBranching.size()+1,helper, false, isImageField,isBranched, true);
                }

            }
        };
        return eventHandler;
    }
//    public EventHandler removeInputFieldFromGridPane(GridPaneHelper helper,boolean isImageField, Button file,
//                                                      TextField answerKey,TextField inputName,
//                                                      TextField inputValue,Button add, Button remove, InputFields fields,
//                                                     int index, boolean isBranched){

        public EventHandler removeInputFieldFromGridPane(GridPaneHelper helper,boolean isImageField, Button file,
                TextField answerKey, TextField inputValue,Button add, Button remove, InputFields fields,
        int index, boolean isBranched){

        return event -> {
            if(isImageField) {
//                helper.getGrid().getChildren().removeAll(file, inputName, inputValue, add, remove);
                helper.getGrid().getChildren().removeAll(file, inputValue, add, remove);
            } else {
//                helper.getGrid().getChildren().removeAll(answerKey, inputName, inputValue, add, remove);
                helper.getGrid().getChildren().removeAll(answerKey, inputValue, add, remove);
            }
            System.out.println("REMOVE INDEX : "+index);
            if(isBranched){
                inputFieldsListBranching.remove(fields);
            }
            else{
                inputFieldsListNonBranching.remove(fields);
            }
//            page.getVignettePageAnswerFields().getAnswerFieldList().remove(index-1);
        };

    }

    public void addInputFieldToHtmlEditor(boolean isImageField, boolean isBranched, boolean isRequired) {
        String question = questionText.getValue();
        ArrayList<String> optionsList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        String name = inputNameProperty.getValue();
        if(isBranched && "b-".equalsIgnoreCase(name)){
            name+=page.getPageName();
        }
        List<InputFields> inputFieldsList;
        if (isBranched) {
            inputFieldsList = new ArrayList<>(inputFieldsListBranching);
        } else {
            inputFieldsList = new ArrayList<>(inputFieldsListNonBranching);
        }
        VignettePageAnswerFields temp = page.getVignettePageAnswerFieldsBranching();
        String type = inputFieldsList.get(0).getInputType();

        page.getVignettePageAnswerFieldsBranching().setQuestion(questionText.getValue());
        page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().clear();

        for (int i = 0; i < inputFieldsList.size(); i++) {
            InputFields input = inputFieldsList.get(i);
            inputFieldsList.get(i).setInputType(this.inputTypeProperty);
            optionsList.add(input.getAnswerKey());
            valueList.add(input.getInputValue());
            AnswerField answerField = new AnswerField();
            answerField.setAnswerKey(input.getAnswerKey());
            answerField.setInputName(input.getInputName());
            answerField.setInputValue(input.getInputValue());
            temp.getAnswerFieldList().add(answerField);
        }
        if(isBranched)
            page.setVignettePageAnswerFieldsBranching(temp);
        else
            page.addAnswerFieldToNonBranching(temp);
        String[] o = new String[optionsList.size()];
        for (int i = 0; i < optionsList.size(); i++)
            o[i] = optionsList.get(i);
        String[] v = new String[valueList.size()];
        for (int i = 0; i < valueList.size(); i++)
            v[i] = valueList.get(i);
        try{
            Questions q = null;
            if(isBranched){
                q = new Questions(type.trim(), question.trim(),this.getImageSourceForQuestion(), o,v, name, isBranched, isRequired);
                if(!Questions.hasBranchingQuestion){
                    page.addToQuestionList(q);
                    Questions.hasBranchingQuestion = true;
                }
                else{
                    AtomicInteger index = new AtomicInteger(-1);
                    page.getQuestionList().stream().forEach(ques->{
                        index.set(index.get()+1);
                        if(ques.getBranchingQuestion())
                            return;
                    });
                    page.getQuestionList().set(index.get(), q);
                }
            }else{
                //Not a branching question
                q = new Questions(type.trim(), question.trim(),this.getImageSourceForQuestion(), o,v, name, isBranched, isRequired);
                page.addToQuestionList(q);
            }

        }catch (Exception e){
            System.out.println("QUESTION ADDING: "+e.getMessage());
        }
        System.out.println("BRANCHING QUESTIONS::: ");
        System.out.println(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList());
        setImageSourceForQuestion("");
        optionsList.clear();
        valueList.clear();
    }
    public  EventHandler fileChoose(InputFields fields) {
        final String[] fileName = {null};
        AtomicReference<BufferedImage> image = new AtomicReference<>();
        return event -> {

            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images", "*.JPG","*.PNG", "*.JPEG", "*.GIF","");
            filterList.add(extFilterJPG);

            FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
            File file = fileHelper.openFileChooser(filterList);
            if(file !=null){
                fileName[0] = file.getName();
                try {
                    image.set(ImageIO.read(file));
                    Images images = new Images(fileName[0],image.get());
                    fields.setImages(fileName[0]);
                    Main.getVignette().addToImageList(images);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * connects source and target buttons
     */
    public boolean connectPages(VignettePage pageTwo, String... pageKey){
//        VignettePage pageOne = Main.getVignette().getPageViewList().get(page.getPageName());
//        VignettePage pageTwo = Main.getVignette().getPageViewList().get(defaultNextPage);

        TabPaneController pane = Main.getVignette().getController();
        Button source = pane.getButtonPageMap().get(page.getPageName());
        Button target = pane.getButtonPageMap().get(pageTwo.getPageName());
        boolean data  = pane.checkPageConnection(page,pageTwo,source,target, pageKey);
        return data;
    }

   // -----------GETTERS AND SETTERS--------------------
    public String getQuestionText() {return questionText.get(); }
    public StringProperty questionTextProperty() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText.set(questionText); }


    public String getInputType() { return inputTypeProperty; }
    public void setInputType(String inputType) { this.inputTypeProperty = inputType; }


    public StringProperty getInputName() { return inputNameProperty; }
    public void setInputName(String inputName) { this.inputNameProperty.set(inputName); }

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