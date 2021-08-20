package Vignette.Settings;

import Application.Main;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VignetteSettings implements Serializable {

    private static final long SerialVersionUID = 20l;
    String jsString;

    public String getJsString() {
        return jsString;
    }

    public void setJsString(String jsString) {
        this.jsString = jsString;
    }

    int numberOfRecentFile;
    String companyName;
    int threshold;
    boolean showCurrentPage;
    boolean showCurrentPageOutOfTotal;
    String cid = "5000";
    String ivetTitle ="Introduction";
    String ivetProject = "Interactive Video Vignettes";
    String ivet = Main.getVignette().getVignetteName();
    String school = "RIT";
    String schoolFullName = "Rochester Institute of Technology";
    String instructor = "Dr. Teese";;
    String courseName = "University Physics";;
    String courseNumber  = "211";
    String courseTerm = "Fall 2020";

    String settingsInJavaScript;

    public VignetteSettings() {
        cid = "5000";
        ivetTitle ="Introduction";
        ivetProject = "Interactive Video Vignettes";
        ivet = Main.getVignette().getVignetteName();
        school = "RIT";
        schoolFullName = "Rochester Institute of Technology";
        instructor = "Dr. Teese";;
        courseName = "University Physics";;
        courseNumber  = "211";
        courseTerm = "Fall 2020";
        jsString = "";
    }
    public String createSettingsJS(){

        String js= "        var cid = \""+cid+"\"; // cid is the Course ID number\n" +
                "        var IVETTitle = \""+ivetTitle+"\"; // Full title of the tutorial\n" +
                "        var IVETProject = \""+ivetProject+"\";\n" +
                "        var ivet = \""+ivet+"\"; // This is a short symbolic title for the tutorial\n" +
                "        var school = \""+school+"\";\n" +
                "        var schoolFull = \""+schoolFullName+"\";\n" +
                "        var instructor = \""+instructor+"\";\n" +
                "        var courseName = \""+courseName+"\";\n" +
                "        var courseNumber = \""+courseNumber+"\";\n" +
                "        var courseTerm = \""+courseTerm+"\";\n" +
                "        var playerChoice; // 1 = Youtube & 0 = Vimeo\n" +
                "        var firstPageFilename = \"pages/login.html\"; // Page to load as first page\n";


        this.settingsInJavaScript = js;
        return js;
    }


    /**
     * All the Setters and getters for the Vignette Settings
     * @return
     */
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }
    public String getIvetTitle() { return ivetTitle; }
    public void setIvetTitle(String ivetTitle) { this.ivetTitle = ivetTitle; }
    public String getIvetProject() { return ivetProject; }
    public void setIvetProject(String ivetProject) { this.ivetProject = ivetProject; }
    public String getIvet() { return ivet; }
    public void setIvet(String ivet) { this.ivet = ivet; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getSchoolFullName() { return schoolFullName; }
    public void setSchoolFullName(String schoolFullName) { this.schoolFullName = schoolFullName; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseNumber() { return courseNumber; }
    public void setCourseNumber(String courseNumber) { this.courseNumber = courseNumber; }
    public String getCourseTerm() { return courseTerm; }
    public void setCourseTerm(String courseTerm) { this.courseTerm = courseTerm; }
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
