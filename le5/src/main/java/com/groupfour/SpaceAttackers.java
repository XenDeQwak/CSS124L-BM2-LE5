package com.groupfour;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpaceAttackers {

    Group root;
    Scene scene;
    Stage stage;

    ImageView ship;
    ArrayList<ImageView> enemyList;

    ArrayList<Rectangle> bullets;
    Thread bullThread;


    public SpaceAttackers () {
        
        root = new Group();
        scene = new Scene(root, 680, 480, Color.BLACK);
        stage = new Stage();

        bullets = new ArrayList<>();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
            int move = 10;
            switch (key.getCode()) {
                case RIGHT:
                    ship.setLayoutX(ship.getLayoutX() + move);
                    break;
                case LEFT:
                    ship.setLayoutX(ship.getLayoutX() - move);
                    break;
                case UP:
                    shoot();
                    break;
                default:
                    System.out.println(key.getCode());
                    //throw new AssertionError();
            }
        });

        bulletMovement();

        ship = new ImageView(new Image(getClass().getResource("ship.png").toExternalForm()));
        ship.setFitWidth(50);
        ship.setFitHeight(50);
        ship.setLayoutX(0);
        ship.setLayoutY(480-ship.getFitHeight());
        root.getChildren().add(ship);
    }

    public void shoot() {

        System.out.println("I shoot");

        Rectangle bul = new Rectangle(5,20, Color.WHITE);
        bul.setLayoutX(ship.getLayoutX() + ship.getFitWidth()/2);
        bul.setLayoutY(ship.getLayoutY());
        root.getChildren().add(bul);
        bullets.add(bul);

    }

    public void bulletMovement() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(1), event -> {
            for (Rectangle bul : bullets) {
                bul.setLayoutY(bul.getLayoutY() - 1);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    public void shipMoveLeft() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(1), event -> {
            for (Rectangle bul : bullets) {
                bul.setLayoutY(bul.getLayoutY() - 1);
            }
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    

    public void start () {
        stage.setScene(scene);
        
        stage.show();
    }

}
