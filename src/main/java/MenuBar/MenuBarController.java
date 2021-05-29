package MenuBar;

import Application.Main;
import MenuBar.Edit.EditMenu;
import MenuBar.File.FileMenuItem;
import MenuBar.Help.HelpMenuItem;
import MenuBar.Vignette.VignetteMenuItem;
import RecentFiles.RecentFiles;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;


/**
 * This class is the fx controller for the menu.fxmls
 */
public class MenuBarController implements Initializable {
    FileMenuItem fileMenuItemClass = new FileMenuItem();
    HelpMenuItem help = new HelpMenuItem();
    EditMenu editMenu = new EditMenu();
    VignetteMenuItem vignetteMenuItem = new VignetteMenuItem();
    int recentFileStartMenuIndex = -1;
    int recentFileEndMenuIndex = -1;



    @FXML
    Menu fileMenuItem;
    @FXML
    MenuItem stopPreviewMenu;
    @FXML
    MenuItem previewVignette;
    @FXML
    MenuItem redo;

    private RecentFiles recentFiles;

    /**
     * Initializes controller after root element has been completely processed.
     * Creates a new ArrayDeque of recentfiles.
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recentFiles = new RecentFiles();
        recentFiles.createRecentFiles();
        Main.getInstance().setRecentFiles(recentFiles);

        createMenuItem();
        menuAddExit();

    }

    /**
     * All File Menu action calls start here.
     */
    // ----------FILE MENU ACTIONS------------
    public void createNewVignette() { fileMenuItemClass.createNewVignette();}
    public void openVignette() { fileMenuItemClass.openVignette(null,recentFiles, true);}
    public void getPreferences() { fileMenuItemClass.setPreferences(); }
    public void saveAsVignette() { fileMenuItemClass.saveAsVignette();}
    public void saveVignette() { fileMenuItemClass.saveVignette();}

    /**
     * todo
     */
    private void createMenuItem() {
        Iterator value = recentFiles.getRecentFiles().iterator();
        int i =0;

        while (value.hasNext()) {

            MenuItem item = new MenuItem();
            File filepath = (File) value.next();
            item.setText(filepath.getName());
            fileMenuItem.getItems().add(item);
            item.setOnAction(event -> {
                fileMenuItemClass.openVignette(filepath,recentFiles, false);
            });
            if(i==0){
              recentFileStartMenuIndex = fileMenuItem.getItems().indexOf(item);
              System.out.println(recentFileStartMenuIndex);
            }
            i++;
            if(i==recentFiles.getRecentFiles().size()){
                recentFileEndMenuIndex = fileMenuItem.getItems().indexOf(item);
                System.out.println(recentFileEndMenuIndex);
            }
        }
    }


    //-----------------HELP MENU ACTIONS -----------------------
    public void tutorialAction() {
        help.openAlert("Tutorial");
    }
    public void openAboutMenu(ActionEvent actionEvent) { help.openAlert("About"); }


    /**
     * All Vignette menu Action calls start here.
     */
    // --------------- VIGNETTE MENU ACTIONS---------------
    public void editVignetteTitle() { vignetteMenuItem.editVignette(); }
    public void VignetteSettings() { vignetteMenuItem.editVignetteSettings();}
    public void openStyleEditor() { vignetteMenuItem.openStyleEditor(); }
    public void preViewVignette() {vignetteMenuItem.previewVignette(stopPreviewMenu, previewVignette);}
    public void stopPreview() { vignetteMenuItem.stopPreviewVignette(stopPreviewMenu,previewVignette);}


    /**
     * All Edit Menu action calls start here.
     */


    // ------------------EDIT MENU ACTIONS -------------------
    public void undoAction() {
        editMenu.undo(redo);
        redo.setDisable(false);
    }

    public void redoAction() { editMenu.redo();}


    /**
     * todo understand recentfiles then doc this
     */
    public void onFileMenuShowing() {
       if(recentFiles.isClearRecentFiles() && recentFileStartMenuIndex!=-1){

           fileMenuItem.getItems().remove(recentFileStartMenuIndex-1,recentFileEndMenuIndex+1);
           recentFileStartMenuIndex = -1;
           recentFileEndMenuIndex=-1;
           recentFiles.setClearRecentFiles(false);
       }

    }

    /**
     * todo why are we adding a seperator for recentfiles in this function?
     * Calls exitApplication() in FileMenuItem.java
     *
     */
    public void menuAddExit(){
        if(recentFiles.getRecentFiles().size()!=0) {
            SeparatorMenuItem sep = new SeparatorMenuItem();
            fileMenuItem.getItems().add(sep);
        }
        MenuItem exit = new MenuItem("Exit");
        fileMenuItem.getItems().add(exit);

        exit.setOnAction(event -> {
            fileMenuItemClass.exitApplication();});
    }



}
