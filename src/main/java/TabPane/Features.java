package TabPane;

import Application.Main;
import GridPaneHelper.GridPaneHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Features {


    private TabPaneController controller;

    public Features(TabPaneController controller)
    {
        this.controller=controller;
    }


    public void changeFormat(Slider slider, TextArea htmlSourceCode)
    {

        // Styling
        String buttonStyle= "-fx-text-align: center;"+ "-fx-background-color: transparent;" +
                "-fx-border-radius: 7;" + "-fx-border-width: 3 3 3 3;" + "-fx-dark-text-color: black;"
                + "-fx-opacity:1;" + "-fx-font-size: " ;
        String defaultSize = "12px;";
        //

        GridPaneHelper helper12 = new GridPaneHelper();


        helper12.setPrefSize(550,200);
        Button button = new Button("Aa Bb Cc 123");
        button.setPrefSize(400,200);

        //slider.setMajorTickUnit(11);
        //String style = htmlSourceCode.getStyle();

        String style = htmlSourceCode.getStyle();

        //currently the only style that is set to htmlSourceCode is font, once
        if(style!="") {
            String target = "(?<=:)(.*?)(?=px)";
            Pattern p = Pattern.compile(target);
            Matcher m = p.matcher(style);
            if (m.find()) {
                String match = m.group(0);
                double size = Double.parseDouble(match);
                slider.setValue(size);
                button.setStyle(buttonStyle+size+"px;");
            }
        }
        else {
            slider.setValue(11);
            button.setStyle(buttonStyle + defaultSize);
        }


        helper12.add(button,0,0,1,1);
        helper12.add(slider,0,4,3,1);


        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                button.setStyle(buttonStyle+ newValue+ "px;");
            }
        });


        boolean clickedOk = helper12.createGridWithoutScrollPane("change font","","OK","Cancel");
        //confirming font change
        if(clickedOk)
            htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }


    public void increaseFont(Slider slider, TextArea htmlSourceCode)
    {
        slider.increment();
        System.out.println("Font increased to "+slider.getValue());
        htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }

    /**
     * Function called when using the keyboars shortcut ctrl, - to decrease font size
     */
    public void decreaseFont(Slider slider, TextArea htmlSourceCode)
    {
        slider.decrement();
        htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }





    public void findAndSelectString(TextArea htmlSourceCode)
    {

        String lookingFor;


        GridPaneHelper searcher = new GridPaneHelper();
        Button button = new Button();
        Label label = new Label("Search for: ");
        TextField textField = new TextField();

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(label,textField,button);


        searcher.add(label,0,0,1,1);
        searcher.add(textField,1,0,1,1);


        Button next = new Button("next");
        next.setDisable(true);
        searcher.add(next,2,0,1,1);

        Button prev = new Button("prev");
        prev.setDisable(true);
        searcher.add(prev,3,0,1,1);

        //lookingFor=textField.getText();
        //final HashMap[] results = new HashMap[1];

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            HashMap<Integer,int[]> results = search(newValue,htmlSourceCode);

            int[] match = results.get(0);
            int posx = match[0]; int posy = match[1];

            htmlSourceCode.selectRange(posx,posy);

            int i = 1;
            next.setDisable(false);
            next.setOnAction(event -> {
                int[] nextMatch = results.get(i);
                int a = nextMatch[0]; int b = nextMatch[1];
                htmlSourceCode.selectRange(a,b);
            });
        });

        searcher.create("","");




        }


    /**
     * Actual search functionality
     * @param lookingFor
     * @param htmlSourceCode
     * @return
     */
    public HashMap<Integer,int[]> search(String lookingFor, TextArea htmlSourceCode) {

            Pattern pattern = Pattern.compile("" + lookingFor + "([\\S\\s]*?)");
            HashMap<Integer, int[]> searchPos = new HashMap<>();
            Matcher matcher = pattern.matcher(htmlSourceCode.getText()); //Where input is a TextInput class
            int i = 0;
            while (matcher.find()) {
                System.out.println(matcher.start() + "  " + matcher.end());
                int[] pos = {matcher.start(), matcher.end()};
                searchPos.put(i, pos);
                i++;
            }

            return searchPos;
        }



}
