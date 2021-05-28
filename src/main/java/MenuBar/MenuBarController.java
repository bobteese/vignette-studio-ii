package MenuBar;

import Application.Main;
import MenuBar.Edit.EditMenu;
import MenuBar.File.FileMenuItem;
import MenuBar.Help.HelpMenuItem;
import MenuBar.Vignette.VignetteMenuItem;
import RecentFiles.RecentFiles;
import javafx.event.ActionEvent;
import javafx.event.Event;
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

    // ----------FILE MENU ACTIONS------------

    /**
     *
     * ActionEvent actionevent is never used throughout

    public void createNewVignette(ActionEvent actionEvent) { fileMenuItemClass.createNewVignette();}
    public void openVignette(ActionEvent actionEvent) { fileMenuItemClass.openVignette(null,recentFiles, true);}
    public void getPreferences(ActionEvent actionEvent) { fileMenuItemClass.setPreferences(); }
    public void saveAsVignette(ActionEvent actionEvent) { fileMenuItemClass.saveAsVignette();}
    public void saveVignette(ActionEvent actionEvent) { fileMenuItemClass.saveVignette();}

    */

    public void createNewVignette() { fileMenuItemClass.createNewVignette();}
    public void openVignette() { fileMenuItemClass.openVignette(null,recentFiles, true);}
    public void getPreferences() { fileMenuItemClass.setPreferences(); }
    public void saveAsVignette() { fileMenuItemClass.saveAsVignette();}
    public void saveVignette() { fileMenuItemClass.saveVignette();}

    /**
     *
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
    public void tutorialAction(ActionEvent actionEvent) {
     help.openAlert("Tutorial");
    }
    public void openAboutMenu(ActionEvent actionEvent) { help.openAlert("About"); }

    // --------------- VIGNETTE MENU ACTIONS---------------
    public void editVignetteTitle(ActionEvent actionEvent) { vignetteMenuItem.editVignette(); }
    public void VignetteSettings(ActionEvent actionEvent) { vignetteMenuItem.editVignetteSettings();}
    public void openStyleEditor(ActionEvent actionEvent) { vignetteMenuItem.openStyleEditor(); }
    public void preViewVignette(ActionEvent actionEvent) {vignetteMenuItem.previewVignette(stopPreviewMenu, previewVignette);}
    public void stopPreview(ActionEvent actionEvent) { vignetteMenuItem.stopPreviewVignette(stopPreviewMenu,previewVignette);}

    // ------------------EDIT MENU ACTIONS -------------------


    /**
     * todo
     * @param actionEvent
     */
    public void undoAction(ActionEvent actionEvent) {
        editMenu.undo(redo);
        redo.setDisable(false);
    }

    /**
     * todo
     * @param actionEvent
     */
    public void redoAction(ActionEvent actionEvent) { editMenu.redo();}


    public void onFileMenuShowing(Event event) {
       if(recentFiles.isClearRecentFiles() && recentFileStartMenuIndex!=-1){

           fileMenuItem.getItems().remove(recentFileStartMenuIndex-1,recentFileEndMenuIndex+1);
           recentFileStartMenuIndex = -1;
           recentFileEndMenuIndex=-1;
           recentFiles.setClearRecentFiles(false);
       }

    }

    /**
     * todo why are we adding a seperator for recentfiles in this function?
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
