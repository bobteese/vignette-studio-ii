package Vignette.Settings;

import java.io.Serializable;

public class VignetteSettings implements Serializable {

    private static final long SerialVersionUID = 20l;

    int numberOfRecentFile  ;
    String companyName;
    int threshold;
    boolean showCurrentPage;
    boolean showCurrentPageOutOfTotal;
    String cid;
    String ivetTitle;
    String ivetProject;
    String ivet;
    String school;
    String schoolFullName;
    String instructor;
    String courseName;
    String courseNumber;
    String courseTerm;

    String settingsInJavaScript;

    public VignetteSettings() {

    }
    public String createSettingsJS(){

        String js= "var cid = \""+cid+";\n"+
                        "var IVETTitle = \""+ivetTitle+"\";\n"+
                        "var IVETProject = \""+ivetProject+"\";\n"+
                        "var ivet =\""+ivet+"\";\n"+
                        "var school =\""+school+"\";"+
                        "var schoolFull = \""+schoolFullName+"\";\n"+
                        "var instructor = \""+instructor+"\";\n"+
                        "var courseName = \""+courseName+"\";\n"+
                        "var courseNumber = \""+courseNumber+"\";\n"+
                        "var courseTerm = \""+courseTerm+"\";\n";
        this.settingsInJavaScript = js;
        return js;
    }

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
