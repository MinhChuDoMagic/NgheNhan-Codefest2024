package org.codefest2024.nghenhan.service.caculator.finder;

import org.codefest2024.nghenhan.service.caculator.data.Node;
import org.codefest2024.nghenhan.service.socket.data.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFSFinder {
    private final int[][] directions = {
            {0, -1, Integer.parseInt(Dir.LEFT)},
            {0, 1, Integer.parseInt(Dir.RIGHT)},
            {-1, 0, Integer.parseInt(Dir.UP)},
            {1, 0, Integer.parseInt(Dir.DOWN)}
    };
    private static BFSFinder instance;

    private BFSFinder(){}

    public static BFSFinder getInstance(){
        if (instance == null) {
            instance = new BFSFinder();
        }
        return instance;
    }

    public Node findBombPlace(int[][] map, Position curr, int targetValue, MapSize size) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            int row = currNode.row;
            int col = currNode.col;

            if (map[row][col] == targetValue) {
                return currNode.parent;
            }

            if (visited[row][col]) continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isValid(newRow, newCol, map, visited, size)) {
                    StringBuilder newCommands = new StringBuilder(currNode.commands);
                    newCommands.append(dir[2]);
                    queue.add(new Node(newRow, newCol, currNode, newCommands));
                }
            }
        }
        return new Node(curr.row, curr.col, null, null);
    }

    public Node findSafe(int[][] map, Position curr, List<Bomb> bomb, MapSize size) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(curr.row, curr.col, null, null));
        boolean[][] visited = new boolean[size.rows][size.cols];

        while (!queue.isEmpty()) {
            Node currNode = queue.poll();

            int row = currNode.row;
            int col = currNode.col;

            if (isSafe(currNode, bomb)) {
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

    private boolean isValid(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return row >= 0
                && row < size.rows
                && col >= 0 && col < size.cols
                && !visited[row][col]
                && map[row][col] != MapInfo.WALL
                && map[row][col] != MapInfo.BRICK
                && map[row][col] != MapInfo.PRISON;
    }

    private boolean isPath(int row, int col, int[][] map, boolean[][] visited, MapSize size) {
        return isValid(row, col, map, visited, size)
                && map[row][col] != MapInfo.BOX;
    }

    private boolean isSafe(Position curr, List<Bomb> bombs) {
        return bombs.stream().noneMatch(bomb ->
                (curr.row == bomb.row && Math.abs(curr.col - bomb.col) <= bomb.power)
                        || (curr.col == bomb.col && Math.abs(curr.row - bomb.row) <= bomb.power)
        );
    }

}
