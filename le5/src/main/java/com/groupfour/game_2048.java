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
    private GridPane backGround;
    private GridPane gameTiles;
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
        gameTiles = new GridPane();
        backGround = new GridPane();
        StackPane mainStack = new StackPane();
        mainStack.getChildren().addAll(backGround,gameTiles);
        accessArray = new Tile[gridSize][gridSize];
        backGround.setBackground(new Background(new BackgroundFill(Color.web("#948c7c"), new CornerRadii(20), Insets.EMPTY)));
        backGround.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5))));
        gameTiles.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(5))));
        backGround.setMaxWidth(gridSize*100);
        backGround.setMaxHeight(gridSize*100);
        gameTiles.setMaxWidth(gridSize*100);
        gameTiles.setMaxHeight(gridSize*100);

        //Background
        for(int row=0; row<gridSize; row++){
            for(int column=0; column<gridSize;column++){
                Tile tilePane = new Tile();
                Tile accessTile = new Tile();
                createClearTile(row, column);
                tilePane.setBackground(new Background(new BackgroundFill(Color.web("#948c7c"), new CornerRadii(20), Insets.EMPTY)));
                tilePane.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
                gameTiles.add(accessTile,column, row);
                backGround.add(tilePane, column, row);
                accessArray[row][column]=accessTile;
            }
        }

        backGround.setAlignment(Pos.CENTER);
        gameTiles.setAlignment(Pos.CENTER);
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
    private void spawnTile() {
        int randomInt1, randomInt2;
        do {
            randomInt1 = random.nextInt(gridSize);
            randomInt2 = random.nextInt(gridSize);
        } while (accessArray[randomInt1][randomInt2].getValue() != 0);
    
        Tile newTile = accessArray[randomInt1][randomInt2];
        newTile.setBackground(new Background(new BackgroundFill(Color.web("#ece4db"), new CornerRadii(20), Insets.EMPTY)));
        newTile.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
        newTile.setValue(2);
    }
    
    private void mergeTile(Tile tileOrigin, Tile tileTarget){
        int targetRow = GridPane.getRowIndex(tileTarget);
        int targetCol = GridPane.getColumnIndex(tileTarget);
        int originRow = GridPane.getRowIndex(tileOrigin);
        int originCol = GridPane.getColumnIndex(tileOrigin);
        tileOrigin.setValue(tileOrigin.getValue() +tileTarget.getValue());
        gameTiles.getChildren().remove(tileTarget);
        GridPane.setRowIndex(tileOrigin, targetRow);
        GridPane.setColumnIndex(tileOrigin, targetCol);
        accessArray[targetRow][targetCol] = tileOrigin;
        createClearTile(originRow, originCol);
        }    
    
    private void moveUp(){
        Boolean moved=false;
        Boolean aboveEmpty=true;
        for(int row=1;row<gridSize;row++){
            for(int column=0;column<gridSize;column++){
                Tile tile=accessArray[row][column];
                //Detect if your current tile has value (0 default null for int apparently)
                if(tile.getValue() != 0){
                    for(int refRow=row-1;refRow>=0;refRow--){
                        //Get the tile that is one row above the current tile
                        Tile refTile=accessArray[refRow][column];
                        //If the value of that tile is equal to the value of current tile
                        if(refTile.getValue()==tile.getValue()){
                            tile.setValue(tile.getValue()+refTile.getValue());
                            replaceTileVertical(tile, refRow, column);
                            createClearTile(row, column);
                            aboveEmpty=false;
                            moved=true;
                            break;
                        }
                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(row==refRow+1){
                                aboveEmpty=false;
                                break;
                            }
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

    private void moveDown() {
        boolean moved = false;
        for (int column = 0; column < gridSize; column++) {
            for (int row = gridSize - 2; row >= 0; row--) { // Start from second last row
                Tile tile = accessArray[row][column];
                if (tile.getValue() != 0) {
                    int targetRow = row;
                    // Move down until hitting a tile or the bottom
                    while (targetRow < gridSize - 1 && accessArray[targetRow + 1][column].getValue() == 0) {
                        targetRow++;
                    }
    
                    // Check for merging with the tile below
                    if (targetRow < gridSize - 1 && accessArray[targetRow + 1][column].getValue() == tile.getValue()) {
                        mergeTile(tile, accessArray[targetRow + 1][column]); // Merge tiles
                        moved = true;
                    } else if (targetRow != row) { // Move to the empty spot
                        replaceTileVertical(tile, targetRow, column);
                        createClearTile(row, column);
                        moved = true;
                    }
                }
            }
        }
        if (moved) {
            spawnTile();
        }
    }
    
    
private void moveLeft() {
    boolean moved = false;
    for (int row = 0; row < gridSize; row++) {
        for (int column = 1; column < gridSize; column++) { // Start from the second column
            Tile tile = accessArray[row][column];
            if (tile.getValue() != 0) {
                int targetColumn = column;
                
                // Move left until hitting a tile or the boundary
                while (targetColumn > 0 && accessArray[row][targetColumn - 1].getValue() == 0) {
                    targetColumn--;
                }

                // Check for merging with the tile on the left
                if (targetColumn > 0 && accessArray[row][targetColumn - 1].getValue() == tile.getValue()) {
                    mergeTile(tile, accessArray[row][targetColumn - 1]); // Merge tiles
                    moved = true;
                } else if (targetColumn != column) { // Move to the empty spot
                    replaceTileVertical(tile, row, targetColumn);
                    createClearTile(row, column);
                    moved = true;
                }
            }
        }
    }
    if (moved) {
        spawnTile();
    }
}


    private void moveRight(){
        System.out.println("sha");
    }
    
    private void replaceTileVertical(Tile tile, int row, int column){
        gameTiles.getChildren().remove(accessArray[row][column]);
        GridPane.setRowIndex(tile,row);
        accessArray[row][column] = tile;
    }

    private void createClearTile(int row, int col){
        Tile tile;
        gameTiles.add(tile = new Tile(),col,row); //no i did not make a mistake, the documentation really does put col first
        tile.setBackground(new Background(new BackgroundFill(Color.web("#cac1b5"), new CornerRadii(20), Insets.EMPTY)));
        tile.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
        accessArray[row][col] = tile;

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
            setOnMouseClicked(e-> {
                System.out.println(value);
            });
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
