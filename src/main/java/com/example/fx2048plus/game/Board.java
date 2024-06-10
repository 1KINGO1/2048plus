package com.example.fx2048plus.game;

import com.example.fx2048plus.config.Config;
import com.example.fx2048plus.config.LevelConfig;
import com.example.fx2048plus.config.Modifier;
import com.example.fx2048plus.config.Modifiers;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class Board extends Pane {

    private final float CELL_SIZE;

    private int defaultCounter = 60 * 5;
    private int currentCounter = defaultCounter;
    private Timeline timer;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("mm:ss").withZone(ZoneId.systemDefault());
    private String time = "Time left: " + LocalTime.ofSecondOfDay(currentCounter).format(fmt);
    private final StringProperty clock = new SimpleStringProperty("00:00");
    private final Group gridGroup = new Group();
    private final Label gameOverLabel;
    private final Button nextButton = new Button("Next");
    private final Button restartButton = new Button("Restart");
    private final Button backButton = new Button("Back");
    private Label timeLabel = new Label();
    private Label targetLabel = new Label();
    private GameState gameState = GameState.getInstance();
    private final LevelConfig config;
    private final Map<Modifiers, Label> modifiersCountMap;
    private final Map<Modifiers, Label> modifiersButtonMap;

    public Board(LevelConfig config, Stage stage, Map<Modifiers, Label> modifiersButtonMap, Map<Modifiers, Label> modifiersCountMap) {
        this.config = config;
        this.CELL_SIZE = config.cellSize;

        this.modifiersCountMap = modifiersCountMap;
        this.modifiersButtonMap = modifiersButtonMap;

        gameOverLabel = new Label();
        gameOverLabel.setMinSize(CELL_SIZE * config.gridSize + Config.GAME_BOX_CELL_STOKE_WIDTH * 2, CELL_SIZE * config.gridSize + Config.GAME_BOX_CELL_STOKE_WIDTH * 2);
        gameOverLabel.setMaxSize(CELL_SIZE * config.gridSize + Config.GAME_BOX_CELL_STOKE_WIDTH * 2, CELL_SIZE * config.gridSize + Config.GAME_BOX_CELL_STOKE_WIDTH * 2);
        gameOverLabel.setLayoutX(Config.GAME_BOX_OFFSET_X - Config.GAME_BOX_CELL_STOKE_WIDTH);
        gameOverLabel.setLayoutY(Config.GAME_BOX_OFFSET_Y - Config.GAME_BOX_CELL_STOKE_WIDTH);
        gameOverLabel.setAlignment(javafx.geometry.Pos.CENTER);
        gameOverLabel.getStyleClass().add("game-over-screen");
        gameOverLabel.setText("Game Over");

        targetLabel.setMinSize(200, 50);
        targetLabel.setMaxSize(200, 50);
        targetLabel.setLayoutX(Config.GAME_BOX_OFFSET_X + Config.GAME_BOX_CELL_STOKE_WIDTH / 2 + CELL_SIZE * config.gridSize - 200);
        targetLabel.setLayoutY(Config.GAME_BOX_OFFSET_Y - 60 - Config.GAME_BOX_CELL_STOKE_WIDTH);
        targetLabel.setText("Target: " + config.target);
        targetLabel.getStyleClass().clear();
        targetLabel.getStyleClass().add("time-label");
        targetLabel.setAlignment(javafx.geometry.Pos.CENTER);
        getChildren().add(targetLabel);

        backButton.getStyleClass().add("button");
        backButton.setLayoutX(Config.GAME_BOX_OFFSET_X + CELL_SIZE * config.gridSize / 2 - 105);
        backButton.setLayoutY(Config.GAME_BOX_OFFSET_Y + Config.GAME_BOX_CELL_STOKE_WIDTH + CELL_SIZE * config.gridSize - 70);

        nextButton.getStyleClass().add("button");
        nextButton.setLayoutX(Config.GAME_BOX_OFFSET_X + CELL_SIZE * config.gridSize / 2 - 37);
        nextButton.setLayoutY(Config.GAME_BOX_OFFSET_Y + Config.GAME_BOX_CELL_STOKE_WIDTH + CELL_SIZE * config.gridSize - 70);

        restartButton.getStyleClass().add("button");
        restartButton.setLayoutX(Config.GAME_BOX_OFFSET_X + CELL_SIZE * config.gridSize / 2 -10);
        restartButton.setLayoutY(Config.GAME_BOX_OFFSET_Y + Config.GAME_BOX_CELL_STOKE_WIDTH + CELL_SIZE * config.gridSize - 70);

        for (int i = 0; i < config.gridSize; i++) {
            for (int j = 0; j < config.gridSize; j++) {
                gridGroup.getChildren().add(createCell(i, j));
            }
        }
        getChildren().add(gridGroup);

        createTimeLabel();
        createBonusCells();
    }

    public void setRestartButtonClickHandler(EventHandler<MouseEvent> eventHandler) {
        restartButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
    }
    public void setNextButtonClickHandler(EventHandler<MouseEvent> eventHandler) {
        nextButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
    }
    public void setBackButtonClickHandler(EventHandler<MouseEvent> eventHandler) {
        backButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
    }

    private Rectangle createCell(int i, int j) {
        final double arcSize = CELL_SIZE / 6d;
        var cell = new Rectangle(Config.GAME_BOX_OFFSET_X + i * CELL_SIZE, Config.GAME_BOX_OFFSET_Y + j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        cell.setFill(Color.WHITE);
        cell.setStroke(Color.GREY);
        cell.setStrokeWidth(Config.GAME_BOX_CELL_STOKE_WIDTH);
        cell.setArcHeight(arcSize);
        cell.setArcWidth(arcSize);
        cell.getStyleClass().add("game-grid-cell");
        return cell;
    }

    private void createBonusCells() {
        var bonusWrapper = new Pane();
        bonusWrapper.setLayoutX(Config.GAME_BOX_OFFSET_X);
        bonusWrapper.setLayoutY(Config.GAME_BOX_OFFSET_Y + config.gridSize * CELL_SIZE + Config.BONUS_PADDING);
        bonusWrapper.setMinSize(config.gridSize * CELL_SIZE, Config.BONUS_SIZE + Config.BONUS_PADDING * 2 + 22);
        bonusWrapper.setMaxSize(config.gridSize * CELL_SIZE, Config.BONUS_SIZE + Config.BONUS_PADDING * 2 + 22);
        bonusWrapper.getStyleClass().add("bonus-wrapper");
        getChildren().add(bonusWrapper);

        int i = 0;

        int add32Count = config.bonuses.stream().filter(bonus -> bonus.getName() == Modifiers.THREETWOADD).findFirst().map(Modifier::getCount).orElse(0);
        createBonus(Modifiers.THREETWOADD, i++, add32Count, "32+", "bonus-add-32", bonusWrapper);

        int shuffleCount = config.bonuses.stream().filter(bonus -> bonus.getName() == Modifiers.SHUFFLE).findFirst().map(Modifier::getCount).orElse(0);
        createBonus(Modifiers.SHUFFLE, i++, shuffleCount, "⇄", "bonus-shuffle", bonusWrapper);

        int removeCount = config.bonuses.stream().filter(bonus -> bonus.getName() == Modifiers.REMOVE).findFirst().map(Modifier::getCount).orElse(0);
        createBonus(Modifiers.REMOVE, i++, removeCount, "X", "bonus-remove", bonusWrapper);

        int x2Count = config.bonuses.stream().filter(bonus -> bonus.getName() == Modifiers.X2).findFirst().map(Modifier::getCount).orElse(0);
        createBonus(Modifiers.X2, i++, x2Count, "x2", "bonus-x2", bonusWrapper);

        int lastChanceCount = config.bonuses.stream().filter(bonus -> bonus.getName() == Modifiers.LASTCHANCE).findFirst().map(Modifier::getCount).orElse(0);
        createBonus(Modifiers.LASTCHANCE, i++, lastChanceCount, "Last Chance", "bonus-last-chance", bonusWrapper);
    }

    private void createBonus(Modifiers modifier, int i, int defaultCount, String icon, String className, Pane bonusWrapper) {
        var cell = new Label();
        cell.setMinSize(Config.BONUS_SIZE, Config.BONUS_SIZE);
        cell.setMaxSize(Config.BONUS_SIZE, Config.BONUS_SIZE);
        cell.setText(icon);

        cell.setLayoutX(Config.BONUS_GAP * i + i * Config.BONUS_SIZE + Config.BONUS_PADDING);
        cell.setLayoutY(Config.BONUS_PADDING);
        cell.getStyleClass().add("bonus-label");
        cell.getStyleClass().add(className);
        cell.setAlignment(javafx.geometry.Pos.CENTER);
        bonusWrapper.getChildren().add(cell);
        modifiersButtonMap.put(modifier, cell);

        var cellCount = new Label();
        cellCount.setMinSize(Config.BONUS_SIZE, Config.BONUS_SIZE);
        cellCount.setMaxSize(Config.BONUS_SIZE, Config.BONUS_SIZE);
        cellCount.setText("x" + defaultCount);
        cellCount.getStyleClass().add("bonus-count");
        cellCount.setLayoutX(Config.BONUS_GAP * i + i * Config.BONUS_SIZE + Config.BONUS_PADDING + Config.BONUS_SIZE / 2 - 12);
        cellCount.setLayoutY(Config.BONUS_PADDING + Config.BONUS_SIZE - 15);
        bonusWrapper.getChildren().add(cellCount);
        modifiersCountMap.put(modifier, cellCount);
    }

    public void addTile(Tile tile) {
        int layoutX = (int) (Config.GAME_BOX_OFFSET_X + tile.getLocation().getX() * CELL_SIZE + Config.GAME_BOX_CELL_STOKE_WIDTH);
        int layoutY = (int) (Config.GAME_BOX_OFFSET_Y + tile.getLocation().getY() * CELL_SIZE + Config.GAME_BOX_CELL_STOKE_WIDTH);
        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);

        gridGroup.getChildren().add(tile);
    }

    public void updateModifiersCount(Modifiers modifier, int count) {
        modifiersCountMap.get(modifier).setText("x" + count);
    }

    public void removeTiles(Set<Tile> mergedToBeRemoved) {
        gridGroup.getChildren().removeAll(mergedToBeRemoved);
    }

    public void removeAllTiles() {
        gridGroup.getChildren().removeIf(node -> node instanceof Tile);
    }

    public void setGameOver(boolean gameOver) {

        gameState.isGameOver = gameOver;

        clearGameOverLabel();

        if (gameOver) {
            gameOverLabel.getStyleClass().add("game-over-screen");
            final var fadeIn = new FadeTransition(Duration.millis(1200), gameOverLabel);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(0.9);
            fadeIn.setInterpolator(Interpolator.EASE_OUT);
            fadeIn.play();

            gridGroup.getChildren().add(gameOverLabel);
            gridGroup.getChildren().add(restartButton);
            gridGroup.getChildren().add(backButton);
        } else {
            gameOverLabel.getStyleClass().clear();
        }
    }
    public void setGameWon(boolean gameWon) {
        gameState.isGameWon = gameWon;

        clearGameOverLabel();

        if (gameWon) {
            applyFadeInAnimation(gameOverLabel);
            gameOverLabel.setText("Game Won");
            gameOverLabel.getStyleClass().add("game-over-screen");
            gameOverLabel.getStyleClass().add("won");
            gridGroup.getChildren().add(gameOverLabel);

            applyFadeInAnimation(nextButton);
            gridGroup.getChildren().add(nextButton);
        } else {
            gameOverLabel.getStyleClass().clear();
        }
    }
    private void clearGameOverLabel(){
        gridGroup.getChildren().remove(gameOverLabel);
        gridGroup.getChildren().remove(restartButton);
        gridGroup.getChildren().remove(backButton);
        gridGroup.getChildren().remove(nextButton);
    }
    private void applyFadeInAnimation(Node node){
        final var fadeIn = new FadeTransition(Duration.millis(1200), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.9);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);
        fadeIn.play();
    }

    public void createTimeLabel() {
        timeLabel.setMinSize(200, 50);
        timeLabel.setMaxSize(200, 50);
        timeLabel.setLayoutX(Config.GAME_BOX_OFFSET_X - Config.GAME_BOX_CELL_STOKE_WIDTH / 2);
        timeLabel.setLayoutY(Config.GAME_BOX_OFFSET_Y - 60 - Config.GAME_BOX_CELL_STOKE_WIDTH);
        timeLabel.getStyleClass().clear();
        timeLabel.getStyleClass().add("time-label");
        timeLabel.setAlignment(javafx.geometry.Pos.CENTER);
        timeLabel.textProperty().bind(clock);
        timer = new Timeline(new KeyFrame(Duration.ZERO, e -> clock.set(tickCounter())), new KeyFrame(Duration.seconds(1)));
        timer.setCycleCount(Animation.INDEFINITE);
    }

    public void startCountdown(int defaultCounter) {
        getChildren().remove(timeLabel);
        this.defaultCounter = defaultCounter;
        currentCounter = defaultCounter;

        if (defaultCounter != 0) {
            createTimeLabel();
            getChildren().add(timeLabel);
            timer.play();
        }
    }

    public void stopCountdown() {
        timer.stop();
    }

    public boolean decreaseCounter(int seconds) {
        if (seconds > currentCounter) {
            return false;
        }
        this.currentCounter -= seconds;

        Label increaseTimeLabel = new Label();
        increaseTimeLabel.setMinSize(100, 50);
        increaseTimeLabel.setMaxSize(100, 50);
        increaseTimeLabel.setLayoutX(Config.GAME_BOX_OFFSET_X - Config.GAME_BOX_CELL_STOKE_WIDTH / 2 + 175);
        increaseTimeLabel.setLayoutY(Config.GAME_BOX_OFFSET_Y - 60 - Config.GAME_BOX_CELL_STOKE_WIDTH);
        increaseTimeLabel.setText("-" + seconds);
        increaseTimeLabel.getStyleClass().add("decrease-time");
        increaseTimeLabel.setAlignment(Pos.CENTER_LEFT);
        getChildren().add(increaseTimeLabel);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), increaseTimeLabel);
        fadeOut.setDelay(Duration.millis(400));
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setInterpolator(Interpolator.EASE_OUT);
        fadeOut.play();

        return true;
    }

    public boolean increaseCounter(int seconds) {
        this.currentCounter += seconds;

        Label increaseTimeLabel = new Label();
        increaseTimeLabel.setMinSize(100, 50);
        increaseTimeLabel.setMaxSize(100, 50);
        increaseTimeLabel.setLayoutX(Config.GAME_BOX_OFFSET_X - Config.GAME_BOX_CELL_STOKE_WIDTH / 2 + 175);
        increaseTimeLabel.setLayoutY(Config.GAME_BOX_OFFSET_Y - 60 - Config.GAME_BOX_CELL_STOKE_WIDTH);
        increaseTimeLabel.setText("+" + seconds);
        increaseTimeLabel.getStyleClass().add("increase-time");
        increaseTimeLabel.setAlignment(Pos.CENTER_LEFT);
        getChildren().add(increaseTimeLabel);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), increaseTimeLabel);
        fadeOut.setDelay(Duration.millis(400));
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setInterpolator(Interpolator.EASE_OUT);
        fadeOut.play();

        return true;
    }

    public void removeTile(Tile tile) {
        gridGroup.getChildren().remove(tile);
    }

    public void activeBonus(Modifiers modifier) {
        modifiersButtonMap.get(modifier).getStyleClass().add("active");
    }
    public void deactiveBonus(Modifiers modifier) {
        modifiersButtonMap.get(modifier).getStyleClass().remove("active");
    }

    public boolean checkTimeGreaterThan(int time) {
        return currentCounter >= time;
    }

    public boolean checkTimeLessThan(int time) {
        return currentCounter <= time;
    }

    public void updateTilesModifierStyles(){
        for (Node node : gridGroup.getChildren()) {
            if (node instanceof Tile) {
                Tile tile = (Tile) node;

                // Set default styles
                tile.setValue(tile.getValue());

                System.out.println(tile.getStyleClass());

                if (tile.getTileModifier() != null) {
                    // Set modifier styles
                    tile.getStyleClass().add(tile.getTileModifier().getClassName());
                }
            }
        }
    }

    public String tickCounter() {
        if (gameState.isGameOver) {
            timer.stop();
            return time;
        }

        this.currentCounter--;
        if (currentCounter == 0) {
            timer.stop();
            setGameOver(true);
        }

        if (currentCounter <= 30 && currentCounter > 10) {
           timeLabel.getStyleClass().add("time-label-yellow");
        }

        if (currentCounter <= 10) {
            timeLabel.getStyleClass().remove("time-label-yellow");
            timeLabel.getStyleClass().add("time-label-red");
        }

        time = "Time left: " + LocalTime.ofSecondOfDay(currentCounter).format(fmt);
        return time;
    }
}
