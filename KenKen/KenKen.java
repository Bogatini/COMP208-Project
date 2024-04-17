import javax.swing.*;
public class KenKen {
    public static void main(String[] args) {
        KenKenGenerator generator = new KenKenGenerator();
        int[][] puzzle = generator.generatePuzzle();
        char[][] operators = generator.getOperators();
        int[][] targets = generator.getTargets();
        SwingUtilities.invokeLater(() -> {
            KenKenGUI gui = new KenKenGUI(puzzle, operators, targets);
            gui.createAndShowGUI();
        });
    }
}
