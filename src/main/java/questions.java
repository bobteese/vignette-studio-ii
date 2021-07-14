import java.sql.Array;
import java.io.*;
import java.util.regex.Pattern;

public class questions {
    String questionType; // radio, checkbox or text
    String questionText;
    String options[];
    String optionValue[];
    String questionName;
    String ImageSrc;
    Boolean branchingQuestion;
    Boolean required;

    public questions(String qtype, String qtext, String opt[], String oValue[], String qName, String ImgSrc, Boolean bques, Boolean req ){
        questionType = qtype;
        questionText = qtext;
        options = opt;
        optionValue = oValue;
        questionName = qName;
        ImageSrc = ImgSrc;
        branchingQuestion = bques;
        required = req;
    }

    public static String createQuestions(questions[] questionObject){
        String appendString = "";
        int index = 1;
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for(questions q: questionObject){
            if(q.branchingQuestion){
                appendString = appendString + "<form id='ques" + index + "' class='branch required'>";
            }else{
                if(q.required){
                    appendString = appendString + "<form id='ques" + index + "' class='required'>";
                }else{
                    appendString = appendString + "<form id='ques" + index + "'>";
                }
            }

            if(q.questionType == "radio") {
                int index2 = 0;
                appendString = appendString + "<div class= 'form-group'><p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%; ' > Q"
                        + index + ". " + q.questionText + "</p>";
                if(q.ImageSrc != null){
                    appendString = appendString + "<img src=" + q.ImageSrc + "alt='Question Description' class='text-center' width='300px' height='400px'/>";
                }
                for(String option : q.options){
                 if(Pattern.matches("^Images/.*", option)){
                     appendString = appendString +
                             "<p><input class='custom_question_answer form-check-input'" + "type= '" + q.questionType + "' name='" + q.questionName +
                             "'id='ques"+ index + "o" + alphabet[index2]  + "'value='"+ q.optionValue[index2] +"'> " +
                             "<img src='" + option + "'alt='IMG_DESCRIPTION' style= 'width: 150px; height: 150px;'>"  + "</p>";
                     index2 = index2 + 1;
                 }else{
                     appendString = appendString +
                             "<p><input class='custom_question_answer form-check-input'" + "type= '" + q.questionType + "' name='" + q.questionName +
                             "'id='ques"+ index + "o" + alphabet[index2]  + "'value='"+ q.optionValue[index2] +"'> " +
                             option + "</p>";
                     index2 = index2 + 1;
                 }
                }
                appendString = appendString + "</div>";
            }
            if(q.questionType == "checkbox") {
                int index2 = 0;
                appendString = appendString + "<div class= 'form-group'><p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%; ' > Q"
                        + index + ". " + q.questionText + "</p>";
                for(String option : q.options){
                    if(Pattern.matches("^Images/.*", option)){
                        appendString = appendString +
                                "<p><input class='custom_question_answer form-check-input'" + "type= '" + q.questionType + "' name='" + q.questionName +
                                "'id='ques"+ index + "o" + alphabet[index2]  + "'value='"+ q.optionValue[index2] +"'> " +
                                "<img src='" + option + "'alt='IMG_DESCRIPTION' style= 'width: 150px; height: 150px;'>"  + "</p>";
                        index2 = index2 + 1;
                    }else {
                        appendString = appendString +
                                "<p><input class='custom_question_answer form-check-input'" + "type= '" + q.questionType + "' name='" + q.questionName +
                                "'id='ques" + index + "o" + alphabet[index2] + "'value='" + q.optionValue[index2] + "'> " +
                                option + "</p>";
                        index2 = index2 + 1;
                    }
                }
                appendString = appendString + "</div>";
            }
            if(q.questionType == "text"){
                appendString = appendString + ("<div class= 'form-group'><p class='normTxt' id='question_text' style='padding: 0px 15px 0px; text-align:left; width:95%; ' > Q"
                        + index + ". " + q.questionText + "</p>"
                        +
                        "<input class='custom_question_answer form-class'" + "type= '" + q.questionType + "' name='" + q.questionName + "'" + "id='ques" + index + "text'" +
                        " value='Enter your answer here' maxlength='400' rows='6' cols='100' style='font-size:medium; font-weight: normal; width: 95%; height: 60px;'></div>");
            }
            appendString = appendString + "</form>";
            index = index + 1;
        }
        return appendString;
    }

    public static void main(String[] args) {
        String[] options = {"Images/logo1.jpg", "Images/logo2.jpg", "Images/logo1.jpg", "Images/logo1.jpg"};
        String[] options2 = {};
        String[] optionsValue = {"A", "B", "C", "D"};
        String[] optionsValue2 = {};
        String ImgSrc = "../Images/image1.png";
        questions questionObject[] = new questions[3];
        questionObject[0] = new questions("radio", "Enter your radio type question here", options, optionsValue, "q1" , ImgSrc, true, true);
        questionObject[1] = new questions("checkbox", "Enter your checkbox type question here", options, optionsValue, "q2", null,false, false );
        questionObject[2] = new questions("text", "Enter your text type question here", options2, optionsValue2, "q3", ImgSrc,false, true);
        System.out.println(createQuestions(questionObject));
    }
}


