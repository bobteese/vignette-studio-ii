package SaveAsFiles;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SaveAsVignette {
    private Logger logger = LoggerFactory.getLogger(SaveAsVignette.class);
    private boolean toSelectDirectory = false;

    /**
     * the function proposes a new solution to create a new folder called VignettePages in the user root directory to avoid creating Vignettes at random location when no
     * directory is selected
     */
    public boolean fileChoose() {
        GridPaneHelper helper = new GridPaneHelper();
        CheckBox checkBox = new CheckBox("Choose the directory to save vignette");
        checkBox.setSelected(true);
        AtomicReference<File> dirForFramework = new AtomicReference<>();
        checkBox.setOnAction(event -> {
            if(checkBox.isSelected()) {
                this.toSelectDirectory = true;
            }else{
                this.toSelectDirectory = false;
            }
        });
        TextField text = helper.addTextField(0,2,400);
        if(Main.getVignette().getSettings().getIvet()!=null && !"".equalsIgnoreCase(Main.getVignette().getSettings().getIvet()))
            text.setText(Main.getVignette().getSettings().getIvet());
        else
            text.setText(Main.getStage().getTitle());

         boolean isCancled = helper.createGrid("Enter Vignette name to be saved",null,"Save","Cancel");
         boolean isValid = false;
        if(isCancled) {
            isValid = false;
            String vignetteNametoSave = text.getText();
            String regexForFileName= "^[a-zA-Z0-9_-]*$";
            Pattern namePattern = Pattern.compile(regexForFileName);
            Matcher nameMatcher = namePattern.matcher(vignetteNametoSave);
            vignetteNametoSave = vignetteNametoSave.replace("//s", "");
            while(!isValid){
                vignetteNametoSave = text.getText();
                String message = "";
                if(vignetteNametoSave.equals("")){
                    message =  "Vignette Name Cannot be empty";
                }else if(vignetteNametoSave.matches(regexForFileName)){
                    isValid = true;
                    break;
                }else{
                    message = "Vignette name can be alphanumeric with underscores and hyphens";
                }
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                        message,false);
                if(dialogHelper.getOk()) {
                    vignetteNametoSave = vignetteNametoSave.replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "-");
                    text.setText(vignetteNametoSave);
                    isCancled = helper.showDialog();
                }
                if(!isCancled) {isValid=false; break;}
            }

            if(isValid) {
                File dir;
                final DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select a Directory to save the vignette");
                directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                dir = directoryChooser.showDialog(Main.getStage());

                //Main.getVignette().setSaved(true);
                if (dir != null) {
                    //dirForFramework is a null parameter that is set to the path for framework.zip within the function createFolder()
                    Main.getVignette().getSettings().setIvet(text.getText());
                    AtomicInteger counter = new AtomicInteger();
                    if(dir.isDirectory()){
                        File[] list = dir.listFiles();
                        String finalVignetteNametoSave = vignetteNametoSave;
                        Arrays.stream(list).forEach(item->{
                            if(item.getName().equalsIgnoreCase(finalVignetteNametoSave))
                                counter.getAndIncrement();
                        });
                    }
                    if(counter.get() >0){
                        vignetteNametoSave+="-"+counter.get();
                    }
                    Main.getInstance().changeTitle(text.getText());
                    Main.getVignette().setVignetteName(text.getText());
                    createFolder(dir,vignetteNametoSave);
                    //only setting the vignette as saved once the files have been created at the specified path
                    Main.getVignette().setSaved(true);
                    //return true when you successfully save as
                    return true;
                }
            }
        }

        //System.out.println("You hit cancel");
        //returning false if the user hit cancel
        return false;
    }


    public void createFolder(File dir, String vignetteName) {
        try {

            //just making this the parent folder for the vignette content
            String pathString = dir.getPath();
            File dir2 = new File(pathString+"/"+vignetteName+"-exports");
            dir2.mkdir();

            String filePath = dir2.getAbsolutePath()+"/"+vignetteName;
            System.out.println("file path: "+filePath);
            //this is the path to the first folder, not the vignette content folder
            Main.getVignette().setMainFolderPath(dir2.getPath()+"/");


            Path path = Paths.get(filePath);
            Main.getVignette().setFolderPath(filePath);
            Files.createDirectories(path);
//            File frameWorkDir = dirForFramework.get();
            copyResourceFolderFromJar(filePath);
//            System.out.println("frameWorkDir: "+frameWorkDir.getAbsolutePath());
//            if (frameWorkDir==null) copyResourceFolderFromJar(filePath);
//            else {copyFrameworkFolderFromUserPath(frameWorkDir.getPath(), filePath);}
            createHTMLPages(filePath);
            createImageFolder(filePath);
            vignetteCourseJsFile(filePath);
            saveFramework(filePath);
            saveVignetteSettingToMainFile(filePath);
            saveCSSFile(filePath);
            saveVignetteClass(filePath,vignetteName);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            logger.error("{Failed to create directory}", e);
            System.err.println("Failed to create directory!" + e.getMessage());
        }
    }
    public static void emptyDirectory(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    emptyDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
    }
    public void createHTMLPages(String destinationPath){
        HashMap<String, VignettePage> pageViewList = Main.getVignette().getPageViewList();
        File pagesFolder = new File(destinationPath+ConstantVariables.PAGE_DIRECTORY+"/");
        //============CLEARING PAGES==================
        emptyDirectory(pagesFolder);
        //============CLEARING PAGES==================
        try {
            Path path = Paths.get(destinationPath+ConstantVariables.PAGE_DIRECTORY);
            BufferedWriter bw = null;
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
//            File questionFile = null;
//            File pageImages = null;
//            if(ReadFramework.getUnzippedFrameWorkDirectory().endsWith("/")){
//                questionFile = new File(ReadFramework.getUnzippedFrameWorkDirectory()+"pages/questionStyle/");
//                pageImages = new File(ReadFramework.getUnzippedFrameWorkDirectory()+"pages/Images/");
//            }else{
//                questionFile = new File(ReadFramework.getUnzippedFrameWorkDirectory()+"/pages/questionStyle/");
//                pageImages = new File(ReadFramework.getUnzippedFrameWorkDirectory()+"/pages/Images/");
//            }
//            File savedPageImages = new File(pagesFolder.getAbsolutePath()+"/Images/");
//            File savedQuestionStyle = new File(pagesFolder.getAbsolutePath()+"/questionStyle/");;
//            if(!savedQuestionStyle.exists()){
//                savedQuestionStyle.mkdir();
//            }else{
//                savedQuestionStyle.delete();
//                savedQuestionStyle.mkdir();
//            }

//            if(!savedPageImages.exists()){
//                savedPageImages.mkdir();
//            }else{
//                savedPageImages.delete();
//                savedPageImages.mkdir();
//            }
//            copyDirectoryCompatibityMode(questionFile, savedQuestionStyle);
//            copyDirectoryCompatibityMode(pageImages,savedPageImages);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("{Create HTML Pages }", e);
            System.err.println("Create HTML Pages: " + e.getMessage());
        }
    }
    public void saveVignetteSettingToMainFile(String destinationPath){
        try{

            File mainFile = new File(destinationPath+"/main.html");
            InputStream stream = new FileInputStream(mainFile);
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
            String mainFileContents = writer.toString() + "\n\n";
            String target = "//VignetteSettings([\\S\\s]*?)//VignetteSettings";
            String comments = "//VignetteSettings";
            Pattern pattern = Pattern.compile(target, Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(mainFileContents);
            String js =  Main.getVignette().getSettings().createSettingsJS();
            if(m.find()){
                System.out.println("FOUND: ");
                mainFileContents = mainFileContents.replaceFirst(m.group(0), comments+"\n"+js+comments);
                FileWriter writerBack = new FileWriter(mainFile, false);
                writerBack.write(mainFileContents);
            }else{
                System.out.println("NO SETTINGS FOUND!");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void saveFramework(String destinationPath){
        File out = new File(destinationPath+"/framework.zip");
        File in = new File(Main.getFrameworkZipFile());
        int BUF_SIZE = 1024;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis  = new FileInputStream(in);
            fos = new FileOutputStream(out);
            byte[] buf = new byte[BUF_SIZE];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }


//        Framework toSave = Main.getVignette().getFrameworkInformation();
//        if(toSave.getSerialNumber()==Long.MAX_VALUE)
//            System.out.println("CREATED USING DEFAULT FRAMEWORK!! ");
//        else
//            System.out.println("PATH: "+toSave.getFrameworkPath());
//
//        try {
//            File sourceFile = new File(ReadFramework.getUnzippedFrameWorkDirectory());
//            File destionationFile = new File(destinationPath+"/framework/");
//            copyDirectory(sourceFile, destionationFile);
//            File fileToZip = new File(destinationPath+"/framework");
//            System.out.println("fileToZip: "+fileToZip.getAbsolutePath());
//            ZipUtils zipUtils = new ZipUtils();
//            FileOutputStream fos = new FileOutputStream(destinationPath+"/framework.zip");
//            ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//            zipUtils.zipFile(fileToZip, fileToZip.getName(), zipOut);
//            zipOut.close();
//            fos.close();
//            ReadFramework.deleteDirectory(destionationFile.getAbsolutePath());
//            System.out.println("DIRECTORY FOR FRAMEWORK COPIED SUCCESSFULLY!!");
//        }catch (Exception e){
//            e.printStackTrace();
//
//        }
    }
    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : sourceDirectory.list()) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }
    public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }

    private static void copyFile(File sourceFile, File destinationFile)
            throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
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
//                        fileName = fileName.replaceAll("\\s","-");
                        System.out.println("File name to be saved as image: "+fileName);
                        File outputfile = new File(destinationPath + File.separator + "Images" + File.separator + fileName);
                        String extension = FilenameUtils.getExtension(fileName);
                        ImageIO.write(bi, extension, outputfile);
                    }
                }
            } catch (IOException e) {
                logger.error("{Create Image Folder}", e);
                e.printStackTrace();
                System.err.println("Create Image Fodler" + e.getMessage());

            }


    }
    /* reason for using zip folder for framework is if sometimes the framework folder is taken from the jar file
    *  then the URL will not work for jar. Input stream works hence best way to do this is to zip the folder
    * */
    public void copyResourceFolderFromJar(String destinationPath) throws URISyntaxException, IOException {
//        InputStream stream =  getClass().getResourceAsStream(ConstantVariables.FRAMEWORK_RESOURCE_FOLDER);
        System.out.println("Framework File to get as resource:" +Main.getFrameworkZipFile());
        byte[] buffer = new byte[1024];
        File out = new File(destinationPath);
        try (FileInputStream fis = new FileInputStream(Main.getFrameworkZipFile());
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)){

            // create output directory is not exists
            if (!out.exists()) {
                out.mkdir();
            }
            // get the zip file content
//            ZipInputStream zis = new ZipInputStream(stream);
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                String removeFrameWorkFolderName = fileName.replaceAll("framework/","");
                File newFile = new File(destinationPath+ File.separator+ removeFrameWorkFolderName);
                boolean isPageFolder = ze.getName().startsWith("pages/");
                if(!isPageFolder){
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
