package org.example;

import javax.swing.Timer;
import java.awt.Color;
import java.awt.Point;
import java.util.*;

public class PathfindingEngine {
    private final HexGrid grid;
    private final Runnable repaintCallback;
    private final StatListener statListener;
    private final ParticleSpawner particleSpawner; // Interface baru untuk spawn partikel
    private final SoundManager soundManager;

    private Timer searchTimer;
    private Timer pathTimer;
    private boolean isSolving = false;
    private final int delayMs = 25;

    public interface StatListener {
        void onUpdate(int steps, int cost);
    }

    public interface ParticleSpawner {
        void spawn(Point p, Color color, int amount);
    }

    public PathfindingEngine(HexGrid grid, Runnable repaintCallback, StatListener statListener, ParticleSpawner particleSpawner, SoundManager sm) {
        this.grid = grid;
        this.repaintCallback = repaintCallback;
        this.statListener = statListener;
        this.particleSpawner = particleSpawner;
        this.soundManager = sm;
    }

    public boolean isSolving() { return isSolving; }

    public void stopAll() {
        if (searchTimer != null && searchTimer.isRunning()) searchTimer.stop();
        if (pathTimer != null && pathTimer.isRunning()) pathTimer.stop();
        isSolving = false;
    }

    public void solve(String algorithm) {
        if (isSolving) return;
        resetVisuals();
        statListener.onUpdate(0, 0);
        isSolving = true;
        grid.resetParents();

        final Queue<HexNode> queue;
        final Stack<HexNode> stack;
        final boolean[][] visited = new boolean[grid.getRows()][grid.getCols()];
        final int[][] dist = new int[grid.getRows()][grid.getCols()];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);

        Point start = grid.getStartNode();
        Point end = grid.getEndNode();

        if (algorithm.equals("DFS")) {
            stack = new Stack<>(); queue = null;
            stack.push(new HexNode(start.x, start.y, 0, 0, null));
        } else if (algorithm.equals("BFS")) {
            queue = new LinkedList<>(); stack = null;
            queue.add(new HexNode(start.x, start.y, 0, 0, null));
        } else {
            queue = new PriorityQueue<>(); stack = null;
            queue.add(new HexNode(start.x, start.y, 0, 0, null));
            dist[start.x][start.y] = 0;
        }

        searchTimer = new Timer(delayMs, e -> {
            boolean isEmpty = (stack != null) ? stack.isEmpty() : queue.isEmpty();
            if (isEmpty) { searchTimer.stop(); isSolving = false; return; }

            HexNode current = (stack != null) ? stack.pop() : queue.poll();

            if (current.r == end.x && current.c == end.y) {
                searchTimer.stop();
                reconstructPathAnimated(current);
                return;
            }

            if ((algorithm.equals("Dijkstra") || algorithm.equals("A*")) && current.g > dist[current.r][current.c]) return;
            visited[current.r][current.c] = true;
            repaintCallback.run();

            List<Point> neighbors = grid.getHexNeighbors(current.r, current.c);
            if (algorithm.equals("DFS")) Collections.shuffle(neighbors);

            for (Point n : neighbors) {
                if (grid.getType(n.x, n.y) != GameConstants.WALL && !visited[n.x][n.y]) {
                    int moveCost = grid.getCost(n.x, n.y);
                    int newG = current.g + moveCost;

                    if (algorithm.equals("BFS") || algorithm.equals("DFS")) {
                        if (grid.getParent(n.x, n.y) == null) {
                            grid.setParent(n.x, n.y, new Point(current.r, current.c));
                            HexNode next = new HexNode(n.x, n.y, 0, 0, current);
                            if (stack != null) stack.push(next); else queue.add(next);
                            if (algorithm.equals("BFS")) visited[n.x][n.y] = true;
                        }
                    } else {
                        if (newG < dist[n.x][n.y]) {
                            dist[n.x][n.y] = newG;
                            int h = (algorithm.equals("A*")) ?
                                    (int)(Math.sqrt(Math.pow(n.x - end.x, 2) + Math.pow(n.y - end.y, 2)) * GameConstants.COST_GRASS) : 0;
                            grid.setParent(n.x, n.y, new Point(current.r, current.c));
                            queue.add(new HexNode(n.x, n.y, newG, newG + h, current));
                        }
                    }
                }
            }
        });
        searchTimer.start();
    }

    private void reconstructPathAnimated(HexNode endNode) {
        ArrayList<HexNode> finalPath = new ArrayList<>();
        HexNode curr = endNode;
        int totalPathCost = 0;
        Point start = grid.getStartNode();

        while (curr != null) {
            finalPath.add(curr);
            totalPathCost += grid.getCost(curr.r, curr.c);

            if(curr.parent == null && !(curr.r == start.x && curr.c == start.y)) {
                Point p = grid.getParent(curr.r, curr.c);
                curr = (p != null) ? new HexNode(p.x, p.y, 0, 0, null) : null;
            } else {
                curr = curr.parent;
            }
        }

        Collections.reverse(finalPath);
        statListener.onUpdate(finalPath.size(), totalPathCost);

        final int[] step = {0};
        Point targetEnd = grid.getEndNode();

        pathTimer = new Timer(70, e -> {
            if (step[0] < finalPath.size()) {
                HexNode n = finalPath.get(step[0]);
                grid.setPawnPosition(new Point(n.r, n.c));

                // Spawn particle saat pion bergerak
                particleSpawner.spawn(new Point(n.r, n.c), GameConstants.COL_PAWN, 8);

                if (n.r != targetEnd.x || n.c != targetEnd.y) {
                    grid.setType(n.r, n.c, GameConstants.SOLUTION);
                }

                if (n.r == targetEnd.x && n.c == targetEnd.y) {
                    // Spawn particle putih saat menang (Confetti)
                    particleSpawner.spawn(new Point(n.r, n.c), Color.WHITE, 30);
                    soundManager.playWinSound();
                }
                repaintCallback.run();
                step[0]++;
            } else {
                ((Timer) e.getSource()).stop();
                isSolving = false;
            }
        });
        pathTimer.start();
    }

    public void resetVisuals() {
        stopAll();
        for(int i=0; i<grid.getRows(); i++) {
            for(int j=0; j<grid.getCols(); j++) {
                if(grid.getType(i, j) == GameConstants.SOLUTION) {
                    grid.setType(i, j, GameConstants.GRASS);
                }
            }
        }
        grid.setPawnPosition(new Point(grid.getStartNode().x, grid.getStartNode().y));
        repaintCallback.run();
    }
}