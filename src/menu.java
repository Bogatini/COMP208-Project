import javax.swing.*;
import java.awt.*;
import javax.swing.JLabel;
import javax. swing. ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menu extends JFrame {

    JFrame gameWin = new JFrame();
    JFrame frame = new JFrame();

    JFrame menuWindow = this;

    public menu() {
        Dimension buttonSize = new Dimension(200, 50);
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));
        imagePanel.setPreferredSize(new Dimension(1200, 400));

        // Buttons
        JButton mathsgrid = new JButton("Maths Grid");
        buttonPanel.add(mathsgrid);
        mathsgrid.setPreferredSize(buttonSize);

        JButton sudoku = new JButton("Killer Sudoku");
        buttonPanel.add(sudoku);
        sudoku.setPreferredSize(buttonSize);

        JButton futoshiki = new JButton("Futoshiki");
        buttonPanel.add(futoshiki);
        futoshiki.setPreferredSize(buttonSize);

        JButton maze = new JButton("Maze");
        buttonPanel.add(maze);
        maze.setPreferredSize(buttonSize);


        mathsgrid.addActionListener(new ActionListener() {
            @Override
           public void actionPerformed(ActionEvent e) {
                menuWindow.setVisible(false);
                gameWin = new MathsGrid();   // CHANGE THIS LINE *****
               openGame(menuWindow, gameWin);
           }
        });

        sudoku.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               menuWindow.setVisible(false);
              gameWin = new KillerSudoku();   // CHANGE THIS LINE *****
            openGame(menuWindow, gameWin);
            }
        });


        futoshiki.addActionListener(new ActionListener() {
            @Override
           public void actionPerformed(ActionEvent e) {
                menuWindow.setVisible(false);
               gameWin = new Futoshiki();   // CHANGE THIS LINE *****
               openGame(menuWindow, gameWin);
           }
        });
        maze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MazeCreator mazeCreator = new MazeCreator(15, 15);
                mazeCreator.setVisible(true);
                mazeCreator.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        if (mazeCreator.getContinueFlag()) {
                            String[][] mazeData = mazeCreator.getMaze();
                            gameWin = new MazeSolver(mazeData);
                            openGame(menuWindow, gameWin);
                        } 
                        else {
                            menuWindow.setVisible(true);
                        }
                    }
                });
            }
        });


        // icon images that represent the games
        JLabel mathsLogo = new JLabel();
        ImageIcon mathsImage = new ImageIcon("src/mathsImage.png");
        mathsLogo.setIcon(mathsImage );
        imagePanel.add(mathsLogo);

        JLabel sudokuLogo = new JLabel();
        ImageIcon sudokuImage = new ImageIcon("src/sudokuImage.png");
        sudokuLogo.setIcon(sudokuImage);
        imagePanel.add(sudokuLogo);

        JLabel futoshikiLogo = new JLabel();
        ImageIcon futoshikiImage = new ImageIcon("src/futoshikiImage.png");
        futoshikiLogo.setIcon(futoshikiImage);
        imagePanel.add(futoshikiLogo);

        JLabel mazeLogo = new JLabel();
        ImageIcon mazeImage = new ImageIcon("src/mazeImage.png");
        mazeLogo.setIcon(mazeImage);
        imagePanel.add(mazeLogo);


        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(imagePanel, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,800);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * this maybe isnt the best name for this method, what it does is adds a listener that is triggered when the gameWindow is closed that reneables currentWindow
     *
     * @param currentWindow
     * @author Liv Mac with help from Fred M
     */
    private void openGame(JFrame currentWindow, JFrame gameWindow) {
        gameWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                currentWindow.setVisible(true);
                currentWindow.setFocusableWindowState(true);
                currentWindow.toFront();
            }
        });
    }

    public static void main(String[] args) {
        menu mainMenu = new menu();
    }
}
