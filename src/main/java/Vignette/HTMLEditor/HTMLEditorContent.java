package Vignette.HTMLEditor;

import Application.Main;
import DialogHelper.DialogHelper;
import DialogHelper.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import Utility.Utility;
import Vignette.Branching.BranchingImpl;
import Vignette.HTMLEditor.InputFields.InputFields;
import Vignette.Page.AnswerField;
import Vignette.Page.VignettePage;
import ConstantVariables.ConstantVariables;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
    List<InputFields> inputFieldsList;
    private final StringProperty questionText = new SimpleStringProperty();
    SimpleStringProperty numberofAnswerChoiceValue;
    SimpleStringProperty branchingType;
    private String inputTypeProperty;
    String defaultNextPage = null;

    public HTMLEditorContent(TextArea htmlSourceCode,
                             String type, VignettePage page,
                             List<String> pageNameList,
                             SimpleStringProperty branchingType,
                             SimpleStringProperty numberofAnswerChoiceValue){
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.page = page;
        this.pageNameList = pageNameList;
        answerChoice= new ArrayList<>();
        answerPage = new ArrayList<>();
        this.branching = new BranchingImpl(this.page);
        inputFieldsList =  new ArrayList<>();
        this.numberofAnswerChoiceValue = numberofAnswerChoiceValue;
        this.branchingType = branchingType;
    }

