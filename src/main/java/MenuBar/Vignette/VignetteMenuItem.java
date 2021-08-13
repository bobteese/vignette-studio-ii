package MenuBar.Vignette;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import DialogHelpers.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import Preview.VignetteServerException;
import SaveAsFiles.SaveAsVignette;
import Vignette.Framework.FileResourcesUtils;
import Vignette.Framework.FilesFromResourcesFolder;
import Vignette.Framework.ReadFramework;
import Vignette.Settings.VignetteSettings;
import Vignette.StyleEditor.CSSEditor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VignetteMenuItem implements VignetteMenuItemInterface {

    private static final Logger logger = LoggerFactory.getLogger(VignetteMenuItem.class);


    /**
     * Once called by clicking on the Rename Vignette option, this function changes the name of the current vignette.
     * todo Change the function name to renameVignette to match behaviour.
     */
    @Override
    public void editVignette() {
        GridPaneHelper helper = new GridPaneHelper();
        TextField text = helper.addTextField(0,2,400);
        if(Main.getVignette().getSettings().getIvet()!=null && !"".equalsIgnoreCase(Main.getVignette().getSettings().getIvet()))
            text.setText(Main.getVignette().getSettings().getIvet());
        else
            text.setText(Main.getStage().getTitle());

//        text.setText(Main.getVignette().getVignetteName());
        boolean isCancled = helper.createGrid("Enter Vignette name to be saved",null,"Save","Cancel");
        boolean isValid = false;

        if(isCancled) {
            isValid = false;
            String vignetteNametoSave = text.getText();
            String regexForFileName= "^[a-zA-Z0-9_-]*$";
            Pattern namePattern = Pattern.compile(regexForFileName);
            Matcher nameMatcher = namePattern.matcher(vignetteNametoSave);
            vignetteNametoSave = vignetteNametoSave.replace("//s", "");
            while(!isValid){
                vignetteNametoSave = text.getText();
                String message = "";
                if(vignetteNametoSave.equals("")){
                    message =  "Vignette Name Cannot be empty";
                }else if(vignetteNametoSave.matches(regexForFileName)){
                    isValid = true;
                    break;
                }else{
                    message = "Vignette name can be alphanumeric with underscores and hyphens";
                }
                DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                        message,false);
                if(dialogHelper.getOk()) {
                    vignetteNametoSave = vignetteNametoSave.replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "-");
                    text.setText(vignetteNametoSave);
                    isCancled = helper.showDialog();
                }
                if(!isCancled) {isValid=false; break;}
            }

            if(isValid) {
                Path dir  = Paths.get(Main.getVignette().getFolderPath());
                System.out.println("PARENT FOLDER: "+dir.getParent());
                String oldPath = Main.getVignette().getFolderPath();
                SaveAsVignette saveAsVignette = new SaveAsVignette();
                saveAsVignette.createFolder(dir.getParent().toFile(), text.getText());
                ReadFramework.deleteDirectory(oldPath);
                System.out.println("Main.getVignette().getFolderPath();: "+Main.getVignette().getFolderPath());
                Main.getInstance().changeTitle(text.getText());
                Main.getVignette().setVignetteName(text.getText());
                //Main.getVignette().setSaved(true);
            }
        }


