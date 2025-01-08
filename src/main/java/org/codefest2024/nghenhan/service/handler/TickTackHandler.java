package org.codefest2024.nghenhan.service.handler;

import org.codefest2024.nghenhan.service.handler.info.InGameInfo;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.service.strategy.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.constant.Constants;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class TickTackHandler {
    private final Strategy farmStrategy = new FarmStrategy();
    private final Strategy seaAttackStrategy = new SeaAttackStrategy();
    private final Strategy doNothingStrategy = new DoNothingStrategy();
    private final Strategy hitAndRunStrategy = new HitAndRunStrategy();
    private final Strategy powerFarmStrategy = new PowerFarmStrategy();
    private final Strategy testStrategy = new TestStrategy();

    public List<Order> handle(GameInfo gameInfo, StrategyEnum strategyEnum) {
        updateMapInfo(gameInfo.map_info);
        return getStrategy(strategyEnum).find(gameInfo);
    }

    private Strategy getStrategy(StrategyEnum strategyEnum) {
        return switch (strategyEnum) {
            case FARM_STRATEGY -> farmStrategy;
            case SEA_DIRECT_ATTACK -> seaAttackStrategy;
            case HIT_AND_RUN -> hitAndRunStrategy;
            case POWER_FARM -> powerFarmStrategy;
            case TEST -> testStrategy;
            case DO_NOTHING -> doNothingStrategy;
        };
    }

    private void updateMapInfo(MapInfo mapInfo) {
        updatePlayers(mapInfo);
        updateBombs(mapInfo);
        updateMap(mapInfo);
        updateEnemyType(mapInfo.enemy);
        updateSkillData(mapInfo.weaponHammers, mapInfo.weaponWinds);
        updateWeaponPlaces(mapInfo);
        updateStunTime(mapInfo.enemy, mapInfo.enemyChild);
        checkIsHit(mapInfo.player);
        updatePosition(mapInfo.player, mapInfo.child);
    }

    private void updatePosition(Player player, Player child) {
        if (player != null && !InGameInfo.playerCurrentPosition.equals(player.currentPosition)) {
            InGameInfo.playerLastPosition = InGameInfo.playerCurrentPosition;
            InGameInfo.playerCurrentPosition = player.currentPosition;
        }

        if (child != null && !InGameInfo.childCurrentPosition.equals(child.currentPosition)) {
            InGameInfo.childLastPosition = InGameInfo.childCurrentPosition;
            InGameInfo.childCurrentPosition = child.currentPosition;
        }
    }

    private void checkIsHit(Player player) {
        if (player != null && player.lives != InGameInfo.playerLives) {
            InGameInfo.playerLives = player.lives;
            System.err.println("PLAYER IS HIT");
        }
    }

    private void updatePlayers(MapInfo mapInfo) {
        for (Player player : mapInfo.players) {
            if (player.id.startsWith(Constants.KEY_TEAM)) {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    mapInfo.child = player;
                    mapInfo.playerIsMarried = true;
                } else {
                    mapInfo.player = player;
                }
            } else {
                if (player.id.endsWith(Constants.KEY_CHILD)) {
                    mapInfo.enemyChild = player;
                    mapInfo.enemyIsMarried = true;
                } else {
                    mapInfo.enemy = player;
                }
            }
        }
    }

    private void updateWeaponPlaces(MapInfo mapInfo) {
        for (WeaponPlace weaponPlace : mapInfo.weaponPlaces) {
            if (weaponPlace.playerId.startsWith(Constants.KEY_TEAM)) {
                if (weaponPlace.playerId.endsWith(Constants.KEY_CHILD)) {
                    mapInfo.childWeaponPlace = weaponPlace;
                } else {
                    mapInfo.playerWeaponPlace = weaponPlace;
                }
            } else {
                if (weaponPlace.playerId.endsWith(Constants.KEY_CHILD)) {
                    mapInfo.enemyChildWeaponPlace = weaponPlace;
                } else {
                    mapInfo.enemyWeaponPlace = weaponPlace;
                }
            }
        }
    }

    private void updateStunTime(Player enemy, Player enemyChild) {
        long currentTime = Instant.now().toEpochMilli();
        if (enemy != null && enemy.isStun && currentTime - InGameInfo.enemyLastStunTime > Bomb.STUN_COOLDOWN * 1000) {
            InGameInfo.enemyLastStunTime = currentTime;
        }

        if (enemyChild != null && enemyChild.isStun && currentTime - InGameInfo.enemyChildLastStunTime > Bomb.STUN_COOLDOWN * 1000) {
            InGameInfo.enemyChildLastStunTime = currentTime;
        }
    }

    private void updateBombs(MapInfo mapInfo) {
        mapInfo.bombs = Stream
                .concat(
                        mapInfo.bombs.stream(),
                        InGameInfo.lastBombs.stream().filter(bomb -> Instant.now().toEpochMilli() - bomb.createdAt < Bomb.BOMB_TIME * 1000)
                )
                .distinct()
                .toList();

        InGameInfo.lastBombs = mapInfo.bombs;

        for (Bomb bomb : mapInfo.bombs) {
            if (bomb.playerId.startsWith(Constants.KEY_TEAM)) {
                if (bomb.playerId.endsWith(Constants.KEY_CHILD)) {
                    mapInfo.childBombs.add(bomb);
                    InGameInfo.childLastBombTime = Math.max(InGameInfo.childLastBombTime, bomb.createdAt);
                } else {
                    mapInfo.playerBombs.add(bomb);
                    InGameInfo.playerLastBombTime = Math.max(InGameInfo.playerLastBombTime, bomb.createdAt);
                }
            } else {
                if (bomb.playerId.endsWith(Constants.KEY_CHILD)) {
                    mapInfo.enemyChildBombs.add(bomb);
                    InGameInfo.enemyChildLastBombTime = Math.max(InGameInfo.enemyChildLastBombTime, bomb.createdAt);
                } else {
                    mapInfo.enemyBombs.add(bomb);
                    InGameInfo.enemyLastBombTime = Math.max(InGameInfo.enemyLastBombTime, bomb.createdAt);
                }
            }
        }
    }

    private void updateMap(MapInfo mapInfo) {
        updateSpoilsInMap(mapInfo.map, mapInfo.spoils);
        updateBombsInMap(mapInfo.map, mapInfo.size, mapInfo.bombs);
        updateHammersInMap(mapInfo.map, mapInfo.size, mapInfo.weaponHammers);
        updateWindsInMap(mapInfo.map, mapInfo.weaponWinds);
        updatePlayersInMap(mapInfo.map, mapInfo.player, mapInfo.child, mapInfo.enemy, mapInfo.enemyChild);
    }

    private void updateEnemyType(Player enemy) {
        if (InGameInfo.enemyType == 0 && enemy != null) {
            InGameInfo.enemyType = enemy.transformType;
        }
    }

    private void updateSkillData(List<Hammer> hammers, List<Wind> winds) {
        for (Hammer hammer : hammers) {
            if (hammer.playerId.startsWith(Constants.KEY_TEAM)) {
                if (hammer.playerId.endsWith(Constants.KEY_CHILD)) {
                    InGameInfo.childLastSkillTime = hammer.createdAt;
                } else {
                    InGameInfo.playerLastSkillTime = hammer.createdAt;
                }
            } else {
                if (hammer.playerId.endsWith(Constants.KEY_CHILD)) {
                    InGameInfo.enemyChildLastSkillTime = hammer.createdAt;
                } else {
                    InGameInfo.enemyLastSkillTime = hammer.createdAt;
                }
            }
        }

        for (Wind wind : winds) {
            long currentTime = Instant.now().toEpochMilli();
            if (wind.playerId.startsWith(Constants.KEY_TEAM)) {
                if (wind.playerId.endsWith(Constants.KEY_CHILD)
                        && currentTime - InGameInfo.childLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.childLastSkillTime = currentTime;
                } else if (currentTime - InGameInfo.playerLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.playerLastSkillTime = currentTime;
                }
            } else {
                if (wind.playerId.endsWith(Constants.KEY_CHILD)
                        && currentTime - InGameInfo.enemyChildLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.enemyChildLastSkillTime = currentTime;
                } else if (currentTime - InGameInfo.enemyLastSkillTime > Wind.COOL_DOWN * 1000) {
                    InGameInfo.enemyLastSkillTime = currentTime;
                }
            }
        }
    }

    private void updateSpoilsInMap(int[][] map, List<Spoil> spoils) {
        spoils.forEach(spoil -> map[spoil.row][spoil.col] = MapInfo.SPOIL);
    }

    private void updateBombsInMap(int[][] map, MapSize size, List<Bomb> bombs) {
        List<int[]> directions = CalculateUtils.getDirections();

        for (Bomb bomb : bombs) {
            map[bomb.row][bomb.col] = MapInfo.BOMB;
            directions.forEach(dir -> markBombExplosion(map, bomb, dir[0], dir[1], size));
        }
    }

    private void updateHammersInMap(int[][] map, MapSize size, List<Hammer> hammers) {
        hammers.forEach(hammer -> markHammerExplosion(map, hammer, size));
    }

    private void updateWindsInMap(int[][] map, List<Wind> winds) {
        winds.forEach(wind -> map[wind.currentRow][wind.currentCol] = MapInfo.WIND);
    }

    private void updatePlayersInMap(int[][] map, Player player, Player child, Player enemy, Player enemyChild) {
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

    private void markBombExplosion(int[][] map, Bomb bomb, int rowDir, int colDir, MapSize size) {
        for (int i = 1; i <= bomb.power; i++) {
            int newRow = bomb.row + i * rowDir;
            int newCol = bomb.col + i * colDir;

            // Check boundaries
            if (newRow < 0 || newRow >= size.rows || newCol < 0 || newCol >= size.cols) {
                break;
            }

            if (map[newRow][newCol] == MapInfo.BLANK
                    || map[newRow][newCol] == MapInfo.DESTROYED
                    || map[newRow][newCol] == MapInfo.SPOIL) {
                map[newRow][newCol] = MapInfo.BOMB_EXPLODE;
            }
        }
    }

    private void markHammerExplosion(int[][] map, Hammer hammer, MapSize mapSize) {
        int centerRow = hammer.destination.row;
        int centerCol = hammer.destination.col;
        int power = hammer.power;

        for (int row = centerRow - power; row <= centerRow + power; row++) {
            for (int col = centerCol - power; col <= centerCol + power; col++) {
                // Check boundaries
                if (row < 0 || row >= mapSize.rows || col < 0 || col >= mapSize.cols) {
                    continue;
                }

                if (map[row][col] == MapInfo.BLANK
                        || map[row][col] == MapInfo.DESTROYED
                        || map[row][col] == MapInfo.SPOIL) {
                    map[row][col] = MapInfo.HAMMER_EXPLODE;
                }
            }
        }
    }
}
