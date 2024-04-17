import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class KenKenGenerator {
    private int[][] puzzle;
    private char[][] operators;
    private int[][] targets;
    private int gridSize;

    public KenKenGenerator() {
        gridSize = 4;
        puzzle = new int[gridSize][gridSize];
        operators = new char[gridSize][gridSize];
        targets = new int[gridSize][gridSize];
    }

    public int[][] generatePuzzle() {
        initializePuzzle();

        Random random = new Random();

        generateCages();

        for (int i = 1; i <= gridSize; i++) {
            char operator = getRandomOperator(random);
            int target = random.nextInt(10) + 1;
            assignOperatorAndTarget(i, operator, target);
        }

        return puzzle;
    }

    private void initializePuzzle() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                puzzle[i][j] = 0;
            }
        }
    }

    private void generateCages() {
        ArrayList<Integer> cells = new ArrayList<>();
        for (int i = 0; i < gridSize * gridSize; i++) {
            cells.add(i);
        }
        Collections.shuffle(cells);

        int currentCell = 1;
        for (int size = 1; size <= 3; size++) {
            for (int cell : cells) {
                int row = cell / gridSize;
                int col = cell % gridSize;
                if (puzzle[row][col] == 0 && canPlaceCage(row, col, size)) {
                    createCage(row, col, size, currentCell);
                    currentCell++;
                }
            }
        }
    }

    private boolean canPlaceCage(int row, int col, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (row + i >= gridSize || col + j >= gridSize || puzzle[row + i][col + j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void createCage(int row, int col, int size, int value) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                puzzle[row + i][col + j] = value;
            }
        }
    }

    private char getRandomOperator(Random random) {
        char[] possibleOperators = {'+', '-', '*', '/'};
        return possibleOperators[random.nextInt(4)];
    }

    private void assignOperatorAndTarget(int value, char operator, int target) {
        boolean assigned = false;
        Random random = new Random();
        while (!assigned) {
            int row = random.nextInt(gridSize);
            int col = random.nextInt(gridSize);
            if (puzzle[row][col] == value && operators[row][col] == 0) { 
                targets[row][col] = target;
                assigned = true;
            }
        }
    }

    public char[][] getOperators() {
        return operators;
    }

    public int[][] getTargets() {
        return targets;
    }
}