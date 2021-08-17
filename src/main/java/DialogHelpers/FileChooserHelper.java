/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package DialogHelpers;

import Application.Main;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.List;

public class FileChooserHelper  {
    private String fileChooserTitle;
    public FileChooserHelper(String title) {
        fileChooserTitle = title;
    }

    public File openFileChooser(List<FileChooser.ExtensionFilter> extensionFilterList){

        Main main = new Main();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle(fileChooserTitle);
        for(FileChooser.ExtensionFilter filter: extensionFilterList){
            filechooser.getExtensionFilters().add(filter);
        }
        return filechooser.showOpenDialog(main.getStage());
    }
    public List<File> openMultipleFileChooser(List<FileChooser.ExtensionFilter> extensionFilterList){

        Main main = new Main();
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle(fileChooserTitle);
        for(FileChooser.ExtensionFilter filter: extensionFilterList){
            filechooser.getExtensionFilters().add(filter);
        }
        return filechooser.showOpenMultipleDialog(main.getStage());
    }


}
