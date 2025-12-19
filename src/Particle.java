package org.example;

import java.awt.Color;

public class Particle {
    public double x, y, vx, vy;
    public int life;
    public Color color;

    public Particle(double x, double y, Color c) {
        this.x = x;
        this.y = y;
        this.vx = (Math.random() - 0.5) * 4;
        this.vy = (Math.random() - 0.5) * 4;
        this.life = 20 + (int)(Math.random() * 20);
        this.color = c;
    }

    public void update() {
        x += vx;
        y += vy;
        life--;
    }
}