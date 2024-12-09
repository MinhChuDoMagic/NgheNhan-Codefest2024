package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.GameInfo;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Wait;

import java.util.List;

public class TestStrategy implements Strategy {
    @Override
    public List<Order> find(GameInfo gameInfo) {
        return List.of(
                new Dir("24"),
                new Wait(500),
                new Dir("b")
        );
    }
}
