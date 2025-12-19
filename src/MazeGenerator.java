package org.example;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MazeGenerator {
    private final HexGrid grid;

    public MazeGenerator(HexGrid grid) {
        this.grid = grid;
    }

    public void generate(int rows, int cols) {
        grid.init(rows, cols);
        grid.setType(grid.getStartNode().x, grid.getStartNode().y, GameConstants.GRASS);

        ArrayList<Point> wallList = new ArrayList<>(grid.getHexNeighbors(grid.getStartNode().x, grid.getStartNode().y));
        Random rand = new Random();

        while (!wallList.isEmpty()) {
            int index = rand.nextInt(wallList.size());
            Point wall = wallList.remove(index);
            List<Point> neighbors = grid.getHexNeighbors(wall.x, wall.y);
            int visitedCount = 0;
            for (Point n : neighbors) {
                if (grid.getType(n.x, n.y) != GameConstants.WALL) visitedCount++;
            }

            if (visitedCount == 1) {
                grid.setType(wall.x, wall.y, generateRandomTerrain(rand));
                for (Point n : neighbors) {
                    if (grid.getType(n.x, n.y) == GameConstants.WALL && !wallList.contains(n)) {
                        wallList.add(n);
                    }
                }
            }
        }
        grid.setType(grid.getStartNode().x, grid.getStartNode().y, GameConstants.GRASS);
        grid.setType(grid.getEndNode().x, grid.getEndNode().y, GameConstants.GRASS);

        addMultiplePaths(rand);
        grid.setPawnPosition(new Point(grid.getStartNode().x, grid.getStartNode().y));
    }

    private void addMultiplePaths(Random rand) {
        int attempts = (grid.getRows() * grid.getCols()) / 10;
        for (int k = 0; k < attempts; k++) {
            int r = rand.nextInt(grid.getRows() - 2) + 1;
            int c = rand.nextInt(grid.getCols() - 2) + 1;
            if (grid.getType(r, c) == GameConstants.WALL) {
                List<Point> neighbors = grid.getHexNeighbors(r, c);
                int pathNeighbors = 0;
                for (Point n : neighbors) {
                    if (grid.getType(n.x, n.y) != GameConstants.WALL) pathNeighbors++;
                }
                if (pathNeighbors >= 2 && rand.nextDouble() < 0.3) {
                    grid.setType(r, c, generateRandomTerrain(rand));
                }
            }
        }
    }

    private int generateRandomTerrain(Random rand) {
        double chance = rand.nextDouble();
        if (chance < 0.65) return GameConstants.GRASS;
        if (chance < 0.85) return GameConstants.MUD;
        return GameConstants.WATER;
    }
}