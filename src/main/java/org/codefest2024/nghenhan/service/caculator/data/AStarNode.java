package org.codefest2024.nghenhan.service.caculator.data;

public class AStarNode extends Node {
    public double g;
    public double h;

    public AStarNode(int row, int col, double g, double h, AStarNode parent, StringBuilder commands) {
        super(row, col, parent, commands);
        this.g = g;
        this.h = h;
    }

    public double getF(){
        return g+h;
    }
}
