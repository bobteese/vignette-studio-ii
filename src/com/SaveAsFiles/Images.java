package com.SaveAsFiles;

import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Images {

    HashMap<String,BufferedImage> listOfImages;
    public Images(){
        listOfImages = new HashMap<>();
    }
    public HashMap<String, BufferedImage> getListOfImages() {
        return listOfImages;
    }

    public void setListOfImages(String fileName ,BufferedImage image) {
        this.listOfImages.put(fileName, image);
    }

}
