package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.socket.data.Action;
import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.Order;
import org.codefest2024.nghenhan.service.socket.data.Wait;

import java.util.ArrayList;
import java.util.List;

public class FinderUtils {
    private FinderUtils() {
    }

    public static List<Order> processDirWithBrick(String dir, boolean isChild, int currentWeapon){
        int indexOfB = dir.indexOf(Dir.ACTION);
        if (indexOfB == 2) {
            return List.of(new Dir(dir.substring(0,2), isChild), new Wait(Wait.REDIRECT, isChild));
        } else {
            String shortenDir = dir.length() > 3 ? dir.substring(0, 3) : dir;

            List<Order> orders = new ArrayList<>();
            if((indexOfB == 0 || indexOfB == 1) && currentWeapon != 1){
                orders.add(new Action(Action.SWITCH_WEAPON, isChild));
            }
            orders.add(new Dir(shortenDir, isChild));

            return orders;
        }
    }
}
