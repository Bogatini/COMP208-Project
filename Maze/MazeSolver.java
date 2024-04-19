/**
 * Class that holds all training functions for a maze solver. A maze is created, 
 * represented as a 2d array, and a reinforcement learning alogrithm finds a 
 * path through it
 * Also handles two other interface classes, MazeCreator and MazeDisplay
 * 
 * @author Fred Mortimer 201639313
 */

import java.util.Random;
import java.io.BufferedReader; // used to get qvalues from file
import java.io.BufferedWriter; // used to write  qvalues to file
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MazeSolver {
    private static final int NUM_EPISODES = 10000; // i chose a random big number - the bigger this number the better the path will be
                                                   // 100 is too small, might not complete - 10000 seems to be good for a 10x10 maze
                                                   // or maybe keep it small - demonstrates how the saved q vaules can be used to extend actor's learning
                                                   // e.g. if it cant find a path with 100 eps, if you run the same maze and get the q values each time, eventually it will "learn" how to solve it
    
    // arbitrary values, can change later
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static final double EXPLORATION_RATE = 0.1;

    private static final int START_ROW = 0;
    private static final int START_COL = 0;
    private static final int END_COL = 14;
    private static final int END_ROW = 14;
    private static final int MAX_COL = 14;
    private static final int MAX_ROW = 14;
    
    // the different directions the actor can go
    // this is a LIE, the actual directions are right, left, up, down
    private static final int[][] ACTION_DELTAS = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}}; // up, down, left, right
    //private static final int[][] ACTION_DELTAS = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}}; // up, down, left, right + diagonals

    private static final int NUM_ACTIONS = ACTION_DELTAS.length;

    // q values are the expected rewards for an action taken in a given state
    // each item in qValues is an array of 4 doubles which hold the q value of each possible action
    // there is one of these arrays for each position on the maze
    private static double[][] qValues;

    // holds a representation of the maze as a 2d array
    private static String[][] MAZE;
  
    // https://stackoverflow.com/questions/24709769/java-using-system-getpropertyuser-dir-to-get-the-home-directory
    private static final String qValuesFilePath = System.getProperty("user.dir") + File.separator + "qValues.txt";
    private static final String mazeFilePath = System.getProperty("user.dir") + File.separator + "maze1.txt";

    MazeDisplay mazeDisplay;

    /**
     * Constructor that handles creating user's input window and output display
     * as well as running the training algorithm on a given maze
    */
    public MazeSolver() {

        MazeCreator mazeCreator = new MazeCreator(MAX_ROW+1, MAX_COL+1); // add one as MazeCreator uses the exact number of squares on each axis, while MazeSolver uses index numbers

        // pause this thread while waiting for the user to create their maze
        while (!mazeCreator.getContinueFlag()) {
            try {
                Thread.sleep(1);
            } 
            catch (InterruptedException e) {
                // Do nothing
            }
        }

        // this is just as bad but it works the same.
        //while (!mazeCreator.getContinueFlag()) {
        //    System.out.print(""); // i have no idea why, but just ";" doesnt work
        //}
        
        // user creates their own maze
        MAZE = mazeCreator.getMaze();
        
        // get maze from file
        //readMazeFile(mazeFilePath);

        mazeDisplay = new MazeDisplay(MAZE);

        // list of every possible state, with a list of qValues for each possible action when in said state
        qValues = new double[MAZE.length * MAZE[0].length][NUM_ACTIONS];

        // either gets qvalues or randomly generates them if no file is present
        readQValues(qValuesFilePath);

        // trains to find qValues
        train();

        // once train() has run, all q values are determined for the maze. WriteQValues() saves them to a file
        writeQValues(qValuesFilePath);

        writeMazeToFile(mazeFilePath);

        // run the code
        try {
            this.solveMaze();
        }
        catch (Exception e)  {
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }

    /**
     * if no qValues are being read from a file, generate small random ones
     */
    private void initializeQValues() {
        Random random = new Random();
        for (int i = 0; i < qValues.length; i++) {
            for (int j = 0; j < qValues[i].length; j++) {
                qValues[i][j] = random.nextDouble() * 0.1; // initialize with small random values
            }
        }
    }

    /**
     * Gets the old qValues from file, if the file is there. If not, generate them
     * @param filePath the filepath of qValues text file
     */
    private void readQValues(String filePath) {       
        try {
            File file = new File(filePath);
            
            if (file.exists()) { // if there is a file with given name
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                int row = 0;

                // can definately clean this up - should now just be a nested for loop, not a while and for
                while ((line = reader.readLine()) != null && row < qValues.length) { // read until the end of the file, or until all qValues have been filled - should probably just choose one
                    String[] values = line.split(", ");

                    for (int column = 0; column < values.length; column++) {
                        qValues[row][column] = Double.parseDouble(values[column]);
                    }
                    row++;
                }
                reader.close();
            }
            else { // if there is not qValues file, generate random values 
                initializeQValues();
            }
        } catch (IOException e) { // most likely reason this is triggered is a bad path name
            e.printStackTrace();
        }
    }

    /**
     * Write all qValues to a .txt file
     * @param filePath the filepath of the new file to be written to
     */
    private void writeQValues(String filePath) {
        System.out.println(filePath);
        try {
            File file = new File(filePath);
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (int i = 0; i < qValues.length; i++) {
                for (int j = 0; j < qValues[i].length; j++) {
                    writer.write(qValues[i][j] + ", ");
                }
                writer.write("\n");
            }
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }

    /**
     * Write maze to a .txt file
     * @param filePath the filepath of the new file to be written to
     */
    private void writeMazeToFile(String filePath) {
        try {
            File file = new File(filePath);
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // MIGHT BE THE WRONG WAY ROUND
            for (int i = 0; i < MAX_ROW; i++) {
                for (int j = 0; j < MAX_COL; j++) {
                    writer.write(MAZE[i][j] + ", ");
                }
                writer.write("\n");
            }
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }

    /**
     * Read maze to a .txt file
     * @param filePath the filepath of the new file to be read from
    */
    private void readMazeFile(String filePath) {       
        try {
            File file = new File(filePath);
            
            if (file.exists()) { // if there is a file with given name
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                String line;
                int row = 0;

                // can definately clean this up - should now just be a nested for loop, not a while and for
                while ((line = reader.readLine()) != null && row < MAX_COL) { // read until the end of the file, or until all qValues have been filled - should probably just choose one
                    String[] values = line.split(", ");

                    for (int column = 0; column < values.length; column++) {
                        MAZE[row][column] = values[column];
                    }
                    row++;
                }
                reader.close();
            }
        } catch (IOException e) { // most likely reason this is triggered is a bad path name
            e.printStackTrace();
        }
    }

    /**
     * The main training algorithm. The algorithm starts in the starting position 
     * and tries to make its way to the end position. At first it will move randomly
     * (if provided with random base qValues) but is punished if it moves into a wall
     * and rewarded if it moves into the end postion. 
     * 
     * The qValues obtained by this algorithm correlate to each possible action the 
     * actor can take in a given position. If, in a given position, an action will 
     * lead to a punishment, the action will have a low qValue by repeatedly taking 
     * actions that do not lead to punishments, and taking ones that lead to rewards
     * the actor will find its way through the maze
     */
    private void train() {
        Random random = new Random();
        for (int i = 0; i < NUM_EPISODES; i++) {
            int currentState = (START_ROW * MAZE[0].length) + START_COL;
            while (currentState != (END_ROW * MAZE[0].length) + END_COL) {
                int action;

                // EXPLORATION_RATE determines how often new random actions are chosen
                if (random.nextDouble() < EXPLORATION_RATE) {
                    action = random.nextInt(NUM_ACTIONS);
                } 
                else action = getBestAction(currentState);

                // move to next location
                int[] chosenAction = ACTION_DELTAS[action];
                int newCol = Math.max(0, Math.min(MAZE[0].length - 1, (currentState % MAZE[0].length) + chosenAction[0]));
                int newRow = Math.max(0, Math.min(MAZE.length - 1, (currentState / MAZE[0].length) + chosenAction[1]));
                
                int newState = (newRow * MAZE[0].length) + newCol;

                double reward;
                if (MAZE[newRow][newCol].equals("W")) { // if new position is a wall, punish actor
                    reward = -10000; // may need to change this?
                }
                else {
                    reward = 0; // dont punish actor if the new position is not a wall
                                //
                                // could add a check here, if the actor is getting close to the end point, give more of a reward than if it was going away
                                // this could be problematic as the actor should be willing to move away from the end point in order to reach it
                                // research papers say its fine to give a "delayed reward" - a big dump of points right at the end
                }

                if (newState == (END_ROW * MAZE[0].length) + END_COL) {
                    reward = 100; // big reward if actor reaches the end
                }

                //*** REINFORCMENT LEARNING STEPS HERE ***

                // get the best possible action and then the qValue for the current state/action
                double maxNextQValue = getBestQValue(newState);
                double targetValue = reward + (DISCOUNT_FACTOR * maxNextQValue);

                // this might work? if the actor goes away from the end point the reward is reduced, but going away from the end point may be needed to find the end?
                // after testing, this sometimes makes it push through walls because the reward reduction from it is less than if the actor backtracks
                //targetValue = targetValue - ((END_COL*END_ROW - currentState)/100);
                
                // implamentation of a simplified bellman equation
                // adjust the old qValue towards the target value. The amount this is done is determined by LEARNING_RATE 
                double oldQValue = qValues[currentState][action];
                double newQValue = oldQValue + LEARNING_RATE * (targetValue - oldQValue);

                // update qValues and move to the next position
                qValues[currentState][action] = newQValue;
                currentState = newState;
            }
        }
    }

    /**
     * For a given state, find the action with the highest qValue
     * @param   state the position in the maze to look at
     * @return  the position of the action with the highest qValue in ACTION_DELTAS
    */
    private int getBestAction(int state) {
        double[] qValuesForCurrentState = qValues[state];
        double bestQValue = qValuesForCurrentState[0];
        int bestAction = 0; // base case is just the first action in ACTION_DELTAS
        
        // bubble up and find biggest qValue
        for (int i = 1; i < NUM_ACTIONS; i++) {
            if (qValuesForCurrentState[i] > bestQValue) {
                bestQValue = qValuesForCurrentState[i];
                bestAction = i;
            }
        }
        return bestAction;
    }

    /**
     * For a given state, find the highest qValue of all possible actions
     * @param   state the position in the maze to look at
     * @return  the qValue of the best action - e.g. the highest number in the position's array in qValues
    */
    private double getBestQValue(int state) {
        double[] qValuesForCurrentState = qValues[state];
        double bestQValue = qValuesForCurrentState[0];
        for (int i = 1; i < NUM_ACTIONS; i++) {
            if (qValuesForCurrentState[i] > bestQValue) {
                bestQValue = qValuesForCurrentState[i];
            }
        }
        return bestQValue;
    }

    /**
     * Solves the maze stored in global variable MAZE, using the qValues created by the training data
     * Also handles displaying the maze being solved, step by step by updating mazeDisplay
    */
    private void solveMaze() throws Exception{
        // currentState must be a single value for training purposes
        int currentState = START_ROW * MAZE[0].length + START_COL;
        int count = 0;

        while (currentState != (END_ROW * MAZE[0].length + END_COL)) {
            count +=1;

            if (count > 100) { // if the maze hasnt been solved and the actor is going in circles
                throw new Exception("ERROR: Cannot complete maze");
            }

            // important difference between solveMaze() and train()
            // is that NO EXPLORATION is done here - only in train()

            int action = getBestAction(currentState);

            MAZE[currentState / MAZE[0].length][currentState % MAZE[0].length] = "P";
            
            int[] chosenAction = ACTION_DELTAS[action];
            int newCol = Math.max(0, Math.min(MAZE[0].length - 1, (currentState % MAZE[0].length) + chosenAction[0]));
            int newRow = Math.max(0, Math.min(MAZE.length - 1, (currentState / MAZE[0].length) + chosenAction[1]));
            currentState = newRow * MAZE[0].length + newCol;

            mazeDisplay.updateMaze(MAZE);
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { 
                // Restore the interrupted status
                Thread.currentThread().interrupt();}
        }
        // do the last step manually
        MAZE[currentState / MAZE[0].length][currentState % MAZE[0].length] = "P";
        mazeDisplay.updateMaze(MAZE);        
    }

    public String[][] getMaze(){
        return MAZE;
    }

    public static void main(String[] args) {
        MazeSolver mazeSolver = new MazeSolver();
    }
}