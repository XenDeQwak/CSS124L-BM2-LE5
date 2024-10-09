package com.groupfour;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
/**
 * JavaFX App
 */
public class connect5 extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        Controller musicController = new Controller();
        musicController.playBackgroundMusic();
        scene = new Scene(loadFXML("connect5"), 1280, 750);
        stage.setScene(scene);
        stage.setTitle("Connect 5: Bonanza");
        stage.getIcons().add(new Image("file:src\\main\\res\\c5Logo.png"));
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(connect5.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
