package org.codefest2024.nghenhan.service.caculator.finder;

import org.codefest2024.nghenhan.service.caculator.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class KeepDistanceFinderVer2 {
    private static KeepDistanceFinderVer2 instance;
    private final int SPOIL_POINT = 5;
    private final int BRICK_POINT = 6;
    private final int ENEMY_RATIO = 2;

    private KeepDistanceFinderVer2() {
    }

    public static KeepDistanceFinderVer2 getInstance() {
        if (instance == null) {
            instance = new KeepDistanceFinderVer2();
        }
        return instance;
    }

    public AStarNode find(int[][] map, Position curr, Position enemy, int targetValue, MapSize size) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        pq.add(new AStarNode(curr.row, curr.col, 0, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (map[row][col] == targetValue) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String move = Integer.toString(dir[2]);

                if (isValidBoxPath(newRow, newCol, map, visited, size)) {
                    double newCost = currNode.g;
                    StringBuilder newCommands = new StringBuilder(currNode.commands);

                    if (map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] == MapInfo.DESTROYED) {
                        newCost += 1; // Empty cell
                    } else if (map[newRow][newCol] == MapInfo.BRICK) {
                        String currentCommand = currNode.commands.toString();
                        if (!currentCommand.isEmpty() && !currentCommand.substring(currentCommand.length() - 1).equals(move)) {
                            newCommands.append(move);
                        }
                        newCommands.append(Dir.ACTION);
                        newCost += BRICK_POINT; // 1s to destroy + 0.2s to move
                    } else if (map[newRow][newCol] == MapInfo.SPOIL) {
                        newCost -= SPOIL_POINT;
                    }

                    // Add move command
                    newCommands.append(move);
                    double heuristic = 16 - 1.0 * ENEMY_RATIO * CalculateUtils.manhattanDistance(new Position(newRow, newCol), enemy);
                    pq.add(new AStarNode(newRow, newCol, newCost, heuristic, currNode, newCommands));
                }
            }
        }

        return new AStarNode(curr.row, curr.col, 0, 0, null, null);
    }

    public AStarNode keepDistance(int[][] map, Position curr, Position enemy, MapSize size) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        pq.add(new AStarNode(curr.row, curr.col, 0, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (!CalculateUtils.enemyNearby(currNode, enemy)) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String move = Integer.toString(dir[2]);

                if (isRunPath(newRow, newCol, map, visited, size)) {
                    double newCost = currNode.g;
                    StringBuilder newCommands = new StringBuilder(currNode.commands);

                    if (map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] == MapInfo.DESTROYED) {
                        newCost += 1; // Empty cell
                    } else if (map[newRow][newCol] == MapInfo.BRICK) {
                        String currentCommand = currNode.commands.toString();
                        if (!currentCommand.isEmpty() && !currentCommand.substring(currentCommand.length() - 1).equals(move)) {
                            newCommands.append(move);
                        }
                        newCommands.append(Dir.ACTION);
                        newCost += BRICK_POINT; // 1s to destroy + 0.2s to move
                    } else if (map[newRow][newCol] == MapInfo.SPOIL) {
                        newCost -= SPOIL_POINT;
                    }

                    // Add move command
                    newCommands.append(move);
                    double heuristic = 16 - 1.0 * ENEMY_RATIO * CalculateUtils.manhattanDistance(new Position(newRow, newCol), enemy);
                    pq.add(new AStarNode(newRow, newCol, newCost, heuristic, currNode, newCommands));
                }
            }
        }

        return new AStarNode(curr.row, curr.col, 0, 0, null, null);
    }

    public AStarNode findSafe(int[][] map, Position curr, Position enemy, MapSize size, List<Bomb> bombs, List<WeaponHammer> hammers, List<WeaponWind> winds) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        pq.add(new AStarNode(curr.row, curr.col, 0, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (isSafe(map, currNode, bombs, hammers, winds)) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String move = Integer.toString(dir[2]);

                if (isPath(newRow, newCol, map, visited, size)) {
                    double newCost = currNode.g;
                    StringBuilder newCommands = new StringBuilder(currNode.commands);

                    if (map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] == MapInfo.DESTROYED) {
                        newCost += 1; // Empty cell
                    } else if (map[newRow][newCol] == MapInfo.SPOIL) {
                        newCost -= SPOIL_POINT;
                    }

                    // Add move command
                    newCommands.append(move);
                    double heuristic = 16 - 1.0 * ENEMY_RATIO * CalculateUtils.manhattanDistance(new Position(newRow, newCol), enemy);
                    pq.add(new AStarNode(newRow, newCol, newCost, heuristic, currNode, newCommands));
                }
            }
        }

        return new AStarNode(curr.row, curr.col, 0, 0, null, null);
    }

    private boolean isValidBoxPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.PRISON
                && map[row][col] != MapInfo.PLAYER
                && map[row][col] != MapInfo.CHILD
                && map[row][col] != MapInfo.ENEMY
                && map[row][col] != MapInfo.ENEMY_CHILD
                && map[row][col] != MapInfo.BOMB
                && map[row][col] != MapInfo.BOMB_EXPLODE
                && map[row][col] != MapInfo.HAMMER_EXPLODE
                && map[row][col] != MapInfo.WIND
                && map[row][col] != MapInfo.CAPTURED_BADGE;
    }

    private boolean isPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.PRISON
                && map[row][col] != MapInfo.PLAYER
                && map[row][col] != MapInfo.CHILD
                && map[row][col] != MapInfo.ENEMY
                && map[row][col] != MapInfo.ENEMY_CHILD
                && map[row][col] != MapInfo.BOMB
                && map[row][col] != MapInfo.WIND
                && map[row][col] != MapInfo.CAPTURED_BADGE
                && map[row][col] != MapInfo.BOX
                && map[row][col] != MapInfo.BRICK;
    }

    private boolean isRunPath(int row, int col, int[][] map, boolean[][] visited, MapSize size){
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.PRISON
                && map[row][col] != MapInfo.PLAYER
                && map[row][col] != MapInfo.CHILD
                && map[row][col] != MapInfo.ENEMY
                && map[row][col] != MapInfo.ENEMY_CHILD
                && map[row][col] != MapInfo.BOMB
                && map[row][col] != MapInfo.WIND
                && map[row][col] != MapInfo.CAPTURED_BADGE
                && map[row][col] != MapInfo.BOX
                && map[row][col] != MapInfo.HAMMER_EXPLODE
                && map[row][col] != MapInfo.BOMB_EXPLODE;
    }

    private boolean isSafe(int[][] map, Position curr, List<Bomb> bombs, List<WeaponHammer> hammers, List<WeaponWind> winds) {
        return isSafeFromBombs(curr, bombs)
                && isSafeFromHammers(curr, hammers)
                && isSafeFromWinds(map, curr, winds);
    }

    private boolean isSafeFromBombs(Position curr, List<Bomb> bombs) {
        return bombs.stream().noneMatch(bomb -> CalculateUtils.isHitBomb(curr, bomb));
    }

    private boolean isSafeFromHammers(Position curr, List<WeaponHammer> hammers) {
        return hammers.stream().noneMatch(hammer -> CalculateUtils.isHitHammer(curr, hammer));
    }

    public static boolean isSafeFromWinds(int[][] map, Position curr, List<WeaponWind> winds) {
        return winds.stream().noneMatch(wind -> CalculateUtils.isHitWind(map, curr, wind));
    }
}
