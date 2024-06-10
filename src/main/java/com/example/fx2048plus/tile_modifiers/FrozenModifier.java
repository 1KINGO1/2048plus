package com.example.fx2048plus.tile_modifiers;

import com.example.fx2048plus.game.Tile;

public class FrozenModifier implements TileModifier{

    private static int modifiersCount = 0;

    private int aliveTimeLeft = 20;
    private Tile tile;

    public FrozenModifier(Tile tile) {
        FrozenModifier.modifiersCount++;
        this.tile = tile;
    }

    private static int getMaxCount() {
        return 2;
    }

    public static double getAppearanceChance() {

        if (modifiersCount >= getMaxCount()) {
            return -1d;
        }

        return 0.0008d;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean isMergeable() {
        return false;
    }

    @Override
    public boolean isDestroyable() {
        return true;
    }

    @Override
    public String getClassName() {
        return "frozen-tile";
    }

    @Override
    public void onMerge() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onMove() {
        aliveTimeLeft--;

        if (aliveTimeLeft <= 0) {
            tile.removeModifier(this);
            FrozenModifier.modifiersCount--;
        }
    }

    @Override
    public void onSpawn() {

    }
}
