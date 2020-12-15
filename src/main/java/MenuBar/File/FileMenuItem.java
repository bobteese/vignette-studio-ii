package MenuBar.File;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelper.DialogHelper;
import DialogHelper.FileChooserHelper;
import DialogHelper.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import TabPane.TabPaneController;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileMenuItem implements FileMenuItemInterface {

    private Logger logger =  LoggerFactory.getLogger(FileMenuItem.class);
    @Override
    public void createNewVignette() {
        TextDialogHelper text = new TextDialogHelper("New Vignette","Enter new vignette name");
        Main.getInstance().changeTitle(text.getTextAreaValue());
    }
    @Override
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
                String path = vgnFile.getParent();
                Main.getVignette().setFolderPath(path);
                Main.getVignette().setSaved(true);
                Main.getVignette().setVignetteName(FilenameUtils.removeExtension(vgnFile.getName()));
                TabPaneController pane = Main.getVignette().getController();
                pane.getAnchorPane().getChildren().clear();
                addButtonToPane(vignette, pane);
            } catch (FileNotFoundException e) {
                logger.error("{}", "open vignette error: "+e);
                e.printStackTrace();
                System.err.println("open vignette error" + e.getMessage());
            }
            catch (IOException e) {
                logger.error("{}", "open vignette error: "+e);
                e.printStackTrace();
                System.err.println("open vignette error" + e.getMessage());
            }
            catch (ClassNotFoundException e) {
                logger.error("{}", "open vignette error: "+e);
                e.printStackTrace();
                System.err.println("open vignette error" + e.getMessage());
            }

        }
    }

    private void addButtonToPane(Vignette vignette, TabPaneController pane) {

        HashMap<String, VignettePage> pageViewList = vignette.getPageViewList();
        HashMap<String, Button> buttonPageMap = new HashMap<>();
        for (Map.Entry mapElement : pageViewList.entrySet()) {
            VignettePage page= (VignettePage) mapElement.getValue();
            ImageView droppedView = new ImageView(new Image(getClass().getResourceAsStream(ConstantVariables.IMAGE_RESOURCE_PATH)));
           Button button= pane.createVignetteButton(page,droppedView,page.getPosX(), page.getPosY(),page.getPageType());
           buttonPageMap.put(page.getPageName(),button);
           pane.getPageNameList().add((String)mapElement.getKey());
        }
        for(Map.Entry buttonPage: buttonPageMap.entrySet()){

            String  page = (String) buttonPage.getKey();
            Button source = (Button) buttonPage.getValue();
            VignettePage vignettePage = vignette.getPageViewList().get(page);
            if(vignettePage.getConnectedTo()!= null) {
                Button target = buttonPageMap.get(vignettePage.getConnectedTo());

                Line line = new Line();
                line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds b = source.getBoundsInParent();
                    return b.getMinX() + b.getWidth() / 2 ;
                }, source.boundsInParentProperty()));
                line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds b = source.getBoundsInParent();
                    return b.getMinY() + b.getHeight() / 2 ;
                }, source.boundsInParentProperty()));
                line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds b = target.getBoundsInParent();
                    return b.getMinX() + b.getWidth() / 2 ;
                }, target.boundsInParentProperty()));
                line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds b = target.getBoundsInParent();
                    return b.getMinY() + b.getHeight() / 2 ;
                }, target.boundsInParentProperty()));

                pane.getAnchorPane().getChildren().add(line);

            }


        }


    }
    @Override
    public void setPreferences() {

        GridPaneHelper paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Recent Files: ", 1, 1);
        paneHelper.addNumberSpinner(1,1,Integer.MAX_VALUE,2,1);
        paneHelper.addLabel("",1,2);
        paneHelper.addButton("Clear Recent Files",2,2);
        paneHelper.createGrid("Preferences",null, "Save","Cancel");


    }

    @Override
    public void exitApplication() {
         DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION,"Message",null,
                                               "Are you sure you want to exit?",false);
         if(helper.getOk()){
             Platform.exit();
             System.exit(0);
         }
    }

    @Override
    public void saveAsVignette() {
      Main.getVignette().saveAsVignette(true);
    }
    @Override
    public void saveVignette() {Main.getVignette().saveAsVignette(false);}
}
