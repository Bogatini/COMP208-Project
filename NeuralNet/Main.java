import java.util.*;
import java.io.*;
import org.ejml.simple.*;


public class Main {
    
    /**
     * Method called from the command line to test the neural network.
     * @param args
     */
    public static void main(String[] args) {
        Network neuralNetwork = new Network("Sudoku", 3);
        neuralNetwork.save();
        
    }
}
