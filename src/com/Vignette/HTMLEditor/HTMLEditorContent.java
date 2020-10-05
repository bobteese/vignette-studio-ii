package com.Vignette.HTMLEditor;


import com.ConstantVariables.ConstantVariables;
import com.DialogHelper.DialogHelper;
import com.DialogHelper.FileChooserHelper;
import com.GridPaneHelper.GridPaneHelper;
import com.SaveAsFiles.Images;
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


    public HTMLEditorContent(HTMLEditor editor, TextArea htmlSourceCode, String type, Images images){
        this.htmlEditor = editor;
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
        this.images = images;
    }

    public void addTextToEditor() throws URISyntaxException, FileNotFoundException {

         File file = null;
         String text=null;
        InputStream inputStream = null;
        ClassLoader classLoader = getClass().getClassLoader();
        if(type.equals(ConstantVariables.loginPageType))
            inputStream = getClass().getResourceAsStream("/resources/HTMLResources/pages/login.html");

        else if(type.equals(ConstantVariables.singlePageType))
            inputStream = getClass().getResourceAsStream("/resources/HTMLResources/pages/q1.html");

        else if(type.equals((ConstantVariables.problemStatementPageType)))
            inputStream = getClass().getResourceAsStream("/resources/HTMLResources/pages/problemStatement.html");

        text = readFile(inputStream);
        htmlSourceCode.setText(text);
        htmlSourceCode.setOnKeyReleased(event -> {
            htmlEditor.setHtmlText(htmlSourceCode.getText());
        });
        htmlEditor.setHtmlText(text);
        htmlEditor.setOnKeyReleased(event -> {
            htmlSourceCode.setText(htmlEditor.getHtmlText());
        });

    }
    public String readFile(InputStream file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {

            bufferedReader = new BufferedReader(new InputStreamReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
                if(text.contains("<img")){
                    System.out.println(text);
                }
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


}
