package org.example;

public class Hit {
    public int x;
    public int y;
    public int damage;
    public boolean onPlayer;

    public Hit(int x, int y, int damage, boolean onPlayer) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.onPlayer = onPlayer;
    }
}
