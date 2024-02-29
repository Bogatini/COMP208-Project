import javax.swing.*;
import java.awt.*;

public class MazeDisplay extends JFrame {

    private String[][] maze;

    public void updateMaze(String[][] maze) {
        this.maze = maze;
        repaint(); // Refresh the display
    }

    public MazeDisplay(String[][] maze) {
        this.maze = maze;
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

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    if (maze[i][j].equals("█")) {
                        g.setColor(Color.BLACK);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    } else if (maze[i][j].equals("▒")) {
                        g.setColor(Color.RED);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }else {
                        g.setColor(Color.WHITE);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(maze[0].length * CELL_SIZE, maze.length * CELL_SIZE);
        }
    }
}
