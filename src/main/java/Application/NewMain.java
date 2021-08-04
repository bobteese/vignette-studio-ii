package Application;

public class NewMain {
    public static void main(String[] args) {
        Main.main(args);
    }
}
/**
 * The given jar is now working well with open existing and new Vignettes, and fulfilling the right click functionalities.
 * The major big fixes for connecting the pages and navigating through the pages is fixed now, but the bug left is that it navigates to 'default' page always and this needs to be fixed with the JS team.
 * The VignettePages now displays the image added to it and the videos as well.
 * The studio is tested to add question with input fields as RadioButtons and CheckBoxes, the testing to add images question field throws an Exception that is yet to be fixed.
 * A sample for what we tested with the jar is also added to the message, and you can try opening the .vgn file and test it, and inform the java team whether it works on your or not.
 * (opening the Vignette would throw an Exception that is because of ConnectPages, this is added to Trello and halfway is done as well!)
 * Rest bugs and fixes are being added to Trello and still being added...
 */

//--module-path /path/to/javafx/sdk --add-modules javafx.controls,javafx.fxml
