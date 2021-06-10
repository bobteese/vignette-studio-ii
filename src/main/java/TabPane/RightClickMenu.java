package TabPane;


import Application.Main;
import MenuBar.Edit.EditMenu;
import Vignette.Page.VignettePage;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.Stack;


/**
 * This class represents the pop up context menu whenever you right click anywhere on the
 * right anchor pane.
 */
public class RightClickMenu extends ContextMenu{

    private TabPaneController controller;
    private double posX;
    private double posY;

    EditMenu editContent = new EditMenu();


    MenuItem newpage = new MenuItem("New Page");
    MenuItem undo = new MenuItem("Undo");
    MenuItem redo = new MenuItem("Redo");


    public RightClickMenu( TabPaneController controller)
    {
        this.controller = controller;


        Main.getInstance().getUndoStack();

        //newpage menu Item.
        newpage.setOnAction(e->{
            createPage();
            e.consume();
        });

        /**
         * this successfully disables everything   
        if(Main.getInstance().getUndoStack().size()==0)
            undo.setDisable(true);
        else
            undo.setDisable(false);

        if(Main.getInstance().getRedoStack().size()==0)
            redo.setDisable(true);
        */

        undo.setOnAction(e-> {
                    this.editContent.undo(redo);
                    redo.setDisable(false);
                });

        redo.setOnAction(e->{
            this.editContent.redo();
            e.consume();
        });

        this.getItems().addAll(newpage,undo,redo);

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
