package org.codefest2024.nghenhan.utils;

import org.codefest2024.nghenhan.service.finder.BFSFinder;
import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.time.Instant;
import java.util.List;

public class SkillUtils {
    private SkillUtils() {
    }

    public static boolean isBombCooldown(int cooldown, boolean isChild){
        return (isChild && Instant.now().toEpochMilli() - InGameInfo.childLastBombTime <= cooldown)
                || (!isChild && Instant.now().toEpochMilli() - InGameInfo.playerLastBombTime <= cooldown);
    }

    public static boolean isSkillCooldown(boolean isChild) {
        long cooldown = switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> Hammer.COOL_DOWN;
            case Player.SEA -> Wind.COOL_DOWN;
            default -> 0L;
        } * 1000;

        return (isChild && Instant.now().toEpochMilli() - InGameInfo.childLastSkillTime <= cooldown)
                || (!isChild && Instant.now().toEpochMilli() - InGameInfo.playerLastSkillTime <= cooldown);
    }

    public static boolean isEnemySkillCooldown(boolean isChild) {
        long cooldown = switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> Hammer.COOL_DOWN;
            case Player.SEA -> Wind.COOL_DOWN;
            default -> 0L;
        } * 1000;

        return (isChild && Instant.now().toEpochMilli() - InGameInfo.enemyChildLastSkillTime <= cooldown)
                || (!isChild && Instant.now().toEpochMilli() - InGameInfo.enemyLastSkillTime <= cooldown);
    }

    public static boolean isHitBomb(Position curr, Bomb bomb) {
        return (curr.row == bomb.row && Math.abs(curr.col - bomb.col) <= bomb.power)
                || (curr.col == bomb.col && Math.abs(curr.row - bomb.row) <= bomb.power);
    }

    public static boolean isHitHammer(Position curr, Hammer hammer) {
        return Math.abs(curr.col - hammer.destination.col) <= hammer.power
                && Math.abs(curr.row - hammer.destination.row) <= hammer.power;
    }

    public static boolean isHitWind(int[][] map, Position curr, Wind wind) {
        int row = wind.currentRow;
        int col = wind.currentCol;

        switch (wind.direction) {
            case 1: // Left
                while (col >= 0) {
                    if (row == curr.row && col == curr.col) return true; // wind can hit the player
                    if (map[row][col] != 0) break; // wind is blocked
                    col--;
                }
                break;

            case 2: // Right
                while (col < map[0].length) {
                    if (row == curr.row && col == curr.col) return true;
                    if (map[row][col] != 0) break;
                    col++;
                }
                break;

            case 3: // Up
                while (row >= 0) {
                    if (row == curr.row && col == curr.col) return true;
                    if (map[row][col] != 0) break;
                    row--;
                }
                break;

            case 4: // Down
                while (row < map.length) {
                    if (row == curr.row && col == curr.col) return true;
                    if (map[row][col] != 0) break;
                    row++;
                }
                break;

            default:
                return false;
        }

        return false;
    }

    public static boolean inHammerRange(Position curr, Position enemy) {
        return Math.abs(curr.col - enemy.col) < 3 && Math.abs(curr.row - enemy.row) < 5 ||
                Math.abs(curr.row - enemy.row) < 3 && Math.abs(curr.col - enemy.col) < 5;
    }

    public static String windDirection(int[][] map, Position player, Position enemy) {

        // Check Left Direction
        if (Math.abs(enemy.row - player.row) <= 1 && enemy.col < player.col) {
            var winds = List.of(
                    new Wind(player.row - 1, player.col - 1, 1),
                    new Wind(player.row, player.col - 1, 1),
                    new Wind(player.row + 1, player.col - 1, 1));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.LEFT;
            }
        }

        // Check Right Direction
        if (Math.abs(enemy.row - player.row) <= 1 && enemy.col > player.col) {
            var winds = List.of(
                    new Wind(player.row - 1, player.col + 1, 2),
                    new Wind(player.row, player.col + 1, 2),
                    new Wind(player.row + 1, player.col + 1, 2));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.RIGHT;
            }
        }

        // Check Up Direction
        if (Math.abs(enemy.col - player.col) <= 1 && enemy.row < player.row) {
            var winds = List.of(
                    new Wind(player.row - 1, player.col - 1, 3),
                    new Wind(player.row - 1, player.col, 3),
                    new Wind(player.row - 1, player.col + 1, 3));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.UP;
            }
        }

        // Check Down Direction
        if (Math.abs(enemy.col - player.col) <= 1 && enemy.row > player.row) {
            var winds = List.of(
                    new Wind(player.row + 1, player.col - 1, 4),
                    new Wind(player.row + 1, player.col, 4),
                    new Wind(player.row + 1, player.col + 1, 4));

            if (!BFSFinder.isSafeFromWinds(map, enemy, winds)) {
                return Dir.DOWN;
            }
        }

        return Dir.INVALID;
    }
}
