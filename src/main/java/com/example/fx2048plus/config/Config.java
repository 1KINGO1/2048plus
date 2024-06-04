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
            add(new Modifier(Modifiers.LASTCHANCE, 2));
            add(new Modifier(Modifiers.X2, 3));
            add(new Modifier(Modifiers.REMOVE, 3));
        }
    };
    private static final List<Modifier> easyLevelModifiers = new ArrayList<Modifier>() {
        {
            add(new Modifier(Modifiers.TIME, 20));
        }
    };
    private static final LevelConfig easyLevelConfig = new LevelConfig(5, 64, easyLevelBonuses, easyLevelModifiers);

    public static LevelConfig getLevelConfig(Levels level) {
        switch (level) {
            case EASY:
                return easyLevelConfig;
            default:
                return easyLevelConfig;
        }
    }
}
