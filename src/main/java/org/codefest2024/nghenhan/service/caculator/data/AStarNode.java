package org.codefest2024.nghenhan.service.caculator.data;

import org.codefest2024.nghenhan.service.socket.data.Position;

public class AStarNode extends Position {
    public double g;
    public double h;
    public AStarNode parent;
    public StringBuilder commands;

    public AStarNode(int row, int col, double g, double h, AStarNode parent, StringBuilder commands) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.parent = parent;
        this.commands = commands == null ? new StringBuilder() : commands;
    }

    public double getF(){
        return g+h;
    }
}
