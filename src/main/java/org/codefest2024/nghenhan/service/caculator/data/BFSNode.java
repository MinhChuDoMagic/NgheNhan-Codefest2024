package org.codefest2024.nghenhan.service.caculator.data;

import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.Position;

public class BFSNode extends Position {
    public BFSNode parent;
    public StringBuilder commands;

    public BFSNode(int row, int col, BFSNode parent, StringBuilder commands) {
        super(row, col);
        this.parent = parent;
        this.commands = commands == null ? new StringBuilder() : commands;
    }

    public String reconstructPath() {
        return commands != null ? commands.toString() : Dir.INVALID;
    }
}
