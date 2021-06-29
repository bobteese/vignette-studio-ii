package SaveAsFiles;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Logger logger = LoggerFactory.getLogger(SaveAsVignette.class);
    private boolean toSelectDirectory = false;

    /**
     * the function proposes a new solution to create a new folder called VignettePages in the user root directory to avoid creating Vignettes at random location when no
     * directory is selected
     */
    public void fileChoose() {
        GridPaneHelper helper = new GridPaneHelper();
        CheckBox checkBox = helper.addCheckBox("Choose the directory for Framework", 0,1, true);
        AtomicReference<File> dirForFramework = new AtomicReference<>();
        checkBox.setOnAction(event -> {
            if(checkBox.isSelected()) {
                this.toSelectDirectory = true;
            }else{
                this.toSelectDirectory = false;
            }
        });
        TextField text = helper.addTextField(0,2,400);
        text.setText(Main.getVignette().getVignetteName());

         boolean isCancled = helper.createGrid("Enter Vignette name to be saved",null,"Save","Cancel");
         boolean isValid = false;
        if(isCancled) {
           isValid = !text.getText().equals("");
            while(!isValid){
                    String textMs = text.getText();
                    String message = text.getText().equals("")? "Vignette Name Cannot be empty":"";
                    DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                            message,false);
                    if(dialogHelper.getOk()) {isCancled= helper.showDialog(); }
                    isValid =  !textMs.equals("");
                    if(!isCancled) {isValid=false; break;}
            }
            if(isValid) {
                File dir;
                if(this.toSelectDirectory){
                    final DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Select a Directory to save the vignette");
                    directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                    dir = directoryChooser.showDialog(Main.getStage());
                }else{
                    String defaultPath = System.getProperty("user.home") + "/VignettePages";
                    String path = defaultPath.replace("\\", "/");
                    dir = new File(path);
                    if(!dir.exists()){
                        if(dir.mkdir()){
                            System.out.println("Created a default Page directory");
                        }else{
                            System.out.println("Error in creating a directory!");
                        }
                    }else{
                        System.out.println("File already exists");
                    }
                }
                Main.getInstance().changeTitle(text.getText());
                Main.getVignette().setVignetteName(text.getText());
                Main.getVignette().setSaved(true);
                if (dir != null) {
                    //dirForFramework is a null parameter that is set to the path for framework.zip within the function createFolder()
                    createFolder(dir, text.getText(), dirForFramework);
                }
            }
        }
    }
    public void createFolder(File dir, String vignetteName, AtomicReference<File> dirForFramework) {
        try {
            String filePath = dir.getAbsolutePath()+"/"+vignetteName;
            Path path = Paths.get(filePath);
            Main.getVignette().setFolderPath(filePath);
            Files.createDirectories(path);

            System.out.println("Directory is created!");
            File frameWorkDir = dirForFramework.get();
            System.out.println("DIR ABS: "+dir.getAbsolutePath());
            if (frameWorkDir==null) copyResourceFolderFromJar(filePath);
            else {copyFrameworkFolderFromUserPath(frameWorkDir.getPath(), filePath);}
            createHTMLPages(filePath);
            createImageFolder(filePath);
            vignetteCourseJsFile(filePath);
            saveCSSFile(filePath);
            saveVignetteClass(filePath,vignetteName);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            logger.error("{Failed to create directory}", e);
            System.err.println("Failed to create directory!" + e.getMessage());

        }

    }
    public void createHTMLPages(String destinationPath){

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
                else{
                    file.delete();
                    file.createNewFile();
                }
                FileWriter fw = null;

                try {
                    fw = new FileWriter(file, false);
                    bw = new BufferedWriter(fw);
                    bw.write(contents.getPageData() == null? "": contents.getPageData());
                }
               catch (IOException e){
                }finally {
                    if(bw!=null) {
                        bw.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("{Create HTML Pages }", e);
            System.err.println("Create HTML Pages !" + e.getMessage());
        }
    }
    public void vignetteCourseJsFile(String destinationPath) {

        BufferedWriter bw = null;
        try {

                File file = new File(destinationPath+ File.separator + ConstantVariables.DATA_DIRECTORY+File.separator
                                   +ConstantVariables.VIGNETTE_SETTING);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = null;
            try {
                fw = new FileWriter(file, false);
                bw = new BufferedWriter(fw);
                VignetteSettings settings =Main.getVignette().getSettings();
                String content = settings== null?new VignetteSettings().createSettingsJS() : settings.createSettingsJS() ;
                bw.write(content);
             }
            catch (IOException e){

            }finally {
                if(bw!=null) {
                    bw.flush();
                }
            }
        }
         catch (IOException e) {
             logger.error("{Create JS Pages }", e);
             e.printStackTrace();
             System.err.println("Create JS Pages " + e.getMessage());
            }

    }
    public void saveCSSFile(String destinationPath){
        BufferedWriter bw = null;
        String  css =Main.getVignette().getCssEditorText();
        try {
           if(null != css) {
               File file = new File(destinationPath + ConstantVariables.CSS_DIRECTORY);

               if (!file.exists()) {
                   file.createNewFile();
               } else {
                   file.delete();
                   file.createNewFile();
               }
               FileWriter fw = new FileWriter(file, false);
               bw = new BufferedWriter(fw);
               bw.write(css);
               if (bw != null)
                   bw.close();
           }
        }
        catch (IOException e) {
            logger.error("{Save CSS File }", e);
            e.printStackTrace();
            System.err.println("Save CSS File" + e.getMessage());
        }

    }
    public void createImageFolder(String destinationPath) {

            List<Images> imagesList = Main.getVignette().getImagesList();
            try {
                for (Images img: imagesList) {
                    if(img!=null){
                        BufferedImage bi = img.getImage();  // retrieve image
                        String fileName = img.getImageName();
                        File outputfile = new File(destinationPath + File.separator + "Images" + File.separator + fileName);
                        String extension = FilenameUtils.getExtension(fileName);
                        System.out.println("IMAGE FILE: "+img);
                        ImageIO.write(bi, extension, outputfile);
                    }
                }
            } catch (IOException e) {
                logger.error("{Create Image Folder }", e);
                e.printStackTrace();
                System.err.println("Create Image Fodler" + e.getMessage());

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
            logger.error("{Resource Folder from Jar }", ex);
            ex.printStackTrace();
            System.err.println("Resource Folder from Jar" + ex.getMessage());
        }

    }
    public void copyFrameworkFolderFromUserPath(String sourcePath, String  destinationPath){

         File srcDir = new File(sourcePath);
         File destDir = new File(destinationPath);
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException e) {
            logger.error("{Resource Folder from User Path }", e);
            e.printStackTrace();
            System.err.println("Resource Folder from User Path " + e.getMessage());
        }
    }
    public void saveVignetteClass(String destinationPath,String vignetteName){
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(destinationPath+File.separator+vignetteName+".vgn");
        } catch (FileNotFoundException e) {
            logger.error("{Save Vignette Class file stream }", e);
            e.printStackTrace();
            System.err.println("Save Vignette Class file stream " + e.getMessage());
        }
        ObjectOutputStream objectOut = null;
        try {
            objectOut = new ObjectOutputStream(fileOut);
            System.out.println(destinationPath+File.separator+vignetteName+".vgn");
        } catch (IOException e) {
            logger.error("{Save Vignette Class  object output stream}", e);
            e.printStackTrace();
            System.err.println("Save Vignette Class  object output stream" + e.getMessage());
        }
        try {
            objectOut.writeObject(Main.getVignette());
        } catch (IOException e) {
            logger.error("{Save Vignette Class  object write output stream}", e);
            e.printStackTrace();
            System.err.println("Save Vignette Class  object write output stream" + e.getMessage());
        }
        try {
            fileOut.close();
            objectOut.close();

        } catch (IOException e) {
            logger.error("{Save Vignette Class IOException}", e);
            e.printStackTrace();
            System.err.println("Save Vignette Class IOException" + e.getMessage());

        }
        System.out.println("The Object  was succesfully written to a file");
    }



}
