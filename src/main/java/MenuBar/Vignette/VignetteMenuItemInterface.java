package MenuBar.Vignette;


import Vignette.Settings.VignetteSettings;
import javafx.scene.control.MenuItem;

public interface VignetteMenuItemInterface {

    void editVignette();
    VignetteSettings editVignetteSettings();
    void openStyleEditor();
    void previewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette);
    void stopPreviewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette);
}
