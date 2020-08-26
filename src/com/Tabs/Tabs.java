package com.Tabs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Tabs {

    public TabPane getTabsPane() {

        TabPane tabPane = new TabPane();

        Tab vignette = setVignetteTab();
        Tab pages = setPageTab();

        tabPane.getTabs().add(vignette);
        tabPane.getTabs().add(pages);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        return tabPane;

    }

    public Tab setVignetteTab() {
        Tab vignette = new Tab("Vignette", new Label(""));

        // vignette pane
        BorderPane vignettePane = new BorderPane();
        SplitPane splitPane = new SplitPane();

        Image login = new Image(getClass().getResourceAsStream(
                "/resources/images/plain.png"));
        ImageView imageView = new ImageView(login);

        Image plain = new Image(getClass().getResourceAsStream(
                "/resources/images/plain.png"));
        ImageView plainImageView = new ImageView(plain);


        VBox leftControl  = new VBox(imageView, plainImageView);
        leftControl.setMaxWidth(80);
        leftControl.setPadding(new Insets(10, 20, 20, 20));
        leftControl.setSpacing(20);

        VBox rightControl = new VBox(new Label("Right Control"));

        splitPane.getItems().addAll(leftControl, rightControl);
        splitPane.setPrefSize(600, 800);
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        splitPane.setStyle("-fx-focus-color: transparent;");

        // add tab pane and set border pane
        vignette.setContent(splitPane);

        return vignette;
    }
    public Tab setPageTab() {
        Tab pages = new Tab("Pages"  , new Label(""));

        //page pane
        BorderPane pagePane = new BorderPane();

        pages.setContent(pagePane);

        return pages;
    }
}
