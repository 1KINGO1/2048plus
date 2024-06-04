package com.example.fx2048plus;

import com.example.fx2048plus.config.Config;
import com.example.fx2048plus.controllers.GameController;
import com.example.fx2048plus.controllers.MainMenuController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Main extends Application {

    static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Config.WIDTH, Config.HEIGHT);
        applyStyles(scene);

        Object controller = fxmlLoader.getController();
        if (controller instanceof MainMenuController) {
            MainMenuController mainMenuController = (MainMenuController) controller;
            mainMenuController.setStage(stage);
        }

        stage.setTitle("2048+");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();

        Object controller = fxmlLoader.getController();
        if (controller instanceof MainMenuController) {
            ((MainMenuController) controller).setStage(primaryStage); 
        } else if (controller instanceof GameController) {
            ((GameController) controller).setStage(primaryStage);
        }

        return root;
    }

    public static void applyFadeTransition(Scene newScene, Stage stage) {
        applyStyles(newScene);
        // Apply fade out to the current scene
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), stage.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            stage.setScene(newScene);
            // Apply fade in to the new scene
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public static void applyStyles(Scene scene){
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
    }

    public static void main(String[] args) {
        launch();
    }
}