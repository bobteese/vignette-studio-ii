/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Application;

import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.FileChooserHelper;
import GridPaneHelper.GridPaneHelper;
import MenuBar.File.FileMenuItem;
import MenuBar.Help.JavaVersion;
import Preview.VignetteServerException;
import Preview.VignetterServer;
import RecentFiles.RecentFiles;
import TabPane.TabPaneController;
import Vignette.Framework.FileResourcesUtils;
import Vignette.Framework.FilesFromResourcesFolder;
import Vignette.Framework.Framework;
import Vignette.Framework.ReadFramework;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
    //todo I added this
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
     * Main entry point for the JavaFX application. User interface defined by means of a stage and scene. Stage is the
     * top level container. Scene is the container for all content.
     * Contains an undo and redo stack for all page related operations.
     * Loads object hierarchy from application.fxml.
     * application.fxml includes menu.fxml and tabs.fxml which are all elements on the main window of the vignette studio
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPaneHelper helper = new GridPaneHelper();
        TextField text = helper.addTextField(0, 2, 400);
        instance = this;
        this.vignette = anotherVignetteInstance();
        Scene homeScene = null;
        //Create the landing page.
        if(openedVignette==null){
            Parent homeRoot = FXMLLoader.load(getClass().getResource("/FXML/Home.fxml"));
            Main.primaryStage = primaryStage;
            Main.primaryStage.setTitle("Vignette Studio 2");
            Main.primaryStage.setMaximized(false);
            Main.primaryStage.setResizable(false);
//            this.primaryStage.resizableProperty().setValue(false);
            String protocol = Main.class.getResource("").getProtocol();
            if(Objects.equals(protocol, "jar")){
                Main.isJar = true;
            } else if(Objects.equals(protocol, "file")) {
                Main.isJar = false;
            }
            homeScene = new Scene(homeRoot);
            homeScene.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
            sc.setLayoutX(homeScene.getWidth() - sc.getWidth());
            sc.setMin(0);
            sc.setOrientation(Orientation.VERTICAL);
            sc.setPrefHeight(180);
            sc.setMax(360);

        }else{
            if (openedVignette.getFrameworkInformation().getSerialNumber() == Long.MAX_VALUE){
                System.out.println("OPENED VIGNETTE WAS CREATED BY DEFAULT FRAMEWORK!! ");
                goAheadWithDefaultFramework();
                System.out.println(Main.getFrameworkZipFile());
            }else{
                System.out.println("NEED TO SELECT EXTERNAL FRAMEWORK!!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Alert");
                alert.setContentText("Default framework doesnt work, try getting "+openedVignette.getFrameworkInformation().getFrameworkName()+" (maybe from: "+openedVignette.getFrameworkInformation().getFrameworkPath()+" )");
                alert.showAndWait();
                (new Main()).chooseDirectory();
            }
            homeScene = openEditor();
        }

        Main.primaryStage.setScene(homeScene);
        Main.primaryStage.show();
        Main.primaryStage.setResizable(true);
        Main.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Exit", null, "Are you sure you want to exit?", false);
                if (helper.getOk()) {
                    try{
                        if(Main.getVignette()!=null)
                            Main.getVignette().stopPreviewVignette();
                        if(ReadFramework.getUnzippedFrameWorkDirectory()!=null && "".equalsIgnoreCase(ReadFramework.getUnzippedFrameWorkDirectory()))
                            ReadFramework.deleteDirectory(ReadFramework.getUnzippedFrameWorkDirectory());
                        File[] vignetteFolder = (new File(ConstantVariables.VIGNETTESTUDIO_PATH)).listFiles();
                        for(File temp:vignetteFolder){
                            if(temp.getAbsolutePath().endsWith(".zip")){
                                temp.delete();
                            }
                        }
//                            if(!Main.defaultFramework){
//                                ReadFramework.deleteDirectory(ReadFramework.getUnzippedFrameWorkDirectory());
//                            }
                    }catch (VignetteServerException e){
                        System.out.println("ERROR TO STOP: "+e.getMessage());
                    }
                    primaryStage.close();
                } else {
                    we.consume();
                }
            }
        });
    }


    public void chooseDirectory() throws IOException {
        File dir;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("zip files", "*.zip"));
        fileChooser.setTitle("Select a Directory from the vignette");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dir = fileChooser.showOpenDialog(Main.getStage());
        String dirName = "";
        if(dir!=null){
            System.out.println("PATH TO GIVE: "+dir.getAbsolutePath());
            Main.setFrameworkZipFile(dir.getAbsolutePath());
            dirName = dir.getName().substring(0, dir.getName().lastIndexOf("."));
            ReadFramework.unZipTheFrameWorkFile(new File(Main.getFrameworkZipFile()));
            instance = this;
            this.vignette = anotherVignetteInstance();
            setMainVignetteInformation(dirName);
            primaryStage.close();
            openEditor();
        }else{
            System.out.println("PRESSED CANCEL!");
        }
