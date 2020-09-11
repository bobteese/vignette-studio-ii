package com.MenuBar.Help;

import com.DialogHelper.DialogHelper;
import javafx.scene.control.Alert;


public class HelpMenuItem {

    public void openAlert(String menuName){

         String message = "";

        if(menuName.equals("Tutorial")){
             message = "A tutorial for vignette studio is not available yet.";
             DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "Tutorial", null,message
                                                   ,false);
         }
         else if (menuName.equals("About")) {

              message = "<html><div style=\"width:350px\">Vignette Studio was created by the Vignette Dreamers as an " +
                      "undergraduate senior project at Rochester Institute of Technology. Vignette Studio was created for the " +
                      "<a href=\"http://livephoto.rit.edu/\">LivePhoto Physics</a> project. Dr. Robert Teese and Professor Tom Reichlmayr " +
                      "sponsored the project, and Dr. Scott Hawker coached the team. Contributors include:<br><br><p>The Vignette Dreamers:<br>Peter-John Rowe, " +
                      "Jake Juby, Monir Hossain, Thomas Connors, and Samuel Nelson</p> <br>Additional Developers:<br>Bradley Bensch, " +
                      "Nick Fuschino, Rohit Garg, Peter Gyory, Chad Koppes, Trevor Koppes, Nicholas Krzysiak, Joseph Ksiazek, Jen Lamere, Cailin Li, " +
                      "Robert Liedka, Nicolas McCurdy, Hector Pieiro II, Chirag Chandrakant Salian, Angel Shiwakoti, Nils Sohn, Brian Soulliard, " +
                      "Juntian Tao, Gordon Toth, Devin Warren, Alexander Wilczek, Todd Williams, Brian Wyant, Asmita Hari, Jiwoo Baik and Felix Brink.<br><br>Vignette Studio " +
                      "is &copy; 2014-2018, the LivePhoto Physics Project at Rochester Institute of Technology. Vignette Studio is licensed to you under the terms of the GNU General Public License (GPL). " +
                      "The terms of the license can be found at <a href=\"http://www.gnu.org/licenses/gpl.html\">http://www.gnu.org/licenses/gpl.html</a></div></html>";
              DialogHelper helper = new DialogHelper(Alert.AlertType.CONFIRMATION, "About",null, message,
                                           true);



         }

    }
}
