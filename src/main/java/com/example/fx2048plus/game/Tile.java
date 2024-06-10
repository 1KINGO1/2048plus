package com.example.fx2048plus.game;

import com.example.fx2048plus.config.Config;
import com.example.fx2048plus.config.LevelConfig;
import com.example.fx2048plus.tile_modifiers.TileModifier;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class Tile extends Label {

    private int value;
    private Location location;
    private TileModifier modifier = null;

    public Tile(int value, LevelConfig config) {
        this.value = value;

        final int squareSize = (int) (config.cellSize - Config.GAME_BOX_CELL_STOKE_WIDTH * 2);
        setMinSize(squareSize, squareSize);
        setMaxSize(squareSize, squareSize);
        setPrefSize(squareSize, squareSize);
        setAlignment(Pos.CENTER);

        setText(value + "");
        getStyleClass().addAll("game-label", "game-tile-" + value);
    }

    public void setLocation(Location location) {

        this.location = location;

        if (modifier != null) {
            modifier.onSpawn();
        }
    }

    public Location getLocation() {
        return location;
    }

    public int getValue() {
        return value;
    }
    public int setValue(int value) {
        setText(value + "");
        getStyleClass().clear();
        getStyleClass().addAll("game-label", "game-tile-" + value);
        return this.value = value;
    }

    public void merge(Tile other) {
        value += other.value;
        setText(value + "");
        getStyleClass().remove("game-tile-" + other.value);
        getStyleClass().add("game-tile-" + value);
    }

    public boolean isMergeable(Tile other) {

        TileModifier otherModifier = other.getTileModifier();

        boolean mergeable = value == other.value;
        if (modifier != null) {
            mergeable = mergeable && modifier.isMergeable();
        }
        if (otherModifier != null) {
            mergeable = mergeable && otherModifier.isMergeable();
        }
        return mergeable;
    }

    public void setModifier(TileModifier modifier) {
        this.modifier = modifier;
    }

    public void removeModifier(TileModifier modifier) {
        this.modifier = null;
    }

    public TileModifier getTileModifier() {
        return modifier;
    }
}
