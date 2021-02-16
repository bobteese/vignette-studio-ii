package Utility;

import ConstantVariables.ConstantVariables;
import Vignette.Page.VignettePage;

public class Utility implements UtilityInterface {
    @Override
    public String replaceNextPage(String text, VignettePage page) {

        String nextPageName = ".*NextPageName.*";
        text = text.replaceFirst(nextPageName,"NextPageName=\""+page.getConnectedTo() +"\";");
            return  text;
    }

    @Override
    public String checkPageType(String text) {
        switch (text){
            case "Multiple-Choice (Radio button)":
                  return ConstantVariables.QUESTIONTYPE_BRACNHRADIO;
            case "Multiple-Select (Checkbox)":
                return ConstantVariables.QUESTIONTYPE_BRACNHCHECKBOX;
            case "No Question":
                return "none";
        }
        return null;
    }
}
