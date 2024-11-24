package org.codefest2024.nghenhan.service.caculator.data;

import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.Position;

public class Node extends Position {
    public Node parent;
    public StringBuilder commands;

    public Node(int row, int col, Node parent, StringBuilder commands) {
        super(row, col);
        this.parent = parent;
        this.commands = commands == null ? new StringBuilder() : commands;
    }

    public String reconstructPath() {
        return commands != null ? commands.toString() : Dir.INVALID;
    }
}
