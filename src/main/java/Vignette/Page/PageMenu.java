package Vignette.Page;

import Application.Main;
import ConstantVariables.BranchingConstants;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import TabPane.TabPaneController;
import Vignette.Framework.ReadFramework;
import Vignette.HTMLEditor.HTMLEditorContent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.util.HashMap;
import java.util.List;


public class PageMenu extends ContextMenu {

    VignettePage page;
    Button vignettePageButton;
    TabPaneController controller;
    MenuItem open = new MenuItem("Open");
    MenuItem duplicate = new MenuItem("Duplicate");
    MenuItem rename = new MenuItem("Rename");
    MenuItem connect = new MenuItem("Connect");
    MenuItem disconnect = new MenuItem("Disconnect");
    MenuItem delete = new MenuItem("Delete");


    //VignettePage copiedPage;


    //todo I added this
    private HashMap<String, HTMLEditorContent> htmlEditorContent;
    private HTMLEditorContent content;
    //

    public PageMenu(VignettePage page, Button vignettePageButton, TabPaneController controller) {
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
        rename.setOnAction(renamePage());

        //KeyCombination copyKeyCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        //KeyCombination pasteKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);

        //copy.setAccelerator(copyKeyCombination);
        //paste.setAccelerator(pasteKeyCombination);

//        this.getItems().addAll(open,duplicate,connect,disconnect,delete);
        this.getItems().addAll(open, duplicate, delete, rename);

    }

