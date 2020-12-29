package MenuBar;

import Application.Main;
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


public class MenuBarController implements Initializable {
    FileMenuItem fileMenuItemClass = new FileMenuItem();
    HelpMenuItem help = new HelpMenuItem();
    VignetteMenuItem vignetteMenuItem = new VignetteMenuItem();

    @FXML
    Menu fileMenuItem;
    @FXML
    MenuItem stopPreviewMenu;
    @FXML
    MenuItem previewVignette;

    private RecentFiles recentFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
         recentFiles = new RecentFiles();
        recentFiles.createRecentFiles();
        Main.getInstance().setRecentFiles(recentFiles);

        createMenuItem();
        SeparatorMenuItem sep = new SeparatorMenuItem();
        fileMenuItem.getItems().add(sep);
        MenuItem exit = new MenuItem("Exit");
        fileMenuItem.getItems().add(exit);

        exit.setOnAction(event -> {
            fileMenuItemClass.exitApplication();});


    }

    // ----------FILE MENU ACTIONS------------
    public void createNewVignette(ActionEvent actionEvent) { fileMenuItemClass.createNewVignette();}
    public void openVignette(ActionEvent actionEvent) { fileMenuItemClass.openVignette(null,recentFiles, true);}
    public void getPreferences(ActionEvent actionEvent) { fileMenuItemClass.setPreferences(); }
    public void saveAsVignette(ActionEvent actionEvent) { fileMenuItemClass.saveAsVignette();}
    public void saveVignette(ActionEvent actionEvent) { fileMenuItemClass.saveVignette();}

    private void createMenuItem() {
        Iterator value = recentFiles.getRecentFiles().iterator();

        while (value.hasNext()) {
            MenuItem item = new MenuItem();
            File filepath = (File) value.next();
            item.setText(filepath.getName());
            fileMenuItem.getItems().add(item);
            item.setOnAction(event -> {
                fileMenuItemClass.openVignette(filepath,recentFiles, false);
            });
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



}
