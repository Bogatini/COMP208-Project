import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menu extends JFrame {

    JFrame gameWin;

    JFrame win = this;

    public menu() {
        Dimension buttonSize = new Dimension(200, 50);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));

        // Buttons
        JButton mathsgrid = new JButton("Maths Grid");
        panel.add(mathsgrid);
        mathsgrid.setPreferredSize(buttonSize);

        mathsgrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                win.setVisible(false);
                gameWin = new MathsGrid();   // CHANGE THIS LINE *****
                openGame(win, gameWin);
            }
        });

        // you need to add all the buttons for the other games
        //change the button name and the object that is made in them


        panel.setBackground(Color.BLACK);
        add(panel, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
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
