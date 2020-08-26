package com.MenuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class FileMenuBar {

    public Menu getFileMenu(){

        Menu fileMenu = new Menu("File");

        MenuItem newVignette = new MenuItem("New Vignette");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        MenuItem saveas = new MenuItem("Save As");
        MenuItem importVignette = new MenuItem("Import");
        MenuItem preferences = new MenuItem("Preferences..");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().add(newVignette);
        fileMenu.getItems().add(open);
        fileMenu.getItems().add(save);
        fileMenu.getItems().add(saveas);
        fileMenu.getItems().add(importVignette);
        fileMenu.getItems().add(preferences);
        fileMenu.getItems().add(exit);

         return  fileMenu;
    }
}
