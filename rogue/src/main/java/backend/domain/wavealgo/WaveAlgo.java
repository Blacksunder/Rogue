package backend.domain.wavealgo;

import backend.domain.map.Constants;
import backend.domain.map.FieldTypes;
import backend.domain.util.WaveAlgorithmCell;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

public class WaveAlgo {
    private final WaveAlgorithmCell[][] searchField;
    private final boolean moveDiagonally = true;
    private final int stepSize = 1;
    private final int[][] field;
    private final int startX;
    private final int startY;

    public WaveAlgo(FieldTypes[][] field, int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        searchField = new WaveAlgorithmCell[Constants.HEIGHT][Constants.WIDTH];
        this.field = new int[Constants.HEIGHT][Constants.WIDTH];
        for (int i = 0; i < Constants.HEIGHT; i++) {
            for (int j = 0; j < Constants.WIDTH; j++) {
                try {
                    if (Constants.isMovable(field[i][j])) {
                        searchField[i][j] = WaveAlgorithmCell.MOVABLE;
                    } else if (field[i][j] == FieldTypes.PLAYER) {
                        searchField[i][j] = WaveAlgorithmCell.FINISH;
                    } else if (i == startY && j == startX) {
                        searchField[i][j] = WaveAlgorithmCell.START;
                    } else {
                        searchField[i][j] = WaveAlgorithmCell.OBSTACLE;
                    }
                    this.field[i][j] = -1;
                } catch (IndexOutOfBoundsException ignored) {
                    throw new IndexOutOfBoundsException("Invalid field");
                }
            }
        }
        this.field[startY][startX] = 0;
    }

    private Stack<int[]> path(int current, int finish_y, int finish_x) {
        Stack<int[]> stack = new Stack<>();
        while (current != 0) {
            boolean changed = false;
            for (int i = -stepSize; i < stepSize + 1 && !changed; i++) {
                for (int j = -stepSize; j < stepSize + 1 && !changed; j++) {
                    try {
                        if (field[finish_y + i][finish_x + j] == current - 1) {
                            current--;
                            changed = true;
                            finish_x += j;
                            finish_y += i;
                            int[] tmp = new int[]{finish_y, finish_x};
                            stack.push(tmp);
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }
            if (!changed) break;
        }
        return stack;
    }

    public Stack<int[]> findPath() throws IllegalAccessError {
        ArrayList<int[]> peaks = new ArrayList<>();
        peaks.add(new int[]{startY, startX});
        int current = 0;
        boolean finish_reached = false;
        int finish_x = -1;
        int finish_y = -1;
        while (!peaks.isEmpty() && !finish_reached) {
            ArrayList<int[]> tmp = new ArrayList<>();
            for (int[] peak : peaks) {
                for (int i = -stepSize; i < stepSize + 1; i++) {
                    for (int j = -stepSize; j < stepSize + 1; j++) {
                        try {
                            if (searchField[peak[0] + i][peak[1] + j] == WaveAlgorithmCell.FINISH) {
                                finish_reached = true;
                                finish_y = peak[0] + i;
                                finish_x = peak[1] + j;
                            }
                            if (moveDiagonally && (Math.abs(i) != Math.abs(j) || i == 0)) {
                                continue;
                            }
                            if (searchField[peak[0] + i][peak[1] + j] == WaveAlgorithmCell.MOVABLE
                                    && i % stepSize == 0
                                    && field[peak[0] + i][peak[1] + j] == -1) {
                                field[peak[0] + i][peak[1] + j] = current + 1;
                                tmp.add(new int[]{peak[0] + i, peak[1] + j});
                            }
                        } catch (IndexOutOfBoundsException ignored) {
                        }
                    }
                }
            }
            peaks = tmp;
            current++;
        }
        if (!finish_reached) {
            throw new IllegalAccessError();
        }
        return path(current, finish_y, finish_x);
    }
}
