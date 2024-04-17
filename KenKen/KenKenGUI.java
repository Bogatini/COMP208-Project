import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KenKenGUI {
    private static final int GRID_SIZE = 4;
    private int[][] puzzle;
    private char[][] operators;
    private int[][] targets;
    private JTextField[][] inputFields;
    private JPanel[][] cages;

    public KenKenGUI(int[][] puzzle, char[][] operators, int[][] targets) {
        this.puzzle = puzzle;
        this.operators = operators;
        this.targets = targets;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("KenKen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridLayout(GRID_SIZE, GRID_SIZE + 1));

        inputFields = new JTextField[GRID_SIZE][GRID_SIZE];
        cages = new JPanel[GRID_SIZE][GRID_SIZE];

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JPanel panel = new JPanel(new BorderLayout());

                JLabel label = new JLabel();
                if (operators[i][j] != 0) {
                    label.setText("<html><b>" + targets[i][j] + "</b> " + operators[i][j]);
                } else {
                    label.setText(String.valueOf(puzzle[i][j]));
                }
                label.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(label, BorderLayout.NORTH);

                JTextField textField = new JTextField(2);
                inputFields[i][j] = textField;
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(textField, BorderLayout.CENTER);

                cages[i][j] = panel;

                frame.getContentPane().add(panel);
            }
        }

        JButton checkButton = new JButton("Check Solution");
        checkButton.addActionListener(new CheckSolutionListener());
        frame.getContentPane().add(checkButton);

        drawCageBorders();

        frame.pack();
        frame.setVisible(true);
    }

    private void drawCageBorders() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (j < GRID_SIZE - 1 && operators[i][j] != operators[i][j + 1]) {
                    cages[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, Color.BLACK));
                }
                if (i < GRID_SIZE - 1 && operators[i][j] != operators[i + 1][j]) {
                    cages[i][j].setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
                }
            }
        }
    }

    private class CheckSolutionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[][] userSolution = getUserSolution();
            if (checkSolution(userSolution)) {
                JOptionPane.showMessageDialog(null, "Congratulations! You solved the puzzle!");
            } else {
                JOptionPane.showMessageDialog(null, "Oops! That's not the correct solution. Try again!");
            }
        }
    }

    private int[][] getUserSolution() {
        int[][] userSolution = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                String input = inputFields[i][j].getText();
                if (!input.isEmpty()) {
                    userSolution[i][j] = Integer.parseInt(input);
                } else {
                    userSolution[i][j] = 0;
                }
            }
        }
        return userSolution;
    }

    private boolean checkSolution(int[][] userSolution) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (puzzle[i][j] != userSolution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}