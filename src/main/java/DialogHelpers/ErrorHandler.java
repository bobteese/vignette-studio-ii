package DialogHelpers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ErrorHandler extends Alert {

    public ErrorHandler(AlertType alertType, String title, String headerText, String contentText, ButtonType... buttonTypes) {
        super(alertType, title, buttonTypes);
        this.setTitle(title);
        this.setContentText(contentText);
        this.setHeaderText(headerText);

    }
}
