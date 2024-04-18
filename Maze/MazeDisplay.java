import javax.swing.*;
import java.awt.*;

public class MazeDisplay extends JFrame {

    private String[][] MAZE;

    private final int CELL_SIZE = 50;

    /**
     * Constructor that handles creating an output window for the maze
     */
    public MazeDisplay(String[][] maze) {
        this.MAZE = maze;
        setTitle("Maze Display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(maze.length * CELL_SIZE, (maze[0].length+1) * CELL_SIZE);
        setLocationRelativeTo(null);
        add(new MazePanel());
        pack(); // Automatically sizes the frame based on contents
        setVisible(true); // Make the frame visible
    }

    /**
     * updates the maze being displayed inside MazeDisplay's window
     * @param   maze the new maze to be shown 
     */
    public void updateMaze(String[][] newMaze) {
        this.MAZE = newMaze;
        repaint(); // Refresh the display
    }

    /**
     * Constructor for a panel in that will be shown in the window
     * This could be in it's own file, but it is just used to override two methods in JPanel
     */
    class MazePanel extends JPanel {
        /**
         * colours every panel in the maze the correct colour, depending on 
         * what symbol is in the position in the 2D array MAZE
         * @param   g the Graphics object inside the panel that handles how it looks
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < MAZE.length; i++) {
                for (int j = 0; j < MAZE[i].length; j++) {
                    // this uses the old characters left over from when the maze was origionally text based. these can be switched out for any characters BUT MAKE SURE TO UPDATE OTHER FILES ACCORDINGLY
                    if (MAZE[i][j].equals("W")) {
                        g.setColor(Color.BLACK);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE); // place a coloured square at the position of the old buttons. IF BUTTONS CHANGE SIZE, CHANGE CELL_SIZE
                    } 
                    else if (MAZE[i][j].equals("P")) {
                        g.setColor(Color.RED);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                    else {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        /**
         * ensures that when this function is called, the size of each cell is taken into account
         * if the normal function is used the grid is tiny as the minimum size for each panel is used
         * @param   Dimension the height and width of the window 
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(MAZE[0].length * CELL_SIZE, MAZE.length * CELL_SIZE);
        }
    }
}