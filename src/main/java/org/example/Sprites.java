package org.example;

import java.awt.Color;

public class Sprites {
    public static final String[] PLAYER = {
            "...yyyy...",
            "..yyyyyy..",
            "..ysksky..",
            "...ssss..l",
            "..bbbbbb.l",
            ".sbbbbbbsl",
            "..bbnnbb.n",
            "..bbbbbb..",
            "..bb..bb..",
            "..nn..nn..",
    };

    public static final String[] RAT = {
            "..........",
            "..........",
            "..........",
            "......nn..",
            "...nnnnnn.",
            "..nnnnnnkn",
            ".nnnnnnnnp",
            "p..nnnnn..",
            "p..n...n..",
            ".pp.......",
    };

    public static final String[] SKELETON = {
            "...wwww...",
            "..wwwwww..",
            "..wkwwkw..",
            "..wwwwww..",
            "...wkwk...",
            "....ww....",
            ".wwwwwwww.",
            ".w.wwww.w.",
            "...w..w...",
            "..ww..ww..",
    };

    public static final String[] ORC = {
            "..dddddd..",
            ".dggggggd.",
            ".grggggrg.",
            ".gggggggg.",
            ".gwggggwg.",
            "..gggggg..",
            "gnnnnnnnng",
            "g.nnnnnn.g",
            "..nn..nn..",
            "..dd..dd..",
    };

    public static final String[] POTION = {
            "..........",
            "....nn....",
            "....cc....",
            "....cc....",
            "...cccc...",
            "..crrrrc..",
            "..crwrrc..",
            "..crrrrc..",
            "...cccc...",
            "..........",
    };

    public static final String[] STAIRS = {
            "aaaaaaaaaa",
            "akkkkkkkka",
            "alllllllla",
            "akkkkkkkka",
            "akllllllka",
            "akkkkkkkka",
            "akkllllkka",
            "akkkkkkkka",
            "akkkllkkka",
            "aaaaaaaaaa",
    };

    private static final Color YELLOW = new Color(240, 200, 60);
    private static final Color SKIN = new Color(240, 190, 150);
    private static final Color DARK = new Color(15, 15, 20);
    private static final Color BLUE = new Color(60, 110, 220);
    private static final Color BROWN = new Color(130, 85, 45);
    private static final Color BONE = new Color(235, 235, 220);
    private static final Color STEEL = new Color(200, 205, 215);
    private static final Color GREEN = new Color(90, 170, 70);
    private static final Color DARK_GREEN = new Color(50, 110, 45);
    private static final Color RED = new Color(220, 50, 50);
    private static final Color PINK = new Color(240, 140, 160);
    private static final Color GLASS = new Color(150, 210, 230);
    private static final Color STONE = new Color(130, 130, 140);

    public static Color color(char c) {
        switch (c) {
            case 'y':
                return YELLOW;
            case 's':
                return SKIN;
            case 'k':
                return DARK;
            case 'b':
                return BLUE;
            case 'n':
                return BROWN;
            case 'w':
                return BONE;
            case 'l':
                return STEEL;
            case 'g':
                return GREEN;
            case 'd':
                return DARK_GREEN;
            case 'r':
                return RED;
            case 'p':
                return PINK;
            case 'c':
                return GLASS;
            case 'a':
                return STONE;
            default:
                return Color.MAGENTA;
        }
    }
}
