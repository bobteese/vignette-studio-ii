package MenuBar.File;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelper.FileChooserHelper;
import DialogHelper.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import TabPane.TabPaneController;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileMenuItem {

    public void createNewVignette() {
        TextDialogHelper text = new TextDialogHelper("New Vignette","Enter new vignette name");
        Main.getInstance().changeTitle(text.getTextAreaValue());
    }
    public void openVignette() {
        FileChooserHelper helper = new FileChooserHelper("Open");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Vignette file (*.vgn)", "*.vgn");
        List<FileChooser.ExtensionFilter> filterList = new ArrayList<>();
        filterList.add(extFilter);
        File vgnFile = helper.openFileChooser(filterList);
        if(vgnFile!=null){
            System.out.println(vgnFile.getPath());
            FileInputStream fi;
            ObjectInputStream oi ;
            try {
                fi = new FileInputStream(vgnFile);
                oi = new ObjectInputStream(fi);
                Vignette vignette = (Vignette) oi.readObject();
                Main.getInstance().changeTitle(vignette.getVignetteName());
                TabPaneController pane = Main.getVignette().getController();
                pane.getAnchorPane().getChildren().clear();
                addButtonToPane(vignette, pane);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void addButtonToPane(Vignette vignette, TabPaneController pane) {

        HashMap<String, VignettePage> pageViewList = vignette.getPageViewList();
        for (Map.Entry mapElement : pageViewList.entrySet()) {
            VignettePage page= (VignettePage) mapElement.getValue();
            ImageView droppedView = new ImageView(new Image(getClass().getResourceAsStream(ConstantVariables.IMAGE_RESOURCE_PATH)));
            pane.createVignetteButton(page,droppedView,page.getPosX(), page.getPosY(),page.getPageType());
        }


    }
    public void setPreferences() {

        GridPaneHelper paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Recent Files: ", 1, 1);
        paneHelper.addNumberSpinner(1,1,Integer.MAX_VALUE,2,1);
        paneHelper.addLabel("",1,2);
        paneHelper.addButton("Clear Recent Files",2,2);
        paneHelper.createGrid("Preferences",null, "Save","Cancel");


    }
    public void saveAsVignette() {
       Main.getVignette().saveAsVignette(true);
    }
    public void saveVignette() {Main.getVignette().saveAsVignette(false);}
}
