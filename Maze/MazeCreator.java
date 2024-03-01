import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class MazeCreator extends JFrame {

    private final int rows;
    private final int cols;
    private final JButton[][] gridButtons;
    private boolean continueFlag = false;
    private String[][] MAZE;

    public String[][] getMaze() {
        return MAZE;
    }
    public boolean getContinueFlag() {
        return continueFlag;
    }

    private void printMaze() {
        for (int i = 0; i< MAZE.length; i++) {
            System.out.println(Arrays.toString(MAZE[i]));
        }
    }

    public MazeCreator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        MAZE = new String[rows][cols];
        gridButtons = new JButton[rows][cols];

        setTitle("Maze Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(rows + 1, cols)); // +1 for the print button row - annoyingly this means the print button is a square (cant find a way to make it not stick to the grid payout set in this line)
        initializeGrid();
        addPrintButton();
        setSize(cols * 50, (rows + 1) * 50); // Adjust size based on number of rows and columns
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void initializeGrid() {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                MAZE[y][x] = " ";
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                button.setOpaque(true);
                button.setBorderPainted(false);
                final int finalY = y; // Need to make final to use in button class (this might be really bad)
                final int finalX = x;
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            MAZE[finalY][finalX] = "█";
                            button.setBackground(Color.BLACK);
                        } 
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            MAZE[finalY][finalX] = " ";
                            button.setBackground(Color.WHITE);
                        }
                    }
                });
                gridButtons[y][x] = button;
                add(button);
            }
        }
    }

    private void addPrintButton() {
        JButton printButton = new JButton("Print Maze");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                continueFlag = true;
                dispose(); // close the window
            }
        });
        add(printButton);
    }

    // just for testing purposes, should never be run
    public static void main(String[] args) {
        new MazeCreator(10, 10); // Adjust the dimensions of the maze grid
    }
}
