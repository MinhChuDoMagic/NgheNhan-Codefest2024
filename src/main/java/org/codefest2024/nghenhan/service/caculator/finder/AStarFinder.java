package org.codefest2024.nghenhan.service.caculator.finder;

import org.codefest2024.nghenhan.service.caculator.data.AStarNode;
import org.codefest2024.nghenhan.service.socket.data.Dir;
import org.codefest2024.nghenhan.service.socket.data.MapInfo;
import org.codefest2024.nghenhan.service.socket.data.MapSize;
import org.codefest2024.nghenhan.service.socket.data.Position;
import org.codefest2024.nghenhan.utils.CalculateUtils;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarFinder{
    private final int[][] directions = {
            {0, -1, Integer.parseInt(Dir.LEFT)},
            {0, 1, Integer.parseInt(Dir.RIGHT)},
            {-1, 0, Integer.parseInt(Dir.UP)},
            {1, 0, Integer.parseInt(Dir.DOWN)}
    };

    public String find(int[][] map, Position curr, Position des, MapSize size) {
        PriorityQueue<AStarNode> pq = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));
        pq.add(new AStarNode(curr.row, curr.col, 0,0, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];

        while (!pq.isEmpty()) {
            AStarNode currNode = pq.poll();
            int row = currNode.row;
            int col = currNode.col;

            // If target is found
            if (des.row == row && des.col == col) {
                return reconstructPath(currNode);
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
                        newCommands.append(Dir.ACTION);
                        newCost += 6; // 1s to destroy + 0.2s to move
                    }

                    // Add move command
                    newCommands.append(move);
                    double heuristic = CalculateUtils.manhattanDistance(new Position(newRow, newCol), des);
                    pq.add(new AStarNode(newRow, newCol, newCost, heuristic, currNode, newCommands));
                }
            }
        }
        return Dir.INVALID;
    }

    private String reconstructPath(AStarNode node) {
        return node != null ? node.commands.toString() : Dir.INVALID;
    }

    private boolean isValid(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.BOX
                && map[row][col] != MapInfo.PRISON;
    }
}
