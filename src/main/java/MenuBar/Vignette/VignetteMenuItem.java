package MenuBar.Vignette;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import Preview.VignetteServerException;
import SaveAsFiles.SaveAsVignette;
import Vignette.Framework.ReadFramework;
import Vignette.Settings.VignetteSettings;
import Vignette.StyleEditor.CSSEditor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


public class VignetteMenuItem implements VignetteMenuItemInterface {

    private static final Logger logger = LoggerFactory.getLogger(VignetteMenuItem.class);


    /**
     * Once called by clicking on the Rename Vignette option, this function changes the name of the current vignette.
     * todo Change the function name to renameVignette to match behaviour.
     */
    @Override
    public void editVignette() {
        TextDialogHelper text;
        if(Main.getVignette().getVignetteName()!=null)
            text = new TextDialogHelper("Rename Vignette","Change the vignette title", Main.getVignette().getVignetteName());
        else
            text = new TextDialogHelper("Rename Vignette","Change the vignette title");
        Main.getInstance().changeTitle(text.getTextAreaValue());
        Main.getVignette().setVignetteName(text.getTextAreaValue());
        if(Main.getVignette().isSaved()){
            Path dir  = Paths.get(Main.getVignette().getFolderPath());
            System.out.println("PARENT FOLDER: "+dir.getParent());
            String oldPath = Main.getVignette().getFolderPath();
            SaveAsVignette saveAsVignette = new SaveAsVignette();
            saveAsVignette.createFolder(dir.getParent().toFile(), text.getTextAreaValue());
            ReadFramework.deleteDirectory(oldPath);
            System.out.println("Main.getVignette().getFolderPath();: "+Main.getVignette().getFolderPath());
        }
    }

    /**
     * This function deals with editing the vignette settings. Information about the instructor and course is entered in
     * the textfields of the generated dialog box.
     *
     */

    StringProperty cidProp = new SimpleStringProperty();
    StringProperty iverTitleProp = new SimpleStringProperty();
    StringProperty ivetProjectProp = new SimpleStringProperty();
    StringProperty ivetNameProp = new SimpleStringProperty();
    StringProperty schoolNameProp = new SimpleStringProperty();
    StringProperty schoolFullNameProp = new SimpleStringProperty();
    StringProperty instructorProp = new SimpleStringProperty();
    StringProperty courseNameProp = new SimpleStringProperty();
    StringProperty courseNumberProp = new SimpleStringProperty();
    StringProperty courseTermProp = new SimpleStringProperty();

