package com.example.fx2048plus.game;

import com.example.fx2048plus.config.LevelConfig;
import javafx.geometry.Bounds;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Game extends StackPane {

    private final LevelConfig config;
    private final Stage stage;
    private final GameManager gameManager;
    private final Bounds gameBounds;

    public Game(LevelConfig config, Stage stage) {
        this.config = config;
        this.stage = stage;
        gameManager = new GameManager(config, stage);
        getChildren().add(gameManager);
        getStyleClass().addAll("game-root");

        gameBounds = gameManager.getLayoutBounds();

        addSwipeHandlers();
        addKeyHandlers();
        setFocusTraversable(true);
        setOnMouseClicked(e -> requestFocus());
    }

    private void addKeyHandlers() {
        setOnKeyPressed(ke -> {
            var keyCode = ke.getCode();
            System.out.println("Key pressed: " + keyCode);
            switch (keyCode) {
                case R -> gameManager.restartGame();
                default -> {
                    if (keyCode.isArrowKey()) gameManager.move(Direction.valueFor(keyCode));
                }
            }
        });
    }

    private void addSwipeHandlers() {
        setOnSwipeUp(e -> gameManager.move(Direction.UP));
        setOnSwipeRight(e -> gameManager.move(Direction.RIGHT));
        setOnSwipeLeft(e -> gameManager.move(Direction.LEFT));
        setOnSwipeDown(e -> gameManager.move(Direction.DOWN));
    }

}
