package SaveAsFiles;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.HashMap;

public class Images {

    String imageName;
    BufferedImage image;

    public Images(String imageName, BufferedImage image){
        this.imageName = imageName;
        this.image = image;
    }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
