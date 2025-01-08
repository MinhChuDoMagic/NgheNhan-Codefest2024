package org.codefest2024.nghenhan.service.finder;

import org.codefest2024.nghenhan.service.finder.data.AStarNode;
import org.codefest2024.nghenhan.service.finder.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;
import org.codefest2024.nghenhan.utils.CalculateUtils;
import org.codefest2024.nghenhan.utils.SkillUtils;

import java.util.*;

public class KeepDistanceFinder {
    private static KeepDistanceFinder instance;

    private final double SPOIL_POINT = 5.01;
    private final double BRICK_POINT = 8.09;
    private final int ENEMY_RATIO = 1;

    private KeepDistanceFinder() {
    }

    public static KeepDistanceFinder getInstance() {
        if (instance == null) {
            instance = new KeepDistanceFinder();
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
                            newCommands.append(move).append(Dir.REDIRECT);
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

    public AStarNode findBrick(int[][] map, Position curr, Position enemy, MapSize size) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        pq.add(new AStarNode(curr.row, curr.col, 0, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (map[row][col] == MapInfo.BRICK) {
                return currNode;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String move = Integer.toString(dir[2]);

                if (isValidBrickPath(newRow, newCol, map, visited, size)) {
                    double newCost = currNode.g;
                    StringBuilder newCommands = new StringBuilder(currNode.commands);

                    if (map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] == MapInfo.DESTROYED) {
                        newCost += 1; // Empty cell
                    } else if (map[newRow][newCol] == MapInfo.BRICK) {
                        String currentCommand = currNode.commands.toString();
                        if (!currentCommand.isEmpty() && !currentCommand.substring(currentCommand.length() - 1).equals(move)) {
                            newCommands.append(move).append(Dir.REDIRECT);
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

    public AStarNode findWithoutBrick(int[][] map, Position curr, Position enemy, int targetValue, MapSize size) {
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

                if (isValidBoxPathWithoutBrick(newRow, newCol, map, visited, size)) {
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

    public AStarNode keepDistance(int[][] map, Position curr, Position enemy, MapSize size, int distance) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        pq.add(new AStarNode(curr.row, curr.col, 0, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (CalculateUtils.manhattanDistance(currNode, enemy) >= distance) {
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
                    double heuristic = 16 - 1.0 * ENEMY_RATIO * CalculateUtils.manhattanDistance(new Position(newRow, newCol), enemy);
                    pq.add(new AStarNode(newRow, newCol, newCost, heuristic, currNode, newCommands));
                }
            }
        }

        return new AStarNode(curr.row, curr.col, 0, 0, null, null);
    }

    public AStarNode keepDistanceWithoutBrick(int[][] map, Position curr, Position enemy, MapSize size) {
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

                if (isRunPathWithoutBrick(newRow, newCol, map, visited, size)) {
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

    public Node findSafe(int[][] map, Position curr, Position enemy, MapSize size, List<Bomb> bombs, List<Hammer> hammers, List<Wind> winds) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections(curr, enemy);

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();
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

                if (isPath(newRow, newCol, map, visited, size)) {
                    StringBuilder newCommands = new StringBuilder(currNode.commands);
                    newCommands.append(dir[2]);

                    queue.add(new Node(newRow, newCol, currNode, newCommands));
                }
            }
        }

        return new Node(curr.row, curr.col, null, null);
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

    private boolean isValidBrickPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
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

    private boolean isValidBoxPathWithoutBrick(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return isValidBoxPath(row, col, map, visited, size)
                && map[row][col] != MapInfo.BRICK;
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

    private boolean isRunPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
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

    private boolean isRunPathWithoutBrick(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return isRunPath(row, col, map, visited, size)
                && map[row][col] != MapInfo.BRICK;
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
