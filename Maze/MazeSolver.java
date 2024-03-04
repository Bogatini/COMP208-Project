import java.util.Random; // https://docs.oracle.com/javase/8/docs/api/java/util/Random.html
import java.io.BufferedReader; // used to get qvalues from file
import java.io.BufferedWriter; // used to write  qvalues to file
import java.io.File;
import java.io.FileReader; //″
import java.io.FileWriter; //″
import java.io.IOException;

public class MazeSolver {

    // final variables are better - can be cached?
    
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
    // each item in qValues is list of 4 doubles which hold the q value of each possible action
    // there is one of these arrays for each position on the maze
    private static double[][] qValues;
    // would prefer these two values to be final but whatever
    public static String[][] MAZE;
    
    // depricated
    private static final String qValuesFilePath = "C:\\Users\\fmort\\Desktop\\COMP208 Project\\qValues.txt";

    private static final String mazeFilePath = "C:\\Users\\fmort\\Desktop\\COMP208 Project\\maze1.txt";

    // https://stackoverflow.com/questions/24709769/java-using-system-getpropertyuser-dir-to-get-the-home-directory - just fackin stole it 
    //private static final String bbb = System.getProperty("user.dir") + File.separator + "qValues.txt";

    MazeDisplay mazeDisplay;

    public MazeSolver() {

        MazeCreator mazeCreator = new MazeCreator(MAX_ROW+1, MAX_COL+1); // add one because oops MazeCreator uses the exact number of squares on each axis, while MazeSolver uses list positions

        // this whole section sucks - must find a way to pause MazeSolver and wait for MazeCreator to finish
        while (!mazeCreator.getContinueFlag()) {
            try {
                Thread.sleep(1); // This just pauses the loop for a short duration to avoid consuming CPU resources (is this even true??)
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
    private void readQValues(String fileName) {       
        try {
            File file = new File(fileName);
            
            if (file.exists()) { // if there is a file with given name
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
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

    // write all qValues to a .txt file of given name
    private void writeQValues(String fileName) {
        try {
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
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }

    private void writeMazeToFile(String fileName) {
        try {
            File file = new File(fileName);
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

    private void readMazeFile(String fileName) {       
        try {
            File file = new File(fileName);
            
            if (file.exists()) { // if there is a file with given name
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
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
                }

                if (newState == (END_ROW * MAZE[0].length) + END_COL) {
                    reward = 100; // big reward if actor reaches the end
                }

                double maxNextQValue = getBestQValue(newState);
                
                // big gross block, all this does is either set bad qValues lower, or good qValues higher. THIS IS THE IMPORTANT REINFORCEMENT LEARNING BIT
                double oldQValue = qValues[currentState][action];
                double targetValue = reward + (DISCOUNT_FACTOR * maxNextQValue);

                // this might work? if the actor goes away from the end point the reward is reduced, but going away from the end point may be needed to find the end?
                // after testing, this sometimes makes it push through walls because the reward reduction from it is less than if the actor backtracks
                //targetValue = targetValue - ((END_COL*END_ROW - currentState)/100);

                double newQValue = oldQValue + LEARNING_RATE * (targetValue - oldQValue);

                //System.out.println(reward + " " + targetValue + " " + newQValue);

                qValues[currentState][action] = newQValue;
                currentState = newState;
            }
        }
    }

    private int getBestAction(int state) {
        double[] qValuesForCurrentState = qValues[state];
        int bestAction = 0; // base case is just the first action in ACTION_DELTAS
        double bestQValue = qValuesForCurrentState[0];
        // bubble up and find biggest qValue
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
        // didnt want to work with coordinates, so currentState is a single integer, top left is 0 moving right increases by 1 each time - this was a massive pain to do the maths of
        int currentState = START_ROW * MAZE[0].length + START_COL;
        int count = 0;

        while (currentState != (END_ROW * MAZE[0].length + END_COL)) {
            count +=1;

            //if (count > END_ROW*END_COL) {
            if (count > 100) {
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

    public static void main(String[] args) {
        MazeSolver mazeSolver = new MazeSolver();

        // dont think this is how ur supposed to do this
        try {
            mazeSolver.solveMaze();
        }
        catch (Exception e)  {
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }
}
