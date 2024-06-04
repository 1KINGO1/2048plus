package com.example.fx2048plus.game;

import com.example.fx2048plus.config.Levels;
import com.example.fx2048plus.config.Modifiers;

import java.util.HashMap;
import java.util.Map;

public class GameState {

    static private GameState instance = null;

    // Reached level
    public Levels level = Levels.EASY;
    // Current level
    public Levels currentLevel = Levels.EASY;
    // Is game over
    public boolean isGameOver = false;
    // Is game won
    public boolean isGameWon = false;
    // Is bonus in use
    public boolean isUsingBonus = false;

    public Map<Modifiers, Integer> modifiersCountMap = new HashMap<>(){
        {
            put(Modifiers.THREETWOADD, 0);
            put(Modifiers.X2, 0);
            put(Modifiers.REMOVE, 0);
            put(Modifiers.LASTCHANCE, 0);
            put(Modifiers.SHUFFLE, 0);
        }
    };

    GameState() {}

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

}
