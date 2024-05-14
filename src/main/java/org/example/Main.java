package org.example;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import static javafx.scene.input.MouseButton.PRIMARY;
import static javafx.scene.paint.Color.WHITE;

public class Main {

    public ImageView mapView;

    public Slider zoom;

    private Image map;
    private WritableImage writableImage;
    private PixelWriter writer;
    private PixelReader mapReader;
    private GraphNodeAL<Point2D> point;
    private GraphNodeAL<Point2D> crossroads;
    private int[] roads;


    public void initialize()
    {
        this.map = mapView.getImage();
        this.mapReader = map.getPixelReader();

        this.writableImage = new WritableImage((int) this.map.getWidth(), (int) this.map.getHeight());
        this.writer = writableImage.getPixelWriter();
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
    private void selectLocation(MouseEvent mouseEvent) {
        //Initialise coordinates.
        double x, y;

        //Setting coordinates.
        x = mouseEvent.getX();
        y = mouseEvent.getY();

        crossRoadDetector();
        if (point == null)
            point = new GraphNodeAL<>(new Point2D(x, y));
        else {
            point.connectToNodeUndirected(new GraphNodeAL<>(new Point2D(x, y)));

            System.out.println("You have clicked: " + x + "," + y);

            try {
                for (double j = (x - 2); j < (x + 2); j++) {

                    for (double i = (y - 2); i < (y + 2); i++) {
                        this.writer.setColor((int) j, (int) i, Color.RED);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.print("Out of bounds caught!");
            }
        }
    }

    private void crossRoadDetector() {
        int height = (int) map.getHeight();
        int width = (int) map.getWidth();
        roads = new int[(int) (height * width)];
        Arrays.fill(roads, -1);
        int radius;
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (compareColour(x, y)) {
                    if (roads[i] == -1)
                        roads[i] = i;
                    if (x + 1 < width && compareColour(x + 1, y) && i + 1 < roads.length) {
                        if (roads[i + 1] == -1)
                            roads[i + 1] = i;
                        else Set.union(roads, roads[i + 1], roads[i]);
                    }
                    if (y + 1 < height && compareColour(x, y + 1) && i + width < roads.length)
                        roads[i + width] = i;
                    if (y > 0 && compareColour(x, y - 1))
                        Set.union(roads, roads[i - width], roads[i]);
                }
                i++;
            }
        }
        radius = 1;
        for(int y = radius; y < width; y++)
            for(int x = radius; x < height; x++)
                for(int circleY = -radius; circleY < radius; circleY++)
                    for(int circleX = -radius; circleX < radius; circleX++)
                        if(compareColour(x + circleX, y + circleY))
                            crossroads.adjList.add(new GraphNodeAL<>(new Point2D(x, y)));
    }

    public boolean compareColour(int x, int y) {
        return (Math.abs((mapReader.getColor(x, y).getRed() * 255) - (WHITE.getRed() * 255)) <= 0.5 && Math.abs((mapReader.getColor(x, y).getGreen() * 255) - (WHITE.getGreen() * 255)) <= 0.5 && Math.abs((mapReader.getColor(x, y).getBlue() * 255) - (WHITE.getBlue() * 255)) <= 0.5);
    }

}
