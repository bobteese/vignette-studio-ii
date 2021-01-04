package MenuBar.Edit;

import Application.Main;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class EditMenu implements EditMenuInterface{


    @Override
    public void undo() {

        Node node = Main.getInstance().getUndoStack().pop();
        AnchorPane pane =Main.getVignette().getController().getAnchorPane();
        pane.getChildren().remove(node);

    }

    @Override
    public void redo() {
        Node node = Main.getInstance().getRedoStack().pop();
        AnchorPane pane = Main.getVignette().getController().getAnchorPane();
        pane.getChildren().add(node);
    }
}
