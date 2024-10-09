package com.groupfour;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;



public class App extends Application {

    static Scene scene;
    private static Stage stage;

    VBox root;
    HBox titleBox;
    Text text;
    GridPane gameBox;

    JsonNode frontPageNode;

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage;
        try {


            // Instantiate game
            RSnake rSnake = new RSnake();
            SpaceAttackers spacegame = new SpaceAttackers();


            // Instantiate stage and scene
            root = new VBox();
            scene = new Scene(root, 640, 480);
            stage.setScene(scene);
            stage.show();
            root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


            //UI Elements
            titleBox = new HBox();
            titleBox.setAlignment(Pos.CENTER);
            titleBox.getStyleClass().add("titlebox");

            text = new Text("3 Games");
            text.getStyleClass().add("title");
            titleBox.getChildren().add(text);

            gameBox = new GridPane();
            gameBox.setGridLinesVisible(true); //remove in the future
            gameBox.setHgap(20);
            gameBox.setVgap(20);
            gameBox.setAlignment(Pos.CENTER);


            //Json components
            frontPageNode = new ObjectMapper().readTree(getClass().getResourceAsStream("fpData.json"));
            getGameFromMenu(frontPageNode);
            root.getChildren().addAll(titleBox, gameBox);
            

            //When a cell gets clicked, this happens
            gameBox.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {

                Node node = (Node) e.getTarget();
                if (node instanceof ImageView || node instanceof Label) {
                    node = node.getParent();
                }

                if (node != null && GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null) {
                    int columnIndex = GridPane.getColumnIndex(node);
                    int rowIndex = GridPane.getRowIndex(node);

                    if (columnIndex == 0 && rowIndex == 0) {
                        rSnake.displayLoadingScreen();  
                    } else if (columnIndex == 1 && rowIndex == 0) {
                        System.out.println("Connect Four");
                    } else if (columnIndex == 2 && rowIndex == 0) {
                        System.out.println("2048");
                        game_2048 game = new game_2048();
                    } else if (columnIndex == 0 && rowIndex == 1) {
                        stage.close();
                        spacegame.stage.setOnCloseRequest(e2 -> {
                            stage.show();
                        });
                        spacegame.start();
                    }
                } else {
                    System.out.println("No games");
                }
                
            });


        } catch (IOException e) {
            System.err.println("Error");
            e.printStackTrace();
        }
    }



    public void getGameFromMenu (JsonNode frontPageNode) {

        if (!frontPageNode.isArray()) {
            System.out.println("Error: JSON data is not an array");
            return;
        }

        int row = 0;
        int column = 0;

        // Unpack JSON components
        for (JsonNode element : frontPageNode) {

            String title = element.get("gameTitle").asText();
            String imageUrl = element.has("gameImage") ? element.get("gameImage").asText() : "";
            String placeholderUrl = element.has("placeholder_image") ? element.get("placeholder_image").asText() : "";
            
            VBox gameBoxElement = new VBox();
            gameBoxElement.setAlignment(Pos.CENTER);
            gameBoxElement.setOnMouseEntered(e -> gameBoxElement.setCursor(Cursor.HAND));
            gameBoxElement.setOnMouseExited(e -> gameBoxElement.setCursor(Cursor.DEFAULT));
            Label gameTitle = new Label(title);
            

            String useImage = (new File("le5\\src\\main\\resources\\com\\groupfour\\" + imageUrl).isFile())? imageUrl : placeholderUrl;
            ImageView gameImage = new ImageView(new Image(getClass().getResource(useImage).toExternalForm()));
            

            gameImage.setFitWidth(150);
            gameImage.setFitHeight(150);
            gameBoxElement.getChildren().addAll(gameTitle, gameImage);
            GridPane.setConstraints(gameBoxElement, column, row);
            gameBox.getChildren().add(gameBoxElement);

            column++;
            if (column > 2) {
                column = 0;
                row++;
            }

        }
    }

    //public 
    


    public static void main(String[] args) {
        launch();
    }


    
    public static Stage getStage() {
        return stage;
    }

}   