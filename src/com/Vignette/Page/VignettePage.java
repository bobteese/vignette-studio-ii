/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.Vignette.Page;

import java.util.HashMap;

public class VignettePage {

    String pageName;
    boolean isFirstPage;
    HashMap<String,VignettePage> nextPages;
    String pageData;
    String connectedTo;

    public VignettePage(String pageName, boolean isFirstPage){
        this.pageName = pageName;
        this.isFirstPage = isFirstPage;
        nextPages= new HashMap<>();
        pageData = null;

    }

    public String getPageName() {
        return pageName;
    }
    public void setPageName(String pageName){this.pageName = pageName;}
    public boolean isFirstPage() { return isFirstPage;}
    public void setFirstPage(boolean firstPage) { isFirstPage = firstPage; }
    public String getConnectedTo() { return connectedTo; }
    public void setConnectedTo(String connectedTo) { this.connectedTo = connectedTo; }
    public HashMap<String, VignettePage> getNextPages() { return nextPages;  }
    public void setNextPages(HashMap<String, VignettePage> nextPages) {  this.nextPages = nextPages; }
    public String getPageData() { return pageData; }
    public void setPageData(String pageData) { this.pageData = pageData; }
}
