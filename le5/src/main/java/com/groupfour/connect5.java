package com.groupfour;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;

public class connect5 {

    private static Scene scene;
    private static Stage stage;


    private static final int TILE_SIZE = 80;
    private static final int COLUMNS = 8;
    private static final int ROWS = 7;
    private boolean redMove = true;
    private Disc[][] grid = new Disc[COLUMNS][ROWS];
    private Pane discRoot = new Pane();

    private Pane createContent() {


        Pane root = new Pane();
        root.getChildren().add(discRoot);

        root.getChildren().add(makeGrid());
        root.getChildren().addAll(makeColumns());
        root.getChildren().addAll(makeBanner(), returnBtn());
        return root;

    }

    private Button returnBtn() {
        Button returnBtn = new Button("Return");
        returnBtn.setFocusTraversable(false);
        returnBtn.setOnMouseClicked(e->{
            controllerC5 cC5 = new controllerC5();
            cC5.stopBackgroundMusic();
            stage.close();
            App.getStage().show();
        });
        
        // Apply the style directly to the button
        returnBtn.setStyle(
            "-fx-background-color: #444;" +
            "-fx-text-fill: #FFF;" +
            "-fx-border-color: #666;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-font-size: 24;" +
            "-fx-font-weight: bold;" +
            "-fx-transition: -fx-background-color 0.3s;"
        );
                
        // Add a hover effect
        returnBtn.setOnMouseEntered(e -> returnBtn.setStyle(
            "-fx-background-color: #555;" +
            "-fx-text-fill: #FFF;" +
            "-fx-border-color: #666;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 10;" +
            "-fx-font-size: 24;" +
            "-fx-font-weight: bold;" +
            "-fx-transition: -fx-background-color 0.3s;"
        ));
        
        returnBtn.setOnMouseExited(e -> returnBtn.setStyle(
        "-fx-background-color: #444;" +
        "-fx-text-fill: #FFF;" +
        "-fx-border-color: #666;" +
        "-fx-border-width: 2;" +
        "-fx-border-radius: 10;" +
        "-fx-font-size: 24;" +
        "-fx-font-weight: bold;" +
        "-fx-transition: -fx-background-color 0.3s;"
        ));
        
        // Position the button below the banner image
        returnBtn.setLayoutY(TILE_SIZE * (ROWS + 2.5)); // Adjusted y position
        returnBtn.setLayoutX((TILE_SIZE * (COLUMNS - 8)) / 2 ); // Adjusted x position
        
        // Scale up the button
        returnBtn.setPrefWidth(720); // Set preferred width
        returnBtn.setPrefHeight(50); // Set preferred height
        
        resetBoard();
        return returnBtn;
    }

    private ImageView makeBanner() {
        Image image = new Image(getClass().getResource("c5Banner.png").toString());
        ImageView banner = new ImageView(image);
        banner.setY(TILE_SIZE * (ROWS + 1));

        return banner;
    }


    private Shape makeGrid() {
        Shape shape = new Rectangle(TILE_SIZE * (COLUMNS + 1), TILE_SIZE * (ROWS + 1));

        for (int y = 0; y < ROWS; y++ ) {
            for (int x = 0; x < COLUMNS; x++) {
                Circle circle = new Circle(TILE_SIZE/2);
                circle.setCenterX(TILE_SIZE/2);
                circle.setCenterY(TILE_SIZE/2);
                circle.setTranslateX(x * (TILE_SIZE+ 5) + 20);
                circle.setTranslateY(y * (TILE_SIZE+ 5) + 20);
                shape = shape.subtract(shape, circle);
            }

        }

        shape.setFill(Color.BLUE);
        return shape;

    }

    private ArrayList<Shape> makeColumns() {
        ArrayList<Shape> list = new ArrayList<>();
        for (int x = 0; x < COLUMNS; x++) {
            Shape rectangle = new Rectangle(TILE_SIZE,TILE_SIZE * (ROWS + 1));
            rectangle.setTranslateX(x * (TILE_SIZE+ 5) + 20);
            rectangle.setFill(Color.TRANSPARENT);

            rectangle.setOnMouseEntered(e -> rectangle.setFill(Color.rgb(200, 200, 50, 0.3)));
            rectangle.setOnMouseExited(e -> rectangle.setFill(Color.TRANSPARENT));

            final int column = x;
            rectangle.setOnMouseClicked(e -> placeDisc(new Disc(redMove), column));

            list.add(rectangle);

        }
        return list;


    }

