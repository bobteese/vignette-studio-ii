/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import MenuBar.Help.JavaVersion;
import MenuBar.Vignette.VignetteMenuItem;
import Preview.VignetteServerException;
import RecentFiles.RecentFiles;
import TabPane.TabPaneController;
import Vignette.Framework.FileResourcesUtils;
import Vignette.Framework.FilesFromResourcesFolder;
import Vignette.Framework.Framework;
import Vignette.Framework.ReadFramework;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;
import Vignette.Vignette;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static MenuBar.File.FileMenuItem.openedVignette;

public class Main extends Application {
    private static Main instance;
    final ScrollBar sc = new ScrollBar();

    public static Main getInstance() {
        return instance;
    }

    @FXML
    Button chooseYourOwn;

    @FXML
    Button useDefault;

    private static Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Vignette vignette;
    private static RecentFiles recentFiles;

    public static String getSelectedFramework() {
        return selectedFramework;
    }

    public static void setSelectedFramework(String selectedFramework) {
        Main.selectedFramework = selectedFramework;
    }

    private static String selectedFramework;

    public static String getFrameworkZipFile() {
        return frameworkZipFile;
    }

    public static void setFrameworkZipFile(String frameworkZipFile) {
        Main.frameworkZipFile = frameworkZipFile;
    }

    private static String frameworkZipFile;
    // todo I added this
    private VignettePage currentVignettePage;

    public static Framework mainFramework;

    public static Framework getMainFramework() {
        return mainFramework;
    }

    public static void setMainFramework(Framework mainFramework) {
        Main.mainFramework = mainFramework;
    }

    public static Vignette anotherVignetteInstance() {
        return (new Vignette());
    }

    public static void setVignette(Vignette v) {
        Main.vignette = v;
    }

    public static boolean openExistingFramework;

