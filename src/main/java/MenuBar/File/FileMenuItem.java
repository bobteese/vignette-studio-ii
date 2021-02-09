package MenuBar.File;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelper.DialogHelper;
import DialogHelper.FileChooserHelper;
import DialogHelper.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import RecentFiles.RecentFiles;
import TabPane.TabPaneController;
import Vignette.Page.Arrow;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


public class FileMenuItem implements FileMenuItemInterface {

    private Logger logger =  LoggerFactory.getLogger(FileMenuItem.class);
    @Override
    public void createNewVignette() {

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
            isCanclled = true;
        }

        if(!isCanclled) {

            Main.getVignette().getController().getAnchorPane().getChildren().clear();
            Main.getVignette().getController().getPagesTab().setDisable(true);
            Main.getVignette().getController().getTabPane().getSelectionModel().select
                    (Main.getVignette().getController().getVignetteTab());
            TextDialogHelper text = new TextDialogHelper("New Vignette", "Enter new vignette name");
            Main.getInstance().changeTitle(text.getTextAreaValue());
        }

    }
    @Override
    public void openVignette(File file,RecentFiles recentFiles, boolean fileChooser) {
        File vgnFile = null;
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
            System.out.println(vgnFile.getPath());
            FileInputStream fi;
            ObjectInputStream oi ;
            try {
                fi = new FileInputStream(vgnFile);
                oi = new ObjectInputStream(fi);
                Vignette vignette = (Vignette) oi.readObject();
                Main.getVignette().setSettings(vignette.getSettings());
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
    @Override
    public void setPreferences() {

        GridPaneHelper paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Recent Files: ", 1, 1);
        Spinner<Integer> spinner = paneHelper.addNumberSpinner(5,1,Integer.MAX_VALUE,2,1);
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
