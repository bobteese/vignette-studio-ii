/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.DialogHelper;

import com.Application.Main;
import javafx.stage.FileChooser;

public class FileChooserHelper  {
    private String fileChooserTitle;
    public FileChooserHelper(String title) {
        fileChooserTitle = title;
    }

    public void openFileChooser(){

        Main main = new Main();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle(fileChooserTitle);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Vignette file (*.vgn)", "*.vgn");
        filechooser.getExtensionFilters().add(extFilter);
        filechooser.showOpenDialog(main.getStage());

    }


}
