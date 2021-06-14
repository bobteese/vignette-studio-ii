package Vignette.Page;

import Application.Main;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;


public class ConnectPages {

    Button source;
    Button target;
    AnchorPane pane;

    HashMap<String, ArrayList<Group>> listOfLineConnectors;
    public ConnectPages( Button source, Button target, AnchorPane pane, HashMap<String, ArrayList<Group>> listOfLineConnectors){
        this.source = source;
        this.target = target;
        this.pane = pane;
        this.listOfLineConnectors = listOfLineConnectors;
    }

    public Group connectSourceAndTarget(){

        Line connector = new Line(10.0f, 10.0f, 100.0f, 40.0f);
        Arrow arrow = new Arrow(source,target);
        // create a Group
        Group group = new Group(arrow);


//        connector.startXProperty().bind(source.layoutXProperty().add((source.getBoundsInParent().getWidth()/2.0)+10));
//        connector.startYProperty().bind(source.layoutYProperty().add((source.getBoundsInParent().getHeight()/2.0)+10 ));
//
//        connector.endXProperty().bind(target.layoutXProperty().add(target.getBoundsInParent().getWidth()/2.0 ));
//        connector.endYProperty().bind(target.layoutYProperty().add(target.getBoundsInParent().getHeight()/2.0 ));

        arrow.setEndX(connector.getEndX());
        arrow.setEndY(connector.getEndY());
        arrow.setStartX(connector.getStartX());
        arrow.setStartY(connector.getStartY());

        pane.getChildren().add(group);


        //Main.getInstance().addUndoStack(group);


        if(listOfLineConnectors.containsKey(source.getText())){

            ArrayList<Group> list = listOfLineConnectors.get(source.getText());
            list.add(group);
            listOfLineConnectors.replace(source.getText(),list) ;

        }
        if(listOfLineConnectors.containsKey(target.getText())){
            ArrayList<Group> list = listOfLineConnectors.get(target.getText());
            list.add(group);
            listOfLineConnectors.replace(target.getText(),list) ;
        }
        if(!listOfLineConnectors.containsKey(source.getText())) {
            ArrayList<Group> arraylist = new ArrayList<>();
            arraylist.add(group);
            listOfLineConnectors.put(source.getText(),arraylist);

        }
        if(!listOfLineConnectors.containsKey(target.getText())){
            ArrayList<Group> arraylist = new ArrayList<>();
            arraylist.add(group);
            listOfLineConnectors.put(target.getText(),arraylist);
        }


     return group;
    }

    public HashMap<String, ArrayList<Group>> getListOfLineConnectors() {
        return listOfLineConnectors;
    }

    public void setListOfLineConnectors(HashMap<String, ArrayList<Group>> listOfLineConnectors) {
        this.listOfLineConnectors = listOfLineConnectors;
    }
}
