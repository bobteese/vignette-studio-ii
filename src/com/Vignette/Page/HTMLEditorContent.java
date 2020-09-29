package com.Vignette.Page;

import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;

public class HTMLEditorContent {

    HTMLEditor htmlEditor;
    TextArea htmlSourceCode;

    public HTMLEditorContent(HTMLEditor editor, TextArea htmlSourceCode, String type){
        this.htmlEditor = editor;
        this.htmlSourceCode = htmlSourceCode;
    }


}
