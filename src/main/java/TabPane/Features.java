package TabPane;

import GridPaneHelper.GridPaneHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;

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




}
