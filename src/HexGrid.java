package org.example;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HexGrid {
    private int rows;
    private int cols;
    private int[][] gridType;
    private Point[][] parents;
    private Point startNode;
    private Point endNode;
    private Point pawnPosition;

    public HexGrid(int rows, int cols) {
        init(rows, cols);
    }

    public void init(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.gridType = new int[rows][cols];
        this.parents = new Point[rows][cols];
        for (int i = 0; i < rows; i++) Arrays.fill(gridType[i], GameConstants.WALL);

        // Default Start/End
        this.startNode = new Point(1, 1);
        this.endNode = new Point(rows - 2, cols - 2);
    }

    public void resetParents() {
        this.parents = new Point[rows][cols];
    }

    public boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public List<Point> getHexNeighbors(int r, int c) {
        List<Point> neighbors = new ArrayList<>();
        int[][] dirs;
        if (r % 2 == 0) dirs = new int[][]{{-1, -1}, {-1, 0}, {0, -1}, {0, 1}, {1, -1}, {1, 0}};
        else dirs = new int[][]{{-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, 0}, {1, 1}};

        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (isValid(nr, nc)) neighbors.add(new Point(nr, nc));
        }
        return neighbors;
    }

    public int getCost(int r, int c) {
        int t = gridType[r][c];
        if (t == GameConstants.GRASS) return GameConstants.COST_GRASS;
        if (t == GameConstants.MUD) return GameConstants.COST_MUD;
        if (t == GameConstants.WATER) return GameConstants.COST_WATER;
        return 1;
    }

    // Getters & Setters
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getType(int r, int c) { return gridType[r][c]; }
    public void setType(int r, int c, int type) { gridType[r][c] = type; }
    public Point getParent(int r, int c) { return parents[r][c]; }
    public void setParent(int r, int c, Point p) { parents[r][c] = p; }
    public Point getStartNode() { return startNode; }
    public Point getEndNode() { return endNode; }
    public Point getPawnPosition() { return pawnPosition; }
    public void setPawnPosition(Point p) { this.pawnPosition = p; }
}