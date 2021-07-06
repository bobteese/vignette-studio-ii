package TabPane;

import Application.Main;
import ConstantVariables.BranchingConstants;
import SaveAsFiles.Images;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.VignettePage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class PagesTab implements Initializable{
    @FXML
    ListView<String> imageListView; // list of image view for the left panel
    @FXML
    AnchorPane rightAnchorPane; // the right anchor pane where the images are dropped
    @FXML
    TextArea htmlSourceCodeTemp;
    @FXML
    Button addImage;
    @FXML
    ComboBox branchingType;
    @FXML
    TextField numberOfAnswerChoice;
    @FXML
    Button nextPageAnswers;
    @FXML
    Label pageName;

    List<Images> imagesList = new ArrayList<>();
    HTMLEditorContent content;
    TabPaneController paneController;
    HashMap<String, String> connectionEntries = new HashMap<>();

    public String getHtmlSourceCodeTextArea() {
        return htmlSourceCodeTextArea.get();
    }

    public StringProperty htmlSourceCodeTextAreaProperty() {
        return htmlSourceCodeTextArea;
    }

    public void setHtmlSourceCodeTextArea(String htmlSourceCodeTextArea) {
        this.htmlSourceCodeTextArea.set(htmlSourceCodeTextArea);
    }

    StringProperty htmlSourceCodeTextArea = new SimpleStringProperty();
    public TabPaneController getPaneController() {
        return paneController;
    }

    public void setPaneController(TabPaneController paneController) {
        this.paneController = paneController;
    }

    public HTMLEditorContent getContent() {
        return content;
    }

    public void setContent(HTMLEditorContent content) {
        this.content = content;
    }

    public VignettePage getPage() {
        return page;
    }

    public void setPage(VignettePage page) {
        this.page = page;
    }

    VignettePage page;
    Tab pagesTab;
    public PagesTab(){}
    public PagesTab(VignettePage page, HTMLEditorContent content) {
        System.out.println("PAGES TAB CON CALLED!!");
        this.page = page;
        paneController = Main.getVignette().getController();
        for (HashMap.Entry<String, String> entry : page.getPagesConnectedTo().entrySet()) {
            String[] temp = entry.getValue().split(",");
            for(String x: temp)
                connectionEntries.put(x.trim(), entry.getKey());
        }
        this.content = paneController.getHTMLContentEditor().get(page.getPageName());
        System.out.println("CONTENT: "+content.toString());
    }

    @FXML public void addImage(ActionEvent actionEvent) {
        imagesList.add(content.addImageTag());
        Main.getVignette().setImagesList(imagesList);
    }
    @FXML public void pageSettingsButtonAction(ActionEvent actionEvent){
        content.editPageSettings();
    }
    @FXML public void addVideoToEditor(ActionEvent actionEvent) {
        content.addVideo();
    }
    @FXML public void addProblemStatmentToQuestion(ActionEvent actionEvent) {
        content.addProblemStatmentToQuestion();
    }
    @FXML public void addInputFieldToEditor(ActionEvent actionEvent) {
        content.addInputFields(false);
    }
    @FXML public void addImageInputField(ActionEvent actionEvent) {
        content.addInputFields(true);
    }
    @FXML public void selectBranchingType(ActionEvent actionEvent){

    }
    @FXML public void onNumberChoiceKeyRelased(ActionEvent actionEvent){

    }
    @FXML public void NextPageAnswersButtonAction(ActionEvent actionEvent){

    }

    public void makeDefaultSetting(){
        if(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()>0)
            numberOfAnswerChoice.setText(page.getVignettePageAnswerFieldsBranching().getAnswerFieldList().size()+"");
        else if(connectionEntries.size()!=0)
            numberOfAnswerChoice.setText(connectionEntries.size()-1+"");
        if("".equalsIgnoreCase(page.getPageData()) || page.getPageData()==null){
            try{
                htmlSourceCodeTemp.setText(content.addTextToEditor());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            htmlSourceCodeTemp.setText(page.getPageData());
        }
        page.setPageData(htmlSourceCodeTemp.getText());
    }

    @FXML
    public void popData(){
        if(htmlSourceCodeTemp!=null)
            htmlSourceCodeTemp.setText("HELLO");
        else
            System.out.println("htmlSourceCodeTemp is null");
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paneController = Main.getVignette().getController();
        if(page==null)
            System.out.println("PAGE IS NULL");
        else
            System.out.println(page.getPageName());
        htmlSourceCodeTemp.setText("HELLO");
        htmlSourceCodeTemp.textProperty().bindBidirectional(htmlSourceCodeTextAreaProperty());
        branchingType.getItems().addAll(BranchingConstants.NO_QUESTION, BranchingConstants.RADIO_QUESTION,
                BranchingConstants.CHECKBOX_QUESTION);
        branchingType.setValue(BranchingConstants.NO_QUESTION);
        nextPageAnswers.setDisable(false);
        numberOfAnswerChoice.setText("0");
    }

//    public void buildConnectionEntriesMap(VignettePage page) {
//        for (HashMap.Entry<String, String> entry : page.getPagesConnectedTo().entrySet()) {
//            String[] temp = entry.getValue().split(",");
//            for(String x: temp)
//                connectionEntries.put(x.trim(), entry.getKey());
//        }
//    }
    public void addDefaultSetting(){

    }
}
