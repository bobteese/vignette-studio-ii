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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vignette implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Vignette.class);
    private static final long serialVersionUID = 4382920312814277325L;
    //------Declaring transient variable---------
    transient List<Images> imagesList = new ArrayList<>();
    transient TabPaneController controller;
    transient String cssEditorText;
    transient VignetterServer server = new VignetteServerImpl();
    //------Declaring transient variable---------


    //keeps track of the current page
    public VignettePage currentPage;
    public String vignetteName;
    public String folderPath;
    public String mainFolderPath;
    public boolean isSaved = false;
    ArrayList<String> htmlFiles = new ArrayList<>();
    HashMap<String, String> imagesPathForHtmlFiles = new HashMap<>();
    //this hashmap stores the values of each pages last page status
    public HashMap<String, Boolean> lastPageValueMap = new HashMap<>();
    //    public HashMap<String, String> extrasLibraryContent = new HashMap<>();
    public HashMap<String, VignettePage> pageViewList = new HashMap<>();
    public VignetteSettings settings;
    public boolean hasFirstPage = false;
    public Framework frameworkInformation;

    /**
     * Getter for the lastPageValueMap
     *
     * @return
     */
    public HashMap<String, Boolean> getLastPageValueMap() {
        return lastPageValueMap;
    }

    /**
     * The setter for the lastPageValueMap
     *
     * @param oldlastPageValueMap
     */
    public void setLastPageValueMap(HashMap<String, Boolean> oldlastPageValueMap) {
        lastPageValueMap = oldlastPageValueMap;
    }


    public HashMap<String, HTMLEditorContent> getHtmlContentEditor() {
        return controller.getHTMLContentEditor();
    }

    public HTMLEditorContent getHTMLEditorContent(VignettePage page) {
        return getHtmlContentEditor().get(page.getPageName());
    }

    public void setImagesPathForHtmlFiles(HashMap<String, String> imagesPathForHtmlFiles) {
        this.imagesPathForHtmlFiles = imagesPathForHtmlFiles;
    }

    public Framework getFrameworkInformation() {
        return frameworkInformation;
    }

    public void setFrameworkInformation(Framework frameworkInformation) {
        this.frameworkInformation = frameworkInformation;
    }

    public HashMap<String, String> getImagesPathForHtmlFiles() {
        return imagesPathForHtmlFiles;
    }

    public void addToImagesPathForHtmlFiles(String pageName, String fileName) {
        this.imagesPathForHtmlFiles.put(pageName, fileName);
    }

    public boolean isHasFirstPage() {
        return hasFirstPage;
    }

    public void addToHtmlFilesList(String fileName) {
        this.htmlFiles.add(fileName);
    }

    public ArrayList<String> getHtmlFiles() {
        return htmlFiles;
    }

    public void setHtmlFiles(ArrayList<String> htmlFiles) {
        this.htmlFiles = htmlFiles;
    }

    public boolean beenOpened;

    public void setHasFirstPage(boolean hasFirstPage) {
        this.hasFirstPage = hasFirstPage;
    }

    public Vignette() {

    }

    public boolean doesHaveFirstPage() {
        if (this.pageViewList.size() == 0)
            return false;
        for (Map.Entry<String, VignettePage> entry : pageViewList.entrySet()) {
            if (entry.getValue().isFirstPage()) {
                this.setHasFirstPage(true);
                return true;
            }
        }
        return false;
    }

    public boolean saveAsVignette(boolean clickedSaveAs) {
        logger.info("{Vignette} :: saveAsVignette : Save As? " + clickedSaveAs);
        try {
            SaveAsVignette saveAs = new SaveAsVignette();
            if (!isSaved || clickedSaveAs) {
                return saveAs.fileChoose();
            } else {
                File dirCheck = new File(folderPath);
                if (!dirCheck.exists()) {
                    logger.info("{Vignette} :: saveAsVignette : " + dirCheck + " doesn't exists, so creating and saving");
                    Path path = Paths.get(folderPath);
                    Files.createDirectories(path);
                    Main.getVignette().setFolderPath(folderPath);
                    saveAs.copyResourceFolderFromJar(folderPath);
                    saveAs.createExtrasFolder(folderPath);
                    saveAs.saveFramework(folderPath);
                }
                saveAs.saveVignetteSettingToMainFile(folderPath);
                saveAs.createHTMLPages(folderPath);
                saveAs.createImageFolder(folderPath);
                saveAs.vignetteCourseJsFile(folderPath);
                saveAs.saveCSSFile(folderPath);
                saveAs.saveVignetteClass(folderPath, vignetteName);
                return true;
            }
        } catch (Exception e) {
            logger.error("{Vignette} :: saveAsVignette : Error while Saving Vignette ", e);
        }
        return true;
    }

    public void previewVignette(String host, int port) throws VignetteServerException {
        logger.info("{Vignette} :: previewVignette : Previewing Vignette on " + host + ":" + port);
        server.loadVignette(folderPath, host, port);
        server.start();
    }

    public void stopPreviewVignette() throws VignetteServerException {
        logger.info("{Vignette} :: stopPreviewVignette : Stopped Preview");
        server.stop();
    }

    public URL getPreviewURL() throws VignetteServerException {
        URL vignetteURL = server.getVignetteUrl();
        logger.info("{Vignette} :: getPreviewURL : " + vignetteURL);
        return vignetteURL;
    }

    public HashMap<String, VignettePage> getPageViewList() {
        return pageViewList;
    }

    public void setPageViewList(HashMap<String, VignettePage> pageViewList) {
        this.pageViewList = pageViewList;
    }

    public void updatePageViewList(String pageName, String value) {
        VignettePage page = this.pageViewList.get(pageName);
        page.setPageData(value);
        this.pageViewList.put(pageName, page);
    }

    public VignetteSettings getSettings() {
        return settings;
    }

    public void setSettings(VignetteSettings settings) {
        this.settings = settings;
    }

    public List<Images> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<Images> imagesList) {
        this.imagesList = imagesList;
    }

    public void addToImageList(Images images) {
        this.imagesList.add(images);
    }

    public String getVignetteName() {
        return vignetteName;
    }

    public void setVignetteName(String vignetteName) {
        this.vignetteName = vignetteName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void setMainFolderPath(String mainFolderPath) {
        this.mainFolderPath = mainFolderPath;
    }

    public String getMainFolderPath() {
        return this.mainFolderPath;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getCssEditorText() {
        return cssEditorText;
    }

    public void setCssEditorText(String cssEditorText) {
        this.cssEditorText = cssEditorText;
    }

    public TabPaneController getController() {
        return controller;
    }

    public void setController(TabPaneController controller) {
        this.controller = controller;
    }


    public void setCurrentPage(VignettePage page) {
        this.currentPage = page;
    }

    public VignettePage getCurrentPage() {
        return this.currentPage;
    }

    public void setPageBeenOpened(boolean opened) {
        beenOpened = opened;
    }

    public boolean hasPageBeenOpened() {
        return this.beenOpened;
    }

    @Override
    public String toString() {
        return "Vignette{" +
                "pageViewList=" + pageViewList +
                ", settings=" + settings +
                ", hasFirstPage=" + hasFirstPage +
                ", currentPage=" + currentPage +
                ", vignetteName='" + vignetteName + '\'' +
                ", imagesList=" + imagesList +
                ", folderPath='" + folderPath + '\'' +
                ", mainFolderPath='" + mainFolderPath + '\'' +
                ", controller=" + controller +
                ", cssEditorText='" + cssEditorText + '\'' +
                ", isSaved=" + isSaved +
                ", htmlFiles=" + htmlFiles +
                ", imagesPathForHtmlFiles=" + imagesPathForHtmlFiles +
                ", lastPageValueMap=" + lastPageValueMap +
                ", server=" + server +
                ", frameworkInformation=" + frameworkInformation +
                ", beenOpened=" + beenOpened +
                '}';
    }
}

