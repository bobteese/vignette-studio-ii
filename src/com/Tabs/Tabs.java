package com.Tabs;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Tabs {

    private final Image IMAGE_LOGIN  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_PROBLEMSTATEMENT  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));
    private final Image IMAGE_SINGLEPAGE  = new Image(getClass().getResourceAsStream("/resources/images/plain.png"));

    private Image[] listOfImages = {IMAGE_LOGIN, IMAGE_PROBLEMSTATEMENT, IMAGE_SINGLEPAGE};
    public TabPane getTabsPane() {

        TabPane tabPane = new TabPane();

        Tab vignette = setVignetteTab();
        Tab pages = setPageTab();

        tabPane.getTabs().add(vignette);
        tabPane.getTabs().add(pages);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        return tabPane;

    }

    public Tab setVignetteTab() {
        Tab vignette = new Tab("Vignette", new Label(""));

        // vignette pane
        SplitPane splitPane = new SplitPane();
        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList (
                "LOGIN", "PROBLEM_STATEMENT", "SINGLE_PAGE");
        listView.setItems(items);
        listView.setStyle("-fx-background-insets: 0 ;");
        listView.setMaxWidth(100);
        listView.setCellFactory(param -> new ListCell<>() {
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
        listView.prefHeightProperty().bind(Bindings.size(items).multiply(80)); /*set the height based on the items.
                                                                                  Otherwise list view shows additional item rows */
        listView.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/
                System.out.println("onDragDetected");

                /* allow any transfer mode */
                Dragboard db = listView.startDragAndDrop(TransferMode.ANY);

                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString("listView.getText()");
                db.setContent(content);

                event.consume();
            }
        });
        listView.setOnMousePressed(new EventHandler <MouseEvent>()
        {
            public void handle(MouseEvent event)
            {

                System.out.println("Event on Source: mouse pressed");
                event.setDragDetect(true);
            }
        });

        listView.setOnMouseDragged(new EventHandler <MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                System.out.println("Event on Source: mouse dragged");
                event.setDragDetect(false);
            }
        });

        VBox leftControl = new VBox(listView);

        /*Image login = new Image(getClass().getResourceAsStream(
                "/resources/images/plain.png"));
        ImageView imageView = new ImageView(login);

        Image singlePage = new Image(getClass().getResourceAsStream(
                "/resources/images/plain.png"));
        ImageView singleImageView = new ImageView(singlePage);

        Image problemPage = new Image(getClass().getResourceAsStream(
                "/resources/images/plain.png"));
        ImageView problemImageView = new ImageView(problemPage);


        VBox leftControl  = new VBox(imageView, singleImageView,problemImageView);*/
        leftControl.setMaxWidth(100);

        Pane drawPane = new Pane();
        drawPane.setOnMouseDragEntered(new EventHandler <MouseDragEvent>()
        {
            public void handle(MouseDragEvent event)
            {
                System.out.println("Event on Target: mouse dragged");
            }
        });

        drawPane.setOnMouseDragOver(new EventHandler <MouseDragEvent>()
        {
            public void handle(MouseDragEvent event)
            {
                System.out.println("Event on Target: mouse drag over");
            }
        });

        drawPane.setOnMouseDragReleased(new EventHandler <MouseDragEvent>()
        {
            public void handle(MouseDragEvent event)
            {

                System.out.println("Event on Target: mouse drag released");
            }
        });

        drawPane.setOnMouseDragExited(new EventHandler <MouseDragEvent>()
        {
            public void handle(MouseDragEvent event)
            {
                System.out.println("Event on Target: mouse drag exited");
            }
        });
        splitPane.getItems().addAll(leftControl, drawPane);
        splitPane.setPrefSize(600, 800);
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        splitPane.setStyle("-fx-focus-color: transparent;");

        // add tab pane and set border pane
        vignette.setContent(splitPane);

        return vignette;
    }
    public Tab setPageTab() {
        Tab pages = new Tab("Pages"  , new Label(""));

        //page pane
        BorderPane pagePane = new BorderPane();

        pages.setContent(pagePane);

        return pages;
    }
}