//    public void addDropDown(){
//        defaultNextPage.getItems().clear();
//        defaultNextPage.getItems().addAll(pageNameList);
//    }
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
        htmlSourceCode.setOnKeyReleased(event -> {

            page.setPageData(htmlSourceCode.getText());

        });

        return text;

    }
    public String readFile(InputStream file){

        //String nextPageAnswers = createNextPageAnswersDialog(false);

        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                if(text.contains("NextPageName")) text = "NextPageName=\""+page.getConnectedTo() +"\";";

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
        helper.addLabel("Video Link:" ,1,1);
        TextField text = helper.addTextField(2,1,400,400);
        boolean isSaved= helper.createGrid("Video Link",null,"ok","Cancel");

        if(isSaved) {

            String getText = htmlSourceCode.getText();
            String iframeRegEx  = ".*<iframe id=\"pageVimeoPlayer\".*";
            String Iframetext = "<iframe id=\"pageVimeoPlayer\" class=\"embed-responsive-item vimPlay1\" " +
                    "src=\""+text.getText()+"\" width=\"800\" height=\"450\" " +
                    "frameborder=\"0\" allow=\"autoplay; fullscreen\" allowfullscreen></iframe>";
            getText =  getText.replaceFirst(iframeRegEx, Iframetext);

            htmlSourceCode.setText(getText);
        }

    }
    public Images addImageTag(){


      GridPaneHelper helper = new GridPaneHelper();
      helper.addLabel("Choose File:" ,1,1);
        final String[] fileName = {null};
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
                FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
                FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
                filterList.add(extFilterJPG);
                filterList.add(extFilterPNG);
                FileChooserHelper fileHelper = new FileChooserHelper("Choose Image");
                File file = fileHelper.openFileChooser(filterList);
                if(file !=null){
                    fileName[0] = file.getName();
                    try {
                       image = ImageIO.read(file );
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
          String imageText = "<img class=\""+className.getText()+"\" width=\""+widthofImage.getText()+"%\" " +
                             "src=\""+ConstantVariables.imageResourceFolder+fileName[0]+ "\" alt=\"IMG_DESCRIPTION\" >\n";
          htmlSourceCode.insertText(field, imageText);

      }
      Images images = new Images(fileName[0],image);
      return images;
    }
    public String createNextPageAnswersDialog(Boolean editNextPageAnswers, Boolean noquestionSelected){
        GridPaneHelper helper = new GridPaneHelper();
        String answerNextPage = "[";
        ComboBox defaultNextPageBox = null;

        if(branchingType.getValue().equals(BranchingConstants.NO_QUESTION)){
            helper.addLabel("Default Next Page", 0,0);
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
                connectPages();

                return defaultNextPage;
            }

            for(int i =0;i<answerChoice.size();i++){

                if(!answerChoice.get(i).getText().equals(""))
                   answerNextPage += "[ "+"'"+answerChoice.get(i).getText()+"'"+ "," + "'"+answerPage.get(i).getValue()+"'" +
                           "],";
            }
            if(branchingType.getValue().equals(BranchingConstants.RADIO_QUESTION)) {
                defaultNextPage = (String) answerPage.get(0).getValue();
                answerNextPage+="[ 'default', '"+ defaultNextPage+"' ],";
            }
            if(branchingType.getValue().equals(BranchingConstants.CHECKBOX_QUESTION)) {
                int size = answerPage.size();
                defaultNextPage = (String) answerPage.get(size-1).getValue();
            }
            connectPages();

            answerNextPage+="]";

            answerChoice.clear();
            answerPage.clear();

            return answerNextPage;

        }
        return "[]";
    }
    public EventHandler addToGridPane(GridPaneHelper helper){
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
               addNextPageTextFieldToGridPane(countOfAnswer,helper, false, false);
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
    public void addNextPageTextFieldToGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers, Boolean addDefault){

        char answerAlphabet = ((char) (65+index));
        if(!editNextPageAnswers) {
            TextField text = helper.addTextField(0, index);
            text.setText(addDefault?"default":""+answerAlphabet);
            String[] pageList = pageNameList.toArray(new String[0]);
            ComboBox dropdown = helper.addDropDown(pageList, 1, index);
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
        if(noBranchingSelected) {

            nextPageAnswers = "[ [\"default\", \""+createNextPageAnswersDialog(false,true)+"\"]]";

        }
        else {
            nextPageAnswers = createNextPageAnswersDialog(false, false);
        }
        Utility utility = new Utility();
        String questionType = BranchingConstants.QUESTION_TYPE+"= '" + utility.checkPageType(branchingType.getValue()) + "';";
        htmlText = htmlSourceCode.getText();

        htmlText = !nextPageAnswers.equals("[]") ?
                htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET, BranchingConstants.NEXT_PAGE_ANSWER+"="
                         + nextPageAnswers + ";") :
                htmlText;
        String questionTypeText = "";
        if( htmlText.contains(BranchingConstants.QUESTION_TYPE)){
            htmlText = htmlText.replaceFirst(BranchingConstants.QUESTION_TYPE_TARGET, questionType);
        } else{

            questionTypeText+=questionType+"\n";
        }

        htmlText = htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_NAME_TARGET, questionTypeText+
                                         BranchingConstants.NEXT_PAGE_NAME +"='"+
                                         defaultNextPage+"';");

        htmlSourceCode.setText(htmlText);

    }
    public void editPageSettings(){
        GridPaneHelper helper = new GridPaneHelper();
        helper.addLabel("Options: ",1,1);
        CheckBox disabledOptions = helper.addCheckBox("Disable",2,1,true);
        helper.addLabel("Opacity",3,1);
        TextField opacity =  helper.addTextField(4,1);
        opacity.setText("1");

        helper.addLabel("Problem Statement: ",1,2);
        CheckBox disabledProblemStatement = helper.addCheckBox("Disable",2,2,true);
        helper.addLabel("Opacity",3,2);
        TextField ProblemOpacity =  helper.addTextField(4,2);
        ProblemOpacity.setText("1");

        helper.addLabel("Prev Page: ",1,3);
        CheckBox disabledPrevPage = helper.addCheckBox("Disable",2,3,true);
        helper.addLabel("Opacity",3,3);
        TextField prevPageOpacity =  helper.addTextField(4,3);
        prevPageOpacity.setText("1");

        helper.addLabel("Next Page: ",1,4);
        CheckBox disabledNextPage = helper.addCheckBox("Disable",2,4,true);
        helper.addLabel("Opacity",3,4);
        TextField nextPageOpacity =  helper.addTextField(4,4);
        nextPageOpacity.setText("1");

        boolean clickedOk = helper.createGrid("Page Settings", null,"Ok","Cancel");
        if(clickedOk) {
            String targetOptions = ".*options.*";
            String targetProblemStatement = ".*problemStatement.*";
            String targetNextPage = ".*NextPage\".*";
            String targetPrevPage = ".*PrevPage.*";
            String htmlText = htmlSourceCode.getText();
            if(htmlText.contains("NextPageAnswerNames")){
                htmlText =htmlText.replaceFirst(targetOptions,"\\$(\"#options\").prop('disabled',"+disabledOptions.isSelected()+
                        ".css('opacity',"+ opacity.getText()+")");
                htmlText =htmlText.replaceFirst(targetProblemStatement,"\\$(\"#problemStatement\").prop('disabled',"+disabledProblemStatement.isSelected()+
                        ".css('opacity',"+ ProblemOpacity.getText()+")");
                htmlText =htmlText.replaceFirst(targetNextPage,"\\$(\"#NextPage\").prop('disabled',"+disabledNextPage.isSelected()+
                        ".css('opacity',"+ nextPageOpacity.getText()+")");
                htmlText =htmlText.replaceFirst(targetPrevPage,"\\$(\"#PrevPage\").prop('disabled',"+disabledPrevPage.isSelected()+
                        ".css('opacity',"+ prevPageOpacity.getText()+")");
            }
            htmlSourceCode.setText(htmlText);

        }
    }

 //------------------------------ Adding Input Fields -----------------------
    public void addInputFields(boolean isImageField){
        int field;
        field = htmlSourceCode.getCaretPosition();
        if(field<=0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please make sure the cursor in on the HTML editor!");
            alert.setTitle("Message");
            alert.show();
        }else {
            createInputField(field, isImageField);
        }

    }
    public void createInputField(int field, boolean isImageField) {
        GridPaneHelper helper = new GridPaneHelper();
        String[] dropDownList = {"text field","text area","radio","checkbox"};

        helper.addLabel("Question:",0,0);
        helper.addLabel("Input Type:", 1,0);

        TextArea question = helper.addTextArea(0,1);
        ComboBox inputTypeDropDown = helper.addDropDown(dropDownList, 2, 0);
        inputTypeDropDown.setOnAction(event -> {
            setInputType((String) inputTypeDropDown.getValue());
        });


        question.textProperty().bindBidirectional(questionTextProperty());

        helper.addLabel("Answer Key:",0,2);
        helper.addLabel("Input Name:",1,2);
        helper.addLabel("Input Value:",2,2);

         int listSize = page.getVignettePageAnswerFields().getAnswerFieldList().size();

        int size = listSize==0 ? 4 : listSize;
        if(listSize >0){
            for (int i = 1; i <= listSize; i++) {
                addInputFieldsToGridPane(i, helper, true, isImageField);
            }
        }
        else {
            for (int i = 1; i <= size; i++) {
                addInputFieldsToGridPane(i, helper, false, isImageField);
            }
        }
        Boolean clickedOk = helper.createGrid("Input Field ", null, "ok", "Cancel");
        if (clickedOk) {
            htmlSourceCode.insertText(field, addInputFieldToHtmlEditor(isImageField));
            page.setPageData(htmlSourceCode.getText());
            Main.getVignette().getPageViewList().put(page.getPageName(),page);
            inputFieldsList.clear();
        }
    }
    public void addInputFieldsToGridPane(int index, GridPaneHelper helper, Boolean editAnswers, Boolean isImageField){

        InputFields fields = new InputFields();
        TextField answerField = null;
        Button file = null;
        
        if(isImageField){
           file = helper.addButton("File",0,index+2,fileChoose(fields));
        }else {
            answerField = helper.addTextField(0, index + 2);
            answerField.textProperty().bindBidirectional(fields.answerKeyProperty());
            if(editAnswers){
                answerField.setText(page.getVignettePageAnswerFields().getAnswerFieldList().get(index-1).getAnswerKey());
            }
        }
        TextField inputName = helper.addTextField(1,index+2);

        inputName.textProperty().bindBidirectional(fields.inputNameProperty());
        inputName.setText(page.getPageName());

        TextField inputValue = helper.addTextField(2,index+2);
        inputValue.textProperty().bindBidirectional(fields.inputValueProperty());
        if(editAnswers){
            inputValue.setText(page.getVignettePageAnswerFields().getAnswerFieldList().get(index-1).getInputValue());
        }


        fields.setId(index);
        fields.setImageField(isImageField);
        fields.setInputType(getInputType());
        inputFieldsList.add(fields);

       Button add=  helper.addButton("+", 3, index+2, addNewInputFieldToGridPane(helper,isImageField));
       Button remove= helper.addButton("-", 4, index+2);
       remove.setOnAction(removeInputFieldFromGridPane(helper,
               isImageField,
               file,
               answerField,
               inputName,
               inputValue,
               add,
               remove,
               fields,
               index));

    }
    public EventHandler addNewInputFieldToGridPane(GridPaneHelper helper, Boolean isImageField){
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
                addInputFieldsToGridPane(inputFieldsList.size(),helper, false, isImageField);
            }
        };
        return eventHandler;
    }
    public EventHandler removeInputFieldFromGridPane(GridPaneHelper helper,boolean isImageField, Button file,
                                                      TextField answerKey,TextField inputName,
                                                      TextField inputValue,Button add, Button remove, InputFields fields,
                                                     int index){

        return event -> {
            if(isImageField) {
                helper.getGrid().getChildren().removeAll(file, inputName, inputValue, add, remove);
            } else {
                helper.getGrid().getChildren().removeAll(answerKey, inputName, inputValue, add, remove);
            }
            inputFieldsList.remove(fields);
            page.getVignettePageAnswerFields().getAnswerFieldList().remove(index-1);
        };

    }

    public String addInputFieldToHtmlEditor(boolean isImageField){

        String parTag = isImageField? "<p class=\"normTxt\">\n":
                         "<p class=\"normTxt\" style='padding: 0px 15px 0px; text-align:left; width:95%; ' >\n";
        StringBuilder builder = new StringBuilder();
        builder.append("<!-- //////// Question //////// -->");
        builder.append(parTag + questionText.getValue() +" </p> \n");

        page.getVignettePageAnswerFields().setQuestion(questionText.getValue());
        page.getVignettePageAnswerFields().getAnswerFieldList().clear();

        for(int i=0;i< inputFieldsList.size();i++){
            InputFields input = inputFieldsList.get(i);
            inputFieldsList.get(i).setInputType(getInputType());
            builder.append(parTag + inputFieldsList.get(i).toString() +" </p>\n");
            AnswerField answerField = new AnswerField();
            answerField.setAnswerKey(input.getAnswerKey());
            answerField.setInputName(input.getInputName());
            answerField.setInputValue(input.getInputValue());
            page.getVignettePageAnswerFields().setAnswerFieldList(answerField);
        }
        builder.append("<br/>");

        return builder.toString();
    }

    public  EventHandler fileChoose(InputFields fields) {
        final String[] fileName = {null};
        AtomicReference<BufferedImage> image = new AtomicReference<>();
        return event -> {

            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            filterList.add(extFilterJPG);
            filterList.add(extFilterPNG);
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

    public void  connectPages(){
        VignettePage pageOne = Main.getVignette().getPageViewList().get(page.getPageName());
        VignettePage pageTwo = Main.getVignette().getPageViewList().get(defaultNextPage);

        TabPaneController pane = Main.getVignette().getController();
        Button source = pane.getButtonPageMap().get(pageOne.getPageName());
        Button target = pane.getButtonPageMap().get(pageTwo.getPageName());

        pane.checkPageConnection(pageOne,pageTwo,source,target);



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
