package MenuBar.Edit;

import Application.Main;
import Vignette.HTMLEditor.HTMLEditorContent;
import Vignette.Page.VignettePage;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.util.Stack;

public class EditMenu implements EditMenuInterface{


    /**
     * This function deals with the UNDO functionality of the main screen of the vignette studio.
     * Used in MenuBarController.
     * Undo functionality works for both the undo option on the edit menu and the CTRL + Z keyboard shortcut.
     * todo find out how the keyboard shortcut is linked to this function
     * @param redo
     */
    //@Override
    public boolean undo(MenuItem undo, MenuItem redo) {
        /**
        VignettePage currentPage = Main.getVignette().getCurrentPage();

        HTMLEditorContent content = Main.getVignette().getHTMLEditorContent(currentPage);
        TextArea textArea = content.getHtmlSourceCode();

        if(textArea.isUndoable()) {
            undo.setDisable(false);
            textArea.undo();
            redo.setDisable(false);
        }
        else
        {
            undo.setDisable(true);
            return false;
        }
        return true;
         */
        return false;
    }

/**
 *
 * Asmitas undo/redo
 *
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
 */

    /**
     * This function deals with the REDO functionality of the main screen of the vignette studio.
     * Also used in MenuBarController.
     * Redo functionality works for both the redo option on the edit menu and the CTRL + SHIFT + Z keyboard shortcut.
     * todo find out how the keyboard shortcut is linked to this function
     */
    @Override
    public void redo(MenuItem undo, MenuItem redo) {
        /**
        VignettePage currentPage = Main.getVignette().getCurrentPage();

        HTMLEditorContent content = Main.getVignette().getHTMLEditorContent(currentPage);
        TextArea textArea = content.getHtmlSourceCode();

        if(textArea.isRedoable()) {
            undo.setDisable(false);
            textArea.redo();
        }
        else
        {
            redo.setDisable(true);
        }

    }

        /**
        Stack<Node> redo = Main.getInstance().getRedoStack();
        if (redo.size()!=0) {
            Node node = redo.pop();
            Main.getInstance().addUndoStack(node);
            AnchorPane pane = Main.getVignette().getController().getAnchorPane();
            pane.getChildren().add(node);
        }
    }
         */

    }
}
