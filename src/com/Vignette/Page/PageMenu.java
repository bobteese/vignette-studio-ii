package com.Vignette.Page;

import com.GridPaneHelper.GridPaneHelper;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.security.PublicKey;

public class PageMenu extends ContextMenu {

    VignettePage page;
    MenuItem open = new MenuItem("Open");
    MenuItem edit = new MenuItem("Edit");
    MenuItem connect = new MenuItem("Connect");
    MenuItem connectCredits = new MenuItem("Connect Credits");
    MenuItem disconnect = new MenuItem("Disconnect");
    MenuItem delete = new MenuItem("Delete");

    public PageMenu(VignettePage page, Button vignettePageButton){
        this.page = page;
        // add menu items to menu

        delete.setOnAction(event -> {
            KeyEvent keyEvent = new KeyEvent(vignettePageButton, vignettePageButton,
                                             KeyEvent.KEY_PRESSED, "", "", KeyCode.DELETE,
                                     false, false, false, false);
           vignettePageButton.fireEvent(keyEvent);
        });
        edit.setOnAction(event -> {
            GridPaneHelper newPageDialog = new GridPaneHelper();
            newPageDialog.addCheckBox("First Page", 1,1, true);
            TextField pageName = newPageDialog.addTextField(1,2, 400);
            newPageDialog.createGrid("Create New page", "Please enter the page name","Ok","Cancel");
            this.page.setPageName(pageName.getText());
            vignettePageButton.setText(pageName.getText());
        });

        this.getItems().add(open);
        this.getItems().add(edit);
        this.getItems().add(connect);
        this.getItems().add(connectCredits);
        this.getItems().add(disconnect);
        this.getItems().add(delete);

    }








}
