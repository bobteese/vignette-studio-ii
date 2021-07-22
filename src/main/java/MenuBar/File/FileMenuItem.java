package MenuBar.File;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.ErrorHandler;
import DialogHelpers.FileChooserHelper;
import DialogHelpers.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import RecentFiles.RecentFiles;
import SaveAsFiles.SaveAsVignette;
import TabPane.TabPaneController;
import Vignette.Framework.Framework;
import Vignette.Framework.ReadFramework;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;

/**
 * The FileMenuItem.java class represents the tasks a user can perform when they click on the "File" Menu option.
 */
import javafx.stage.Stage;

public class FileMenuItem implements FileMenuItemInterface {

    private Logger logger =  LoggerFactory.getLogger(FileMenuItem.class);

    /** todo understand how a vignette is created
     *Deals with creating a new vignette.
     */

    public boolean saveVignetteBeforeOtherOperation(){
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
        Boolean isCanclled = false;
        if (result.get().getText().equals("Yes")) {
            if(Main.getVignette().isSaved()){
                Main.getVignette().saveAsVignette(false);
            }
            else {
                Main.getVignette().saveAsVignette(true);
            }

        }
        if (result.get().getText().equals("Cancel")){
            return true;
        }
        return false;
    }
    @Override
    public void createNewVignette() {
        boolean isCanclled = saveVignetteBeforeOtherOperation();
        if(!isCanclled) {
            try {
                openedVignette = null;
                Main.getInstance().stop();
                Main.getInstance().start(Main.getStage());
                Main.getStage().setMaximized(true);

                SaveAsVignette saveAsVignette = new SaveAsVignette();
                saveAsVignette.fileChoose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static File vgnFile;
    public static Vignette openedVignette;
    /**
     * Function that deals with opening an existing vignette in vignette studio ii using FileChooserHelper
     * This function is used in MenuBarController.java
     * @param file null
     * @param recentFiles (ArrayDeque of recently created vignettes)
     * @param fileChooser
     */
    @Override
    public void openVignette(File file, RecentFiles recentFiles, boolean fileChooser) {
//        Main.getStage().setMaximized(true);
        Main.openExistingFramework = true;

//        File vgnFile = null;
        if(fileChooser) {
            FileChooserHelper helper = new FileChooserHelper("Open");
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Vignette file (*.vgn)", "*.vgn");
            List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
            filterList.add(extFilter);
            vgnFile = helper.openFileChooser(filterList);
        }
        else{
            vgnFile = file;
        }
        if(vgnFile!=null){
            recentFiles.addRecentFile(vgnFile);
            FileInputStream fi;
            ObjectInputStream oi ;
            try {
                fi = new FileInputStream(vgnFile);
                oi = new ObjectInputStream(fi);
                openedVignette = (Vignette) oi.readObject();

                try {
                    Main.getInstance().stop();
                    Main.getInstance().start(Main.getStage());
                    Main.getStage().setMaximized(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (openedVignette.getFrameworkInformation().getSerialNumber() == Long.MAX_VALUE){
                    System.out.println("OPENED VIGNETTE WAS CREATED BY DEFAULT FRAMEWORK!! ");
                }else{
                    System.out.println("NEED TO SELECT EXTERNAL FRAMEWORK!!");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert");
                    alert.setContentText("Default framework doesnt work, try getting "+openedVignette.getFrameworkInformation().getFrameworkName()+" (maybe from: "+openedVignette.getFrameworkInformation().getFrameworkPath()+" )");
                    alert.showAndWait();
                    (new Main()).chooseDirectory();
                }
                Main.getVignette().setSettings(null);
                Main.getVignette().setSettings(openedVignette.getSettings());
                Main.getInstance().changeTitle(openedVignette.getVignetteName());
                System.out.println(openedVignette.getFrameworkInformation());
                String path = vgnFile.getParent();
                Main.getVignette().setFolderPath(path);
                Main.getVignette().setSaved(true);
                Main.getVignette().setVignetteName(FilenameUtils.removeExtension(vgnFile.getName()));
                TabPaneController pane = Main.getVignette().getController();
                pane.getAnchorPane().getChildren().clear();
                addButtonToPane(openedVignette, pane);
                for (Map.Entry<String, VignettePage> entry : Main.getVignette().getPageViewList().entrySet()) {
                    pane.makeFinalConnection(entry.getValue());
                }

//                selectedFramework(vgnFile, vignette);
                //-------Vignette Selected---------
//                System.out.println("FILE CHOOSER 1!!");
//                final FileChooser selectFramework = new FileChooser();
//                selectFramework.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("zip files", "*.zip"));
//                selectFramework.setTitle("Select a Directory from the vignette");
//                selectFramework.setInitialDirectory(new File(System.getProperty("user.home")));
//                File dir = selectFramework.showOpenDialog(Main.getStage());
//                String dirName = "";
//                if(dir!=null){
//                    dirName = dir.getName();
//                }else{
//                    dirName = "framework";
//                }
//                if(Framework.frameworkIsPresent(new FileInputStream(ConstantVariables.FRAMEWORK_VERSION_FILE_PATH), dirName)){
//                    String content = ReadFramework.readFrameworkVersionFile();
//                    System.out.println(content);
//                }
                //-------Vignette Selected---------

//                Main.getVignette().getController().getAnchorPane().getChildren().clear();
//                Main.getVignette().getController().getPagesTab().setDisable(true);
//                Main.getVignette().getController().getTabPane().getSelectionModel().select(Main.getVignette().getController().getVignetteTab());

            } catch (FileNotFoundException e) {
                ErrorHandler error = new ErrorHandler(Alert.AlertType.ERROR,"Error",null, "File not found");
                error.showAndWait();
                logger.error("{}", "open vignette error: "+e);
                e.printStackTrace();
                System.err.println("open vignette error" + e.getMessage());
            }
            catch (IOException e) {
                ErrorHandler error = new ErrorHandler(Alert.AlertType.ERROR,"Error",null, "Error Opening Vignette");
                error.showAndWait();
                logger.error("{}", "open vignette error: "+e);
                e.printStackTrace();
                System.err.println("open vignette error" + e.getMessage());
            }
            catch (ClassNotFoundException e) {
                ErrorHandler error = new ErrorHandler(Alert.AlertType.ERROR,"Error",null, "Error Opening Vignette");
                error.showAndWait();
                logger.error("{}", "open vignette error: "+e);
                e.printStackTrace();
                System.err.println("open vignette error" + e.getMessage());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

        }
    }
    public static void selectedFramework(){
//        Framework selectedToOpen = Main.getVignette().getFrameworkInformation();
        Framework selectedToOpen = Main.getMainFramework();
        System.out.println("openedVignette.getFrameworkInformation(): "+openedVignette.getFrameworkInformation());
        if(openedVignette.getFrameworkInformation().equals(selectedToOpen)){
            System.out.println("DIR SELECTED TO OPEN IS MATCHED WITH THE ONE USED TO CREATE ");
        }else{
            ErrorHandler error = new ErrorHandler(Alert.AlertType.ERROR,"Error",null, "Different files Selected!!");
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
     * @param vignette
     * @param pane
     */
    public static void addButtonToPane(Vignette vignette, TabPaneController pane) {

        HashMap<String, VignettePage> pageViewList = vignette.getPageViewList();
        HashMap<String, Button> buttonPageMap = new HashMap<>();
        for (Map.Entry mapElement : pageViewList.entrySet()) {

            HashMap<String,Image> imageMap = pane.getImageMap();
            VignettePage page= (VignettePage) mapElement.getValue();
            Image buttonImage = imageMap.get(page.getPageType());
            ImageView droppedView = new ImageView(buttonImage);

            Button button= pane.createVignetteButton(page,droppedView,page.getPosX(), page.getPosY(),page.getPageType());
           buttonPageMap.put(page.getPageName(),button);
           pane.getPageNameList().add((String)mapElement.getKey());
        }
        for(Map.Entry buttonPage: buttonPageMap.entrySet()){

            String  page = (String) buttonPage.getKey();
            Button source = (Button) buttonPage.getValue();
            VignettePage vignettePage = vignette.getPageViewList().get(page);
            if(vignettePage.getConnectedTo()!= null) {
                VignettePage pageTwo = vignette.getPageViewList().get(vignettePage.getConnectedTo());
                Button target = buttonPageMap.get(vignettePage.getConnectedTo());

//                Line connector = new Line(10.0f, 10.0f, 100.0f, 40.0f);
//                Arrow arrow = new Arrow(source,target);
//                // create a Group
//                Group group = new Group(arrow);
//
//                arrow.setEndX(connector.getEndX());
//                arrow.setEndY(connector.getEndY());
//                arrow.setStartX(connector.getStartX());
//                arrow.setStartY(connector.getStartY());
//
//                pane.getAnchorPane().getChildren().add(group);
                pane.checkPageConnection(vignettePage,pageTwo,source,target);
            }


        }

    }


    /**
     *This function allows the user to set the preferred number of recent vignettes they have easy access to.
     *The dialog box that is opened when the user clicks on the Preferences option is created here.
     *All the options, and information on the dialog box is below.
     */
    @Override
    public void setPreferences() {

        GridPaneHelper paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Recent Files: ", 1, 1);
        Spinner<Integer> spinner = paneHelper.addNumberSpinner(Main.getRecentFiles().getNumRecentFiles(),1,Integer.MAX_VALUE,2,1);
        paneHelper.addLabel("",1,2);
       Button button =  paneHelper.addButton("Clear Recent Files",2,2);
        button.setOnAction(event -> {
          Main.getRecentFiles().clearRecentFiles();
        });
        paneHelper.createGrid("Preferences",null, "Save","Cancel");
        boolean isSaved = paneHelper.isSave();

        if(isSaved){
           Main.getRecentFiles().saveNumberRecentFiles(spinner.getValue());
        }


    }


    /**
     * This function allows the user to exit from vignette studio through the File option using DialogHelper.java
     * exitApplication() is called by menuAddExit() in MenuBarController.java
     */
    @Override
    public void exitApplication() {
         DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION,"Message",null,
                                               "Are you sure you want to exit?",false);
         if(helper.getOk()){
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
      Main.getVignette().saveAsVignette(true);

      String filename = Main.getVignette().getVignetteName();
      String folderpath = Main.getVignette().getFolderPath();
      recentFiles.addRecentFile(new File(folderpath+"\\"+filename+".vgn"));

    }

    /**
     *
     */
    @Override
    public void saveVignette() {Main.getVignette().saveAsVignette(false);}



    @Override
    public void openInExplorer(RecentFiles recentFiles) throws IOException {
        String folderpath = Main.getVignette().getFolderPath();


        if(folderpath!=null) {
            //when saving as in the current session, the path has forward slashes in it for some reason.
            folderpath= folderpath.replace("/", "\\");
            System.out.println("Opening  folder location at "+folderpath);
            Desktop.getDesktop().open(new File(folderpath));
        }
        else {
            System.out.println("You need to save as");
            Alert needToSaveAs = new Alert(Alert.AlertType.INFORMATION);
            needToSaveAs.setHeaderText("Current Vignette hasnt been saved to a directory");
            needToSaveAs.show();
        }
    }


    @Override
    public void scormExport()
    {
        System.out.println("Exporting in scorm format");

        Main.getVignette().saveAsVignette(true);

        String folderpath = Main.getVignette().getFolderPath();
        try {
            File manifest  = new File(folderpath+ "//" + "imsmanifest.xml");
            if (manifest.createNewFile()) {
                System.out.println("File created: " + manifest.getName());
                writeToManifest(manifest);
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public void writeToManifest(File manifest) throws IOException {
        String folderpath = Main.getVignette().getFolderPath();
        List<String> results = new ArrayList<String>();

        File dir = new File(folderpath);

        String xml ="<?xml version=\"1.0\" standalone=\"no\" ?>\n" +
                "<!--\n" +
                "Minimum calls, run-time example. SCORM 2004 3rd Edition.\n" +
                "\n" +
                "Provided by Rustici Software - http://www.scorm.com\n" +
                "\n" +
                "This example builds upon the single file per SCO example to add the bare minimum SCORM \n" +
                "run-time calls.\n" +
                "-->\n" +
                "\n" +
                "<manifest identifier=\"%s\" version=\"1\"\n" +
                "          xmlns=\"http://www.imsglobal.org/xsd/imscp_v1p1\"\n" +
                "          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "          xmlns:adlcp=\"http://www.adlnet.org/xsd/adlcp_v1p3\"\n" +
                "          xmlns:adlseq=\"http://www.adlnet.org/xsd/adlseq_v1p3\"\n" +
                "          xmlns:adlnav=\"http://www.adlnet.org/xsd/adlnav_v1p3\"\n" +
                "          xmlns:imsss=\"http://www.imsglobal.org/xsd/imsss\"\n" +
                "          xsi:schemaLocation=\"http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd\n" +
                "                              http://www.adlnet.org/xsd/adlcp_v1p3 adlcp_v1p3.xsd\n" +
                "                              http://www.adlnet.org/xsd/adlseq_v1p3 adlseq_v1p3.xsd\n" +
                "                              http://www.adlnet.org/xsd/adlnav_v1p3 adlnav_v1p3.xsd\n" +
                "                              http://www.imsglobal.org/xsd/imsss imsss_v1p0.xsd\">\n" +
                "\n" +
                "  <metadata>\n" +
                "    <schema>ADL SCORM</schema>\n" +
                "    <schemaversion>2004 3rd Edition</schemaversion>\n" +
                "  </metadata>\n" +
                "  <organizations default=\"%s\">\n" +
                "    <organization identifier=\"%s\">\n" +
                "      <title>%s</title>\n" +
                "        <item identifier=\"main_item\" identifierref=\"main_resource\">\n" +
                "          <title>%s</title>\n" +
                "        </item>\n" +
                "    </organization>\n" +
                "  </organizations>\n" +
                "\n" +
                "  <resources>\n" +
                "    <resource identifier=\"main_resource\" type=\"webcontent\" adlcp:scormType=\"sco\"  href=\"main.html\">";


        String close = "    </resource>\n" +
                "  </resources>\n" +
                "</manifest>";


        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(manifest, true)));


        try {

            String titleName = Main.getVignette().getVignetteName();
            printWriter.printf(xml,titleName,titleName,titleName,titleName,titleName);

            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    //System.out.println("Directory");
                    showFiles(file.listFiles(),printWriter); // Calls same method again.
                }
            }

            printWriter.print(close);
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        finally {
            printWriter.close();
        }
    }

    public  void showFiles(File[] files, PrintWriter printWriter) throws IOException {

        String resource ="      <file href=\"%s\"/>\n";

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Another Directory");
                showFiles(file.listFiles(),printWriter); // Calls same method again.
            } else {

                String extension = FilenameUtils.getExtension(file.getAbsolutePath());
                //check extensions
                if (extension.equalsIgnoreCase("html") || extension.equalsIgnoreCase("js") ||
                        extension.equalsIgnoreCase("css") || extension.equalsIgnoreCase("png") ||
                extension.equalsIgnoreCase("jpg"))

                {
                    String path = file.getAbsolutePath();
                    String base = Main.getVignette().getFolderPath();
                    String relative = new File(base).toURI().relativize(new File(path).toURI()).getPath();
                    System.out.println("relative path = " + relative);
                    //write to xml file
                    printWriter.printf(resource,relative);
                }
            }
        }
    }



}
