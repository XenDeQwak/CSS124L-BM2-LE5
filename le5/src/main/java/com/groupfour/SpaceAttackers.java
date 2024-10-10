package com.groupfour;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpaceAttackers {

    static SpaceAttackers CURRENT_INSTANCE;

    Group root;
    Scene scene;
    Stage stage;

    ImageView ship;
    ArrayList<ImageView> enemyList;

    ArrayList<Rectangle> bullets;
    Timeline bulletTimeline;
    Timeline enemyTimeline;
    Timeline shipTimeline;

    int enemymove = 15;

    public SpaceAttackers (SpaceAttackers spacegame) {
        CURRENT_INSTANCE = spacegame;

        root = new Group();
        scene = new Scene(root, 680, 480, Color.BLACK);
        stage = new Stage();

        bullets = new ArrayList<>();

        enemyList = new ArrayList<>();
        populateEnemy(enemyList);
        makeShip();
        bulletMovement();
        enemyMovement();

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
    }

    private void makeShip () {
        ship = new ImageView(new Image(getClass().getResource("ship.png").toExternalForm()));
        ship.setFitWidth(50);
        ship.setFitHeight(50);
        ship.setLayoutX(scene.getWidth()/2 - ship.getFitWidth()/2);
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

    private void populateEnemy(ArrayList<ImageView> enemyList) {
        int enemyRowCount = 3;
        int enemyColCount = 8;
        int enemyCount = enemyRowCount * enemyColCount;

        int posXStart = 15;
        int posYSStart = 10;
        int posX = posXStart;
        int posY = posYSStart;

        for (int row = 0; row < enemyRowCount; row++) {
            ImageView enemy = null;
            for (int col = 0; col < enemyColCount; col++) {
                
                enemy = new ImageView(new Image(getClass().getResource("enemy.png").toExternalForm()));
                enemy.setLayoutX(posX);
                enemy.setLayoutY(posY);
                enemy.setFitWidth(50);
                enemy.setFitHeight(50);
                root.getChildren().add(enemy);
                enemyList.add(enemy);

                posX += 30 + enemy.getFitWidth();

            }
            posX = posXStart;
            posY += 15 + enemy.getFitHeight();
        }
    }

    public final void bulletMovement() {
        bulletTimeline = new Timeline();
        bulletTimeline.setCycleCount(Timeline.INDEFINITE);    

        KeyFrame keyFrame = new KeyFrame(Duration.millis(1), event -> {
            Rectangle removeBullet = null;
            ImageView removeEnemy = null;

            for (Rectangle bul : bullets) {
                bul.setLayoutY(bul.getLayoutY() - 1);

                // Check collision
                for (ImageView enemy : enemyList) {
                    if (bul.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                        removeBullet = bul;
                        removeEnemy = enemy;
                        break;
                    }
                }
            }

            // Remove entities
            if (removeBullet != null && removeEnemy != null) {
                root.getChildren().remove(removeBullet);
                root.getChildren().remove(removeEnemy);
                bullets.remove(removeBullet);
                enemyList.remove(removeEnemy);
            }

        });

        bulletTimeline.getKeyFrames().add(keyFrame);
        bulletTimeline.play();
    }

    public final void enemyMovement() {
        enemyTimeline = new Timeline();
        enemyTimeline.setCycleCount(Timeline.INDEFINITE);    
        
        KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), (event) -> {
            boolean goDown = false;
            
            if (enemyList.isEmpty()) win();

            for (ImageView e : enemyList) {
                if (e.getBoundsInParent().getMinX() < 0 || e.getBoundsInParent().getMaxX() > stage.getWidth()-e.getFitWidth()/2) {
                    goDown = true;
                    break;
                }
            }

            if (goDown) {
                //lose();
                enemymove *= -1;
                for (ImageView e : enemyList) {
                    e.setLayoutY(e.getLayoutY() + 20);
                    e.setLayoutX(e.getLayoutX() + enemymove);
                }
            }

            boolean losegame = false;
            for (ImageView e : enemyList) {
                e.setLayoutX(e.getLayoutX() + enemymove);
                if (e.getBoundsInParent().intersects(ship.getBoundsInParent()) || e.getLayoutY()+e.getFitHeight()*1.65 > stage.getHeight()) {
                    losegame = true;
                    break;
                }
            }
            if (losegame) {
                lose();
            }
        });

        enemyTimeline.getKeyFrames().add(keyFrame);
        enemyTimeline.play();
    }


    public void shipMoveLeft() {
        shipTimeline = new Timeline();
        shipTimeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(1), event -> {
            for (Rectangle bul : bullets) {
                bul.setLayoutY(bul.getLayoutY() - 1);
            }
        });

        shipTimeline.getKeyFrames().add(keyFrame);
    }

    public void start () {
        stage.setScene(scene);
        
        stage.show();
    }

    public void lose () {


        bullets.clear();
        enemyList.clear();
        root.getChildren().clear();

        Group loseRoot = new Group();
        Scene loseScene = new Scene(loseRoot, 200,100);

        Label loseTitle = new Label("YOU LOSE!!");
        Button tryAgain = new Button("Try Again?");
        Button goBack = new Button("Go Back?");

        loseTitle.setLayoutX(65);
        loseTitle.setLayoutY(10);

        tryAgain.setLayoutX(25);
        tryAgain.setLayoutY(45);
        
        goBack.setLayoutX(110);
        goBack.setLayoutY(45);

        tryAgain.setOnAction(e -> {
            System.out.println("tryagain");
            stage.setScene(scene);

            enemymove = 15;
            makeShip();
            populateEnemy(enemyList);
        });

        goBack.setOnAction(e -> {
            System.out.println("goback");
            stage.close();
            App.stage.show();
        });

        loseRoot.getChildren().addAll(loseTitle, tryAgain, goBack);
        stage.setScene(loseScene);
    }


    public void win () {


        bullets.clear();
        enemyList.clear();
        root.getChildren().clear();

        Group loseRoot = new Group();
        Scene loseScene = new Scene(loseRoot, 200,100);

        Label loseTitle = new Label("YOU WIN!!");
        Button tryAgain = new Button("Try Again?");
        Button goBack = new Button("Go Back?");

        loseTitle.setLayoutX(65);
        loseTitle.setLayoutY(10);

        tryAgain.setLayoutX(25);
        tryAgain.setLayoutY(45);
        
        goBack.setLayoutX(110);
        goBack.setLayoutY(45);

        tryAgain.setOnAction(e -> {
            System.out.println("tryagain");
            stage.setScene(scene);

            enemymove = 15;
            makeShip();
            populateEnemy(enemyList);
        });

        goBack.setOnAction(e -> {
            System.out.println("goback");
            stage.close();
            App.stage.show();
        });

        loseRoot.getChildren().addAll(loseTitle, tryAgain, goBack);
        stage.setScene(loseScene);
    }

}
