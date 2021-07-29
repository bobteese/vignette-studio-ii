package MenuBar;

import Application.Main;
import MenuBar.Edit.EditMenu;
import MenuBar.File.FileMenuItem;
import MenuBar.Help.HelpMenuItem;
import MenuBar.Vignette.VignetteMenuItem;
import RecentFiles.RecentFiles;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.VignettePage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;









/**
 * This class is the fx controller for the menu.fxmls
 */
public class MenuBarController implements Initializable {
    FileMenuItem fileMenuItemClass = new FileMenuItem();
    HelpMenuItem help = new HelpMenuItem();

    //commenting out the editmenu option
    //EditMenu editMenu = new EditMenu();

    VignetteMenuItem vignetteMenuItem = new VignetteMenuItem();
    int recentFileStartMenuIndex = -1;
    int recentFileEndMenuIndex = -1;



    @FXML
    Menu fileMenuItem;
    @FXML
    MenuItem stopPreviewMenu;
    @FXML
    MenuItem previewVignette;

    /**
     * These FXML buttons were there in Asmitas undo/redo implementation.
    @FXML
    MenuItem undo;
    @FXML
    MenuItem redo;
    */

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
    public void saveAsVignette() { fileMenuItemClass.saveAsVignette(recentFiles);}
    public void saveVignette() { fileMenuItemClass.saveVignette();}

    public void openInExplorer() throws IOException {
        fileMenuItemClass.openInExplorer(recentFiles);}

    public void scormExport()
    {
        fileMenuItemClass.scormExport();
    }

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

            //code below lets you open a recent file from the file menu
            item.setOnAction(event -> {
                fileMenuItemClass.openVignette(filepath,recentFiles, false);
            });

            if(i==0){
              recentFileStartMenuIndex = fileMenuItem.getItems().indexOf(item);
                /**
                 * This is whats stored in fileMenuItem. Anything past index 9 gives an error when trying to display
                 *
                 * 0 MenuItem[id=newVignette, styleClass=[menu-item]]
                 * 1 MenuItem[id=openVignette, styleClass=[menu-item]]
                 * 2 SeparatorMenuItem@191572b6[styleClass=[menu-item, custom-menu-item, separator-menu-item]]
                 * 3 MenuItem[id=saveVignette, styleClass=[menu-item]]
                 * 4 MenuItem[id=saveAs, styleClass=[menu-item]]
                 * 5 SeparatorMenuItem@6f7923d3[styleClass=[menu-item, custom-menu-item, separator-menu-item]]
                 * 6 MenuItem@39453f14[styleClass=[menu-item]]
                 * 7 SeparatorMenuItem@6ea5cf37[styleClass=[menu-item, custom-menu-item, separator-menu-item]]
                 * 8 MenuItem@5c178097[styleClass=[menu-item]]
                 */
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
    public void openAboutMenu() { help.openAlert("About"); }
    public void openDocumentation() throws IOException {help.openDocumentation();}



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
    /**
    public void undoAction() {
        //editMenu.undo(redo);
        editMenu.undo(undo,redo);
        redo.setDisable(false);
    }

    public void redoAction() {
        //editMenu.redo();
        editMenu.redo(undo,redo);
    }
     */


    /**
     * todo understand recentfiles then doc this
     */
    public void onFileMenuShowing() {
       if(recentFiles.isClearRecentFiles() && recentFileStartMenuIndex!=-1){

           // removing the menu item seperators?
           fileMenuItem.getItems().remove(recentFileStartMenuIndex-1,recentFileEndMenuIndex+1);
           recentFileStartMenuIndex = -1;
           recentFileEndMenuIndex=-1;
           recentFiles.setClearRecentFiles(false);
       }

    }

    /**
     * Calls exitApplication() in FileMenuItem.java
     * Adding a seperator menu item between recent files and the exit option if there are recentfiles to display
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
