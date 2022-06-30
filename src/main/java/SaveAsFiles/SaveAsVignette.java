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
        logger.info("{SaveAsVignette}::fileChoose >");
        GridPaneHelper helper = new GridPaneHelper();
        CheckBox checkBox = new CheckBox("Choose the directory to save vignette");
        checkBox.setSelected(true);
        AtomicReference<File> dirForFramework = new AtomicReference<>();
        checkBox.setOnAction(event -> {
            if (checkBox.isSelected()) {
                this.toSelectDirectory = true;
            } else {
                this.toSelectDirectory = false;
            }
        });
        TextField text = helper.addTextField(0, 2, 400);
        if (Main.getVignette().getSettings().getIvet() != null && !"".equalsIgnoreCase(Main.getVignette().getSettings().getIvet()))
            text.setText(Main.getVignette().getSettings().getIvet());
        else
            text.setText(Main.getStage().getTitle());

        boolean isCancled = helper.createGrid("Enter Vignette name to be saved", null, "Save", "Cancel");
        boolean isValid = false;
        if (isCancled) {
            isValid = false;
            String vignetteNametoSave = text.getText();
            String regexForFileName = "^[a-zA-Z0-9_-]*$";
            Pattern namePattern = Pattern.compile(regexForFileName);
            Matcher nameMatcher = namePattern.matcher(vignetteNametoSave);
            vignetteNametoSave = vignetteNametoSave.replace("//s", "");
            while (!isValid) {
                vignetteNametoSave = text.getText();
                String message = "";
                if (vignetteNametoSave.equals("")) {
                    message = "Vignette Name Cannot be empty";
                } else if (vignetteNametoSave.matches(regexForFileName)) {
                    isValid = true;
                    break;
                } else {
                    message = "Vignette name can be alphanumeric with underscores and hyphens";
                }
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION, "Message", null,
                        message, false);
                if (dialogHelper.getOk()) {
                    vignetteNametoSave = vignetteNametoSave.replaceAll("[^a-zA-Z0-9\\-_]", "-");
                    text.setText(vignetteNametoSave);
                    isCancled = helper.showDialog();
                }
                if (!isCancled) {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                logger.info("{SaveAsVignette}::fileChoose: Vignette Name " + vignetteNametoSave);
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
                    if (dir.isDirectory()) {
                        File[] list = dir.listFiles();
                        String finalVignetteNametoSave = vignetteNametoSave;
                        Arrays.stream(list).forEach(item -> {
                            if (item.getName().equalsIgnoreCase(finalVignetteNametoSave))
                                counter.getAndIncrement();
                        });
                    }
                    if (counter.get() > 0) {
                        vignetteNametoSave += "-" + counter.get();
                    }
                    logger.info("{SaveAsVignette}::fileChoose: Vignette Directory  " + dir.getAbsolutePath());
                    Main.getInstance().changeTitle(text.getText());
                    Main.getVignette().setVignetteName(text.getText());
                    createFolder(dir, vignetteNametoSave);
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
        logger.info("{SaveAsVignette}::createFolder: > ");
        try {

            //just making this the parent folder for the vignette content
            String pathString = dir.getPath();
            File dir2 = new File(pathString + "/" + vignetteName + "-exports");
            dir2.mkdir();

            String filePath = dir2.getAbsolutePath() + "/" + vignetteName;
            //this is the path to the first folder, not the vignette content folder
            Main.getVignette().setMainFolderPath(dir2.getPath() + "/");


            Path path = Paths.get(filePath);
            Main.getVignette().setFolderPath(filePath);
            Files.createDirectories(path);
            copyResourceFolderFromJar(filePath);
            createExtrasFolder(filePath);
            createHTMLPages(filePath);
            createImageFolder(filePath);
            vignetteCourseJsFile(filePath);
            saveFramework(filePath);
            saveVignetteSettingToMainFile(filePath);
            saveCSSFile(filePath);
            saveVignetteClass(filePath, vignetteName);
        } catch (IOException | URISyntaxException e) {
            logger.error("{SaveAsVignette}::createFolder: Failed to create directory! > ", e);
            System.err.println("Failed to create directory!" + e.getMessage());
        }
    }

    public static void emptyDirectory(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    emptyDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public void createExtrasFolder(String destinationPath) {
        logger.info("{SaveAsVignette}::createExtrasFolder: > ");
        File pagesFolder = new File(destinationPath + ConstantVariables.EXTRAS_DIRECTORY + "/");
        //============CLEARING PAGES==================
        emptyDirectory(pagesFolder);
        //============CLEARING PAGES==================
        try {
            Path path = Paths.get(destinationPath + ConstantVariables.EXTRAS_DIRECTORY);
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("{SaveAsVignette}::createExtrasFolder: Creating extras folder exception: ", e);
        }
    }

    public void createHTMLPages(String destinationPath) {
        logger.info("> {SaveAsVignette}::createHTMLPages: destinationPath " + destinationPath);
        HashMap<String, VignettePage> pageViewList = Main.getVignette().getPageViewList();
        File pagesFolder = new File(destinationPath + ConstantVariables.PAGE_DIRECTORY + "/");
        //============CLEARING PAGES==================
        emptyDirectory(pagesFolder);
        //============CLEARING PAGES==================
        try {
            Path path = Paths.get(destinationPath + ConstantVariables.PAGE_DIRECTORY);
            BufferedWriter bw = null;
            Files.createDirectories(path);
            for (Map.Entry mapElement : pageViewList.entrySet()) {
                String fileName = (String) mapElement.getKey();
                VignettePage contents = (VignettePage) mapElement.getValue();
                File file = new File(path.toString() + File.separator + fileName + ".html");

                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                FileWriter fw = null;
                try {
                    fw = new FileWriter(file, false);
                    bw = new BufferedWriter(fw);
                    bw.write(contents.getPageData() == null ? "" : contents.getPageData());
                } catch (IOException e) {
                    logger.error("{SaveAsVignette}::createHTMLPages: Exception while writing pageData ", e);
                } finally {
                    if (bw != null) {
                        bw.flush();
                    }
                }
            }
        } catch (IOException e) {
            logger.error("{SaveAsVignette}::createHTMLPages: Exception while creating HTML pages ", e);
            System.err.println("Create HTML Pages: " + e.getMessage());
        }
        logger.info("< {SaveAsVignette}::createHTMLPages");
    }

    public void saveVignetteSettingToMainFile(String destinationPath) {
        logger.info("> {SaveAsVignette}::saveVignetteSettingToMainFile: destinationPath " + destinationPath);
        InputStream stream = null;
        StringWriter writer = new StringWriter();
        try {
            File mainFile = new File(destinationPath + "/main.html");
            stream = new FileInputStream(mainFile);
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
            String mainFileContents = writer.toString() + "\n\n";
            String target = "//VignetteSettings([\\S\\s]*?)//VignetteSettings";
            String comments = "//VignetteSettings";
            Pattern pattern = Pattern.compile(target, Pattern.CASE_INSENSITIVE);
            Matcher m = pattern.matcher(mainFileContents);
            String js = Main.getVignette().getSettings().createSettingsJS();
            if (m.find()) {
                logger.info("> {SaveAsVignette}::saveVignetteSettingToMainFile: SETTINGS FOUND! ");
                mainFileContents = mainFileContents.replaceFirst(m.group(0), comments + "\n" + js + comments);
                FileWriter writerBack = new FileWriter(mainFile, false);
                writerBack.write(mainFileContents);
            } else {
                logger.info("> {SaveAsVignette}::saveVignetteSettingToMainFile: NO SETTINGS FOUND! ");
            }

        } catch (Exception e) {
            logger.error("{SaveAsVignette}::saveVignetteSettingToMainFile : Error while saving Vignette Setting ", e);
        } finally {
            try {
                if (stream != null)
                    stream.close();
                writer.close();
            } catch (IOException e) {
                logger.error("{SaveAsVignette}::saveVignetteSettingToMainFile : Error while closing stream or writer ", e);
            }

        }
        logger.info("< {SaveAsVignette}::saveVignetteSettingToMainFile");

    }

    public void saveFramework(String destinationPath) {
        logger.info("> {SaveAsVignette}::saveFramework : destinationPath " + destinationPath);
        File out = new File(destinationPath + "/framework.zip");
        File in = new File(Main.getFrameworkZipFile());
        int BUF_SIZE = 1024;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(in);
            fos = new FileOutputStream(out);
            byte[] buf = new byte[BUF_SIZE];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            logger.error("> {SaveAsVignette}::saveFramework : Exception while writing  ", e);
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (Exception ex) {
                logger.error("> {SaveAsVignette}::saveFramework : Exception while closing streams ", ex);
            }
        }
        logger.info("< {SaveAsVignette}::saveFramework");
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

    public static void copyFile(File sourceFile, File destinationFile)
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
        logger.info("> {SaveAsVignette}::vignetteCourseJsFile : destinationPath " + destinationPath);
        BufferedWriter bw = null;
        try {
            File file = new File(destinationPath + File.separator + ConstantVariables.DATA_DIRECTORY + File.separator
                    + ConstantVariables.VIGNETTE_SETTING);

            logger.info("> {SaveAsVignette}::vignetteCourseJsFile : Vignette Setting File " + file);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = null;
            try {
                fw = new FileWriter(file, false);
                bw = new BufferedWriter(fw);
                VignetteSettings settings = Main.getVignette().getSettings();
                String content = settings == null ? new VignetteSettings().createSettingsJS() : settings.createSettingsJS();
                bw.write(content);
            } catch (IOException e) {
                logger.error("> {SaveAsVignette}::vignetteCourseJsFile : Exception while writing ", e);
            } finally {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
            }
        } catch (IOException e) {
            logger.error("> {SaveAsVignette}::vignetteCourseJsFile : Exception ", e);
        }
        logger.info("< {SaveAsVignette}::vignetteCourseJsFile ");
    }

    public void saveCSSFile(String destinationPath) throws IOException {
        logger.info("> {SaveAsVignette}::saveCSSFile : destinationPath  " + destinationPath);
        BufferedWriter bw = null;
        String css = Main.getVignette().getCssEditorText();
        try {
            if (null != css) {
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

            }
        } catch (IOException e) {
            logger.error("{SaveAsVignette}::saveCSSFile : exception while saving ", e);

        } finally {
            if (bw != null)
                bw.close();
        }
        logger.info("< {SaveAsVignette}::saveCSSFile : ");
    }

    public void createImageFolder(String destinationPath) {
        logger.info("> {SaveAsVignette}::createImageFolder : destinationPath  " + destinationPath);
        List<Images> imagesList = Main.getVignette().getImagesList();
        try {
            for (Images img : imagesList) {
                if (img != null) {
                    BufferedImage bi = img.getImage();  // retrieve image
                    String fileName = img.getImageName();
                    System.out.println("File name to be saved as image: " + fileName);
                    File outputFile = new File(destinationPath + File.separator + "Images" + File.separator + fileName);
                    logger.info("{SaveAsVignette}::createImageFolder : File  to be saved as image: " + outputFile);
                    String extension = FilenameUtils.getExtension(fileName);
                    ImageIO.write(bi, extension, outputFile);
                }
            }
        } catch (IOException e) {
            logger.error("{SaveAsVignette}::createImageFolder: Exception while creating Image Folder ", e);
        }

        logger.info("< {SaveAsVignette}::createImageFolder");
    }

    /* reason for using zip folder for framework is if sometimes the framework folder is taken from the jar file
     *  then the URL will not work for jar. Input stream works hence best way to do this is to zip the folder
     * */
    public void copyResourceFolderFromJar(String destinationPath) throws URISyntaxException, IOException {
        logger.info("> {SaveAsVignette}::copyResourceFolderFromJar : destinationPath  " + destinationPath);
        logger.info("> {SaveAsVignette}::copyResourceFolderFromJar : Framework File to get as resource  " + Main.getFrameworkZipFile());
        System.out.println("Framework File to get as resource:" + Main.getFrameworkZipFile());
        byte[] buffer = new byte[1024];
        File out = new File(destinationPath);
        try (FileInputStream fis = new FileInputStream(Main.getFrameworkZipFile());
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zis = new ZipInputStream(bis)) {

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
                String removeFrameWorkFolderName = fileName.replaceAll("framework/", "");
                File newFile = new File(destinationPath + File.separator + removeFrameWorkFolderName);
                boolean isPageFolder = ze.getName().startsWith("pages/");
                if (!isPageFolder) {
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
        } catch (IOException ex) {
            logger.error("{SaveAsVignette}::copyResourceFolderFromJar: Exception while copying ", ex);
        }
        logger.info("< {SaveAsVignette}::copyResourceFolderFromJar");
    }

    public void copyFrameworkFolderFromUserPath(String sourcePath, String destinationPath) {
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

    public void saveVignetteClass(String destinationPath, String vignetteName) {
        logger.info("> {SaveAsVignette}::saveVignetteClass : destinationPath  " + destinationPath + " vignetteName " + vignetteName);
        FileOutputStream fileOut = null;
        ObjectOutputStream objectOut = null;
        try {
            fileOut = new FileOutputStream(destinationPath + File.separator + vignetteName + ".vgn");
            logger.info("> {SaveAsVignette}::saveVignetteClass : fileOut " + destinationPath + File.separator + vignetteName + ".vgn");
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(Main.getVignette());
        } catch (Exception e) {
            logger.error("{SaveAsVignette}::saveVignetteClass : Exception while saving Vignette Class ", e);
        } finally {
            try {
                if (fileOut != null) fileOut.close();
                if (objectOut != null) objectOut.close();
            } catch (Exception e) {
                logger.error("{SaveAsVignette}::saveVignetteClass : Exception while closing streams ", e);
            }
        }
        System.out.println("The Object  was successfully written to a file");
        logger.info("< {SaveAsVignette}::saveVignetteClass : The Object  was successfully written to a file");
    }
}
