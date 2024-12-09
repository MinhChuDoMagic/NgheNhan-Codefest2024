package org.codefest2024.nghenhan.service.strategy;

import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.GameInfo;

import java.util.List;

public interface Strategy {
    List<Order> find(GameInfo gameInfo);
}
