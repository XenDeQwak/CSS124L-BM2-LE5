package com.groupfour;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * JavaFX App
 */
public class connect5 {

    private static Scene scene;
    private static Stage c5Stage;

    public void openConnect5() throws IOException {
        App.getStage().hide(); // Hide the App stage instead of closing it
        c5Stage = new Stage(); // Initialize c5Stage here
        if (c5Stage != null) {
            controllerC5 musicController = new controllerC5();
            musicController.playBackgroundMusic();
            scene = new Scene(loadFXML("connect5"), 1280, 750);
            c5Stage.setScene(scene);
            c5Stage.setTitle("Connect 5: Bonanza");
            try {
                c5Stage.getIcons().add(new Image(getClass().getResource("c5Logo.png").toExternalForm()));
            } catch (NullPointerException e) {
                System.out.println("Icon not found");
            }
            c5Stage.show();
        } else {
            System.out.println("c5Stage is null");
        }
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(connect5.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
