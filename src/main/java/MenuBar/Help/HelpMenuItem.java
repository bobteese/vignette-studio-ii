package MenuBar.Help;

import Application.Main;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public class HelpMenuItem implements HelpMenuItemInterface {

    @Override
    public void openAlert(String menuName){
        if(menuName.equals("Tutorial")){
            Main.openTutorialAlertBox();
        }else if (menuName.equals("About")) {
            Main.openAboutAlertBox();
         }
    }

    /**
     * This function lets the user view a pdf of the documentation.
     * @throws IOException
     */
    @Override
    public void openDocumentation() throws IOException {
        //System.out.println("Opening documentation");
        String inputPdf = "pdf/Vignette Studio Documentation.pdf";
        Path tempOutput = Files.createTempFile("Vignette Studio Documentation", ".pdf");
        tempOutput.toFile().deleteOnExit();
        System.out.println("tempOutput: " + tempOutput);
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream(inputPdf)) {
            Files.copy(is, tempOutput, StandardCopyOption.REPLACE_EXISTING);
        }
        Desktop.getDesktop().open(tempOutput.toFile());

        //use this to view online.
        //getHostServices().showDocument("https://docs.google.com/document/d/1loa3WrsEVV23AzRGlEjfxi_o4JnWFRVLcyM_YH1ZjnI/edit");
    }
}
