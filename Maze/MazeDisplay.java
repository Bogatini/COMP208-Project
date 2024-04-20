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
        setSize(750,800);
        setLocationRelativeTo(null);
        add(new MazePanel());
        setVisible(true);
        
    }

    /**
     * updates the maze being displayed inside MazeDisplay's window
     * @param   maze the new maze to be shown 
     */
    public void updateMaze(String[][] newMaze) {
        this.MAZE = newMaze;
        repaint(); // refresh the window
    }

    /**
     * Constructor for the contents of the window
     * Is a single panel that can be coloured in. where a wall is in the map, a black rectange is drawn
     */
    class MazePanel extends JPanel {

        public MazePanel(){
            this.setSize(MAZE[0].length * CELL_SIZE, MAZE.length * CELL_SIZE);
        }

        /**
         * colours every panel in the maze, the colour is dependant on 
         * what symbol is in the panel's position in the 2D array MAZE
         * 
         * This function is called internally in many JPanel methods
         * The important one for us is repaint() 
         * 
         * @param   g the Graphics object inside the panel (the small squares)
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < MAZE.length; i++) {
                for (int j = 0; j < MAZE[i].length; j++) {
                    // this uses the old characters left over from when the maze was origionally text based. these can be switched out for any characters BUT MAKE SURE TO UPDATE OTHER FILES ACCORDINGLY
                    if (MAZE[i][j].equals("W")) {
                        g.setColor(Color.BLACK);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE); // place a coloured square at the position of the old buttons
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
    }
}