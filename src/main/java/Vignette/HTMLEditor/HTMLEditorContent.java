package Vignette.HTMLEditor;

import Application.Main;
import DialogHelpers.DialogHelper;
import DialogHelpers.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import Utility.Utility;
import Vignette.Branching.BranchingImpl;
import Vignette.HTMLEditor.InputFields.InputFields;
import Vignette.Page.AnswerField;
import Vignette.Page.ConnectPages;
import Vignette.Page.VignettePage;
import ConstantVariables.ConstantVariables;
import Vignette.Page.VignettePageAnswerFields;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import TabPane.TabPaneController;
import ConstantVariables.BranchingConstants;


public class HTMLEditorContent {


    private TextArea htmlSourceCode;
    private String type;
    private VignettePage page;
    private int countOfAnswer;
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
    SimpleStringProperty numberofAnswerChoiceValue;
    SimpleStringProperty branchingType;
    private String inputTypeProperty;
    String defaultNextPage = null;
    private String editConnectionString="";
    HashMap<String, String> optionEntries = new HashMap<>();



    private boolean hasBranchingQuestion;


    public HTMLEditorContent(TextArea htmlSourceCode,
                             String type, VignettePage page,
                             List<String> pageNameList,
                             SimpleStringProperty branchingType,
                             SimpleStringProperty numberofAnswerChoiceValue, Label pageName){
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.page = page;
        this.pageNameList = pageNameList;
        answerChoice= new ArrayList<>();
        answerPage = new ArrayList<>();
        this.branching = new BranchingImpl(this.page);
        inputFieldsListBranching =  new ArrayList<>();
        inputFieldsListNonBranching =  new ArrayList<>();
        this.numberofAnswerChoiceValue = numberofAnswerChoiceValue;
        this.branchingType = branchingType;
        pageName.setAlignment(Pos.CENTER);
        pageName.setText(page.getPageName());
        updateOptionEntries();
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
    public String addTextToEditor() throws URISyntaxException, FileNotFoundException {

         String text = null;
        InputStream inputStream = null;

         if(!type.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) {
             inputStream = getClass().getResourceAsStream(ConstantVariables.PAGE_TYPE_LINK_MAP.get(type));
             text = readFile(inputStream);
         }
         else{
             text= ConstantVariables.SCRIPT_FOR_CUSTOM_PAGE;
         }

        htmlSourceCode.setText(text);

        //after opening the page, first it will set the initial text. Print statement below onKeyRelease will be executed
        //and if you type anything it will be recognized because of this event handler.
        htmlSourceCode.setOnKeyReleased(event -> {

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

    public String setText(String text){
        htmlSourceCode.setText(text);
        htmlSourceCode.setOnKeyReleased(event -> {
           // htmlEditor.setHtmlText(htmlSourceCode.getText());
            page.setPageData(htmlSourceCode.getText());

        });

        return text;

    }
    public void addVideo() {
        GridPaneHelper helper = new GridPaneHelper();
        helper.addLabel("Video Link:", 1, 1);
        TextField text = helper.addTextField(2, 1, 400, 400);
        String[] videoOptions = {BranchingConstants.VIMEO_VIDEO_OPTION, BranchingConstants.YOUTUBE_VIDEO_OPTION};

        ComboBox video  = helper.addDropDown(videoOptions,0,1);
        boolean isSaved = helper.createGrid("Video Link", null, "ok", "Cancel");
        if (isSaved) {
            String getText = htmlSourceCode.getText();
            String iframeRegEx = ".*<iframe id=\"pageVimeoPlayer\".*";
            Pattern pattern = Pattern.compile(iframeRegEx);
            Matcher matcher = pattern.matcher(getText);
            if (matcher.find()) {
                //String previous = (matcher.group(0));
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                String videoID="", videoURL = "";
                if(BranchingConstants.VIMEO_VIDEO_OPTION.equalsIgnoreCase(video.getValue().toString())){
                    videoID = text.getText().split("/")[text.getText().split("/").length-1];
                    videoURL = "https://player.vimeo.com/video/"+videoID;
                }else if(BranchingConstants.YOUTUBE_VIDEO_OPTION.equalsIgnoreCase(video.getValue().toString())){
//                    https://www.youtube.com/watch?v=Bwbfz8gky08
                    videoID = text.getText().split("=")[1];
                    videoURL = "https://player.vimeo.com/video/"+videoID;
                }
                String Iframetext = "\t<iframe id=\"pageVimeoPlayer\" class=\"embed-responsive-item vimPlay1\" " +
                        "src=\"" + videoURL + "\" width=\"800\" height=\"450\" " +
                        "frameborder=\"0\" allow=\"autoplay; fullscreen\" allowfullscreen></iframe>";
                htmlSourceCode.replaceSelection(Iframetext);


                //Saves the page, required for undo/redo
                page.setPageData(htmlSourceCode.getText());
                Main.getVignette().getPageViewList().put(page.getPageName(),page);
            }
        }
    }
    /**
     * Identify multiple file uploads and add them as an image tag to the source HTMl code
     * @return
     */
    public Images addImageTag(){
        GridPaneHelper helper = new GridPaneHelper();
        helper.addLabel("Choose File:" ,1,1);
        final String[] fileName = {null};
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("All Images(*)", "*.JPG","*.PNG","*.JPEG","*.GIF");
                filterList.add(extFilterJPG);
                FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
                File file = fileHelper.openFileChooser(filterList);
                if(file !=null){
                    fileName[0] = file.getName();
                    try {
                       image = ImageIO.read(file);
                        Main.getVignette().getImagesList().add(new Images(fileName[0], image));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        Button addImageButton =  helper.addButton("File",2,1,eventHandler);
        helper.addLabel("Width of Image",1,2);
        TextField widthofImage = helper.addTextField(2,2);
        widthofImage.setText("50");
        helper.addLabel("Image Class Name",1,3);
        TextField className = helper.addTextField(2,3);
        className.setText("img-fluid");
        boolean clicked = helper.createGrid("Image",null,"Ok","Cancel");
        boolean isValid = false;
        System.out.println();
        System.out.println(fileName);
        if(clicked) {
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
//          String imageText = "<img class=\""+className.getText()+"\" style='width:\""+widthofImage.getText()+"%\" " +
//                             "src=\""+ConstantVariables.imageResourceFolder+fileName[0]+ "\" alt=\"IMG_DESCRIPTION\" >\n";
            String imageText ="<img class=\""+className.getText()+"\" style='width:"+widthofImage.getText()+"%;' src=\""+ConstantVariables.imageResourceFolder+fileName[0]+"\" alt=\"IMG_DESCRIPTION\">\n";
            String temp = htmlSourceCode.getText();
            //String text = "<img class=\"img-fluid\" width=\"50%\" src=\"Images/Screen Shot 2021-06-09 at 11.14.27 AM.png\" alt=\"IMG_DESCRIPTION\" >";
            String imagePatter =".*<img[^>]*src=\\\"([^\\\"]*)\\\" alt=\\\"([^\\\"]*)\\\">";


            Pattern pattern = Pattern.compile(imagePatter);
            Matcher matcher = pattern.matcher(temp);

            if(matcher.find()){
                htmlSourceCode.selectRange(matcher.start(), matcher.end());
                htmlSourceCode.replaceSelection(imageText);

                page.setPageData(htmlSourceCode.getText());
                Main.getVignette().getPageViewList().put(page.getPageName(),page);
            }
            else {
                System.out.println("IMAGE NOT FOUND");
            }

        }
        Images images = new Images(fileName[0],image);
        return images;
    }
    public String addProblemStatmentToQuestion(){
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
                    htmlSourceCode.setText("");
                    htmlSourceCode.setText(htmlCodeInString);
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
        if(branchingType.getValue().equals(BranchingConstants.NO_QUESTION)){
            helper.addLabel("Default Next Page", 0,0);
            if(optionEntries.size()>0)
                defaultNextPageBox = helper.addDropDownWithDefaultSelection(pageNameList.stream().toArray(String[]::new), 0,1, optionEntries.get("default"));
            else
                defaultNextPageBox = helper.addDropDown(pageNameList.stream().toArray(String[]::new), 0,1);
        }
        else {
            int size = editNextPageAnswers ? answerChoice.size() :
                    numberofAnswerChoiceValue.getValue() == null ? 0 : Integer.parseInt(numberofAnswerChoiceValue.getValue());
            for (int i = 0; i < size; i++) {
                addNextPageTextFieldToGridPane(i, helper, editNextPageAnswers, false);
            }
            if(branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)){
                addNextPageTextFieldToGridPane(size+1,helper, editNextPageAnswers, true);
            }

        }
        Boolean clickedOk = helper.createGrid("Next Answer Page ",null, "ok","Cancel");
        if(clickedOk){
            if(branchingType.getValue().equals(BranchingConstants.NO_QUESTION)){
                defaultNextPage = (String) defaultNextPageBox.getSelectionModel().getSelectedItem();
                if(!defaultNextPage.equalsIgnoreCase(page.getPageName())){
                    VignettePage pageTwo = Main.getVignette().getPageViewList().get(defaultNextPage);
                    page.setQuestionType(branchingType.getValue());
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
            for(int i =0;i<answerChoice.size();i++){
                if(!answerChoice.get(i).getText().equals("")){
                    if(!answerPage.get(i).getValue().toString().equalsIgnoreCase(page.getPageName())){
                        VignettePage pageTwo = Main.getVignette().getPageViewList().get(answerPage.get(i).getValue().toString());
                        if(connectPages(pageTwo, answerChoice.get(i).getText()))
                            answerNextPage += " "+"'"+answerChoice.get(i).getText()+"'"+ ":" + "'"+answerPage.get(i).getValue()+"'" +",";
                    }else{
                        DialogHelper connectionNotPossible = new DialogHelper(Alert.AlertType.ERROR,"Cannot Connect Pages",
                                null,"Pages May not connect to itself", false);
                    }
                }
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
//            System.out.println(answerNextPage);
            answerChoice.clear();
            answerPage.clear();
            return answerNextPage;
        }
        return "{}";
    }

    /**
     *
     * @param helper
     * @return
     */
    public EventHandler addToGridPane(GridPaneHelper helper){
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
               addNextPageTextFieldToGridPane(countOfAnswer,helper, false, false);
               countOfAnswer++;
            }
        };
        return eventHandler;
    }
    public EventHandler removeFromGridPane(GridPaneHelper helper, TextField text,ComboBox dropdown,Button add,Button remove){

        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
              helper.getGrid().getChildren().removeAll(text,dropdown,add,remove);
              countOfAnswer--;
            }
        };
        return eventHandler;
    }

    /**
     * this function is used to support the '+' functionality to add pages textField in the next answer page dialog box
     * @param index
     * @param helper
     * @param editNextPageAnswers
     * @param addDefault
     */

    public void addNextPageTextFieldToGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers, Boolean addDefault){
        char answerAlphabet = ((char) (65+index));
        if(!editNextPageAnswers) {
            TextField text = helper.addTextField(0, index);
            text.setText(addDefault?"default":""+answerAlphabet);
            String[] pageList = pageNameList.toArray(new String[0]);
            ComboBox dropdown = helper.addDropDown(pageList, 1, index);
            if(optionEntries.size()>0)
                dropdown.setValue(optionEntries.get(answerAlphabet+""));
            Button add= helper.addButton("+", 2, index, addToGridPane(helper));
            Button remove =  helper.addButton("-", 3, index);
            remove.setOnAction(removeFromGridPane(helper,text,dropdown,add,remove));
            countOfAnswer++;
            answerChoice.add(text);
            answerPage.add(dropdown);
        }
        else {
           helper.addExistingTextField(answerChoice.get(index),0,index);
           helper.addExistingDropDownField(answerPage.get(index),1,index);
           Button add= helper.addButton("+", 2, index, addToGridPane(helper));
           Button remove =  helper.addButton("-", 3, index);
           remove.setOnAction(removeFromGridPane(helper,answerChoice.get(index),answerPage.get(index),add,remove));
        }
    }
    public void editNextPageAnswers(Boolean noBranchingSelected){
        String htmlText ="";
        String nextPageAnswers = "";
        nextPageAnswers = createNextPageAnswersDialog(false, false);
        Utility utility = new Utility();
        String questionType = BranchingConstants.QUESTION_TYPE+"= '" + utility.checkPageType(branchingType.getValue()) + "';";
        htmlText = htmlSourceCode.getText();

        htmlText = !nextPageAnswers.equals("{}") ?
                htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET, BranchingConstants.NEXT_PAGE_ANSWER+"="
                        + nextPageAnswers + ";") :
                htmlText;
        String questionTypeText = "";
        if( htmlText.contains(BranchingConstants.QUESTION_TYPE)){
            htmlText = htmlText.replaceFirst(BranchingConstants.QUESTION_TYPE_TARGET, questionType);
            System.out.println("QUESTION TYPE: "+questionType);
            page.setQuestionType(branchingType.getValue());
//            htmlText = htmlText.replaceFirst(BranchingConstants.QUESTION_TYPE, questionTypeText);
        } else{
            questionTypeText+=questionType+"\n";
        }

        htmlText = htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_NAME_TARGET, questionTypeText+
                                         BranchingConstants.NEXT_PAGE_NAME +"='"+
                                         defaultNextPage+"';");
        htmlSourceCode.setText(htmlText);
        page.setPageData(htmlSourceCode.getText());
        Main.getVignette().getPageViewList().put(page.getPageName(),page);
    }




    /**
        paneHelper.addLabel("Recent Files: ", 1, 1);
        Spinner<Integer> spinner = paneHelper.addNumberSpinner(Main.getRecentFiles().getNumRecentFiles(),1,Integer.MAX_VALUE,2,1);
        paneHelper.addLabel("",1,2);
        Button button =  paneHelper.addButton("Clear Recent Files",2,2);
        button.setOnAction(event -> {
            Main.getRecentFiles().clearRecentFiles();
        });
        paneHelper.createGrid("Preferences",null, "Save","Cancel");
        boolean isSaved = paneHelper.isSave();

        if(isSaved){
            Main.getRecentFiles().saveNumberRecentFiles(spinner.getValue());
        }

     */


        /**
         * page setting button pane that appears on left toolbox
         */
    public void editPageSettings(){
        GridPaneHelper helper = new GridPaneHelper();

        String buttonStyle= "-fx-text-align: center;"+ "-fx-background-color: transparent;" + "-fx-font-size: 25px;" +
                "-fx-border-radius: 7;" + "-fx-text-fill:  #007bff;" +
                "-fx-color: #007bff;" + "-fx-border-width: 3 3 3 3;"+ "-fx-border-color: #007bff;"+"-fx-opacity:";

        //------------------------------------- EDIT OPTIONS -----------------------------------------------------------
        helper.addLabel("Options: ",1,1);
        CheckBox disabledOptions = helper.addCheckBox("Disable",2,1,true);
        helper.addLabel("Opacity",3,1);
        Spinner optionsSpinner = new Spinner(0.0,1.0,1.0,0.1);
        helper.addSpinner(optionsSpinner,4,1);

        AtomicReference<Double> optionsOpacity = new AtomicReference<>((double) 1);
        Button optionsButton = new Button("Options");

        optionsButton.setStyle(buttonStyle+1+";");

        optionsSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            optionsOpacity.set((Double) newValue);
            optionsButton.setStyle(buttonStyle+optionsOpacity+";");
        });
        helper.addButton(optionsButton,5,1);
        //-------------------------------------------------------------------------------------------------------------

