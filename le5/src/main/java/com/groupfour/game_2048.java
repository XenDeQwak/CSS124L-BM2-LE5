package com.groupfour;


import java.util.Random;

import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;





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
        VBox.setMargin(returnBtn, new Insets(20, 0, 0, 0));

        //CREATING GRID FOR GAME
        gameTiles = new GridPane();
        backGround = new GridPane();
        StackPane mainStack = new StackPane();
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
                createClearTile(row, column);
                tilePane.setBackground(new Background(new BackgroundFill(Color.web("#948c7c"), new CornerRadii(20), Insets.EMPTY)));
                tilePane.setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
                backGround.add(tilePane, column, row);
            }
        }

        mainStack.getChildren().addAll(backGround,gameTiles);
        mainStack.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(mainStack,returnBtn);

        //SWITCH SCENES//
        Scene gameScene = new Scene(root, 900, 700);
        stage2048.setScene(gameScene);
        stage2048.show();

        //For Detecting WASD and Arrow Keys
        EventHandler<KeyEvent> keyEventHandler = e -> {
            //spawnTile();
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
        ScaleTransition st = new ScaleTransition(Duration.millis(100), accessArray[randomInt1][randomInt2]);
        st.setFromX(0.1);
        st.setFromY(0.1);
        st.setToX(1);
        st.setToY(1);
        st.play();
    }
    
    private void moveUp(){
        Boolean moved=false;
        Boolean leftEmpty=true;
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
                            leftEmpty=false;
                            moved=true;
                            break;
                        }
                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(row==refRow+1){
                                leftEmpty=false;
                                break;
                            }
                            replaceTileVertical(tile, refRow+1, column);
                            createClearTile(row,column);
                            leftEmpty=false;
                            moved=true;
                            break;
                        }
                    }
                    //If leftEmpty, means every tile above is value=0;means null
                    if(leftEmpty){
                    replaceTileVertical(tile, 0, column);
                    createClearTile(row, column);
                    moved=true;
                    }
                    leftEmpty=true;
                }
            }
        }
        if(moved){
        spawnTile();
        }
    }

    private void moveDown(){
        Boolean moved=false;
        Boolean belowEmpty=true;
        for(int row=gridSize-2;row>=0;row--){
            for(int column=0;column<gridSize;column++){
                Tile tile=accessArray[row][column];
                //Detect if your current tile has value (0 default null for int apparently)
                if(tile.getValue() != 0){
                    for(int refRow=row+1;refRow<gridSize;refRow++){
                        //Get the tile that is one row below the current tile
                        Tile refTile=accessArray[refRow][column];
                        //If the value of that tile is equal to the value of current tile
                        if(refTile.getValue()==tile.getValue()){
                            tile.setValue(tile.getValue()+refTile.getValue());
                            replaceTileVertical(tile, refRow, column);
                            createClearTile(row, column);
                            belowEmpty=false;
                            moved=true;
                            break;
                        }
                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(row==refRow-1){
                                belowEmpty=false;
                                break;
                            }
                            replaceTileVertical(tile, refRow-1, column);
                            createClearTile(row,column);
                            belowEmpty=false;
                            moved=true;
                            break;
                        }
                    }
                    //If belowEmpty, means every tile above is value=0;means null
                    if(belowEmpty){
                        replaceTileVertical(tile, gridSize-1, column);
                        createClearTile(row, column);
                        moved=true;
                    }
                    belowEmpty=true;
                }
            }
        }
        if(moved){
        spawnTile();
        }
    }

    private void moveRight(){
        Boolean moved=false;
        Boolean leftEmpty=true;
        for(int row=0;row<gridSize;row++){
            for(int column=gridSize-2;column>=0;column--){
                Tile tile=accessArray[row][column];
                //Detect if your current tile has value (0 default null for int apparently)
                if(tile.getValue() != 0){
                    for(int refColumn=column+1;refColumn<gridSize;refColumn++){
                        //Get the tile that is one column to the left of the current tile
                        Tile refTile=accessArray[row][refColumn];
                        //If the value of that tile is equal to the value of current tile
                        if(refTile.getValue()==tile.getValue()){
                            System.out.println("test");
                            tile.setValue(tile.getValue()+refTile.getValue());
                            replaceTileHorizontal(tile, row, refColumn);
                            createClearTile(row, column);
                            leftEmpty=false;
                            moved=true;
                            break;
                        }
                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(column==refColumn-1){
                                leftEmpty=false;
                                break;
                            }
                            replaceTileHorizontal(tile, row, refColumn-1);
                            createClearTile(row,column);
                            leftEmpty=false;
                            moved=true;
                            break;
                        }
                    }
                    //If leftEmpty, means every tile above is value=0;means null
                    if(leftEmpty){
                    replaceTileHorizontal(tile, row, gridSize-1);
                    createClearTile(row, column);
                    moved=true;
                    }
                    leftEmpty=true;
                }
            }
        }
        if(moved){
        spawnTile();
        }
    }
    
    private void moveLeft() {
        Boolean moved=false;
        Boolean leftEmpty=true;
        for(int row=0;row<gridSize;row++){
            for(int column=1;column<gridSize;column++){
                Tile tile=accessArray[row][column];
                //Detect if your current tile has value (0 default null for int apparently)
                if(tile.getValue() != 0){
                    for(int refColumn=column-1;refColumn>=0;refColumn--){
                        //Get the tile that is one column to the left of the current tile
                        Tile refTile=accessArray[row][refColumn];
                        //If the value of that tile is equal to the value of current tile
                        if(refTile.getValue()==tile.getValue()){
                            tile.setValue(tile.getValue()+refTile.getValue());
                            replaceTileHorizontal(tile, row, refColumn);
                            createClearTile(row, column);
                            leftEmpty=false;
                            moved=true;
                            break;
                        }
                        //If the value of that tile is not 0 and is not equal to the value of current tile, 
                        //move current tile to one row below the reference tile.
                        else if(refTile.getValue() != 0){
                            if(column==refColumn+1){
                                leftEmpty=false;
                                break;
                            }
                            replaceTileHorizontal(tile, row, refColumn+1);
                            createClearTile(row,column);
                            leftEmpty=false;
                            moved=true;
                            break;
                        }
                    }
                    //If leftEmpty, means every tile above is value=0;means null
                    if(leftEmpty){
                    replaceTileHorizontal(tile, row, 0);
                    createClearTile(row, column);
                    moved=true;
                    }
                    leftEmpty=true;
                }
            }
        }
        if(moved){
        spawnTile();
        }
    }
    
    private void replaceTileVertical(Tile tile, int row, int column){
        gameTiles.getChildren().remove(accessArray[row][column]);
        GridPane.setRowIndex(tile,row);
        accessArray[row][column] = tile;
    }
    private void replaceTileHorizontal(Tile tile, int row, int column){
        gameTiles.getChildren().remove(accessArray[row][column]);
        GridPane.setColumnIndex(tile,column);
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
            setBorder(new Border(new BorderStroke(Color.web("#948c7c"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(5))));
            updateColor();
        }
    
        public void setValue(int value) {
            this.value = value;
            text.setText(String.valueOf(value));
            updateColor();
        }
    
        public int getValue() {
            return value;
        }

        public void updateColor() {
            Color color;
            switch (value) {
                case 2:
                    color = Color.web("#eee4da");
                    break;
                case 4:
                    color = Color.web("#ede0c8");
                    break;
                case 8:
                    color = Color.web("#f2b179");
                    break;
                case 16:
                    color = Color.web("#f59563");
                    break;
                case 32:
                    color = Color.web("#f67c5f");
                    break;
                case 64:
                    color = Color.web("#f3623b");
                    break;
                case 128:
                    color = Color.web("#b3a47a");
                    break;
                case 256:
                    color = Color.web("#f8d82a");
                    break;
                case 512:
                    color = Color.web("#f9c80e");
                    break;
                case 1024:
                    color = Color.web("#f9c922");
                    break;
                case 2048:
                    color = Color.web("#ffea4d");
                    break;
                case 4096:
                    color = Color.web("#ffcc00");
                    break;
                default:
                    color = Color.web("#ccc");
                    break;
            }
            setBackground(new Background(new BackgroundFill(color, new CornerRadii(15), Insets.EMPTY)));
        }
    }
}
