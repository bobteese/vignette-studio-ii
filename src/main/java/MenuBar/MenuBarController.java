package MenuBar;

import Application.Main;
import MenuBar.File.FileMenuItem;
import MenuBar.Help.HelpMenuItem;
import MenuBar.Vignette.VignetteMenuItem;
import Preview.VignetteServerException;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public class MenuBarController {
    FileMenuItem file = new FileMenuItem();
    HelpMenuItem help = new HelpMenuItem();
    VignetteMenuItem vignetteMenuItem = new VignetteMenuItem();

    // ----------FILE MENU ACTIONS------------
    public void createNewVignette(ActionEvent actionEvent) { file.createNewVignette();}
    public void openVignette(ActionEvent actionEvent) { file.openVignette();}
    public void getPreferences(ActionEvent actionEvent) { file.setPreferences(); }
    public void saveAsVignette(ActionEvent actionEvent) { file.saveAsVignette();}
    public void saveVignette(ActionEvent actionEvent) { file.saveVignette();}


    //-----------------HELP MENU ACTIONS -----------------------
    public void tutorialAction(ActionEvent actionEvent) {
     help.openAlert("Tutorial");
    }
    public void openAboutMenu(ActionEvent actionEvent) { help.openAlert("About"); }

    // --------------- VIGNETTE MENU ACTIONS---------------
    public void editVignetteTitle(ActionEvent actionEvent) { vignetteMenuItem.editVignette(); }
    public void VignetteSettings(ActionEvent actionEvent) { vignetteMenuItem.editVignetteSettings();}
    public void openStyleEditor(ActionEvent actionEvent) { vignetteMenuItem.openStyleEditor(); }


    public void preViewVignette(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setResizable(true);
        alert.setWidth(500);
        alert.setHeight(500);
        alert.setTitle("Preview Vignette");
        alert.setContentText("Changes will not be visible unless they are saved before previewing.");
        ButtonType okButton = new ButtonType("Save and Preview", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Preview Without Saving", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
        for ( ButtonType bt : alert.getDialogPane().getButtonTypes() )
        {
            Button button = ( Button ) alert.getDialogPane().lookupButton( bt );
            button.setPrefWidth(200);

        }
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get().getText().equals("Save and Preview")) {
            Main.getVignette().saveAsVignette(true);
        }
        else if(result.get().getText().equals("Preview Without Saving")){
            System.out.println("Previwe without saving");
        }
        else {
            System.out.println("cancel"+result.get().toString());
        }
        try {
            Main.getVignette().previewVignette(null, -1);
        } catch (VignetteServerException e) {
            //handleVignetteServerException(c, e);

        }

        URL vigPreview = null;
        try {
            vigPreview = Main.getVignette().getPreviewURL();
        } catch (VignetteServerException e) {
           // handleVignetteServerException(c, e);

        }
        boolean preview = true;
        if (preview) {
            // Open the vignette url in the browser
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(vigPreview.toURI());
                } catch (IOException e) {
                   // handleIOException(c, e);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + vigPreview.toString());
                } catch (IOException e) {
                   // handleIOException(c, e);
                }
            }
        } else {

        }


    }
}
