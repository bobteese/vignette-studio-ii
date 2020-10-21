/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.Application;

import com.Vignette.Vignette;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }
    private static Stage primaryStage;


    private static Vignette vignette = new Vignette();
    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        Parent root = FXMLLoader.load(getClass().getResource("/resources/FXML/application.fxml"));
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("untitled");
        this.primaryStage.setMaximized(true);
        this.primaryStage.setScene(new Scene(root, 600, 800));
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
    }
}
