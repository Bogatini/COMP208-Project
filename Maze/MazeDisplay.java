import javax.swing.*;
import java.awt.*;

public class MazeDisplay extends JFrame {

    private String[][] MAZE;

    public void updateMaze(String[][] maze) {
        this.MAZE = maze;
        repaint(); // Refresh the display
    }

    public MazeDisplay(String[][] maze) {
        this.MAZE = maze;
        setTitle("Maze Display");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(maze.length * 50, (maze[0].length+1) * 50);
        setLocationRelativeTo(null);
        add(new MazePanel());
        pack(); // Automatically sizes the frame based on contents
        setVisible(true); // Make the frame visible
    }

    class MazePanel extends JPanel {
        private static final int CELL_SIZE = 50;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int i = 0; i < MAZE.length; i++) {
                for (int j = 0; j < MAZE[i].length; j++) {
                    // this uses the old characters left over from when the maze was origionally text based. these can be switched out for any characters BUT MAKE SURE TO UPDATE OTHER FILES ACCORDINGLY
                    if (MAZE[i][j].equals("█")) {
                        g.setColor(Color.BLACK);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE); // place a coloured square at the position of the old buttons. IF BUTTONS CHANGE SIZE, CHANGE CELL_SIZE
                    } 
                    else if (MAZE[i][j].equals("▒")) {
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

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(MAZE[0].length * CELL_SIZE, MAZE.length * CELL_SIZE);
        }
    }
}
