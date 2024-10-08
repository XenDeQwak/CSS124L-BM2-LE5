package com.groupfour;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSnake {
    private int speed = 5;
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
    private AnimationTimer animationTimer;

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
    }

    public void startSnakeGame() {
        try {
            RSnake rSnake = new RSnake();
            Stage snakeStage = new Stage();
            BorderPane snakeBorderPane = new BorderPane();

            Button returnBtn = new Button("Return to Main Menu");
            Button restartBtn = new Button("Restart game");
            HBox controlBox = new HBox();
            returnBtn.setOnAction(e -> {
                snakeStage.close();
                App.getStage().show();
            });
            restartBtn.setOnAction(e -> {
                rSnake.restart();
            });
            snakeBorderPane.setCenter(rSnake.getCanvas());
            controlBox.getChildren().addAll(returnBtn, restartBtn);
            snakeBorderPane.setBottom(controlBox);
            Scene scene = new Scene(snakeBorderPane, rSnake.getWidth() * rSnake.getCornerSize(), rSnake.getHeight() * rSnake.getCornerSize() + 50);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, rSnake.getKeyEventHandler());
            snakeStage.setScene(scene);
            snakeStage.setTitle("The Rogue Snake");
            snakeStage.show();
            App.getStage().hide();
            rSnake.startGame();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startGame() {
        startAnimation();
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
            gc.fillText("GAME OVER", 100, 250);
            animationTimer.stop();
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        // game over if snake touches border
        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y <= 0) {
                    gameOver = true;
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y >= height) {
                    gameOver = true;
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x <= 0) {
                    gameOver = true;
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x >= width) {
                    gameOver = true;
                }
                break;
        }
        // Let snake grow if it has eaten food
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y));
            newFood();
        }
        // Game over if snake hits itself
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        // background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width * cornerSize, height * cornerSize);

        // score
        gc.setFill(Color.RED);
        gc.setFont(new Font("Century Gothic", 20));
        gc.fillText("Score: " + (snake.size() - 3), 10, 40);

        //food color randomizer, this will change for the roguelike element. implemented as linear scaling for now
        Color cc = Color.WHITE;
		switch (foodColor) {
		case 0:
			cc = Color.PURPLE;
			break;
		case 1:
			cc = Color.LIGHTBLUE;
			break;
		case 2:
			cc = Color.YELLOW;
			break;
		case 3:
			cc = Color.PINK;
			break;
		case 4:
			cc = Color.ORANGE;
			break;
		}
		gc.setFill(cc);
		gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize);

		// snake
		for (Corner c : snake) {
			gc.setFill(Color.LIGHTGREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 1, cornerSize - 1);
			gc.setFill(Color.GREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 2, cornerSize - 2);

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
			foodColor = rand.nextInt(5);
			speed++;
			break;

		}
	}

    public void startAnimation() {
        animationTimer = new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    draw();
                    return;
                }

                if (now - lastTick > 1000000000 / speed) {
                    lastTick = now;
                    draw();
                }
            }
        };
        animationTimer.start();
    }
    public void restart() {
        snake.clear();
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        snake.add(new Corner(width / 2, height / 2));
        direction = Dir.left;
        speed = 5;
        gameOver = false;
        animationTimer.stop();
        startAnimation();
        newFood();
    }
}