    /**
     * Main entry point for the JavaFX application. User interface defined by means
     * of a stage and scene. Stage is the
     * top level container. Scene is the container for all content.
     * Contains an undo and redo stack for all page related operations.
     * Loads object hierarchy from application.fxml.
     * application.fxml includes menu.fxml and tabs.fxml which are all elements on
     * the main window of the vignette studio
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) {
        logger.info("{Main}:: start: Started Application");
        try {
            GridPaneHelper helper = new GridPaneHelper();
            TextField text = helper.addTextField(0, 2, 400);
            instance = this;
            this.vignette = anotherVignetteInstance();
            Scene homeScene = null;
            // Create the landing page.
            if (openedVignette == null) {
                Parent homeRoot = FXMLLoader.load(getClass().getResource("/FXML/Home.fxml"));
                Main.primaryStage = primaryStage;
                Main.primaryStage.setTitle("Vignette Studio 2");
                Main.primaryStage.setMaximized(false);
                String protocol = Main.class.getResource("").getProtocol();
                if (Objects.equals(protocol, "jar")) {
                    Main.isJar = true;
                } else if (Objects.equals(protocol, "file")) {
                    Main.isJar = false;
                }
                homeScene = new Scene(homeRoot);
                homeScene.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
                sc.setLayoutX(homeScene.getWidth() - sc.getWidth());
                sc.setMin(0);
                sc.setOrientation(Orientation.VERTICAL);
                sc.setPrefHeight(180);
                sc.setMax(360);

            } else {
                if (openedVignette.getFrameworkInformation().getSerialNumber() == Long.MAX_VALUE) {
                    goAheadWithDefaultFramework();
                    logger.info("{Main} :: start : Starting VS2 with Default Framework " + Main.getFrameworkZipFile());
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert");
                    alert.setContentText("Default framework doesnt work, try getting "
                            + openedVignette.getFrameworkInformation().getFrameworkName() + " (maybe from: "
                            + openedVignette.getFrameworkInformation().getFrameworkPath() + " )");
                    alert.showAndWait();
                    (new Main()).chooseDirectory();
                }
                homeScene = openEditor();
            }

            Main.primaryStage.setScene(homeScene);
            Main.primaryStage.show();
            Main.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Exit", null,
                            "Are you sure you want to exit?", false);
                    if (helper.getOk()) {
                        try {
                            if (Main.getVignette() != null)
                                Main.getVignette().stopPreviewVignette();
                            if (ReadFramework.getUnzippedFrameWorkDirectory() != null
                                    && !"".equalsIgnoreCase(ReadFramework.getUnzippedFrameWorkDirectory()))
                                ReadFramework.deleteDirectory(ReadFramework.getUnzippedFrameWorkDirectory());
                            File[] vignetteFolder = (new File(ConstantVariables.VIGNETTESTUDIO_PATH)).listFiles();
                            if (vignetteFolder != null && vignetteFolder.length > 0) {
                                for (File temp : vignetteFolder) {
                                    if (temp.getAbsolutePath().endsWith(".zip")) {
                                        temp.delete();
                                    }
                                }
                            }
                        } catch (VignetteServerException e) {
                            System.out.println("ERROR TO STOP: " + e.getMessage());
                            logger.error("{Main} :: start : error starting ", e);
                        }
                        primaryStage.close();
                    } else {
                        we.consume();
                    }
                }
            });
        } catch (Exception e) {
            logger.error("{Main} :: start : error starting ", e);
        }
    }

    public void chooseDirectory() throws IOException {
        makeVignetteStudioDir();
        File dir;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("zip files", "*.zip"));
        fileChooser.setTitle("Select a Directory from the vignette");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = fileChooser.showOpenDialog(Main.getStage());
        String dirName = "";
        if (dir != null) {
            Main.setFrameworkZipFile(dir.getAbsolutePath().replaceAll("//s", "%20"));
            dirName = dir.getName().substring(0, dir.getName().lastIndexOf("."));
            ReadFramework.unZipTheFrameWorkFile(new File(Main.getFrameworkZipFile()));
            instance = this;
            this.vignette = anotherVignetteInstance();
            setMainVignetteInformation(dirName);
            logger.info("Setting Vignette Directory " + dirName);
            primaryStage.close();
            openEditor();
        } else {
            System.out.println("PRESSED CANCEL!");
        }
    }

    private void setMainVignetteInformation(String dirName) {
        long serialNumber;
        Random random = new Random();
        if (dirName.equalsIgnoreCase(ConstantVariables.DEFAULT_FRAMEWORK_PATH)) {
            serialNumber = Long.MAX_VALUE;
            Main.setMainFramework(new Framework(Main.getFrameworkZipFile(), dirName, serialNumber));
            return;
        }
        do {
            serialNumber = Math.abs(random.nextLong());
        } while (serialNumber == Long.MAX_VALUE);
        Framework f = new Framework(Main.getFrameworkZipFile(), dirName, serialNumber);
        if (!f.addToFrameworkVersionFile()) {
            ArrayList<Framework> listOfFrameworks = ReadFramework.readFrameworkVersionFile();
            for (Framework framework : listOfFrameworks) {
                if (framework.getFrameworkName().equalsIgnoreCase(f.getFrameworkName())) {
                    Main.setMainFramework(framework);
                    break;
                }
            }
        } else {
            Main.setMainFramework(f);
        }
    }

    public static boolean defaultFramework = false;

    public static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    public void goAheadWithDefaultFramework() throws IOException, URISyntaxException {
        makeVignetteStudioDir();
        // Framework defaultFramework = new
        // Framework(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
        String os = System.getProperty("os.name");
        Main.defaultFramework = true;
        if (!Main.isJar) {
            FilesFromResourcesFolder filesFromResourcesFolder = new FilesFromResourcesFolder();
            List<File> list = filesFromResourcesFolder.getAllFilesFromResource("");
            for (File f : list) {
                if (f.getAbsolutePath().endsWith(".zip")) {
                    Main.setFrameworkZipFile(f.getAbsolutePath().replaceAll("//s", "%20"));
                    break;
                }
            }
        } else {
            FileResourcesUtils fileResourcesUtils = new FileResourcesUtils();
            InputStream is = null;
            // if(os.trim().startsWith("Mac")){
            // is =
            // fileResourcesUtils.getFileFromResourceAsStream(ConstantVariables.DEFAULT_FRAMEWORK_PATH_USING_FILE_SEPARATOR);
            // }else{
            // is =
            // fileResourcesUtils.getFileFromResourceAsStream(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
            // }

            is = fileResourcesUtils.getFileFromResourceAsStream(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
            if (is != null) {
                is = fileResourcesUtils.getFileFromResourceAsStream("framework.zip");
            }
            final File tempFile = File.createTempFile("framework", ".zip",
                    new File(ConstantVariables.VIGNETTESTUDIO_PATH));
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(is, out);
            }
            Main.setFrameworkZipFile(tempFile.getAbsolutePath().replaceAll("//s", "%20"));
            tempFile.deleteOnExit();
        }
        setMainVignetteInformation(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
        ReadFramework.unZipTheFrameWorkFile(new File(Main.getFrameworkZipFile()));
        if (openEditor() == null) {
            Main.getInstance().start(Main.getStage());
        }
    }

    public void makeVignetteStudioDir() {
        File file = new File(ConstantVariables.VIGNETTESTUDIO_PATH);
        try {
            if (!file.exists()) {
                file.mkdirs();
                logger.info("{Main}:: makeVignetteStudioDir : Successfully created vignettestudio-ii folder");
            } else {
                logger.info("{Main}:: makeVignetteStudioDir : vignettestudio-ii folder already exits");
            }
        } catch (SecurityException e) {
            logger.error("{Main}:: makeVignetteStudioDir : Error creating .vignettestudio-ii folder" ,e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Warning");
            alert.setContentText("Error creating .vignettestudio-ii folder");
        }
    }

    public Scene openEditor() throws IOException {

        // makeVignetteStudioDir();
        initializeCssContentFromFramework();
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Main.primaryStage.close();

        if (Main.getVignette() == null) {
            this.vignette = anotherVignetteInstance();
        } else if (Main.getVignette().getVignetteName() != null) {
            Main.getInstance().changeTitle(Main.getVignette().getVignetteName());
        } else {
            Main.primaryStage.setTitle("untitled");
        }
        Main.getInstance().changeTitle(Main.getVignette().getVignetteName());
        Main.getVignette().setFrameworkInformation(Main.getMainFramework());
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/application.fxml"));
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        scene.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
        sc.setLayoutX(scene.getWidth() - sc.getWidth());
        sc.setMin(0);
        sc.setOrientation(Orientation.VERTICAL);
        sc.setPrefHeight(180);
        sc.setMax(360);

        if (Main.getVignette().getSettings() == null) {
            VignetteSettings vs = (new VignetteMenuItem()).editVignetteSettings();
            if (vs == null) {
                return null;
            }
        }
        final String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            Main.primaryStage.setFullScreen(true);
        }
        Main.primaryStage.setResizable(true);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.setX(bounds.getMinX());
        Main.primaryStage.setY(bounds.getMinY());
        Main.primaryStage.setWidth(bounds.getWidth());
        Main.primaryStage.setHeight(bounds.getHeight());
        Main.primaryStage.setMaxWidth(bounds.getWidth() * 2);
        Main.primaryStage.show();
        Main.primaryStage.getIcons()
                .add(new Image((getClass().getResourceAsStream(ConstantVariables.IMAGE_ICON_RESOURCE_PATH))));
        return scene;
    }

    public void initializeCssContentFromFramework() {
        try {
            String cssFilePath = "";
            if (!Main.getVignette().isSaved()) {
                cssFilePath = ReadFramework.getUnzippedFrameWorkDirectory();
            } else {
                cssFilePath = Main.getVignette().getFolderPath();
            }
            if (cssFilePath.endsWith("/")) {
                cssFilePath += "css/custom.css";
            } else {
                cssFilePath += "/css/custom.css";
            }
            logger.info("{initializeCssContentFromFramework} : Init CSS File " + cssFilePath);
            File cssFile = new File(cssFilePath);
            FileInputStream inputStream = new FileInputStream(cssFile);
            StringWriter getContent = new StringWriter();
            IOUtils.copy(inputStream, getContent, StandardCharsets.UTF_8);
            Main.getVignette().setCssEditorText(getContent.toString());
            inputStream.close();
            getContent.close();
        } catch (Exception ex) {
            logger.error("{Main}:: initializeCssContentFromFramework : Custom CSS File exception ", ex);
        }
    }

    public void openDocumentation() {
        try {
            String inputPdf = "pdf/Vignette Studio Documentation.pdf";
            Path tempOutput = Files.createTempFile("Vignette Studio Documentation", ".pdf");
            tempOutput.toFile().deleteOnExit();
            logger.error("{Main}:: openDocumentation : Opening Documentation " + tempOutput);
            try (InputStream is = Main.class.getClassLoader().getResourceAsStream(inputPdf)) {
                if (is != null)
                    Files.copy(is, tempOutput, StandardCopyOption.REPLACE_EXISTING);
            }
            Desktop.getDesktop().open(tempOutput.toFile());
        } catch (Exception e) {
            logger.error("{openDocumentation} :", e);
        }
        // use this to view online.
        // getHostServices().showDocument("https://docs.google.com/document/d/1loa3WrsEVV23AzRGlEjfxi_o4JnWFRVLcyM_YH1ZjnI/edit");
    }

    private String executeGitCommandToGetTags(boolean runRemote) {

        String command = "";
        if (runRemote) {
            command = "git ls-remote --tags";
        } else {
            command = "git tag";
        }

        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String tags = "";
            while ((line = input.readLine()) != null) {
                tags += (line + "\n");
            }
            return tags;
        } catch (IOException exception) {
            logger.error("{executeGitCommandToGetTags} : ", exception);
        }
        return null;
    }

    public static void openTutorialAlertBox() {
        logger.info("{Main} :: openTutorialAlertBox : ");
        final String tutorialLink = "https://vimeo.com/662854416/9f472dd942";
        String message = "Click on the link to view the tutorial";
        Hyperlink link = new Hyperlink(tutorialLink);

        Text messageText = new Text(message);
        messageText.setStyle("-fx-padding: 5px");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        HBox linkBox = new HBox(new VBox(messageText, link));
        messageText.setStyle("-fx-line-spacing: 0.8em; -fx-font-family: Arial");

        link.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                HostServicesDelegate hostServices = HostServicesDelegate.getInstance(Main.getInstance());
                hostServices.showDocument(link.getText());
                alert.close();
            }
        });
        alert.getDialogPane().setContent(linkBox);
        alert.showAndWait();
    }

    public static String getVersionFromFile() {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(ConstantVariables.DEFAULT_VERSION_PATH)) {
            byte[] vb = new byte[128];
            if (is != null){
                is.read(vb);
                return new String(vb);
            }
        } catch (IOException e) {
            logger.error("{Main}:: getVersionFromFile : Error reader version file ",e);
        }
        return null;
    }

    public static void openAboutAlertBox() {
        logger.info("> {Main} :: openAboutAlertBox ");
        try {
            String version = getVersionFromFile();
            logger.info("{Main} :: openAboutAlertBox : Vignette Studio Version : " + version);
            Text text = new Text();
            // Setting the text to be added.
            final String gnuLink = "https://www.gnu.org/licenses/gpl.html";
            final String livePhotoLink = "http://livephoto.rit.edu/";
            Hyperlink link = new Hyperlink(gnuLink);
            Hyperlink photoLink = new Hyperlink(livePhotoLink);
            if (version == null || "".equalsIgnoreCase(version))
                version = "2.0.0";
            String version2Message = "Vignette Studio II was created at the Rochester Institute of Technology as part of the Interactive Video-Enhanced Tutorials (IVET) Project."
                    +
                    " Vignette Studio II was developed under the direction of Robert Teese. " +
                    "\n\nSoftware Developers: " +
                    "\nAyush Arora, Asmita Hari, Johann Lee, Anand Rajasrinivasan, Robert Teese, Ashnil Vazirani and Atharva Lele"
                    +
                    "\n\nIVET Project leaders:  " +
                    "\nRobert Teese and Michelle Chabot at Rochester Institute of Technology, " +
                    "and Kathleen Koenig and Alexandru Maries at the University of Cincinnati" +
                    "\n\nThis material is based in part upon work supported by National Science Foundation (NSF) grants DUE-1821391 and DUE-1821396."
                    +
                    " Any opinions, findings and conclusions or recommendations expressed in this material are those of the author(s) and"
                    +
                    " do not necessarily reflect the views of the National Science Foundation." +
                    "\n\nVignette Studio II version: " + version +
                    "\nJava version: " + JavaVersion.getFullVersion() + "\n" + "";

            final Separator separator = new Separator();
            separator.setMaxWidth(700);
            separator.setStyle("-fx-border-width: 1px;");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            logger.info("{Main} :: openAboutAlertBox : Alert created " + alert);
            alert.setHeaderText("About Vignette Studio II");
            text.setText(version2Message);
            text.setWrappingWidth(700);
            text.setStyle("-fx-line-spacing: 0.8em; -fx-font-family: Arial");
            link.setStyle("-fx-padding: 0");
            photoLink.setStyle("-fx-padding: 0");
            String GNUText1 = "\nVignette Studio II is \u00A9 2020-2021," +
                    " The Interactive Video-Enhanced Tutorials Project at Rochester Institute of Technology. \n";

            String GNUText2 = "It is licensed to you under the terms of the GNU General Public License (GPL): ";

            HBox linkBox = new HBox(new VBox(new Text(GNUText1), new HBox(new Text(GNUText2), link)));

            VBox box = new VBox(text);
            box.setSpacing(5);
            box.getChildren().add(separator);
            box.getChildren().add(linkBox);
            link.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    HostServicesDelegate hostServices = HostServicesDelegate.getInstance(Main.getInstance());
                    hostServices.showDocument(gnuLink);
                }
            });
            alert.getDialogPane().getChildren().stream().filter(node -> node instanceof TextArea)
                    .forEach(node -> ((TextArea) node).setMinHeight(Region.USE_PREF_SIZE));
            alert.getDialogPane().setContent(box);
            logger.info("{Main} :: openAboutAlertBox : Alert Dialog Pane Content Set ");
            alert.showAndWait();
        } catch (Exception e) {
            logger.error("{Main} :: openAboutAlertBox : ", e);
        }
    }

    public void openAbout() {
        logger.info("{Main} :: openAbout ");
        openAboutAlertBox();
    }

    public void openTutorial() {
        logger.info("{Main} :: openTutorial ");
        openTutorialAlertBox();
    }

    /**
     * Returns the stage of the JavaFX application.
     *
     * @return primaryStage
     */
    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * Changes the title of the vignette
     *
     * @param title title to be changed to
     */
    public void changeTitle(String title) {
        this.primaryStage.setTitle(title);
    }

