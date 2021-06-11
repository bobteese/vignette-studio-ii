package TabPane;

import Application.Main;
import ConstantVariables.ConstantVariables;
import DialogHelpers.DialogHelper;
import GridPaneHelper.GridPaneHelper;
import Vignette.Page.PageMenu;
import Vignette.Page.VignettePage;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageCreator {

    private TabPaneController controller;
    HashMap<String,Image> imageMap;
    HashMap<String, String> pageIds;

    HashMap<String, Button> buttonPageMap = new HashMap<>();
    private HashMap<String,VignettePage> pageViewList = Main.getVignette().getPageViewList();
    private List<String> pageNameList = new ArrayList<String>();

    private int firstPageCount = 0;

    Button one;
    Button two;
    VignettePage pageOne;
    VignettePage pageTwo;
    Boolean isConnected= false;


    public PageCreator(TabPaneController controller)
    {
        this.controller=controller;
        imageMap = controller.getImageMap();
        pageIds = controller.getPageIds();

    }




    /**
     *  This function makes use of DragEvents and the information stored on the DragBoard to create the required HTML
     *  page in the vignette editor. The images on the listView are associated with the appropriate HTML pages in order
     *  to do so.
     * @return
     */
    public VignettePage createPage(DragEvent event) {

        System.out.println("Page created using page creator");


        Dragboard db = event.getDragboard();

        String pageType="";
        //if (db.hasString())
        //    pageType = db.getString().trim();

        pageType = db.getString().trim();
        GridPaneHelper newPageDialog = new GridPaneHelper();

        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        boolean selected = false;
        if(pageType.equalsIgnoreCase(ConstantVariables.LOGIN_PAGE_TYPE)){
            checkBox.setSelected(true);
            checkBox.setDisable(true);
        }
        //textbox to enter page name
        TextField pageName = newPageDialog.addTextField(1, 3, 400);
        //setting the default pageID
        pageName.setText(pageIds.get(pageType));
        String pageTitle = "Create New "+pageType+" Page";
        boolean cancelClicked = newPageDialog.createGrid(pageTitle, "Please enter the page name", "Ok", "Cancel");
        if (!cancelClicked) return null;
        boolean isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0;

        //checking whether the user has entered a unique pageID
        while (!isValid) {
            String message = pageNameList.contains(pageName.getText()) ? " All page id must be unique"
                    : pageName.getText().length() == 0 ? "Page id should not be empty" : "";


            //creating an information alert to deal--------------
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setContentText(message);
            alert.showAndWait();
            //---------------------------------------------------


            cancelClicked = newPageDialog.showDialog();
            isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0;
            if (!cancelClicked) return null;
        }

        boolean check = checkBox.isSelected();
        if(check){
            firstPageCount++;
            Main.getVignette().setHasFirstPage(true);
        }
        pageNameList.add(pageName.getText());

        //creating a new Vignette page based off user provided information.
        VignettePage page = new VignettePage(pageName.getText().trim(), check, pageType);
        return page;
    }




    /**
     * This function creates a Vignette page at position (X,Y) on the right Anchor pane
     * Used in RightClickMenu.java
     * @param page the vignette page created
     * @param posX X coordinate of right click
     * @param posY Y coordinate of right click
     */
    public void createPageFromRightClick(VignettePage page, double posX, double posY)
    {


        System.out.println("Creating using pageCreator");

        if(page!=null)
        {
            Image imageValue = imageMap.get(page.getPageType());
            ImageView droppedView = new ImageView(imageValue); // create a new image view

            if (page != null) {
                Button pageViewButton = createVignetteButton(page, droppedView, posX, posY, page.getPageType());
            }
        }
    }




    /**
     *This was the function used in the original vignette studio ii to create pages after drag and dropping
     * the plain orange icon. Once the icon is dropped it will open a dialog box that prompts you to choose the required
     * page type and type in a valid page id.
     *
     * This is not used for the drag and drop method of creating pages anymore but rather when you use the new option from
     * the right click menu anywhere on the right anchor pane.
     *
     * Called in TabPane.RightClickMenu.java
     */
    public VignettePage createNewPageDialog(boolean pastePage, String pageType){
        GridPaneHelper  newPageDialog = new GridPaneHelper();
        boolean disableCheckBox = Main.getVignette().doesHaveFirstPage() || Main.getVignette().isHasFirstPage();
        CheckBox checkBox = newPageDialog.addCheckBox("First Page", 1,1, true, disableCheckBox);
        ComboBox dropDownPageType = newPageDialog.addDropDown(ConstantVariables.listOfPageTypes,1,2);
        TextField pageName = newPageDialog.addTextField(1,3, 400);

        dropDownPageType.setOnAction(event -> {
            String value = (String) dropDownPageType.getValue();
            if(value.equals(ConstantVariables.LOGIN_PAGE_TYPE)) pageName.setText("login");
            if(value.equals(ConstantVariables.QUESTION_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.WHAT_LEARNED_PAGE_TYPE)) pageName.setText("whatLearned");
            if(value.equals(ConstantVariables.RESPONSE_CORRECT_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.RESPONSE_INCORRECT_PAGE_TYPE)) pageName.setText("q");
            if(value.equals(ConstantVariables.CREDIT_PAGE_TYPE)) pageName.setText("credits");
            if(value.equals(ConstantVariables.COMPLETION_PAGE_TYPE)) pageName.setText("Completion");
        });
        if(pastePage && pageType!=null){
            dropDownPageType.setValue(pageType);
            dropDownPageType.setDisable(true);
        }

        String pageTitle = "Create New Page";
        boolean cancelClicked = newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");
        if(!cancelClicked) return null;

        // if page ids exists  or if the text is empty
        boolean isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0 && !dropDownPageType.getValue().equals("Please select page type");
        boolean start = !dropDownPageType.getValue().equals("Please select page type");

        while (!isValid){
            String message = dropDownPageType.getValue().equals("Please select page type")?"Select a valid Page Type":
                    pageNameList.contains(pageName.getText())?" All page id must be unique"
                            :pageName.getText().length() == 0? "Page id should not be empty":"";


            //creating an information alert to deal--------------
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setContentText(message);
            alert.showAndWait();
            //---------------------------------------------------


            cancelClicked = newPageDialog.showDialog();
            isValid = !pageNameList.contains(pageName.getText()) && pageName.getText().length() > 0 && !dropDownPageType.getValue().equals("Please select page type");
            if(!cancelClicked) return null;

        }
        boolean check = checkBox.isSelected();
        if(check){ firstPageCount++;}
        VignettePage page = new VignettePage(pageName.getText().trim(), check, dropDownPageType.getValue().toString());
        pageNameList.add(pageName.getText());
        dropDownPageType.setDisable(false);
        return page;
    }


    /**
     * This method creates a vignette button on dropped
     * @Params page  creates a vignette page class for each page
     * @Param droppedView Image view
     * @param posX contains the mouse position
     * @param posY contains the mouse position
     * **/
    public Button createVignetteButton(VignettePage page, ImageView droppedView, double posX, double posY,String type){

        Button vignettePageButton = new Button(page.getPageName(), droppedView);
        buttonPageMap.put(page.getPageName(), vignettePageButton);

        vignettePageButton.relocate(posX,posY);

        final double[] delatX = new double[1]; // used when the image is dragged to a different position
        final double[] deltaY = new double[1];
        vignettePageButton.setAlignment(Pos.CENTER); // center the text
        vignettePageButton.setTextAlignment(TextAlignment.CENTER);
        vignettePageButton.setContentDisplay(ContentDisplay.CENTER);
        vignettePageButton.setWrapText(true); // wrap to reduce white space

        //----- start of mouse event methods----------
        vignettePageButton.setOnMousePressed(mouseEvent -> {
            delatX[0] = vignettePageButton.getLayoutX() - mouseEvent.getSceneX();
            deltaY[0] = vignettePageButton.getLayoutY() - mouseEvent.getSceneY();
            vignettePageButton.setCursor(Cursor.MOVE);
        });
        vignettePageButton.setOnMouseReleased(mouseEvent -> {
            vignettePageButton.setCursor(Cursor.HAND);
        });
        vignettePageButton.setOnMouseDragged(mouseEvent -> {

            vignettePageButton.setLayoutX(mouseEvent.getSceneX() + delatX[0]); // set it to mew postion
            vignettePageButton.setLayoutY(mouseEvent.getSceneY() + deltaY[0] );
            page.setPosX(mouseEvent.getSceneX() + delatX[0]);
            page.setPosY(mouseEvent.getSceneY() + deltaY[0]);
            pageViewList.put(page.getPageName(),page);
            Main.getVignette().setPageViewList(pageViewList);

        });
        vignettePageButton.setOnMouseClicked(mouseEvent -> {
            String text = null;
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    controller.openPage(page, type);

                }
            }

            // this is the code the deals with opening the right click menu
            if(mouseEvent.getButton().equals(MouseButton.SECONDARY)) {


                /*
                Creating the right click vignette page menu and adding it to the button representing the
                page.
                 */
                PageMenu pageMenu = new PageMenu(page, vignettePageButton, controller);
                vignettePageButton.setContextMenu(pageMenu);


                pageMenu.setOnAction(e -> {
                    if(((MenuItem)e.getTarget()).getText().equals("Connect")){
                        isConnected = true;
                        one = vignettePageButton;
                        pageOne = page;
                    }
                });
            }
//            else if(isConnected) {
//                connectPages(mouseEvent);
//                isConnected=false;
//            }
        });

        vignettePageButton.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.DELETE))
                controller.deletePage(page,vignettePageButton);
        });

        controller.getRightAnchorPane().getChildren().add(vignettePageButton);
        page.setPosX(posX);
        page.setPosY(posY);
        pageViewList.put(page.getPageName(),page);
        Main.getInstance().addUndoStack(vignettePageButton);

        // -------end of mouse event methods-------
        return vignettePageButton;
    }
}
