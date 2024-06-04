package com.example.fx2048plus.controllers;

import com.example.fx2048plus.game.Game;
import com.example.fx2048plus.game.GameState;
import com.example.fx2048plus.config.Config;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameController {
    Stage stage;
    GameState gameState = GameState.getInstance();

    @FXML
    Pane wrapper;

    public void initialize() {}

    public void setStage(Stage stage) {

        this.stage = stage;
        loadGame();

    }

    private void loadGame(){
        Game game;
        game = new Game(Config.getLevelConfig(gameState.currentLevel), stage);
        wrapper.getChildren().add(game);
    }
}