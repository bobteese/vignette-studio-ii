package TabPane;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.fxmisc.richtext.*;


/**
 * This class represents the pop up context menu whenever you right click anywhere on the
 * right anchor pane.
 */
public class EditorRightClickMenu extends ContextMenu{

    private CodeArea htmlSourceCode;
    private TabPaneController controller;

    private double posX;
    private double posY;

    MenuItem undo = new MenuItem("Undo");
    MenuItem redo = new MenuItem("Redo");
    MenuItem cut = new MenuItem("Cut");
    MenuItem copy = new MenuItem("Copy");
    MenuItem paste = new MenuItem("Paste");
    MenuItem delete = new MenuItem("Delete");
    MenuItem showScript = new MenuItem("Show Script");
    MenuItem hideScript = new MenuItem("Hide Script");

    public EditorRightClickMenu(TabPaneController controller,CodeArea htmlSourceCode)
    {
        this.htmlSourceCode = htmlSourceCode;

        this.controller = controller;


        //undo menu Item.
        undo.setOnAction(e->{
            htmlSourceCode.getUndoManager().undo();
            e.consume();
        });

        //redo menu Item
        redo.setOnAction(event -> {
            htmlSourceCode.getUndoManager().redo();
            event.consume();
        });

        cut.setOnAction(event -> {
            cut();
        });

        copy.setOnAction(event -> {
            copy();
        });

        paste.setOnAction(event -> {
            paste();
        });

        delete.setOnAction(event -> {
            delete();
        });

        showScript.setOnAction(event -> {
            this.controller.showScript();
        });

        hideScript.setOnAction(event -> {
            this.controller.hideScript();
        });




        this.getItems().addAll(undo,redo,cut,copy,paste,delete,hideScript,showScript);


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
     * The copy functionality for the textArea
     */
    public void copy() {
        IndexRange selection = htmlSourceCode.getSelection();
        if(selection.getLength() > 0) {
            ClipboardContent content = new ClipboardContent();
            content.putString(htmlSourceCode.getSelectedText());
            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    /**
     * The cut functionality of the textArea
     */
    public void cut(){
        copy();
        IndexRange selection = htmlSourceCode.getSelection();
        htmlSourceCode.deleteText(selection.getStart(), selection.getEnd());
    }

    /**
     * The paste functionality of the textArea
     */
    public void paste(){
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            String text = clipboard.getString();
            if (text != null) {
                htmlSourceCode.replaceSelection(text);
            }
        }
    }


    /**
     * The Delete functionality of the textArea
     */
    public void delete(){
        IndexRange selection = htmlSourceCode.getSelection();
        htmlSourceCode.deleteText(selection.getStart(), selection.getEnd());
    }


    /**
     * This function is called to check which buttons need to be disabled. Called each time the user right clicks.
     */
    public void checkButtonStatus() {
        if (htmlSourceCode.getUndoManager().isUndoAvailable())
            undo.setDisable(false);
        else
            undo.setDisable(true);

        if (htmlSourceCode.getUndoManager().isRedoAvailable())
            redo.setDisable(false);
        else
            redo.setDisable(true);

        if (htmlSourceCode.getSelection().getEnd() - htmlSourceCode.getSelection().getStart() == 0) {
            copy.setDisable(true);
            cut.setDisable(true);
            delete.setDisable(true);
        } else {
            copy.setDisable(false);
            cut.setDisable(false);
            delete.setDisable(false);
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            String text = clipboard.getString();
            if (text != null) {
                paste.setDisable(false);
            } else {
                paste.setDisable(true);
            }
        }

        if(this.controller.getScriptIsHidden()) {
            hideScript.setDisable(true);
            showScript.setDisable(false);
        }
        else {
            hideScript.setDisable(false);
            showScript.setDisable(true);
        }
    }
}
