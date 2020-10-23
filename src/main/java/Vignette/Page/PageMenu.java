package Vignette.Page;

import DialogHelper.DialogHelper;
import ConstantVariables.ConstantVariables;
import GridPaneHelper.GridPaneHelper;
import TabPane.TabPaneController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class PageMenu extends ContextMenu {

    VignettePage page;
    Button vignettePageButton;
    TabPaneController controller;
    MenuItem open = new MenuItem("Open");
    MenuItem edit = new MenuItem("Edit");
    MenuItem connect = new MenuItem("Connect");
    MenuItem connectCredits = new MenuItem("Connect Credits");
    MenuItem disconnect = new MenuItem("Disconnect");
    MenuItem delete = new MenuItem("Delete");

    public PageMenu(VignettePage page, Button vignettePageButton, TabPaneController controller){
        this.page = page;
        // add menu items to menu
        this.controller = controller;
        this.vignettePageButton = vignettePageButton;

        delete.setOnAction(deletePageData());
        edit.setOnAction(editPageDetails());

        this.getItems().add(open);
        this.getItems().add(edit);
        this.getItems().add(connect);
        this.getItems().add(connectCredits);
        this.getItems().add(disconnect);
        this.getItems().add(delete);

    }
    
    public EventHandler deletePageData() {
        EventHandler<ActionEvent> event = event1 -> {
            KeyEvent keyEvent = new KeyEvent(vignettePageButton, vignettePageButton,
                    KeyEvent.KEY_PRESSED, "", "", KeyCode.DELETE,
                    false, false, false, false);
            vignettePageButton.fireEvent(keyEvent);
        };
        return event;
    }
    
    public EventHandler editPageDetails(){

        EventHandler<ActionEvent> event = e -> {
            GridPaneHelper  newPageDialog = new GridPaneHelper();
            boolean disableCheckBox = !this.page.isFirstPage && (controller.getFirstPageCount() != 0);
            CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
            if(page.isFirstPage) {checkBox.setSelected(true);}
            TextField pageName = newPageDialog.addTextField(1,2, 400);
            ComboBox dropDownPageType = newPageDialog.addDropDown(ConstantVariables.listOfPageTypes,1,3);
            dropDownPageType.getSelectionModel().select(page.pageType);
            pageName.setText(page.getPageName());
            boolean cancelClicked = newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");
            // if page ids exists  or if the text is empty
            String prevText = page.getPageName();
            boolean isValid = page.getPageName().equals(pageName.getText()) || !controller.getPageNameList().contains(pageName.getText()) && pageName.getText().length() > 0
                    && !dropDownPageType.getValue().equals("Please select page type");
            if(!cancelClicked) { newPageDialog.closeDialog(); };
            while (!isValid){

                String message = pageName.getText().length() == 0? "Page id should not be empty":
                        controller.getPageNameList().contains(pageName.getText())?" All page id must be unique"
                                :dropDownPageType.getValue().equals("Please select page type")?"Select Page Type":"";
                DialogHelper helper = new DialogHelper(Alert.AlertType.INFORMATION,"Message",null,
                        message,false);
                if(helper.getOk()) { newPageDialog.showDialog(); }
                isValid = !page.getPageName().equals(pageName.getText()) && !controller.getPageNameList().contains(pageName.getText())
                          && pageName.getText().length() >0 && !dropDownPageType.getValue().equals("Please select page type");
                if(!cancelClicked) { newPageDialog.closeDialog(); };
            }
            boolean check = checkBox.isSelected();
            if (check){
                page.setFirstPage(true);
                controller.setFirstPageCount(1);
            }
            else if(!checkBox.isDisabled()) {
                page.setFirstPage(false);
                controller.setFirstPageCount(0);
            }
            if(!prevText.equals(pageName.getText())){
                controller.getPageNameList().add(pageName.getText());
                controller.getPageNameList().remove(prevText);
            }
            this.page.setPageName(pageName.getText());
            vignettePageButton.setText(pageName.getText());
            this.page.setPageType(dropDownPageType.getValue().toString());
        };

        return event;

    }








}