//        TextDialogHelper text;
//        if(Main.getVignette().isSaved())
//            text = new TextDialogHelper("Rename Vignette","Change the vignette title", Main.getVignette().getVignetteName());
//        else
//            text = new TextDialogHelper("Rename Vignette","Change the vignette title");
//        StringProperty vignetteNametoSave = new SimpleStringProperty(text.getTextAreaValue());
//
//        String regexForFileName= "^[a-zA-Z0-9_-]*$";
//        Pattern namePattern = Pattern.compile(regexForFileName);
//        Matcher nameMatcher = namePattern.matcher(vignetteNametoSave.get());
//        vignetteNametoSave.set(vignetteNametoSave.get().replace("//s", ""));
//        boolean isValid = false;
//        do{
//            String message = "";
//            if(vignetteNametoSave.equals("")){
//                message =  "Vignette Name Cannot be empty";
//                isValid = false;
//            }else if(vignetteNametoSave.get().matches(regexForFileName)){
//                isValid = true;
//                break;
//            }else{
//                message = "Vignette name can be alphanumeric with underscores and hyphens";
//                isValid = false;
//            }
//            DialogHelper dialogHelper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
//                    message,false);
//            if(dialogHelper.getOk()) {
//                vignetteNametoSave.set(vignetteNametoSave.get().replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "-"));
//                System.out.println("vignetteNametoSave: "+vignetteNametoSave.get());
//                Optional<String> name =  text.showAndWait();
//                name.ifPresent(vn -> { text.setTextAreaValue(vignetteNametoSave.get()); });
////                text = new TextDialogHelper("Rename Vignette","Change the vignette title", vignetteNametoSave.get());
//            }
//        }while(!isValid);
//
//        Main.getInstance().changeTitle(text.getTextAreaValue());
//        Main.getVignette().setVignetteName(text.getTextAreaValue());
//        if(Main.getVignette().isSaved()){
//            Path dir  = Paths.get(Main.getVignette().getFolderPath());
//            System.out.println("PARENT FOLDER: "+dir.getParent());
//            String oldPath = Main.getVignette().getFolderPath();
//            SaveAsVignette saveAsVignette = new SaveAsVignette();
//            saveAsVignette.createFolder(dir.getParent().toFile(), text.getTextAreaValue());
//            ReadFramework.deleteDirectory(oldPath);
//            System.out.println("Main.getVignette().getFolderPath();: "+Main.getVignette().getFolderPath());
//        }
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
        HashMap<String, int[]> rgbColorMap = cssEditor.getrgbColorMap();



        GridPaneHelper customStylehelper = new GridPaneHelper();
        StringProperty vignetteBackgroundColorProperty = new SimpleStringProperty("Default");

        //Dealing with .whiteBG class in custom.css
        customStylehelper.addLabel("Vignette BackGround Color: ", 1, 2);
        ComboBox VignetteBackgroundColors = customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS,2,2);
        VignetteBackgroundColors.valueProperty().bindBidirectional(vignetteBackgroundColorProperty);
        customStylehelper.addLabel("Vignette Font Family",1,3);
        Arrays.sort(CSSEditor.FONTS);
        ComboBox vignetteFontFamily = customStylehelper.addDropDown(CSSEditor.FONTS,2,3);
        customStylehelper.addLabel("Vignette Text Color: ", 1, 4);
        ComboBox vignetteTextColors =  customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,2,4);
        StringProperty vignetteTextColorProperty = new SimpleStringProperty("lightcoral");
        vignetteTextColors.valueProperty().bindBidirectional(vignetteTextColorProperty);
        customStylehelper.addLabel("Italic Text: ", 1, 5);
        CheckBox italicCheckboxForVignetteText = customStylehelper.addCheckBox("",2,5,false);
        //Dealing with .whiteBG class in custom.css

        customStylehelper.addLabel("Font Size: ", 5, 2);
        customStylehelper.addDropDown(CSSEditor.FONT_SIZES,6,2);
