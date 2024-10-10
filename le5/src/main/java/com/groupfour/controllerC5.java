package com.groupfour;
import javafx.scene.control.Slider;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.fxml.FXML;

import java.io.File;
import java.net.URL;

public class controllerC5{

    private static MediaPlayer mediaPlayer;

    @FXML
    private Slider musicControl;


    public void playBackgroundMusic() {
        String musicFile = "c5Music.mp3";
        URL resource = getClass().getResource(musicFile);
        if (resource == null) {
            System.out.println("Error: Could not find music file " + musicFile);
            return;
        }
        Media media = new Media(resource.toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();
    }

    public void playSoundEffect() {
        String soundFile = "click.mp3";
        URL resource = getClass().getResource(soundFile);
        if (resource == null) {
            System.out.println("Error: Could not find music file " + soundFile);
            return;
        }
        AudioClip sound = new AudioClip(resource.toExternalForm());
        sound.setVolume(1.0);
        sound.play();
}

    public void stopBackgroundMusic() {
        mediaPlayer.stop();
    }
}
