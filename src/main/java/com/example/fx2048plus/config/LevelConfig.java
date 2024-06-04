package com.example.fx2048plus.config;

import java.util.List;

public class LevelConfig {
    public int gridSize;
    public int target;
    // 0 - no time limit
    public float cellSize;
    public List<Modifier> bonuses;
    public List<Modifier> modifiers;

    public LevelConfig(int gridSize, int target, List<Modifier> bonuses, List<Modifier> modifiers) {
        this.gridSize = gridSize;
        this.target = target;
        this.cellSize = 120 / (gridSize/4f);
        this.bonuses = bonuses;
        this.modifiers = modifiers;
    }

    public LevelConfig(int gridSize, int target) {
        this.gridSize = gridSize;
        this.target = target;
        this.cellSize = 120 / (gridSize/4f);
    }
}
