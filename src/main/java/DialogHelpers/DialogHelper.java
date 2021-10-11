/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package DialogHelpers;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Optional;

public class DialogHelper extends Alert {



    Boolean ok = false;
    public void setMessage(String contentText){
        this.setContentText(contentText);
    }
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

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = this.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);


        Optional<ButtonType> result = this.showAndWait();


        if(result.isPresent() && result.get() == ButtonType.OK) {
            this.ok = true;
        }

    }

    public Boolean getOk() {
        return ok;
    }


}
