package Vignette.Page;

import java.io.Serializable;

public class AnswerField implements Serializable {

    public AnswerField(String answerKey, String inputName, String inputValue) {
        this.answerKey = answerKey;
        this.inputName = inputName;
        this.inputValue = inputValue;
    }
    public AnswerField(){}
    String answerKey;
    String inputName;
    String inputValue;
    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }



}
