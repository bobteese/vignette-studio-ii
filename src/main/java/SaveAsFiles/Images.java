package SaveAsFiles;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;

public class Images implements Serializable {

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
