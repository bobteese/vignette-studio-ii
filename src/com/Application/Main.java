package com.Application;

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
    private Stage primaryStage;
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

    public Stage getStage(){
        return this.primaryStage;
    }
    public void changeTitle(String title){
        this.primaryStage.setTitle(title);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
