/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.TabPane;

import com.DialogHelper.DialogHelper;
import com.GridPaneHelper.GridPaneHelper;
import com.Vignette.Page.PageMenu;
import com.Vignette.Page.VignettePage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/** @author Asmita Hari
 * This class is used to initilaze the left panel of list of images
 *  ,handles the drag and drop functionality and creates a new vignette page for each image dropped
 * **/
public class TabPaneController implements Initializable {

    @FXML
    ListView<String> imageListView; // list of image view for the left panel
    @FXML
    AnchorPane rightAnchorPane; // the right anchor pane where the images are dropped
    @FXML
    Tab pagesTab;
    @FXML
    TabPane tabPane;

    // image sources
    private final Image IMAGE_LOGIN  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_PROBLEMSTATEMENT  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));


    private Image[] listOfImages = {IMAGE_LOGIN, IMAGE_PROBLEMSTATEMENT, IMAGE_SINGLEPAGE};
    private final ObjectProperty<ListCell<String>> dragSource = new SimpleObjectProperty<>();

    private List<String> pageNameList = new ArrayList<String>();
    private int firstPageCount = 0;

    /**
     * This method initialize the list when the controller loads
     * **/
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
                            String ListViewVal = null;
                            if (empty) {
                            } else {
                                if(name.equals("LOGIN")) {
                                    ListViewVal = "Login";
                                    imageView.setImage(listOfImages[0]);
                                }
                                else if(name.equals("PROBLEM_STATEMENT")) {
                                    ListViewVal = "Problem Statement";
                                    imageView.setImage(listOfImages[1]);
                                }
                                else if(name.equals("SINGLE_PAGE")) {
                                    ListViewVal = "Single Page";
                                    imageView.setImage(listOfImages[2]);
                                }
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
    /**
     * This method is called when an image is dropped into the anchor pane.
     * the method is called in resources/FXML tabs.fxml
     * ***/
    public void imageDropped(DragEvent event) {
        /* data dropped */
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) { // if the dragboard has text accept it
            String imageType = db.getString();
            Image imageValue = null;
            double posX = event.getSceneX();
            double posY = event.getSceneY();
            switch (imageType){ // checks for the type of the image and assigns the image source
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

            ClipboardContent content = new ClipboardContent(); // put the type of the image in clipboard
            content.putString(imageType);
            db.setContent(content); // set the content in the dragboard
            ImageView droppedView = new ImageView(imageValue); // create a new image view

            VignettePage page = createNewPageDialog(); /* when an image is dropped create a dialog pane to accept user
                                                               input for the page name */

            // add the dropped node to the anchor pane. Here a button is added with image and text.
            Button pageViewButton = createVignetteButton(page,droppedView,posX,posY);
            if(pageViewButton!=null) {
                this.rightAnchorPane.getChildren().add(pageViewButton);
            }

            success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }
    /**
     * This method is required to get the drag and drop to work as it accepts the incoming drag from another node
     * the method is called in resources/FXML tabs.fxml
     * ***/
    public void imageDragOver(DragEvent event) {
        /* show to the user that it is an actual gesture target */
        if (event.getDragboard().hasString() || event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
    }

    /**
     * This method creates a new dialog pane by accepting the input from the user which is the page name
     *
     * ***/
    public VignettePage createNewPageDialog(){
        GridPaneHelper  newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = firstPageCount > 0? true: false;
        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        TextField pageName = newPageDialog.addTextField(1,2, 400);
        newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");
        // if page ids exists  or if the text is empty
        if(pageNameList.contains(pageName.getText()) || pageName.getText().length() == 0){
            String message = pageName.getText().length() == 0? "Page id should not be empty"
                                                              :" All page id must be unique";
           DialogHelper helper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                                       message,false);
           if(helper.getOk()) { newPageDialog.showDialog();}

        }
        boolean check = checkBox.isSelected();
        if(check){ firstPageCount++;}
        VignettePage page = new VignettePage(pageName.getText(), check);
        pageNameList.add(pageName.getText());

        return page;
    }
    /**
     * This method creates a vignette button on dropped
     * @Params page  creates a vignette page class for each page
     * @Param droppedView Image view
     * @param posX contains the mouse position
     * @param posY contains the mouse position
     * **/
    public Button createVignetteButton(VignettePage page, ImageView droppedView, double posX, double posY){

        Button vignettePageButton = new Button(page.getPageName(), droppedView);
        vignettePageButton.setLayoutX(posX); // setting the button position at the position where image is dropped
        vignettePageButton.setLayoutY(posY);

        final double[] delatX = new double[1]; // used when the image is dragged to a different position
        final double[] deltaY = new double[1];

        vignettePageButton.setAlignment(Pos.CENTER); // center the text
        vignettePageButton.setTextAlignment(TextAlignment.CENTER);
        vignettePageButton.setContentDisplay(ContentDisplay.CENTER);
        vignettePageButton.setWrapText(true); // wrap to reduce white space
        //----- start of mouse event methods----------
        vignettePageButton.setOnMousePressed(mouseEvent -> {
                delatX[0] = vignettePageButton.getLayoutX() - mouseEvent.getSceneX();
                deltaY[0] = vignettePageButton.getLayoutY() - mouseEvent.getSceneY();
                vignettePageButton.setCursor(Cursor.MOVE);
        });
        vignettePageButton.setOnMouseReleased(mouseEvent -> {
                vignettePageButton.setCursor(Cursor.HAND);
        });
        vignettePageButton.setOnMouseDragged(mouseEvent -> {

            vignettePageButton.setLayoutX(mouseEvent.getSceneX() + delatX[0]); // set it to mew postion
            vignettePageButton.setLayoutY(mouseEvent.getSceneY() + deltaY[0] );

        });
        vignettePageButton.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    pagesTab.setDisable(false);
                    tabPane.getSelectionModel().select(pagesTab);
                }
            }
            if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                PageMenu pageMenu = new PageMenu(page, vignettePageButton, this);
                vignettePageButton.setContextMenu(pageMenu);
            }
        });
        vignettePageButton.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.DELETE)){
                DialogHelper confirmation = new DialogHelper(Alert.AlertType.CONFIRMATION,
                                                        "Delete Page",
                                                    null,
                                                   "Are you sure you want to delete this page?",
                                                   false);
                if(confirmation.getOk()) {
                    this.rightAnchorPane.getChildren().remove(vignettePageButton);
                }
            }
        });
        // -------end of mouse event methods-------
        return vignettePageButton;
    }
    public List<String> getPageNameList() {
        return pageNameList;
    }

    public void setPageNameList(List<String> pageNameList) {
        this.pageNameList = pageNameList;
    }

    public int getFirstPageCount() {
        return firstPageCount;
    }

    public void setFirstPageCount(int firstPageCount) {
        this.firstPageCount = firstPageCount;
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
