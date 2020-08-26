package com.presentation;

import com.MenuBar.EditMenuBar;
import com.MenuBar.FileMenuBar;
import com.MenuBar.HelpMenuBar;
import com.MenuBar.VignetteMenuBar;
import com.Tabs.Tabs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Pane drawingPane = new Pane();

        BorderPane theBorderPane = new BorderPane();

        drawingPane.setPrefSize(800, 800);
        drawingPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        Tabs tab = new Tabs();

        // classes to get menu bar

        FileMenuBar fileMenuBar =  new FileMenuBar(); // File Menu Options class
        EditMenuBar editMenuBar = new EditMenuBar(); // Edit Menu options class
        VignetteMenuBar vignetteMenuBar = new VignetteMenuBar(); // Vignette Menu options class
        HelpMenuBar helpMenuBar = new HelpMenuBar(); // Help Menu options class


        // adding all items to menubar
        MenuBar menuBar = new MenuBar(); // create a menu bar

        Menu fileMenu = fileMenuBar.getFileMenu(); // get the file menu
        Menu editMenu = editMenuBar.getEditMenu(); // get the edit menu
        Menu vignetteMenu = vignetteMenuBar.getVignetteMenu(); // get the vignette menu
        Menu helpMenu = helpMenuBar.getHelpMenuBar(); // get the vignette menu
        VBox vbox = new VBox(menuBar, tab.getTabsPane());

        menuBar.getMenus().addAll(fileMenu,editMenu,vignetteMenu,helpMenu);
        theBorderPane.setTop(vbox);

        //scroll pane

        ScrollPane scrollPane = new ScrollPane(drawingPane);
        scrollPane.setPrefSize(300, 300);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-focus-color: transparent;");

        // setting scene
        Parent root = FXMLLoader.load(getClass().getResource("vignette.fxml"));
        primaryStage.setTitle("untitled");
        primaryStage.setScene(new Scene(theBorderPane, 600, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
