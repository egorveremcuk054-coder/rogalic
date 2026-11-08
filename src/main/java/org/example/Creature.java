package org.example;

public class Creature {
    public String name;
    public char symbol;
    public int x;
    public int y;
    public int hp;
    public int maxHp;
    public int attack;

    public Creature(String name, char symbol, int hp, int attack) {
        this.name = name;
        this.symbol = symbol;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
    }
}
