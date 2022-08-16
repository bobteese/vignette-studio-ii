package MenuBar.File;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.ErrorHandler;
import GridPaneHelper.GridPaneHelper;
import RecentFiles.RecentFiles;
import SaveAsFiles.SaveAsVignette;
import TabPane.TabPaneController;
import Vignette.Framework.Framework;
import Vignette.Framework.ReadFramework;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The FileMenuItem.java class represents the tasks a user can perform when they click on the "File" Menu option.
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileMenuItem implements FileMenuItemInterface {

    private final Logger logger = LoggerFactory.getLogger(FileMenuItem.class);

    /**
     * Deals with creating a new vignette.
     */

    public boolean saveVignetteBeforeOtherOperation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setResizable(true);
        alert.setWidth(500);
        alert.setHeight(500);
        alert.setTitle("Save Vignette");
        alert.setHeaderText(null);
        alert.setContentText("Save vignette before creating?");
        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) return false;
        if (result.get().getText().equals("Yes")) {
            Main.getVignette().saveAsVignette(!Main.getVignette().isSaved());
        }else{
            return true;
        }
        return false;
    }

    @Override
    public void createNewVignette() {
        boolean isCanclled = saveVignetteBeforeOtherOperation();
        logger.info("Creating New Vignette " + isCanclled);
        if (!isCanclled) {
            try {
                openedVignette = null;
                Main newMain = new Main();
                newMain.start(Main.getStage());
                Main.getStage().setWidth(1182.0);
                Main.getStage().setHeight(632.0);
                Main.getStage().centerOnScreen();

            } catch (Exception e) {
                logger.error("{createNewVignette}", e);
                e.printStackTrace();
            }
        }
    }

    public static File vgnFile;
    public static Vignette openedVignette;

    /**
     * Function that deals with opening an existing vignette in vignette studio ii using FileChooserHelper
     * This function is used in MenuBarController.java
     *
     * @param file        null
     * @param recentFiles (ArrayDeque of recently created vignettes)
     * @param fileChooser boolean flag
     */
    @Override
    public void openVignette(File file, RecentFiles recentFiles, boolean fileChooser) {
        (new Main()).openVignetteFromHomePage(null);
    }

    public static void selectedFramework() {
        Framework selectedToOpen = Main.getMainFramework();
        if (openedVignette.getFrameworkInformation().equals(selectedToOpen)) {
            System.out.println("DIR SELECTED TO OPEN IS MATCHED WITH THE ONE USED TO CREATE ");
        } else {
            ErrorHandler error = new ErrorHandler(Alert.AlertType.ERROR, "Error", null, "Different files Selected!!");
            error.showAndWait();
            return;
        }
        Main.getVignette().setSettings(null);
        Main.getVignette().setSettings(openedVignette.getSettings());
        Main.getInstance().changeTitle(openedVignette.getVignetteName());
        String path = vgnFile.getParent();
        Main.getVignette().setFolderPath(path);
        Main.getVignette().setSaved(true);
        Main.getVignette().setVignetteName(FilenameUtils.removeExtension(vgnFile.getName()));
    }

    /**
     * Helper function used in openVignette() ^^
     *
     * @param vignette vignette
     * @param pane pane
     */
    public static void addButtonToPane(Vignette vignette, TabPaneController pane) {

        HashMap<String, VignettePage> pageViewList = vignette.getPageViewList();
        HashMap<String, Button> buttonPageMap = new HashMap<>();
        for (Map.Entry<String, VignettePage> mapElement : pageViewList.entrySet()) {

            VignettePage page = mapElement.getValue();
            Image buttonImage = null;
            if (vignette.getImagesPathForHtmlFiles() != null && vignette.getImagesPathForHtmlFiles().get(page.getPageType()) != null) {
                try {
                    InputStream is = new FileInputStream(ReadFramework.getUnzippedFrameWorkDirectory() + "/" + vignette.getImagesPathForHtmlFiles().get(page.getPageType()));
                    buttonImage = new Image(is);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                buttonImage = new Image(Main.class.getResourceAsStream(ConstantVariables.DEFAULT_RESOURCE_PATH));
            }
            ImageView droppedView = new ImageView(buttonImage);

            Button button = pane.createVignetteButton(page, droppedView, page.getPosX(), page.getPosY(), page.getPageType());
            buttonPageMap.put(page.getPageName(), button);
            pane.getPageNameList().add(mapElement.getKey());
        }
        for (Map.Entry<String, Button> buttonPage : buttonPageMap.entrySet()) {

            String page = buttonPage.getKey();
            Button source = buttonPage.getValue();
            VignettePage vignettePage = vignette.getPageViewList().get(page);
            if (vignettePage.getConnectedTo() != null) {
                VignettePage pageTwo = vignette.getPageViewList().get(vignettePage.getConnectedTo());
                Button target = buttonPageMap.get(vignettePage.getConnectedTo());
                pane.checkPageConnection(vignettePage, pageTwo, source, target);
            }
        }
    }


    /**
     * This function allows the user to set the preferred number of recent vignettes they have easy access to.
     * The dialog box that is opened when the user clicks on the Preferences option is created here.
     * All the options, and information on the dialog box is below.
     */
    @Override
    public void setPreferences() {
        GridPaneHelper paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Recent Files: ", 1, 1);
        Spinner<Integer> spinner = paneHelper.addNumberSpinner(Main.getRecentFiles().getNumRecentFiles(), 1, Integer.MAX_VALUE, 2, 1);
        paneHelper.addLabel("", 1, 2);
        Button button = paneHelper.addButton("Clear Recent Files", 2, 2);
        button.setOnAction(event -> Main.getRecentFiles().clearRecentFiles());
        paneHelper.createGrid("Preferences", null, "Save", "Cancel");
        boolean isSaved = paneHelper.isSave();

        if (isSaved) {
            Main.getRecentFiles().saveNumberRecentFiles(spinner.getValue());
        }
    }

    /**
     * This function allows the user to exit from vignette studio through the File option using DialogHelper.java
     * exitApplication() is called by menuAddExit() in MenuBarController.java
     */
    @Override
    public void exitApplication() {

        DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Message", null,
                "Are you sure you want to exit?", false);
        if (helper.getOk()) {
            logger.info("{FileMenuItem} : Exiting Application");
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     *
     */
    // @Override
    // public void saveAsVignette(){
    public void saveAsVignette(RecentFiles recentFiles) {

        logger.info("{FileMenuItem} : saveAsVignette");
        Main.getVignette().saveAsVignette(true);
        String filename = Main.getVignette().getVignetteName();
        String folderpath = Main.getVignette().getFolderPath();
        recentFiles.addRecentFile(new File(folderpath + "\\" + filename + ".vgn"));
    }

    /**
     *
     */
    @Override
    public void saveVignette() {
        logger.info("{FileMenuItem} : saveVignette");
        Main.getVignette().saveAsVignette(false);
    }


    /**
     * This function was meant to open the directory containing all the vignette content.
     * Requirement would be to save as the vignette first.
     * <p>
     * TODO THIS NEEDS TO BE CHECKED. Doesnt work on mac apparently.
     *
     * @param recentFiles recentFiles
     * @throws IOException throws IO
     */
    @Override
    public void openInExplorer(RecentFiles recentFiles) throws IOException {
        logger.info("{FileMenuItem} : openInExplorer");
        String folderpath = Main.getVignette().getFolderPath();
        if (folderpath != null) {
            //when saving as in the current session, the path has forward slashes in it for some reason.
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(Main.getVignette().getFolderPath()));
            }
        } else {
            System.out.println("You need to save as");
            Alert needToSaveAs = new Alert(Alert.AlertType.INFORMATION);
            needToSaveAs.setHeaderText("Current Vignette hasnt been saved to a directory");
            needToSaveAs.show();
        }
    }


