package com.MenuBar.File;

import com.Application.Main;
import com.DialogHelper.FileChooserHelper;
import com.DialogHelper.TextDialogHelper;
import com.GridPaneHelper.GridPaneHelper;
import com.Vignette.Settings.VignetteSettings;


public class FileMenuItem {

    public void createNewVignette() {
        TextDialogHelper text = new TextDialogHelper("New Vignette","Enter new vignette name");
        Main.getInstance().changeTitle(text.getTextAreaValue());
    }
    public void openVignette() {
        FileChooserHelper helper = new FileChooserHelper("Open");
        helper.openFileChooser();
    }
    public void setPreferences() {

        GridPaneHelper<VignetteSettings> paneHelper = new GridPaneHelper();
        paneHelper.addLabel("Recent Files: ", 1, 1);
        paneHelper.addNumberSpinner(1,1,Integer.MAX_VALUE,2,1);
        paneHelper.addLabel("",1,2);
        paneHelper.addButton("Clear Recent Files",2,2);
        paneHelper.createGrid("Preferences",null, "Save","Cancel");


    }
}
