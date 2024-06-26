package com.example.fx2048plus.tile_modifiers;

import com.example.fx2048plus.game.Tile;

public class StoneModifier implements TileModifier{

    private static int modifiersCount = 0;

    private int aliveTimeLeft = 100;
    private Tile tile;

    public StoneModifier(Tile tile) {
        StoneModifier.modifiersCount++;
        this.tile = tile;
    }

    private static int getMaxCount() {
        return 1;
    }

    public static double getAppearanceChance() {

        if (modifiersCount >= getMaxCount()) {
            return -1d;
        }

        return 0.005d;
    }

    @Override
    public boolean isMovable() {
        return true;
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
        return "stone-tile";
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onMerge() {

    }

    @Override
    public void onSpawn() {

    }
    @Override
    public void onMove() {
        aliveTimeLeft--;

        if (aliveTimeLeft <= 0) {
            tile.removeModifier(this);
            StoneModifier.modifiersCount--;
        }
    }
    public static void cleanup(){
        StoneModifier.modifiersCount = 0;
    }
}