    private EventHandler<ActionEvent> renamePage() {
        return event -> {
            GridPaneHelper helper = new GridPaneHelper();
            TextField text = helper.addTextField(0, 2, 400);
            text.setText(page.getPageName());

            boolean clickedOk = helper.createGrid("Enter New Page Name", null, "ok", "Cancel");
            if (clickedOk) {
                if (!"".equalsIgnoreCase(text.getText())) {
                    final String regexForPageName = "^[a-zA-Z0-9_-]*$";
                    String newPageName = text.getText();
                    System.out.println("new Page Name "+ newPageName) ;
                    List<String> pageNameList = Main.getVignette().getController().getPageNameList();
                    boolean isValid = !pageNameList.contains(newPageName) && newPageName.length() > 0 && newPageName.matches(regexForPageName)
                            && !newPageName.equalsIgnoreCase("No Link");
                    //checking whether the user has entered a unique pageID
                    while (!isValid) {
                        String message = newPageName.equalsIgnoreCase("No Link")? "Page Name cannot be 'No Link'" :
                                pageNameList.contains(newPageName) ? " All page names must be unique"
                                : newPageName.length() == 0 ? "Page name should not be empty" :
                                !newPageName.matches(regexForPageName) ? "Page name can be alphanumeric with underscores and hyphens" :
                                          "";
                        //creating an information alert to deal--------------
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Alert");
                        alert.setContentText(message);
                        alert.showAndWait();
                        text.setText(newPageName.replaceAll("[^a-zA-Z0-9.\\-_]", "-"));
                        System.out.println("text "+ text.getText()) ;
                        //---------------------------------------------------
                        clickedOk = helper.showDialog();
                        newPageName = text.getText();
                        isValid = !pageNameList.contains(newPageName) && newPageName.length() > 0 && newPageName.matches(regexForPageName);
                        if (!clickedOk) break;
                    }
                    if (clickedOk) {
                        String key = page.getPageName();
                        Main.getVignette().getController().getButtonPageMap().put(newPageName, Main.getVignette().getController().getButtonPageMap().get(key));
                        Main.getVignette().getController().getButtonPageMap().get(key).setText(newPageName);
                        Main.getVignette().getController().getButtonPageMap().remove(key);

                        Main.getVignette().getLastPageValueMap().put(newPageName, Main.getVignette().getLastPageValueMap().get(key).booleanValue());
                        Main.getVignette().getLastPageValueMap().remove(key);

                        Main.getVignette().getHtmlContentEditor().put(newPageName, Main.getVignette().getHtmlContentEditor().get(key));
                        Main.getVignette().getHtmlContentEditor().remove(key);


                        for (int i = 0; i < Main.getVignette().getController().getPageNameList().size(); i++) {
                            if (Main.getVignette().getController().getPageNameList().get(i).equalsIgnoreCase(key)) {
                                Main.getVignette().getController().getPageNameList().remove(i);
                                break;
                            }
                        }

                        Main.getVignette().getPageViewList().put(newPageName, page);
                        Main.getVignette().getPageViewList().remove(key);
                        page.setPageName(newPageName);
                        Main.getVignette().getController().getPageNameList().add(key);
                        System.out.println("page name " + page.getPageName());
                        //preserving connection before removing for the new page
                        for (String s : Main.getVignette().getController().getPageNameList()) {
                            VignettePage temp = Main.getVignette().getPageViewList().get(s);
                            if (temp == null){
                                continue;
                            }
                            if (temp.getPagesConnectedTo() != null
                                    && temp.getPagesConnectedTo().containsKey(key)) {
                                temp.getPagesConnectedTo().put(key, temp.getPagesConnectedTo().get(key));
                                temp.getPagesConnectedTo().remove(key);
                            }

                            temp.setNextPageAnswerNames(temp.getNextPageAnswerNames().replace(key, newPageName));
                            String htmlText = temp.getPageData();
                            String nextPageAnswers = Main.getVignette().getPageViewList().get(s).getNextPageAnswerNames();
                            htmlText = !nextPageAnswers.equals("") ?
                                    htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET, nextPageAnswers) :
                                    htmlText.replaceFirst(BranchingConstants.NEXT_PAGE_ANSWER_NAME_TARGET, "NextPageAnswerNames={};");
                            temp.setPageData(htmlText);
                            Main.getVignette().getPageViewList().put(s, temp);
                        }
                    }
                }
            }
        };
    }

    public static void copyPasteVignettePage(VignettePage page, Button vignettePageButton) {
        TabPaneController controller = Main.getVignette().getController();
        VignettePage newPage = controller.createNewPageDialog(true, page.getPageType());
        if (page != null && page.getPageData() != null) {
            page.setPageData(page.getPageData());
        }

        ImageView droppedView = null;
        if (page != null) {
            if (Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()) != null) {
                File f = new File(ReadFramework.getUnzippedFrameWorkDirectory() + "/" + Main.getVignette().getImagesPathForHtmlFiles().get(page.getPageType()));
                droppedView = new ImageView(f.toURI().toString());
            } else {
                droppedView = new ImageView(new Image(ConstantVariables.DEFAULT_RESOURCE_PATH)); // create a new image view
            }
        }
        if (newPage != null) {
            controller.createVignetteButton(newPage, droppedView,
                    vignettePageButton.getLayoutX() + 100, vignettePageButton.getLayoutY(),
                    page.getPageType());
            newPage.setPageData(page.getPageData());
            newPage.setQuestionList(page.getQuestionList());
            newPage.setNumberOfNonBracnchQ(page.getNumberOfNonBracnchQ());
            newPage.setVignettePageAnswerFieldsBranching(page.getVignettePageAnswerFieldsBranching());
            newPage.setVignettePageAnswerFieldsNonBranching(page.getVignettePageAnswerFieldsNonBranching());
            newPage.setQuestionType(page.getQuestionType());
//                newPage.setNextPageAnswerNames(page.getNextPageAnswerNames());
//                newPage.setNumberOfNonBracnchQ(page.getNumberOfNonBracnchQ());
        }
    }

    /**
     * todo so this function pretty much already does duplicate the code
     * find out how
     *
     * @return
     */
    private EventHandler<ActionEvent> duplicatePage() {

        return event -> {
            copyPasteVignettePage(page, vignettePageButton);
        };

    }

    /**
     * @return private EventHandler<ActionEvent> copyPage() {

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


    public EventHandler disconnectPages() {

        return e -> {
            int len = page.nextPages.size();
            String text = len == 0 ? "There are no pages to disconnect" :
                    "Are you sure you want to disconnect pages";
            DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Disconnect Pages", text, null, false);

            if (helper.getOk() && len > 0) {
                if (len >= 1) {
                    GridPaneHelper paneHelper = new GridPaneHelper();
                    String[] list = new String[len + 1];
                    int count = 0;
                    list[count] = "Choose which page to disconnect";
                    for (String key : page.nextPages.keySet()) {
                        count++;
                        list[count] = key;

                    }
                    ComboBox dropDownList = paneHelper.addDropDown(list, 0, 1);
                    boolean isSaved = paneHelper.createGrid("Disconnect", null, "Ok", "Cancel");

                    if (isSaved) {

                        Group grp = null;
                        String dropDownValue = (String) dropDownList.getValue();
                        if (page.nextPages.containsKey(dropDownValue)) {
                            grp = page.nextPages.get(dropDownValue);
                        }
                        if (grp != null) {
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
            controller.openPage(page, page.getPageType());
        };
    }


}
