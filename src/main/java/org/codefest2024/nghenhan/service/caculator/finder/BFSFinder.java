package org.codefest2024.nghenhan.service.caculator.finder;

import org.codefest2024.nghenhan.service.caculator.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFSFinder {
    private static BFSFinder instance;

    private BFSFinder() {
    }

    public static BFSFinder getInstance() {
        if (instance == null) {
            instance = new BFSFinder();
        }
        return instance;
    }

    public Node find(int[][] map, Position curr, int targetValue, MapSize size) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            int row = currNode.row;
            int col = currNode.col;

            if (map[row][col] == targetValue) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if ((targetValue == MapInfo.BOX && isValidBoxPath(newRow, newCol, map, visited, size)
                        || (targetValue == MapInfo.BRICK && isValidBrickPath(newRow, newCol, map, visited, size)))) {
                    StringBuilder newCommands = new StringBuilder(currNode.commands);
                    newCommands.append(dir[2]);
                    queue.add(new Node(newRow, newCol, currNode, newCommands));
                }
            }
        }
        return new Node(curr.row, curr.col, null, null);
    }

    public Node findSafe(int[][] map, Position curr, MapSize size, List<Bomb> bombs, List<WeaponHammer> hammers, List<WeaponWind> winds) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            int row = currNode.row;
            int col = currNode.col;

            if (isSafe(map, currNode, bombs, hammers, winds)) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isPath(newRow, newCol, map, visited, size)) {
                    StringBuilder newCommands = new StringBuilder(currNode.commands);
                    newCommands.append(dir[2]);

                    queue.add(new Node(newRow, newCol, currNode, newCommands));
                }
            }
        }
        return new Node(curr.row, curr.col, null, null);
    }

    public String oneSafeStep(int[][] map, Position curr, List<Bomb> bombs, List<WeaponHammer> hammers, List<WeaponWind> winds) {
        List<int[]> directions = CalculateUtils.getDirections();
        for (int[] dir : directions) {
            int newRow = curr.row + dir[0];
            int newCol = curr.col + dir[1];
            if (isSafe(map, new Position(newRow, newCol), bombs, hammers, winds)) {
                return String.valueOf(dir[2]);
            }
        }
        return "";
    }

    private boolean isValidBoxPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.BRICK
                && map[row][col] != MapInfo.PRISON;
    }

    private boolean isValidBrickPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.BOX
                && map[row][col] != MapInfo.PRISON;
    }

    private boolean isPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return isValidBoxPath(row, col, map, visited, size)
                && map[row][col] != MapInfo.BOX;
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
