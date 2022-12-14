package Vignette.Page;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class Arrow extends Group {

    private final Line line;
    Button source;
    Button target;

    public Button getSource() {
        return source;
    }

    public void setSource(Button source) {
        this.source = source;
    }

    public Button getTarget() {
        return target;
    }

    public void setTarget(Button target) {
        this.target = target;
    }

    public Label getLineLabel() {
        return lineLabel;
    }

    public void setLineLabel(Label lineLabel) {
        this.lineLabel = lineLabel;
    }

    Label lineLabel = new Label();
    public Arrow(Button source, Button target, String lineText, AnchorPane pane) {
        this( new Line(1250.0f, 150.0f, 100.0f, 300.0f),
                new Line(1250.0f, 150.0f, 100.0f, 300.0f),
                new Line(1250.0f, 150.0f, 100.0f, 300.0f));
        if(pane.getChildren().indexOf(this.lineLabel)==-1){
            pane.getChildren().add(this.lineLabel);
        }
        this.source = source;
        this.target = target;
        this.lineLabel.setLabelFor(line);
        this.lineLabel.setText("");
        this.lineLabel.setText(lineText);
    }
    public void setLabelTest(String text){
        this.lineLabel.setText("");
        this.lineLabel.setText(text);
    }
    private static final double arrowLength = 20;
    private static final double arrowWidth = 7;

    private Arrow(Line line, Line arrow1, Line arrow2) {
        super(line, arrow1, arrow2);
        this.line = line;
        this.lineLabel = lineLabel;
        InvalidationListener updater = o -> {
            double ex = getEndX();
            double ey = getEndY();
            double sx = getStartX();
            double sy = getStartY();

            arrow1.setEndX(ex);
            arrow1.setEndY(ey);
            arrow2.setEndX(ex);
            arrow2.setEndY(ey);

            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
                double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part ortogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(ex + dx - oy);
                arrow1.setStartY(ey + dy + ox);
                arrow2.setStartX(ex + dx + oy);
                arrow2.setStartY(ey + dy - ox);
            }
            lineLabel.setLayoutX((line.getStartX()+line.getEndX())/2);
            lineLabel.setLayoutY((line.getStartY()+ line.getEndY())/2);
        };

        // add updater to properties
        startXProperty().addListener(updater);
        startYProperty().addListener(updater);
        endXProperty().addListener(updater);
        endYProperty().addListener(updater);
        updater.invalidated(null);
    }

    // start/end properties

    public final void setStartX(double value) {
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                   Bounds b = source.getBoundsInParent();
                    return b.getMinX() + b.getWidth() / 2 ;
                 }, source.boundsInParentProperty()));
        //line.setStartX(value);
    }

    public final double getStartX() {
        return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
        return line.startXProperty();
    }

    public final void setStartY(double value) {
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds b = source.getBoundsInParent();
                    return b.getMinY() + b.getHeight() / 2 ;
                }, source.boundsInParentProperty()));
        //line.setStartY(value);
    }

    public final double getStartY() {
        return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
        return line.startYProperty();
    }

    public final void setEndX(double value) {
        if (target != null){
            line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                Bounds b = target.getBoundsInParent();
                return (b.getMinX() + b.getWidth() / 2)-10 ;
            }, target.boundsInParentProperty()));
        }

        //line.setEndX(value);
    }

    public final double getEndX() {
        return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
        return line.endXProperty();
    }

    public final void setEndY(double value) {
        if (target != null){
            line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                Bounds b = target.getBoundsInParent();
                return (b.getMinY() + b.getHeight() / 2)-10 ;
            }, target.boundsInParentProperty()));
        }

        //line.setEndY(value);
    }

    public final double getEndY() {
        return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
        return line.endYProperty();
    }

}