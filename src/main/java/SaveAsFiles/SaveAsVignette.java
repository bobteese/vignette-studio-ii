package SaveAsFiles;

import Application.Main;
import ConstantVariables.ConstantVariables;
import GridPaneHelper.GridPaneHelper;
import Vignette.Page.VignettePage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SaveAsVignette {


    public void fileChoose() {

        GridPaneHelper helper = new GridPaneHelper();
        CheckBox checkBox = helper.addCheckBox("Choose the directory for Framework", 0,1, true);
        AtomicReference<File> dirForFramework = new AtomicReference<>();
        checkBox.setOnAction(event -> {
            if(checkBox.isSelected()) {
                final DirectoryChooser directoryChooserForFramework = new DirectoryChooser();
                directoryChooserForFramework.setTitle("Select a Directory for framework");

                // Set Initial Directory
                directoryChooserForFramework.setInitialDirectory(new File(System.getProperty("user.home")));
                dirForFramework.set(directoryChooserForFramework.showDialog(Main.getStage()));
            }
            else {dirForFramework.set(null); }
        });
        TextField text = helper.addTextField(0,2,400);
         boolean isCancled = helper.createGrid("Enter New Vignette name",null,"Save","Cancel");
        if(isCancled) {


            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select a Directory to save the vignette");

            // Set Initial Directory
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File dir = directoryChooser.showDialog(Main.getStage());
            Main.getInstance().changeTitle(text.getText());
            if (dir != null) {
                createFolder(dir,text.getText(), dirForFramework);

            }
        }
    }
    public void createFolder(File dir, String vignetteName, AtomicReference<File> dirForFramework) {
        try {
            String filePath = dir.getAbsolutePath()+"/"+vignetteName;
            Path path = Paths.get(filePath);

            Files.createDirectories(path);

            System.out.println("Directory is created!");
            File frameWorkDir = dirForFramework.get();
            if (frameWorkDir==null) copyResourceFolderFromJar(filePath);
            else {copyFrameworkFolderFromUserPath(frameWorkDir.getPath(), filePath);}
            createHTMLPages(filePath);
            createImageFolder(filePath);

        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to create directory!" + e.getMessage());

        }

    }
    public void createHTMLPages(String destinationPath) {

        HashMap<String, VignettePage> pageViewList = Main.getVignette().getPageViewList();

        Path path = Paths.get(destinationPath+ConstantVariables.PAGE_DIRECTORY);
        BufferedWriter bw = null;
        try {
            Files.createDirectories(path);
            for (Map.Entry mapElement : pageViewList.entrySet()) {
                String fileName = (String) mapElement.getKey();
                VignettePage contents = (VignettePage) mapElement.getValue();
                File file = new File(path.toString() + File.separator + fileName+".html");

                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(contents.getPageData() == null? "": contents.getPageData());
                if(bw!=null)
                    bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void vignetteCourseJsFile() {

    }
    public void createImageFolder(String destinationPath) {

            List<Images> imagesList = Main.getVignette().getImagesList();
            try {
                for (Images img: imagesList) {
                    BufferedImage bi = img.getImage();  // retrieve image
                    String fileName = img.getImageName();
                    File outputfile = new File(destinationPath + File.separator + "Images" + File.separator + fileName);
                    String extension = FilenameUtils.getExtension(fileName);

                    ImageIO.write(bi, extension, outputfile);
                }
            } catch (IOException e) {
                // handle exception

            }


    }
    /* reason for using zip folder for framework is if sometimes the framework folder is taken from the jar file
    *  then the URL will not work for jar. Input stream works hence best way to do this is to zip the folder
    * */
    public void copyResourceFolderFromJar(String destinationPath) throws URISyntaxException, IOException {
        InputStream stream =  getClass().getResourceAsStream(ConstantVariables.FRAMEWORK_RESOURCE_FOLDER);

        byte[] buffer = new byte[1024];
        File out = new File(destinationPath);

        try {

            // create output directory is not exists
            if (!out.exists()) {
                out.mkdir();
            }

            // get the zip file content
            ZipInputStream zis = new ZipInputStream(stream);
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {

                String fileName = ze.getName();
                String removeFrameWorkFolderName = fileName.replaceAll("framework/","");
                File newFile = new File(destinationPath+ File.separator+ removeFrameWorkFolderName);
                if (ze.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    public void copyFrameworkFolderFromUserPath(String sourcePath, String  destinationPath){

         File srcDir = new File(sourcePath);
         File destDir = new File(destinationPath);
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
