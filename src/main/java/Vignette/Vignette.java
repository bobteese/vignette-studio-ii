package Vignette;

import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;

import java.io.Serializable;
import java.util.HashMap;

public class Vignette implements Serializable {

    private static final long SerialVersionUID = 10l;

    HashMap<String,VignettePage> pageViewList = new HashMap<>();
    VignetteSettings settings;
    transient Images image;

    public Vignette() {

    }

    public void saveAsVignette() {
        SaveAsVignette saveAs = new SaveAsVignette();
        saveAs.fileChoose();
    }

    public HashMap<String, VignettePage> getPageViewList() { return pageViewList; }
    public void setPageViewList(HashMap<String, VignettePage> pageViewList) { this.pageViewList = pageViewList; }
    public VignetteSettings getSettings() { return settings;  }
    public void setSettings(VignetteSettings settings) { this.settings = settings;  }
    public Images getImage() { return image; }
    public void setImage(Images image) { this.image = image; }



}
