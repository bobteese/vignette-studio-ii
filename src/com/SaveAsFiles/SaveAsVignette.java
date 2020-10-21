package com.SaveAsFiles;

import com.Application.Main;
import com.GridPaneHelper.GridPaneHelper;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveAsVignette {


    public void fileChoose() {

        GridPaneHelper helper = new GridPaneHelper();
        TextField text = helper.addTextField(0,1,400);

         boolean isCancled = helper.createGrid("Enter New Vignette name",null,"Save","Cancel");
        if(isCancled) {


            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a Directory");

            // Set Initial Directory
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File dir = directoryChooser.showDialog(Main.getStage());
            if (dir != null) {
                createFolder(dir,text.getText());

            } else {

            }
        }
    }
    public void createFolder(File dir, String vignetteName) {
        try {
            String filePath = dir.getAbsolutePath()+"/"+vignetteName;
            Path path = Paths.get(filePath);

            //java.nio.file.Files;
            Files.createDirectories(path);

            System.out.println("Directory is created!");
            copyResourceFolder(filePath);

        } catch (IOException e) {

            System.err.println("Failed to create directory!" + e.getMessage());

        }

    }
    public void createHTMLPages() {

    }
    public void vignetteCoureseJsFile() {

    }
    public void createImageFolder() {

    }
    public void copyResourceFolder(String destinationPath){
        String source = "C:/your/source";
        File srcDir = new File(source);

        String destination = "C:/your/destination";
        File destDir = new File(destination);

//        try {
//            FileUtils.copyDirectory(srcDir, destDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }



}