    private boolean gameEnded(int column, int row) {
        List<Point2D> vertical = IntStream.rangeClosed(row - 4, row + 4)
                .mapToObj(r -> new Point2D(column, r))
                .collect(Collectors.toList());
        List<Point2D> horizontal = IntStream.rangeClosed(column - 4, column + 4)
                .mapToObj(c -> new Point2D(c, row))
                .collect(Collectors.toList());
        Point2D topLeft = new Point2D(column - 4, row - 4);
        List<Point2D> topDiagonal = IntStream.rangeClosed(0, 8)
                .mapToObj(i -> topLeft.add(i, i))
                .collect(Collectors.toList());
        Point2D bottomLeft = new Point2D(column - 4, row + 4);
        List<Point2D> botDiagonal = IntStream.rangeClosed(0, 8)
                .mapToObj(i -> bottomLeft.add(i, -i))
                .collect(Collectors.toList());
    
        return checkRange(vertical) || checkRange(horizontal) || checkRange(topDiagonal) || checkRange(botDiagonal);
    }

    private void placeDisc(Disc disc, int column) {
    int row = ROWS - 1;
    do {
        if (!getDisc(column, row).isPresent()) {
            break;
        }
        row--;
    } while (row >= 0);

    grid[column][row] = disc;
    discRoot.getChildren().add(disc);
    disc.setTranslateX(column * (TILE_SIZE + 5) + 20);
    disc.setTranslateY(-TILE_SIZE);

    Timeline timeline = new Timeline();
    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), new KeyValue(disc.translateYProperty(), row * (TILE_SIZE + 5) + 20)));
    timeline.play();

    if (gameEnded(column, row)) {
        gameOver();
    } else if (isTie()) {
        tieGame();
    }

    redMove = !redMove;
}

    private boolean checkRange(List<Point2D> list) {
        int chain = 0;
        for (Point2D point: list) {
            int row = (int) point.getY();
            int column = (int) point.getX();

            Disc disc=getDisc(column, row).orElse(new Disc(!redMove));
            if (disc.red == redMove) {
                chain++;
                if (chain == 5) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }

    private void gameOver() {
    System.out.println(redMove ? "Red wins!" : "Yellow wins!");

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText(redMove ? "Red wins!" : "Yellow wins!");
    alert.setContentText("Do you want to play again?");

    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
        resetBoard();
    } else {
        controllerC5 cC5 = new controllerC5();
        cC5.stopBackgroundMusic();
        stage.close();
        App.getStage().show();
    }
}

    private boolean isTie() {
        for (int column = 0; column < COLUMNS; column++) {
            for (int row = 0; row < ROWS; row++) {
                if (getDisc(column, row).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void tieGame() {
        System.out.println("It's a tie!");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("It's a tie!");
        alert.setContentText("Do you want to play again?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            resetBoard();
        } else {
            stage.close();
            App.getStage().show();
        }
    }



    private void resetBoard() {

        for (int column = 0; column < COLUMNS; column++) {
            for (int row = 0; row < ROWS; row++) {
                grid[column][row] = null;
            }
        }
        
        discRoot.getChildren().clear();
        redMove = true;
    }

    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) {
            return Optional.empty();
        }

        return Optional.ofNullable(grid[column][row]);
    }

    private static class Disc extends Circle {
        private final boolean red;
        public Disc(boolean red){
            super(TILE_SIZE / 2, red ? Color.RED : Color.YELLOW);
            this.red = red;

            setCenterX(TILE_SIZE / 2);
            setCenterY(TILE_SIZE / 2);
        }

    }

    public void openConnect5() throws IOException {

        controllerC5 cC5 = new controllerC5();
        cC5.playBackgroundMusic();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("connect5.fxml"));
        Parent root = loader.load();
        scene = new Scene(root, 1280, 750);
        stage = new Stage();
        scene = new Scene(createContent());
        stage.setScene(scene);
        stage.setTitle("Connect 5: Bonanza");
        stage.getIcons().add(new Image(getClass().getResource("c5Logo.png").toExternalForm()));
        resetBoard();
        stage.show();


        stage.setOnCloseRequest(event -> {
            cC5.stopBackgroundMusic();
            System.out.println("Application closed");
            App.getStage().show();
        });

    }

}
