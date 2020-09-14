package com.TabPane;

import com.GridPaneHelper.GridPaneHelper;
import com.Vignette.Page.VignettePage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class TabPaneController implements Initializable {

    @FXML
    ListView<String> imageListView;
    @FXML
    AnchorPane rightAnchorPane;

    private final Image IMAGE_LOGIN  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_PROBLEMSTATEMENT  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));

    private Image[] listOfImages = {IMAGE_LOGIN, IMAGE_PROBLEMSTATEMENT, IMAGE_SINGLEPAGE};
    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> items = FXCollections.observableArrayList (
                "LOGIN", "PROBLEM_STATEMENT", "SINGLE_PAGE");
        imageListView.setItems(items);
        imageListView.setStyle("-fx-background-insets: 0 ;");
        imageListView.setMaxWidth(100);

        imageListView.setCellFactory(lv -> {
                    ListCell<String> cell = new ListCell<String>() {
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
                    };

                    cell.setOnDragDetected(event -> {
                        if (!cell.isEmpty()) {
                            Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
                            ClipboardContent cc = new ClipboardContent();
                            cc.putString(cell.getItem());
                            db.setContent(cc);
                            dragSource.set(cell);
                        }
                    });
                    return cell;
        });

    }

    public void imageDropped(DragEvent event) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            String imageType = db.getString();
            Image imageValue = null;
            double posX = event.getSceneX();
            double posY = event.getSceneY();
            switch (imageType){
                case "LOGIN":
                      imageValue= listOfImages[0];
                      break;
                case  "PROBLEM_STATEMENT":
                      imageValue = listOfImages[1];
                      break;
                case  "SINGLE_PAGE":
                       imageValue  = listOfImages[2];
                       break;
            }
            ClipboardContent content = new ClipboardContent();
            content.putString(imageType);
            db.setContent(content);
            DraggableImage droppedView = new DraggableImage(imageValue);

            droppedView.setX(posX);
            droppedView.setY(posY);
            createNewPageDialog();
            this.rightAnchorPane.getChildren().add(droppedView);

            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
    }

    // this is required to get the drag and drop to work
    public void imageDragOver(DragEvent event) {
        /* show to the user that it is an actual gesture target */
        if (event.getDragboard().hasString() || event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }
    public void createNewPageDialog(){
        GridPaneHelper<VignettePage>  newPageDialog = new GridPaneHelper<>();
        newPageDialog.addCheckBox("First Page", 2,1);
        newPageDialog.addTextField(1,2);
        newPageDialog.addLabel("First Page", 1,1);
        newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");

    }



public  static class DraggableImage extends ImageView {
    private double mouseX ;
    private double mouseY ;
    public DraggableImage(Image image) {
        super(image);

        setOnMousePressed(event -> {
            mouseX = event.getSceneX() ;
            mouseY = event.getSceneY() ;
        });

        setOnMouseDragged(event -> {
            double deltaX = event.getSceneX()  ;
            double deltaY = event.getSceneY()  ;
            relocate(deltaX,  deltaY);
        });
    }

}

}
