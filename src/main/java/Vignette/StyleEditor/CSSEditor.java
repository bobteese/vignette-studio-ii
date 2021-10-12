package Vignette.StyleEditor;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class CSSEditor {

    /*private String cssSelector = "selector";
    private String cssProperty = "property";
    private String cssNewValue = "new_value"*/;
    public static final String DEFAULT_VALUE = "Default";
    public static final String LIGHT_SKY_BLUE = "LightSkyBlue";
    public static final String LIGHT_GRAY = "LightGray";
    public static final String LIGHT_PINK = "LightPink";
    public static final String LIGHT_BLUE = "LightBlue";
    public static final String AZURE = "Azure";
    public static final String BLACK = "Black";
    public static final String LIGHT_YELLOW = "LightYellow";
    public static final String WHITE = "White";

    public static final String[] BACKGROUND_COLORS =
            {DEFAULT_VALUE, AZURE, BLACK, LIGHT_BLUE, LIGHT_GRAY, LIGHT_PINK, LIGHT_SKY_BLUE, LIGHT_YELLOW, WHITE};

    public static final String[] BACKGROUND_COLORS2 =
            { DEFAULT_VALUE, AZURE, BLACK, LIGHT_BLUE, LIGHT_GRAY, LIGHT_PINK, LIGHT_SKY_BLUE, LIGHT_YELLOW, WHITE};



    public static final Map<String, String> BACKGROUND_COLORS_HEX = new HashMap<>();

//    private static final String BACKGROUND_COLOR_DEFAULT = "rgba(215,213,166, 1.0)";
//    private String backgroundColorSelector = "body";
//    private String backgroundColorProperty = "background-color";

    public static final String[] FONTS =
            {"Arial", "Arial, sans-serif", "Helvetica, sans-serif", "Avantgarde, TeX Gyre Adventor, URW Gothic L, sans-serif",
                    "Optima, sans-serif", "Arial Narrow, sans-serif", "sans-serif", "Gill Sans, sans-serif", "Trebuchet MS, sans-serif",
            "Noto Sans, sans-serif", "Times, Times New Roman, serif", "Didot, serif", "Georgia, serif", "Palatino, URW Palladio L, serif",
            "Courier New, monospace", "DejaVu Sans Mono, monospace", "Comic Sans MS, Comic Sans, cursive", "Brush Script MT, Brush Script Std, cursive",
            "Jazz LET, fantasy", "Blippo, fantasy", "Stencil Std, fantasy"};

//    private static final String TITLE_FONT_DEFAULT = "Georgia, \"Times New Roman\", serif";
//    private String titleFontSelector = "#pagenav, #header";
//    private String titleFontProperty = "font-family";

//    public static final String[] FONT_SIZES =
//            {DEFAULT_VALUE, "X-Small", "Small", "Medium", "Large", "X-Large"};
//    private static final String FONT_SIZE_DEFAULT = "0.85em";
//    private String fontSizeSelector = "body";
//    private String fontSizeProperty = "font-size";

    public static final String[] TEXT_COLORS =
            {DEFAULT_VALUE, "Blue", "Gray", "Green", "Maroon", "Navy", "Orange", "Purple", "Red", "White"};

    public static final Map<String,String> TEXT_COLORS_HEX = new HashMap<>();
//    private static final String TITLE_TEXT_COLOR_DEFAULT = "Black";
//    private String titleTextColorSelector = "#pagenav, #header";
//    private String titleTextColorProperty = "color";

//    private static final String NAVIGATION_BUTTON_BORDER_VALUE = "5px solid";
//    private static final String NAVIGATION_BUTTON_COLOR_DEFAULT = "rgba(200, 180, 0, 0.3)";
//    private String navigationButtonColorSelector = "#pagenav .left, #pagenav .right, #pagenav .credits";
//    private String navigationButtonColorProperty = "border";

//    private static final String FONT_STYLE_NORMAL = "normal";
//    private String fontStyleSelector = "body";
//    private String fontStyleProperty = "font-style";
//    public static final String ITALIC_VALUE = "italic";
//    public static final String BOLD_VALUE = "bold";

//    private static final String POPUP_BUTTON_COLOR_DEFAULT = "#242628";
//    private static final String POPUP_BUTTON_TEXT_COLOR_DEFAULT = "#FFF";
//    private String popupButtonSelector = ".jconfirm .jconfirm-box .jconfirm-buttons button.btn-grey";
//    private String popupButtonColorProperty = "background-color";
//    private String popupButtonTextColorProperty = "color";

    private Map<String, Color> colorObjectMap;
    private Map<String, int[]> rgbColorMap;


    public CSSEditor() {

        //The Color object with corresponding rgb code needs to be added to this map
        //when a new background color option is added

        colorObjectMap = new HashMap<String, Color>();
        colorObjectMap.put("Azure", createnewColor(240,255,255));
        colorObjectMap.put("Black", createnewColor(0,0,0));
        colorObjectMap.put("LightBlue", createnewColor(173,216,230));
        colorObjectMap.put(LIGHT_GRAY, createnewColor(211,211,211));
        colorObjectMap.put(LIGHT_PINK, createnewColor(255,182,193));
        colorObjectMap.put(LIGHT_SKY_BLUE, createnewColor(135,206,250));
        colorObjectMap.put("LightYellow", createnewColor(255,255,224));
        colorObjectMap.put("White", createnewColor(255,255,255));


        //storing rgb values in a hashmap, color name is the key and array of rgb values is the value
        rgbColorMap = new HashMap<String, int[]>();
        rgbColorMap.put(DEFAULT_VALUE,new int[]{249,215,76});
        rgbColorMap.put("Azure", new int[]{240, 255, 255});
        rgbColorMap.put("Black", new int[]{0,0,0});
        rgbColorMap.put("LightBlue", new int[]{173,216,230});
        rgbColorMap.put(LIGHT_GRAY, new int[]{211,211,211});
        rgbColorMap.put(LIGHT_PINK, new int[]{255,182,193});
        rgbColorMap.put(LIGHT_SKY_BLUE, new int[]{135,206,250});
        rgbColorMap.put("LightYellow", new int[]{255,255,224});
        rgbColorMap.put("White", new int[]{255,255,255});

        BACKGROUND_COLORS_HEX.put("Azure", "#F0FFFF");
        BACKGROUND_COLORS_HEX.put("Black", "#000000");
        BACKGROUND_COLORS_HEX.put("LightBlue", "#ADD8E6");
        BACKGROUND_COLORS_HEX.put(LIGHT_GRAY, "#D3D3D3");
        BACKGROUND_COLORS_HEX.put(LIGHT_PINK, "#FFB6C1");
        BACKGROUND_COLORS_HEX.put(LIGHT_SKY_BLUE, "#87CEFA");
        BACKGROUND_COLORS_HEX.put("LightYellow", "#FFFFE0");
        BACKGROUND_COLORS_HEX.put("White", "#FFFFFF");
        BACKGROUND_COLORS_HEX.put("Default", "#eee");

        TEXT_COLORS_HEX.put("Default","#212529");
        TEXT_COLORS_HEX.put("Blue","#0000FF");
        TEXT_COLORS_HEX.put("Gray","#808080");
        TEXT_COLORS_HEX.put("Green","#008000");
        TEXT_COLORS_HEX.put("Maroon","#800000");
        TEXT_COLORS_HEX.put("Navy","#000080");
        TEXT_COLORS_HEX.put("Orange","#FFA500");
        TEXT_COLORS_HEX.put("Purple","#800080");
        TEXT_COLORS_HEX.put("Red","#FF0000");
        TEXT_COLORS_HEX.put("White","#FFFFFF");

    }

    public Color createnewColor(int v1,int v2,int v3){
        return Color.rgb(v1,v2,v3);
    }


    public String colorToRGB(int[] values)
    {
        return "rgb("+values[0]+", "+values[1]+", "+values[2]+")";
    }

    public String colorToRGBwithOpacity(int[] values)
    {
        return "rgba("+values[0]+", "+values[1]+", "+values[2]+",0.5)";
    }



    public Map<String,int[]> getrgbColorMap()
    {
        return this.rgbColorMap;
    }

}
