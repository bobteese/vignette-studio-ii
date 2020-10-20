/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.DialogHelper;

import com.Application.Main;
import javafx.stage.FileChooser;

import javax.sound.midi.MidiChannel;
import java.io.File;
import java.util.List;

public class FileChooserHelper  {
    private String fileChooserTitle;
    public FileChooserHelper(String title) {
        fileChooserTitle = title;
    }

    public File openFileChooser(List<FileChooser.ExtensionFilter> extensionFilterList){

        FileChooser filechooser = new FileChooser();
        filechooser.setTitle(fileChooserTitle);
        for(FileChooser.ExtensionFilter filter: extensionFilterList){
            filechooser.getExtensionFilters().add(filter);
        }
        return filechooser.showOpenDialog(Main.getStage());

    }


}
