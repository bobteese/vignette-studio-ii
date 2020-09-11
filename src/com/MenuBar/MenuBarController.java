package com.MenuBar;

import com.GridPaneHelper.GridPaneHelper;
import com.MenuBar.File.FileMenuItem;
import com.MenuBar.Help.HelpMenuItem;
import com.MenuBar.Vignette.VignetteMenuItem;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;

public class MenuBarController {
    FileMenuItem file = new FileMenuItem();
    HelpMenuItem help = new HelpMenuItem();
    VignetteMenuItem vignette = new VignetteMenuItem();

    // ----------FILE MENU ACTIONS------------
    public void createNewVignette(ActionEvent actionEvent) { file.createNewVignette();}
    public void openVignette(ActionEvent actionEvent) { file.openVignette();}
    public void getPreferences(ActionEvent actionEvent) { file.setPreferences(); }


    //-----------------HELP MENU ACTIONS -----------------------
    public void tutorialAction(ActionEvent actionEvent) {
     help.openAlert("Tutorial");
    }
    public void openAboutMenu(ActionEvent actionEvent) { help.openAlert("About"); }

    // --------------- VIGNETTE MENU ACTIONS---------------
    public void editVignetteTitle(ActionEvent actionEvent) {vignette.editVignette(); }
    public void VignetteSettings(ActionEvent actionEvent) { vignette.editVignetteSettings();}
    public void openStyleEditor(ActionEvent actionEvent) { vignette.openStyleEditor(); }
}
