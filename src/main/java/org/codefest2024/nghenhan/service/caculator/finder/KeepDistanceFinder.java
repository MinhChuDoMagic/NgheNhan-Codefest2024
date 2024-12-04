package org.codefest2024.nghenhan.service.caculator.finder;

import org.codefest2024.nghenhan.service.caculator.data.DijkstraNode;
import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.MapSize;
import org.codefest2024.nghenhan.service.socket.data.Position;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class KeepDistanceFinder {
    private static KeepDistanceFinder instance;

    private KeepDistanceFinder() {
    }

    public static KeepDistanceFinder getInstance() {
        if (instance == null) {
            instance = new KeepDistanceFinder();
        }
        return instance;
    }

    //dijkstra
    public String find(int[][] map, Position player, Position enemy, MapSize size) {
        PriorityQueue<DijkstraNode> pq = new PriorityQueue<>(Comparator.comparing(node -> node.g));
        pq.add(new DijkstraNode(player.row, player.col, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];
        List<int[]> directions = CalculateUtils.getDirections();

        while (!pq.isEmpty()) {
            DijkstraNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (!CalculateUtils.enemyNearby(currNode, enemy)) {
                return currNode.reconstructPath();
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

                    if (map[newRow][newCol] == MapInfo.BLANK || map[newRow][newCol] ==  MapInfo.DESTROYED) {
                        newCost += 1; // Empty cell
                    } else if (map[newRow][newCol] == MapInfo.BRICK) {
                        String currentCommand = currNode.commands.toString();
                        if (!currentCommand.isEmpty() && !currentCommand.substring(currentCommand.length() - 1).equals(move)) {
                            newCommands.append(move);
                        }
                        newCommands.append(Dir.ACTION);
                        newCost += 6; // 1s to destroy + 0.2s to move
                    }

                    // Add move command
                    newCommands.append(move);
                    pq.add(new DijkstraNode(newRow, newCol, newCost, currNode, newCommands));
                }
            }
        }
        return Dir.INVALID;
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
}
