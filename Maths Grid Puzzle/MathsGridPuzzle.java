import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class MathsGridPuzzle extends JFrame {

    // 0 for no given number
    // 1 for a given number
    // 2, 3, 4 for +,-,*

    // TODO ADD A TIMER - GIVE HUW THIS INFO ^^^ PLUS TIME
    // must give as a single array with those numbers in the correct place
    // this is to be saved to a csv with the time to be used as training data

    private String[][] equations = new String[][]{
            {"?", "-", "?", "+", "?", "=", "6"},
            {"-", "B", "+", "B", "-", "B", "B"},
            {"?", "+", "?", "-", "?", "=", "14"},
            {"+", "B", "+", "B", "-", "B", "B"},
            {"?", "-", "?", "+", "?", "=", "5"},
            {"=", "B", "=", "B", "=", "B", "B"},
            {"9", "B", "10", "B", "4", "B", "B"}
    };

    private String[][] emptyEqautionsGrid = new String[][]{
        {"?", "_", "?", "_", "?", "=", "!"},
        {"_", "B", "_", "B", "_", "B", "B"},
        {"?", "_", "?", "_", "?", "=", "!"},
        {"_", "B", "_", "B", "_", "B", "B"},
        {"?", "_", "?", "_", "?", "=", "!"},
        {"=", "B", "=", "B", "=", "B", "B"},
        {"!", "B", "!", "B", "!", "B", "B"}
    };

    private String[][] answerGrid;

    private final int CELL_SIZE = 50;
    private List<JTextField> answerFields; // a ListArray that can hold the actual interactive labels used to get inputs from user

    public MathsGridPuzzle() {
        setTitle("Maths Grid Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(equations.length + 1, equations[0].length));

        // this is where a new puzzle is generated

        answerGrid = fillGrid(emptyEqautionsGrid);

        for (String[] row : emptyEqautionsGrid) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        // now all we need to do is take out some of the answers from the solved puzzle
        
        equations = replaceAnswers(answerGrid);
       


        initializeGrid(equations);

        addCheckButton();

        setSize(equations[0].length * CELL_SIZE, equations.length * CELL_SIZE);
        setLocationRelativeTo(null);

        pack();
        setVisible(true);
    }

    private void initializeGrid(String[][] inputGrid) {
        answerFields = new ArrayList<>();

        for (String[] line : inputGrid) {
            for (int i = 0; i < line.length; i++){
                String item = line[i];
                // if there is an empty slot, add a box the user can write in
                if (item.equals("?")) {
                    JTextField textField = new JTextField();
                    textField.setHorizontalAlignment(SwingConstants.CENTER);
                    textField.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    add(textField);
                    answerFields.add(textField);

                } 
                // black square
                else if (item.equals("B")) {
                    JPanel blackSquare = new JPanel();
                    blackSquare.setBackground(Color.BLACK);
                    blackSquare.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    blackSquare.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    add(blackSquare);
                } 
                // if there is an answer in position already 
                else if (item.length() == 1 && Character.isDigit(item.charAt(0)) && (i != line.length-1)) { 
                    JTextField textField = new JTextField(item);
                    textField.setEditable(false);
                    textField.setHorizontalAlignment(SwingConstants.CENTER);
                    textField.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    add(textField);
                    answerFields.add(textField); 
                }
                // if there is a symbol, just place it as a non interactive label
                else {
                    JLabel square = new JLabel(item, SwingConstants.CENTER);
                    square.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                    square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    add(square);
                }
            }
        }
    }

    private void addCheckButton() {
        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get values from text fields - using a listarray is the easiest way of doing this, tried with just an array
                // the values are then cast from an listArray into a String[] then into an int[]
                List<String> inputValues = new ArrayList<>();
                for (JTextField answer : answerFields) {
                    inputValues.add(answer.getText());
                }

                String[] answersArray = inputValues.toArray(new String[0]);

                int[] answersIntArray = new int[answersArray.length];
                for (int i = 0; i < answersArray.length; i++) {
                    answersIntArray[i] = Integer.parseInt(answersArray[i]);
                }
                
                // we now have an array of our answers - answersIntArray

                // check if equations are correct
                boolean isCorrect = checkEquationsHorizontal(answersIntArray, equations);

                if (isCorrect) {
                    JOptionPane.showMessageDialog(MathsGridPuzzle.this, "All equations are correct!");
                    // end the game and timer
                } else {
                    JOptionPane.showMessageDialog(MathsGridPuzzle.this, "Some equations are incorrect.");
                }
            }
        });
        add(checkButton);
    }


    private boolean checkEquationsHorizontal(int[] answersIntArray, String[][] equations) {
        int arrayPointer = 0;

        for (int i = 0; i < (equations.length-2); i+=2){
            int[] answersSubArray = {answersIntArray[arrayPointer], answersIntArray[arrayPointer+1], answersIntArray[arrayPointer+2]};

            arrayPointer+=3;

            boolean correct = evaluateEquation(answersSubArray, equations[i]);

            if (!correct) {
                return false; // equation is not correct
            }
        }
        return true; // all equations are correct
    }
    
    private boolean evaluateEquation(int[] answersArray, String[] equation) {
        // have to do this better than har coding it - if you get the lenght of a line, then take two off (= and answer slots) you get an iterable array, step through 2 at a time
        int result = answersArray[0];
        
        String operator1 = equation[1];
        String operator2 = equation[3];
        int answer = Integer.parseInt(equation[6]);


        // step comes in as 0, 2 or 4
        // these correlate to sets of three in answersArray, 0 for the first 3, 2 for the second etc.


        switch (operator1) {
            case "+":
                result = result + answersArray[1];
                break;
            case "-":
                result = result - answersArray[1];
                break;
            case "*":
                result = result * answersArray[1];
                break;
        }
        switch (operator2) {
            case "+":
                result = result + answersArray[2];
                break;
            case "-":
                result = result - answersArray[2];
                break;
            case "*":
                result = result * answersArray[2];
                break;
        }
        
        return (result == answer);
    }

    private static String[][] fillGrid(String[][] grid) {
        Random random = new Random();

        String[] operations = {"+", "-", "*"};

        // shuffle numbers 1 to 9
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        int baseNumberIndex = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals("?")) {
                    grid[i][j] = Integer.toString(numbers.get(baseNumberIndex++));
                } else if (grid[i][j].equals("_")) {
                    grid[i][j] = operations[random.nextInt(operations.length)];
                }
            }
        }

        // calculate and fill answers
        for (int i = 0; i < grid.length; i++) {
            if (grid[i][grid[i].length - 2].equals("=")) {
                int answer = calculateRow(grid, i);
                grid[i][grid[i].length - 1] = Integer.toString(answer);
            }
            if (grid[grid.length - 2][i].equals("=")) {
                int answer = calculateColumn(grid, i);
                grid[grid.length - 1][i] = Integer.toString(answer);
            }
        }
        return grid;
    }

    private static int calculateRow(String[][] grid, int row) {
        int result = Integer.parseInt(grid[row][0]);
        for (int i = 2; i < grid[row].length - 1; i += 2) {
            int operand = Integer.parseInt(grid[row][i]);
            String operation = grid[row][i - 1];
            switch (operation) {
                case "+":
                    result += operand;
                    break;
                case "-":
                    result -= operand;
                    break;
                case "*":
                    result *= operand;
                    break;
                case "/":
                    result /= operand;
                    break;
            }
        }
        return result;
    }

    private static int calculateColumn(String[][] grid, int column) {
        int result = Integer.parseInt(grid[0][column]);
        for (int i = 2; i < grid.length - 1; i += 2) {
            int operand = Integer.parseInt(grid[i][column]);
            String operation = grid[i - 1][column];
            switch (operation) {
                case "+":
                    result += operand;
                    break;
                case "-":
                    result -= operand;
                    break;
                case "*":
                    result *= operand;
                    break;
                case "/":
                    result /= operand;
                    break;
            }
        }
        return result;
    }
    // private void replaceNumbersWithQuestionMarks(String[][] grid, double replaceChance) {
    private String[][] replaceAnswers(String[][] grid) {
        Random random = new Random();

        for (int i = 0; i < grid.length-2; i+=2) {
            for (int j = 0; j < grid[i].length-2; j+=2) {
                if (random.nextDouble() > 0.25) { // CHANGE NUMBER HERE TO REMOVE FEWER NUMBERS
                    grid[i][j] = "?";
                }
            }
        }
        return grid;
    }
    

    

    public static void main(String[] args) {
        MathsGridPuzzle puzzleWindow = new MathsGridPuzzle();
    }
}
