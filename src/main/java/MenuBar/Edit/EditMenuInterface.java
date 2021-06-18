package MenuBar.Edit;

import javafx.scene.control.MenuItem;

public interface EditMenuInterface {

     boolean undo(MenuItem undo,MenuItem redo);
     void redo(MenuItem undo, MenuItem redo);

     //void undo(MenuItem redo);
     //void redo();
}
