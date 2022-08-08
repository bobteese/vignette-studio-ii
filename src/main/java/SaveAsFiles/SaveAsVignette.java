package SaveAsFiles;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import MenuBar.File.FileMenuItem;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;
import Vignette.Vignette;
import javafx.scene.control.*;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class SaveAsVignette {
    private final Logger logger = LoggerFactory.getLogger(SaveAsVignette.class);

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

        Vignette oldVgn = Main.getVignette();
        String oldVgnFolderPath = oldVgn.getFolderPath();
        logger.info("{SaveAsVignette}::fileChoose > oldVgn " + oldVgn.getVignetteName() + " path " + oldVgnFolderPath);
        TextField text = helper.addTextField(0, 2, 400);
        if (Main.getVignette().getSettings().getIvet() != null && !"".equalsIgnoreCase(Main.getVignette().getSettings().getIvet()))
            text.setText(Main.getVignette().getSettings().getIvet());
        else
            text.setText(Main.getStage().getTitle());

        boolean isCancled = helper.createGrid("Enter Vignette name to be saved", null, "Save", "Cancel");
        boolean isValid;
        if (isCancled) {

            String vignetteNametoSave = text.getText();
            String regexForFileName = "^[a-zA-Z0-9_-]*$";

            vignetteNametoSave = vignetteNametoSave.replace("//s", "");
            while (true) {
                vignetteNametoSave = text.getText();
                String message;
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
                        assert list != null;
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
                    createFolder(dir, vignetteNametoSave, oldVgnFolderPath);
                    //only setting the vignette as saved once the files have been created at the specified path
                    Main.getVignette().setSaved(true);
                    //return true when you successfully save as
                    return true;
                }
            }
        }
        //returning false if the user hit cancel
        return false;
    }


    public void createFolder(File dir, String vignetteName, String oldVgnFolderPath) {
        logger.info("{SaveAsVignette}::createFolder: > ");
        try {

            //just making this the parent folder for the vignette content
            String pathString = dir.getPath();
            File dir2 = new File(pathString + "/" + vignetteName + "-exports");
            if (dir2.exists()) {
                logger.info("{SaveAsVignette}::createFolder: >  " + dir2.getAbsolutePath() + " already exists.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setResizable(true);
                alert.setWidth(500);
                alert.setHeight(500);
                alert.setTitle("Alert");
                alert.setContentText("Vignette with the same name already exists at this location.");
                ButtonType okButton = new ButtonType("Replace", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Try Different Name", ButtonBar.ButtonData.BACK_PREVIOUS);
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
                logger.info("{SaveAsVignette}::createFolder: > Prompting the user to Replace, Try with different Name or Cancel");
                Optional<ButtonType> result = alert.showAndWait();
                if (!result.isPresent()) {
                    return;
                }
                logger.info("{SaveAsVignette}::createFolder: > User selects to " + result.get().getText());
                if (result.get().getText().equalsIgnoreCase("Try Different Name")) {
                    fileChoose();
                    return;
                } else if (result.get().getText().equalsIgnoreCase("Cancel")) {
                    return;
                }
            } else {
                dir2.mkdir();
            }
            // If User selects Replace
            logger.info("{SaveAsVignette}::createFolder: > Deleting the old changes ");
            FileUtils.forceDelete(dir2);

            logger.info("{SaveAsVignette}::createFolder: > Making the new folder " + dir2.mkdir());
            String filePath = dir2.getAbsolutePath() + "/" + vignetteName;
            //this is the path to the first folder, not the vignette content folder
            Main.getVignette().setMainFolderPath(dir2.getPath() + "/");


            Path path = Paths.get(filePath);
            Main.getVignette().setFolderPath(filePath);
            Files.createDirectories(path);
            copyResourceFolderFromJar(filePath);
            createExtrasFolder(filePath, oldVgnFolderPath);
            createHTMLPages(filePath);
            vignetteCourseJsFile(filePath);
            createImageFolder(filePath,oldVgnFolderPath);
            saveFramework(filePath);
            saveVignetteSettingToMainFile(filePath);
            saveCSSFile(filePath);
            saveVignetteClass(filePath, vignetteName);
            deleteMACOSXFolder(filePath);
            scormExport();
        } catch (IOException | URISyntaxException e) {
            logger.error("{SaveAsVignette}::createFolder: Failed to create directory! > ", e);
        }
        logger.info("< {SaveAsVignette}::createFolder ");
    }

    public void deleteMACOSXFolder(String filePath) {
        File macosX = new File(filePath + ConstantVariables.MACOSXFOLDER_DIRECTORY + "/");
        try {
            logger.info("{SaveAsVignette}::deleteMACOSXFolder: > deleteMACOSXFolderPath " + macosX.getAbsolutePath());
            FileUtils.deleteDirectory(macosX);
        } catch (Exception e) {
            logger.error("{SaveAsVignette}::deleteMACOSXFolder: > error deleting MACOSX folder { " + macosX.getAbsolutePath() + " }", e);
        }
        logger.info("< {SaveAsVignette}::deleteMACOSXFolder ");
    }

    public void scormExport() {
        String folderPath = Main.getVignette().getFolderPath();
        logger.info("> {SaveAsVignette}::scormExport: folderPath " + folderPath);
        FileMenuItem FMIObj = new FileMenuItem();
        if (folderPath != null) {
            try {
                File manifest = new File(folderPath + "//" + "imsmanifest.xml");
                if (manifest.exists())
                    manifest.delete();
                manifest.createNewFile();
                FMIObj.writeToManifest(manifest);
                folderPath = Main.getVignette().getMainFolderPath();
                logger.info("{SaveAsVignette}::scormExport: > Zipping to this location = " + folderPath);

                File f = new File(folderPath + "/" + Main.getVignette().getSettings().getIvet() + "_SCORM.zip");

                if (!f.getParentFile().exists())
                    f.getParentFile().mkdirs();
                if (!f.exists())
                    f.createNewFile();

                logger.info("{SaveAsVignette}::scormExport: > SCORM FILE: " + f.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(f);
                ZipOutputStream zos = new ZipOutputStream(fos);

                File[] start = new File(Main.getVignette().getFolderPath()).listFiles();
                if (start == null) {
                    logger.info("{SaveAsVignette}::scormExport: > No files found at  " + Main.getVignette().getFolderPath());
                    return;
                }
                for (File file : start) {
                    //skip the zip files
                    if (!file.getName().contains(".zip"))
                        FMIObj.addDirToZipArchive(zos, file, null);

                }
                logger.info("{SaveAsVignette}::scormExport: > Successfully wrote to SCROM file " + f.getAbsolutePath());
                zos.flush();
                fos.flush();
                zos.close();
                fos.close();
            } catch (Exception e) {
                logger.error("> {SaveAsVignette}::scormExport : Error occurred while choosing scorm ", e);
            }
        }
        //folderpath is null, cannot scorm export
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("You need to Save As in order to scorm export");
            alert.showAndWait();
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

    public void createExtrasFolder(String destinationPath, String oldVgnFolderPath) {
        logger.info("{SaveAsVignette}::createExtrasFolder: > destinationPath " + destinationPath);
        logger.info("{SaveAsVignette}::createExtrasFolder: > Main.getVignette().getFolderPath() " + Main.getVignette().getFolderPath());
        File pagesFile = new File(destinationPath + ConstantVariables.EXTRAS_DIRECTORY + "/");
        //============CLEARING PAGES==================
//        emptyDirectory(pagesFile);
        //============CLEARING PAGES==================
        try {
            Path path = Paths.get(destinationPath + ConstantVariables.EXTRAS_DIRECTORY);
            if (!pagesFile.exists()) {
                Files.createDirectories(path);
            }
            if (oldVgnFolderPath != null) {
                File extrasFile = new File(oldVgnFolderPath + ConstantVariables.EXTRAS_DIRECTORY + "/");
                logger.info("{SaveAsVignette}::createExtrasFolder: > Copying extras folder from oldVgn " + extrasFile.getAbsolutePath());
                if (extrasFile.equals(pagesFile)) {
                    File[] files = extrasFile.listFiles();
                    assert files != null;
                    logger.info("{SaveAsVignette}::createExtrasFolder: > found  " + files.length + " to copy from old Vignette");
                    for (File file : files) {
                        if (!FileUtils.directoryContains(pagesFile, file)) {
                            logger.info("{SaveAsVignette}::createExtrasFolder: > Copying the file " + file.getName() + " to " + pagesFile.getAbsolutePath());
                            FileUtils.copyFileToDirectory(file, pagesFile);
                        }

                    }
                } else {
                    logger.info("{SaveAsVignette}::createExtrasFolder: > Copying the whole directory ");
                    FileUtils.copyDirectory(extrasFile, pagesFile);
                }


            }
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
            for (Map.Entry<String, VignettePage> mapElement : pageViewList.entrySet()) {
                String fileName = mapElement.getKey();
                VignettePage contents = mapElement.getValue();
                File file = new File(path + File.separator + fileName + ".html");

                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                FileWriter fw;
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
            if (!mainFile.exists()) {
                mainFile.createNewFile();
            }
            stream = new FileInputStream(mainFile);
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
            String mainFileContents = writer + "\n\n";
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

        try {
            if (!in.equals(out)) {
                FileUtils.copyFile(in, out);
            }

        } catch (Exception e) {
            logger.error("> {SaveAsVignette}::saveFramework : Exception while writing  ", e);
        }

        logger.info("< {SaveAsVignette}::saveFramework");
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        String[] files = sourceDirectory.list();
        assert files != null;
        for (String f : files) {
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
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw;
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
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
                file.createNewFile();
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

    public void createImageFolder(String destinationPath,String oldVgnFolderPath) {
        logger.info("> {SaveAsVignette}::createImageFolder : destinationPath  " + destinationPath);
        logger.info("> {SaveAsVignette}::createImageFolder : oldVgnFolderPath  " + oldVgnFolderPath);
        logger.info("> {SaveAsVignette}::createImageFolder : Vignette " + Main.getVignette());
        List<Images> imagesList = Main.getVignette().getImagesList();
        try {

            File imagesPath = new File(oldVgnFolderPath + File.separator + "Images" + File.separator);
            File[] fileList = imagesPath.listFiles();
            if (fileList == null) {
                logger.info("> {SaveAsVignette}::createImageFolder : No Images found at " + imagesPath.getAbsolutePath());
                return;
            }
            logger.info("> {SaveAsVignette}::createImageFolder : Adding " + fileList.length + " images to new imageList");
            for (File imageFile : fileList) {
                Images images = new Images(imageFile.getName(), ImageIO.read(imageFile));
                if(!imagesList.contains(images))
                    imagesList.add(images);
            }

            Main.getVignette().setImagesList(imagesList);
            logger.info("> {SaveAsVignette}::createImageFolder : There are  " + imagesList.size() + " to be copied");
            for (Images img : imagesList) {
                logger.info("> {SaveAsVignette}::createImageFolder : Image " + img);
                if (img != null) {
                    BufferedImage bi = img.getImage();  // retrieve image
                    String fileName = img.getImageName();
                    File outputFile = new File(destinationPath + File.separator + "Images" + File.separator + fileName);
                    logger.info("{SaveAsVignette}::createImageFolder : File  to be saved as image: " + outputFile);
                    if (!outputFile.getParentFile().exists()) {
                        outputFile.mkdirs();
                    }
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    }
                    String extension = FilenameUtils.getExtension(fileName);
                    if (bi != null) {
                        ImageIO.write(bi, extension, outputFile);
                    }

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
        File zip = new File(Main.getFrameworkZipFile());

        FileInputStream fis = new FileInputStream(zip);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ZipInputStream zis = new ZipInputStream(bis);
        System.out.println(zis.available());
        try {

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
            zis.close();
            fis.close();
            bis.close();
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
