package com.MenuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class VignetteMenuBar {

    public Menu getVignetteMenu(){

        Menu vignetteMenu = new Menu("Vignette");

        MenuItem editVignette = new MenuItem("Edit Vignette..");
        MenuItem settings = new MenuItem("Settings..");
        MenuItem styleEditor = new MenuItem("Style Editor");
        MenuItem previewVignette = new MenuItem("Preview Vignette");
        MenuItem stopPreview = new MenuItem("Stop Preview");
        MenuItem startServer = new MenuItem("Start Vignette Server");

        vignetteMenu.getItems().add(editVignette);
        vignetteMenu.getItems().add(settings);
        vignetteMenu.getItems().add(styleEditor);
        vignetteMenu.getItems().add(previewVignette);
        vignetteMenu.getItems().add(stopPreview);
        vignetteMenu.getItems().add(startServer);

        return  vignetteMenu;
    }
}
