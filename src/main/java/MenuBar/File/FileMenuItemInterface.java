package MenuBar.File;

import RecentFiles.RecentFiles;

import java.io.File;

public interface FileMenuItemInterface {

     void createNewVignette();
     void openVignette(File file, RecentFiles recentFiles, boolean fileChooser);
     void saveAsVignette(RecentFiles recentFiles);
     void saveVignette();
     void setPreferences();
     void exitApplication();


}
