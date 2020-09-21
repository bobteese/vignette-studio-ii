/*******************************************************************************
 * Copyright 2020, Rochester Institute of Technology
 * */
package com.Vignette.Page;

public class VignettePage {

    String pageName;
    boolean isFirstPage;
    VignettePage connectedTo;

    public VignettePage(String pageName, boolean isFirstPage){
        this.pageName = pageName;
        this.isFirstPage = isFirstPage;
    }

    public String getPageName() {
        return pageName;
    }
    public void setPageName(String pageName){this.pageName = pageName;}
    public boolean isFirstPage() { return isFirstPage;}
    public void setFirstPage(boolean firstPage) { isFirstPage = firstPage; }
}
