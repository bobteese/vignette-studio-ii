package TabPane;

import Application.Main;
import SaveAsFiles.Images;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.VignettePage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PagesTab implements Initializable{
    @FXML
    ListView<String> imageListView; // list of image view for the left panel
    @FXML
    AnchorPane rightAnchorPane; // the right anchor pane where the images are dropped
//    @FXML
//    Tab pagesTab;
//    @FXML
//    Tab vignetteTab;
//    @FXML
//    TabPane tabPane;
    @FXML
    TextArea htmlSourceCode;
    @FXML
    Button addImage;
//    @FXML
//    Button addProblemStatement;
//    @FXML
//    ComboBox selectNextPage;
//    @FXML
//    ScrollPane scrollPane;
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
    VignettePage page;
    Tab pagesTab;
    public PagesTab(){}
//    public PagesTab(VignettePage page, Tab tab) {
////        htmlSourceCode.setMaxWidth(Main.getVignette().getController().htmlSourceCode.getMaxWidth());
//        this.page = page;
//        this.pagesTab = tab;
//        this.htmlSourceCode.setText(page.getPageData());
//        paneController = Main.getVignette().getController();
//
//    }
    public void addImage(ActionEvent actionEvent) {
        imagesList.add(content.addImageTag());
        Main.getVignette().setImagesList(imagesList);
    }
    public void pageSettingsButtonAction(ActionEvent actionEvent){

    }
    public void addVideoToEditor(ActionEvent actionEvent){

    }
    public void addInputFieldToEditor(ActionEvent actionEvent){

    }
    public void addImageInputField(ActionEvent actionEvent){

    }
    public void selectBranchingType(ActionEvent actionEvent){

    }
    public void onNumberChoiceKeyRelased(ActionEvent actionEvent){

    }
    public void NextPageAnswersButtonAction(ActionEvent actionEvent){

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paneController = Main.getVignette().getController();
    }
}
