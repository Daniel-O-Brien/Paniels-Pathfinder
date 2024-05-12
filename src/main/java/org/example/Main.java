package org.example;

import java.io.IOException;
import java.io.Reader;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Main {

    public ImageView mapView;

    public Slider zoom;

    private Image map;
    private WritableImage writableImage;
    private PixelWriter writer;
    private PixelReader mapReader;


    public void initialize()
    {
        this.map = mapView.getImage();
        this.mapReader = map.getPixelReader();

        this.writableImage = new WritableImage((int) this.map.getWidth(), (int) this.map.getHeight());
        writer = writableImage.getPixelWriter();
    }




    @FXML
    private void closeHand()
    {
        zoom.setCursor(Cursor.CLOSED_HAND);
    }

    @FXML
    private void openHand(){
        zoom.setCursor(Cursor.OPEN_HAND);
    }

    @FXML
    private void selectLocation(MouseEvent mouseEvent){
        //Initialise coordinates.
        double x, y;

        //Setting coordinates.
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        System.out.println("You have clicked: " + x + "," + y);

        for(double j = (y - 2); j < (y + 2); j++)
        {

            for (double i = (x - 2); i < (x + 2); i++)
            {
                this.writer.setColor( (int) i, (int) j, Color.RED);
            }

            System.out.println();
        }

    }

}
