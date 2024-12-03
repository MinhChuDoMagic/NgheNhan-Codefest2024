package org.codefest2024.nghenhan.service.caculator.seaAttack;

import org.codefest2024.nghenhan.service.caculator.Strategy;
import org.codefest2024.nghenhan.service.caculator.farming.NormalFarmStrategy;
import org.codefest2024.nghenhan.service.caculator.finder.AStarFinder;
import org.codefest2024.nghenhan.service.caculator.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.time.Instant;
import java.util.List;

public class SeaAttackStrategy implements Strategy {
    private final NormalFarmStrategy normalFarmStrategy = new NormalFarmStrategy();
    private final AStarFinder aStarFinder = AStarFinder.getInstance();

    @Override
    public List<Order> find(GameInfo gameInfo) {
        MapInfo mapInfo = gameInfo.map_info;
        Player myPlayer = null;
        Player enemyPlayer = null;
        Player myChild = null;
        Player enemyChild = null;
        for (Player player : mapInfo.players) {
            if (player.id.startsWith(Constants.KEY_TEAM)) {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    myChild = player;
                } else {
                    myPlayer = player;
                }
            } else {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    enemyChild = player;
                } else {
                    enemyPlayer = player;
                }
            }
        }

        updateMap(mapInfo.map, myPlayer, myChild, enemyPlayer, enemyChild, mapInfo.bombs);

        if (myPlayer != null) {
            if (!myPlayer.hasTransform) {
                return normalFarmStrategy.find(gameInfo, myPlayer);
            } else {
                return playerStrategy(mapInfo, myPlayer, enemyPlayer);
            }
        }

        return List.of();
    }

    private List<Order> playerStrategy(MapInfo mapInfo, Player player, Player enemy) {
        if(!enemy.isStun && !isCooldown(player.isChild)){
            InGameInfo.myPlayerLastSkillTime = Instant.now().toEpochMilli();
            return List.of(new Action(Action.USE_WEAPON, player.isChild));
        }
        String dir = aStarFinder.find(mapInfo.map, player.currentPosition, enemy.currentPosition, mapInfo.size);
        return List.of(new Dir(processDir(dir)));
    }

    private  String processDir(String dir) {
        int indexOfB = dir.indexOf('b');
        if (indexOfB == 2 && dir.charAt(0) != dir.charAt(1)) {
            return dir.substring(0, 2);
        }else {
            return dir.length() > 3 ? dir.substring(0, 3) : dir;
        }
    }

    private boolean isCooldown(boolean isChild) {
        long cooldown = switch (InGameInfo.playerType) {
            case Player.MOUNTAIN -> WeaponHammer.COOL_DOWN;
            case Player.SEA -> WeaponWind.COOL_DOWN;
            default -> 0L;
        } * 1000;

        return (isChild && Instant.now().toEpochMilli() - InGameInfo.myChildLastSkillTime <= cooldown)
                || (!isChild && Instant.now().toEpochMilli() - InGameInfo.myPlayerLastSkillTime <= cooldown);
    }

    private void updateMap(int[][] map, Player player, Player child, Player enemy, Player enemyChild, List<Bomb> bombs) {
        updateMap(map, bombs);
        updateMap(map, player, child, enemy, enemyChild);
    }

    private void updateMap(int[][] map, Player player, Player child, Player enemy, Player enemyChild) {
        if (player != null) {
            if (map[player.currentPosition.row][player.currentPosition.col] == MapInfo.BADGE) {
                map[player.currentPosition.row][player.currentPosition.col] = MapInfo.CAPTURED_BADGE;
            } else {
                map[player.currentPosition.row][player.currentPosition.col] = MapInfo.PLAYER;
            }
        }

        if (child != null) {
            map[child.currentPosition.row][child.currentPosition.col] = MapInfo.CHILD;
        }

        if (enemy != null) {
            if (map[enemy.currentPosition.row][enemy.currentPosition.col] == MapInfo.BADGE) {
                map[enemy.currentPosition.row][enemy.currentPosition.col] = MapInfo.CAPTURED_BADGE;
            } else {
                map[enemy.currentPosition.row][enemy.currentPosition.col] = MapInfo.ENEMY;
            }
        }

        if (enemyChild != null) {
            map[enemyChild.currentPosition.row][enemyChild.currentPosition.col] = MapInfo.ENEMY_CHILD;
        }
    }

    private void updateMap(int[][] map, List<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            map[bomb.row][bomb.col] = MapInfo.BOMB;
        }
    }
}
