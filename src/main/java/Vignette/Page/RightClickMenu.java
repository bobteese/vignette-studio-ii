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
import javafx.scene.layout.AnchorPane;

public class RightClickMenu extends ContextMenu{

    private AnchorPane pane;
    private TabPaneController controller;

    MenuItem newpage = new MenuItem("New Page");

    //todo only activate this if something is copied.
    MenuItem paste = new MenuItem("Paste");

    MenuItem undo = new MenuItem("Undo");
    MenuItem redo = new MenuItem("Redo");


    public RightClickMenu( TabPaneController controller)
    {
        this.controller = controller;

        //newpage menu Item.
        newpage.setOnAction(e->{
            controller.createNewPageDialog(false,null);
            e.consume();
        });

        this.getItems().addAll(newpage,paste,undo,redo);

    }

}
