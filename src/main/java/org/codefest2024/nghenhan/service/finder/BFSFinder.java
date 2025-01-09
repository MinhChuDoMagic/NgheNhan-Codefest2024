package org.codefest2024.nghenhan.service.finder;

import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.util.Comparator;
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

    public Node findSafe(int[][] map, Position curr, MapSize size, List<Bomb> bombs, List<Hammer> hammers, List<Wind> winds) {
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

    public Node findEnemy(int[][] map, Position curr, Position enemy, MapSize size) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            int row = currNode.row;
            int col = currNode.col;

            if (SkillUtils.inHammerRange(currNode, enemy)) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isSafePath(newRow, newCol, map, visited, size)) {
                    StringBuilder newCommands = new StringBuilder(currNode.commands);
                    newCommands.append(dir[2]);

                    queue.add(new Node(newRow, newCol, currNode, newCommands));
                }
            }
        }
        return new Node(curr.row, curr.col, null, null);
    }

    public String oneSafeStep(int[][] map, Position curr, List<Bomb> bombs, List<Hammer> hammers, List<Wind> winds) {
        List<int[]> directions = CalculateUtils.getDirections();

        Bomb nearestBomb = bombs.stream().min(Comparator.comparing(bomb -> CalculateUtils.manhattanDistance(curr, bomb))).orElse(null);
        if (nearestBomb != null) {
            return directions.stream()
                    .filter(dir -> isSafe(map, new Position(curr.row + dir[0], curr.col + dir[1]), bombs, hammers, winds))
                    .min(Comparator.comparing(dir -> CalculateUtils.manhattanDistance(nearestBomb, new Position(curr.row + dir[0], curr.col + dir[1]))))
                    .map(dir -> String.valueOf(dir[2]))
                    .orElse("");
        }

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
                && map[row][col] != MapInfo.PRISON
                && map[row][col] != MapInfo.PLAYER
                && map[row][col] != MapInfo.CHILD
                && map[row][col] != MapInfo.ENEMY
                && map[row][col] != MapInfo.ENEMY_CHILD
                && map[row][col] != MapInfo.BOMB;
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

    private boolean isSafePath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return isValidBoxPath(row, col, map, visited, size)
                && map[row][col] != MapInfo.BOX
                && map[row][col] != MapInfo.BOMB_EXPLODE
                && map[row][col] != MapInfo.HAMMER_EXPLODE
                && map[row][col] != MapInfo.WIND
                && map[row][col] != MapInfo.CAPTURED_BADGE;
    }

    private boolean isSafe(int[][] map, Position curr, List<Bomb> bombs, List<Hammer> hammers, List<Wind> winds) {
        return isSafeFromBombs(curr, bombs)
                && isSafeFromHammers(curr, hammers)
                && isSafeFromWinds(map, curr, winds);
    }

    private boolean isSafeFromBombs(Position curr, List<Bomb> bombs) {
        return bombs.stream().noneMatch(bomb -> SkillUtils.isHitBomb(curr, bomb));
    }

    private boolean isSafeFromHammers(Position curr, List<Hammer> hammers) {
        return hammers.stream().noneMatch(hammer -> SkillUtils.isHitHammer(curr, hammer));
    }

    public static boolean isSafeFromWinds(int[][] map, Position curr, List<Wind> winds) {
        return winds.stream().noneMatch(wind -> SkillUtils.isHitWind(map, curr, wind));
    }
}