        //-------------------------------- EDIT PROBLEM STATEMENT ------------------------------------------------------
        helper.addLabel("Problem Statement: ",1,2);
        CheckBox disabledProblemStatement = helper.addCheckBox("Disable",2,2,true);
        helper.addLabel("Opacity",3,2);
        Spinner problemStatementSpinner = new Spinner(0.0,1.0,1.0,0.1);
        helper.addSpinner(problemStatementSpinner,4,2);

        AtomicReference<Double> probOpacity = new AtomicReference<>((double) 1);
        Button probButton = new Button("Show Problem Statement");

        probButton.setStyle(buttonStyle+1+";");

        problemStatementSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            probOpacity.set((Double) newValue);
            probButton.setStyle(buttonStyle+probOpacity+";");
        });
        helper.addButton(probButton,5,2);
        //-------------------------------------------------------------------------------------------------------------

        //----------------------------------- EDIT PREV PAGE -----------------------------------------------------------

        helper.addLabel("Prev Page: ",1,3);
        CheckBox disabledPrevPage = helper.addCheckBox("Disable",2,3,true);
        helper.addLabel("Opacity",3,3);
        Spinner prevPageSpinner = new Spinner(0.0,1.0,1.0,0.1);
        helper.addSpinner(prevPageSpinner,4,3);

        AtomicReference<Double> prevPageOpacity = new AtomicReference<>((double) 1);
        Button prevPageButton = new Button("Back to Previous Page");

        prevPageButton.setStyle(buttonStyle+1+";");

        prevPageSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            prevPageOpacity.set((Double) newValue);
            prevPageButton.setStyle(buttonStyle+prevPageOpacity+";");
        });
        helper.addButton(prevPageButton,5,3);
        //-------------------------------------------------------------------------------------------------------------


        //----------------------------------- EDIT NEXT PAGE -----------------------------------------------------------

        helper.addLabel("Next Page: ",1,4);
        CheckBox disabledNextPage = helper.addCheckBox("Disable",2,4,true);
        helper.addLabel("Opacity",3,4);
        Spinner nextPageSpinner = new Spinner(0.0,1.0,1.0,0.1);
        helper.addSpinner(nextPageSpinner,4,4);

        AtomicReference<Double> nextPageOpacity = new AtomicReference<>((double) 1);
        Button nextPageButton = new Button("Continue to Next Page");

        nextPageButton.setStyle(buttonStyle+1+";");

        nextPageSpinner.valueProperty().addListener((observable,oldValue,newValue) -> {
            nextPageOpacity.set((Double) newValue);
            nextPageButton.setStyle(buttonStyle+nextPageOpacity+";");
        });
        helper.addButton(nextPageButton,5,4);
        //-------------------------------------------------------------------------------------------------------------



        boolean clickedOk = helper.createGrid("Page Settings", null,"Ok","Cancel");
        if(clickedOk) {

            String target = "//Settings([\\S\\s]*?)settings";
            String htmlText = htmlSourceCode.getText();
            Pattern pattern = Pattern.compile(target);
            Matcher matcher = pattern.matcher(htmlText);

            if(matcher.find())
            {
                System.out.println("found");
                String tag1 = "//Settings";
                String options ="    $(\"#options\").prop('disabled', "+disabledOptions.isSelected()+
                        ").css('opacity', "+optionsSpinner.getValue()+");";
                String problemStatement ="    $(\"#problemStatement\").prop('disabled', "+disabledProblemStatement.isSelected()+
                        ").css('opacity', "+ problemStatementSpinner.getValue()+");";
                String prevPage ="    $(\"#PrevPage\").prop('disabled', "+disabledPrevPage.isSelected()+
                        ").css('opacity', "+ prevPageSpinner.getValue()+");";
                String nextPage = "    $(\"#NextPage\").prop('disabled', "+disabledNextPage.isSelected()+
                        ").css('opacity', "+ nextPageSpinner.getValue()+");";
                String tag2 = "    //settings";
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
                createInputField(field,isImageField,false);
            });

            helper1.addButton(buttonNonBranch,0,0);



            // Checking the page if there currently is a
            Pattern branchPattern = Pattern.compile("<!-- //////// BranchQ //////// -->\n(.*?)<!-- //////// End BranchQ //////// -->\n", Pattern.DOTALL);
            Matcher matcher;
            matcher = branchPattern.matcher(htmlSourceCode.getText());

            if (matcher.find()) {
                Button buttonEditBranching = new Button("Edit Branching Input Field");
                buttonEditBranching.setPrefSize(1000,60);
                buttonEditBranching.setOnAction(event ->{createInputField(field, isImageField, true);});
                helper1.addButton(buttonEditBranching,0,1);
            }
            else {
                Button buttonAddBranching = new Button("Branching");
                buttonAddBranching.setPrefSize(1000,60);
                buttonAddBranching.setOnAction(event->{
                    createInputField(field, isImageField, true);
                });
                helper1.addButton(buttonAddBranching, 0, 1);
            }

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
            helper.addLabel("Answer Key:",0,2);
            helper.addLabel("Input Value:",1,2);
            //------------------------------------------------------------------------

            int listSize=0;
            if(isBranched)
                listSize = page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size();
            int size = listSize==0 ? 4 : listSize;
            if(listSize >0){
                for (int i = 1; i <= listSize; i++) {
                    //addInputFieldsToGridPane(i, helper, true, isImageField);
                    addInputFieldsToGridPane(i,helper,true,isImageField,isBranched);
                }
            }
            else {
                for (int i = 1; i <= size; i++) {
                    //addInputFieldsToGridPane(i, helper, false, isImageField);
                    addInputFieldsToGridPane(i,helper,false,isImageField,isBranched);
                }
            }
        }else if(getInputType().equalsIgnoreCase(ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN) || getInputType().equalsIgnoreCase(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN)){
            helper.removeAllFromHelper();
            addStuffToHelper(helper, field, isImageField, isBranched);

        }
        helper.setScaleShape(true);
    }


    public void addStuffToHelper(GridPaneHelper helper, int field, boolean isImageField, boolean isBranched){
        String[] dropDownListBranching = {ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN, ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN};
        String[] dropDownListNonBranching = {ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN, ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN,
                ConstantVariables.RADIO_INPUT_TYPE_DROPDOWN, ConstantVariables.CHECKBOX_INPUT_TYPE_DROPDOWN};

        helper.addLabel("Question:",0,0);
        helper.addLabel("Input Type:", 1,0);

        TextArea question = helper.addTextArea(0,1);

        // This prevents the user from selecting textarea and textfield options in branched questions
        ComboBox inputTypeDropDown;
        if(isBranched){
            inputTypeDropDown = helper.addDropDown(dropDownListBranching, 2, 0);
        }
        else{
            inputTypeDropDown = helper.addDropDown(dropDownListNonBranching, 2, 0);
        }
        helper.addLabel("Input Name:",1,1);
        TextField inputName = helper.addTextField(page.getPageName(), 2,1);
        InputFields fields = new InputFields();
        inputName.textProperty().bindBidirectional(fields.inputNameProperty());
        inputName.setText(page.getPageName());

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
            manageTextFieldsForInputFieldHelper(helper, field, isImageField, isBranched);
        });

        helper.setScaleShape(true);
    }
    public void createInputField(int field, boolean isImageField, boolean isBranched) {
        GridPaneHelper helper = new GridPaneHelper();
        // -----ADDING Question TextArea, InputValue TextField and label
        addStuffToHelper(helper, field, isImageField, isBranched);

        System.out.println("HELPER : "+helper.getGrid().getChildren().toString());
        System.out.println("INPUT TYPE VALUE: "+getInputType());
        //Keep on adding options
        manageTextFieldsForInputFieldHelper(helper, field, isImageField, isBranched);

        Boolean clickedOk = helper.createGrid("Input Field ", null, "ok", "Cancel");

        if (clickedOk) {
            String questionToInsert = addInputFieldToHtmlEditor(isImageField,isBranched);
            String htmlCodeInString = htmlSourceCode.getText();

            //Replace existing question
            Pattern branchPattern;
            if(isBranched)
                branchPattern = Pattern.compile("<!-- //////// BranchQ //////// -->\n(.*?)<!-- //////// End BranchQ //////// -->\n", Pattern.DOTALL);
            else
                branchPattern = Pattern.compile("<!-- //////// Non BranchQ //////// -->\n(.*?)<!-- //////// End Non BranchQ //////// -->\n", Pattern.DOTALL);
            Matcher matcher;
            //Adding or replacing existing branching question
//            if(isBranched) {

            matcher = branchPattern.matcher(htmlCodeInString);
            //If there already is a branching question, find it and replace it in an undoable manner
            if (matcher.find()) {
                //selecting and replacing to make the process undoable
                if(!isBranched){
                    // if it is a nonBranching question, a new question is Appended to the Text of NonBranching question
                    String comments = "<!-- //////// Non BranchQ //////// -->\n ";
                    questionToInsert = comments + matcher.group(1)+"<!-- //////// Question: "+page.getVignettePageAnswerFieldsNonBranching().size()+" //////// -->\n"+questionToInsert;
                    questionToInsert+="<!-- //////// End Non BranchQ //////// -->\n";
                }
                    htmlSourceCode.selectRange(matcher.start(), matcher.end());
                    htmlSourceCode.replaceSelection(questionToInsert);
            }
            // otherwise insert it at the user provided position
            else {
                System.out.println("No existing branching question Found, Appending at caret position");
                htmlSourceCode.insertText(field,questionToInsert);
            }


//            }
//            // inserting the non branching question at user provided position.
//            else
//            {
//                System.out.println("Appending Non branching question at caret position");
//                htmlSourceCode.insertText(field,questionToInsert);
//            }

            //saving changes
            page.setPageData(htmlSourceCode.getText());
            Main.getVignette().getPageViewList().put(page.getPageName(), page);
            inputFieldsListBranching.clear();
            inputFieldsListNonBranching.clear();
        }else{
            helper.removeAllFromHelper();
            helper.clear();
        }
    }

    /**
     * add input fields to the HTML content
     * @param index
     * @param helper
     * @param editAnswers
     * @param isImageField
     */
    public void addInputFieldsToGridPane(int index, GridPaneHelper helper, Boolean editAnswers, Boolean isImageField, boolean isBranched){

        InputFields fields = new InputFields();
        TextField answerField = null;
        Button file = null;
        Group group = new Group();
        if(isImageField){
           file = helper.addButton("File",0,index+2,fileChoose(fields));
        }else {
            answerField = helper.addTextField("option choice "+index,0, index + 2);
            answerField.textProperty().bindBidirectional(fields.answerKeyProperty());
            if(editAnswers){
                answerField.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index-1).getAnswerKey());
            }
        }

        // this sets the input type of the question to the page id
