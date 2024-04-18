import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KillerSudokuGame extends JFrame {
    private static final int GRID_SIZE = 9;
    private static final int CELL_SIZE = 60;
    private static final int SUBGRID_SIZE = 3;
    private static final int CANVAS_WIDTH = GRID_SIZE * CELL_SIZE;
    private static final int CANVAS_HEIGHT = GRID_SIZE * CELL_SIZE;

    private final DottedTextField[][] cells = new DottedTextField[GRID_SIZE][GRID_SIZE];
    private final int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private final Map<List<Point>, Integer> cages = new HashMap<>();

    public KillerSudokuGame() {
        setTitle("Killer Sudoku");
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE + 1, GRID_SIZE + 1));
        initializeCells();
        generateSudoku();
        defineCages();
        displayCageSums();
        applyBorders();
        addCheckButton();
        setVisible(true);
    }

    private void initializeCells() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new DottedTextField();
                cells[row][col].setEditable(true);
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("SansSerif", Font.BOLD, 20));
                // Set the cage information for each cell
                for (List<Point> cage : cages.keySet()) {
                    if (cage.contains(new Point(row, col))) {
                        cells[row][col].setCage(cage);
                        break;
                    }
                }
                add(cells[row][col]);
            }
        }
    }

    private void generateSudoku() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        if (!fillGrid(0, 0)) {
            System.out.println("Failed to solve the Sudoku grid.");
        }

    }

    private boolean fillGrid(int row, int col) {
        if (col == GRID_SIZE) {
            col = 0;
            row++;
        }
        if (row == GRID_SIZE) {
            return true; // Grid filled successfully
        }

        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= GRID_SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        for (int num : numbers) {
            if (isSafe(row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(row, col + 1)) {
                    return true;
                }
                grid[row][col] = 0; // Backtrack
            }
        }
        return false;
    }

    private boolean isSafe(int row, int col, int num) {
        for (int i = 0; i < GRID_SIZE; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return false;
            }
        }
        int startRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int startCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        for (int i = 0; i < SUBGRID_SIZE; i++) {
            for (int j = 0; j < SUBGRID_SIZE; j++) {
                if (grid[startRow + i][startCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void defineCages() {
        List<Point> availablePoints = new ArrayList<>();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                availablePoints.add(new Point(i, j));
            }
        }
        Collections.shuffle(availablePoints);

        int cageId = 1;
        while (!availablePoints.isEmpty()) {
            List<Point> thisCage = new ArrayList<>();
            int sum = 0;
            for (int i = 0; i < 3 && !availablePoints.isEmpty(); i++) {
                Point p = availablePoints.remove(0);
                thisCage.add(p);
                sum += grid[p.x][p.y];
            }
            cages.put(thisCage, sum);
            cageId++;
        }
    }

    private void displayCageSums() {
        for (Map.Entry<List<Point>, Integer> entry : cages.entrySet()) {
            List<Point> cagePoints = entry.getKey();
            int sum = entry.getValue();
            if (!cagePoints.isEmpty()) {
                Point firstCell = cagePoints.get(0);
                cells[firstCell.x][firstCell.y].getSumLabel().setText(String.valueOf(sum));
            }
        }
    }

    private void applyBorders() {
        // Apply custom dashed borders to cage boundaries
        for (List<Point> cageCells : cages.keySet()) {
            int topRow = Integer.MAX_VALUE;
            int bottomRow = Integer.MIN_VALUE;
            int leftCol = Integer.MAX_VALUE;
            int rightCol = Integer.MIN_VALUE;

            // Determine the boundaries of the cage
            for (Point p : cageCells) {
                topRow = Math.min(topRow, p.x);
                bottomRow = Math.max(bottomRow, p.x);
                leftCol = Math.min(leftCol, p.y);
                rightCol = Math.max(rightCol, p.y);
            }

            // Apply border to the surrounding cells of the cage
            for (int row = topRow; row <= bottomRow; row++) {
                for (int col = leftCol; col <= rightCol; col++) {
                    JTextField cell = cells[row][col];
                    if (row == topRow || row == bottomRow || col == leftCol || col == rightCol) {
                        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1)); // Change color for cage borders
                    } else {
                        int top = (row == 0 || row % SUBGRID_SIZE == 0) ? 2 : 1;
                        int left = (col == 0 || col % SUBGRID_SIZE == 0) ? 2 : 1;
                        int bottom = (row == GRID_SIZE - 1 || (row + 1) % SUBGRID_SIZE == 0) ? 2 : 1;
                        int right = (col == GRID_SIZE - 1 || (col + 1) % SUBGRID_SIZE == 0) ? 2 : 1;

                        // Apply thick black lines for 3x3 grid borders
                        if (top == 2 || left == 2 || bottom == 2 || right == 2) {
                            cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                        } else {
                            cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.LIGHT_GRAY));
                        }
                    }
                }
            }
        }
    }

    private void addCheckButton() {
        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkSolution()) {
                    JOptionPane.showMessageDialog(KillerSudokuGame.this, "Congratulations! Your solution is correct.");
                } else {
                    JOptionPane.showMessageDialog(KillerSudokuGame.this,
                            "Sorry, your solution is incorrect. Please try again.");
                }
            }
        });
        add(checkButton);
    }

    private boolean checkSolution() {
        // Check rows and columns
        for (int i = 0; i < GRID_SIZE; i++) {
            Set<Integer> rowSet = new HashSet<>();
            Set<Integer> colSet = new HashSet<>();
            for (int j = 0; j < GRID_SIZE; j++) {
                String rowText = cells[i][j].getText().trim();
                String colText = cells[j][i].getText().trim();

                // Check if cell text is empty
                if (rowText.isEmpty() || colText.isEmpty()) {
                    return false;
                }

                int rowCellValue = Integer.parseInt(rowText);
                int colCellValue = Integer.parseInt(colText);
                if (rowCellValue < 1 || rowCellValue > 9 || colCellValue < 1 || colCellValue > 9 ||
                        rowSet.contains(rowCellValue) || colSet.contains(colCellValue)) {
                    return false;
                }
                rowSet.add(rowCellValue);
                colSet.add(colCellValue);
            }
        }

        // Check 3x3 subgrids
        for (int startRow = 0; startRow < GRID_SIZE; startRow += SUBGRID_SIZE) {
            for (int startCol = 0; startCol < GRID_SIZE; startCol += SUBGRID_SIZE) {
                Set<Integer> subgridSet = new HashSet<>();
                for (int i = startRow; i < startRow + SUBGRID_SIZE; i++) {
                    for (int j = startCol; j < startCol + SUBGRID_SIZE; j++) {
                        String subgridText = cells[i][j].getText().trim();

                        // Check if cell text is empty
                        if (subgridText.isEmpty()) {
                            return false;
                        }

                        int subgridCellValue = Integer.parseInt(subgridText);
                        if (subgridCellValue < 1 || subgridCellValue > 9 || subgridSet.contains(subgridCellValue)) {
                            return false;
                        }
                        subgridSet.add(subgridCellValue);
                    }
                }
            }
        }

        // Check cage sums
        for (Map.Entry<List<Point>, Integer> entry : cages.entrySet()) {
            List<Point> cagePoints = entry.getKey();
            int sum = entry.getValue();
            int cageSum = 0;
            for (Point p : cagePoints) {
                String cageText = cells[p.x][p.y].getText().trim();

                // Check if cell text is empty
                if (cageText.isEmpty()) {
                    return false;
                }

                cageSum += Integer.parseInt(cageText);
            }
            if (cageSum != sum) {
                return false;
            }
        }

        return true;
    }

    private void printSolution() {
        System.out.println("Killer Sudoku Solution:");
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(KillerSudokuGame::new);
    }
}
