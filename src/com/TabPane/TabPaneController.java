package com.TabPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.net.URL;
import java.util.ResourceBundle;

public class TabPaneController implements Initializable {

    @FXML
    ListView<String> imageListView;

    private final Image IMAGE_LOGIN  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_PROBLEMSTATEMENT  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));

    private Image[] listOfImages = {IMAGE_LOGIN, IMAGE_PROBLEMSTATEMENT, IMAGE_SINGLEPAGE};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> items = FXCollections.observableArrayList (
                "LOGIN", "PROBLEM_STATEMENT", "SINGLE_PAGE");
        imageListView.setItems(items);
        imageListView.setStyle("-fx-background-insets: 0 ;");
        imageListView.setMaxWidth(100);
        imageListView.setCellFactory(param -> new ListCell<>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                } else {
                    if(name.equals("LOGIN"))
                        imageView.setImage(listOfImages[0]);
                    else if(name.equals("PROBLEM_STATEMENT"))
                        imageView.setImage(listOfImages[1]);
                    else if(name.equals("SINGLE_PAGE"))
                        imageView.setImage(listOfImages[2]);
                    setGraphic(imageView);
                }
            }
        });
        imageListView.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/
                System.out.println("onDragDetected");

                /* allow any transfer mode */
                Dragboard db = imageListView.startDragAndDrop(TransferMode.ANY);

                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString("listView.getText()");
                db.setContent(content);

                event.consume();
            }
        });

    }

    public void imageDropped(DragEvent event) {
        System.out.println("Drag dropped");
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {

            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
    }

    public void imageDragOver(DragEvent event) {
        /* show to the user that it is an actual gesture target */
        if (event.getDragboard().hasString()) {
            System.out.println("onDragEntered");
            // target.setFill(Color.GREEN);
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }
}
