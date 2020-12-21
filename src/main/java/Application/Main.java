/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import DialogHelper.DialogHelper;
import Vignette.Vignette;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {



    private static Main instance;
    final ScrollBar sc = new ScrollBar();
    public static Main getInstance() {
        return instance;
    }
    private static Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Vignette vignette = new Vignette();


    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/application.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("untitled");
        this.primaryStage.setMaximized(true);

        Scene scene =new Scene(root, 600, 800);
        scene.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
        sc.setLayoutX(scene.getWidth()-sc.getWidth());
        sc.setMin(0);
        sc.setOrientation(Orientation.VERTICAL);
        sc.setPrefHeight(180);
        sc.setMax(360);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {

                 DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION,"",null,
                                            "Are you sure you want to exit?", false);
                 if (helper.getOk()){
                     we.consume();
                     primaryStage.close();
                 }
            }
        });

    }

    public static Stage getStage(){
        return primaryStage;
    }
    public void changeTitle(String title){
        this.primaryStage.setTitle(title);
    }
    public static Vignette getVignette() { return vignette; }
    public void setVignette(Vignette vignette) { this.vignette = vignette; }

    public static void main(String[] args) {
        launch(args);
        logger.debug("Hello from Logback");

        logger.debug("getNumber() : {}", 5);

    }

}
