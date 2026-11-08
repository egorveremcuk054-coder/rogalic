package org.example;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    public static final int WIDTH = 48;
    public static final int HEIGHT = 26;
    public static final int VIEW_RADIUS = 7;

    public char[][] map;
    public boolean[][] explored;
    public Creature player;
    public ArrayList<Creature> monsters = new ArrayList<>();
    public ArrayList<String> messages = new ArrayList<>();
    public ArrayList<Hit> hits = new ArrayList<>();
    public int depth;
    public int kills;
    public boolean gameOver;

    private Random random = new Random();
    private Dungeon dungeon;

    public Game() {
        start();
    }

    public void start() {
        player = new Creature("Вы", '@', 30, 5);
        depth = 1;
        kills = 0;
        gameOver = false;
        messages.clear();
        hits.clear();
        addMessage("Найдите лестницу > и спуститесь глубже. Управление: стрелки или WASD");
        newLevel();
    }

    public void movePlayer(int dx, int dy) {
        if (gameOver) {
            return;
        }

        hits.clear();
        int newX = player.x + dx;
        int newY = player.y + dy;
        Creature target = monsterAt(newX, newY);

        if (target != null) {
            attack(player, target);
        } else if (map[newY][newX] == '#') {
            return;
        } else {
            player.x = newX;
            player.y = newY;
            if (map[newY][newX] == '!') {
                drinkPotion();
                map[newY][newX] = '.';
            } else if (map[newY][newX] == '>') {
                goDeeper();
                return;
            }
        }

        monstersTurn();
        updateExplored();
    }

    public Creature monsterAt(int x, int y) {
        for (Creature monster : monsters) {
            if (monster.x == x && monster.y == y) {
                return monster;
            }
        }
        return null;
    }

    public boolean isVisible(int x, int y) {
        int dx = x - player.x;
        int dy = y - player.y;
        return dx * dx + dy * dy <= VIEW_RADIUS * VIEW_RADIUS;
    }

    private void newLevel() {
        do {
            dungeon = new Dungeon(WIDTH, HEIGHT, random);
        } while (dungeon.rooms.size() < 4);

        map = dungeon.tiles;
        explored = new boolean[HEIGHT][WIDTH];
        monsters.clear();

        Room firstRoom = dungeon.rooms.get(0);
        player.x = firstRoom.centerX();
        player.y = firstRoom.centerY();

        Room lastRoom = dungeon.rooms.get(dungeon.rooms.size() - 1);
        map[lastRoom.centerY()][lastRoom.centerX()] = '>';

        int monsterCount = Math.min(3 + depth * 2, 20);
        for (int i = 0; i < monsterCount; i++) {
            placeMonster(createMonster());
        }
        for (int i = 0; i < 2; i++) {
            placePotion();
        }

        updateExplored();
    }

    private Creature createMonster() {
        int roll = random.nextInt(depth + 2);
        if (roll < 2) {
            return new Creature("Крыса", 'r', 6, 2);
        }
        if (roll < 4) {
            return new Creature("Скелет", 's', 12, 4);
        }
        return new Creature("Орк", 'o', 20, 6);
    }

    private void placeMonster(Creature monster) {
        while (true) {
            Room room = randomRoomExceptFirst();
            int x = room.x + random.nextInt(room.width);
            int y = room.y + random.nextInt(room.height);
            if (map[y][x] == '.' && monsterAt(x, y) == null) {
                monster.x = x;
                monster.y = y;
                monsters.add(monster);
                return;
            }
        }
    }

    private void placePotion() {
        while (true) {
            Room room = randomRoomExceptFirst();
            int x = room.x + random.nextInt(room.width);
            int y = room.y + random.nextInt(room.height);
            if (map[y][x] == '.') {
                map[y][x] = '!';
                return;
            }
        }
    }

    private Room randomRoomExceptFirst() {
        int index = 1 + random.nextInt(dungeon.rooms.size() - 1);
        return dungeon.rooms.get(index);
    }

    private void attack(Creature attacker, Creature defender) {
        int damage = Math.max(1, attacker.attack - random.nextInt(3));
        defender.hp -= damage;
        hits.add(new Hit(defender.x, defender.y, damage, defender == player));

        if (attacker == player) {
            addMessage("Вы атакуете: " + defender.name + " -" + damage);
        } else {
            addMessage(attacker.name + " атакует вас: -" + damage);
        }

        if (defender.hp <= 0) {
            defender.hp = 0;
            if (defender == player) {
                gameOver = true;
                addMessage("Вы погибли на уровне " + depth);
            } else {
                monsters.remove(defender);
                kills++;
                addMessage("Враг побеждён: " + defender.name);
            }
        }
    }

    private void monstersTurn() {
        for (Creature monster : monsters) {
            if (gameOver) {
                return;
            }
            int distance = Math.abs(monster.x - player.x) + Math.abs(monster.y - player.y);
            if (distance == 1) {
                attack(monster, player);
            } else if (distance <= VIEW_RADIUS) {
                moveTowardPlayer(monster);
            }
        }
    }

    private void moveTowardPlayer(Creature monster) {
        int dx = direction(monster.x, player.x);
        int dy = direction(monster.y, player.y);
        int distanceX = Math.abs(player.x - monster.x);
        int distanceY = Math.abs(player.y - monster.y);

        if (distanceX >= distanceY) {
            if (!tryMove(monster, dx, 0)) {
                tryMove(monster, 0, dy);
            }
        } else {
            if (!tryMove(monster, 0, dy)) {
                tryMove(monster, dx, 0);
            }
        }
    }

    private int direction(int from, int to) {
        if (to > from) {
            return 1;
        }
        if (to < from) {
            return -1;
        }
        return 0;
    }

    private boolean tryMove(Creature monster, int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return false;
        }

        int newX = monster.x + dx;
        int newY = monster.y + dy;

        if (map[newY][newX] == '#') {
            return false;
        }
        if (monsterAt(newX, newY) != null) {
            return false;
        }
        if (newX == player.x && newY == player.y) {
            return false;
        }

        monster.x = newX;
        monster.y = newY;
        return true;
    }

    private void drinkPotion() {
        player.hp = Math.min(player.hp + 10, player.maxHp);
        addMessage("Вы выпили зелье: +10 здоровья");
    }

    private void goDeeper() {
        depth++;
        player.maxHp += 5;
        player.attack += 1;
        player.hp = player.maxHp;
        addMessage("Вы спустились на уровень " + depth + ". Здоровье восстановлено, сила +1");
        newLevel();
    }

    private void updateExplored() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (isVisible(x, y)) {
                    explored[y][x] = true;
                }
            }
        }
    }

    private void addMessage(String text) {
        messages.add(text);
        if (messages.size() > 4) {
            messages.remove(0);
        }
    }
}
