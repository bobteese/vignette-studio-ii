package TabPane;

import GridPaneHelper.GridPaneHelper;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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

//    public void changeFormat(Slider slider, CodeArea htmlSourceCode)
//    {
//
//        // Styling
//        String buttonStyle= "-fx-text-align: center;"+ "-fx-background-color: transparent;" +
//                "-fx-border-radius: 7;" + "-fx-border-width: 3 3 3 3;" + "-fx-dark-text-color: black;"
//                + "-fx-opacity:1;" + "-fx-font-size: " ;
//
//        String defaultSize = "12px;";
//
//
//        GridPaneHelper helper12 = new GridPaneHelper();
//
//
//        helper12.setPrefSize(550,200);
//        Button button = new Button("Aa Bb Cc 123");
//        button.setPrefSize(400,200);
//
//
//        String style = htmlSourceCode.getStyle();
//        if(style!="") {
//            String target = "(?<=:)(.*?)(?=px)";
//            Pattern p = Pattern.compile(target);
//            Matcher m = p.matcher(style);
//            if (m.find()) {
//                String match = m.group(0);
//                double size = Double.parseDouble(match);
//                slider.setValue(size);
//                button.setStyle(buttonStyle+size+"px;");
//            }
//        }
//        else {
//            slider.setValue(11);
//            button.setStyle(buttonStyle + defaultSize);
//        }
//
//
//        helper12.add(button,0,0,1,1);
//        helper12.add(slider,0,4,3,1);
//
//
//        slider.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                button.setStyle(buttonStyle+ newValue+ "px;");
//            }
//        });
//
//
//        boolean clickedOk = helper12.createGridWithoutScrollPane("change font","","OK","Cancel");
//        //confirming font change
//        if(clickedOk)
//            htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
//    }


    /**
     * Function called when using the keyboard shortcut ctrl, + to increase font size
     */
    public void increaseFont(Slider slider, CodeArea htmlSourceCode)
    {
        slider.increment();
        htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }

    /**
     * Function called when using the keyboard shortcut ctrl, - to decrease font size
     */
    public void decreaseFont(Slider slider, CodeArea htmlSourceCode)
    {
        slider.decrement();
        htmlSourceCode.setStyle("-fx-font-size: "+slider.getValue()+"px;");
    }


    /**
     * This function starts the process for searching for a string within the editor.
     * Creates the dialog box that lets the user enter what theyre looking for.
     * @param htmlSourceCode
     */
    public void findAndSelectString(CodeArea htmlSourceCode)
    {
        //show script when searching
        if(this.controller.getScriptIsHidden()) {
            this.controller.showScript();
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

            //resetting the style
            controller.defaultStyle();

            HashMap<Integer,int[]> results;
            if(!newValue.equals("")){
                results = (HashMap<Integer, int[]>) search(newValue,htmlSourceCode);
            }
            else {
                results = null;
                label2.setText("  0 results");
            }

            if(results!= null && results.size()!=0) {

                //resetting the style
                this.controller.defaultStyle();

                label2.setText("  " + results.size() + " results");
                //This highlights all the search results
                for (int a = 0; a < results.size(); a++) {
                    int[] match = results.get(a);
                    int posx = match[0];
                    int posy = match[1];
                    htmlSourceCode.setStyleClass(posx, posy, "search");
                }


                // value to step through searches
                AtomicInteger i = new AtomicInteger();
                i.set(0);

                //initial search result
                int[] match = results.get(i.get());
                int posx = match[0]; int posy = match[1];

                htmlSourceCode.selectRange(posx, posy);


                //if there arent any search results, disable the next button
                if (results.size() > 0)
                    next.setDisable(false);


                //next button step through functionality
                next.setOnAction(event -> {
                    int[] nextMatch = results.get(i.incrementAndGet());

                    int display = i.get()+1;
                    label2.setText(display+"/"+results.size());
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
                    int display = i.get()+1;
                    label2.setText(display+"/"+results.size());


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
                //resetting the style
                this.controller.defaultStyle();
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
     * The Search functionality
     * @param lookingFor
     * @param htmlSourceCode
     * @return
     */
    public Map<Integer,int[]> search(String lookingFor, CodeArea htmlSourceCode) {

            String search = "(?i)"+lookingFor;
            Pattern pattern = Pattern.compile(search);
            HashMap<Integer, int[]> searchPos = new HashMap<>();
            Matcher matcher = pattern.matcher(htmlSourceCode.getText()); //Where input is a TextInput class
            int i = 0;
            while (matcher.find()) {
                int[] pos = {matcher.start(), matcher.end()};
                searchPos.put(i, pos);
                i++;
            }
            return searchPos;
        }
}
