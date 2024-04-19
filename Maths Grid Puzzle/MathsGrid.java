/**
 * Class representing a mathematical grid puzzle the puzzle is stored as a 2d array. There is a 
 * grid template that is filled and then displayed to the user. The user can then fill in the 
 * gaps inside the grid and check their answers. An arrayList is used to keep track of these 
 * answers. The users time is tracked once they start the puzzle. Once the game is complete, 
 * the starting state of the grid and the time taken to finish the grid are used as training
 * data in the nueral network.  
 * @author Fred Mortimer 201639313
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class MathsGrid extends JFrame {

    // Neural Network setup
    Network neuralNetworkInterface;

    /* 
    an empty template for a puzzle grid. ? - POSSIBLE user input (may be filled in as a hint)
                                         _ - mathematical operation (+,-,*)
                                         ! - answer slot
                                         B - black square
                                         = - equals square 
    */
    private static final String[][] emptyEquationsGrid = new String[][]{
        {"?", "_", "?", "_", "?", "=", "!"},
        {"_", "B", "_", "B", "_", "B", "B"},
        {"?", "_", "?", "_", "?", "=", "!"},
        {"_", "B", "_", "B", "_", "B", "B"},
        {"?", "_", "?", "_", "?", "=", "!"},
        {"=", "B", "=", "B", "=", "B", "B"},
        {"!", "B", "!", "B", "!", "B", "B"}
    };

    // holds the fully completed grid
    private String[][] answerGrid;

    // hold the grid that is given to the user
    private String[][] filledEquationsGrid;

    private final int CELL_SIZE = 75;
    private final int trainingTime = 10;
    private List<JTextField> answerFields; // a ListArray that can hold the actual interactive labels used to get inputs from user
                                           // confusing but its the only way to get what the user has put into the fields easily

    private Timer timer;
    private long startTime;
    private long elapsedTime;

    private Double[] NNArray = new Double[9];

    // the percentage chance for an input square to already have the answer inside it
    private final double difficulty = 0.25; 

    /**
     * Constructor that handles creating the grid and the UI element 
     */
    public MathsGrid() {
        System.out.println("MathsGrid.java: Instantiating Network.");
        Network neuralNetworkInterface = new Network("MathsGrid", 10);
        System.out.println("MathsGrid.java: Calling Save().");
        neuralNetworkInterface.save();
        
        // set up the window
        setTitle("Maths Grid Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(emptyEquationsGrid.length + 1, emptyEquationsGrid[0].length));

        // fill the grid with correct numbers
        answerGrid = fillGrid(emptyEquationsGrid);
        
        //prints the answers in the terminal
        for (String[] row : emptyEquationsGrid) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        } 
        
        // before starting the game, allow some time for the NN to train

        // creates a loading screen before the puzzle begins. this is used to give time to train the NN
        JDialog loadingPopUp = new JDialog();
        loadingPopUp.setTitle("Loading...");
        loadingPopUp.setSize(emptyEquationsGrid[0].length * CELL_SIZE, emptyEquationsGrid.length * CELL_SIZE);
        loadingPopUp.setLocationRelativeTo(null); // place at centre of screen
        loadingPopUp.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // dont let the user close the window, could mess up training algo
        loadingPopUp.add(new JLabel("Loading... Please wait.", SwingConstants.CENTER));
        loadingPopUp.setVisible(true);

        // allow for training time
        System.out.println("MathsGrid.java: Calling Train()");
        neuralNetworkInterface.train((double) trainingTime);
        System.out.println("MathsGrid.java: Training Complete.");

        // pause current thread to give the neural network time to train
        
        try {
            Thread.sleep(trainingTime*1000); // pause for ten seconds (method takes milliseconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 

        // once the training is done, remove the loading screen and continue the code
        loadingPopUp.setVisible(false);
        loadingPopUp.dispose();

        // take out some of the answers that have just been placed in the grid
        filledEquationsGrid = replaceAnswers(answerGrid);
        
        // fill the window with labels and text fields
        initializeGrid(filledEquationsGrid);

        setNNValues(answerFields);

        // add check bottom at the bottom of the window
        addCheckButton();

        // create and start timer
        JLabel timerLabel = new JLabel("");

        startTime = System.currentTimeMillis();
        
        // every time the timer ticks is an action, every time an action is taken the timer label in the window is updated
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText(elapsedTime + " seconds");
            }
        });

        // start and add the timer to the next available square, which is next to the check button
        timer.start();
        add(timerLabel);

        // add a label with the estimated difficulty of the puzzle
        JLabel difficultyLabel = new JLabel();
        difficultyLabel.setText((neuralNetworkInterface.predict(getValuesFromTextFields(answerFields))));
        add(difficultyLabel);
        
        
        // ensures the window is the correct shape and in the centre of the screen
        setSize(emptyEquationsGrid[0].length * CELL_SIZE, emptyEquationsGrid.length * CELL_SIZE);
        setLocationRelativeTo(null);

        // display the window
        setVisible(true);
    }

    /**
     * Creates a window and fills it with the correct labels and input fields
     * the grid layout is determined by the inputted grid
     * @param inputGrid a 2d array that represetns the grid 
     */
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
                    // make the symbols bigger
                    if (item.equals("+") || item.equals("-") || item.equals("*") || item.equals("=")){
                        square.setFont(new Font("Dialog", Font.PLAIN, 30));
                    }
                    add(square);
                }
            }
        }
    }

    /**
     * Adds a check button to the window
     * This handles getting the user's inputs from the inputFields and checking if they are correct
     * This is done inside the ActionListener for the buttons
     * If all answers are correct, this gets sends the data collected from the completed puzzle and
     * passes it to the neural network 
     */
    private void addCheckButton() {
        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get values from text fields - using a listArray is the easiest way of doing this, tried with just an array
                
                int[] answersIntArray = new int[12]; // must be 12 not 9 as some extra squares may be read unintentionally (this does not affect functionality)
                int pointer = 0;

                for (JTextField answer : answerFields){
                    String contents = answer.getText();
                    // input steralisation - if the inputted string isnt a digit between 1-9, set it to -1 so it will always be wrong
                    if (contents.length() == 1 && Character.isDigit(contents.charAt(0))){
                        answersIntArray[pointer] = Integer.parseInt(answer.getText());
                    }
                    else {
                        answersIntArray[pointer] = -1;
                    }
                    pointer+=1;
                }
                // now we have inputted values in an array                

                // check if equations are correct
                boolean isCorrect = checkEquations(answersIntArray, filledEquationsGrid);

                if (isCorrect) {
                    // end the game and timer and add the starting data and time taken to training data array
                    timer.stop();
                    double elapsedTimeDouble = (double) elapsedTime;
                    double elapsedTimeSecondsDouble = elapsedTimeDouble/1000;
                    setNNValues(answerFields);
                    neuralNetworkInterface.addTrainingData(NNArray, elapsedTimeSecondsDouble);

                    JOptionPane.showMessageDialog(MathsGrid.this, "All equations are correct!\n" + elapsedTime + " seconds taken");
                    dispose(); // this closes the program
                    neuralNetworkInterface.save();
                } 
                else {
                    JOptionPane.showMessageDialog(MathsGrid.this, "Some equations are incorrect.");
                }
            }
        });
        add(checkButton);
    }

    /**
     * Determines if all equations in the grid are correct
     * @param answersIntArray     all the users inputted values and any values given to them, in order top left to bottom right
     *                            if there is an empty field, it should be replaced with -1, meaning the equation will always be wrong
     * @param filledEquationsGrid 2d array that represents the grid given to the user, with randomised mathematical operations
     *                            every other line of this array is a equation that needs to be checked
     * @return                    return true if every equation is mathematically correct
     */
    private boolean checkEquations(int[] answersIntArray, String[][] filledEquationsGrid) {
        int arrayPointer = 0;

        // gets a sub array of all the users numbers on one line
        for (int i = 0; i < (filledEquationsGrid.length-2); i+=2){
            int[] answersSubArray = {answersIntArray[arrayPointer], answersIntArray[arrayPointer+1], answersIntArray[arrayPointer+2]};

            arrayPointer+=3;

            boolean correct = evaluateEquation(answersSubArray, filledEquationsGrid[i]);

            if (!correct) {
                return false; // atleast one equation is not correct
            }
        }
        return true; // all equations are correct
    }
    
    /**
     * Determines if a single equation is mathematically correct
     * @param inputNumberArray  holds the three numbers that the user has placed into the grid
     * @param equation          an array of strings that represents a mathematical equation
     *                          this does not have any user inputted numbers inside it as it is taken from the blank grid
     *                          you could take from the completed grid or the users gird as they have identical answers and operators
     * @return                  if the numbers given form a correct equation return true, if not return false
     */
    private boolean evaluateEquation(int[] inputNumberArray, String[] equation) {
        // take an equation line from the table, find the mathematical operations and the digits and 
        int result = inputNumberArray[0];
        
        String operator1 = equation[1];
        String operator2 = equation[3];
        int answer = Integer.parseInt(equation[6]);

        switch (operator1) {
            case "+":
                result = result + inputNumberArray[1];
                break;
            case "-":
                result = result - inputNumberArray[1];
                break;
            case "*":
                result = result * inputNumberArray[1];
                break;
        }
        switch (operator2) {
            case "+":
                result = result + inputNumberArray[2];
                break;
            case "-":
                result = result - inputNumberArray[2];
                break;
            case "*":
                result = result * inputNumberArray[2];
                break;
        }
        return (result == answer);
    }

    /**
     * takes an empty grid template and fills it with random numbers and operators
     * the grid returned will always be completable 
     * @param grid  the empty grid that is used as a template
     * @return      the filled and completed puzzle grid
     */
    private static String[][] fillGrid(String[][] grid) {
        Random random = new Random();

        String[] operations = {"+", "-", "*"}; // divide massively makes the game harder without a calculator and i want to stick to whole numbers

        // create an arrayList of number 1-9, then shuffle them
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            numbers.add(i);
        }
        //https://stackoverflow.com/questions/16112515/how-to-shuffle-an-arraylist
        Collections.shuffle(numbers);

        int pointer = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].equals("?")) { // input square - these may be removed later
                    grid[i][j] = Integer.toString(numbers.get(pointer++));
                } 
                else if (grid[i][j].equals("_")) { // operator square
                    grid[i][j] = operations[random.nextInt(operations.length)];
                }
            }
        }

        // the equations have been writen
        // now calculate and place the answers
        // these loops are written to allow for bigger grids incase we want to make bigger ones later
        for (int i = 0; i < grid.length; i++) {
            if (grid[i][grid[i].length - 2].equals("=")) { // if it is an equation line
                int answer = calculateRow(grid, i);
                grid[i][grid[i].length - 1] = Integer.toString(answer);
            }
            if (grid[grid.length - 2][i].equals("=")) { // if it is an equation column
                int answer = calculateColumn(grid, i);
                grid[grid.length - 1][i] = Integer.toString(answer);
            }
        }
        return grid;
    }

    /**
     * takes a filled grid and calculates the answer to a specific row
     * @param grid  the filled grid with operators inside
     * @param row   the row of the equation to be calculated
     * @return      the answer to the row's equation
     */
    private static int calculateRow(String[][] grid, int row) {
        int result = Integer.parseInt(grid[row][0]);

        for (int i = 2; i < grid[row].length - 1; i += 2) { // equations are every other line, so step = 2
            int operand = Integer.parseInt(grid[row][i]);
            String operation = grid[row][i-1];
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
            }
        }
        return result;
    }

    /**
     * takes a filled grid and calculates the answer to a specific column
     * @param grid     the filled grid with operators inside
     * @param column   the column of the equation to be calculated
     * @return         the answer to the column's equation
     */
    private static int calculateColumn(String[][] grid, int column) {
        int result = Integer.parseInt(grid[0][column]);

        for (int i = 2; i < grid.length - 1; i += 2) { // equations are every other line, so step = 2
            int operand = Integer.parseInt(grid[i][column]);
            String operation = grid[i-1][column];
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
            }
        }
        return result;
    }

    /**
     * takes a filled grid with all the user input squares filled and returns 
     * the same grid but with some of these squares replaced with "?"
     * this denotes an empty space for the user to fill in the final grid
     * 
     * @param grid     the filled grid with all squares filled
     * @return         the grid with some random squares changed
     */
    private String[][] replaceAnswers(String[][] grid) {
        Random random = new Random();

        for (int i = 0; i < grid.length-2; i+=2) {
            for (int j = 0; j < grid[i].length-2; j+=2) {
                if (random.nextDouble() > difficulty) { // CHANGE VARIABLE HERE TO REMOVE FEWER NUMBERS
                    grid[i][j] = "?"; // denotes an empty input square for the user
                }
            }
        }
        return grid;
    }

    public long getTime(){
        return elapsedTime;
    }

    public Double[] getNNValues(){
        return NNArray;
    }

    /**
     * takes a arrayList of Text Fields the user can enter their answers into
     * takes the values out of these fields and places them into the neural network's data array
     * 
     * @param inputList  arrayList of fields. the number of these may change depending on how many
     *                   squares the user has to fill
     */
    private void setNNValues(List<JTextField> inputList) {
        NNArray = getValuesFromTextFields(inputList);
    }

    /**
     * takes a arrayList of Text Fields the user can enter their answers into
     * and returns the values inside them
     * 
     * @param inputList  arrayList of fields. the number of these may change depending on how many
     *                   squares the user has to fill
     */
    private Double[] getValuesFromTextFields(List<JTextField> inputList){
        Double[] outputList = new Double[9];

        for (int i = 0; i < 9; i++){
            String inputText = inputList.get(i).getText();
            if (inputText.equals("")){
                outputList[i] = (double) -1;
            }
            else {
                outputList[i] = Double.parseDouble(inputText);
            }
        }
        return outputList;
    }

    public static void main(String[] args) {
        MathsGrid puzzleWindow = new MathsGrid();
    }
}
