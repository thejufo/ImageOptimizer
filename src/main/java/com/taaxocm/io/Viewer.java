package com.taaxocm.io;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Viewer extends Stage {

    private ImageView imageView;

    public Viewer() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);

        VBox root = new VBox();
        root.getChildren().add(imageView);

        setScene(new Scene(root));
    }

    public void setImage(Image source) {
        if (source != null) {
            imageView.setImage(source);
            hide();
            resizeImage(imageView, source);
            show();
        }
    }

    public void resizeImage(ImageView imageView, Image source) {
        double width, height;
        if (source != null) {
            double ratio = source.getWidth() / source.getHeight();

            if ((ratio / 500) < 500) {
                width = 500;
                height = (int) (500 / ratio);
            } else if (500 * ratio < 500) {
                height = 500;
                width = (int) (500 * ratio);
            } else {
                height = 500;
                width = 500;
            }
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
        }
    }
}
