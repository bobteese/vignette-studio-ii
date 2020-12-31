package Vignette.Page;

import Application.Main;
import DialogHelper.DialogHelper;
import ConstantVariables.ConstantVariables;
import GridPaneHelper.GridPaneHelper;
import TabPane.TabPaneController;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;


public class PageMenu extends ContextMenu {

    VignettePage page;
    Button vignettePageButton;
    TabPaneController controller;
    MenuItem open = new MenuItem("Open");
    MenuItem edit = new MenuItem("Edit");
    MenuItem copy = new MenuItem("Copy");
    MenuItem paste = new MenuItem("Paste");
    MenuItem connect = new MenuItem("Connect");
    MenuItem connectCredits = new MenuItem("Connect Credits");
    MenuItem disconnect = new MenuItem("Disconnect");
    MenuItem delete = new MenuItem("Delete");

    public PageMenu(VignettePage page, Button vignettePageButton, TabPaneController controller){
        this.page = page;
        // add menu items to menu
        this.controller = controller;
        this.vignettePageButton = vignettePageButton;

        open.setOnAction(openPage());
        delete.setOnAction(deletePageData());
        edit.setOnAction(editPageDetails());
        disconnect.setOnAction(disconnectPages());

        this.getItems().add(open);
        this.getItems().add(edit);
        this.getItems().add(connect);
        this.getItems().add(connectCredits);
        this.getItems().add(disconnect);
        this.getItems().add(delete);

    }
    
    public EventHandler deletePageData() {
        return event1 -> {
            KeyEvent keyEvent = new KeyEvent(vignettePageButton, vignettePageButton,
                    KeyEvent.KEY_PRESSED, "", "", KeyCode.DELETE,
                    false, false, false, false);
            vignettePageButton.fireEvent(keyEvent);
        };
    }
    
    public EventHandler editPageDetails(){

        return e -> {
            GridPaneHelper  newPageDialog = new GridPaneHelper();
            HashMap<String, VignettePage> pageHashMap = controller.getPageViewList();
            pageHashMap.remove(page.getPageName());
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

            pageHashMap.put(pageName.getText(), this.page);


        };
    }

    public EventHandler disconnectPages(){

        return e -> {
            int len = page.nextPages.size();
            String text = len == 0? "There are no pages to disconnect":
                    "Are you sure you want to disconnect pages";
            DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Disconnect Pages",text , null, false);

            if (helper.getOk() && len>0) {
                if(len>1){
                    GridPaneHelper paneHelper = new GridPaneHelper();
                    String[] list = new String[len+1];
                     int count = 0;
                     list[count] = "Choose which page to disconnect";
                    for(String key: page.nextPages.keySet()){
                        count++;
                        list[count] = key;

                    }
                    ComboBox dropDownList = paneHelper.addDropDown(list,0,1);
                    boolean isSaved = paneHelper.createGrid("Disconnect",null,"Ok","Cancel");

                    if(isSaved){

                        Group grp = null;
                        String dropDownValue = (String)dropDownList.getValue();
                       if(page.nextPages.containsKey(dropDownValue) ){
                           grp = page.nextPages.get(dropDownValue);
                       }
                       if(grp !=null){
                           controller.getAnchorPane().getChildren().remove(grp);
                           page.removeNextPages(dropDownValue);
                           Main.getVignette().getPageViewList().get(dropDownValue).removeNextPages(page.getPageName());
                       }
                    }




                }

            }
        };

    }
    public EventHandler openPage() {
        return event -> {
            controller.openPage(page,page.getPageType());
        };
    }








}
