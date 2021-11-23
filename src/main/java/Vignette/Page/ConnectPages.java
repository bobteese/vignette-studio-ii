package Vignette.Page;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


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

    public Group connectSourceAndTarget(String connectedVia, String previousConnection){

        Line connector = new Line(10.0f, 10.0f, 100.0f, 40.0f);
        /**
         * to check if the connection already exits then recreate the connection to prevent overlap on UI.
         */
        int groupItr = 0;
        int arrowItr = 0;


        try {
            if(!"".equalsIgnoreCase(previousConnection) && !target.getText().equalsIgnoreCase(previousConnection)){
                AtomicInteger groupIndexToRemove = new AtomicInteger();
                pane.getChildren().stream().forEach(element ->{
                    if(element instanceof Group){
                        ((Group) element).getChildren().stream().forEach(arrowElement->{
                            if(arrowElement instanceof Arrow){
                                Arrow arrow = ((Arrow)arrowElement);
                                if(arrow.getSource().getText().equalsIgnoreCase(source.getText()) && arrow.getTarget().getText().equalsIgnoreCase(previousConnection)){
                                    pane.getChildren().remove(groupIndexToRemove.get());
                                }
                            }
                        });
                    }
                    groupIndexToRemove.getAndIncrement();
                });
            }
            else
            {
                Iterator<Node> itr = pane.getChildren().listIterator();
                while (itr.hasNext()){
                    Object g = itr.next();
                    if(g instanceof Group){
                        ListIterator<Node> arrowList = (((Group) g).getChildren().listIterator());
                        while(arrowList.hasNext()){
                            Object aTemp =  arrowList.next();
                            if(aTemp instanceof Arrow){
                                Arrow a = (Arrow) aTemp;
                                if((source.getText().equalsIgnoreCase(a.getSource().getText()) && target.getText().equalsIgnoreCase(a.getTarget().getText()))){
                                    if(pane.getChildren().remove(pane.getChildren().get(groupItr))) {
                                        System.out.println("CONNECTION TO RECREATED!!");
                                    }
                                }
                                //DO NOT REMOVE
                            }
                            arrowItr++;
                        }
                    }
                    groupItr++;
                }
            }
        }catch (Exception e){
            System.out.println("NULL " +e.getMessage());
        }


        // create a Group
        Arrow arrow = new Arrow(source,target, connectedVia, pane);
        Group group = new Group(arrow, arrow.getLineLabel());

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

        // add text to the arrow
        if(connectedVia!=null){
            arrow.setLabelTest(connectedVia);
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
