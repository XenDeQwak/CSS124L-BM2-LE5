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
                createClearTile(row, column);
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
        spawnTile(0, 3);
        spawnTile(1,3);
        spawnTile(3,3);
    }
    //End of constructor

    //Movement functions
    private void spawnTile(int temp,int temper){
        // int randomInt1 = random.nextInt(gridSize);
        int randomInt2 = random.nextInt(gridSize);
        // while (accessArray[randomInt1][randomInt2].getValue()!=0){
        //     randomInt1 = random.nextInt(gridSize);
        //     randomInt2 = random.nextInt(gridSize);
        // }
        // accessArray[randomInt1][randomInt2].setBackground((new Background(new BackgroundFill(Color.web("#ece4db"), new CornerRadii(20), Insets.EMPTY))));
        // accessArray[randomInt1][randomInt2].setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
        // accessArray[randomInt1][randomInt2].setValue(randomInt1);
        accessArray[temp][temper].setBackground((new Background(new BackgroundFill(Color.web("#ece4db"), new CornerRadii(20), Insets.EMPTY))));
        accessArray[temp][temper].setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
        accessArray[temp][temper].setValue(temp+1);
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
        Boolean moved=false;
        
        for(int row=1;row<gridSize;row++){
            for(int column=0;column<gridSize;column++){
                Tile tile=accessArray[row][column];

                //Detect if you current tile has value (0 default null for int apparently)
                if(tile.getValue() != 0){

                    for(int refRow=row-1;refRow>=0;refRow--){

                        //Get the tile that is one row above the current tile
                        Tile refTile=accessArray[refRow][column];


                        System.out.println("Tile value: " + tile.getValue());
                        System.out.println("Reference Tile value: " + refTile.getValue());

                        //If the value of that tile is equal to the value of current tile
                        if(refTile.getValue()==tile.getValue()){
                            System.out.println("equal called for" +row+ column+" value: "+ tile.getValue());
                            moved=true;
                            break;
                        }

                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(row==refRow+1){
                                System.out.println("already at limit for"+row+column +"value:  "+ tile.getValue());
                                moved=true;
                                break;
                            }
                            System.out.println("non equal non zero called for" +row+ column +" value: "+ tile.getValue());
                            replaceTileVertical(tile, refRow+1, column);
                            createClearTile(row,column);
                            moved=true;
                            break;
                        }
                    }

                    if(!moved){
                    replaceTileVertical(tile, 0, column);
                    createClearTile(row, column);
                    }
                    moved=false;
                }
            }
        }
        // spawnTile();
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
    
    private void replaceTileVertical(Tile tile, int row, int column){
        realGameTiles.getChildren().remove(accessArray[row][column]);
        GridPane.setRowIndex(tile,row);
        accessArray[row][column] = tile;
    }
    private void createClearTile(int row, int col){
        Tile tile;
        realGameTiles.add(tile = new Tile(),col,row); //no i did not make a mistake, the documentation really does put col first
        tile.setBackground(new Background(new BackgroundFill(Color.web("#cac1b5"), new CornerRadii(20), Insets.EMPTY)));
        tile.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
        accessArray[row][col]  = tile;

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
