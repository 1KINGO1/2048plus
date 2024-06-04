package com.example.fx2048plus.config;

public class Modifier {
    private Modifiers name;
    private int count;

    public Modifier(Modifiers name, int count) {
        this.name = name;
        this.count = count;
    }

    public Modifiers getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

}
