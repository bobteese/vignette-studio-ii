package MenuBar.Help;

import java.io.IOException;

public interface HelpMenuItemInterface {

    void openAlert(String menuName);

    void openDocumentation() throws IOException;
}
