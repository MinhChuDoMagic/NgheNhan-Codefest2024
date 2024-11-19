package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.socket.DriveCommand;
import org.codefest2024.nghenhan.service.socket.data.GameInfo;

public interface DirectionFinder {
    DriveCommand find(GameInfo gameInfo);
}
