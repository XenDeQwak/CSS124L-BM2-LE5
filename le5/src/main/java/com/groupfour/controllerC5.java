package com.groupfour;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class controllerC5 implements Initializable{

    private static MediaPlayer mediaPlayer;

    @FXML
    private Slider musicControl;

    @FXML
    private GridPane gridpane;

    private List<Circle> circles;


    public void playBackgroundMusic() {
        String musicFile = "c5Music.mp3";
        URL resource = getClass().getResource(musicFile);
        if (resource == null) {
            System.out.println("Error: Could not find music file " + musicFile);
            return;
        }
        Media media = new Media(resource.toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
        @Override
        public void initialize(URL arg0, ResourceBundle arg1) {
            circles = new ArrayList<>();
            for (Node node : gridpane.getChildren()) {
                if (node instanceof Circle) {
                    circles.add((Circle) node);
                }
            }

            gridpane.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                Node node = (Node) e.getTarget();
                int columnIndex = GridPane.getColumnIndex(node);
                changeCircleColor(columnIndex);
            });
        }

        private void changeCircleColor(int columnIndex) {
            // for (int i = circles.size() - 1; i >= 0; i--) {
            //     Circle circle = circles.get(i);
            //     int circleColumnIndex = GridPane.getColumnIndex(circle);
            //     if (circleColumnIndex == columnIndex && circle.getFill().equals(Color.LIGHTGRAY)) {
            //         circle.setFill(Color.RED); 
            //         break;
            //     }
            // }
            System.out.println("Test");
        }
    }
