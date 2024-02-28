import java.util.Random; // https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
import java.lang.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MazeSolver {
    
    public static String[][] MAZE = { // final improves testing performance as MAZE can be cached
        {" ","█"," "," "," "," "," "," "," "," "},
        {" ","█"," ","█"," "," "," ","█"," "," "},
        {" ","█"," "," ","█"," "," "," "," "," "},
        {" ","█"," ","█"," ","█"," "," "," "," "},
        {" ","█"," ","█"," "," "," ","█","█","█"},
        {" "," "," ","█"," "," "," "," "," "," "},
        {" ","█"," ","█"," ","█"," "," "," "," "},
        {" ","█"," ","█"," "," "," "," ","█"," "},
        {" ","█"," ","█"," "," ","█","█","█"," "},
        {" "," "," ","█"," "," "," "," "," "," "}
    };
    public static String[][] MAZE_COPY = MAZE; // printed at the end - just to show trail

    private static final int NUM_EPISODES = 10000; // i chose a random big number - the bigger this number the better the path will be
                                                   // 100 is too small, could not complete - 10000 seems to be good for a 10x10 maze
    
    // arbitrary values, can change later
    private static final double LEARNING_RATE = 0.1;
    private static final double DISCOUNT_FACTOR = 0.9;
    private static final double EXPLORATION_RATE = 0.1;

    private static final int START_ROW = 0;
    private static final int START_COL = 0;
    private static final int END_COL = 9;
    private static final int END_ROW = 9;
    
    // the different directions the actor can go
    // this is a LIE, the actual directions are right, left, up, down
    private static final int[][] ACTION_DELTAS = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}}; // up, down, left, right

    private static final int NUM_ACTIONS = ACTION_DELTAS.length;

    // q values are the expected rewards for an action taken in a given state
    // each item in qValues is list of 4 doubles which hold the q value of each possible action
    // there is one of these arrays for each position on the maze
    private double[][] qValues;

    private String qValuesFilePath = "C:\\Users\\fmort\\Desktop\\COMP208 Project\\qValues.txt";

    MazeDisplay mazeDisplay = new MazeDisplay(MAZE_COPY);

    public MazeSolver() {
        qValues = new double[MAZE.length * MAZE[0].length][NUM_ACTIONS];

        MazeCreator mazeCreator = new MazeCreator(END_ROW, END_COL);

        while (!mazeCreator.getContinueFlag()) {
            System.out.print(""); // i have no idea why, but just ";" doesnt work
        }

        //MAZE = mazeCreator.getNewMaze();

        // need to pause here and wait for this to finish ^^^^

        // either gets or randomly generates q values
        getQValues(qValuesFilePath);

        // trains to find qValues
        train();

        // once train() has run, all q values are determined for the maze. WriteQValues() saves them to a file
        writeQValues(qValuesFilePath); 
    }

    private void initializeQValues() {
        Random random = new Random();
        for (int i = 0; i < qValues.length; i++) {
            for (int j = 0; j < qValues[i].length; j++) {
                qValues[i][j] = random.nextDouble() * 0.1; // initialize with small random values
            }
        }
    }

    // get the old qValues from file, if the file is there. if not, generate them
    private void getQValues(String fileName) {       
        try {
            File file = new File(fileName);
            if (file.exists()) { 
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line;
                int row = 0;
                while ((line = reader.readLine()) != null && row < qValues.length) {
                    String[] values = line.split(", ");

                    for (int column = 0; column < values.length && column < qValues[row].length; column++) {
                        qValues[row][column] = Double.parseDouble(values[column]);
                    }
                    row++;
                }
                reader.close();
            }
            else {
                initializeQValues();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeQValues(String fileName) {
        try{
        File file = new File(fileName);
        file.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (int i = 0; i < qValues.length; i++) {
            for (int j = 0; j < qValues[i].length; j++) {
                writer.write(qValues[i][j] + ", ");
            }
            writer.write("\n");
        }

        writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    private void train() {
        Random random = new Random();
        for (int i = 0; i < NUM_EPISODES; i++) {
            int currentState = (START_ROW * MAZE[0].length) + START_COL;
            while (currentState != (END_ROW * MAZE[0].length) + END_COL) {
                int action;

                // i want the actor to prioritise solving the maze as efficiently as possible
                // maybe increase punishment the more steps it takes?

                
                // EXPLORATION_RATE determines how often new random actions are chosen
                if (random.nextDouble() < EXPLORATION_RATE) {
                    action = random.nextInt(NUM_ACTIONS);
                } 
                else action = getBestAction(currentState);

                int[] delta = ACTION_DELTAS[action];
                
                int newCol = Math.max(0, Math.min(MAZE.length - 1, (currentState % MAZE[0].length) + delta[0]));
                int newRow = Math.max(0, Math.min(MAZE[0].length - 1, (currentState / MAZE[0].length) + delta[1]));
                
                int newState = (newRow * MAZE[0].length) + newCol;

                double reward;
                if (MAZE[newRow][newCol].equals("█")) { // if new position is a wall, punish it
                    reward = -10000; // may need to reduce this?
                }
                else {
                    reward = 0; // dont punish the computer if the new position is not a wall
                }

                if (newState == (END_ROW * MAZE[0].length) + END_COL) {
                    reward = 100; // big reward if the computer reaches the end
                }

                double maxNextQValue = getBestQValue(newState);
                
                double oldQValue = qValues[currentState][action];
                double targetValue = reward + (DISCOUNT_FACTOR * maxNextQValue);
                double newQValue = oldQValue + LEARNING_RATE * (targetValue - oldQValue);

                qValues[currentState][action] = newQValue;
                currentState = newState;
            }
        }
    }

    private int getBestAction(int state) {
        double[] qValuesForCurrentState = qValues[state];
        int bestAction = 0;
        double bestQValue = qValuesForCurrentState[0];
        for (int i = 1; i < NUM_ACTIONS; i++) {
            if (qValuesForCurrentState[i] > bestQValue) {
                bestQValue = qValuesForCurrentState[i];
                bestAction = i;
            }
        }
        return bestAction;
    }

    // takes any state are returns the highest qValue in its position in the qValues array
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

    public void solveMaze() throws Exception{
        int currentState = START_ROW * MAZE[0].length + START_COL;
        int count = 0;

        while (currentState != (END_ROW * MAZE[0].length + END_COL)) {
            count +=1;

            //if (count > END_ROW*END_COL) {
            if (count > 100) {
                throw new Exception("ERROR: Cannot complete maze");
            }

            int action = getBestAction(currentState);

            // print the most recent action
            //System.out.println(Arrays.toString(ACTION_DELTAS[action]));
            // show the current state
            MAZE_COPY[currentState / MAZE[0].length][currentState % MAZE[0].length] = "▒";
            
            int[] delta = ACTION_DELTAS[action];
            int newCol = Math.max(0, Math.min(MAZE[0].length - 1, (currentState % MAZE[0].length) + delta[0]));
            int newRow = Math.max(0, Math.min(MAZE.length - 1, (currentState / MAZE[0].length) + delta[1]));
            currentState = newRow * MAZE[0].length + newCol;

            mazeDisplay.updateMaze(MAZE_COPY);
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) { 
                // Restore the interrupted status
                Thread.currentThread().interrupt();}
        }
        //System.out.println("(" + END_ROW + ", " + END_COL + ")"); // this line cheats a bit
        MAZE_COPY[currentState / MAZE[0].length][currentState % MAZE[0].length] = "▒";
        mazeDisplay.updateMaze(MAZE_COPY);

        //System.out.println(count+1 + "\n");
        
    }

    public static void main(String[] args) {
        MazeSolver mazeSolver = new MazeSolver();


        try {
            mazeSolver.solveMaze();
        }
        catch (Exception e)  {
            System.err.println(e.getMessage());
        }
        
    }
}
