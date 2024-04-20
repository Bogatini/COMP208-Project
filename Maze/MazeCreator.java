import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MazeCreator extends JFrame {

    private final int rows;
    private final int columns;
    private final JButton[][] gridButtons;
    private final int CELL_SIZE = 50;
    private boolean continueFlag = false;
    private String[][] MAZE;

    /**
     * Constructor that creates the input window for the user
     *
     * @param rows    number of rows in the grid
     * @param columns number of columns in the grid
     */
    public MazeCreator(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        MAZE = new String[rows][columns];
        gridButtons = new JButton[rows][columns];

        setTitle("Maze Creator");
        setLayout(new GridBagLayout()); // Change layout to GridBagLayout
        initializeGrid();
        addPrintButton();
        setSize(columns * CELL_SIZE, (rows + 1) * CELL_SIZE); // Adjust size based on number of rows and columns
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    /**
     * Creates an empty grid of buttons the user can click on to draw a maze Clicking a button changes it appearance
     * and reflects this change in the array representing the maze "W" is a wall represented in black " " is an empty
     * space represented in white Three mouse listener functions are used here to allow the user to click and drag to
     * select buttons
     */
    private void initializeGrid() {
        GridBagConstraints gridSettings = new GridBagConstraints();
        gridSettings.fill = GridBagConstraints.BOTH; // Make buttons stretch in both directions
        gridSettings.weightx = 1.0 / columns; // Evenly distribute horizontal space
        gridSettings.weighty = 1.0 / (rows + 1); // Evenly distribute vertical space including the row for the "Done" button


        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                MAZE[y][x] = " ";
                JButton button = new JButton();
                button.setBackground(Color.WHITE);
                button.setOpaque(true);
                button.setBorderPainted(false);
                final int finalY = y; // need to make final to use in button's mouse listener
                final int finalX = x;
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            MAZE[finalY][finalX] = "W";
                            button.setBackground(Color.BLACK);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            MAZE[finalY][finalX] = " ";
                            button.setBackground(Color.WHITE);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            MAZE[finalY][finalX] = "W";
                            button.setBackground(Color.BLACK);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            MAZE[finalY][finalX] = " ";
                            button.setBackground(Color.WHITE);
                        }
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            MAZE[finalY][finalX] = "W";
                            button.setBackground(Color.BLACK);
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            MAZE[finalY][finalX] = " ";
                            button.setBackground(Color.WHITE);
                        }
                    }
                });
                gridButtons[y][x] = button;
                gridSettings.gridx = x;
                gridSettings.gridy = y;
                add(button, gridSettings);
            }
        }
    }

    /**
     * Adds a button at the bottom of the maze that lets the user finish editing the maze
     */
    private void addPrintButton() {
        GridBagConstraints gridSettings = new GridBagConstraints();
        gridSettings.fill = GridBagConstraints.BOTH; // Make button stretch in both directions
        gridSettings.gridwidth = columns; // Make the button span all columns
        gridSettings.gridx = 0;
        gridSettings.gridy = rows; // Place the button on the row below the grid
        JButton printButton = new JButton("Done");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                continueFlag = true;
                dispose(); // close the window
            }
        });
        add(printButton, gridSettings);
    }

    public String[][] getMaze() {
        return MAZE;
    }

    public boolean getContinueFlag() {
        return continueFlag;
    }

    public void setContinueFlag(boolean inputFlag) {
        this.continueFlag = inputFlag;
    }

    public void printMaze() {
        for (int i = 0; i < MAZE.length; i++) {
            for (int j = 0; j < MAZE[i].length; j++) {
                System.out.print(MAZE[i][j] + ", ");
            }
            System.out.print("\n");
        }
    }
}