//        if(openedVignette!=null){
//            FileMenuItem.selectedFramework();
//        }
//        primaryStage.setMaximized(true);
    }

    private void setMainVignetteInformation(String dirName) {
        long serialNumber;
        Random random = new Random();
        if(dirName.equalsIgnoreCase(ConstantVariables.DEFAULT_FRAMEWORK_PATH)){
            serialNumber = Long.MAX_VALUE;
            System.out.println("SETTING MAIN FRAMEWORK TO DEFAULT!! ");
            Main.setMainFramework(new Framework(Main.getFrameworkZipFile(), dirName, serialNumber));
            return;
        }
        do{
            serialNumber = Math.abs(random.nextLong());
        }while (serialNumber==Long.MAX_VALUE);
        Framework f = new Framework(Main.getFrameworkZipFile(), dirName, serialNumber);
        System.out.println("CURRENT VIGNETTE FRAMEWORK NAME: "+f.getFrameworkName());
        if(!f.addToFrameworkVersionFile()){
            ArrayList<Framework> listOfFrameworks = ReadFramework.readFrameworkVersionFile();
            for(Framework framework : listOfFrameworks){
                if(framework.getFrameworkName().equalsIgnoreCase(f.getFrameworkName())){
                    Main.setMainFramework(framework);
                    break;
                }
            }
        }else{
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
//        Framework defaultFramework = new Framework(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
        String os = System.getProperty("os.name");
        System.out.println("NO EXTERNAL FRAMEWORK FOUND! SELECT MY DEFAULT ONE!!");
        Main.setFrameworkZipFile(ConstantVariables.DEFAULT_FRAMEWORK_FOLDER);
        Main.defaultFramework = true;
        if(!Main.isJar){
            System.out.println("NOT IS JAR");
            FilesFromResourcesFolder filesFromResourcesFolder = new FilesFromResourcesFolder();
            List<File> list =  filesFromResourcesFolder.getAllFilesFromResource(ConstantVariables.DEFAULT_RESOURCES);
            for(File f:list){
                if(f.getAbsolutePath().endsWith(".zip")){
                    Main.setFrameworkZipFile(f.getAbsolutePath());
                    break;
                }
            }
        }else{
            FileResourcesUtils fileResourcesUtils = new FileResourcesUtils();
            InputStream is= null;
            System.out.println("OS: "+os);
            if(os.trim().startsWith("Mac")){
                System.out.println("USING SEPARATOR");
                is = fileResourcesUtils.getFileFromResourceAsStream(ConstantVariables.DEFAULT_FRAMEWORK_PATH_USING_FILE_SEPARATOR);
            }else{
                System.out.println("WITHOUT USING SEPARATOR");
                is = fileResourcesUtils.getFileFromResourceAsStream(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
            }
            if(is!=null){
                System.out.println("OBTAINED IS TO BE NULL");
                is = fileResourcesUtils.getFileFromResourceAsStream("HTMLResources/framework.zip");
            }
            final File tempFile = File.createTempFile("framework", ".zip", new File(ConstantVariables.VIGNETTESTUDIO_PATH));
            try (FileOutputStream out = new FileOutputStream(tempFile))
            {
                IOUtils.copy(is, out);
            }
            Main.setFrameworkZipFile(tempFile.getAbsolutePath());
            System.out.println("FRAMEWORK FILE: "+Main.getFrameworkZipFile());
            tempFile.deleteOnExit();


//            FileResourcesUtils fileResourcesUtils = new FileResourcesUtils();
//            List<Path> paths =  fileResourcesUtils.getPathsFromResourceJAR(ConstantVariables.DEFAULT_RESOURCES);
//            for(Path p:paths){
//                if(p.toString().endsWith(".zip")){
//                    System.out.println("FOUND P: "+p);
//                    InputStream is = fileResourcesUtils.getFileFromResourceAsStream(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
//                    if(is!=null){
//                        System.out.println("OBTAINED IS TO BE NULL");
//                        is = fileResourcesUtils.getFileFromResourceAsStream("HTMLResources/framework.zip");
//                    }
//                    final File tempFile = File.createTempFile("framework", ".zip", new File(ConstantVariables.VIGNETTESTUDIO_PATH));
//                    try (FileOutputStream out = new FileOutputStream(tempFile))
//                    {
//                        IOUtils.copy(is, out);
//                    }
//                    Main.setFrameworkZipFile(tempFile.getAbsolutePath());
//                    System.out.println("FRAMEWORK FILE: "+Main.getFrameworkZipFile());
//                    tempFile.deleteOnExit();
//                    break;
//                }
//            }
        }
        setMainVignetteInformation(ConstantVariables.DEFAULT_FRAMEWORK_PATH);
        ReadFramework.unZipTheFrameWorkFile(new File(Main.getFrameworkZipFile()));
//        if(openedVignette!=null){
//            FileMenuItem.selectedFramework();
//        }
        openEditor();
    }
    public void makeVignetteStudioDir(){
        File file = new File(ConstantVariables.VIGNETTESTUDIO_PATH);
        try {
            file.mkdirs();
            System.out.println("Successfully created vignettestudio-ii folder");
        } catch (SecurityException e) {

            logger.error("{Recent Files}", e);
            e.printStackTrace();
            System.out.println("{Recent Files}"+ e);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Warning");
            alert.setContentText("Error creating .vignettestudio-ii folder");

        }
    }
    public Scene openEditor() throws IOException {
        makeVignetteStudioDir();
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Main.primaryStage.close();
        if(Main.getVignette()==null){
            System.out.println("NEED NEW VIGNETTE INSTANCE!");
            this.vignette = anotherVignetteInstance();
        }else if(Main.getVignette().getVignetteName()!=null){
            Main.getInstance().changeTitle(Main.getVignette().getVignetteName());
        }
        Main.getInstance().changeTitle(Main.getVignette().getVignetteName());
        Main.getVignette().setFrameworkInformation(Main.getMainFramework());

//        if(Main.getVignette()==null){
//            Main.getVignette().setController(FileMenuItem.openedVignette.getController());
//        }
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/application.fxml"));
        Main.primaryStage.setTitle("untitled");
        Main.primaryStage.setMaximized(true);
        Scene scene = new Scene(root,bounds.getWidth(), bounds.getHeight());
        scene.getStylesheets().add(getClass().getResource("/FXML/FXCss/stylesheet.css").toString());
        sc.setLayoutX(scene.getWidth() - sc.getWidth());
        sc.setMin(0);
        sc.setOrientation(Orientation.VERTICAL);
        sc.setPrefHeight(180);
        sc.setMax(360);
        Main.primaryStage.setResizable(true);
        Main.primaryStage.setScene(scene);
        Main.primaryStage.show();
//        Main.primaryStage.initStyle(StageStyle.DECORATED);
        Main.primaryStage.getIcons().add(new Image((getClass().getResourceAsStream(ConstantVariables.IMAGE_ICON_RESOURCE_PATH))));

        return scene;
    }


    public void openDocumentation() throws IOException {

        //System.out.println("Opening documentation");

        String inputPdf = "pdf/Vignette Studio Documentation.pdf";
        Path tempOutput = Files.createTempFile("Vignette Studio Documentation", ".pdf");
        tempOutput.toFile().deleteOnExit();
        System.out.println("tempOutput: " + tempOutput);
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(inputPdf)) {
            Files.copy(is, tempOutput, StandardCopyOption.REPLACE_EXISTING);
        }
        Desktop.getDesktop().open(tempOutput.toFile());

        //use this to view online.
        //getHostServices().showDocument("https://docs.google.com/document/d/1loa3WrsEVV23AzRGlEjfxi_o4JnWFRVLcyM_YH1ZjnI/edit");
    }

    public void openAbout()
    {

        String message = "<html><div style=\"font-size:18px\">Vignette Studio was created by the Vignette Dreamers as an " +
                "undergraduate senior project at Rochester Institute of Technology. Vignette Studio was created for the " +
                "<a href=\"http://livephoto.rit.edu/\">LivePhoto Physics</a> project. Dr. Robert Teese and Professor Tom Reichlmayr " +
                "sponsored the project, and Dr. Scott Hawker coached the team. Contributors include:<br><br><p>The Vignette Dreamers:<br>Peter-John Rowe, " +
                "Jake Juby, Monir Hossain, Thomas Connors, and Samuel Nelson</p> <br>Additional Developers:<br>Bradley Bensch, " +
                "Nick Fuschino, Rohit Garg, Peter Gyory, Chad Koppes, Trevor Koppes, Nicholas Krzysiak, Joseph Ksiazek, Jen Lamere, Cailin Li, " +
                "Robert Liedka, Nicolas McCurdy, Hector Pieiro II, Chirag Chandrakant Salian, Angel Shiwakoti, Nils Sohn, Brian Soulliard, " +
                "Juntian Tao, Gordon Toth, Devin Warren, Alexander Wilczek, Todd Williams, Brian Wyant, Asmita Hari, Jiwoo Baik and Felix Brink.<br><br>Vignette Studio " +
                "is &copy; 2014-2018, the LivePhoto Physics Project at Rochester Institute of Technology. Vignette Studio is licensed to you under the terms of the GNU General Public License (GPL). " +
                "The terms of the license can be found at <a href=\"http://www.gnu.org/licenses/gpl.html\">http://www.gnu.org/licenses/gpl.html</a>" +
                "<p style=\"text-align: center;\">Vignette Studio version 1.0</p>" +
                "<p style=\"text-align: center;\">Java version"+ JavaVersion.getFullVersion()+"</p>" +
                "</div></html>";



        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("About Vignette Studio");

        WebView webView = new WebView();
        webView.getEngine().loadContent(message);
        alert.getDialogPane().setContent(webView);;
        alert.showAndWait();
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
     * @param vignette
     */
//    public void setVignette(Vignette vignette) { this.vignette = vignette; }


    /**
     * Instance of the application is created on the JavaFX thread.
     *
     * @param args
     */
    public static boolean isJar = false;
    public static void main(String[] args) {
        launch(args);

        logger.debug("Hello from Logback");

        logger.debug("getNumber() : {}", 5);

    }

    /**
     * Returns the ArrayDeque of recent vignette files.
     * Used in setPreferences() of File.FileMenuItem.java when the user sets their preferred number of previous files to be displayed.
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

    public void openVignetteFromHomePage(ActionEvent actionEvent) {

//        (new FileMenuItem()).openVignette(null, recentFiles, true);
//        System.out.println("OPENED VIGNETTE FROM HOME: "+ openedVignette.getVignetteName());

        FileChooserHelper helper = new FileChooserHelper("Open");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Vignette file (*.vgn)", "*.vgn");
        List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
        filterList.add(extFilter);
        File vgnFile = helper.openFileChooser(filterList);
        if(vgnFile!=null){
            FileInputStream fi;
            ObjectInputStream oi ;
            try {
                fi = new FileInputStream(vgnFile);
                oi = new ObjectInputStream(fi);
                Vignette vignette = (Vignette) oi.readObject();
                File frameworkFile = null;
                File[] list = (new File(vgnFile.getParent())).listFiles();
                for(File f:list){
                    if(f.getName().endsWith("zip")){
                        frameworkFile = f;
                        break;
                    }
                }
                if(frameworkFile!=null){
                    Main.setFrameworkZipFile(frameworkFile.getAbsolutePath());
                }
//                ReadFramework.unZipTheFrameWorkFile(frameworkFile);
//                try {
//                    Main.getInstance().stop();
//                    Main.getInstance().start(Main.getStage());
//                    Main.getStage().setMaximized(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                Main.setMainFramework(vignette.getFrameworkInformation());
                if(frameworkFile!=null){
                    ReadFramework.unZipTheFrameWorkFile(frameworkFile);
                }else{
                    System.out.println("NO FRAMEWORK FOUND WITHIN THE FILE!!");
                }
                Main.getStage().setTitle(vignette.getVignetteName());
                Main.getVignette().setSettings(null);
                Main.getVignette().setSettings(vignette.getSettings());
                Main.getVignette().setPageViewList(vignette.getPageViewList());
                System.out.println("vignette.getFrameworkInformation(): "+vignette.getFrameworkInformation());
                String path = vgnFile.getParent();
                Main.getVignette().setFolderPath(path);
                Main.getVignette().setSaved(true);
                Main.getVignette().setVignetteName(FilenameUtils.removeExtension(vgnFile.getName()));
                TabPaneController pane = Main.getVignette().getController();
                openEditor();
//                pane.getAnchorPane().getChildren().clear();
//                addButtonToPane(openedVignette, pane);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
    public void addUndoStack(Node node) {
       this.undoStack.push(node);
    }
    public Stack<Node> getUndoStack() {
       return this.undoStack;
    }
    public void addRedoStack(Node node){
        this.redoStack.push(node);
    }
    public Stack<Node> getRedoStack() {
        return redoStack;
    }
     */

}
