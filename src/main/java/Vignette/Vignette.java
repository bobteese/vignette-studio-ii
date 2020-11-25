package Vignette;

import Preview.VignetteServerException;
import Preview.VignetteServerImpl;
import Preview.VignetterServer;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import TabPane.TabPaneController;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;

import java.io.Serializable;
import java.net.URL;
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
    transient TabPaneController controller;
    transient String cssEditorText;
    transient boolean isSaved;
    transient VignetterServer server = new VignetteServerImpl();
    public Vignette() {

    }

    public void saveAsVignette(boolean clickedSaveAs) {
        SaveAsVignette saveAs = new SaveAsVignette();
        if(!isSaved || clickedSaveAs) {
            saveAs.fileChoose();
        }
        else{
            saveAs.createHTMLPages(folderPath);
        }
    }
    public void previewVignette(String host,int port) throws VignetteServerException {

        server.loadVignette(folderPath,host,port);
        server.start();

    }
    public void stopPreviewVignette() throws VignetteServerException {
        server.stop();

    }
    public URL getPreviewURL() throws VignetteServerException {
        return server.getVignetteUrl();
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
    public String getCssEditorText() {
        return cssEditorText;
    }

    public void setCssEditorText(String cssEditorText) {
        this.cssEditorText = cssEditorText;
    }
    public TabPaneController getController() { return controller; }
    public void setController(TabPaneController controller) { this.controller = controller; }




}
