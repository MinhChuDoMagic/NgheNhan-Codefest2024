package org.codefest2024.nghenhan.service.caculator.data;

import org.codefest2024.nghenhan.service.socket.data.Position;

public class Node extends Position {
    public double cost;
    public Node parent;
    public StringBuilder commands;

    public Node(int row, int col, double cost, Node parent, StringBuilder commands) {
        this.row = row;
        this.col = col;
        this.cost = cost;
        this.parent = parent;
        this.commands = commands == null ? new StringBuilder() : commands;
    }
}
