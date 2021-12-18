package gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GuiElementBox{
    public ImageView imageView;
    public GuiElementBox(String directory){
        try {
            Image image = new Image(new FileInputStream(directory));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            this.imageView = imageView;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
