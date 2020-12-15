package MenuBar;

import Application.Main;
import MenuBar.File.FileMenuItem;
import MenuBar.Help.HelpMenuItem;
import MenuBar.Vignette.VignetteMenuItem;
import Preview.VignetteServerException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;



public class MenuBarController {
    FileMenuItem file = new FileMenuItem();
    HelpMenuItem help = new HelpMenuItem();
    VignetteMenuItem vignetteMenuItem = new VignetteMenuItem();

    @FXML
    MenuItem stopPreviewMenu;

    // ----------FILE MENU ACTIONS------------
    public void createNewVignette(ActionEvent actionEvent) { file.createNewVignette();}
    public void openVignette(ActionEvent actionEvent) { file.openVignette();}
    public void getPreferences(ActionEvent actionEvent) { file.setPreferences(); }
    public void saveAsVignette(ActionEvent actionEvent) { file.saveAsVignette();}
    public void saveVignette(ActionEvent actionEvent) { file.saveVignette();}
    public void exitApplication(ActionEvent actionEvent) { file.exitApplication(); }


    //-----------------HELP MENU ACTIONS -----------------------
    public void tutorialAction(ActionEvent actionEvent) {
     help.openAlert("Tutorial");
    }
    public void openAboutMenu(ActionEvent actionEvent) { help.openAlert("About"); }

    // --------------- VIGNETTE MENU ACTIONS---------------
    public void editVignetteTitle(ActionEvent actionEvent) { vignetteMenuItem.editVignette(); }
    public void VignetteSettings(ActionEvent actionEvent) { vignetteMenuItem.editVignetteSettings();}
    public void openStyleEditor(ActionEvent actionEvent) { vignetteMenuItem.openStyleEditor(); }
    public void preViewVignette(ActionEvent actionEvent) {vignetteMenuItem.previewVignette(stopPreviewMenu);}
    public void stopPreview(ActionEvent actionEvent) { vignetteMenuItem.stopPreviewVignette(stopPreviewMenu);}


}
