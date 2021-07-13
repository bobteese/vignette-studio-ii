package TabPane;


import Application.Main;
import MenuBar.Edit.EditMenu;
import Vignette.Page.VignettePage;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import org.reactfx.EventStream;
import org.reactfx.SuspendableNo;
import org.reactfx.util.Tuple2;

import java.util.List;
import java.util.Optional;


/**
 * This class represents the pop up context menu whenever you right click anywhere on the
 * right anchor pane.
 */
public class EditorRightClickMenu extends ContextMenu{

    private CodeArea htmlSourceCode;
    private double posX;
    private double posY;






    MenuItem undo = new MenuItem("Undo");
    MenuItem redo = new MenuItem("Redo");
    MenuItem cut = new MenuItem("Cut");
    MenuItem copy = new MenuItem("Copy");
    MenuItem paste = new MenuItem("Paste");
    MenuItem delete = new MenuItem("Delete");

    public EditorRightClickMenu(CodeArea htmlSourceCode)
    {
        this.htmlSourceCode = htmlSourceCode;


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




        this.getItems().addAll(undo,redo,cut,copy,paste,delete);


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

    public void copy() {
        IndexRange selection = htmlSourceCode.getSelection();
        if(selection.getLength() > 0) {
            ClipboardContent content = new ClipboardContent();

            content.putString(htmlSourceCode.getSelectedText());

            Clipboard.getSystemClipboard().setContent(content);
        }
    }

    public void cut(){
        copy();
        IndexRange selection = htmlSourceCode.getSelection();
        htmlSourceCode.deleteText(selection.getStart(), selection.getEnd());
    }

    public void paste(){
        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            String text = clipboard.getString();
            if (text != null) {
                htmlSourceCode.replaceSelection(text);
            }
        }
    }

    public void delete(){
        IndexRange selection = htmlSourceCode.getSelection();
        htmlSourceCode.deleteText(selection.getStart(), selection.getEnd());
    }



    public void checkButtonStatus() {
        if (htmlSourceCode.getUndoManager().isUndoAvailable())
            undo.setDisable(false);
        else
            undo.setDisable(true);

        if (htmlSourceCode.getUndoManager().isRedoAvailable())
            redo.setDisable(false);
        else
            redo.setDisable(true);

        System.out.println("This the selection= " + htmlSourceCode.getSelection());

        if (htmlSourceCode.getSelection().getEnd() - htmlSourceCode.getSelection().getStart() == 0) {
            System.out.println("Disabled copy, cut and delete");
            copy.setDisable(true);
            cut.setDisable(true);
            delete.setDisable(true);
        } else {
            System.out.println("Enabled copy, cut and delete");
            copy.setDisable(false);
            cut.setDisable(false);
            delete.setDisable(false);
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard.hasString()) {
            String text = clipboard.getString();
            if (text != null) {
                System.out.println("Paste text = " + text);
                System.out.println("Enabling Paste");
                paste.setDisable(false);
            } else {
                System.out.println("Disabling paste");
                paste.setDisable(true);
            }
        }
    }
}
