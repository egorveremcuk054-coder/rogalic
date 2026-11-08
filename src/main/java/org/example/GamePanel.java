package org.example;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    public static final int TILE = 20;
    public static final int HUD_HEIGHT = 115;

    private final Color floorColor = new Color(50, 48, 55);
    private final Color floorColorAlt = new Color(56, 54, 61);
    private final Color floorLineColor = new Color(38, 36, 42);
    private final Color brickColor = new Color(110, 96, 86);
    private final Color mortarColor = new Color(64, 56, 52);
    private final Color hudColor = new Color(22, 22, 28);
    private final Color hudLineColor = new Color(70, 70, 82);
    private final Color dangerColor = new Color(235, 70, 70);
    private final Color healthColor = new Color(70, 190, 80);
    private final Color healthBackColor = new Color(80, 25, 25);
    private final Color hitOnMonsterColor = new Color(255, 230, 120);

    private final Font textFont = new Font("Consolas", Font.PLAIN, 16);
    private final Font smallFont = new Font("Consolas", Font.BOLD, 13);
    private final Font bigFont = new Font("Consolas", Font.BOLD, 36);

    private Game game = new Game();

    public GamePanel() {
        setPreferredSize(new Dimension(Game.WIDTH * TILE, Game.HEIGHT * TILE + HUD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawMap(g);
        drawMonsters(g);
        drawSprite(g, Sprites.PLAYER, game.player.x, game.player.y);
        drawLight(g);
        drawMonsterHealth(g);
        drawHits(g);
        drawHud(g);

        if (game.gameOver) {
            drawGameOver(g);
        }
    }

    private void drawMap(Graphics g) {
        for (int y = 0; y < Game.HEIGHT; y++) {
            for (int x = 0; x < Game.WIDTH; x++) {
                if (!game.explored[y][x]) {
                    continue;
                }

                char tile = game.map[y][x];
                if (tile == '#') {
                    if (isNextToFloor(x, y)) {
                        drawWall(g, x, y);
                    }
                } else {
                    drawFloor(g, x, y);
                    if (tile == '!') {
                        drawSprite(g, Sprites.POTION, x, y);
                    }
                    if (tile == '>') {
                        drawSprite(g, Sprites.STAIRS, x, y);
                    }
                }
            }
        }
    }

    private boolean isNextToFloor(int x, int y) {
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx >= 0 && ny >= 0 && nx < Game.WIDTH && ny < Game.HEIGHT && game.map[ny][nx] != '#') {
                    return true;
                }
            }
        }
        return false;
    }

    private void drawFloor(Graphics g, int x, int y) {
        int px = x * TILE;
        int py = y * TILE;

        if ((x + y) % 2 == 0) {
            g.setColor(floorColor);
        } else {
            g.setColor(floorColorAlt);
        }
        g.fillRect(px, py, TILE, TILE);

        g.setColor(floorLineColor);
        g.drawLine(px, py, px + TILE - 1, py);
        g.drawLine(px, py, px, py + TILE - 1);
    }

    private void drawWall(Graphics g, int x, int y) {
        int px = x * TILE;
        int py = y * TILE;
        int half = TILE / 2;

        g.setColor(brickColor);
        g.fillRect(px, py, TILE, TILE);

        g.setColor(mortarColor);
        g.drawLine(px, py, px + TILE - 1, py);
        g.drawLine(px, py + half, px + TILE - 1, py + half);
        g.drawLine(px + half, py, px + half, py + half);
        g.drawLine(px, py + half, px, py + TILE - 1);
    }

    private void drawMonsters(Graphics g) {
        for (Creature monster : game.monsters) {
            if (game.isVisible(monster.x, monster.y)) {
                drawSprite(g, spriteFor(monster.symbol), monster.x, monster.y);
            }
        }
    }

    private String[] spriteFor(char symbol) {
        if (symbol == 'r') {
            return Sprites.RAT;
        }
        if (symbol == 's') {
            return Sprites.SKELETON;
        }
        return Sprites.ORC;
    }

    private void drawSprite(Graphics g, String[] sprite, int x, int y) {
        int pixel = TILE / 10;
        for (int row = 0; row < sprite.length; row++) {
            for (int col = 0; col < sprite[row].length(); col++) {
                char c = sprite[row].charAt(col);
                if (c != '.') {
                    g.setColor(Sprites.color(c));
                    g.fillRect(x * TILE + col * pixel, y * TILE + row * pixel, pixel, pixel);
                }
            }
        }
    }

    private void drawLight(Graphics g) {
        for (int y = 0; y < Game.HEIGHT; y++) {
            for (int x = 0; x < Game.WIDTH; x++) {
                if (game.explored[y][x]) {
                    int darkness = darknessAt(x, y);
                    if (darkness > 0) {
                        g.setColor(new Color(0, 0, 0, darkness));
                        g.fillRect(x * TILE, y * TILE, TILE, TILE);
                    }
                }
            }
        }
    }

    private int darknessAt(int x, int y) {
        if (!game.isVisible(x, y)) {
            return 185;
        }
        int dx = x - game.player.x;
        int dy = y - game.player.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return (int) (distance * 150 / Game.VIEW_RADIUS);
    }

    private void drawMonsterHealth(Graphics g) {
        for (Creature monster : game.monsters) {
            if (game.isVisible(monster.x, monster.y) && monster.hp < monster.maxHp) {
                int px = monster.x * TILE + 2;
                int py = monster.y * TILE;
                int width = TILE - 4;

                g.setColor(healthBackColor);
                g.fillRect(px, py, width, 3);
                g.setColor(healthColor);
                g.fillRect(px, py, width * monster.hp / monster.maxHp, 3);
            }
        }
    }

    private void drawHits(Graphics g) {
        g.setFont(smallFont);
        for (Hit hit : game.hits) {
            String text = "-" + hit.damage;
            int px = hit.x * TILE + (TILE - g.getFontMetrics().stringWidth(text)) / 2;
            int py = hit.y * TILE - 2;

            g.setColor(Color.BLACK);
            g.drawString(text, px + 1, py + 1);
            if (hit.onPlayer) {
                g.setColor(dangerColor);
            } else {
                g.setColor(hitOnMonsterColor);
            }
            g.drawString(text, px, py);
        }
    }

    private void drawHud(Graphics g) {
        int top = Game.HEIGHT * TILE;
        g.setColor(hudColor);
        g.fillRect(0, top, getWidth(), HUD_HEIGHT);
        g.setColor(hudLineColor);
        g.drawLine(0, top, getWidth(), top);

        drawHealthBar(g, 10, top + 10);

        g.setFont(textFont);
        g.setColor(Color.WHITE);
        String stats = "Сила: " + game.player.attack
                + "   Уровень: " + game.depth
                + "   Побеждено: " + game.kills;
        g.drawString(stats, 225, top + 24);

        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < game.messages.size(); i++) {
            g.drawString(game.messages.get(i), 10, top + 50 + i * 19);
        }
    }

    private void drawHealthBar(Graphics g, int x, int y) {
        int width = 200;
        int height = 18;
        int filled = width * game.player.hp / game.player.maxHp;

        g.setColor(healthBackColor);
        g.fillRect(x, y, width, height);
        if (game.player.hp <= game.player.maxHp / 3) {
            g.setColor(dangerColor);
        } else {
            g.setColor(healthColor);
        }
        g.fillRect(x, y, filled, height);
        g.setColor(hudLineColor);
        g.drawRect(x, y, width, height);

        g.setFont(smallFont);
        String text = "Здоровье " + game.player.hp + "/" + game.player.maxHp;
        int textX = x + (width - g.getFontMetrics().stringWidth(text)) / 2;
        g.setColor(Color.BLACK);
        g.drawString(text, textX + 1, y + 14);
        g.setColor(Color.WHITE);
        g.drawString(text, textX, y + 13);
    }

    private void drawGameOver(Graphics g) {
        int mapHeight = Game.HEIGHT * TILE;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), mapHeight);

        g.setFont(bigFont);
        g.setColor(dangerColor);
        drawCentered(g, "Вы погибли", mapHeight / 2 - 30);

        g.setFont(textFont);
        g.setColor(Color.WHITE);
        drawCentered(g, "Уровень: " + game.depth + "   Побеждено врагов: " + game.kills, mapHeight / 2 + 10);
        drawCentered(g, "Нажмите R или Enter, чтобы начать заново", mapHeight / 2 + 40);
    }

    private void drawCentered(Graphics g, String text, int y) {
        int x = (getWidth() - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (game.gameOver) {
            if (key == KeyEvent.VK_R || key == KeyEvent.VK_ENTER) {
                game.start();
            }
        } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            game.movePlayer(0, -1);
        } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            game.movePlayer(0, 1);
        } else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            game.movePlayer(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            game.movePlayer(1, 0);
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
