package MenuBar.Vignette;


import javafx.scene.control.MenuItem;

public interface VignetteMenuItemInterface {

    void editVignette();
    void editVignetteSettings();
    void openStyleEditor();
    void previewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette);
    void stopPreviewVignette(MenuItem stopPreviewMenu, MenuItem previewVignette);
}
