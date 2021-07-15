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
import org.fxmisc.richtext.*;
import org.w3c.dom.Text;

import javax.swing.text.Style;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Features {


    private TabPaneController controller;
    private EditorRightClickMenu editorRightClickMenu;

    private boolean isScriptHidden;




    public Features(TabPaneController controller)
    {
        this.controller=controller;
        this.editorRightClickMenu=controller.getEditorRightClickMenu();
    }

    public void setScriptHidden(boolean hide){
        this.isScriptHidden = hide;
    }

    public boolean scriptHidden(){
        return this.isScriptHidden;
    }


    public void changeFormat(Slider slider, CodeArea htmlSourceCode)
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


    public void increaseFont(Slider slider, CodeArea htmlSourceCode)
    {
        slider.increment();
        System.out.println("Font increased to "+slider.getValue());
        htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }

    /**
     * Function called when using the keyboars shortcut ctrl, - to decrease font size
     */
    public void decreaseFont(Slider slider, CodeArea htmlSourceCode)
    {
        slider.decrement();
        htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }





    public void findAndSelectString(CodeArea htmlSourceCode)
    {

        String target = "<!--Do Not Change content in this block-->"; //([\\S\\s]*?)<!-- //////// Do Not Change content in this block //////// -->";
        String htmlText = htmlSourceCode.getText();

        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(htmlText);

        if(m.find()) {
            htmlSourceCode.unfoldText(m.start());
            this.controller.setScriptIsHidden(false);
        }



        GridPaneHelper searcher = new GridPaneHelper();
        Label label = new Label("Search for: ");
        TextField textField = new TextField();
        searcher.add(label,0,0,1,1);
        searcher.add(textField,1,0,1,1);
        Label label2 = new Label("\t\t\t");
        searcher.add(label2,4,0,2,1);

        Button prev = new Button("prev");
        prev.setDisable(true);
        searcher.add(prev,2,0,1,1);

        Button next = new Button("next");
        next.setDisable(true);
        searcher.add(next,3,0,1,1);

        Label helpLabel = new Label("Scroll to see search results not on screen");
        searcher.add(helpLabel,1,1,1,1);
        searcher.setResizable(false);



        //adding a listener to the textfield in order to search each time the user enters something new
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            controller.defaultStyle();

            HashMap<Integer,int[]> results;
            if(!newValue.equals(""))
                results = search(newValue,htmlSourceCode);
            else {
                results = null;
                label2.setText("  0 results");
            }



            if(results!= null && results.size()!=0) {

                this.controller.defaultStyle();

                label2.setText("  " + results.size() + " results");
                //This highlights all the search results
                for (int a = 0; a < results.size(); a++) {
                    int[] match = results.get(a);
                    int posx = match[0];
                    int posy = match[1];
                    //htmlSourceCode.selectRange(posx, posy);
                    htmlSourceCode.setStyleClass(posx, posy, "search");
                }



                // value to step through searches
                AtomicInteger i = new AtomicInteger();
                i.set(0);

                //initial search result
                int[] match = results.get(i.get());
                int posx = match[0]; int posy = match[1];

                htmlSourceCode.selectRange(posx, posy);

               // htmlSourceCode.requestFollowCaret();




                //if there arent any search results, disable the next button
                if (results.size() > 0)
                    next.setDisable(false);


                //next button step through functionality
                next.setOnAction(event -> {
                    int[] nextMatch = results.get(i.incrementAndGet());

                    int display = i.get()+1;
                    label2.setText(""+display+"/"+results.size());
                    prev.setDisable(false);

                    //disable next if youre at the last search result
                    if (i.get() == results.size() - 1) {
                        next.setDisable(true);
                    }

                    int a = nextMatch[0]; int b = nextMatch[1];

                    htmlSourceCode.selectRange(a, b);
                    htmlSourceCode.setStyleClass(posx,posy,"search");


                });

                prev.setOnAction(event -> {
                    int[] nextMatch = results.get(i.decrementAndGet());
                    //

                    int display = i.get()+1;
                    label2.setText(""+display+"/"+results.size());


                    if (i.get() == 0) {
                        prev.setDisable(true);
                        next.setDisable(false);
                    }

                    int a = nextMatch[0]; int b = nextMatch[1];
                    htmlSourceCode.selectRange(a, b);
                });
            }
            else
            {
                this.controller.defaultStyle();

                //textField.setStyle();
                System.out.println("No results found for ");
                label2.setText("  0 results");
                next.setDisable(true);
                prev.setDisable(true);

            }
        });

        //creating search toolbar

        Stage stage = searcher.createGridStage("Find","");
        stage.setX(1100.0);
        stage.setY(200.0);

       // Deals with closing the searcher stage when it loses focus
        htmlSourceCode.setOnMouseClicked(event -> {
            controller.defaultStyle();
            stage.close();
        });

    }


    /**
     * Actual search functionality
     * @param lookingFor
     * @param htmlSourceCode
     * @return
     */
    public HashMap<Integer,int[]> search(String lookingFor, CodeArea htmlSourceCode) {

            Pattern pattern = Pattern.compile("" + lookingFor + "([\\S\\s]*?)");
            HashMap<Integer, int[]> searchPos = new HashMap<>();


            htmlSourceCode.unfoldText(0);


            Matcher matcher = pattern.matcher(htmlSourceCode.getText()); //Where input is a TextInput class
            int i = 0;
            while (matcher.find()) {
                //System.out.println(matcher.start() + "  " + matcher.end());


                //restore default page styling


                int a = matcher.start();
                int b = matcher.end();

                int[] pos = {matcher.start(), matcher.end()};

                //set colors for searching
                //htmlSourceCode.setStyle(a,b,"-fx-fill: red;");

                searchPos.put(i, pos);
                i++;
            }
            return searchPos;
        }



}
