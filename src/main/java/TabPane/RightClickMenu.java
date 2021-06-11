package TabPane;


import Application.Main;
import TabPane.PageCreator;
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
    private PageCreator creator;


    private double posX;
    private double posY;

    EditMenu editContent = new EditMenu();


    MenuItem newpage = new MenuItem("New Page");
    MenuItem undo = new MenuItem("Undo");
    MenuItem redo = new MenuItem("Redo");

    private Stack<Node> undoStack;
    private Stack<Node> redoStack;


    public RightClickMenu(PageCreator creator)
    {
        //this.controller = controller;

        this.creator=creator;


        undoStack = Main.getInstance().getUndoStack();
        redoStack = Main.getInstance().getRedoStack();

        //newpage menu Item.
        newpage.setOnAction(e->{
            createPage();
            e.consume();
        });


        undo.setOnAction(e-> {

            //Stack<Node> undo = Main.getInstance().getUndoStack();


            if (undoStack.size()!=0) {
                Node node = undoStack.pop();
                Main.getInstance().addRedoStack(node);
                AnchorPane pane = Main.getVignette().getController().getAnchorPane();
                pane.getChildren().remove(node);
            }
            if(undoStack.size()==0){
                redo.setDisable(true);
            }

        });

        redo.setOnAction(e->{
            //Stack<Node> redo = Main.getInstance().getRedoStack();
            if (redoStack.size()!=0) {
                Node node = redoStack.pop();
                Main.getInstance().addUndoStack(node);
                AnchorPane pane = Main.getVignette().getController().getAnchorPane();
                pane.getChildren().add(node);
            }



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
        VignettePage page = creator.createNewPageDialog(false,null);
        creator.createPageFromRightClick(page,this.posX,this.posY);
        return page;
    }


    /**
     * This function either enables or disables the undo/redo functionality in the right click context menu
     * Called in TabPaneController.initialize() everytime a right click is made before showing the context menu.
     */
    public void setUndoRedoDisability()
    {
        if(undoStack.size()==0)
            undo.setDisable(true);
        else
            undo.setDisable(false);

        if(redoStack.size()==0)
            redo.setDisable(true);
        else
            redo.setDisable(false);

    }

    public Stack<Node> getUndoStack()
    {
        return undoStack;
    }


}
