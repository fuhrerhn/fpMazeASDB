package org.example;

import java.awt.Color;

public class GameConstants {
    // Bobot (Weights)
    public static final int COST_GRASS = 1;
    public static final int COST_MUD = 5;
    public static final int COST_WATER = 10;

    // Tipe Sel
    public static final int WALL = 0;
    public static final int GRASS = 1;
    public static final int MUD = 2;
    public static final int WATER = 3;
    public static final int SOLUTION = 99;

    // Warna Neon & Terrain
    public static final Color COL_BG = new Color(15, 15, 20);
    public static final Color COL_WALL = new Color(25, 25, 35);
    public static final Color COL_WALL_BORDER = new Color(40, 40, 50);
    public static final Color COL_GRASS = new Color(34, 139, 34);
    public static final Color COL_MUD = new Color(139, 69, 19);
    public static final Color COL_WATER = new Color(25, 25, 112);
    public static final Color COL_VISITED = new Color(0, 240, 255, 180);
    public static final Color COL_VISITED_BORDER = new Color(150, 255, 255);
    public static final Color COL_START = new Color(0, 255, 0);
    public static final Color COL_FINISH_RED = new Color(255, 50, 50);
    public static final Color COL_PAWN = new Color(255, 215, 0);
    public static final Color COL_PAWN_BORDER = new Color(50, 50, 0);
}