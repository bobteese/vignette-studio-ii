package com.Vignette.Page;

import com.ConstantVariables.ConstantVariables;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;

import java.io.*;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTMLEditorContent {

    private HTMLEditor htmlEditor;
    private TextArea htmlSourceCode;
    private String type;


    public HTMLEditorContent(HTMLEditor editor, TextArea htmlSourceCode, String type){
        this.htmlEditor = editor;
        this.htmlSourceCode = htmlSourceCode;
        this.type = type;
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


}
