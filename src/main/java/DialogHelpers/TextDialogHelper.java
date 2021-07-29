/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package DialogHelpers;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TextDialogHelper extends TextInputDialog {

    private String textAreaValue;

    public TextDialogHelper(String title,String headerText, String defaultText) {
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.textAreaValue = defaultText;
        Optional<String> result = this.showAndWait();
        result.ifPresent(name -> { textAreaValue = name; });
    }

    public TextDialogHelper(String title,String headerText) {
        this.setTitle(title);
        this.setHeaderText(headerText);
        Optional<String> result = this.showAndWait();
        result.ifPresent(name -> { textAreaValue = name; });
    }
    public TextDialogHelper(String title,String headerText, String defaultText) {
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.textAreaValue = defaultText;
        Optional<String> result = this.showAndWait();
        result.ifPresent(name -> { textAreaValue = name; });
    }

    public String getTextAreaValue(){
        return textAreaValue;
    }

}
