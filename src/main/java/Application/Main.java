/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import RecentFiles.RecentFiles;
import Vignette.Vignette;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

public class Main extends Application {



    private static Main instance;
    final ScrollBar sc = new ScrollBar();
    public static Main getInstance() {
        return instance;
    }
    private static Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Vignette vignette = new Vignette();
    private static RecentFiles recentFiles;
    private Stack<Node> undoStack;
    private Stack<Node> redoStack;





    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
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

                 DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION,"Exit",null,
                                            "Are you sure you want to exit?", false);
                 if (helper.getOk()){
                     primaryStage.close();
                 }
                 else {
                     we.consume();
                 }
            }
        });

        primaryStage.getIcons().add(
                new Image((getClass().getResourceAsStream(ConstantVariables.IMAGE_ICON_RESOURCE_PATH))));

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
    public static RecentFiles getRecentFiles() {
        return recentFiles;
    }

    public void setRecentFiles(RecentFiles recentFiles) {
        this.recentFiles = recentFiles;
    }

    public void addUndoStack(Node node) {
       this.undoStack.push(node);
    }
    public Stack<Node> getUndoStack() {
       return this.undoStack;
    }
    public void addRedoStack(Node node){
        this.redoStack.push(node);
    }
    public Stack<Node> getRedoStack() {
        return redoStack;
    }

}