//        customStylehelper.addLabel("Title Text Color: ", 1, 3);
//        customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,2,3);
        customStylehelper.addLabel("Popup Button Color",3,3);
        customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,4,3);
        customStylehelper.addLabel("Popup Text Color: ", 5, 3);
        ComboBox textColors =  customStylehelper.addDropDown(CSSEditor.TEXT_COLORS,6,3);

        customStylehelper.addLabel("Bold Text: ", 3, 4);
        CheckBox boldTextForVIgnetteTitle = customStylehelper.addCheckBox("",4,4,false);

        customStylehelper.addLabel("Bold Text: ", 5, 4);
        customStylehelper.addCheckBox("",6,4,false);
        TextArea customTextarea = customStylehelper.addTextArea(2,8,600,600);
        try {
            if(Main.getVignette().getCssEditorText()!=null){
                customTextarea.setText(Main.getVignette().getCssEditorText());
            }else{
                FilesFromResourcesFolder filesFromResourcesFolder = new FilesFromResourcesFolder();
                FileResourcesUtils fileResourcesUtils = new FileResourcesUtils();
                String cssFilePath = "";
                if(Main.getVignette().isSaved()){
                    cssFilePath = Main.getVignette().getFolderPath();
                }else{
                    cssFilePath = ReadFramework.getUnzippedFrameWorkDirectory();
                }
                if(cssFilePath.endsWith("/")){
                    cssFilePath+="css/custom.css";
                }else{
                    cssFilePath+="/css/custom.css";
                }
                System.out.println("cssFilePath: "+cssFilePath);
                File cssFile = new File(cssFilePath);
                FileInputStream inputStream = new FileInputStream(cssFile);
                StringWriter getContent = new StringWriter();
                IOUtils.copy(inputStream, getContent, StandardCharsets.UTF_8);
                customTextarea.setText(getContent.toString());
            }
        } catch (FileNotFoundException ex) {
            logger.error("{Custom CSS File}", ex);
        } catch (IOException ex) {
            logger.error("{Custom CSS File}", ex);
        }


        //==================Vignette Stuff Dealing with .whiteBG class in custom.css=============================
        Matcher italicText = Pattern.compile("\\.whiteBG(.*?)\\{([\\S\\s]*?)\\}").matcher(customTextarea.getText());
        if(italicText.find()){
            if(Pattern.compile("(.*?)font-style: italic;(.*?)").matcher(italicText.group(0)).find()){
                italicCheckboxForVignetteText.setSelected(true);
            }else{
                italicCheckboxForVignetteText.setSelected(false);
            }
        }

        VignetteBackgroundColors.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "\\.whiteBG(.*?)\\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            if(m.find()){
                String bodyTag = m.group(0);
                String backgroundColor = "background-color:([\\S\\s]*?);";
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "background-color: "+CSSEditor.BACKGROUND_COLORS_HEX.get(newValue)+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }
        });

        vignetteFontFamily.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "\\.whiteBG(.*?)\\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            if(m.find()){
                String loginTitle = m.group(0);
                String fontFamilyRegex = "font-family:(.*?);";
                Pattern fontFamilyPattern = Pattern.compile(fontFamilyRegex);
                Matcher fontFamilyMatcher = fontFamilyPattern.matcher(loginTitle);
                if(fontFamilyMatcher.find()){
                    String addingFamilyFont = "font-family: ";
                    if("default".equalsIgnoreCase(newValue.toString())){
                        addingFamilyFont+=CSSEditor.FONTS[0];
                    }else{
                        addingFamilyFont+=newValue;
                    }
                    addingFamilyFont+=";";
                    loginTitle = loginTitle.replace(loginTitle.substring(fontFamilyMatcher.start(), fontFamilyMatcher.end()), addingFamilyFont);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(loginTitle);
                }

//                String bodyTag = m.group(0);
//                String backgroundColor = "background-color:([\\S\\s]*?);";
//                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
//                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
//                if(backgroundMatcher.find()){
//                    String colorToReplace = "background-color: "+CSSEditor.BACKGROUND_COLORS_HEX.get(newValue)+";";
//                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
//                    customTextarea.selectRange(m.start(), m.end());
//                    customTextarea.replaceSelection(bodyTag);
//                }else{
//                    System.out.println("backgroundMatcher didnt find anything!");
//                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }
        });
        vignetteTextColors.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "\\.whiteBG(.*?)\\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            if(m.find()){
                String bodyTag = m.group(0);
