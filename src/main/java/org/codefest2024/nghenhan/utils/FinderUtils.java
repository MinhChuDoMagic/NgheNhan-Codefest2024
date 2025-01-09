package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.ArrayList;
import java.util.List;

public class FinderUtils {
    private FinderUtils() {
    }

    public static List<Order> processDirWithBrick(String dir, boolean isChild, int currentWeapon) {
        int indexOfRedirect = dir.indexOf(Dir.REDIRECT);
        String processedDir = dir.replace(Dir.REDIRECT, "");

        int indexOfAction = dir.indexOf(Dir.ACTION);
        if (indexOfAction == 0 && dir.length() > 1) {
            String currentDirection = isChild
                    ? moveDirection(InGameInfo.childCurrentPosition, InGameInfo.childLastPosition)
                    : moveDirection(InGameInfo.playerCurrentPosition, InGameInfo.playerLastPosition);
            String targetDirection = dir.substring(1, 2);
            if (!currentDirection.isEmpty()
                    && !currentDirection.equals(targetDirection)) {
                if (isChild) {
                    InGameInfo.childLastPosition = InGameInfo.childCurrentPosition;
                } else {
                    InGameInfo.playerLastPosition = InGameInfo.playerCurrentPosition;
                }

                List<Order> orders = new ArrayList<>(List.of(
                        new Dir(targetDirection, isChild),
                        new Wait(Wait.REDIRECT, isChild),
                        new Dir(Dir.ACTION, isChild)
                ));

                if (currentWeapon != 1) {
                    orders.add(new Action(Action.SWITCH_WEAPON, isChild));
                }

                return orders;
            }
        }

        if (indexOfRedirect == 2) {
            List<Order> orders = new ArrayList<>(List.of(new Dir(processedDir.substring(0, 2), isChild), new Wait(Wait.REDIRECT, isChild)));
            if (indexOfAction == 2) {
                orders.add(new Dir(Dir.ACTION, isChild));
            }
            if (currentWeapon != 1) {
                orders.add(new Action(Action.SWITCH_WEAPON, isChild));
            }
            return orders;
        } else {
            String shortenDir = processedDir.length() > 3 ? processedDir.substring(0, 3) : processedDir;

            List<Order> orders = new ArrayList<>();
            if (processedDir.contains(Dir.ACTION) && currentWeapon != 1) {
                orders.add(new Action(Action.SWITCH_WEAPON, isChild));
            }
            orders.add(new Dir(shortenDir, isChild));

            return orders;
        }
    }

    public static String moveDirection(Position curr, Position last) {
        if (curr == null || last == null) {
            return Dir.INVALID;
        }

        if (curr.row == last.row) {
            if (curr.col < last.col) {
                return Dir.LEFT; // Moving left
            } else if (curr.col > last.col) {
                return Dir.RIGHT; // Moving right
            }
        } else if (curr.col == last.col) {
            if (curr.row < last.row) {
                return Dir.UP; // Moving up
            } else if (curr.row > last.row) {
                return Dir.DOWN; // Moving down
            }
        }

        return Dir.INVALID;
    }
}
