package com.example.fx2048plus.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 800;
    public static final int BONUS_SIZE = 60;
    public static final int BONUS_GAP = 35;
    public static final int BONUS_PADDING = 20;
    public static final float GAME_BOX_OFFSET_X = WIDTH / 16f;
    public static final float GAME_BOX_OFFSET_Y = HEIGHT / 11f;
    public static final int GAME_BOX_CELL_STOKE_WIDTH = 8;

    private static final List<Modifier> easyLevelBonuses = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.THREETWOADD, 4));
            add(new Modifier(Modifiers.SHUFFLE, 3));
            add(new Modifier(Modifiers.LASTCHANCE, 1));
            add(new Modifier(Modifiers.X2, 3));
            add(new Modifier(Modifiers.REMOVE, 3));
        }
    };
    private static final List<Modifier> easyLevelModifiers = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.TIME, 15));
        }
    };
    private static final LevelConfig easyLevelConfig = new LevelConfig(5, 2048, easyLevelBonuses, easyLevelModifiers);

    private static final List<Modifier> mediumLevelBonuses = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.THREETWOADD, 3));
            add(new Modifier(Modifiers.SHUFFLE, 2));
            add(new Modifier(Modifiers.LASTCHANCE, 0));
            add(new Modifier(Modifiers.X2, 2));
            add(new Modifier(Modifiers.REMOVE, 1));
        }
    };
    private static final List<Modifier> mediumLevelModifiers = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.TIME, 10));
        }
    };
    private static final LevelConfig mediumLevelConfig = new LevelConfig(4, 2048, mediumLevelBonuses, mediumLevelModifiers);

    private static final List<Modifier> hardLevelBonuses = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.THREETWOADD, 2));
            add(new Modifier(Modifiers.SHUFFLE, 2));
            add(new Modifier(Modifiers.LASTCHANCE, 0));
            add(new Modifier(Modifiers.X2, 1));
            add(new Modifier(Modifiers.REMOVE, 1));
        }
    };
    private static final List<Modifier> hardLevelModifiers = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.TIME, 10));
        }
    };
    private static final LevelConfig hardLevelConfig = new LevelConfig(4, 4096, hardLevelBonuses, hardLevelModifiers);

    public static LevelConfig getLevelConfig(Levels level) {
        switch (level) {
            case EASY:
                return easyLevelConfig;
            case MEDIUM:
                return mediumLevelConfig;
            case HARD:
                return hardLevelConfig;
            default:
                return easyLevelConfig;
        }
    }
}
