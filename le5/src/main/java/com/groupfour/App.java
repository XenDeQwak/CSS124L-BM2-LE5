package com.groupfour;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;



public class App extends Application {


    static Scene scene;
    private static Stage stage;
    


    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;
        VBox loadingRoot = new VBox();
        loadingRoot.setAlignment(Pos.CENTER);
        Scene loadingScene = new Scene(loadingRoot, 640, 480);

        // Add a loading text to the loading scene
        Text loadingText = new Text("Loading...");
        loadingRoot.getChildren().add(loadingText);

        // Create a loading animation
        Timeline loadingTimeline = new Timeline();
        KeyValue loadingKeyValue = new KeyValue(loadingText.translateYProperty(), -5);
        KeyFrame loadingKeyFrame = new KeyFrame(Duration.millis(800), loadingKeyValue);
        loadingTimeline.getKeyFrames().add(loadingKeyFrame);
        loadingTimeline.setAutoReverse(true);
        loadingTimeline.setCycleCount(Timeline.INDEFINITE);
        loadingTimeline.play();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(20);
                }
                return null;
            }
        };

        // Create a progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(task.progressProperty());
        loadingRoot.getChildren().add(progressBar);
        progressBar.getStyleClass().add("progress-bar");

        // Start the task
        new Thread(task).start();
        stage.setScene(loadingScene);
        stage.show();

        Timeline timer = new Timeline();
        KeyValue timerKeyValue = new KeyValue(loadingText.opacityProperty(), 0);
        KeyFrame timerKeyFrame = new KeyFrame(Duration.seconds(2), timerKeyValue);
        timer.getKeyFrames().add(timerKeyFrame);

        stage.setOnCloseRequest(e -> {
            if (task.isRunning()) {
                task.cancel();
            }
        });
        task.setOnSucceeded (e -> {

            try {
                // Instantiate games
                RSnake rSnake = new RSnake();
                connect5 open = new connect5();
                
                VBox root = new VBox();
                scene = new Scene(root, 640, 480);
                stage.setScene(scene);
                stage.setTitle("Three For Three");
                stage.show();
                root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

                //UI Elements
                HBox titleBox = new HBox();
                titleBox.setAlignment(Pos.CENTER);
                titleBox.getStyleClass().add("titlebox");
    
                Text text = new Text("Three for Three");
                text.getStyleClass().add("title");
                titleBox.getChildren().add(text);
    
                Timeline titleTimeline = new Timeline();
                KeyValue titleKeyValue = new KeyValue(text.translateYProperty(), -5);
                KeyFrame titleKeyFrame = new KeyFrame(Duration.millis(800), titleKeyValue);
                titleTimeline.getKeyFrames().add(titleKeyFrame);
                titleTimeline.setAutoReverse(true);
                titleTimeline.setCycleCount(Timeline.INDEFINITE);
                titleTimeline.play();
    
                GridPane gameBox = new GridPane();
                gameBox.setHgap(20);
                gameBox.setVgap(20);
                gameBox.setAlignment(Pos.CENTER);
    
    
    
                //Json components
                JsonNode frontPageNode = new ObjectMapper().readTree(getClass().getResourceAsStream("fpData.json"));
                if (frontPageNode.isArray()) {
                    int row = 0;
                    int column = 0;
                    for (JsonNode element : frontPageNode) {
                        String title = element.get("gameTitle").asText();
                        String imageUrl = element.has("gameImage") ? element.get("gameImage").asText() : "";
                        String placeholderUrl = element.has("placeholder_image") ? element.get("placeholder_image").asText() : "";
    
                        VBox gameBoxElement = new VBox();
                        gameBoxElement.getStyleClass().add("game-cell");
    
                        gameBoxElement.setAlignment(Pos.CENTER);
                        gameBoxElement.setOnMouseEntered(e1 -> {
                            gameBoxElement.setCursor(Cursor.HAND);
    
                            // Create a Timeline animation
                            Timeline timeline = new Timeline();
                            KeyValue keyValueX = new KeyValue(gameBoxElement.scaleXProperty(), 1.2);
                            KeyValue keyValueY = new KeyValue(gameBoxElement.scaleYProperty(), 1.2);
                            KeyFrame keyFrame = new KeyFrame(Duration.millis(100), keyValueX, keyValueY);
                            timeline.getKeyFrames().add(keyFrame);
                            timeline.play();
                        });
    
                        gameBoxElement.setOnMouseExited(e2 -> {
                            gameBoxElement.setCursor(Cursor.DEFAULT);
    
                            // Create a Timeline animation
                            Timeline timeline = new Timeline();
                            KeyValue keyValueX = new KeyValue(gameBoxElement.scaleXProperty(), 1);
                            KeyValue keyValueY = new KeyValue(gameBoxElement.scaleYProperty(), 1);
                            KeyFrame keyFrame = new KeyFrame(Duration.millis(100), keyValueX, keyValueY);
                            timeline.getKeyFrames().add(keyFrame);
                            timeline.play();
                        });
                        Label gameTitle = new Label(title);
                        gameTitle.getStyleClass().add("gameTitle");
                        ImageView gameImage = new ImageView();
                        
    
                        //exception handler image
                        if (!imageUrl.isEmpty()) {
                            try {
                                gameImage = new ImageView(new Image(getClass().getResource(imageUrl).toExternalForm()));
                            } catch (NullPointerException e3) {
                                System.out.println("File does not exist"); //checker, remove in the future
                                gameImage = new ImageView(new Image(getClass().getResource(placeholderUrl).toExternalForm()));
                            }
                        }
    
                        gameImage.setFitWidth(150);
                        gameImage.setFitHeight(150);
                        gameBoxElement.getChildren().addAll(gameImage, gameTitle);
    
                        GridPane.setConstraints(gameBoxElement, column, row);
                        gameBox.getChildren().add(gameBoxElement);
    
                        column++;
                        if (column > 2) {
                            column = 0;
                            row++;
                        }
                    }
                } else {
                    System.out.println("Error: JSON data is not an array");
                }
                
                root.getChildren().addAll(titleBox, gameBox);
                
    
    
                //When a cell gets clicked, this happens
                gameBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e4 -> {
                    Node node = (Node) e4.getTarget();
                    if (node instanceof ImageView || node instanceof Label) {
                        node = node.getParent();
                    }
                    if (node != null && GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                        int columnIndex = GridPane.getColumnIndex(node);
                        int rowIndex = GridPane.getRowIndex(node);
    
                        if (columnIndex == 0 && rowIndex == 0) {
                            rSnake.startSnakeGame();
                        } else if (columnIndex == 1 && rowIndex == 0) {
                            open.openConnect5();
                            System.out.println("Connect Five");
                        } 
                        
                        else if (columnIndex == 2 && rowIndex == 0) {
                            System.out.println("2048");
                            game_2048 game = new game_2048();
                        }
                    } else {
                        System.out.println("No games");
                    }
                });
    
    
    
            } catch (IOException e5) {
                System.err.println("Error");
                e5.printStackTrace();
            }


        });
timer.play();
    }
    


    public static void main(String[] args) {
        launch();
    }


    
    public static Stage getStage() {
        return stage;
    }

}   