//        TextField inputName = helper.addTextField(page.getPageName(), 1,index+2);
//        inputName.textProperty().bindBidirectional(fields.inputNameProperty());

        // we dont need input value for regular questions
        TextField inputValue;
        if(isBranched) {
//            inputName.setText(page.getPageName());
//            inputValue = helper.addTextField(2, index + 2);
//            inputValue.textProperty().bindBidirectional(fields.inputValueProperty());
//            if (editAnswers) {
//                inputValue.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index - 1).getInputValue());
//            }
        }
        inputValue = helper.addTextField(1, index + 2);
        inputValue.textProperty().bindBidirectional(fields.inputValueProperty());
        if (editAnswers) {
            inputValue.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().get(index - 1).getInputValue());
        }
//        //todo
//        else
//             inputValue = null;


        fields.setId(index);
        fields.setImageField(isImageField);
        fields.setInputType(getInputType());

        //todo non branching questions cannot use input tags
        int removeIndex;
        if(isBranched){
            inputFieldsListBranching.add(fields);
            removeIndex = inputFieldsListBranching.size();
        }
        else{
            inputFieldsListNonBranching.add(fields);
            removeIndex = inputFieldsListNonBranching.size();
        }


        // the +, - buttons on the GridPane
       Button add =  helper.addButton("+", 2, index+2, addNewInputFieldToGridPane(helper,isImageField, isBranched));
       Button remove = helper.addButton("-", 3, index+2);

