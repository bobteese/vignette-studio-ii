package Vignette.HTMLEditor.InputFields;

import SaveAsFiles.Images;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InputFields {

    private int id;
    private  String inputType;
    private final StringProperty inputName = new SimpleStringProperty();
    private final StringProperty inputValue = new SimpleStringProperty();
    private final StringProperty answerKey = new SimpleStringProperty();

    private String imageFileName;
    private boolean isImageField;

    public String getAnswerKey() { return answerKey.get();}
    public StringProperty answerKeyProperty() { return answerKey; }

    public void setAnswerKey(String answerKey) { this.answerKey.set(answerKey); }
    public String getInputType() {
        return inputType;
    }
    public void  setInputType(String inputType) { this.inputType = inputType;}
    public String getInputName() {
        return inputName.get();
    }

    public StringProperty inputNameProperty() {
        return inputName;
    }
    public void setInputName(String inputName) {
        this.inputName.set(inputName);
    }

    public String getInputValue() {
        return inputValue.get();
    }
    public StringProperty inputValueProperty() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue.set(inputValue);
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String  getImages() { return imageFileName; }
    public void setImages(String images) { this.imageFileName = images; }
    public boolean isImageField() { return isImageField; }
    public void setImageField(boolean imageField) { isImageField = imageField; }

    @Override
    public String toString() {
        String value ="";
        if (isImageField){
          value="<label>"+
                  "<input class='custom_question_answer'  " +
                  "type='" + inputType + "' " +
                  "name='" + inputName.getValue() + "' " +
                  "id='" + id + "' " +
                  "value='" + inputValue.getValue() + "'>\n"+
                   inputValue.getValue()+
                   "\n<img class=\"img-fluid\" " +
                  "src=\"Images/"+imageFileName+"\" alt=\"IMG_DESCRIPTION\" width=\"400\" height=\"auto\" " +
                  "style=\"vertical-align:bottom;\"> \n</label>";
        }
        else {
            value= "<label>" +
                    "<input class='custom_question_answer'  " +
                    "type='" + inputType + "' " +
                    "name='" + inputName.getValue() + "' " +
                    "id='" + id + "' " +
                    "value='" + inputValue.getValue() + "'>\n" +
                    answerKey.getValue() + "\n" +
                    "</label>\n";
        }
        return value;

    }
}
