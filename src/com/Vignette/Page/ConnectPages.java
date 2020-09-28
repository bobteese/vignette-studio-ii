package com.Vignette.Page;

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
    public ConnectPages( Button source, Button target, AnchorPane pane){
        this.source = source;
        this.target = target;
        this.pane = pane;
        this.listOfLineConnectors = new HashMap<>();
    }

    public void connectSourceAndTarget(){

        Line connector = new Line(10.0f, 10.0f, 100.0f, 40.0f);
        // create a Group
        Group group = new Group(connector);

        connector.startXProperty().bind(source.layoutXProperty().add((source.getBoundsInParent().getWidth()/2.0)+10));
        connector.startYProperty().bind(source.layoutYProperty().add((source.getBoundsInParent().getHeight()/2.0)+10 ));

        connector.endXProperty().bind(target.layoutXProperty().add(target.getBoundsInParent().getWidth()/2.0 ));
        connector.endYProperty().bind(target.layoutYProperty().add(target.getBoundsInParent().getHeight()/2.0 ));
        pane.getChildren().add(group);
        ArrayList<Group> arraylist = new ArrayList<Group>();


    }

    public HashMap<String, ArrayList<Group>> getListOfLineConnectors() {
        return listOfLineConnectors;
    }

    public void setListOfLineConnectors(HashMap<String, ArrayList<Group>> listOfLineConnectors) {
        this.listOfLineConnectors = listOfLineConnectors;
    }
}
