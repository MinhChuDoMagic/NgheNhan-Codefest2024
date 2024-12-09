package org.codefest2024.nghenhan.service.finder.data;

public class DijkstraNode extends Node {
    public double g;

    public DijkstraNode(int row, int col, double g, Node parent, StringBuilder commands) {
        super(row, col, parent, commands);
        this.g = g;
    }
}
