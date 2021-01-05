package MenuBar.Edit;

import Application.Main;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

import java.util.Stack;

public class EditMenu implements EditMenuInterface{


    @Override
    public void undo(MenuItem redo) {


        Stack<Node> undo = Main.getInstance().getUndoStack();
        if (undo.size()!=0) {
            Node node = undo.pop();
            Main.getInstance().addRedoStack(node);
            AnchorPane pane = Main.getVignette().getController().getAnchorPane();
            pane.getChildren().remove(node);
        }
        if(undo.size()==0){
            redo.setDisable(true);
        }

    }

    @Override
    public void redo() {
        Stack<Node> redo = Main.getInstance().getRedoStack();
        if (redo.size()!=0) {
            Node node = redo.pop();
            Main.getInstance().addUndoStack(node);
            AnchorPane pane = Main.getVignette().getController().getAnchorPane();
            pane.getChildren().add(node);
        }
    }
}
