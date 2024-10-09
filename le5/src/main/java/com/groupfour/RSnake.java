package com.groupfour;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RSnake {
    private int speed = 8;
    private int foodColor = 0;
    private int width = 20;
    private int height = 20;
    private int foodX = 0;
    private int foodY = 0;
    private int cornerSize = 25;
    private List<Corner> snake = new ArrayList<>();
    private Dir direction = Dir.left;
    private boolean gameOver = false;
    private Random rand = new Random();
    private Canvas canvas;
    private GraphicsContext gc;
    private boolean displayFeverDownMessage = false;
    private long feverDownMessageStartTime;
    private String price;
    private List<Color> foodColors = new ArrayList<>();
    private List<Integer> foodProbabilities = new ArrayList<>();
    private int foodPoints = 0;
    private boolean[] isPurchased = new boolean[4];
    private Thread gameThread;
    private boolean isPaused = false;
    private long lastFoodEatenTime = System.currentTimeMillis();
    private Text scoreLabel;
    private volatile boolean running = true;

    Stage snakeStage = new Stage();

    public enum Dir {
        left, right, up, down
    }

    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    public boolean getgameOver() {
        return gameOver;
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCornerSize() {
        return cornerSize;
    }

    public RSnake() {
        canvas = new Canvas(width * cornerSize, height * cornerSize);
        gc = canvas.getGraphicsContext2D();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        foodColors.add(Color.RED);
        foodProbabilities.add(100);
        isPurchased = new boolean[4];
        for (int i = 0; i < 4; i++) {
            isPurchased[i] = false;
        }
    }

    public void startSnakeGame() {
        running = true;
        try {
            Stage snakeStage = new Stage();
            BorderPane snakeBorderPane = new BorderPane();

            Button returnBtn = new Button("Return to Main Menu");
            Button restartBtn = new Button("Restart game");
            Button shopBtn = new Button("Shop");
            HBox controlBox = new HBox();
            returnBtn.setOnAction(e -> {
                snakeStage.close();
                App.getStage().show();
                stopGame();
            });
            restartBtn.setOnAction(e -> {
                restart();
            });
            shopBtn.setOnAction(e -> {
                stopGame();
                snakeStage.close();
                try {
                    shop();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            snakeBorderPane.setCenter(getCanvas());
            controlBox.getChildren().addAll(returnBtn, restartBtn, shopBtn);
            snakeBorderPane.setBottom(controlBox);
            
            scoreLabel = new Text("Score: " + foodPoints);
            scoreLabel.setFont(new Font("Century Gothic", 30));
            scoreLabel.setFill(Color.RED);
            snakeBorderPane.setTop(scoreLabel);
            BorderPane.setAlignment(scoreLabel, Pos.TOP_LEFT);

            Scene scene = new Scene(snakeBorderPane, getWidth() * getCornerSize(), getHeight() * getCornerSize() + 100);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, getKeyEventHandler());
            snakeStage.setScene(scene);
            snakeStage.setTitle("The Rogue Snake");
            snakeStage.show();
            App.getStage().hide();
            startGame();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startGame() {
        gameThread = new Thread(() -> {
            while (running) {
                try {
                    if (!isPaused) {
                        updateGameState();
                    }
                    Platform.runLater(this::draw);
                    Thread.sleep(1000 / speed);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        gameThread.start();
    }
    //pause not yet implemented, idk if i should even implement it
    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void stopGame() {
        running = false;
    }
    
    public void updateGameState() {

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        // game over if snake touches border
        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > height - 1) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > width - 1) {
                    gameOver = true;
                }
                break;
        }
        // Let snake grow if it has eaten food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            lastFoodEatenTime = System.currentTimeMillis();
            snake.add(new Corner(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y));
            int previousFoodColor = foodColor;
            newFood();
            if (foodColors.get(previousFoodColor) == Color.BLACK) {
                foodPoints += 10;
                snake.add(new Corner(snake.get(snake.size() - 4).x, snake.get(snake.size() - 4).y));
            } else if (foodColors.get(previousFoodColor) == Color.VIOLET) {
                foodPoints += 5;
            } else if (foodColors.get(previousFoodColor) == Color.GREEN) {
                foodPoints += 3;
            } else if (foodColors.get(previousFoodColor) == Color.BLUE) {
                foodPoints += 2;
            } else if (foodColors.get(previousFoodColor) == Color.RED) {
                foodPoints++;
            }
            speed++;
            updateScoreLabel(scoreLabel);
        }
        // Game over if snake hits itself
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }
        checkSpeed();
    }
    //lowers speed if 8 seconds has passed without a fruit being eaten
    public void checkSpeed() {
        if (gameOver) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        long timeSinceLastFoodEaten = currentTime - lastFoodEatenTime;
        if (timeSinceLastFoodEaten > 8000) {
            speed -= 2;
            lastFoodEatenTime = currentTime;
            System.out.println("Speed lowered"); //just a checker
            displayFeverDownMessage = true;
            feverDownMessageStartTime = currentTime;
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    //key event handler, might add wasd if needed
    public EventHandler<KeyEvent> getKeyEventHandler() {
        return key -> {
            if (key.getCode() == KeyCode.UP && direction != Dir.down) {
                direction = Dir.up;
            }
            if (key.getCode() == KeyCode.LEFT && direction != Dir.right) {
                direction = Dir.left;
            }
            if (key.getCode() == KeyCode.DOWN && direction != Dir.up) {
                direction = Dir.down;
            }
            if (key.getCode() == KeyCode.RIGHT && direction != Dir.left) {
                direction = Dir.right;
            }
        };
    }

    //draws needed elements
    public void draw() {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Impact", 50));
            gc.fillText("GAME OVER", 130, 250);
            return;
        }
        // background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width * cornerSize, height * cornerSize);

        // score

        gc.setFill(foodColors.get(foodColor));
        gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize);

		// snake
		for (Corner c : snake) {
			gc.setFill(Color.LIGHTGREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 1, cornerSize - 1);
			gc.setFill(Color.GREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 2, cornerSize - 2);

		}

        if (displayFeverDownMessage) {
            gc.setFill(Color.ORANGE);
            gc.setFont(new Font("Impact", 40));
            gc.fillText("FEVER DOWN!!", 130, 50);
            if (System.currentTimeMillis() - feverDownMessageStartTime > 2000) {
                displayFeverDownMessage = false;
            }
        }
    }

    //makes new food everytime a snake eats food
	public void newFood() {
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);
        
            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
        
            int totalProbability = 0;
            for (int probability : foodProbabilities) {
                totalProbability += probability;
            }
            int randomProbability = rand.nextInt(totalProbability);
            int cumulativeProbability = 0;
            for (int i = 0; i < foodProbabilities.size(); i++) {
                cumulativeProbability += foodProbabilities.get(i);
                if (randomProbability < cumulativeProbability) {
                    foodColor = i;
                    break;
                }
            }
            break;
        }
    }

    
    public void restart() {
        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        direction = Dir.left;
        if (speed > 8 || speed < 8) {
            speed = 8;
        }
        gameOver = false;
        newFood();
        lastFoodEatenTime = System.currentTimeMillis();
    }

    public void shop() throws IOException {
        snakeStage.setTitle("Store");
        VBox root = new VBox();
        Scene scene = new Scene(root, 480, 360);
        snakeStage.setScene(scene);
        snakeStage.show();

        HBox shopName = new HBox();
        shopName.setMinHeight(50);
        shopName.setAlignment(Pos.CENTER);
        Text text = new Text("Shop\nFood points: " + foodPoints);
        text.getStyleClass().add("title");
        shopName.getChildren().add(text);

        Button shopReturn = new Button("Return to game");
        shopReturn.setOnAction(e -> {
            stopGame();
            snakeStage.close();
            startSnakeGame();
        });
        GridPane shopBox = new GridPane();
        shopBox.setGridLinesVisible(true); //remove in the future
        shopBox.setHgap(20);
        shopBox.setVgap(20);
        shopBox.setAlignment(Pos.CENTER);

        //Json components
        JsonNode shopNode = new ObjectMapper().readTree(getClass().getResourceAsStream("shop.json"));
        if (shopNode.isArray()) {
            int row = 0;
            int column = 0;
            for (JsonNode element : shopNode) {
                String name = element.get("itemName").asText();
                price = element.get("price").asText();
                String description = element.get("description").asText();
                String imageUrl = element.has("itemImage") ? element.get("itemImage").asText() : "";
                String placeholderUrl = element.has("placeholder_image") ? element.get("placeholder_image").asText() : "";

                VBox shopBoxElement = new VBox();
                shopBoxElement.setMinWidth(146.6666666666667);
                shopBoxElement.setAlignment(Pos.CENTER);
                shopBoxElement.setOnMouseEntered(e -> shopBoxElement.setCursor(Cursor.HAND));
                shopBoxElement.setOnMouseExited(e -> shopBoxElement.setCursor(Cursor.DEFAULT));
                Label item = new Label(name);
                Label itemPrice = new Label(price + " Food point");
                Label itemDescription = new Label(description);

                ImageView shopImage = new ImageView();

                //exception handler image
                if (!imageUrl.isEmpty()) {
                    try {
                        shopImage = new ImageView(new Image(getClass().getResource(imageUrl).toExternalForm()));
                    } catch (NullPointerException e) {
                        shopImage = new ImageView(new Image(getClass().getResource(placeholderUrl).toExternalForm()));
                    }
                }

                shopImage.setFitWidth(60);
                shopImage.setFitHeight(60);
                shopBoxElement.getChildren().addAll(item, shopImage, itemPrice, itemDescription);

                GridPane.setConstraints(shopBoxElement, column, row);
                shopBox.getChildren().add(shopBoxElement);

                column++;
                if (column > 2) {
                    column = 0;
                    row++;
                }
            }
        } else {
            System.out.println("Error: JSON data is not an array");
        }
        root.getChildren().addAll(shopName, shopBox, shopReturn);

        shopBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                Node node = (Node) e.getTarget();
                if (node instanceof ImageView || node instanceof Label) {
                    node = node.getParent();
                }
                if (node != null && GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                    int columnIndex = GridPane.getColumnIndex(node);
                    int rowIndex = GridPane.getRowIndex(node);
                    int itemIndex = columnIndex + rowIndex * 3;
                    JsonNode element = shopNode.get(itemIndex);
                    int priceInt = Integer.parseInt(element.get("price").asText());
                    if (columnIndex == 0 && rowIndex == 0) {
                        if (isPurchased[itemIndex]) {
                            System.out.println("Already purchased");
                        } else {
                            if (priceInt <= foodPoints) {
                                isPurchased[itemIndex] = true;
                                foodPoints -= priceInt;
                                updateShopLabel(text);
                                foodColors.add(Color.BLUE);
                                foodProbabilities.add(100);
                            } else {
                                System.out.println("Cant purchase");
                            }
                        }

                    } else if (columnIndex == 1 && rowIndex == 0) {
                        if (isPurchased[itemIndex]) {
                            System.out.println("Already purchased");
                        } else {
                            if (priceInt <= foodPoints) {
                                isPurchased[itemIndex] = true;
                                foodPoints -= priceInt;
                                updateShopLabel(text);
                                foodColors.add(Color.GREEN);
                                foodProbabilities.add(100);
                            } else {
                                System.out.println("Cant purchase");
                            }
                        }
                    } else if (columnIndex == 2 && rowIndex == 0) {
                        if (isPurchased[itemIndex]) {
                            System.out.println("Already purchased");
                        } else {
                            if (priceInt <= foodPoints) {
                                isPurchased[itemIndex] = true;
                                foodPoints -= priceInt;
                                updateShopLabel(text);
                                foodColors.add(Color.VIOLET);
                                foodProbabilities.add(100);
                            } else {
                                System.out.println("Cant purchase");
                            }
                        }
                        
                    } else if (columnIndex == 0 && rowIndex == 1) {
                        if (isPurchased[itemIndex]) {
                            System.out.println("Already purchased");
                        } else {
                            if (priceInt <= foodPoints) {
                                isPurchased[itemIndex] = true;
                                foodPoints -= priceInt;
                                updateShopLabel(text);
                                foodColors.add(Color.WHITE);
                                foodProbabilities.add(100);
                            } else {
                                System.out.println("Cant purchase");
                            }
                        }
                    }
                } else {
                    System.out.println("Unclickable");
                }
            });
    }
    public void updateShopLabel(Text text) {
        text.setText("Shop\nFood points: " + foodPoints);
    }
    public void updateScoreLabel(Text text) {
        text.setText("Score: " + foodPoints);
    }
    //Goofy ahh loading screen
    public void displayLoadingScreen() {
        Stage loadingStage = new Stage();
        loadingStage.initOwner(App.getStage());
        loadingStage.setTitle("Loading...");

        BorderPane loadingPane = new BorderPane();
        loadingPane.setPrefSize(300, 200);

        Label loadingLabel = new Label("Loading The Rogue Snake...");
        loadingLabel.setFont(new Font("Century Gothic", 20));
        loadingPane.setCenter(loadingLabel);

        Scene loadingScene = new Scene(loadingPane);
        loadingStage.setScene(loadingScene);
        loadingStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), e -> {
            loadingStage.close();
            startSnakeGame();
        }));
        timeline.play();
    }
}