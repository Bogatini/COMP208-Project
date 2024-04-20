import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menu extends JFrame {

    JFrame gameWin = new JFrame();
    JFrame frame = new JFrame();

    JFrame menuWindow = this;

    public menu() {
        Dimension buttonSize = new Dimension(200, 50);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));

        // Buttons
        JButton mathsgrid = new JButton("Maths Grid");
        panel.add(mathsgrid);
        mathsgrid.setPreferredSize(buttonSize);

        JButton sudoku = new JButton("Sudoku");
        panel.add(sudoku);
        sudoku.setPreferredSize(buttonSize);

        JButton futoshiki = new JButton("Futoshiki");
        panel.add(futoshiki);
        futoshiki.setPreferredSize(buttonSize);

        JButton maze = new JButton("Maze");
        panel.add(maze);
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

        // spaghetti code from fred
        // beacuse two windows are created, two windowListeners must be created, one inside the other
        maze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuWindow.setVisible(false);
                MazeCreator mazeCreator = new MazeCreator(15, 15);
                mazeCreator.setVisible(true); // first step, create the maze

                mazeCreator.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                        if (mazeCreator.getContinueFlag()) {
                            String[][] mazeData = mazeCreator.getMaze();
                            gameWin = new MazeSolver(mazeData); // second step, solve the maze
                                                                // pass maze data to MazeSolver
                            gameWin.addWindowListener(new java.awt.event.WindowAdapter() {
                                @Override
                                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                    menuWindow.setVisible(true);
                                }
                            });

                            openGame(menuWindow, gameWin);
                        } 
                        else {
                            menuWindow.setVisible(true); // if the user didn't finish creating the maze (e.g. closed the window didnt press done), show the menu again
                        }
                    }
                });
            }
        });
        
    // icon images that represent the games
        
        JLabel mathsLogo = new JLabel();
        ImageIcon mathsImage = new ImageIcon("mathsImage.png");
        mathsLogo.setIcon(mathsImage );
        panel.add(mathsLogo);

        JLabel sudokuLogo = new JLabel();
        ImageIcon sudokuImage = new ImageIcon("sudokuImage.png");
        sudokuLogo.setIcon(sudokuImage);
        panel.add(sudokuLogo);

        JLabel futoshikiLogo = new JLabel();
        ImageIcon futoshikiImage = new ImageIcon("futoshikiImage.png");
        futoshikiLogo.setIcon(futoshikiImage);
        panel.add(futoshikiLogo);

        JLabel mazeLogo = new JLabel();
        ImageIcon mazeImage = new ImageIcon("mazeImage.png");
        mazeLogo.setIcon(mazeImage);
        panel.add(mazeLogo);

        panel.setBackground(Color.BLACK);
        add(panel, BorderLayout.CENTER);
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,500);

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
        gameWin.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                currentWindow.setVisible(true); // show the menu when the new window is closed
                currentWindow.setFocusableWindowState(true);
                currentWindow.toFront();
            }
        });
    }

    public static void main(String[] args) {
        menu mainMenu = new menu();
    }
}
