/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import RecentFiles.RecentFiles;
import TabPane.TabPaneController;
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
    private static Vignette vignette;
    private static RecentFiles recentFiles;
    private Stack<Node> undoStack;
    private Stack<Node> redoStack;

    public static Vignette anotherVignetteInstance(){
        return(new Vignette());
    }
    public static void setVignette(Vignette v){
        Main.vignette = v;
    }
    /**
     * Main entry point for the JavaFX application. User interface defined by means of a stage and scene. Stage is the
     * top level container. Scene is the container for all content.
     * Contains an undo and redo stack for all page related operations.
     * Loads object hierarchy from application.fxml.
     * application.fxml includes menu.fxml and tabs.fxml which are all elements on the main window of the vignette studio
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        this.vignette = anotherVignetteInstance();
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
                 DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION,"Exit",null,"Are you sure you want to exit?", false);
                 if (helper.getOk()){
                     primaryStage.close();
                 }else {
                     we.consume();
                 }
            }
        });
        primaryStage.getIcons().add(new Image((getClass().getResourceAsStream(ConstantVariables.IMAGE_ICON_RESOURCE_PATH))));
    }
    /**
     * Returns the stage of the JavaFX application.
     * @return primaryStage
     */
    public static Stage getStage(){
        return primaryStage;
    }

    /**
     * Changes the title of the vignette
     * @param title title to be changed to
     */
    public void changeTitle(String title){
        this.primaryStage.setTitle(title);
    }

    /**
     * Getter for the vignette
     * @return vignette
     */
    public static Vignette getVignette() { return vignette; }

    /**
     * Setter for the vignette
     * @param vignette
     */
//    public void setVignette(Vignette vignette) { this.vignette = vignette; }


    /**
     * Instance of the application is created on the JavaFX thread.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
        logger.debug("Hello from Logback");

        logger.debug("getNumber() : {}", 5);

    }

    /**
     * Returns the ArrayDeque of recent vignette files.
     * Used in setPreferences() of File.FileMenuItem.java when the user sets their preferred number of previous files to be displayed.
     * @return recentFiles ArrayDeque of recentfiles
     */
    public static RecentFiles getRecentFiles() {
        return recentFiles;
    }

    /**
     * Used to set the recentFiles in the MenuBarController
     * @param recentFiles
     */
    public void setRecentFiles(RecentFiles recentFiles) {
        this.recentFiles = recentFiles;
    }

    /**
     *
     * @param node
     */
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
