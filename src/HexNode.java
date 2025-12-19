package org.example;

public class HexNode implements Comparable<HexNode> {
    public int r, c, g, f;
    public HexNode parent;

    public HexNode(int r, int c, int g, int f, HexNode parent) {
        this.r = r;
        this.c = c;
        this.g = g;
        this.f = f;
        this.parent = parent;
    }

    @Override
    public int compareTo(HexNode other) {
        return Integer.compare(this.f, other.f);
    }
}