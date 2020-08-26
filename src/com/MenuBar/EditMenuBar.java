package com.MenuBar;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class EditMenuBar {

    public Menu getEditMenu(){
        Menu editMenu = new Menu("Edit");

        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem delete = new MenuItem("Delete");

        editMenu.getItems().add(undo);
        editMenu.getItems().add(redo);
        editMenu.getItems().add(cut);
        editMenu.getItems().add(copy);
        editMenu.getItems().add(paste);
        editMenu.getItems().add(delete);

        return  editMenu;
    }
}
