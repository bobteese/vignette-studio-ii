/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package Vignette.Page;

import javafx.scene.Group;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashMap;

public class VignettePage implements Serializable {
    private static final long SerialVersionUID = 30l;
    protected PropertyChangeSupport propertyChangeSupport;
    String pageName;
    boolean isFirstPage;
    HashMap<String, Group> nextPages;
    String pageData;
    String connectedTo;
    String pageType;
    double posX;
    double posY;

    public VignettePage(String pageName, boolean isFirstPage, String pageType){
        this.pageName = pageName;
        this.isFirstPage = isFirstPage;
        nextPages= new HashMap<>();
        pageData = null;
        this.pageType = pageType;

    }

    public String getPageName() {
        return pageName;
    }
    public void setPageName(String pageName){this.pageName = pageName;}
    public boolean isFirstPage() { return isFirstPage;}
    public void setFirstPage(boolean firstPage) { isFirstPage = firstPage; }
    public String getConnectedTo() { return connectedTo; }
    public void setConnectedTo(String connectedTo) { this.connectedTo = connectedTo; }
    public HashMap<String, Group> getNextPages() { return nextPages;  }
    public void setNextPages(String key, Group connector) { this.nextPages.put(key,connector);}
    public void removeNextPages(String key) { this.nextPages.remove(key);}
    public String getPageData() { return pageData; }
    public void setPageData(String pageData) { this.pageData = pageData; }
    public String getPageType() { return pageType; }
    public void setPageType(String pageType) { this.pageType = pageType; }
    public double getPosX() { return posX; }
    public void setPosX(double posX) { this.posX = posX; }
    public double getPosY() { return posY; }
    public void setPosY(double posY) { this.posY = posY; }
}
