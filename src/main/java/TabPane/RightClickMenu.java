package TabPane;

import Application.Main;
import MenuBar.Edit.EditMenu;
import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.scene.control.*;
import MenuBar.MenuBarController;


/**
 * This class represents the pop up context menu whenever you right click anywhere on the
 * right anchor pane.
 */
public class RightClickMenu extends ContextMenu{

    private TabPaneController controller;
    private double posX;
    private double posY;

    MenuItem newpage = new MenuItem("New Page");

    public RightClickMenu( TabPaneController controller)
    {
        this.controller = controller;
        MenuBarController menu = new MenuBarController();
        EditMenu edit = new EditMenu();


        //newpage menu Item.
        newpage.setOnAction(e->{
            createPage();
            e.consume();
        });

        this.getItems().addAll(newpage);

    }

    /**
     * Set the X,Y coordinates for each right click menu object
     * @param posX
     * @param posY
     */
    public void setXY(double posX, double posY)
    {
        this.posX=posX;
        this.posY=posY;
    }


    /**
     * This function calls the createPageFromRightClick() function in TabPaneController to
     * create a vignette page.
     * @return
     */
    public VignettePage createPage()
    {
        VignettePage page = controller.createNewPageDialog(false,null);
        controller.createPageFromRightClick(page,this.posX,this.posY);
        return page;
    }




}
