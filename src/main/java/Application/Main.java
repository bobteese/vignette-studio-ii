/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import Preview.VignetteServerException;
import Preview.VignetterServer;
import RecentFiles.RecentFiles;
import TabPane.TabPaneController;
import Vignette.Framework.Framework;
import Vignette.Framework.ReadFramework;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.basic.BasicButtonUI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Main extends Application {



    private static Main instance;
    final ScrollBar sc = new ScrollBar();

    public static Main getInstance() {
        return instance;
    }


    @FXML
    Button chooseYourOwn;

    @FXML
    Button useDefault;


    private static Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Vignette vignette;
    private static RecentFiles recentFiles;

    public static String getFrameworkZipFile() {
        return frameworkZipFile;
    }

    public static void setFrameworkZipFile(String frameworkZipFile) {
        Main.frameworkZipFile = frameworkZipFile;
    }

    private static String frameworkZipFile;
    //todo I added this
    private VignettePage currentVignettePage;


    public static Vignette anotherVignetteInstance() {
        return (new Vignette());
    }

    public static void setVignette(Vignette v) {
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
    public void start(Stage primaryStage) throws Exception {
        GridPaneHelper helper = new GridPaneHelper();
        TextField text = helper.addTextField(0, 2, 400);


        //Create the landing page.
        Parent root1 = FXMLLoader.load(getClass().getResource("/FXML/Home.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Vignette Studio 2");


        this.primaryStage.setMaximized(false);
        this.primaryStage.resizableProperty().setValue(false);

        Scene scene1 = new Scene(root1);
        scene1.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());

        sc.setLayoutX(scene1.getWidth() - sc.getWidth());
        sc.setMin(0);
        sc.setOrientation(Orientation.VERTICAL);
        sc.setPrefHeight(180);
        sc.setMax(360);
        this.primaryStage.setScene(scene1);
        this.primaryStage.show();
    }


    public void chooseDirectory() throws IOException {
        File dir;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("zip files", "*.zip"));
        fileChooser.setTitle("Select a Directory from the vignette");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = fileChooser.showOpenDialog(Main.getStage());
        String dirName = "";
        if(dir!=null){
            Main.setFrameworkZipFile(dir.getAbsolutePath());
            dirName = dir.getName().substring(0, dir.getName().lastIndexOf("."));
        }else{
            Main.setFrameworkZipFile(System.getProperty("user.dir") + "/src/main/resources/HTMLResources/framework.zip");
            dirName = "framework";
        }
        ReadFramework.unZipTheFrameWorkFile(Main.getFrameworkZipFile());
        instance = this;
        this.vignette = anotherVignetteInstance();
        Random random = new Random();
        Framework f = new Framework(Main.getFrameworkZipFile(), dirName, Math.abs(random.nextLong()));
        if(!f.addToFrameworkVersionFile()){
            ArrayList<Framework> listOfFrameworks = ReadFramework.readFrameworkVersionFile();
            for(Framework framework : listOfFrameworks){
                if(framework.getFrameworkName().equalsIgnoreCase(f.getFrameworkName())){
                    System.out.println("");
                    Main.getVignette().setFrameworkInformation(framework);
                    break;
                }
            }
        }else{
            Main.getVignette().setFrameworkInformation(f);
        }
        //closing the landing page
        primaryStage.setMaximized(true);
        primaryStage.close();
        openEditor();
    }



    public void openEditor() throws IOException {
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.close();
        instance = this;
        this.vignette = anotherVignetteInstance();
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/application.fxml"));
        primaryStage.setTitle("untitled");
        primaryStage.setMaximized(true);
        Scene scene = new Scene(root,bounds.getWidth(), bounds.getHeight());
        scene.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
        sc.setLayoutX(scene.getWidth() - sc.getWidth());
        sc.setMin(0);
        sc.setOrientation(Orientation.VERTICAL);
        sc.setPrefHeight(180);
        sc.setMax(360);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Exit", null, "Are you sure you want to exit?", false);
                if (helper.getOk()) {
                    try{
                        Main.getVignette().stopPreviewVignette();
                        try {
                            ReadFramework.deleteDirectory(ReadFramework.getUnzippedFrameWorkDirectory());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }catch (VignetteServerException e){
                        System.out.println("ERROR TO STOP: "+e.getMessage());
                    }
                    primaryStage.close();
                } else {
                    we.consume();
                }
            }
        });
        primaryStage.getIcons().add(new Image((getClass().getResourceAsStream(ConstantVariables.IMAGE_ICON_RESOURCE_PATH))));
    }



    /**
     * Returns the stage of the JavaFX application.
     *
     * @return primaryStage
     */
    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * Changes the title of the vignette
     *
     * @param title title to be changed to
     */
    public void changeTitle(String title) {
        this.primaryStage.setTitle(title);
    }

    /**
     * Getter for the vignette
     *
     * @return vignette
     */
    public static Vignette getVignette() {
        return vignette;
    }

    /**
     * Setter for the vignette
     * @param vignette
     */
//    public void setVignette(Vignette vignette) { this.vignette = vignette; }


    /**
     * Instance of the application is created on the JavaFX thread.
     *
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
     *
     * @return recentFiles ArrayDeque of recentfiles
     */
    public static RecentFiles getRecentFiles() {
        return recentFiles;
    }

    /**
     * Used to set the recentFiles in the MenuBarController
     *
     * @param recentFiles
     */
    public void setRecentFiles(RecentFiles recentFiles) {
        this.recentFiles = recentFiles;
    }






    /**
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
     */
}