//    /**
//     * Exports the vignette into a SCORM compliant package that can be uploaded to an LMS.
//     * Just the beginning of the flow. Creates a dialog box.
//     */
//    @Override
//    public void scormExport() {
//        logger.info("{FileMenuItem} :: scormExport : Exporting in scorm format");
//        System.out.println("Exporting in scorm format");
//        GridPaneHelper gridPane = new GridPaneHelper();
//        Text text = new Text();
//
//        String mainFilePath = Main.getVignette().getMainFolderPath().replaceAll("\\s", "%20");
//        String zipFilePathMessage;
//        //this means the vignette has been saved as before
//        if (mainFilePath != null)
//            zipFilePathMessage = mainFilePath;
//        else
//            zipFilePathMessage = "Needs to be Saved, Click on EXPORT to continue";
//
////        zipFilePathMessage+=" and I am adding some random text to make sure that it does wrap up text and I get to know that this stuff works really well";
//        text.setText(zipFilePathMessage);
//        text.setTextAlignment(TextAlignment.CENTER);
//        text.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-fill: gray;");
//        text.resize(600, 40);
//        text.setWrappingWidth(550);
//
//        gridPane.add(text, 2, 0, 3, 1);
//        Button export = new Button("EXPORT");
//
//        export.setOnAction(event -> {
//            //gridPane.hideDialog();
//            chooseSCORM();
//            gridPane.closeDialog();
//        });
//
//        gridPane.add(export, 1, 0, 1, 1);
//        gridPane.create("SCORM Export", "", "Cancel");
//    }