//                String backgroundColor = "^color:(\\s*)(.*?);";
                String backgroundColor = "color:([\\S\\s]*?);";
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "color: "+(newValue)+";";

                    //System.out.println(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()));

                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);

                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("Color Matcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }
        });
        italicCheckboxForVignetteText.selectedProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "\\.whiteBG(.*?)\\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            String bodyTag = "";
            if(m.find()) {
                bodyTag = m.group(0);
                String temp = "  font-style: italic;\n";
                if(newValue){
                    bodyTag = bodyTag.replace("}", temp+"}");
                }else{
                    bodyTag = bodyTag.replace(temp + "}", "}");
                }
                customTextarea.selectRange(m.start(), m.end());
                customTextarea.replaceSelection(bodyTag);
            }
        });
        //==================Vignette Stuff Dealing with .whiteBG class in custom.css=============================
        StringProperty textColorProperty = new SimpleStringProperty("Default");
        textColors.valueProperty().bindBidirectional(textColorProperty);
        textColors.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "body \\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            if(m.find()){
                String bodyTag = m.group(0);
                String backgroundColor = "color:(.*?);";
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    //String colorToReplace = "color: "+rgbColorMap.get(newValue)+";";
                    String colorToReplace = "color: "+CSSEditor.TEXT_COLORS_HEX.get(newValue)+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("Title text color didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }
        });

        Matcher findBold = Pattern.compile("\\.loginTitle(.*?)\\{([\\S\\s]*?)\\}").matcher(customTextarea.getText());
        if(findBold.find()){
            if(Pattern.compile("(.*?)font-weight: bold;(.*?)").matcher(findBold.group(0)).find()){
                System.out.println("SETTING TRUE");
                boldTextForVIgnetteTitle.setSelected(true);
            }else{
                boldTextForVIgnetteTitle.setSelected(false);
            }
        }
        boldTextForVIgnetteTitle.selectedProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "\\.loginTitle(.*?)\\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            String bodyTag = "";
            if(m.find()) {
                bodyTag = m.group(0);
                String temp = "  font-weight: bold;\n";
                if(newValue){
                    bodyTag = bodyTag.replace("}", temp+"}");
                }else{
                    bodyTag = bodyTag.replace(temp + "}", "}");
                }
                customTextarea.selectRange(m.start(), m.end());
                customTextarea.replaceSelection(bodyTag);
            }
        });


        /**
         *
         *
         * This was to edit the button css individually, delete if not usefull
         *
         *
        customStylehelper.addLabel("Button Color ", 5, 5);
        ComboBox buttonColor = customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS2,6,5);



        // ------------------------------ Button color: -----------------------------------------------------
        customStylehelper.addLabel("Button Color ", 5, 5);
        ComboBox buttonColor1 = customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS2,6,5);

        buttonColor1.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    String bodyPattern = "\\.btn-outline-primary(.*?)\\{([\\S\\s]*?)}";
                    String cssText = customTextarea.getText();
                    Pattern p = Pattern.compile(bodyPattern);
                    Matcher m = p.matcher(cssText);
                    if (m.find()) {
                        String bodyTag = m.group(0);
                        System.out.println(bodyTag);
                        String color = "color:([\\S\\s]*?);";
                        Pattern backgroundPattern = Pattern.compile(color);
                        Matcher backgroundMatcher = backgroundPattern.matcher(bodyTag);
                        if (backgroundMatcher.find()) {

                            String colorToReplace = "color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                            bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                            customTextarea.selectRange(m.start(), m.end());
                            customTextarea.replaceSelection(bodyTag);
                        } else {
                            System.out.println("backgroundMatcher didnt find anything!");
                        }
                    } else {
                        System.out.println("NO FOUND BODY TAG!!");
                    }
                });
            //deal with sub button stuff like hover, focus, disabled
        // ------------------------------ Button Border color: -----------------------------------------------------



        customStylehelper.addLabel("Button Border Color ", 5, 6);
        ComboBox buttonBorderColor = customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS2,6,6);

            buttonBorderColor.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                String bodyPattern = "\\.btn-outline-primary(.*?)\\{([\\S\\s]*?)}";
                String cssText = customTextarea.getText();
                String color = "border-color:([\\S\\s]*?);";
                Pattern p = Pattern.compile(bodyPattern);
                Matcher m = p.matcher(cssText);
                if(m.find()){
                    String bodyTag = m.group(0);
                    Pattern backgroundPattern =  Pattern.compile(color);
                    Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                    if(backgroundMatcher.find()){
                        String colorToReplace = "border-color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                        bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                        customTextarea.selectRange(m.start(), m.end());
                        customTextarea.replaceSelection(bodyTag);
                    }else{
                        System.out.println("backgroundMatcher didnt find anything!");
                    }
                }else{
                    System.out.println("NO FOUND BODY TAG!!");
                }
                bodyPattern = "\\.btn-outline-primary:hover(.*?)\\{([\\S\\s]*?)}";
                p= Pattern.compile(bodyPattern);
                m = p.matcher(customTextarea.getText());
                if(m.find()){
                    String bodyTag = m.group(0);
                    Pattern backgroundPattern =  Pattern.compile(color);
                    Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                    if(backgroundMatcher.find()){
                        String colorToReplace = "border-color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                        bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                        customTextarea.selectRange(m.start(), m.end());
                        customTextarea.replaceSelection(bodyTag);
                    }else{
                        System.out.println("backgroundMatcher didnt find anything!");
                    }
                }else{
                    System.out.println("NO FOUND BODY TAG!!");
                }

                bodyPattern = "\\.btn-outline-primary.dropdown-toggle(.*?)\\{([\\S\\s]*?)}";
                p= Pattern.compile(bodyPattern);
                m = p.matcher(customTextarea.getText());
                if(m.find()){
                    String bodyTag = m.group(0);
                    Pattern backgroundPattern =  Pattern.compile(color);
                    Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                    if(backgroundMatcher.find()){
                        String colorToReplace = "border-color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                        bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                        customTextarea.selectRange(m.start(), m.end());
                        customTextarea.replaceSelection(bodyTag);
                    }else{
                        System.out.println("backgroundMatcher didnt find anything!");
                    }
                }else{
                    System.out.println("NO FOUND BODY TAG!!");
                }
                //deal with sub button stuff like hover, focus, disabled


        });

        // ------------------------------ Button Background color: -----------------------------------------------------

        customStylehelper.addLabel("Button Background Color ", 5, 7);
        ComboBox buttonBackgroundColor = customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS2,6,7);
        buttonBackgroundColor.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String bodyPattern = "\\.btn-outline-primary:hover(.*?)\\{([\\S\\s]*?)\\}";
            String cssText = customTextarea.getText();
            Pattern p = Pattern.compile(bodyPattern);
            Matcher m = p.matcher(cssText);
            if(m.find()){
                String bodyTag = m.group(0);
                String backgroundColor = "background-color:([\\S\\s]*?);";
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "background-color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }

            bodyPattern ="\\.btn-outline-primary.focus(.*?)\\{([\\S\\s]*?)\\}";
            p = Pattern.compile(bodyPattern);
            m = p.matcher(customTextarea.getText());
            if(m.find()){
                String bodyTag = m.group(0);
                String backgroundColor = "box-shadow:([\\S\\s]*?);";
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "box-shadow: 0 0 0 0.2rem "+cssEditor.colorToRGBwithOpacity(rgbColorMap.get(newValue))+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }


            bodyPattern ="\\.btn-outline-primary.dropdown-toggle:focus(.*?)\\{([\\S\\s]*?)\\}";
            p = Pattern.compile(bodyPattern);
            m = p.matcher(customTextarea.getText());
            if(m.find()){
                String bodyTag = m.group(0);
                String backgroundColor = "box-shadow:([\\S\\s]*?);";
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "box-shadow: 0 0 0 0.2rem "+cssEditor.colorToRGBwithOpacity(rgbColorMap.get(newValue))+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }
        });
        */




        customStylehelper.addLabel("Change Button Color ", 5, 5);
        ComboBox buttonColor = customStylehelper.addDropDown(CSSEditor.BACKGROUND_COLORS2,6,5);


        /**
         * The comments /*ButtonStart* / /*Button* / must be there in order for this to work.
         */
        buttonColor.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String borderColor = "border-color:([\\S\\s]*?);";
            String color = "color:([\\S\\s]*?);";


            String bodyPattern = "(?<=\\/\\*ButtonStart\\*\\/)([\\s\\S]*?)(?=\\/\\*ButtonEnd\\*\\/)";

            Pattern p= Pattern.compile(bodyPattern);
            Matcher m = p.matcher(customTextarea.getText());
            if(m.find()){
                String bodyTag = m.group(0);
                Pattern backgroundPattern =  Pattern.compile(color);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "color: " + cssEditor.colorToRGB(rgbColorMap.get(newValue)) + ";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }


            p= Pattern.compile(bodyPattern);
            m = p.matcher(customTextarea.getText());
            if(m.find()){
                String bodyTag = m.group(0);
                Pattern backgroundPattern =  Pattern.compile(borderColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "border-color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }


            String backgroundColor = "background-color:([\\S\\s]*?);";

            p= Pattern.compile(bodyPattern);
            m = p.matcher(customTextarea.getText());
            if(m.find()){
                String bodyTag = m.group(0);
                Pattern backgroundPattern =  Pattern.compile(backgroundColor);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "background-color: "+cssEditor.colorToRGB(rgbColorMap.get(newValue))+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }

            String boxShadow = "box-shadow:([\\S\\s]*?);";


            p= Pattern.compile(bodyPattern);
            m = p.matcher(customTextarea.getText());
            if(m.find()){
                String bodyTag = m.group(0);
                Pattern backgroundPattern =  Pattern.compile(boxShadow);
                Matcher backgroundMatcher  = backgroundPattern.matcher(bodyTag);
                if(backgroundMatcher.find()){
                    String colorToReplace = "box-shadow: 0 0 0 0.2rem "+cssEditor.colorToRGBwithOpacity(rgbColorMap.get(newValue))+";";
                    bodyTag = bodyTag.replace(bodyTag.substring(backgroundMatcher.start(), backgroundMatcher.end()), colorToReplace);
                    customTextarea.selectRange(m.start(), m.end());
                    customTextarea.replaceSelection(bodyTag);
                }else{
                    System.out.println("backgroundMatcher didnt find anything!");
                }
            }else{
                System.out.println("NO FOUND BODY TAG!!");
            }

        });
        customStylehelper.addLabel("custom.css Style: ", 1, 8);
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
