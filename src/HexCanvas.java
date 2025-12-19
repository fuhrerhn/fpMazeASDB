package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HexCanvas extends JPanel {
    private final HexGrid grid;
    private PathfindingEngine engine; // Set via setter
    private float animTime = 0f;
    private Timer renderTimer;
    private final List<Particle> particles = new ArrayList<>();

    private double hexRadius, hexWidth, hexHeight, offsetX, offsetY;

    public HexCanvas(HexGrid grid) {
        this.grid = grid;
        setBackground(GameConstants.COL_BG);

        renderTimer = new Timer(16, e -> {
            animTime += 0.1f;
            if (animTime > 100) animTime = 0;

            // Update logic partikel
            synchronized (particles) {
                Iterator<Particle> it = particles.iterator();
                while (it.hasNext()) {
                    Particle p = it.next();
                    p.update();
                    if (p.life <= 0) it.remove();
                }
            }
            repaint();
        });
        renderTimer.start();
    }

    public void setEngine(PathfindingEngine engine) {
        this.engine = engine;
    }

    // Method untuk menambah partikel (dipanggil oleh Engine)
    public void addParticles(Point gridPos, Color c, int amount) {
        Point.Double center = getHexCenter(gridPos.x, gridPos.y);
        synchronized (particles) {
            for(int i=0; i<amount; i++) {
                particles.add(new Particle(center.x, center.y, c));
            }
        }
    }

    public void clearParticles() {
        synchronized (particles) {
            particles.clear();
        }
    }

    private Point.Double getHexCenter(int r, int c) {
        double x = offsetX + (c * hexWidth) + ((r % 2 != 0) ? hexWidth / 2.0 : 0);
        double y = offsetY + (r * (hexHeight * 0.75));
        return new Point.Double(x, y);
    }

    private Polygon getHexPolygon(double cx, double cy, double radius) {
        Polygon p = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle_rad = Math.toRadians(30 + (60 * i));
            p.addPoint((int) (cx + radius * Math.cos(angle_rad)), (int) (cy + radius * Math.sin(angle_rad)));
        }
        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = grid.getRows();
        int cols = grid.getCols();
        int panelW = getWidth();
        int panelH = getHeight();

        double maxH = (double) panelH / (rows * 0.75 + 0.25);
        double maxW = (double) panelW / (cols + 0.5);
        hexWidth = Math.min(maxW, maxH);
        hexRadius = hexWidth / Math.sqrt(3);
        hexHeight = 2 * hexRadius;

        offsetX = (panelW - ((cols * hexWidth) + (hexWidth / 2))) / 2 + (hexWidth / 2);
        offsetY = (panelH - ((rows * (hexHeight * 0.75)) + (hexHeight * 0.25))) / 2 + (hexRadius);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Point.Double center = getHexCenter(r, c);
                Polygon poly = getHexPolygon(center.x, center.y, hexRadius);
                int type = grid.getType(r, c);

                if (type == GameConstants.WALL) {
                    g2.setColor(GameConstants.COL_WALL);
                    g2.fillPolygon(poly);
                    g2.setColor(GameConstants.COL_WALL_BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawPolygon(poly);
                } else {
                    if (type == GameConstants.WATER) g2.setColor(GameConstants.COL_WATER);
                    else if (type == GameConstants.MUD) g2.setColor(GameConstants.COL_MUD);
                    else g2.setColor(GameConstants.COL_GRASS);

                    g2.fillPolygon(poly);

                    // Draw Visited Logic
                    if (engine != null && engine.isSolving() && grid.getParent(r, c) != null && type != GameConstants.SOLUTION) {
                        g2.setColor(GameConstants.COL_VISITED);
                        g2.fillPolygon(poly);
                        g2.setColor(GameConstants.COL_VISITED_BORDER);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawPolygon(poly);
                    }

                    if (type == GameConstants.SOLUTION) {
                        float pulse = (float) (Math.sin(animTime) * 0.5 + 0.5);
                        g2.setColor(new Color(255, 0, 255, (int) (150 + (pulse * 105))));
                        g2.fillPolygon(poly);
                        g2.setColor(new Color(255, 255, 255, 100));
                        g2.fillPolygon(getHexPolygon(center.x, center.y, hexRadius * 0.5));
                    }
                    g2.setColor(new Color(0, 0, 0, 50));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawPolygon(poly);
                }
            }
        }

        // Draw Particles (Perubahan penting dari file upload)
        synchronized (particles) {
            for (Particle p : new ArrayList<>(particles)) {
                g2.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), Math.max(0, Math.min(255, p.life * 10))));
                g2.fillOval((int)p.x, (int)p.y, 4, 4);
            }
        }

        drawSpecialHex(g2, grid.getStartNode(), GameConstants.COL_START, "S");
        drawSpecialHex(g2, grid.getEndNode(), GameConstants.COL_FINISH_RED, "F"); // Label F sesuai file baru

        Point pawnPos = grid.getPawnPosition();
        if (pawnPos != null) {
            Point.Double p = getHexCenter(pawnPos.x, pawnPos.y);
            int bounce = (int) (Math.sin(animTime * 2) * 5);
            int pawnSize = (int) (hexRadius * 1.2);
            g2.setColor(Color.BLACK);
            g2.fillOval((int) p.x - pawnSize / 2 + 2, (int) p.y - pawnSize / 2 + 2 - bounce, pawnSize, pawnSize);
            g2.setColor(GameConstants.COL_PAWN);
            g2.fillOval((int) p.x - pawnSize / 2, (int) p.y - pawnSize / 2 - bounce, pawnSize, pawnSize);
            g2.setColor(GameConstants.COL_PAWN_BORDER);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval((int) p.x - pawnSize / 2, (int) p.y - pawnSize / 2 - bounce, pawnSize, pawnSize);
        }
    }

    private void drawSpecialHex(Graphics2D g2, Point pt, Color c, String text) {
        Point.Double center = getHexCenter(pt.x, pt.y);
        Polygon poly = getHexPolygon(center.x, center.y, hexRadius * 0.8);
        g2.setColor(c);
        g2.fillPolygon(poly);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawPolygon(poly);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, (int) (hexRadius)));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (int) center.x - fm.stringWidth(text) / 2, (int) center.y + fm.getAscent() / 3);
    }
}