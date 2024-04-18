import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KillerSudokuGame extends JFrame {
    private static final int GRID_SIZE = 9;
    private static final int CELL_SIZE = 80;
    private static final int SUBGRID_SIZE = 3;
    private static final int CANVAS_WIDTH = GRID_SIZE * CELL_SIZE;
    private static final int CANVAS_HEIGHT = GRID_SIZE * CELL_SIZE;
    private Timer timer;
    private JLabel timerLabel;

    private final DottedTextField[][] cells = new DottedTextField[GRID_SIZE][GRID_SIZE];
    private final int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private final Map<List<Point>, Integer> cages = new HashMap<>();

    public KillerSudokuGame() {
        setTitle("Killer Sudoku");
        setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridLayout(GRID_SIZE + 1, GRID_SIZE + 1));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        generateSudoku();
        defineCages();
        initializeCells(centerPanel);
        Map<Integer, Color> cageColors = new HashMap<>();
        List<Color> usedColors = new ArrayList<>(); // Create an empty list to keep track of used colors
        for (Map.Entry<List<Point>, Integer> entry : cages.entrySet()) {
            List<Point> cage = entry.getKey(); // Generate unique ID for each cage
            List<Point> neighbors = getNeighbors(cage);
            Color cageColor = getRandomColor(usedColors, neighbors); // Get a random color for each cage
            cageColors.put(System.identityHashCode(cage), cageColor);
            usedColors.add(cageColor); // Add the used color to the list
        }
        for (Map.Entry<List<Point>, Integer> entry : cages.entrySet()) {
            List<Point> cage = entry.getKey();
            int cageId = System.identityHashCode(cage); // Get the cage ID
            Color cageColor = cageColors.get(cageId); // Retrieve color for this cage ID
            for (Point p : cage) {
                int row = p.x;
                int col = p.y;
                cells[row][col].setCage(cage);
                cells[row][col].setBackground(cageColor);
            }
        }
        displayCageSums();
        applyBorders();
        add(centerPanel, BorderLayout.CENTER);
        addCheckButton(bottomPanel);
        add(bottomPanel, BorderLayout.SOUTH);
        addTimerLabel(bottomPanel);
        startTimer();
        setLocationRelativeTo(null);
        setVisible(true);

    }

    private Color getRandomColor(List<Color> usedColors, List<Point> neighbors) {
        Color[] pastelColors = {
                new Color(255, 255, 153), // Pastel yellow
                new Color(173, 216, 230), // Pastel blue
                new Color(152, 251, 152), // Pastel green
                new Color(255, 182, 193), // Pastel pink
                new Color(221, 160, 221), // Pastel purple
                new Color(255, 204, 153) // Pastel orange
        };
        Random random = new Random();

        // Shuffle the colors to randomize selection
        List<Color> shuffledColors = Arrays.asList(pastelColors);
        Collections.shuffle(shuffledColors);

        // Iterate over the shuffled colors to find a suitable color
        for (Color color : shuffledColors) {
            boolean colorUsed = false;

            // Check if the color is already used by neighboring cells or cages
            if (usedColors.contains(color) || hasSameColorNeighbor(neighbors, color)) {
                colorUsed = true;
            }

            // Return the color if it's not used by any neighboring cells
            if (!colorUsed) {
                return color;
            }
        }

        // If no suitable color is found, choose a random color
        return pastelColors[random.nextInt(pastelColors.length)];
    }

    private List<Point> getNeighbors(List<Point> cage) {
        Set<Point> neighbors = new HashSet<>();
        for (Point p : cage) {
            int x = p.x;
            int y = p.y;
            if (x > 0) {
                neighbors.add(new Point(x - 1, y)); // Left
            }
            if (x < GRID_SIZE - 1) {
                neighbors.add(new Point(x + 1, y)); // Right
            }
            if (y > 0) {
                neighbors.add(new Point(x, y - 1)); // Up
            }
            if (y < GRID_SIZE - 1) {
                neighbors.add(new Point(x, y + 1)); // Down
            }
        }
        return new ArrayList<>(neighbors);
    }

    private boolean hasSameColorNeighbor(List<Point> neighbors, Color color) {
        for (Point neighbor : neighbors) {
            // Check if the neighboring cell has the same color
            int row = neighbor.x;
            int col = neighbor.y;
            if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                Color neighborColor = cells[row][col].getBackground();
                if (neighborColor.equals(color)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initializeCells(JPanel centerPanel) {
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
                        if (cage.get(0).equals(new Point(row, col))) {
                            // If this cell is the top left cell of the cage, add the sumLabel
                            cells[row][col].setLayout(new BorderLayout());
                            cells[row][col].add(cells[row][col].getSumLabel(), BorderLayout.NORTH);
                        }
                        break;
                    }
                }
                centerPanel.add(cells[row][col]);
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
            // Randomly determine the number of cells in this cage (2 or 3)
            int cellsInCage = Math.random() < 0.5 ? 2 : 3;

            // Select a random starting point for the cage
            Point startPoint = availablePoints.remove(0);
            thisCage.add(startPoint);
            sum += grid[startPoint.x][startPoint.y];

            // Add adjacent cells to the cage until the desired number of cells is reached
            while (thisCage.size() < cellsInCage) {
                Point lastPoint = thisCage.get(thisCage.size() - 1);
                List<Point> adjacentPoints = getAdjacentPoints(lastPoint);
                Point nextPoint = null;
                for (Point p : adjacentPoints) {
                    if (availablePoints.contains(p)) {
                        nextPoint = p;
                        break;
                    }
                }
                if (nextPoint != null) {
                    availablePoints.remove(nextPoint);
                    thisCage.add(nextPoint);
                    sum += grid[nextPoint.x][nextPoint.y];
                } else {
                    break; // No more adjacent available points
                }
            }

            // Check if the cage has more than one cell
            if (thisCage.size() > 1) {
                cages.put(thisCage, sum);
            }

            cageId++;
        }
    }

    private List<Point> getAdjacentPoints(Point point) {
        int x = point.x;
        int y = point.y;
        List<Point> adjacentPoints = new ArrayList<>();
        if (x > 0) {
            adjacentPoints.add(new Point(x - 1, y)); // Left
        }
        if (x < GRID_SIZE - 1) {
            adjacentPoints.add(new Point(x + 1, y)); // Right
        }
        if (y > 0) {
            adjacentPoints.add(new Point(x, y - 1)); // Up
        }
        if (y < GRID_SIZE - 1) {
            adjacentPoints.add(new Point(x, y + 1)); // Down
        }
        return adjacentPoints;
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
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = cells[row][col];
                int top = (row % SUBGRID_SIZE == 0) ? 2 : 1;
                int left = (col % SUBGRID_SIZE == 0) ? 2 : 1;
                int bottom = ((row + 1) % SUBGRID_SIZE == 0 || row == GRID_SIZE - 1) ? 2 : 1;
                int right = ((col + 1) % SUBGRID_SIZE == 0 || col == GRID_SIZE - 1) ? 2 : 1;

                // Apply border lines
                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
            }
        }
    }

    private void addCheckButton(JPanel bottomPanel) {
        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkSolution()) {
                    timer.stop();
                    JOptionPane.showMessageDialog(KillerSudokuGame.this, "Congratulations! Your solution is correct.");
                } else {
                    JOptionPane.showMessageDialog(KillerSudokuGame.this,
                            "Sorry, your solution is incorrect. Please try again.");
                }
            }
        });
        bottomPanel.add(checkButton);
    }

    private void addTimerLabel(JPanel bottomPanel) {
        timerLabel = new JLabel("Time: 00:00");
        bottomPanel.add(timerLabel);
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            int seconds = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                int mins = seconds / 60;
                int secs = seconds % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", mins, secs));
            }
        });
        timer.start();
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

    // private void printSolution() {
    // System.out.println("Killer Sudoku Solution:");
    // for (int i = 0; i < GRID_SIZE; i++) {
    // for (int j = 0; j < GRID_SIZE; j++) {
    // System.out.print(grid[i][j] + " ");
    // }
    // System.out.println();
    // }
    // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KillerSudokuGame::new);
        // KillerSudokuGame game = new KillerSudokuGame();
        // SwingUtilities.invokeLater(game::printSolution);
    }
}
