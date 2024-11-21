package org.codefest2024.nghenhan.service.caculator;

import org.codefest2024.nghenhan.service.caculator.data.Node;
import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.MapSize;
import org.codefest2024.nghenhan.service.socket.data.Position;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarFinder{
    private int[][] directions = {
            {0, -1, Integer.parseInt(Dir.LEFT)},
            {0, 1, Integer.parseInt(Dir.RIGHT)},
            {-1, 0, Integer.parseInt(Dir.UP)},
            {1, 0, Integer.parseInt(Dir.DOWN)}
    };

    public String find(int[][] map, Position currentPosition, MapSize size) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
        pq.add(new Node(currentPosition.row, currentPosition.col, 0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int row = current.row;
            int col = current.col;

            // If target is found
            if (map[row][col] == 6) {
                return reconstructPath(current);
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                String move = Integer.toString(dir[2]);

                if (isValid(newRow, newCol, map, visited, size)) {
                    double newCost = current.cost;
                    StringBuilder newCommands = new StringBuilder(current.commands);

                    if (map[newRow][newCol] == 0) {
                        newCost += 0.2; // Empty cell
                    } else if (map[newRow][newCol] == 3) {
                        newCommands.append(Dir.ACTION);
                        newCost += 1.2; // 1s to destroy + 0.2s to move
                        map[newRow][newCol] = 0; // Mark as empty after destruction
                    }

                    // Add move command
                    newCommands.append(move);
                    double heuristic = manhattanDistance(newRow, newCol, map, size);
                    pq.add(new Node(newRow, newCol, newCost + heuristic, current, newCommands));
                }
            }
        }
        return "";
    }

    private String reconstructPath(Node node) {
        return node != null ? node.commands.toString() : "";
    }

    private boolean isValid(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0 && row < size.rows && col >= 0 && col < size.cols && !visited[row][col] && map[row][col] != 1 && map[row][col] != 2 && map[row][col] != 5;
    }

    private double manhattanDistance(int row, int col, int[][] map, MapSize size) {
        for (int r = 0; r < size.rows; r++) {
            for (int c = 0; c < size.cols; c++) {
                if (map[r][c] == 6) {
                    return Math.abs(row - r) + Math.abs(col - c);
                }
            }
        }
        return 0;
    }
}
