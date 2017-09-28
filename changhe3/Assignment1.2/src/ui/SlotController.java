package ui;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ResourceBundle;

public class SlotController implements Initializable {
    public ImageView img;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        img.setFitHeight(60);
        img.setFitWidth(60);
    }

    public void loadImage(String pathStr) {
        try {
            Path path = FileSystems.getDefault().getPath(pathStr);
            img.setImage(new Image(Files.newInputStream(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

