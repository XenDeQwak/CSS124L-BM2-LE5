package com.groupfour;


import java.util.Random;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;


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

    private boolean mergeTile(Tile tileOrigin, Tile tileTarget){
        int targetRow = GridPane.getRowIndex(tileTarget);
        int targetCol = GridPane.getColumnIndex(tileTarget);
        int originRow = GridPane.getRowIndex(tileOrigin);
        int originCol = GridPane.getColumnIndex(tileOrigin);
        tileOrigin.setValue(tileOrigin.getValue() +tileTarget.getValue());
        realGameTiles.getChildren().remove(tileTarget);
        GridPane.setRowIndex(tileOrigin, targetRow);
        GridPane.setColumnIndex(tileOrigin, targetCol);
        accessArray[targetRow][targetCol] = tileOrigin;
        createClearTile(originRow, originCol);
        return true;
        }    
    
    private void moveUp(){
        Boolean moved=false;
        Boolean aboveEmpty=true;
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
                            mergeTile(tile, refTile);
                            aboveEmpty=false;
                            moved=true;
                            break;
                        }
                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(row==refRow+1){
                                System.out.println("already at limit for"+row+column +"value:  "+ tile.getValue());
                                aboveEmpty=false;
                                break;
                            }
                            System.out.println("non equal non zero called for" +row+ column +" value: "+ tile.getValue());
                            replaceTileVertical(tile, refRow+1, column);
                            createClearTile(row,column);
                            aboveEmpty=false;
                            moved=true;
                            break;
                        }
                    }
                    //If aboveEmpty, means every tile above is value=0;means null
                    if(aboveEmpty){
                    replaceTileVertical(tile, 0, column);
                    createClearTile(row, column);
                    moved=true;
                    }
                    aboveEmpty=true;
                }
            }
        }
        if(moved){
        spawnTile();
        }
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
    
    private boolean replaceTileVertical(Tile tile, int row, int column){
        realGameTiles.getChildren().remove(accessArray[row][column]);
        GridPane.setRowIndex(tile,row);
        accessArray[row][column] = tile;
        return true;
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
