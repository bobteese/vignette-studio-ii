package MenuBar.Vignette;

import Application.Main;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import Preview.VignetteServerException;
import SaveAsFiles.SaveAsVignette;
import TabPane.Features;
import Vignette.Framework.ReadFramework;
import Vignette.Settings.VignetteSettings;
import Vignette.StyleEditor.CSSEditor;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.NavigationActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VignetteMenuItem implements VignetteMenuItemInterface {

    private static final Logger logger = LoggerFactory.getLogger(VignetteMenuItem.class);
    private Features featureController;

    /**
     * Once called by clicking on the Rename Vignette option, this function changes
     * the name of the current vignette.
     * todo Change the function name to renameVignette to match behaviour.
     */
    @Override
    public void editVignette() {
        logger.info("{VignetteMenuItem} :: editVignette");
        GridPaneHelper helper = new GridPaneHelper();
        TextField text = helper.addTextField(0, 2, 400);
        if (Main.getVignette().getSettings().getIvet() != null
                && !"".equalsIgnoreCase(Main.getVignette().getSettings().getIvet()))
            text.setText(Main.getVignette().getSettings().getIvet());
        else
            text.setText(Main.getStage().getTitle());

        // text.setText(Main.getVignette().getVignetteName());
        boolean isCancled = helper.createGrid("Enter Vignette name to be saved", null, "Save", "Cancel");
        boolean isValid = false;

        if (isCancled) {
            isValid = false;
            String vignetteNametoSave = text.getText();
            String regexForFileName = "^[a-zA-Z0-9_-]*$";
            Pattern namePattern = Pattern.compile(regexForFileName);
            Matcher nameMatcher = namePattern.matcher(vignetteNametoSave);
            vignetteNametoSave = vignetteNametoSave.replace("//s", "");
            while (!isValid) {
                vignetteNametoSave = text.getText();
                String message = "";
                if (vignetteNametoSave.equals("")) {
                    message = "Vignette Name Cannot be empty";
                } else if (vignetteNametoSave.matches(regexForFileName)) {
                    isValid = true;
                    break;
                } else {
                    message = "Vignette name can be alphanumeric with underscores and hyphens";
                }
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION, "Message", null,
                        message, false);
                if (dialogHelper.getOk()) {
                    vignetteNametoSave = vignetteNametoSave.replaceAll("[^a-zA-Z0-9\\-_]", "-");
                    text.setText(vignetteNametoSave);
                    isCancled = helper.showDialog();
                }
                if (!isCancled) {
                    isValid = false;
                    break;
                }
                logger.info("{VignetteMenuItem} :: editVignette : Vignette Name to Save " + vignetteNametoSave);
            }

            if (isValid) {
                Path dir = Paths.get(Main.getVignette().getFolderPath());
                String oldPath = Main.getVignette().getFolderPath();
                SaveAsVignette saveAsVignette = new SaveAsVignette();
                saveAsVignette.createFolder(dir.getParent().toFile(), text.getText(),null);
                ReadFramework.deleteDirectory(oldPath);
                logger.info(
                        "{VignetteMenuItem} :: editVignette : Vignette Saved at " + Main.getVignette().getFolderPath());
                Main.getInstance().changeTitle(text.getText());
                Main.getVignette().setVignetteName(text.getText());
            }
        }
    }

    /**
     * This function deals with editing the vignette settings. Information about the
     * instructor and course is entered in
     * the textfields of the generated dialog box.
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

    private void setDefaultSettingToTextField(VignetteSettings settings) {
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
    public VignetteSettings editVignetteSettings() {
        logger.info("> {VignetteMenuItem}: editVignetteSettings");
        GridPaneHelper paneHelper = new GridPaneHelper();
        VignetteSettings settings = Main.getVignette().getSettings() != null ? Main.getVignette().getSettings()
                : new VignetteSettings();
        setDefaultSettingToTextField(settings);

        paneHelper.addLabel("cid: ", 1, 2);
        TextField cid = paneHelper.addTextField(settings.getCid(), 2, 2, 400);
        cid.textProperty().bindBidirectional(cidProp);

        paneHelper.addLabel("Vignette Title: ", 1, 3);
        TextField ivetTitle = paneHelper.addTextField(2, 3, 400);
        ivetTitle.textProperty().bindBidirectional(iverTitleProp);

        paneHelper.addLabel("Vignette Project: ", 1, 4);
        TextField ivetProject = paneHelper.addTextField(2, 4, 400);
        ivetProject.textProperty().bindBidirectional(ivetProjectProp);

        paneHelper.addLabel("Vignette Name: ", 1, 5);
        TextField ivetName = paneHelper.addTextField(2, 5, 400);
        ivetName.textProperty().bindBidirectional(ivetNameProp);
        ivetName.setPromptText("My_Vignette");

        paneHelper.addLabel("School: ", 1, 6);
        TextField schoolName = paneHelper.addTextField(2, 6, 400);
        schoolName.textProperty().bindBidirectional(schoolNameProp);

        paneHelper.addLabel("School FullName: ", 1, 7);
        TextField schoolFullName = paneHelper.addTextField(2, 7, 400);
        schoolFullName.textProperty().bindBidirectional(schoolFullNameProp);

        paneHelper.addLabel("Instructor: ", 1, 8);
        TextField instructor = paneHelper.addTextField(2, 8, 400);
        instructor.textProperty().bindBidirectional(instructorProp);

        paneHelper.addLabel("Course Name: ", 1, 9);
        TextField courseName = paneHelper.addTextField(2, 9, 400);
        courseName.textProperty().bindBidirectional(courseNameProp);

        paneHelper.addLabel("Course Number: ", 1, 10);
        TextField courseNumber = paneHelper.addTextField(2, 10, 400);
        courseNumber.textProperty().bindBidirectional(courseNumberProp);

        paneHelper.addLabel("Course Term: ", 1, 11);
        TextField courseTerm = paneHelper.addTextField(2, 11, 400);
        courseTerm.textProperty().bindBidirectional(courseTermProp);

        boolean isClicked = paneHelper.createGrid("Vignette Settings", null, "Save", "Cancel");

        if (isClicked) {
            final String regexForPageName = "^[a-zA-Z0-9_-]*$";
            String ivetNameStr = ivetName.getText() == null ? "My_Vignette" : ivetName.getText();
            boolean isValid = ivetNameStr.matches(regexForPageName);
            while (!isValid) {
                String message = !ivetNameStr.matches(regexForPageName)
                                ? "Vignette name can only be alphanumeric with underscores and hyphens" : "";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Alert");
                alert.setContentText(message);
                alert.showAndWait();
                ivetName.setText(ivetNameStr.replaceAll("[^a-zA-Z0-9\\-_]", "-"));
                isClicked = paneHelper.showDialog();
                ivetNameStr = ivetName.getText();
                isValid = !ivetNameStr.isEmpty() && ivetNameStr.matches(regexForPageName);
                if (!isClicked)
                    return null;
            }
            settings.setCid(cid.getText());
            settings.setIvetTitle(ivetTitle.getText());
            settings.setIvet(ivetNameStr);
            settings.setIvetProject(ivetProject.getText());
            settings.setSchool(schoolName.getText());
            settings.setSchoolFullName(schoolFullName.getText());
            settings.setInstructor(instructor.getText());
            settings.setCourseName(courseName.getText());
            settings.setCourseNumber(courseNumber.getText());
            settings.setCourseTerm(courseTerm.getText());
            String js = settings.createSettingsJS();
            settings.setJsString(js);
            Main.getVignette().setSettings(settings);
            return settings;
        } else {
            return null;
        }

    }

    public static String getKeyByValue(HashMap<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void openStyleEditor() {
        logger.info("{VignetteMenuItem} :: openStyleEditor");
        CSSEditor cssEditor = new CSSEditor();
        // HashMap<String, int[]> rgbColorMap = cssEditor.getrgbColorMap();
        featureController = new Features(Main.getVignette().getController());

        GridPaneHelper customStylehelper = new GridPaneHelper();

        CodeArea customTextarea = new CodeArea();
        customTextarea.setPrefHeight(1000);
        customTextarea.setPrefWidth(1000);

        if (Main.getVignette().getCssEditorText() != null
                || !"".equalsIgnoreCase(Main.getVignette().getCssEditorText())) {
            customTextarea.selectAll();
            customTextarea.replaceSelection(Main.getVignette().getCssEditorText());
            customTextarea.position(0, 0);
        }
        customTextarea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination search = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (search.match(ke)) {
                    featureController.findAndSelectString(customTextarea);
                }
            }
        });
        customTextarea.position(0, 0);
        customTextarea.start(NavigationActions.SelectionPolicy.CLEAR);

        VirtualizedScrollPane<CodeArea> vsPane = new VirtualizedScrollPane<>(customTextarea);
        vsPane.scrollYBy(0.1);
        GridPaneHelper.setColumnSpan(customStylehelper, 3);
        vsPane.getContent().setWrapText(true);
        customStylehelper.add(vsPane, 1, 1, 2, 1);
        vsPane.prefWidthProperty().bind(customStylehelper.getDialogPane().widthProperty());
        vsPane.prefHeightProperty().bind(customStylehelper.getDialogPane().heightProperty());

        boolean isSaved = customStylehelper.createGridWithoutScrollPane("Style Editor", null, "Save", "Cancel");
        if (isSaved) {
            Main.getVignette().setCssEditorText(customTextarea.getText());
        }
    }

    @Override
    public void previewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette) {

        try {

            if (Main.getVignette().getPreviewURL() != null) {
                Main.getVignette().stopPreviewVignette();
                logger.error("{VignetteMenuItem} :: previewVignette : Stopped preview preview because URL is NULL");
            }
            logger.info("{VignetteMenuItem} :: previewVignette : " + Main.getVignette().getPreviewURL());

        } catch (VignetteServerException vx) {
            logger.error("{VignetteMenuItem} :: previewVignette : Error in stopping previous preview:", vx);
        }
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
        for (ButtonType bt : alert.getDialogPane().getButtonTypes()) {
            Button button = (Button) alert.getDialogPane().lookupButton(bt);
            button.setPrefWidth(200);
        }
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get().getText().equals("Save and Preview")) {

            logger.info("{VignetteMenuItem} :: previewVignette : Save and Preview");
            if (Main.getVignette().isSaved()) {
                Main.getVignette().saveAsVignette(false);
            } else {
                Main.getVignette().saveAsVignette(true);
            }
        } else if (result.get().getText().equals("Preview Without Saving")) {
            logger.info("{VignetteMenuItem} :: previewVignette : Preview Without Saving");
        } else {

            logger.info("{VignetteMenuItem} :: previewVignette : Preview Cancelled ");
            isCancelled = true;
        }
        if (!isCancelled) {
            try {
                logger.info("{VignetteMenuItem} :: previewVignette : Preview Vignette ");
                Main.getVignette().previewVignette(null, -1);
            } catch (VignetteServerException e) {
                DialogHelper helper = new DialogHelper(Alert.AlertType.ERROR, "Error Message", null, e.toString(),
                        false);
                logger.error("{VignetteMenuItem}::Vignette Server Exception ", e);
            }

            URL vigPreview = null;
            try {
                vigPreview = Main.getVignette().getPreviewURL();
            } catch (VignetteServerException e) {
                DialogHelper helper = new DialogHelper(Alert.AlertType.ERROR,
                        "Error Message", null,
                        e.toString(), false);
                logger.error("{VignetteMenuItem}::Vignette Preview Exception ", e);

            }
            // Open the vignette url in the browser
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(vigPreview.toURI());
                } catch (Exception e) {
                    logger.error("{VignetteMenuItem}::Vignette Preview Exception Desktop ", e);
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + vigPreview.toString());
                } catch (IOException e) {
                    logger.error("{VignetteMenuItem}::Vignette Preview Exception Runtime ", e);
                }
            }

        }

    }

    @Override
    public void stopPreviewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette) {
        logger.error("{VignetteMenuItem}::stopPreviewVignette");
        previewVignette.setDisable(false);
        stopPreviewMenu.setDisable(true);
        try {
            Main.getVignette().stopPreviewVignette();
        } catch (VignetteServerException e) {
            logger.error("{VignetteMenuItem}::Vignette Preview Exception Stop Preview ", e);
        }
    }
}
