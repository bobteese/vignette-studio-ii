/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import Vignette.Vignette;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static Main instance;
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
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
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