    /**
     * Getter for the vignette
     *
     * @return vignette
     */
    public static Vignette getVignette() {
        return vignette;
    }

    /**
     * Setter for the vignette
     *
     * @param vignette
     */
    // public void setVignette(Vignette vignette) { this.vignette = vignette; }

    /**
     * Instance of the application is created on the JavaFX thread.
     *
     * @param args
     */
    public static boolean isJar = false;

    public static void main(String[] args) {
        launch(args);

    }

    /**
     * Returns the ArrayDeque of recent vignette files.
     * Used in setPreferences() of File.FileMenuItem.java when the user sets their
     * preferred number of previous files to be displayed.
     *
     * @return recentFiles ArrayDeque of recentfiles
     */
    public static RecentFiles getRecentFiles() {
        return recentFiles;
    }

    /**
     * Used to set the recentFiles in the MenuBarController
     *
     * @param recentFiles
     */
    public void setRecentFiles(RecentFiles recentFiles) {
        this.recentFiles = recentFiles;
    }

    public void manuallyCopyVignette(Vignette v) {
        Main.getVignette().setVignetteName(v.getVignetteName());
        Main.getVignette().setCurrentPage(v.getCurrentPage());
        Main.getVignette().setImagesList(v.getImagesList());
        // Main.getVignette().setFolderPath(v.getFolderPath());
        // Main.getVignette().setMainFolderPath(v.getMainFolderPath());
        // Main.getVignette().setSaved(v.isSaved());
        Main.getVignette().setHtmlFiles(v.getHtmlFiles());
        Main.getVignette().setImagesPathForHtmlFiles(v.getImagesPathForHtmlFiles());
        Main.getVignette().setLastPageValueMap(v.getLastPageValueMap());
        Main.getVignette().setPageViewList(v.getPageViewList());
        Main.getVignette().setSettings(v.getSettings());
        Main.getVignette().setHasFirstPage(v.isHasFirstPage());
        Main.getVignette().setFrameworkInformation(v.getFrameworkInformation());
    }

