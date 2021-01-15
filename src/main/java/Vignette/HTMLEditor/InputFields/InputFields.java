package Vignette.HTMLEditor.InputFields;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InputFields {

    private final StringProperty inputType = new SimpleStringProperty();
    private final StringProperty inputClass = new SimpleStringProperty();
    private final StringProperty inputName = new SimpleStringProperty();
    private final StringProperty inputId = new SimpleStringProperty();
    private final StringProperty inputValue = new SimpleStringProperty();

    public String getInputType() {
        return inputType.get();
    }

    public StringProperty inputTypeProperty() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType.set(inputType);
    }

    public String getInputClass() {
        return inputClass.get();
    }

    public StringProperty inputClassProperty() {
        return inputClass;
    }

    public void setInputClass(String inputClass) {
        this.inputClass.set(inputClass);
    }

    public String getInputName() {
        return inputName.get();
    }

    public StringProperty inputNameProperty() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName.set(inputName);
    }

    public String getInputId() {
        return inputId.get();
    }

    public StringProperty inputIdProperty() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId.set(inputId);
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





}
