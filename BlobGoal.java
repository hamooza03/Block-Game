package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

    public BlobGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] coloredBoard = board.flatten();
        boolean[][] visited = new boolean[coloredBoard.length][coloredBoard[0].length];
        int maxBlobSize = 0;

        for (int i = 0; i < coloredBoard.length; i++) {
            for (int j = 0; j < coloredBoard[i].length; j++) {
                int blobSize = undiscoveredBlobSize(i, j, coloredBoard, visited);
                maxBlobSize = Math.max(maxBlobSize, blobSize);
            }
        }
        return maxBlobSize;
    }

    @Override
    public String description() {
        return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
                + " blocks, anywhere within the block";
    }


    public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
        if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[0].length || visited[i][j] || !unitCells[i][j].equals(targetGoal)) {
            return 0;
        }

        visited[i][j] = true;
        int size = 1;

        size += undiscoveredBlobSize(i - 1, j, unitCells, visited);
        size += undiscoveredBlobSize(i + 1, j, unitCells, visited);
        size += undiscoveredBlobSize(i, j - 1, unitCells, visited);
        size += undiscoveredBlobSize(i, j + 1, unitCells, visited);

        return size;

    }

}
