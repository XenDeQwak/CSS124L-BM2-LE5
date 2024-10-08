package com.groupfour;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class game_2048{
    
    public game_2048(){
        App.getStage().close();
        
        Stage stage2048 = new Stage();
        VBox root = new VBox();
        Button test = new Button("Test");
        test.setOnMouseClicked(e->{
            stage2048.close();
            App.getStage().show();
        });

        //CREATING GRID FOR GAME
        GridPane gameTiles = new GridPane();
        // gameTiles.setGridLinesVisible(true);
        gameTiles.setHgap(5);
        gameTiles.setVgap(5);
        gameTiles.prefWidthProperty().bind(stage2048.widthProperty());
        gameTiles.prefHeightProperty().bind(stage2048.heightProperty());
        StackPane accessArray[][] = new StackPane[4][4];


        for(int column=0; column<4; column++){
            for(int row=0; row<4;row++){
                StackPane tilePane = new StackPane();
                Rectangle tile = new Rectangle(100,100);
                Label label = new Label();

                //Styling
                tile.setFill(Color.BISQUE);
                tile.setStroke(Color.BEIGE);
                label.setFont(Font.font(24));
                label.setAlignment(Pos.CENTER);
               
                tilePane.getChildren().addAll(tile,label);
                gameTiles.add(tilePane, column, row);
                accessArray[column][row]=tilePane;
            }
        }

        gameTiles.setAlignment(Pos.CENTER);

        //WIP
        Label label = (Label) accessArray[0][0].getChildren().get(1);
        label.setText("2");
        
        root.getChildren().addAll(gameTiles,test);

        //SWITCH SCENES//
        Scene gameScene = new Scene(root, 1920, 1080);
        stage2048.setScene(gameScene);
        stage2048.setMaximized(true);
        stage2048.show();
    }
    
    public class Tile extends Pane {
        private int value;
        private Text text;
    
        public Tile(int value) {
            this.value = value;
            text = new Text(String.valueOf(value));
            text.setFont(javafx.scene.text.Font.font(24));
            getChildren().add(text);
            setStyle("-fx-background-color: #ccc; -fx-border-color: #666; -fx-border-width: 1px;");
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
