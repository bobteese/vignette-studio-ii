package com.DialogHelper;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class DialogHelper extends Alert {

    public DialogHelper(AlertType alertType, String title, String headerText, String contentText, boolean useTextArea) {

        super(alertType);
        this.setTitle(title);
        this.setHeaderText(headerText);
        this.setContentText(contentText);

        if(useTextArea) {
            this.setTitle("");
            TextArea textArea = new TextArea(contentText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 1);


            this.getDialogPane().setExpandableContent(expContent);
        }

        this.showAndWait();
    }

}
