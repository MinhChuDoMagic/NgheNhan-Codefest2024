package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.GameInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;

import java.util.List;

public class DoNothingStrategy implements Strategy {
    @Override
    public List<Order> find(GameInfo gameInfo) {
        return List.of();
    }
}
