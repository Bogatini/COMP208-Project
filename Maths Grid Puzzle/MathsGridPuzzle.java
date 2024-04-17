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

    private String[][] filledEquationsGrid;

    private String[][] emptyEquationsGrid = new String[][]{
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
                                           // confusing but its the only way to get what the user has put into the fields easily

    private Timer timer;
    private long startTime;
    private long elapsedTime;

    public MathsGridPuzzle() {
        setTitle("Maths Grid Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(emptyEquationsGrid.length + 1, emptyEquationsGrid[0].length));

        // this is where a new puzzle is generated

        answerGrid = fillGrid(emptyEquationsGrid);

        // used to print out the correct answer
        /* for (String[] row : emptyEquationsGrid) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        } */

        // now all we need to do is take out some of the answers from the solved puzzle
        
        filledEquationsGrid = replaceAnswers(answerGrid);
       
        initializeGrid(filledEquationsGrid);

        addCheckButton();

        // create and start timer
        JLabel timerLabel = new JLabel("");

        startTime = System.currentTimeMillis();
        // create a timer that every time it ticks activates an ActionListener, this can be used to update the timer label in the window
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText(elapsedTime + " seconds");
            }
        });
        timer.start();

        add(timerLabel);

        setSize(emptyEquationsGrid[0].length * CELL_SIZE, emptyEquationsGrid.length * CELL_SIZE);
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
                // get values from text fields - using a listArray is the easiest way of doing this, tried with just an array
                
                int[] answersIntArray = new int[12]; // must be 12 not 9 as some extra squares may be read unintentionally (this does not affect functionality)
                int pointer = 0;

                for (JTextField answer : answerFields){
                    answersIntArray[pointer] = Integer.parseInt(answer.getText());
                    pointer+=1;
                }
                // now we have inputted values in an array                

                // check if equations are correct
                boolean isCorrect = checkEquations(answersIntArray, filledEquationsGrid);

                if (isCorrect) {
                    // end the game and timer
                    timer.stop();
                    JOptionPane.showMessageDialog(MathsGridPuzzle.this, "All equations are correct!\n" + elapsedTime + " seconds taken");    
                } 
                else {
                    JOptionPane.showMessageDialog(MathsGridPuzzle.this, "Some equations are incorrect.");
                }
            }
        });
        add(checkButton);
    }

    private boolean checkEquations(int[] answersIntArray, String[][] filledEquationsGrid) {
        int arrayPointer = 0;

        for (int i = 0; i < (filledEquationsGrid.length-2); i+=2){
            int[] answersSubArray = {answersIntArray[arrayPointer], answersIntArray[arrayPointer+1], answersIntArray[arrayPointer+2]};

            arrayPointer+=3;

            boolean correct = evaluateEquation(answersSubArray, filledEquationsGrid[i]);

            if (!correct) {
                return false; // equation is not correct
            }
        }
        return true; // all equations are correct
    }
    
    private boolean evaluateEquation(int[] equationArray, String[] equation) {
        // take an equation line from the table, find the mathematical operations and the digits and 
        int result = equationArray[0];
        
        String operator1 = equation[1];
        String operator2 = equation[3];
        int answer = Integer.parseInt(equation[6]);

        switch (operator1) {
            case "+":
                result = result + equationArray[1];
                break;
            case "-":
                result = result - equationArray[1];
                break;
            case "*":
                result = result * equationArray[1];
                break;
        }
        switch (operator2) {
            case "+":
                result = result + equationArray[2];
                break;
            case "-":
                result = result - equationArray[2];
                break;
            case "*":
                result = result * equationArray[2];
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
        //https://stackoverflow.com/questions/16112515/how-to-shuffle-an-arraylist
        Collections.shuffle(numbers);

        int baseNumberIndex = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals("?")) {
                    grid[i][j] = Integer.toString(numbers.get(baseNumberIndex++));

                } 
                else if (grid[i][j].equals("_")) {
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
