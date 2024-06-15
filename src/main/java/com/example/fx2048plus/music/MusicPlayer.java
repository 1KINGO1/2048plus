package com.example.fx2048plus.music;

import com.example.fx2048plus.Main;
import com.example.fx2048plus.config.Levels;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {

    private static final Media buttonClick = new Media(Main.class.getResource("sounds/button-click.mp3").toString());
    private static final Media transition = new Media(Main.class.getResource("sounds/transition.mp3").toString());

    private static final Media mainMenuMusic = new Media(Main.class.getResource("sounds/main-menu-music.mp3").toString());

    private static final Media easyLevelMusic = new Media(Main.class.getResource("sounds/easy-level-music.mp3").toString());
    private static final Media mediumLevelMusic = new Media(Main.class.getResource("sounds/medium-level-music.mp3").toString());
    private static final Media hardLevelMusic = new Media(Main.class.getResource("sounds/hard-level-music.mp3").toString());
    private static final Media victoryMusic = new Media(Main.class.getResource("sounds/victory-music.mp3").toString());
    private static final Media gameLost = new Media(Main.class.getResource("sounds/game-lost.mp3").toString());

    private static MediaPlayer currentMusicPlayer = new MediaPlayer(mainMenuMusic);


    public static void playButtonClickSound() {
        applyPlayerSettings(new MediaPlayer(buttonClick)).play();
    }

    public static void playTransitionSound() {
        MediaPlayer player = applyPlayerSettings(new MediaPlayer(transition));
        player.setStartTime(Duration.seconds(1.0));

        player.play();
    }

    private static MediaPlayer applyPlayerSettings(MediaPlayer player) {
        player.setVolume(0.3);
        return player;
    }

    public static void playMainMenuMusic() {

        currentMusicPlayer = new MediaPlayer(mainMenuMusic);

        applyPlayerSettings(currentMusicPlayer);
        currentMusicPlayer.setVolume(0.1);
        currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        currentMusicPlayer.play();
    }

    public static void stopMainMenuMusic() {
        currentMusicPlayer.stop();
    }

    public static void playLevelMusic(Levels level) {

        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }

        if (level == Levels.EASY) {
            currentMusicPlayer = new MediaPlayer(easyLevelMusic);
        }
        if (level == Levels.MEDIUM) {
            currentMusicPlayer = new MediaPlayer(mediumLevelMusic);
        }
        if (level == Levels.HARD) {
            currentMusicPlayer = new MediaPlayer(hardLevelMusic);
        }


        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    applyPlayerSettings(currentMusicPlayer);
                    currentMusicPlayer.setVolume(0.1);
                    currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    currentMusicPlayer.play();
                })
        );

        timeline.playFromStart();
    }

    public static void playWinMusic(){
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }

        currentMusicPlayer = new MediaPlayer(victoryMusic);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.6), e -> {
                    applyPlayerSettings(currentMusicPlayer);
                    currentMusicPlayer.setVolume(0.07);
                    currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                    currentMusicPlayer.play();
                })
        );

        timeline.playFromStart();
    }

    public static void playGameLostSound(){
        if (currentMusicPlayer != null) {
            currentMusicPlayer.stop();
        }

        currentMusicPlayer = new MediaPlayer(gameLost);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.6), e -> {
                    applyPlayerSettings(currentMusicPlayer);
                    currentMusicPlayer.setVolume(0.07);
                    currentMusicPlayer.setCycleCount(0);
                    currentMusicPlayer.play();
                })
        );

        timeline.playFromStart();
    }

    public static void stopLevelMusic() {
        currentMusicPlayer.stop();
    }

}
