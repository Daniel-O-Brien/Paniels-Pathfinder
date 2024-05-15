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
    private List<GraphNodeAL<Point2D>> points = new ArrayList<>();
    private List<GraphNodeAL<Point2D>> roads = new ArrayList<>();
    private int height;
    private int width;
    private List<GraphNodeAL<Point2D>> path = new ArrayList<>();
//    private int[] roads;
//    private int[] mapColour;


    public void initialize() {
        map = mapView.getImage();
        mapReader = map.getPixelReader();

        writableImage = new WritableImage((int) this.map.getWidth(), (int) this.map.getHeight());
        writer = writableImage.getPixelWriter();

        height = (int) map.getHeight();
        width = (int) map.getWidth();
    }


    @FXML
    private void closeHand() {
        zoom.setCursor(Cursor.CLOSED_HAND);
    }

    @FXML
    private void openHand() {
        zoom.setCursor(Cursor.OPEN_HAND);
    }

    @FXML
    private void selectLocation(MouseEvent mouseEvent) {

        //Setting coordinates.
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        if (points.isEmpty())
            points.add(new GraphNodeAL<>(new Point2D(mouseX, mouseY)));
        else {
            points.add(new GraphNodeAL<>(new Point2D(mouseX, mouseY)));
            crossRoadDetector();
        }

        System.out.println("You have clicked: " + mouseX + "," + mouseY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, mapReader.getColor(x, y));
            }
        }
        for(GraphNodeAL<Point2D> point : points)
            for (double j = (point.data.getY() - 2); j < (point.data.getY() + 2); j++)
                for (double i = (point.data.getX() - 2); i < (point.data.getX() + 2); i++)
                    if(j > 0 && j < width && i > 0 && i < height)
                        writer.setColor((int) i, (int) j, Color.RED);
        mapView.setImage(writableImage);
    }

    private void crossRoadDetector() {
//        for(int i =0; i < mapColour.length; i++){
//            writer.setColor();
//        }
//        roads = new int[(int) (height * width)];
//        Arrays.fill(roads, -1);
//        int radius;
//        int i = 0;
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                if (compareColour(x, y)) {
//                    if (roads[i] == -1)
//                        roads[i] = i;
//                    if (x + 1 < width && compareColour(x + 1, y) && i + 1 < roads.length) {
//                        if (roads[i + 1] == -1)
//                            roads[i + 1] = i;
//                        else Set.union(roads, roads[i + 1], roads[i]);
//                    }
//                    if (y + 1 < height && compareColour(x, y + 1) && i + width < roads.length)
//                        roads[i + width] = i;
//                    if (y > 0 && compareColour(x, y - 1))
//                        Set.union(roads, roads[i - width], roads[i]);
//                }
//                i++;
//            }
//        }
        int radius = 1;
        for (int y = 0; y < height - radius; y++) {
            start:
            for (int x = 0; x < width - radius; x++) {
                if (compareColour(x, y)) {
                    for (int circleY = -radius; circleY <= radius; circleY++) {
                        for (int circleX = -radius; circleX <= radius; circleX++) {
                            if (x + circleX >= 0 && y + circleY >= 0 && x + circleX < width && y + circleY < height && compareColour(x + circleX, y + circleY)) {
                                for (GraphNodeAL<Point2D> crossroad : roads)
                                    if (Math.abs(crossroad.data.getX() - x + circleX) < 2 && Math.abs(crossroad.data.getY() - y + circleY) < 2) {
                                        continue start;
                                    }
                                roads.add(new GraphNodeAL<>(new Point2D(x, y)));
                            }
                        }
                    }
                }
            }
        }
        int i = 0;
        for (GraphNodeAL<Point2D> road : roads) {
            for (int j = i+1; j < roads.size(); j++)
                if (Math.abs(road.data.getX() - roads.get(j).data.getX()) < 2 && Math.abs(road.data.getY() - roads.get(j).data.getY()) < 2) {
                road.connectToNodeUndirected(roads.get(j));
            }
            i++;
        }

        for (GraphNodeAL<Point2D> point : points) {
            GraphNodeAL<Point2D> closestRoad = roads.get(0);
            for (GraphNodeAL<Point2D> road : roads) {
                if (Math.abs(point.data.getX() - closestRoad.data.getX()) < Math.abs(point.data.getX() - road.data.getX()) && point.data.getY() - closestRoad.data.getY() < point.data.getY() - road.data.getY())
                    closestRoad = road;
            }
            point.connectToNodeUndirected(closestRoad);
        }
        path = traverseGraphDepthFirst(points.get(0), points.get(1), null, null);


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, mapReader.getColor(x, y));
            }
        }

        for(GraphNodeAL<Point2D> node : path)
            for (double j = (node.data.getY() - 2); j < (node.data.getY() + 2); j++)
                for (double k = (node.data.getX() - 2); k < (node.data.getX() + 2); k++)
                    writer.setColor((int) k, (int) j, Color.RED);
        mapView.setImage(writableImage);
    }

    public List<GraphNodeAL<Point2D>> traverseGraphDepthFirst(GraphNodeAL<Point2D> from, GraphNodeAL<Point2D> to, List<GraphNodeAL<Point2D>> encountered, List<GraphNodeAL<Point2D>> path){
        if(encountered==null){
            encountered=new ArrayList<>();
            path = new ArrayList<>();
        }
        encountered.add(from);
        path.add(from);
        if(from == to)
            return path;
        for(GraphNodeAL<Point2D> adjNode : from.adjList) {
            if (!encountered.contains(adjNode))
                traverseGraphDepthFirst(adjNode, to, encountered, path);
            else {
                path.remove(path.size()-1);
                return path;
            }
        }
        return null;
    }

    public boolean compareColour(int x, int y) {
        return ((mapReader.getColor(x, y).getRed() * 255) >= 0xF0 && (mapReader.getColor(x, y).getGreen() * 255) >= 0xF0  && (mapReader.getColor(x, y).getBlue() * 255) >= 0xF0);
    }
}
