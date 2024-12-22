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
        int indexOfRedirect = dir.indexOf(Dir.REDIRECT);
        String processedDir = dir.replace(Dir.REDIRECT, "");
        if(indexOfRedirect == 2) {
            return List.of(new Dir(processedDir.substring(0,2), isChild), new Wait(Wait.REDIRECT, isChild));
        } else {
            String shortenDir = processedDir.length() > 3 ? processedDir.substring(0, 3) : processedDir;

            List<Order> orders = new ArrayList<>();
            if(processedDir.contains(Dir.ACTION) && currentWeapon != 1){
                orders.add(new Action(Action.SWITCH_WEAPON, isChild));
            }
            orders.add(new Dir(shortenDir, isChild));

            return orders;
        }
    }
}
