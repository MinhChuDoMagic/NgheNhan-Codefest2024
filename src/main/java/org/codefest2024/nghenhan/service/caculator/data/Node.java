package org.codefest2024.nghenhan.service.caculator.data;

import org.codefest2024.nghenhan.service.socket.data.Position;

public class Node extends Position {
    public double g;
    public double h;
    public Node parent;
    public StringBuilder commands;

    public Node(int row, int col, double g, double h, Node parent, StringBuilder commands) {
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
