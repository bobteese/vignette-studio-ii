package Vignette.Page;

import ConstantVariables.ConstantVariables;

import java.io.Serializable;
import java.util.Arrays;

public class Questions implements Serializable {
    public Questions(){}

    public Questions(Questions q){
        this.questionType = q.questionType;
        this.questionText = q.questionText;
        this.options = q.options;
        this.optionValue = q.optionValue;
        this.questionName = q.questionName;
        this.branchingQuestion = q.branchingQuestion;
    }

    public Questions(String questionType, String questionText, String[] options, String[] optionValue, String questionName, Boolean branchingQuestion) {
        this.questionType = questionType;
        this.questionText = questionText;
        this.options = options;
        this.optionValue = optionValue;
        this.questionName = questionName;
        this.branchingQuestion = branchingQuestion;
    }

    String questionType; // radio, checkbox or text
    String questionText;
    String options[];
    String optionValue[];
    String questionName;
    Boolean branchingQuestion;

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
    public static String createQuestions(Questions[] questionObject){
        String appendString = "";
        int index = 1;
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for(Questions q: questionObject){
            appendString = appendString + "<form id='ques" + index + "'>\n";
            if(q.questionType == "radio" || q.questionType == "checkbox") {
                int index2 = 0;
                appendString = appendString + "<div class= 'form-group'><p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%; ' > Q"
                        + index + ". " + q.questionText + "</p>\n";
                for(String option : q.options){
                    if(q.branchingQuestion){
                        appendString = appendString +
                                "<input class='custom_question_answer form-check-input'" + "type= '" + q.questionType + "' name='" + q.questionName +
                                "'id='ques"+ index + "o" + alphabet[index2]  + "'value='"+ q.optionValue[index2] +"'> " +
                                option + "</label></p>\n";
                    } else{
                        appendString = appendString +
                                "<input class='custom_question_answer form-check-input'" + "type= '" + q.questionType + "' name='" + q.questionName +
                                "'id='ques"+ index + "o" + alphabet[index2]  + "' value='"+ q.optionValue[index2] + "'> " +
                                option + "</label></p>\n";
                    }
                    index2 = index2 + 1;
                }
                appendString = appendString + "</div>";
            }
            if(ConstantVariables.TEXTFIELD_INPUT_TYPE_DROPDOWN.equalsIgnoreCase(q.questionType) || ConstantVariables.TEXTAREA_INPUT_TYPE_DROPDOWN.equalsIgnoreCase(q.questionType)){
                appendString = appendString + ("<div class= 'form-group'><p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%; ' > Q"
                        + index + ". " + q.questionText + "</p>\n"
                        +
                        "<input class='custom_question_answer form-class'" + "type= '" + q.questionType + "' name='" + q.questionName + "'" + "id='ques" + index + "text'" +
                        " value='Enter your answer here' maxlength='400' rows='6' cols='100' style='font-size:medium; font-weight: normal; height: 60px;'></div>\n");
            }
            appendString = appendString + "</form>\n";
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

    @Override
    public String toString() {
        return "Questions{" +
                "questionType='" + questionType + '\'' +
                ", questionText='" + questionText + '\'' +
                ", options=" + Arrays.toString(options) +
                ", optionValue=" + Arrays.toString(optionValue) +
                ", questionName='" + questionName + '\'' +
                ", branchingQuestion=" + branchingQuestion +
                '}';
    }
}
