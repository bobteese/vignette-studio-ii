package MenuBar.Vignette;

import Application.Main;
import DialogHelper.TextDialogHelper;
import GridPaneHelper.GridPaneHelper;
import Vignette.Settings.VignetteSettings;
import Vignette.StyleEditor.CSSEditor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class VignetteMenuItem {

    public void editVignette() {
        TextDialogHelper text = new TextDialogHelper("Edit Vignette","Change the vignette title");
        Main.getInstance().changeTitle(text.getTextAreaValue());
    }
    public void editVignetteSettings(){

        GridPaneHelper paneHelper = new GridPaneHelper();
        VignetteSettings settings = new VignetteSettings();
        paneHelper.addLabel("Company name: ", 1, 1);
        TextField companyName = paneHelper.addTextField(2,1,400);
        paneHelper.addLabel("cid: ", 1, 2);
        TextField cid = paneHelper.addTextField(2,2,400);
        paneHelper.addLabel("IVET Title: ", 1, 3);
        TextField ivetTitle = paneHelper.addTextField(2,3,400);
        paneHelper.addLabel("IVET Project: ", 1, 4);
        TextField ivetProject = paneHelper.addTextField(2,4,400);
        paneHelper.addLabel("IVET Name: ", 1, 5);
        TextField ivetName = paneHelper.addTextField(2,5,400);
        paneHelper.addLabel("School: ", 1, 6);
        TextField schoolName= paneHelper.addTextField(2,6,400);
        paneHelper.addLabel("School FullName: ", 1, 7);
        TextField schoolFullName = paneHelper.addTextField(2,7,400);
        paneHelper.addLabel("Instructor: ", 1, 8);
        TextField instructor = paneHelper.addTextField(2,8,400);
        paneHelper.addLabel("Course Name: ", 1, 9);
        TextField courseName = paneHelper.addTextField(2,9,400);
        paneHelper.addLabel("Course Number: ", 1, 10);
        TextField courseNumber = paneHelper.addTextField(2,10,400);
        paneHelper.addLabel("Course Term: ", 1, 11);
        TextField courseTerm = paneHelper.addTextField(2,11,400);
        boolean isClicked = paneHelper.createGrid("Vignette  Settings",null, "Save","Cancel");
        if(isClicked){
            settings.setCompanyName(companyName.getText());
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

           String js =  settings.createSettingsJS();

        }
    }
    public void openStyleEditor(){
        CSSEditor cssEditor = new CSSEditor();
        GridPaneHelper paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Vignette BackGround Color: ", 1, 1);
        paneHelper.addDropDown(CSSEditor.BACKGROUND_COLORS,2,1);
        paneHelper.addLabel("Vignette Title Font",1,2);
        paneHelper.addDropDown(CSSEditor.FONTS,2,2);
        paneHelper.addLabel("Font Size: ", 1, 3);
        paneHelper.addDropDown(CSSEditor.FONT_SIZES,2,3);
        paneHelper.addLabel("Title Text Color: ", 1, 4);
        paneHelper.addDropDown(CSSEditor.TEXT_COLORS,2,4);
        paneHelper.addLabel("Popup Button Color",1,5);
        paneHelper.addDropDown(CSSEditor.TEXT_COLORS,2,5);
        paneHelper.addLabel("Popup Text Color: ", 1, 6);
        paneHelper.addDropDown(CSSEditor.TEXT_COLORS,2,6);
        paneHelper.addLabel("Italic Text: ", 1, 7);
        paneHelper.addCheckBox("",2,7,false);
        paneHelper.addLabel("Bold Text: ", 1, 8);
        paneHelper.addCheckBox("",2,8,false);
        paneHelper.addLabel("Bold Text: ", 1, 8);
        paneHelper.addCheckBox("",2,8,false);
        Button showCSSEditor =  paneHelper.addButton("Show Custom CSS Editor",1,9);
        EventHandler<ActionEvent> event = openCustomCSSStyleEditor( paneHelper);
        showCSSEditor.setOnAction(event);
        paneHelper.createGrid("Style Editor",null, "Save CSS Styles","Cancel");

    }
    public EventHandler<ActionEvent> openCustomCSSStyleEditor( GridPaneHelper gridPaneHelper) {

        EventHandler<ActionEvent> event = e -> {
            gridPaneHelper.closeDialog();
            GridPaneHelper paneHelper = new GridPaneHelper();
            paneHelper.addLabel("Style.css Style: ", 1, 1);
            paneHelper.addTextArea(2,1,600,600);
            Button showPrest = paneHelper.addButton("Show preset CSS editor",2,2);
            EventHandler<ActionEvent> openPresetEvent = openPresetEventHandler( paneHelper);
            showPrest.setOnAction(openPresetEvent);
            paneHelper.createGrid("Vignette  Settings",null, "Save","Cancel");

        };
        return event;
    }
    public EventHandler<ActionEvent> openPresetEventHandler(GridPaneHelper gridPaneHelper){
        EventHandler<ActionEvent> event = e -> {
            gridPaneHelper.closeDialog();
            openStyleEditor();

        };
        return event;

    }
    public List<String> convertStringArrayToList(String[] array){

        return Arrays.asList(array);

    }
}
