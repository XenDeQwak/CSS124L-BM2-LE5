package com.groupfour;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class game_2048{

    //Could be used for dynamic grid size, user input menu
    int gridSize =4;
    Random random = new Random();
    private GridPane gameTiles;
    private GridPane realGameTiles;
    private Tile accessArray[][];
    public game_2048(){
        App.getStage().close();
        Stage stage2048 = new Stage();
        VBox root = new VBox();

        //RETURN BUTTON
        Button returnBtn = new Button("Return");
        returnBtn.setFocusTraversable(false);
        returnBtn.setOnMouseClicked(e->{
            stage2048.close();
            App.getStage().show();
        });

        //CREATING GRID FOR GAME
        realGameTiles = new GridPane();
        gameTiles = new GridPane();
        StackPane mainStack = new StackPane();
        mainStack.getChildren().addAll(gameTiles,realGameTiles);
        accessArray = new Tile[gridSize][gridSize];
        gameTiles.setBackground(new Background(new BackgroundFill(Color.web("#948c7c"), new CornerRadii(20), Insets.EMPTY)));
        gameTiles.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5))));
        realGameTiles.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5))));
        gameTiles.setMaxWidth(gridSize*100);
        gameTiles.setMaxHeight(gridSize*100);
        realGameTiles.setMaxWidth(gridSize*100);
        realGameTiles.setMaxHeight(gridSize*100);

        //Background
        for(int row=0; row<gridSize; row++){
            for(int column=0; column<gridSize;column++){
                Tile tilePane = new Tile();
                Tile accessTile = new Tile();
                accessTile.setBackground(new Background(new BackgroundFill(Color.web("#cac1b5"), new CornerRadii(20), Insets.EMPTY)));
                accessTile.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
                tilePane.setBackground(new Background(new BackgroundFill(Color.web("#948c7c"), new CornerRadii(20), Insets.EMPTY)));
                tilePane.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
                realGameTiles.add(accessTile,column, row);
                gameTiles.add(tilePane, column, row);
                accessArray[row][column]=accessTile;
            }
        }

        gameTiles.setAlignment(Pos.CENTER);
        realGameTiles.setAlignment(Pos.CENTER);
        root.getChildren().addAll(mainStack,returnBtn);

        //SWITCH SCENES//
        Scene gameScene = new Scene(root, 900, 700);
        stage2048.setScene(gameScene);
        stage2048.show();

        //For Detecting WASD and Arrow Keys
        EventHandler<KeyEvent> keyEventHandler = e -> {
            System.out.println("Key pressed: " + e.getCode());
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) {
                moveUp();
            } else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                moveLeft();
            } else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) {
                moveDown();
            } else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                moveRight();
            } else {
                return;
            }
        };
        gameScene.setOnKeyPressed(keyEventHandler);
        spawnTile();
        spawnTile();
    }
    //End of constructor

    //Movement functions
    private void spawnTile(){
        int randomInt1 = random.nextInt(gridSize);
        int randomInt2 = random.nextInt(gridSize);
        while (accessArray[randomInt1][randomInt2].getValue()!=0){
            randomInt1 = random.nextInt(gridSize);
            randomInt2 = random.nextInt(gridSize);
        }
        accessArray[randomInt1][randomInt2].setBackground((new Background(new BackgroundFill(Color.web("#ece4db"), new CornerRadii(20), Insets.EMPTY))));
        accessArray[randomInt1][randomInt2].setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
        accessArray[randomInt1][randomInt2].setValue(2);
    }

    // private TranslateTransition mergeTile(Tile tile1, Tile tile2){
    //     tile1.setValue(tile1.getValue() +tile2.getValue());
    //     TranslateTransition transition = new TranslateTransition(Duration.millis(500), tile1);
    //     transition.setToX(tile2.getLayoutX()-tile1.getLayoutX());
    //     transition.setToY(tile2.getLayoutY()-tile1.getLayoutY());
    //     int row =GridPane.getRowIndex(tile1);
    //     int col =GridPane.getColumnIndex(tile1);
    //     GridPane.setRowIndex(tile1, GridPane.getRowIndex(tile2));
    //     GridPane.setColumnIndex(tile1, GridPane.getColumnIndex(tile2));
    //     GridPane.setRowIndex(tile2, row);
    //     GridPane.setColumnIndex(tile2, col);
    //     tile2.setBackground(new Background(new BackgroundFill(Color.web("#cac1b5"), new CornerRadii(20), Insets.EMPTY)));
    //     tile2.setBorder(new Border(new BorderStroke(Color.web("#998b7d"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
    //     transition.play();
    //     return transition;
    // }

    private void moveUp(){
        ParallelTransition parallelTransition = new ParallelTransition();
        Boolean moved;
        for(int row=1;row<gridSize;row++){
            for(int column=0;column<gridSize;column++){
                Tile tile=accessArray[row][column];
                if(tile.getValue() != 0){
                    // TranslateTransition transition = new TranslateTransition(Duration.millis(500), tile);
                    for(int refRow=row-1;refRow>=0;refRow--){
                        Tile refTile=accessArray[refRow][column];
                        if(refTile.getValue()==tile.getValue()){
                            // parallelTransition.getChildren().add(mergeTile(tile, refTile));
                            break;
                        }
                        else if(refTile.getValue() != 0){
                            // parallelTransition.getChildren().add(mergeTile(tile, refTile));
                            break;
                        }
                    }

                    // double target_y = accessArray[0][column].getLayoutY();
                    // double origin_y = tile.getLayoutY();
                    // transition.setFromY(origin_y);
                    // transition.setToY(target_y-5);
                    // transition.play();
                    
                    realGameTiles.getChildren().remove(accessArray[0][column]);
                    GridPane.setRowIndex(tile,0);
                    Tile accessTile;
                    realGameTiles.add(accessTile = new Tile(),column,row);
                    accessTile.setBackground(new Background(new BackgroundFill(Color.web("#cac1b5"), new CornerRadii(20), Insets.EMPTY)));
                    accessTile.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
                    accessArray[row][column]  = accessTile;

                }
            }
        }
        spawnTile();
        System.out.println("wao");
    }
    private void moveDown(){
        System.out.println("ra");
    }
    private void moveLeft(){
        System.out.println("wa");
    }
    private void moveRight(){
        System.out.println("sha");
    }
    
    //Tile Template WIP
    public class Tile extends StackPane {
        private int value;
        private Text text;
    
        public Tile() {
            text = new Text();
            text.setFont(javafx.scene.text.Font.font(24));
            getChildren().add(text);
            setPrefSize(100, 100);
        }
    
        public void setValue(int value) {
            this.value = value;
            text.setText(String.valueOf(value));
        }
    
        public int getValue() {
            return value;
        }
    }
}