//    /**
//     * This function makes sure that the Vignette has been saved as and then creates the imsmanifest file that is
//     * required in order to upload the package to the LMS.
//     */
//    public void chooseSCORM() {
//
//        boolean isSaved = Main.getVignette().isSaved();
//        Main.getVignette().saveAsVignette(!isSaved);
//        String folderpath = Main.getVignette().getFolderPath();
//        if (folderpath != null) {
//            try {
//                File manifest = new File(folderpath + "//" + "imsmanifest.xml");
//                manifest.delete();
//                manifest.createNewFile();
//                writeToManifest(manifest);
//                //zipping the content as required
//                System.out.println("Zipping to this location = " + Main.getVignette().getMainFolderPath());
//                File f = new File(Main.getVignette().getMainFolderPath() + "/" + Main.getVignette().getSettings().getIvet() + "_SCORM.zip");
//
//                if (!f.getParentFile().exists())
//                    f.getParentFile().mkdirs();
//                if (!f.exists())
//                    f.createNewFile();
//                System.out.println("SCORM FILE: " + f.getAbsolutePath());
//                FileOutputStream fos = new FileOutputStream(f);
//                ZipOutputStream zos = new ZipOutputStream(fos);
//
//                File start = new File(Main.getVignette().getFolderPath());
//                for (File file : start.listFiles()) {
//                    //skip the zip files
//                    if (!file.getName().contains(".zip"))
//                        addDirToZipArchive(zos, file, null);
//                    else
//                        continue;
//                }
//                zos.flush();
//                fos.flush();
//                zos.close();
//                fos.close();
//            } catch (Exception e) {
//                logger.error("{FileMenuItem} :: chooseSCORM : ",e);
//            }
//        }
//        //folderpath is null, cannot scorm export
//        else {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Warning");
//            alert.setHeaderText("You need to Save As in order to scorm export");
//            alert.showAndWait();
//        }
//    }


    /**
     * This function zips the content of the vignette into a .zip folder that can be uploaded to the LMS.
     *
     * @param zos ZipOutputStream
     * @param fileToZip fileToZip
     * @param parentDirectoryName parentDirectoryName
     * @throws Exception throws Exception
     */
    public void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parentDirectoryName) throws Exception {
        logger.info("> {FileMenuItem}:addDirToZipArchive " + fileToZip);
        if (fileToZip == null || !fileToZip.exists()) {
            return;
        }
        String zipEntryName = fileToZip.getName();
        if (parentDirectoryName != null && !parentDirectoryName.isEmpty()) {
            zipEntryName = parentDirectoryName + "/" + fileToZip.getName();
        }

        if (fileToZip.isDirectory()) {
//            System.out.println("+" + zipEntryName);
            File[] files = fileToZip.listFiles();
            if (files != null && files.length == 0) {
                zos.putNextEntry(new ZipEntry(zipEntryName + "/"));
                zos.closeEntry();
            } else {
                assert files != null;
                for (File file : files) {
                    addDirToZipArchive(zos, file, zipEntryName);
                }
            }
        } else {
//            System.out.println("   " + zipEntryName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(fileToZip);
            zos.putNextEntry(new ZipEntry(zipEntryName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }


    /**
     * This function deals with writing the content of the Vignette to the imsmanifest file.
     *
     * @param manifest manifest
     */
    public void writeToManifest(File manifest) {
        logger.info("> {FileMenuItem}::writeToManifest: manifest file " + manifest.getAbsolutePath());
        String folderpath = Main.getVignette().getFolderPath();
        File dir = new File(folderpath);

        String xml12 = "<?xml version=\"1.0\" standalone=\"no\" ?>\n" +
                "<manifest identifier=\"%s\" version=\"1\"\n" +
                "      xmlns=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2\"\n" +
                "       xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_rootv1p2\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://www.imsproject.org/xsd/imscp_rootv1p1p2 imscp_rootv1p1p2.xsd\n" +
                "                           http://www.imsglobal.org/xsd/imsmd_rootv1p2p1 imsmd_rootv1p2p1.xsd\n" +
                "                           http://www.adlnet.org/xsd/adlcp_rootv1p2 adlcp_rootv1p2.xsd\">\n" +
                "  \n" +
                "\n" +
                "  <metadata>\n" +
                "   <schema>ADL SCORM</schema>\n" +
                "    <schemaversion>1.2</schemaversion>\n" +
                "  </metadata>\n" +
                "  <organizations default=\"IVET\">\n" +
                "    <organization identifier=\"IVET\">\n" +
                "      <title>%s</title>\n" +
                "        <item identifier=\"main_item\" identifierref=\"main_resource\">\n" +
                "          <title>%s</title>\n" +
                "        </item>\n" +
                "    </organization>\n" +
                "  </organizations>\n" +
                "\n" +
                "  <resources>\n" +
                "    <resource identifier=\"main_resource\" type=\"webcontent\" adlcp:scormtype=\"sco\"  href=\"main.html\">\n";


        String close = "    </resource>\n" +
                "  </resources>\n" +
                "</manifest>";

        try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(manifest, true)))) {

            String IVETtitle = Main.getVignette().getSettings().getIvetTitle();
            String IVETName = Main.getVignette().getSettings().getIvet();
            printWriter.printf(xml12, IVETName, IVETtitle, IVETName);

            //calling function to recursively list files
            File[] f = dir.listFiles();
            assert f != null;
            showFiles(f, printWriter);

            printWriter.print(close);
            logger.info("> {FileMenuItem}::writeToManifest: Successfully wrote to manifest file " + manifest.getAbsolutePath());
        } catch (Exception e) {
            logger.error("{FileMenuItem} ::writeToManifest : Error occured while writing to manifest file ", e);
        }
    }

    /**
     * Function to recursively get absolute paths for files from the vignette folder.
     *
     * @param files files
     * @param printWriter printWriter
     */
    public void showFiles(File[] files, PrintWriter printWriter) {

        String resource = "      <file href=\"%s\"/>\n";

        for (File file : files) {
            if (file.isDirectory()) {
                File[] f = file.listFiles();
                assert f != null;
                showFiles(f, printWriter); // Calls same method again.
            } else {
                String extension = FilenameUtils.getExtension(file.getAbsolutePath());
                //check extensions
                if (extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("js") ||
                        extension.equalsIgnoreCase("css") || extension.equalsIgnoreCase("png") ||
                        extension.equalsIgnoreCase("jpg")) {
                    String path = file.getAbsolutePath();
                    String base = Main.getVignette().getFolderPath();
                    String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();
                    //write to xml file
                    printWriter.printf(resource, relative);
                }
            }
        }
    }

    @Override
    public void addLibraryToExtras() {
        boolean isSaved = Main.getVignette().isSaved();
        if (!isSaved) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Warning");
            alert.setContentText("To add Library, the current vignette should be saved!");
            alert.showAndWait();
        }
        Main.getVignette().saveAsVignette(!isSaved); // saving the current state of vignette
        if (Main.getVignette().isSaved()) {

            String pathToExtras = Main.getVignette().getFolderPath() + ConstantVariables.EXTRAS_DIRECTORY;
            File extrasFolder = new File(pathToExtras);
            if (!extrasFolder.exists())
                extrasFolder.mkdir();

            GridPaneHelper helper1 = new GridPaneHelper();
            helper1.setResizable(false);
            //Buttons now manually created and added to allow them to be customizable.
            Button addDirectory = new Button("Add a Directory");
            addDirectory.setPrefSize(1000, 60);
            addDirectory.setOnAction(event -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Add Library support");
                File selectedFile = directoryChooser.showDialog(Main.getStage());
                if (selectedFile != null) {
                    if (selectedFile.isDirectory()) {
                        File copyFile = new File(pathToExtras + "/" + selectedFile.getName() + "");
                        if (!copyFile.exists()) {
                            try {
                                copyFile.mkdir();
                                SaveAsVignette.copyDirectoryCompatibityMode(selectedFile, copyFile);
                            } catch (Exception e) {
                                logger.error("{FileMenuItem} :: addLibraryToExtras : Cannot create copy directory: " , e);
                            }
                        }
                    }
                }
                helper1.closeDialog();
            });

            helper1.addButton(addDirectory, 0, 0);

            Button addAFile = new Button("Add a File");
            addAFile.setPrefSize(1000, 60);
            addAFile.setOnAction(event -> {
                FileChooser filechooser = new FileChooser();
                filechooser.setTitle("Add Library support");
                File openedFile = filechooser.showOpenDialog(Main.getStage());
                if (openedFile != null) {
                    if (openedFile.isFile()) {
                        File copyFile = new File(pathToExtras + "/" + openedFile.getName());
                        if (!copyFile.exists()) {
                            try {
                                copyFile.createNewFile();
                            } catch (IOException e) {
                                logger.error("{FileMenuItem} :: addLibraryToExtras : Cannot create copy file: " , e);
                            }
                        }
                        try {
                            SaveAsVignette.copyFile(openedFile, copyFile);
                        } catch (IOException exception) {
                            logger.error("{FileMenuItem} :: addLibraryToExtras : Cannot copy file: " , exception);
                        }
                    }
                }
                helper1.closeDialog();
            });
            helper1.addButton(addAFile, 0, 1);
            helper1.setPrefSize(300, 100);
            helper1.create("Choose type of Extra Item", "", "Cancel");
        }
    }


}