    private void setDefaultSettingToTextField(VignetteSettings settings){
            cidProp.set(settings.getCid());
            iverTitleProp.set(settings.getIvetTitle());
            ivetProjectProp.set(settings.getIvetProject());
            ivetNameProp.set(settings.getIvet());
            schoolNameProp.set(settings.getSchool());
            schoolFullNameProp.set(settings.getSchoolFullName());
            instructorProp.set(settings.getInstructor());
            courseNameProp.set(settings.getCourseName());
            courseNumberProp.set(settings.getCourseNumber());
            courseTermProp.set(settings.getCourseTerm());
    }
    @Override
    public void editVignetteSettings(){
        GridPaneHelper paneHelper = new GridPaneHelper();
        VignetteSettings settings = Main.getVignette().getSettings() !=null? Main.getVignette().getSettings() :new VignetteSettings();
        setDefaultSettingToTextField(settings);

        paneHelper.addLabel("cid: ", 1, 2);
        TextField cid = paneHelper.addTextField(settings.getCid(),2,2,400);
        cid.textProperty().bindBidirectional(cidProp);

        paneHelper.addLabel("IVET Title: ", 1, 3);
        TextField ivetTitle = paneHelper.addTextField(2,3,400);
        ivetTitle.textProperty().bindBidirectional(iverTitleProp);

        paneHelper.addLabel("IVET Project: ", 1, 4);
        TextField ivetProject = paneHelper.addTextField(2,4,400);
        ivetProject.textProperty().bindBidirectional(ivetProjectProp);

        paneHelper.addLabel("IVET Name: ", 1, 5);
        TextField ivetName = paneHelper.addTextField(2,5,400);
        ivetName.textProperty().bindBidirectional(ivetNameProp);


        paneHelper.addLabel("School: ", 1, 6);
        TextField schoolName= paneHelper.addTextField(2,6,400);
        schoolName.textProperty().bindBidirectional(schoolNameProp);


        paneHelper.addLabel("School FullName: ", 1, 7);
        TextField schoolFullName = paneHelper.addTextField(2,7,400);
        schoolFullName.textProperty().bindBidirectional(schoolFullNameProp);

        paneHelper.addLabel("Instructor: ", 1, 8);
        TextField instructor = paneHelper.addTextField(2,8,400);
        instructor.textProperty().bindBidirectional(instructorProp);

        paneHelper.addLabel("Course Name: ", 1, 9);
        TextField courseName = paneHelper.addTextField(2,9,400);
        courseName.textProperty().bindBidirectional(courseNameProp);


        paneHelper.addLabel("Course Number: ", 1, 10);
        TextField courseNumber = paneHelper.addTextField(2,10,400);
        courseNumber.textProperty().bindBidirectional(courseNumberProp);

        paneHelper.addLabel("Course Term: ", 1, 11);
        TextField courseTerm = paneHelper.addTextField(2,11,400);
        courseTerm.textProperty().bindBidirectional(courseTermProp);

        boolean isClicked = paneHelper.createGrid("Vignette  Settings",null, "Save","Cancel");
        if(isClicked){
            settings.setCid(cid.getText());
            settings.setIvetTitle(ivetTitle.getText());
            settings.setIvet(ivetName.getText());
            settings.setIvetProject(ivetProject.getText());
            settings.setSchool(schoolName.getText());
            settings.setSchoolFullName(schoolFullName.getText());
            settings.setInstructor(instructor.getText());
            settings.setCourseName(courseName.getText());
            settings.setCourseNumber(courseNumber.getText());
            settings.setCourseTerm(courseTerm.getText());
        }
        String js =  settings.createSettingsJS();
        settings.setJsString(js);
        Main.getVignette().setSettings(settings);
    }
    @Override
    public void openStyleEditor(){
        CSSEditor cssEditor = new CSSEditor();
        GridPaneHelper customStylehelper = new GridPaneHelper();
        customStylehelper.addLabel("Vignette BackGround Color: ", 1, 2);
        customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS,2,2);
        customStylehelper.addLabel("Vignette Title Font",3,2);
        customStylehelper.addDropDown(CSSEditor.FONTS,4,2);
        customStylehelper.addLabel("Font Size: ", 5, 2);
        customStylehelper.addDropDown(CSSEditor.FONT_SIZES,6,2);
        customStylehelper.addLabel("Title Text Color: ", 1, 3);
        customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,2,3);
        customStylehelper.addLabel("Popup Button Color",3,3);
        customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,4,3);
        customStylehelper.addLabel("Popup Text Color: ", 5, 3);
        customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,6,3);
        customStylehelper.addLabel("Italic Text: ", 1, 4);
        customStylehelper.addCheckBox("",2,4,false);
        customStylehelper.addLabel("Bold Text: ", 3, 4);
        customStylehelper.addCheckBox("",4,4,false);
        customStylehelper.addLabel("Bold Text: ", 5, 4);
        customStylehelper.addCheckBox("",6,4,false);
        TextArea customTextarea=  customStylehelper.addTextArea(2,8,600,600);
        customStylehelper.addLabel("custom.css Style: ", 1, 8);
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(ConstantVariables.CUSTOM_CSS_SOURCE_PAGE)));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
                stringBuffer.append("\n");
            }
            customTextarea.setText(stringBuffer.toString());

        } catch (FileNotFoundException ex) {
            logger.error("{Custom CSS File}", ex);
        } catch (IOException ex) {
            logger.error("{Custom CSS File}", ex);
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException exp) {
                logger.error("{Custom CSS File Finally}", exp);
            }
        }
        boolean isSaved = customStylehelper.createGrid("Style Editor",null, "Save","Cancel");
        if(isSaved) {
            Main.getVignette().setCssEditorText(customTextarea.getText());
        }

    }

    @Override
    public void previewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette) {
        previewVignette.setDisable(true);
        stopPreviewMenu.setDisable(false);
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
        boolean isCancelled = false;
        for ( ButtonType bt : alert.getDialogPane().getButtonTypes() )
        {
            Button button = ( Button ) alert.getDialogPane().lookupButton( bt );
            button.setPrefWidth(200);

        }
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get().getText().equals("Save and Preview")) {
            if(Main.getVignette().isSaved()){
                Main.getVignette().saveAsVignette(false);
            }
            else {
                Main.getVignette().saveAsVignette(true);
            }
        }
        else if(result.get().getText().equals("Preview Without Saving")){

        }
        else {
            isCancelled = true;
        }
        if(!isCancelled) {
            try {
                Main.getVignette().previewVignette(null, -1);
            } catch (VignetteServerException e) {
                DialogHelper helper = new DialogHelper(Alert.AlertType.ERROR, "Error Message", null, e.toString()
                        , false);
                logger.error("{Vignette Server Exception}", e);

            }

            URL vigPreview = null;
            try {
                vigPreview = Main.getVignette().getPreviewURL();
            } catch (VignetteServerException e) {
                DialogHelper helper = new DialogHelper(Alert.AlertType.ERROR,
                        "Error Message", null,
                        e.toString()
                        , false);
                logger.error("{Vignette Preview Exception}", e);

            }

            boolean preview = true;
            if (preview) {
                // Open the vignette url in the browser
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(vigPreview.toURI());
                    } catch (IOException e) {
                        logger.error("{Vignette Preview Exception Desktop}", e);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        runtime.exec("xdg-open " + vigPreview.toString());
                    } catch (IOException e) {
                        logger.error("{Vignette Preview Exception Runtime}", e);
                    }
                }
            } else {

            }
        }

    }

    @Override
    public void stopPreviewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette) {
        previewVignette.setDisable(false);
        stopPreviewMenu.setDisable(true);
        try {
            Main.getVignette().stopPreviewVignette();
        } catch (VignetteServerException e) {
            e.printStackTrace();
            logger.error("{Vignette Preview Exception Stop Preview}", e);
        }
    }
}
