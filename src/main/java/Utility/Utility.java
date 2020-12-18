package Utility;

import Vignette.Page.VignettePage;

public class Utility implements UtilityInterface {
    @Override
    public String replaceNextPage(String text, VignettePage page) {

        String nextPageName = ".*NextPageName.*";
        text = text.replaceFirst(nextPageName,"NextPageName=\""+page.getConnectedTo() +"\";");
            return  text;
    }
}
