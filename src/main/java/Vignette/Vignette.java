package Vignette;

import Application.Main;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Vignette implements Serializable {

    private static final long SerialVersionUID = 10l;

    HashMap<String,VignettePage> pageViewList = new HashMap<>();
    VignetteSettings settings;
    transient List<Images> imagesList = new ArrayList<>();
    String vignetteName;
    transient String folderPath;

    transient boolean isSaved;
    public Vignette() {

    }

    public void saveAsVignette() {
        SaveAsVignette saveAs = new SaveAsVignette();
        if(!isSaved) {
            saveAs.fileChoose();
        }
        else{
            saveAs.createHTMLPages(folderPath);
        }
    }

    public HashMap<String, VignettePage> getPageViewList() { return pageViewList; }
    public void setPageViewList(HashMap<String, VignettePage> pageViewList) { this.pageViewList = pageViewList; }
    public VignetteSettings getSettings() { return settings;  }
    public void setSettings(VignetteSettings settings) { this.settings = settings;  }
    public List<Images> getImagesList() { return imagesList;  }
    public void setImagesList(List<Images> imagesList) { this.imagesList = imagesList; }
    public String getVignetteName() { return vignetteName; }
    public void setVignetteName(String vignetteName) {this.vignetteName = vignetteName; }
    public String getFolderPath() { return folderPath; }
    public void setFolderPath(String folderPath) { this.folderPath = folderPath; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }


}
