package com.groupfour;
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
public class Controller implements Initializable{

    private static MediaPlayer mediaPlayer;

    @FXML
    private Slider musicControl;

    @FXML
    private GridPane gridpane;

    private List<Circle> circles;


    public void playBackgroundMusic() {
        String musicFile = "src\\main\\res\\c5Music.m4a";
        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        mediaPlayer.play();
    }
        @Override
        public void initialize(URL arg0, ResourceBundle arg1) {
            circles = new ArrayList<>();
            for (javafx.scene.Node node : gridpane.getChildren()) {
                if (node instanceof Circle) {
                    circles.add((Circle) node);
                }
            }

            gridpane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Node node = (Node) event.getTarget();
                    int columnIndex = GridPane.getColumnIndex((javafx.scene.Node) node);
                    if (columnIndex != -1) { // check if the click is on a column
                        changeCircleColor(columnIndex);
                    }
                }
            });
        }

        private void changeCircleColor(int columnIndex) {
            for (int i = circles.size() - 1; i >= 0; i--) {
                Circle circle = circles.get(i);
                int circleColumnIndex = GridPane.getColumnIndex(circle);
                if (circleColumnIndex == columnIndex && circle.getFill().equals(Color.LIGHTGRAY)) {
                    circle.setFill(Color.RED); // change the color to red
                    break;
                }
            }
        }
    }
