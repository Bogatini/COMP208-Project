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
        /* neuralNetwork.addTrainingData(new Double[]{0d, 1d, 1d}, 65d);
        neuralNetwork.addTrainingData(new Double[]{0d, 0d, 1d}, 99.73d);
        neuralNetwork.addTrainingData(new Double[]{1d, 1d, 0d}, 56.2d);
        neuralNetwork.addTrainingData(new Double[]{1d, 0d, 1d}, 48d); */
        // neuralNetwork.train(30d);
        neuralNetwork.save();
        System.out.println(neuralNetwork.predict(new Double[]{1d, 0d, 1d}));
        System.out.println(neuralNetwork.predict(new Double[]{1d, 1d, 1d}));
        System.out.println(neuralNetwork.predict(new Double[]{0d, 0d, 1d}));
        System.out.println(neuralNetwork.predict(new Double[]{1d, 0d, 0d}));
        
    }
}
