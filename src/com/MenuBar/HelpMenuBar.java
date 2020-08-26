package com.MenuBar;

import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class HelpMenuBar {

    public Menu getHelpMenuBar() {

        Menu helpMenu = new Menu("Help");

        MenuItem tutorial = new MenuItem("Tutorial");
        MenuItem about = new MenuItem("About");


        helpMenu.getItems().add(tutorial);
        helpMenu.getItems().add(about);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);




        tutorial.setOnAction( e -> {
            alert.setTitle("Tutorial");
            alert.setHeaderText(null);
            alert.setContentText("A tutorial for vignette studio is not available yet.");
            alert.showAndWait();
        });

        about.setOnAction(e->{
            alert.setTitle("About Vignette");
            alert.setHeaderText(null);

            String text = "<html><div style=\"width:350px\">Vignette Studio was created by the Vignette Dreamers as an " +
                    "undergraduate senior project at Rochester Institute of Technology. Vignette Studio was created for the " +
                    "<a href=\"http://livephoto.rit.edu/\">LivePhoto Physics</a> project. Dr. Robert Teese and Professor Tom Reichlmayr " +
                    "sponsored the project, and Dr. Scott Hawker coached the team. Contributors include:<br><br><p>The Vignette Dreamers:<br>Peter-John Rowe, " +
                    "Jake Juby, Monir Hossain, Thomas Connors, and Samuel Nelson</p> <br>Additional Developers:<br>Bradley Bensch, " +
                    "Nick Fuschino, Rohit Garg, Peter Gyory, Chad Koppes, Trevor Koppes, Nicholas Krzysiak, Joseph Ksiazek, Jen Lamere, Cailin Li, " +
                    "Robert Liedka, Nicolas McCurdy, Hector Pieiro II, Chirag Chandrakant Salian, Angel Shiwakoti, Nils Sohn, Brian Soulliard, " +
                    "Juntian Tao, Gordon Toth, Devin Warren, Alexander Wilczek, Todd Williams, Brian Wyant, Asmita Hari, Jiwoo Baik and Felix Brink.<br><br>Vignette Studio " +
                    "is &copy; 2014-2018, the LivePhoto Physics Project at Rochester Institute of Technology. Vignette Studio is licensed to you under the terms of the GNU General Public License (GPL). " +
                    "The terms of the license can be found at <a href=\"http://www.gnu.org/licenses/gpl.html\">http://www.gnu.org/licenses/gpl.html</a></div></html>";

            TextArea textArea = new TextArea(text);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
            // alert.setContentText(text);
            alert.showAndWait();
        });


        return  helpMenu;

    }
}