    public void openVignetteFromHomePage(ActionEvent actionEvent) {

        FileChooserHelper helper = new FileChooserHelper("Open");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Vignette file (*.vgn)", "*.vgn");
        List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
        filterList.add(extFilter);
        File vgnFile = helper.openFileChooser(filterList);
        if (vgnFile != null) {
            logger.info("OPENED VIGNETTE FROM HOME: " + vgnFile.getAbsolutePath());
            FileInputStream fi;
            ObjectInputStream oi;
            try {
                fi = new FileInputStream(vgnFile);
                oi = new ObjectInputStream(fi);
                Vignette vignette = (Vignette) oi.readObject();
                logger.info("Opened VGN " + vignette);
                File frameworkFile = null;
                File[] list = (new File(vgnFile.getParentFile().getAbsolutePath())).listFiles();
                for (File f : list) {
                    if (f.getName().endsWith("zip")) {
                        frameworkFile = f;
                        break;
                    }
                }
                if (frameworkFile != null) {
                    Main.setFrameworkZipFile(frameworkFile.getAbsolutePath());
                }
                Main.setMainFramework(vignette.getFrameworkInformation());
                if (frameworkFile != null) {
                    ReadFramework.unZipTheFrameWorkFile(frameworkFile);
                } else {
                    logger.error("NO FRAMEWORK FOUND WITHIN THE FILE!! ");
                }
                Main.getStage().setTitle(vignette.getVignetteName());
                manuallyCopyVignette(vignette);
                String path = vgnFile.getParent();
                File mainFolder = new File(path);

                Main.getVignette().setFolderPath(path);
                Main.getVignette().setSaved(true);
                Main.getVignette().setMainFolderPath(mainFolder.getParent());
                TabPaneController pane = Main.getVignette().getController();
                openEditor();
            } catch (Exception e) {
                logger.error("{Main} : openVignetteFromHome : Exception Opening Vignette ", e);
            }

        }
    }

    /**
     * public void addUndoStack(Node node) {
     * this.undoStack.push(node);
     * }
     * public Stack<Node> getUndoStack() {
     * return this.undoStack;
     * }
     * public void addRedoStack(Node node){
     * this.redoStack.push(node);
     * }
     * public Stack<Node> getRedoStack() {
     * return redoStack;
     * }
     */

}
