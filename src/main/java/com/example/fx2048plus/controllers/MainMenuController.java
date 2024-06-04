package com.example.fx2048plus.controllers;

import com.example.fx2048plus.game.GameState;
import com.example.fx2048plus.Main;
import com.example.fx2048plus.config.Levels;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuController {
    GameState gameState;

    private Stage stage;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button easyButton;

    @FXML
    private Button mediumButton;

    @FXML
    private Button hardButton;

    @FXML
    private Button exitButton;

    @FXML
    private Text createdByFooter;

    private boolean shouldAnimate = true;
    private boolean isInTransition = false;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        gameState = GameState.getInstance();

        shouldAnimate = gameState.level == Levels.EASY;

        disableUnreachedLevels();

        animateLogo();
        animateEasyButton();
        animateMediumButton();
        animateHardButton();
        animateFooter();
    }

    private void disableUnreachedLevels(){
        if (gameState.level == Levels.EASY) {
            easyButton.setDisable(false);
            mediumButton.setDisable(true);
            hardButton.setDisable(true);
        } else if (gameState.level == Levels.MEDIUM) {
            easyButton.setDisable(false);
            mediumButton.setDisable(false);
            hardButton.setDisable(true);
        } else if (gameState.level == Levels.HARD) {
            easyButton.setDisable(false);
            mediumButton.setDisable(false);
            hardButton.setDisable(false);
        }
    }

    @FXML
    protected void onQuitButtonClick() {
        System.exit(0);
    }

    @FXML
    protected void easyButtonClickHandler(){
        if (isInTransition) return;
        isInTransition = true;
        try {
            gameState.currentLevel = Levels.EASY;
            Scene scene = new Scene(Main.loadFXML("game"));
            Main.applyFadeTransition(scene, stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void animateLogo() {

        if (!shouldAnimate) {
            logoImageView.setTranslateY(-270);
            return;
        }

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(logoImageView.translateYProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(3), new KeyValue(logoImageView.translateYProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(3.4), new KeyValue(logoImageView.translateYProperty(), -270, Interpolator.EASE_BOTH))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }
    @FXML
    protected void animateEasyButton() {
        easyButton.setOpacity(0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(easyButton.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.2), new KeyValue(easyButton.opacityProperty(), 1, Interpolator.EASE_BOTH))
        );
        timeline.setDelay(shouldAnimate ? Duration.seconds(4) : Duration.seconds(1));
        timeline.setCycleCount(1);
        timeline.play();
    }
    @FXML
    protected void animateMediumButton() {
        mediumButton.setOpacity(0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(mediumButton.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.2), new KeyValue(mediumButton.opacityProperty(), 1, Interpolator.EASE_BOTH))
        );
        timeline.setDelay(shouldAnimate ? Duration.seconds(4.2) : Duration.seconds(1.2));
        timeline.setCycleCount(1);
        timeline.play();
    }
    @FXML
    protected void animateHardButton() {
        hardButton.setOpacity(0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(hardButton.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.2), new KeyValue(hardButton.opacityProperty(), 1, Interpolator.EASE_BOTH))
        );
        timeline.setDelay(shouldAnimate ? Duration.seconds(4.4) : Duration.seconds(1.4));
        timeline.setCycleCount(1);
        timeline.play();
    }
    @FXML
    protected void animateFooter() {

        if (!shouldAnimate) {
            createdByFooter.setOpacity(1);
            return;
        }

        Timeline timelineExitButton = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(exitButton.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(5), new KeyValue(exitButton.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(5.2), new KeyValue(exitButton.opacityProperty(), 1, Interpolator.EASE_BOTH))
        );
        Timeline timelineCreatedByFooter = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(createdByFooter.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(5.3), new KeyValue(createdByFooter.opacityProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(5.5), new KeyValue(createdByFooter.opacityProperty(), 1, Interpolator.EASE_BOTH))
        );
        timelineExitButton.setCycleCount(1);
        timelineCreatedByFooter.setCycleCount(1);
        timelineCreatedByFooter.play();
        timelineExitButton.play();
    }

}