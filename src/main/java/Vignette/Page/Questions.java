package Vignette.Page;

import Application.Main;
import Application.NewMain;
import ConstantVariables.ConstantVariables;
import Vignette.Framework.FileResourcesUtils;
import Vignette.Framework.FilesFromResourcesFolder;
import Vignette.Framework.ReadFramework;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Questions implements Serializable {
    public Questions(){}
    static HashMap<String, String> questionStyleFileList = new HashMap<>();
    String questionType; // radio, checkbox or text
    String questionText;
    String options[];
    String optionValue[];
    String imageSource; // image within the question
    String questionName;
    Boolean branchingQuestion;
    Boolean isImageField;

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }
    public static HashMap<String, String> getQuestionStyleFileList() {
        return questionStyleFileList;
    }

    public Questions(Questions q){
        this.questionType = q.questionType;
        this.questionText = q.questionText;
        this.imageSource = q.imageSource;
        this.options = q.options;
        this.optionValue = q.optionValue;
        this.questionName = q.questionName;
        this.branchingQuestion = q.branchingQuestion;
        this.required = q.required;
        this.isImageField = q.isImageField;
    }

    public Questions(String questionType, String questionText, String imageSource, String[] options, String[] optionValue, String questionName, Boolean branchingQuestion, Boolean required, Boolean isImageField) {
        this.questionType = questionType;
        this.questionText = questionText;
        this.imageSource  = imageSource;
        this.options = options;
        this.optionValue = optionValue;
        this.questionName = questionName;
        this.branchingQuestion = branchingQuestion;
        this.required = required;
        this.isImageField = isImageField;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getGetStyleRegex() {
        return getStyleRegex;
    }

    public String getGetClassesRegex() {
        return getClassesRegex;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String[] getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String[] optionValue) {
        this.optionValue = optionValue;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public Boolean getBranchingQuestion() {
        return branchingQuestion;
    }

    public void setBranchingQuestion(Boolean branchingQuestion) {
        this.branchingQuestion = branchingQuestion;
    }

    Boolean required;
    static final String getStyleRegex = "var style = ([\\S\\s]*?);";
    static final String getClassesRegex = "var classes = ([\\S\\s]*?);";

    public static String createQuestions(Questions[] questionObject){
        String appendString = "";
        Pattern stylePattern = Pattern.compile(getStyleRegex);
        Pattern classesPattern = Pattern.compile(getClassesRegex);

        int index = 1;
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for(Questions q: questionObject){
            String key = "";
            if(q.questionType.equalsIgnoreCase("radio")){
                key = "radio";
            }else if(q.questionType.equalsIgnoreCase("checkbox")){
                key = "checkbox";
            }else{
                key = "textbox";
            }
            Matcher styleMatcher  = stylePattern.matcher(getStyleFromFile(getQuestionStyleFileList().get(key)));
            Matcher questionStyleMatcher = stylePattern.matcher(getStyleFromFile(getQuestionStyleFileList().get("question_text")));
            Matcher classesMatcher = classesPattern.matcher(getStyleFromFile(getQuestionStyleFileList().get(key)));
            Matcher questionClassesMatcher = classesPattern.matcher(getStyleFromFile(getQuestionStyleFileList().get("question_text")));
            String questionTextStyle  = getStyleFromFileString(questionStyleMatcher);
            String questionTypeStyle = getStyleFromFileString(styleMatcher);
            String classesForQuestion = getClassesFromFileString(classesMatcher);
            String classesForInput = getClassesFromFileString(questionClassesMatcher);
            questionTypeStyle = questionTypeStyle.replaceAll("\'", "");
            questionTextStyle = questionTextStyle.replaceAll("\'", "");
            questionTypeStyle = questionTypeStyle.replaceAll("\'", "");
            questionTextStyle = questionTextStyle.replaceAll("\'", "");
//            appendString = appendString + "<form id='ques" + index + "'>\n";
            if(q.branchingQuestion){
                appendString = appendString + "<!-- BranchQ--> \n <form id='ques" + index + "' class='branch required'>\n";
            }else{
                if(q.required){
                    appendString = appendString + "<form id='ques" + index + "' class='required'>\n";
                }else{
                    appendString = appendString + "<form id='ques" + index + "'>\n";
                }
            }
            String imageString="";
            if(q.imageSource != null && !"".equalsIgnoreCase(q.imageSource)){
                imageString = "<img src=" + q.imageSource + " alt='Question Description' class='text-center' width='300px' height='auto'/>\n";
            }
            if(q.questionType == "radio" || q.questionType == "checkbox") {
                int index2 = 0;
//                padding: 0px 15px 0px; text-align:left; width:95%;
                appendString = appendString + "<div class= '"+classesForQuestion+"'><p class='normTxt' id='question_text' style='"+questionTextStyle+" ' > Q"
                        + index + ". " + q.questionText + "</p>\n" + imageString;
                for(String option : q.options){
                    if(q.isImageField){
                        if(q.branchingQuestion){
                            appendString = appendString +
                                    "<p><label><input class='"+classesForInput+"' " + " type= '" + q.questionType + "' name='" + q.questionName +
                                    "' id='ques"+ index + "o" + alphabet[index2]  + "' value='"+ q.optionValue[index2] +"' style=' "+questionTypeStyle+" '> " +
                                    "<img src='images/" + option + "' alt='Question Description' class='text-center' width='300px' height='auto'/></br>\n" + "</label></p>\n";
                        } else{
                            appendString = appendString +
                                    "<p><label><input class='"+classesForInput+"'" + " type= '" + q.questionType + "' name='" + q.questionName +
                                    "' id='ques"+ index + "o" + alphabet[index2]  + "' value='"+ q.optionValue[index2] + "' style=' "+questionTypeStyle+" '> " +
                                    "<img src='images/" + option + "' alt='Question Description' class='text-center' width='300px' height='auto'/></br>\n" + "</label></p>\n";
                        }
                    }else{
                        if(q.branchingQuestion){
                            appendString = appendString +
                                    "<p><label><input class='"+classesForInput+"' " + " type= '" + q.questionType + "' name='" + q.questionName +
                                    "' id='ques"+ index + "o" + alphabet[index2]  + "' value='"+ q.optionValue[index2] +"' style=' "+questionTypeStyle+" '> " +
                                    option + "</label></p>\n";
                        } else{
                            appendString = appendString +
                                    "<p><label><input class='"+classesForInput+"'" + " type= '" + q.questionType + "' name='" + q.questionName +
                                    "' id='ques"+ index + "o" + alphabet[index2]  + "' value='"+ q.optionValue[index2] + "' style=' "+questionTypeStyle+" '> " +
                                    option + "</label></p>\n";
                        }
                    }
                    index2 = index2 + 1;
                }
                appendString = appendString + "</div>";
            }else if(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN.equalsIgnoreCase(q.questionType) || ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN.equalsIgnoreCase(q.questionType)){
                String inputText = "<input class='"+classesForInput+"'" + " type= '" + "text" + "' name='" + q.questionName + "'" + " id='ques" + index + "text'" +
                        " placeholder='Enter your answer here' maxlength='400' rows='6' cols='100' style=' "+questionTypeStyle+" '></div>\n";
                appendString = appendString + ("<div class= '"+classesForQuestion+"'><p class='normTxt' id='question_text' style='"+questionTextStyle+" ' > Q"
                        + index + ". " + q.questionText + "</p>\n"
                        + imageString
                        + inputText );

            }
            appendString = appendString + "\n</form>\n";
            if(q.branchingQuestion){
                appendString = appendString + "<!-- BranchQ-->\n";
            }
            index = index + 1;
        }
        return appendString;
    }
//    public static String createQuestions(Questions[] questionObject){
//        String appendString = "";
//        int index = 1;
//        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
//        for(Questions q: questionObject){
//            appendString = appendString + "<form id='ques" + index + "'>\n";
//            if(q.questionType == "radio" || q.questionType == "checkbox") {
//                int index2 = 0;
//                appendString = appendString + "<p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%;'> Q"
//                        + index + ". " + q.questionText + "</p>\n";
//                for(String option : q.options){
//                    if(q.branchingQuestion){
//                        appendString = appendString + "<p style='padding: 0px 15px 0px; text-align:left; width:95%; '><label>" +
//                                "<input class='custom_question_answer'" + "type= '" + q.questionType + "' name='" + q.questionName +
//                                "' -id='ques"+ index + "o" + alphabet[index2]  + "'value='"+ q.optionValue[index2] +"'> " +
//                                option + "</label></p>\n";
//                    } else{
//                        appendString = appendString + "<p style='padding: 0px 15px 0px; text-align:left; width:95%; '><label>" +
//                                "<input class='custom_question_answer'" + "type= '" + q.questionType + "' name='" + q.questionName +
//                                "' id='ques"+ index + "o" + alphabet[index2]  + "' value='"+ q.optionValue[index2] + "'>" +
//                                option + "</label></p>\n";
//                    }
//                    index2 = index2 + 1;
//                }
//            }
//            if(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN.equalsIgnoreCase(q.questionType) || ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN.equalsIgnoreCase(q.questionType)){
//                if("".equalsIgnoreCase(q.options[0])){
//                    q.options[0] = "Enter your answer here";
//                }
//                appendString = appendString + ("<p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%; ' > Q"
//                        + index + ". " + q.questionText + "</p>\n"
//                        + "<p style='padding: 0px 15px 0px; text-align:left; width:95%; '><label>" +
//                        "<input class='custom_question_answer'" + " type= '" + "text" + "' name='" + q.questionName + "'" + "id='ques" + index + "text'" +
//                        " value='"+q.options[0]+"' maxlength='400' rows='6' cols='100' style='font-size:medium; font-weight: normal; width: 65em; height: 60px;'></label></p>\n");
//            }
//            appendString = appendString + "</form>\n";
//            index = index + 1;
//        }
//        return appendString;
//    }
    public static String getStyleFromFileString(Matcher matcher){
        String style="";
        if(matcher.find()){
            String styles[] = (matcher.group(1).replaceAll("\\{", "").replaceAll("}", "").trim()).split("\n");
            for(String s:styles){
                s = (s.trim());
                style += s.split(":")[0].replaceAll("\"", "").trim() +":"+ s.split(":")[1].replaceAll("\"", "").trim().replaceAll(",$", "") +"; ";
            }
        }else{
            System.out.println("STYLE NOT FOUND !!");
        }
        return style;
    }
    public static String getClassesFromFileString(Matcher matcher){
        String classText="";
        if(matcher.find()){
            String classes[] = matcher.group(1).replaceAll("\\[", "").replaceAll("]", "").split(",");
            for(String s:classes){
                s = s.replaceAll("\"", " ")+", ";
                classText+=s;
            }
            classText = classText.trim().replaceAll(",$", "");
            return classText;
        }else{
            System.out.println("STYLE NOT FOUND !!");
        }
        return classText;
    }
    public static String getStyleFromFile(String fileAbsPath){
        try{
            InputStream stream = new FileInputStream(fileAbsPath);
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, StandardCharsets.UTF_8);
            return writer.toString();
        }catch (IOException e){
            System.out.println("CANNOT READ STYLE: "+e.getMessage());
        }
        return "";
    }

    private static List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }
        return filenames;
    }
    private static InputStream getResourceAsStream(String resource) {
        final InputStream in
                = Main.class.getClassLoader().getResourceAsStream(resource);

        return in == null ? Main.class.getResourceAsStream(resource) : in;
    }
    private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern){
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for(final File file : fileList){
            if(file.isDirectory()){
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else{
                try{
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if(accept){
                        retval.add(fileName);
                    }
                } catch(final IOException e){
                    throw new Error(e);
                }
            }
        }
        return retval;
    }


    public static String getQuestionStyleForDefaultFramework(){
        try{
            FilesFromResourcesFolder filesFromResourcesFolder = new FilesFromResourcesFolder();
//            FileResourcesUtils fileResourcesUtils = new FileResourcesUtils();
//            System.out.println(fileResourcesUtils.getPathsFromResourceJAR(ConstantVariables.DEFAULT_FRAMEWORK_FOLDER+"/questionStyle"));
            System.out.println(filesFromResourcesFolder.getAllFilesFromResource(ConstantVariables.DEFAULT_FRAMEWORK_FOLDER+"/questionStyle"));
            return "";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String toString() {
        return "Questions{" +
                "questionType='" + questionType + '\'' +
                ", questionText='" + questionText + '\'' +
                ", imageSource='" + imageSource + '\'' +
                ", options=" + Arrays.toString(options) +
                ", optionValue=" + Arrays.toString(optionValue) +
                ", questionName='" + questionName + '\'' +
                ", branchingQuestion=" + branchingQuestion +
                '}';
    }
}
