package org.codefest2024.nghenhan.service.socket;

import org.codefest2024.nghenhan.service.socket.data.Dir;

public class DriveCommand {
    public Dir player;
    public Dir child;

    public DriveCommand(Dir player, Dir child) {
        this.player = player;
        this.child = child;
    }

    public DriveCommand(Dir player) {
        this.player = player;
    }
}
