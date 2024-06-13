package com.example.fx2048plus.tile_modifiers;

public interface TileModifier {

    public static double getAppearanceChance() {
        return 0;
    }


    public boolean isMovable();
    public boolean isMergeable();
    // Destroy with bonus
    public boolean isDestroyable();

    public String getClassName();
    public void onMerge();
    public void onDestroy();
    public void onMove();
    public void onSpawn();

    public static void cleanup() {};
}
