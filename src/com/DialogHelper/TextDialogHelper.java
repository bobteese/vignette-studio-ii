/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.DialogHelper;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class TextDialogHelper extends TextInputDialog {

    private String textAreaValue;
    public TextDialogHelper(String title,String headerText) {

        this.setTitle(title);
        this.setHeaderText(headerText);
        Optional<String> result = this.showAndWait();
        result.ifPresent(name -> { textAreaValue = name; });
    }

    public String getTextAreaValue(){
        return textAreaValue;
    }

}
