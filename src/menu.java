import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class menu extends JFrame {

    public menu() {
        Dimension buttonSize = new Dimension(200, 50);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,5));

        // Buttons
        JButton sudoko = new JButton("Sudoku");
        panel.add(sudoko);
        sudoko.setPreferredSize(buttonSize);

        JButton futoShiki = new JButton("Futoshiki");
        panel.add(futoShiki);
        futoShiki.setPreferredSize(buttonSize);

        JButton kenken = new JButton("KenKen");
        panel.add(kenken);
        kenken.setPreferredSize(buttonSize);

        JButton mathsgrid = new JButton("Maths Grid");
        panel.add(mathsgrid);
        mathsgrid.setPreferredSize(buttonSize);

        mathsgrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MathsGrid newWin = new MathsGrid();
            }
        });

        this.setFocusableWindowState(false);
        this.toBack();

        JButton maze = new JButton("Maze");
        panel.add(maze);
        maze.setPreferredSize(buttonSize);



        panel.setBackground(Color.BLACK);
        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Menu");


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new menu();
    }
}
