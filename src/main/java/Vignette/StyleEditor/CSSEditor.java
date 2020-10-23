package Vignette.StyleEditor;

import javafx.scene.paint.Color;

import java.util.HashMap;

public class CSSEditor {

    private String cssSelector = "selector";
    private String cssProperty = "property";
    private String cssNewValue = "new_value";
    public static final String DEFAULT_VALUE = "Default";

    public static final String[] BACKGROUND_COLORS =
            {DEFAULT_VALUE, "Azure", "Black", "LightBlue", "LightGray", "LightPink", "LightSkyBlue", "LightYellow", "White"};
    private static final String BACKGROUND_COLOR_DEFAULT = "rgba(215,213,166, 1.0)";
    private String backgroundColorSelector = "body";
    private String backgroundColorProperty = "background-color";

    public static final String[] FONTS =
            {DEFAULT_VALUE, "Arial", "Courier", "Helvetica", "Times New Roman", "Verdana"};
    private static final String TITLE_FONT_DEFAULT = "Georgia, \"Times New Roman\", serif";
    private String titleFontSelector = "#pagenav, #header";
    private String titleFontProperty = "font-family";

    public static final String[] FONT_SIZES =
            {DEFAULT_VALUE, "X-Small", "Small", "Medium", "Large", "X-Large"};
    private static final String FONT_SIZE_DEFAULT = "0.85em";
    private String fontSizeSelector = "body";
    private String fontSizeProperty = "font-size";

    public static final String[] TEXT_COLORS =
            {DEFAULT_VALUE, "Blue", "Gray", "Green", "Maroon", "Navy", "Orange", "Purple", "Red", "White"};
    private static final String TITLE_TEXT_COLOR_DEFAULT = "Black";
    private String titleTextColorSelector = "#pagenav, #header";
    private String titleTextColorProperty = "color";

    private static final String NAVIGATION_BUTTON_BORDER_VALUE = "5px solid";
    private static final String NAVIGATION_BUTTON_COLOR_DEFAULT = "rgba(200, 180, 0, 0.3)";
    private String navigationButtonColorSelector = "#pagenav .left, #pagenav .right, #pagenav .credits";
    private String navigationButtonColorProperty = "border";

    private static final String FONT_STYLE_NORMAL = "normal";
    private String fontStyleSelector = "body";
    private String fontStyleProperty = "font-style";
    public static final String ITALIC_VALUE = "italic";
    public static final String BOLD_VALUE = "bold";

    private static final String POPUP_BUTTON_COLOR_DEFAULT = "#242628";
    private static final String POPUP_BUTTON_TEXT_COLOR_DEFAULT = "#FFF";
    private String popupButtonSelector = ".jconfirm .jconfirm-box .jconfirm-buttons button.btn-grey";
    private String popupButtonColorProperty = "background-color";
    private String popupButtonTextColorProperty = "color";

    private HashMap<String, Color> colorObjectMap;

    public CSSEditor() {

        //The Color object with corresponding rgb code needs to be added to this map
        //when a new background color option is added
        colorObjectMap = new HashMap<String, Color>();
        colorObjectMap.put("Azure", createnewColor(240,255,255));
        colorObjectMap.put("Black", createnewColor(0,0,0));
        colorObjectMap.put("LightBlue", createnewColor(173,216,230));
        colorObjectMap.put("LightGray", createnewColor(211,211,211));
        colorObjectMap.put("LightPink", createnewColor(255,182,193));
        colorObjectMap.put("LightSkyBlue", createnewColor(135,206,250));
        colorObjectMap.put("LightYellow", createnewColor(255,255,224));
        colorObjectMap.put("White", createnewColor(255,255,255));
    }

    public Color createnewColor(int v1,int v2,int v3){
        Color c = Color.rgb(v1,v2,v3);
        return c;
    }
}
