package org.codefest2024.nghenhan.service.finder;

import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.util.*;

public class BombPlaceFinder {
    private static BombPlaceFinder instance;
    private final double BRICK_POINT = 8.09;
    private final double BOMB_RATIO = 3.41;

    private BombPlaceFinder() {
    }

    public static BombPlaceFinder getInstance() {
        if (instance == null) {
            instance = new BombPlaceFinder();
        }
        return instance;
    }

    public AStarNode find(int[][] map, Position curr, int power, MapSize size) {
        AStarNode optimalNode = null;
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getG));
        pq.add(new AStarNode(curr.row, curr.col, 0, -1.0 * BOMB_RATIO * calculateBoxesDestroyed(map, curr, power, size), null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            if (optimalNode != null && currNode.g - 4 * BOMB_RATIO > optimalNode.getF()) {
                break;
            }

            if (currNode.h != 0 && (optimalNode == null || optimalNode.getF() > currNode.getF())) {
                optimalNode = currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String move = Integer.toString(dir[2]);

                if (isValid(newRow, newCol, map, visited, size)) {
                    double newCost = currNode.g;
                    StringBuilder newCommands = new StringBuilder(currNode.commands);

                    if (map[newRow][newCol] == MapInfo.BLANK
                            || map[newRow][newCol] == MapInfo.DESTROYED
                            || map[newRow][newCol] == MapInfo.SPOIL) {
                        newCost += 1; // Empty cell
                    } else if (map[newRow][newCol] == MapInfo.BRICK) {
                        String currentCommand = currNode.commands.toString();
                        if (!currentCommand.isEmpty() && !currentCommand.substring(currentCommand.length() - 1).equals(move)) {
                            newCommands.append(move).append(Dir.REDIRECT);
                        }
                        newCommands.append(Dir.ACTION);
                        newCost += BRICK_POINT; // 1s to destroy + 0.2s to move
                    }

                    // Add move command
                    newCommands.append(move);
                    double heuristic = -1.0 * BOMB_RATIO * calculateBoxesDestroyed(map, new Position(newRow, newCol), power, size);
                    pq.add(new AStarNode(newRow, newCol, newCost, heuristic, currNode, newCommands));
                }
            }
        }

        return optimalNode;
    }

    // BFS
    public Node findSafe(int[][] map, Position curr, Bomb bomb, MapSize size) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            int row = currNode.row;
            int col = currNode.col;

            if (!SkillUtils.isHitBomb(currNode, bomb)) {
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
        return null;
    }

    private long calculateBoxesDestroyed(int[][] map, Position bomb, int power, MapSize size) {
        return CalculateUtils.getDirections()
                .stream()
                .filter(dir -> isBombHitBox(map, bomb, power, dir[0], dir[1], size))
                .count();
    }

    private boolean isBombHitBox(int[][] map, Position bomb, int power, int rowDir, int colDir, MapSize size) {
        List<Integer> invalidObstacles = List.of(MapInfo.WALL, MapInfo.BRICK, MapInfo.PRISON);
        for (int i = 1; i <= power; i++) {
            int newRow = bomb.row + i * rowDir;
            int newCol = bomb.col + i * colDir;
            if (newRow < 0 || newRow >= size.rows || newCol < 0 || newCol >= size.cols) {
                break;
            }
            if (invalidObstacles.stream().anyMatch(invalidObstacle -> map[newRow][newCol] == invalidObstacle)) {
                return false;
            }
            if (map[newRow][newCol] == MapInfo.BOX) {
                return true;
            }
        }
        return false;
    }

    private boolean isValid(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.BOX
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
        return isValid(row, col, map, visited, size)
                && map[row][col] != MapInfo.BRICK;
    }
}
