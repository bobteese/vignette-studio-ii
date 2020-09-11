package com.Vignette.Settings;

public class VignetteSettings {

    int numberOfRecentFile  ;
    String companyName;
    int threshold;
    boolean showCurrentPage;
    boolean showCurrentPageOutOfTotal;

    public VignetteSettings() {

    }

    public String getCompanyName() { return companyName; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public int getThreshold() { return threshold; }

    public void setThreshold(int threshold) { this.threshold = threshold; }

    public boolean isShowCurrentPage() { return showCurrentPage; }

    public void setShowCurrentPage(boolean showCurrentPage) { this.showCurrentPage = showCurrentPage; }

    public boolean isShowCurrentPageOutOfTotal() { return showCurrentPageOutOfTotal; }

    public void setShowCurrentPageOutOfTotal(boolean showCurrentPageOutOfTotal) { this.showCurrentPageOutOfTotal = showCurrentPageOutOfTotal; }
    public int getNumberOfRecentFile() { return numberOfRecentFile;}
    public void setNumberOfRecentFile(int numberOfRecentFile) {this.numberOfRecentFile = numberOfRecentFile;}

}
