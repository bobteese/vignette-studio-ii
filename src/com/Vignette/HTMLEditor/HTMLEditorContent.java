package com.Vignette.HTMLEditor;


import com.ConstantVariables.ConstantVariables;
import com.DialogHelper.DialogHelper;
import com.DialogHelper.FileChooserHelper;
import com.GridPaneHelper.GridPaneHelper;
import com.SaveAsFiles.Images;
import com.Vignette.Page.VignettePage;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTMLEditorContent {

    private HTMLEditor htmlEditor;
    private TextArea htmlSourceCode;
    private String type;
    private Images images;
    private VignettePage page;
    private int countOfAnswer;
    private List<String> pageNameList;
    private  List<TextField> answerChoice;
    private List<ComboBox> answerPage;
    String nextPageAnswers ;

    public HTMLEditorContent(HTMLEditor editor, TextArea htmlSourceCode, String type, Images images, VignettePage page, List<String> pageNameList){
        this.htmlEditor = editor;
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.images = images;
        this.page = page;
        this.pageNameList = pageNameList;
        answerChoice= new ArrayList<>();
        answerPage = new ArrayList<>();
    }

    public void addTextToEditor() throws URISyntaxException, FileNotFoundException {

         File file = null;
         String text=null;
        InputStream inputStream = null;
        ClassLoader classLoader = getClass().getClassLoader();
        if(type.equals(ConstantVariables.LOGIN_PAGE_TYPE))
            inputStream = getClass().getResourceAsStream(ConstantVariables.LOGIN_HTML_SOURCE_PAGE);

        else if(type.equals(ConstantVariables.QUESTION_PAGE_TYPE))
            inputStream = getClass().getResourceAsStream(ConstantVariables.Q1_HTML_SOURCE_PAGE);

        else if(type.equals((ConstantVariables.PROBLEM_STATEMENT_PAGE)))
            inputStream = getClass().getResourceAsStream(ConstantVariables.PROBLEM_STATEMENT_HTML_SOURCE_PAGE);
        else if(type.equals((ConstantVariables.PROBLEM_PAGE_TYPE)))
            inputStream = getClass().getResourceAsStream(ConstantVariables.PROBLEM_HTML_SOURCE_PAGE);
        else if(type.equals((ConstantVariables.COMPLETION_PAGE_TYPE)))
            inputStream = getClass().getResourceAsStream(ConstantVariables.COMPLETION_HTML_SOURCE_PAGE);
        else if(type.equals((ConstantVariables.RESPONSE_CORRECT_PAGE)))
            inputStream = getClass().getResourceAsStream(ConstantVariables.RESPONSE_CORRECT_HTML_SOURCE_PAGE);
        else if(type.equals((ConstantVariables.RESPONSE_INCORRECT_PAGE)))
            inputStream = getClass().getResourceAsStream(ConstantVariables.RESPONSE_INCORRECT_SOURCE_PAGE);
        else if(type.equals((ConstantVariables.WHAT_LEARNED_PAGE)))
            inputStream = getClass().getResourceAsStream(ConstantVariables.WHAT_LEARNED_HTML_SOURCE_PAGE);

        text = readFile(inputStream);
        htmlSourceCode.setText(text);
        htmlSourceCode.setOnKeyReleased(event -> {
            htmlEditor.setHtmlText(htmlSourceCode.getText());
            page.setPageData(htmlSourceCode.getText());

        });
        htmlEditor.setHtmlText(text);
        htmlEditor.setOnKeyReleased(event -> {
            htmlSourceCode.setText(htmlEditor.getHtmlText());
            page.setPageData(htmlEditor.getHtmlText());
        });

    }
    public String readFile(InputStream file){

        String nextPageAnswers = createNextPageAnswersDialog(false);
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                if(text.contains("NextPageName")) text = "NextPageName="+page.getConnectedTo() +";";
                if(text.contains("NextPageAnswerNames")) {
                  this.nextPageAnswers = text = "NextPageAnswerNames="+nextPageAnswers+";";
                }

                stringBuffer.append(text);
                stringBuffer.append("\n");
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger("HTML Editor class").log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger("HTML Editor class").log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException exp) {
                Logger.getLogger("HTML Editor class").log(Level.SEVERE, null, exp);
            }
        }

        return stringBuffer.toString();
    }

    public void setText(String text){

        htmlEditor.setHtmlText(text);
        htmlSourceCode.setText(text);
    }
    public void addImageTag(){


      GridPaneHelper helper = new GridPaneHelper();
      helper.addLabel("Choose File:" ,1,1);
        final String[] fileName = {null};
        System.out.println(fileName.length+" "+ fileName[0]);
        Label label = helper.addLabel("",3,1);
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
                    System.out.println(fileName[0]);
                    label.setText(fileName[0]);
                    try {
                        images.setListOfImages(fileName[0], ImageIO.read(file ) );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
      Button addImageButton =  helper.addButton("File",2,1,eventHandler);

      helper.addLabel("Width of Image",1,2);
      TextField widthofImage = helper.addTextField(2,2);
      helper.addLabel("Image Class Name",1,3);
      TextField className = helper.addTextField(2,3);
      boolean clicked = helper.createGrid("Image",null,"Ok","Cancel");
      boolean isValid = false;
      if(clicked) {
          while (!isValid){

              String message =fileName.length>0 && fileName[0] == null? "File Name Cannot be empty":"";
              DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                      message,false);
              if(dialogHelper.getOk()) { helper.showDialog(); }
              isValid = fileName[0] != null;

          }
          int field;
          field = htmlSourceCode.getCaretPosition();
          System.out.println(field);
          String imageText = "<img class=\""+className.getText()+"\" width=\""+widthofImage.getText()+"%\" " +
                             "src=\""+ConstantVariables.imageResourceFolder+fileName[0]+ "\" alt=\"IMG_DESCRIPTION\" >\n";
          htmlSourceCode.insertText(field, imageText);
          htmlEditor.setHtmlText(htmlSourceCode.getText());
      }

    }
    public String createNextPageAnswersDialog(Boolean editNextPageAnswers){
        GridPaneHelper helper = new GridPaneHelper();
        String answerNextPage = "[";
        int size = editNextPageAnswers? answerChoice.size() : 4;
        for(int i =0; i<size;i++) {
            addTextFieldGridPane(i,helper, editNextPageAnswers);
        }
        Boolean clickedOk = helper.createGrid("Next Answer Page ",null, "ok","Cancel");
        if(clickedOk){

            for(int i =0;i<answerChoice.size();i++){
                if(!answerChoice.get(i).getText().equals(""))
                   answerNextPage += "[ "+"'"+answerChoice.get(i).getText()+"'"+ "," + "'"+answerPage.get(i).getValue()+"'" + "],";
            }
            answerNextPage+="]";
            return answerNextPage;

        }
        return "[]";
    }
    public EventHandler addToGridPane(GridPaneHelper helper){
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void handle(Event event) {
               addTextFieldGridPane(countOfAnswer,helper, false);
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
    public void addTextFieldGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers){


        if(!editNextPageAnswers) {
            TextField text = helper.addTextField(0, index);
            String[] pageList = pageNameList.toArray(new String[0]);
            ComboBox dropdown = helper.addDropDown(pageList, 1, index);
            Button add= helper.addButton("+", 2, index, addToGridPane(helper));
            Button remove =  helper.addButton("-", 3, index);
            remove.setOnAction(removeFromGridPane(helper,text,dropdown,add,remove));
            countOfAnswer++;
            answerChoice.add(text);
            answerPage.add(dropdown);
        }else {
           helper.addExistingTextField(answerChoice.get(index),0,index);
           helper.addExistingDropDownField(answerPage.get(index),1,index);
           Button add= helper.addButton("+", 2, index, addToGridPane(helper));
           Button remove =  helper.addButton("-", 3, index);
           remove.setOnAction(removeFromGridPane(helper,answerChoice.get(index),answerPage.get(index),add,remove));

        }

    }
    public void editNextPageAnswers(){
        String nextPageAnswers = createNextPageAnswersDialog(true);
        String htmlText = htmlSourceCode.getText();
        String target = ".*NextPageAnswerNames.*";
        if(htmlText.contains("NextPageAnswerNames")){
          htmlText =htmlText.replaceFirst(target,"NextPageAnswerNames="+nextPageAnswers+";");
        }
        htmlSourceCode.setText(htmlText);
        htmlEditor.setHtmlText(htmlText);
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

        /* $("#options").prop('disabled', false).css('opacity', 1);
            $("#problemStatement").prop('disabled', false).css('opacity', 1);
            $("#PrevPage").prop('disabled', false).css('opacity', 1);
            $("#NextPage").prop('disabled', false).css('opacity', 1);
         */
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
            htmlEditor.setHtmlText(htmlText);
        }
    }


}
