package Vignette.HTMLEditor;

import DialogHelper.DialogHelper;
import DialogHelper.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import Vignette.Branching.BranchingImpl;
import Vignette.HTMLEditor.InputFields.InputFields;
import Vignette.Page.VignettePage;
import ConstantVariables.ConstantVariables;
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
import java.util.List;

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
    public HTMLEditorContent(TextArea htmlSourceCode, String type, VignettePage page, List<String> pageNameList){
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.page = page;
        this.pageNameList = pageNameList;
        answerChoice= new ArrayList<>();
        answerPage = new ArrayList<>();
        this.branching = new BranchingImpl(this.page);
        inputFieldsList =  new ArrayList<>();
    }

    public String addTextToEditor() throws URISyntaxException, FileNotFoundException {

         String text = null;
        InputStream inputStream = null;

         if(!type.equals(ConstantVariables.CUSTOM_PAGE_TYPE)) {
             inputStream = getClass().getResourceAsStream(ConstantVariables.PAGE_TYPE_LINK_MAP.get(type));
             text = readFile(inputStream);
         }

        htmlSourceCode.setText(text);
        htmlSourceCode.setOnKeyReleased(event -> {

            page.setPageData(htmlSourceCode.getText());

        });

        return text;

    }
    public String readFile(InputStream file){

        String nextPageAnswers = createNextPageAnswersDialog(false);

        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                if(text.contains("NextPageName")) text = "NextPageName=\""+page.getConnectedTo() +"\";";
                if(text.contains("NextPageAnswerNames")) {
                  this.nextPageAnswers = text = "NextPageAnswerNames="+nextPageAnswers+";";
                }

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
    public String createNextPageAnswersDialog(Boolean editNextPageAnswers){
        GridPaneHelper helper = new GridPaneHelper();
        String answerNextPage = "[";
        int size = editNextPageAnswers? answerChoice.size() : 4;
        for(int i =0; i<size;i++) {
            addNextPageTextFieldToGridPane(i,helper, editNextPageAnswers);
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
               addNextPageTextFieldToGridPane(countOfAnswer,helper, false);
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
    public void addNextPageTextFieldToGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers){


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
        }
        else {
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
          htmlText =!nextPageAnswers.equals("[]")?
                         htmlText.replaceFirst(target,"NextPageAnswerNames="+nextPageAnswers+";"):
                   htmlText;
        }
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
//---------------------------BRANCHING---------------------------------------------------
    public void addNoBranchToEditor(){
        String text = this.branching.noBranching();
        htmlSourceCode.insertText(ConstantVariables.INSERT_BRANCHING_AT_INDEX,"\n");
        htmlSourceCode.insertText(ConstantVariables.INSERT_BRANCHING_AT_INDEX,text);
    }
    public void addBranchRadio() {
        String pageAnswers = createNextPageAnswersDialog(false);
        String text = this.branching.branchingRadio(pageAnswers);
        htmlSourceCode.insertText(ConstantVariables.INSERT_BRANCHING_AT_INDEX,"\n");
        htmlSourceCode.insertText(ConstantVariables.INSERT_BRANCHING_AT_INDEX,text);

    }
    public void addInputFields(){
       createInputField();

    }
    public void createInputField() {
        GridPaneHelper helper = new GridPaneHelper();
        helper.addLabel("Input Name",0,0);
        helper.addLabel("Input Value",1,0);
        helper.addLabel("Input Type",2,0);
        String answerNextPage = "[";
        int size = 4;
        for (int i = 0; i < size; i++) {
            addInputFieldsToGridPane(i, helper, false);
        }
        Boolean clickedOk = helper.createGrid("Input Field ", null, "ok", "Cancel");
        if (clickedOk) {

            for (int i = 0; i < answerChoice.size(); i++) {
                if (!answerChoice.get(i).getText().equals(""))
                    answerNextPage += "[ " + "'" + answerChoice.get(i).getText() + "'" + "," + "'" + answerPage.get(i).getValue() + "'" + "],";
            }
            answerNextPage += "]";
        }
    }
    public void addInputFieldsToGridPane(int index, GridPaneHelper helper, Boolean editNextPageAnswers){
        String[] dropDownList = {"text field","text area","radio","checkbox"};

        TextField inputName = helper.addTextField(0,index+1);
        TextField inputValue = helper.addTextField(1,index+1);
        ComboBox inputType = helper.addDropDown(dropDownList, 2, index+1);

        Button add= helper.addButton("+", 3, index+1, addToGridPane(helper));
        Button remove =  helper.addButton("-", 4, index+1);

    }




}
