package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

    public PerimeterGoal(Color c) {
        super(c);
    }

    @Override
    public int score(Block board) {
        Color[][] coloredBoard = board.flatten();
        int edgeScore = 0;

        for (int i = 0; i < coloredBoard.length; i++) {
            for (int j = 0; j < coloredBoard[i].length; j++) {
                if (coloredBoard[i][j].equals(targetGoal) && (i == 0 || i == coloredBoard.length - 1 || j == 0 || j == coloredBoard[i].length - 1)) {
                    edgeScore += (i == 0 || i == coloredBoard.length - 1) && (j == 0 || j == coloredBoard[i].length - 1) ? 2 : 1;
                }
            }
        }
        return edgeScore;
    }

    @Override
    public String description() {
        return "Place the highest number of " + GameColors.colorToString(targetGoal)
                + " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
    }

}
