package com.groupfour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;



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
    private Text fpLabel;
    private volatile boolean running = true;
    private List<Corner> bombs = new ArrayList<>();
    private int bombProbability = 100;
    private long lastBombSpawnTime = System.currentTimeMillis();
    private Color bombColor = Color.GRAY;
    private List<Long> bombSpawnTimes = new ArrayList<>();
    private int bombDuration = 15000;
    private final Lock lock = new ReentrantLock();
    private final Condition pauseCondition = lock.newCondition();
    private String databaseUrl;
    private boolean hasUpdated = true;
    private Text scoreLabel;
    private int score = 0;
    private boolean directionChangePending = false;
    

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
        databaseUrl = "https://rogue-snake-leaderboard-default-rtdb.asia-southeast1.firebasedatabase.app/";

    }

    public void startSnakeGame() {
        running = true;
        try {
            Stage snakeStage = new Stage();
            BorderPane snakeBorderPane = new BorderPane();

            Button returnBtn = new Button("Return to Main Menu");
            Button restartBtn = new Button("Restart game");
            Button shopBtn = new Button("Shop");
            Button ldbBtn = new Button("Leaderboards");
            returnBtn.setFocusTraversable(false);
            restartBtn.setFocusTraversable(false);
            shopBtn.setFocusTraversable(false);
            ldbBtn.setFocusTraversable(false);
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
            ldbBtn.setOnAction(e -> {
                try {
                    Stage leaderboardStage = new Stage();
                    leaderboardStage.setTitle("Leaderboard");
                    VBox root = new VBox();
                    root.setSpacing(10);
                    root.setPadding(new Insets(10));
                    Scene scene = new Scene(root, 300, 400);
                    leaderboardStage.setScene(scene);
                    leaderboardStage.show();

                    Text leaderboardTitle = new Text("Leaderboard");
                    leaderboardTitle.setFont(new Font("Arial", 24));
                    VBox leaderboardVbox = new VBox(leaderboardTitle);
                    leaderboardVbox.setAlignment(Pos.CENTER);
                    root.getChildren().add(leaderboardVbox);

                    Text leaderboardText = new Text();
                    leaderboardText.setFont(new Font("Arial", 18));
                    root.getChildren().add(leaderboardText);

                    displayLeaderboard(leaderboardText);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            snakeBorderPane.setCenter(getCanvas());
            controlBox.getChildren().addAll(returnBtn, restartBtn, shopBtn, ldbBtn);
            snakeBorderPane.setBottom(controlBox);
            
            VBox numInfo = new VBox();
            fpLabel = new Text("Food points: " + foodPoints);
            scoreLabel = new Text("Score: " + score);
            fpLabel.setFont(new Font("Century Gothic", 20));
            fpLabel.setFill(Color.RED);
            numInfo.getChildren().addAll(fpLabel, scoreLabel);
            scoreLabel.setFont(Font.font("Century Gothic", FontWeight.BOLD, 20));
            scoreLabel.setFill(Color.YELLOWGREEN);
            snakeBorderPane.setTop(numInfo);
            BorderPane.setAlignment(fpLabel, Pos.TOP_LEFT);

            Scene scene = new Scene(snakeBorderPane, getWidth() * getCornerSize(), getHeight() * getCornerSize() + 100);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, getKeyEventHandler());
            snakeStage.setScene(scene);
            snakeStage.setTitle("The Rogue Snake");
            snakeStage.show();
            App.getStage().hide();
            startGame();
            pauseGame();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startGame() {
        gameThread = new Thread(() -> {
            while (running) {
                try {
                    lock.lock();
                    try {
                        while (isPaused) {
                            pauseCondition.await();
                        }
                    } finally {
                        lock.unlock();
                    }
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
    
    public void pauseGame() {
        if (!isPaused) {
            isPaused = true;
            Platform.runLater(this::draw);
        }
    }

    public void resumeGame() {
        isPaused = false;
        lock.lock();
        try {
            pauseCondition.signal();
        } finally {
            lock.unlock();
        }
        Platform.runLater(this::draw);
    }

    public void stopGame() {
        running = false;
    }
    
    public void updateGameState() {
        bombs();
    
        // update body positions
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }
    
        // update head position
        int newX = snake.get(0).x;
        int newY = snake.get(0).y;
    
        switch (direction) {
            case up:
                newY--;
                break;
            case down:
                newY++;
                break;
            case left:
                newX--;
                break;
            case right:
                newX++;
                break;
        }
    
        snake.get(0).x = newX;
        snake.get(0).y = newY;

        // Let snake grow if it has eaten food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            lastFoodEatenTime = System.currentTimeMillis();
            snake.add(new Corner(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y));
            int previousFoodColor = foodColor;
            newFood();
            bombProbability++;
            if (foodColors.get(previousFoodColor) == Color.WHITE) {
                foodPoints += 10;
                score += 100;
                snake.add(new Corner(snake.get(snake.size() - 4).x, snake.get(snake.size() - 4).y));
            } else if (foodColors.get(previousFoodColor) == Color.VIOLET) {
                foodPoints += 5;
                score += 50;
            } else if (foodColors.get(previousFoodColor) == Color.GREEN) {
                foodPoints += 3;
                score += 30;
            } else if (foodColors.get(previousFoodColor) == Color.BLUE) {
                foodPoints += 2;
                score += 20;
            } else if (foodColors.get(previousFoodColor) == Color.RED) {
                foodPoints++;
                score += 10;
            }
            speed++;
            updatefpLabel(fpLabel);
            updateScoreLabel(scoreLabel);
        }
    
        // game over if snake touches border
        if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
            gameOver = true;
        }
    
        // Game over if snake hits itself
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }
    
        checkSpeed();
        for (Corner bomb : bombs) {
            if (snake.get(0).x == bomb.x && snake.get(0).y == bomb.y) {
                gameOver = true;
            }
        }
        long currentTime = System.currentTimeMillis();
        for (int i = bombs.size() - 1; i >= 0; i--) {
            if (currentTime - bombSpawnTimes.get(i) > bombDuration) {
                bombs.remove(i);
                bombSpawnTimes.remove(i);
            } else {
                break;
            }
        }
        if (gameOver) {
            if (hasUpdated) {
                updateScore();
                hasUpdated = false;
            }
        } else {
            hasUpdated = true;
        }
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
            if (directionChangePending) {
                return;
            }
            directionChangePending = true;
            Dir newDirection = null;
            if (key.getCode() == KeyCode.UP) {
                newDirection = Dir.up;
            }
            if (key.getCode() == KeyCode.DOWN) {
                newDirection = Dir.down;
            }
            if (key.getCode() == KeyCode.LEFT) {
                newDirection = Dir.left;
            }
            if (key.getCode() == KeyCode.RIGHT) {
                newDirection = Dir.right;
            }
            if (newDirection != null) {
                int newX = snake.get(0).x;
                int newY = snake.get(0).y;
                switch (newDirection) {
                    case up:
                        newY--;
                        break;
                    case down:
                        newY++;
                        break;
                    case left:
                        newX--;
                        break;
                    case right:
                        newX++;
                        break;
                }
                boolean collision = false;
                for (int i = 1; i < snake.size(); i++) {
                    if (snake.get(i).x == newX && snake.get(i).y == newY) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
                    direction = newDirection;
                }
            }
            if (key.getCode() == KeyCode.R) {
                resumeGame();
            }
            directionChangePending = false;
        };
    }

    //draws needed elements
    public void draw() {
        lock.lock();
        try {
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
    
            gc.setFill(bombColor);
            for (Corner bomb : bombs) {
                gc.fillOval(bomb.x * cornerSize, bomb.y * cornerSize, cornerSize, cornerSize);
            }
    
            if (isPaused) {
                gc.setFill(Color.RED);
                gc.setFont(new Font("Impact", 40));
                gc.fillText("GAME PAUSED", 130, 50);
                gc.fillText("Press R to resume", 100, 90);
            }
        } finally {
            lock.unlock();
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
        bombs.clear(); 
        bombSpawnTimes.clear();
        lastBombSpawnTime = System.currentTimeMillis();
        lastFoodEatenTime = System.currentTimeMillis();
        score = 0;
        updateScoreLabel(scoreLabel);
    }

    public void shop() throws IOException {
        pauseGame();
        snakeStage.setTitle("Store");
        VBox root = new VBox();
        Scene scene = new Scene(root, 600, 400);
        snakeStage.setScene(scene);
        snakeStage.show();

        HBox shopName = new HBox();
        shopName.setMinHeight(50);
        shopName.setAlignment(Pos.CENTER);
        Text text = new Text("Shop\nFood points: " + foodPoints + "\nFood");
        text.getStyleClass().add("title");
        shopName.getChildren().add(text);

        HBox snakeName = new HBox();
        Text snakeText = new Text("Snakes");
        snakeName.setMinHeight(50);
        snakeName.setAlignment(Pos.CENTER);
        snakeName.getChildren().add(snakeText);

        Button shopReturn = new Button("Return to game");
        shopReturn.setOnAction(e -> {
            stopGame();
            snakeStage.close();
            startSnakeGame();
        });
        HBox buttonLoc = new HBox(shopReturn);
        buttonLoc.setAlignment(Pos.CENTER);
        buttonLoc.setMinHeight(50);

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

        GridPane snakeBox = new GridPane();
        snakeBox.setGridLinesVisible(true); //remove in the future
        snakeBox.setHgap(20);
        snakeBox.setVgap(20);
        snakeBox.setAlignment(Pos.CENTER);

        //Json components
        JsonNode snakeNode = new ObjectMapper().readTree(getClass().getResourceAsStream("snake.json"));
        if (snakeNode.isArray()) {
            int row = 0;
            int column = 0;
            for (JsonNode element : snakeNode) {
                String snakeTitle = element.get("snakeName").asText();
                price = element.get("price").asText();
                String description = element.get("description").asText();
                String imageUrl = element.has("snakeImage") ? element.get("snakeImage").asText() : "";
                String placeholderUrl = element.has("placeholder_image") ? element.get("placeholder_image").asText() : "";

                VBox snakeBoxElement = new VBox();
                snakeBoxElement.setMinWidth(146.6666666666667);
                snakeBoxElement.setAlignment(Pos.CENTER);
                Label snake = new Label(snakeTitle);
                Label snakePrice = new Label(price + " coins");
                Label snakeDescription = new Label(description);
                Button use = new Button("Use");
                use.setOnMouseEntered(e -> use.setCursor(Cursor.HAND));
                use.setOnMouseExited(e -> use.setCursor(Cursor.DEFAULT));

                ImageView snakeImage = new ImageView();

                //exception handler image
                if (!imageUrl.isEmpty()) {
                    try {
                        snakeImage = new ImageView(new Image(getClass().getResource(imageUrl).toExternalForm()));
                    } catch (NullPointerException e) {
                        snakeImage = new ImageView(new Image(getClass().getResource(placeholderUrl).toExternalForm()));
                    }
                }

                snakeImage.setFitWidth(50);
                snakeImage.setFitHeight(50);
                snakeBoxElement.getChildren().addAll(snake, snakeImage, snakePrice, snakeDescription, use);

                GridPane.setConstraints(snakeBoxElement, column, row);
                snakeBox.getChildren().add(snakeBoxElement);

                column++;
                if (column > 2) {
                    column = 0;
                    row++;
                }
            }
        } else {
            System.out.println("Error: JSON data is not an array");
        }

        VBox vbox = new VBox(shopName, shopBox, snakeName, snakeBox, buttonLoc);
        vbox.setAlignment(Pos.CENTER);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vbox);
        scrollPane.setFitToWidth(true);
        root.getChildren().addAll(scrollPane);


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
    public void updatefpLabel(Text text) {
        text.setText("Food points: " + foodPoints);
    }
    public void updateScoreLabel(Text text) {
        text.setText("Score: " + score);
    }

    public void updateScore() {
        try {
            URL url = new URL(databaseUrl + "/scores.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder leaderboard = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    leaderboard.append(line);
                }
                reader.close();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(leaderboard.toString());

                int currentScore = score;
                boolean isHighScore = true;

                for (JsonNode node : jsonNode) {
                    int score = node.get("score").asInt();
                    if (score >= currentScore) {
                        isHighScore = false;
                        break;
                    }
                }

                if (isHighScore) {
                    Platform.runLater(() -> {
                    String playerName = getPlayerName();
                    updateScoreWithPlayerName(playerName);
                    });
                }
            } else {
                System.out.println("Error updating score: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error updating score: " + e.getMessage());
        }
    }

    public String getPlayerName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New High Score!");
        dialog.setHeaderText("Congratulations, you got a new high score!");
        dialog.setContentText("Please enter your name:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse("Anonymous");
    }

    public void updateScoreWithPlayerName(String playerName) {
        try {
            URL url = new URL(databaseUrl + "/scores.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String json = "{\"name\": \"" + playerName + "\", \"score\": " + score + "}";
            connection.getOutputStream().write(json.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Score updated successfully!");
            } else {
                System.out.println("Error updating score: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error updating score: " + e.getMessage());
        }
    }

    public void displayLeaderboard(Text leaderboardText) {
        try {
            URL url = new URL(databaseUrl + "/scores.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
    
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder leaderboard = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    leaderboard.append(line);
                }
                reader.close();
    
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(leaderboard.toString());
    
                // Sort the scores in descending order
                List<JsonNode> scores = new ArrayList<>();
                jsonNode.fieldNames().forEachRemaining(fieldName -> scores.add(jsonNode.get(fieldName)));
                scores.sort((a, b) -> b.get("score").asInt() - a.get("score").asInt());
    
                StringBuilder scoresString = new StringBuilder();
                for (JsonNode node : scores) {
                    String name = node.get("name").asText();
                    int score = node.get("score").asInt();
                    scoresString.append(name).append(": ").append(score).append("\n");
                }
    
                leaderboardText.setText(scoresString.toString());
            } else {
                System.out.println("Error displaying leaderboard: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error displaying leaderboard: " + e.getMessage());
        }
    }
    public void bombs() {
        long currentTime = System.currentTimeMillis();
        for (int i = bombs.size() - 1; i >= 0; i--) {
            if (currentTime - bombSpawnTimes.get(i) > bombDuration) {
                bombs.remove(i);
                bombSpawnTimes.remove(i);
            }
        }
        if (currentTime - lastBombSpawnTime > 5000) {
            lastBombSpawnTime = currentTime;
            if (rand.nextInt(100) < bombProbability) {
                spawnBomb();
            }
        }
    }

    public void spawnBomb() {
        int bombX = rand.nextInt(width);
        int bombY = rand.nextInt(height);
    
        boolean isOccupied = false;
        //so that bomb doesnt spawn on snake
        for (Corner c : snake) {
            if (c.x == bombX && c.y == bombY) {
                isOccupied = true;
                break;
            }
        }
    
        if (bombX == foodX && bombY == foodY) {
            isOccupied = true;
        }
    
        if (!isOccupied) {
            bombs.add(new Corner(bombX, bombY));
            bombSpawnTimes.add(System.currentTimeMillis());
        }
    }
}
