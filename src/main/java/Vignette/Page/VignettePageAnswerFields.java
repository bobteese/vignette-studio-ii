package Vignette.Page;

import javafx.scene.layout.Pane;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
Creating a new class which holds the information about question and asnwers for a give page
A class called input field already exists which is bind the grid elements and cannot be used to save information about
the input fields in a file as SimpleStringProperty is not Serializable
*/

public class VignettePageAnswerFields implements Serializable {

    private static final long SerialVersionUID = 30l;

    String question;
    List<AnswerField> answerFieldList;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerField> getAnswerFieldList() {
        return answerFieldList;
    }

    public void setAnswerFieldList(AnswerField answerFieldList) {
        this.answerFieldList.add(answerFieldList);
    }
    public VignettePageAnswerFields(){
        answerFieldList = new ArrayList<>();
    }

    @Override
    public String toString(){
        return this.answerFieldList.toString();
    }

}


