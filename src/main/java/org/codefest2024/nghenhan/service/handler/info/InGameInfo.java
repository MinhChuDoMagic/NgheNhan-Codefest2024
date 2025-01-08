package org.codefest2024.nghenhan.service.handler.info;

import org.codefest2024.nghenhan.service.socket.data.Bomb;
import org.codefest2024.nghenhan.service.socket.data.Position;

import java.util.List;

public class InGameInfo {
    public static long playerLastSkillTime = 0;
    public static long childLastSkillTime = 0;
    public static long enemyLastSkillTime = 0;
    public static long enemyChildLastSkillTime = 0;

    public static long playerLastBombTime = 0;
    public static long childLastBombTime = 0;
    public static long enemyLastBombTime = 0;
    public static long enemyChildLastBombTime = 0;

    public static List<Bomb> lastBombs = List.of();

    public static int playerType = 0;
    public static int enemyType = 0;

    public static long enemyLastStunTime = 0;
    public static long enemyChildLastStunTime = 0;

    public static int playerLives = 0;
    public static Position playerCurrentPosition = new Position(1,1);
    public static Position playerLastPosition = new Position(1,1);
    public static Position childCurrentPosition = new Position(1,1);
    public static Position childLastPosition = new Position(1,1);
}
