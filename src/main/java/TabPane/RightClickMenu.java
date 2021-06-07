package TabPane;

import Vignette.Page.VignettePage;
import Vignette.Vignette;
import javafx.scene.control.*;


/**
 * This class represents the pop up context menu whenever you right click anywhere on the
 * right anchor pane.
 */
public class RightClickMenu extends ContextMenu{

    private TabPaneController controller;
    private double posX;
    private double posY;

    MenuItem newpage = new MenuItem("New Page");
    MenuItem undo = new MenuItem("Undo");
    MenuItem redo = new MenuItem("Redo");


    public RightClickMenu( TabPaneController controller)
    {
        this.controller = controller;

        //newpage menu Item.
        newpage.setOnAction(e->{
            createPage();
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


    public VignettePage createPage()
    {
        VignettePage page = controller.createNewPageDialog(false,null);
        controller.createPageFromRightClick(page,this.posX,this.posY);


        return page;
    }

}
