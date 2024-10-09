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

    public connect5() throws IOException {
    App.getStage().close();

        Stage c5Stage = new Stage();
        controllerC5 musicController = new controllerC5();
        musicController.playBackgroundMusic();
        scene = new Scene(loadFXML("connect5"), 1280, 750);
        c5Stage.setScene(scene);
        c5Stage.setTitle("Connect 5: Bonanza");
        c5Stage.getIcons().add(new Image("file:le5\\src\\main\\resources\\com\\groupfour\\res\\c5Logo.png"));
        c5Stage.show();
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(connect5.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