//       page.setVignettePageAnswerFields(page.getVignettePageAnswerFields().getAnswerFieldList().add());
//       remove.setOnAction(removeInputFieldFromGridPane(helper,
//               isImageField, file, answerField, inputName, inputValue,
//               add, remove, fields, removeIndex, isBranched));

        remove.setOnAction(removeInputFieldFromGridPane(helper,
                isImageField, file, answerField, inputValue,
                add, remove, fields, removeIndex, isBranched));
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
                    System.out.println("ADD NEW INPUT INDEX BRANCHING: "+inputFieldsListBranching.size());
                    addInputFieldsToGridPane(inputFieldsListBranching.size()+1,helper, false, isImageField,isBranched);
                }else{
                    System.out.println("ADD NEW INPUT INDEX NON BRANCHING: "+inputFieldsListNonBranching.size());
                    addInputFieldsToGridPane(inputFieldsListNonBranching.size()+1,helper, false, isImageField,isBranched);
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

    public String addInputFieldToHtmlEditor(boolean isImageField, boolean isBranched) {
        String divTag = "    <div id=\"multiple\">\n";

        String parTag = isImageField ? "<p class=\"normTxt\">\n" :
                "<p class=\"normTxt\" style='padding: 0px 15px 0px; text-align:left; width:95%; ' >\n";

        StringBuilder builder = new StringBuilder();
        String questionToInsert = "<p class=\"normTxt\" id=\"question_text\" style='padding: 0px 15px 0px; text-align:left; width:95%; ' >\n" +
                "" + questionText.getValue() + "\n" +
                "</p>";

        if (isBranched)//if its a branching question.
        {
            VignettePageAnswerFields temp = page.getVignettePageAnswerFieldsBranching();
            builder.append("\n<!-- //////// BranchQ //////// -->\n");
            builder.append(divTag + questionToInsert);

            page.getVignettePageAnswerFieldsBranching().setQuestion(questionText.getValue());
            page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().clear();

            for (int i = 0; i < inputFieldsListBranching.size(); i++) {
                InputFields input = inputFieldsListBranching.get(i);
                inputFieldsListBranching.get(i).setInputType(this.inputTypeProperty);
                System.out.println(inputFieldsListBranching.get(i).toString());

                builder.append(parTag + inputFieldsListBranching.get(i).toString() + " </p>\n");
                AnswerField answerField = new AnswerField();
                answerField.setAnswerKey(input.getAnswerKey());
                answerField.setInputName(input.getInputName());
                answerField.setInputValue(input.getInputValue());
                temp.getAnswerFieldList().add(answerField);
            }

            System.out.println("ADDING NEW ANSWER FIELD: "+temp.getAnswerFieldList());
            page.setVignettePageAnswerFieldsBranching(temp);
            System.out.println("PAGE ANSWER FIELD: "+ page.getVignettePageAnswerFieldsBranching());

            System.out.println("PAGE BRANCHING ANSWER FIELD: "+page.getVignettePageAnswerFieldsBranching().getAnswerFieldList());
            builder.append("</div>\n");
            builder.append("<br/>\n");
            builder.append("<!-- //////// End BranchQ //////// -->\n");
        }

        // creating non branched question
        else {
            VignettePageAnswerFields tempToAdd = new VignettePageAnswerFields();
//            builder.append("\n<!-- //////// Non BranchQ //////// -->\n");
            builder.append(divTag + questionToInsert);

            tempToAdd.setQuestion(questionText.getValue());
//            page.getVignettePageAnswerFieldsBranching().setQuestion(questionText.getValue());
//            page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().clear();
            System.out.println("NON BRANCHING FIELD SIZE: "+inputFieldsListNonBranching.size());
            for (int i = 0; i < inputFieldsListNonBranching.size(); i++) {
                InputFields input = inputFieldsListNonBranching.get(i);
                inputFieldsListNonBranching.get(i).setInputType(this.inputTypeProperty);
//                System.out.println(inputFieldsListNonBranching.get(i).toString());
                builder.append(parTag + inputFieldsListNonBranching.get(i).toString() + " </p>\n");
                AnswerField answerField = new AnswerField();
                answerField.setAnswerKey(input.getAnswerKey());
                answerField.setInputName(input.getInputName());
                tempToAdd.getAnswerFieldList().add(answerField);
            }
            page.addAnswerFieldToNonBranching(tempToAdd);
            builder.append("</div>\n");
            builder.append("<br/>\n");
            builder.append("<!-- //////// End Question //////// -->\n");
        }

        return builder.toString();
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
    public void setInputType(String inputType) { this.inputTypeProperty= inputType; }


    public TextArea getHtmlSourceCode() { return htmlSourceCode; }
    public void setHtmlSourceCode(TextArea htmlSourceCode) {
        this.htmlSourceCode = htmlSourceCode;
    }


}
