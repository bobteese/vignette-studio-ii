package Vignette;

import Application.Main;
import Preview.VignetteServerException;
import Preview.VignetteServerImpl;
import Preview.VignetterServer;
import SaveAsFiles.Images;
import SaveAsFiles.SaveAsVignette;
import TabPane.TabPaneController;
import Vignette.Framework.Framework;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.VignettePage;
import Vignette.Settings.VignetteSettings;
import javafx.scene.control.TextArea;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vignette implements Serializable {

    private static final long SerialVersionUID = 10l;
    HashMap<String,VignettePage> pageViewList = new HashMap<>();
    VignetteSettings settings;

    boolean hasFirstPage = false;

    VignettePage currentPage;

    String lastPage = "";

    ArrayList<String> lastPages = new ArrayList<>();




    String vignetteName;
    transient List<Images> imagesList = new ArrayList<>();
    transient String folderPath;
    transient TabPaneController controller;
    transient String cssEditorText;
    transient boolean isSaved;
    transient ArrayList<String> htmlFiles = new ArrayList<>();
    transient HashMap<String, String> imagesPathForHtmlFiles = new HashMap<>();



    public HashMap<String,HTMLEditorContent> getHtmlContentEditor()
    {
        return controller.getHTMLContentEditor();
    }

    public HTMLEditorContent getHTMLEditorContent(VignettePage page)
    {
        return getHtmlContentEditor().get(page.getPageName());
    }



    public void addLastPage(String page){
        lastPages.add(page);
    }
    public void deleteLastPage(String page)
    {
        lastPages.remove(page);
    }
    public ArrayList<String> getLastPages()
    {
        return lastPages;
    }




    public void setImagesPathForHtmlFiles(HashMap<String, String> imagesPathForHtmlFiles) {
        this.imagesPathForHtmlFiles = imagesPathForHtmlFiles;
    }

    transient VignetterServer server = new VignetteServerImpl();

    public Framework getFrameworkInformation() {
        return frameworkInformation;
    }

    public void setFrameworkInformation(Framework frameworkInformation) {
        this.frameworkInformation = frameworkInformation;
    }

    Framework frameworkInformation;
    public HashMap<String,String> getImagesPathForHtmlFiles() {
        return imagesPathForHtmlFiles;
    }

    public void addToImagesPathForHtmlFiles(String pageName,String fileName){
        this.imagesPathForHtmlFiles.put(pageName, fileName);
    }
    public boolean isHasFirstPage() {
        return hasFirstPage;
    }
    public void addToHtmlFilesList(String fileName){
        this.htmlFiles.add(fileName);
    }
    public ArrayList<String> getHtmlFiles() {
        return htmlFiles;
    }

    public void setHtmlFiles(ArrayList<String> htmlFiles) {
        this.htmlFiles = htmlFiles;
    }



    boolean beenOpened;


    public void setHasFirstPage(boolean hasFirstPage) {
        this.hasFirstPage = hasFirstPage;
    }

    public Vignette() {

    }
    public boolean doesHaveFirstPage(){
        if(this.pageViewList.size()==0)
            return false;
        for (Map.Entry<String, VignettePage> entry : pageViewList.entrySet()) {
            if (entry.getValue().isFirstPage()){
                this.setHasFirstPage(true);
                return true;
            }
        }
        return false;
    }

    public void saveAsVignette(boolean clickedSaveAs) {
        SaveAsVignette saveAs = new SaveAsVignette();
        if(!isSaved || clickedSaveAs) {
            saveAs.fileChoose();
        }
        else{
            saveAs.saveVignetteSettingToMainFile(folderPath);
            saveAs.createHTMLPages(folderPath);
            saveAs.createImageFolder(folderPath);
            saveAs.vignetteCourseJsFile(folderPath);
//            saveAs.saveFramework(folderPath);
//            if(!Main.getVignette().isSaved)
//                saveAs.saveFramework(folderPath);
            saveAs.saveVignetteClass(folderPath, vignetteName);
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
    public void updatePageViewList(String pageName, String value) {
        VignettePage page = this.pageViewList.get(pageName);
        page.setPageData(value);
        this.pageViewList.put(pageName,page);
    }
    public VignetteSettings getSettings() { return settings;  }
    public void setSettings(VignetteSettings settings) { this.settings = settings;  }
    public List<Images> getImagesList() { return imagesList;  }
    public void setImagesList(List<Images> imagesList) { this.imagesList = imagesList; }
    public void addToImageList(Images images){this.imagesList.add(images);}
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






    public void setCurrentPage(VignettePage page)
    {

        System.out.println("SETTING");
        this.currentPage = page;
    }
    public VignettePage getCurrentPage()
    {
        return this.currentPage;
    }

    public void setPageBeenOpened(boolean opened){  beenOpened = opened; }
    public boolean hasPageBeenOpened(){ return this.beenOpened; }



}
