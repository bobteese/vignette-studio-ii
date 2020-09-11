package com.MenuBar.Vignette;

import com.Application.Main;
import com.DialogHelper.TextDialogHelper;
import com.GridPaneHelper.GridPaneHelper;
import com.Vignette.Settings.VignetteSettings;
import com.Vignette.StyleEditor.CSSEditor;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class VignetteMenuItem {

    public void editVignette() {
        TextDialogHelper text = new TextDialogHelper("Edit Vignette","Change the vignette title");
        Main.getInstance().changeTitle(text.getTextAreaValue());
    }
    public void editVignetteSettings(){

        GridPaneHelper<VignetteSettings> paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Company name: ", 1, 1);
        paneHelper.addTextField(2,1);
        paneHelper.addLabel("Threshold",1,2);
        paneHelper.addNumberSpinner(7,0,Integer.MAX_VALUE,2,2);
        paneHelper.addLabel("Show current page: ", 1, 3);
        paneHelper.addCheckBox("",2,3);
        paneHelper.addLabel("Show current page out of total: ", 1, 4);
        paneHelper.addCheckBox("",2,4);
        paneHelper.createGrid("Vignette  Settings",null, "Save","Cancel");
    }
    public void openStyleEditor(){
        CSSEditor cssEditor = new CSSEditor();
        GridPaneHelper<VignetteSettings> paneHelper = new GridPaneHelper();
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
        paneHelper.addCheckBox("",2,7);
        paneHelper.addLabel("Bold Text: ", 1, 8);
        paneHelper.addCheckBox("",2,8);
        paneHelper.addLabel("Bold Text: ", 1, 8);
        paneHelper.addCheckBox("",2,8);
        Button showCSSEditor =  paneHelper.addButton("Show Custom CSS Editor",1,9);
        EventHandler<ActionEvent> event = openCustomCSSStyleEditor( paneHelper);
        showCSSEditor.setOnAction(event);
        paneHelper.createGrid("Style Editor",null, "Save CSS Styles","Cancel");

    }
    public EventHandler<ActionEvent> openCustomCSSStyleEditor( GridPaneHelper gridPaneHelper) {

        EventHandler<ActionEvent> event = e -> {
            gridPaneHelper.closeDialog();
            GridPaneHelper<VignetteSettings> paneHelper = new GridPaneHelper();
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
