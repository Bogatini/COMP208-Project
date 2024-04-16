import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MathsGridPuzzle extends JFrame {

    private JPanel puzzlePanel;
    private JTextField[][] equationFields;
    private JLabel[][] equationLabels;

    // 0 for no given number
    // 1 for a given number
    // 2, 3, 4 for +,-,*

    // TODO ADD A TIMER - GIVE HUW THIS INFO ^^^ PLUS TIME
    // must give as a single array with those numbers in the correct place
    // this is to be saved to a csv with the time to be used as training data

    private String[][] equations  = new String[][] {
        {"", "-", "", "+", "", "=", "6"},
        {"-", " ", "+", " ", "-" , " ", " "},
        {"", "+", "", "-", " ", "=", "14"},
        {"+", " ", "+", " ", "-" , " ", " "},
        {"", "-", "", "+", " ", "=", "12"},
        {"=", " ", "=", " ", "=" , " ", " "},
        {"9", " ", "10", " ", "4", " ", " "}
    };

    public MathsGridPuzzle() {
        setTitle("Maths Grid Puzzle");
        setSize(400, 400); // Initial size
        setMinimumSize(new Dimension(400, 400)); // Minimum size to ensure square shape
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        puzzlePanel = new JPanel(new GridLayout(4, 5));

        

        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check everything is correct here
            }
        });
        puzzlePanel.add(new JPanel()); // Empty cell
        puzzlePanel.add(checkButton); // Check button

        setContentPane(puzzlePanel);
        setVisible(true);
    }


    
    
    public static void main(String[] args) {
        MathsGridPuzzle puzzleWindow = new MathsGridPuzzle();
        //SwingUtilities.invokeLater(MathsGridPuzzle::new);

        for
        System.out.println(puzzleWindow.equations);
    }
}
