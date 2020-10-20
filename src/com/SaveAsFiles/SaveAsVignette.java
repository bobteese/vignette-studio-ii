package com.SaveAsFiles;

import com.Application.Main;
import com.DialogHelper.FileChooserHelper;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveAsVignette {


    public void fileChoose() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Directory");

        // Set Initial Directory
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dir = directoryChooser.showDialog(Main.getStage());
        if (dir != null) {

        } else {

        }
    }
    public void createFolder() {

    }
    public void createHTMLPages() {

    }
    public void vignetteCoureseJsFile() {

    }
    public void createImageFolder() {

    }
    public void copyResourceFolder(){

    }



}
