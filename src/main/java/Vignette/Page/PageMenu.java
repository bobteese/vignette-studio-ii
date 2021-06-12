package Vignette.Page;

import Application.Main;
import DialogHelpers.DialogHelper;
import ConstantVariables.ConstantVariables;
import GridPaneHelper.GridPaneHelper;
import TabPane.TabPaneController;
import Vignette.HTMLEditor.HTMLEditorContent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;


import java.util.HashMap;


public class PageMenu extends ContextMenu {

    VignettePage page;
    Button vignettePageButton;
    TabPaneController controller;
    MenuItem open = new MenuItem("Open");
    MenuItem duplicate = new MenuItem("Duplicate");
    MenuItem connect = new MenuItem("Connect");
    MenuItem disconnect = new MenuItem("Disconnect");
    MenuItem delete = new MenuItem("Delete");


    //VignettePage copiedPage;


    //todo I added this
    private HashMap<String, HTMLEditorContent> htmlEditorContent;
    private HTMLEditorContent content;
    //

    public PageMenu(VignettePage page, Button vignettePageButton, TabPaneController controller){
        this.page = page;
        // add menu items to menu
        this.controller = controller;
        this.vignettePageButton = vignettePageButton;

        //todo I added this
        //todo This is for the COPY PASTE functionality
        htmlEditorContent = controller.getHTMLContentEditor();
        content = htmlEditorContent.get(page.getPageName());
        //-----------------------------------------------------


        open.setOnAction(openPage());
        delete.setOnAction(deletePageData());
        disconnect.setOnAction(disconnectPages());
        duplicate.setOnAction(duplicatePage());

        //KeyCombination copyKeyCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        //KeyCombination pasteKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);

        //copy.setAccelerator(copyKeyCombination);
        //paste.setAccelerator(pasteKeyCombination);

        this.getItems().addAll(open,duplicate,connect,disconnect,delete);

    }


    /**
     * todo so this function pretty much already does duplicate the code
     * find out how 
     * @return
     */
    private EventHandler<ActionEvent> duplicatePage() {

        return event -> {

            VignettePage page = controller.createNewPageDialog(true,this.page.getPageType());
            if (this.page.getPageData() != null) {
                page.setPageData(this.page.getPageData());
            }
            ImageView droppedView = new ImageView(new Image(getClass().getResourceAsStream(ConstantVariables.IMAGE_RESOURCE_PATH)));
            controller.createVignetteButton(page,droppedView,
                                            vignettePageButton.getLayoutX()+100,vignettePageButton.getLayoutY(),
                                           page.getPageType());

        };

    }

    /**
     * @return

    private EventHandler<ActionEvent> copyPage() {

        //This is in fact that page that you right click on.
        return event -> copiedPage = page;


        //return  event -> {copiedPage = page;
        //System.out.println("Copied page = "+copiedPage.pageType);
        //};
*/
            /**
             * todo : after getting the content of the page you have to add it to htmlEditorcontent hashmap.
             * todo : the copied page's new name will be the key for the value
             * todo : Cant directly add it because it would be bugged if the user doesnt paste it.
             * todo : if youre duplicating then you can add it probably
             * todo : find out how to Paste the new page since its not being dragged to your specified location
             * todo : add right click functionality on the 'right anchor pane'
             */

    //}


    public EventHandler deletePageData() {
        return event1 -> {
            KeyEvent keyEvent = new KeyEvent(vignettePageButton, vignettePageButton,
                    KeyEvent.KEY_PRESSED, "", "", KeyCode.DELETE,
                    false, false, false, false);
            vignettePageButton.fireEvent(keyEvent);
        };
    }


    public EventHandler disconnectPages(){

        return e -> {
            int len = page.nextPages.size();
            String text = len == 0? "There are no pages to disconnect":
                    "Are you sure you want to disconnect pages";
            DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Disconnect Pages",text , null, false);

            if (helper.getOk() && len>0) {
                if(len>=1){
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
