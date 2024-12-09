package org.codefest2024.nghenhan.service.handler.info;

import org.codefest2024.nghenhan.service.socket.data.Position;

public class InGameInfo {
    public static long myPlayerLastSkillTime = 0;
    public static long myChildLastSkillTime = 0;
    public static long enemyLastSkillTime = 0;
    public static long enemyChildLastSkillTime = 0;

    public static int playerType = 0;
    public static int enemyType = 0;

    public static boolean isEnemyStun = false;

    public static Position playerLastPosition;
    public static Position childLastPosition;
    public static Position enemyLastPosition;
    public static Position enemyChildLastPosition;
}
