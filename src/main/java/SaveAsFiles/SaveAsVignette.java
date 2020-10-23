package SaveAsFiles;

import Application.Main;
import ConstantVariables.ConstantVariables;
import GridPaneHelper.GridPaneHelper;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
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

        } catch (IOException | URISyntaxException e) {

            System.err.println("Failed to create directory!" + e.getMessage());

        }

    }
    public void createHTMLPages() {

    }
    public void vignetteCoureseJsFile() {

    }
    public void createImageFolder() {

    }
    public void copyResourceFolder(String destinationPath) throws URISyntaxException {
        // URI source =getClass().getResource(ConstantVariables.FRAMEWORK_RESOURCE_FOLDER).toURI();
       // File srcDir = new File(getClass().getResource(ConstantVariables.FRAMEWORK_RESOURCE_FOLDER).toExternalForm());

        InputStream srcDir  = getClass().getResourceAsStream(ConstantVariables.FRAMEWORK_RESOURCE_FOLDER);

        String destination = destinationPath;
        File destDir = new File(destination);

        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
