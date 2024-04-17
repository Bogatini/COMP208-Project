/**
 * Class containing useful methods for processing, used in the training and use of Layer.java and Network.java.
 */

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.ejml.simple.*;

public class Util {
    public static String epochLog = "";
    
    public static List<SimpleMatrix> decodeSaveString(String saveString) {
        List<SimpleMatrix> values = new ArrayList<SimpleMatrix>();
        String[] matrixStrings = saveString.split("|");
        String[] biasStrings = matrixStrings[0].split(" ");
        SimpleMatrix biases = new SimpleMatrix(biasStrings.length, 1);
        for (int i = 0; i < biasStrings.length; i++) {
            biases.set(i, Double.parseDouble(biasStrings[i]));
        }
        values.add(biases);
        String[] weightRowStrings = matrixStrings[1].split(" ");
        SimpleMatrix weights = new SimpleMatrix(weightRowStrings.length, weightRowStrings[0].split(",").length);
        for (int i = 0; i < weightRowStrings.length; i++) {
            for (int j = 0; j < weightRowStrings[0].split(",").length; j++) {
                String[] weightStrings = weightRowStrings[i].split(",");
                weights.set(i, j, Double.parseDouble(weightStrings[j]));
            }
        }
        values.add(weights);
        String[] biasGradStrings = matrixStrings[2].split(" ");
        SimpleMatrix biasGrads = new SimpleMatrix(biasGradStrings.length, 1);
        for (int i = 0; i < biasGradStrings.length; i++) {
            biasGrads.set(i, Double.parseDouble(biasGradStrings[i]));
        }
        values.add(biasGrads);
        String[] weightGradRowStrings = matrixStrings[3].split(" ");
        SimpleMatrix weightGrads = new SimpleMatrix(weights);
        for (int i = 0; i < weightGradRowStrings.length; i++) {
            for (int j = 0; j < weightGradRowStrings[0].split(",").length; j++) {
                String[] weightGradStrings = weightGradRowStrings[i].split(",");
                weightGrads.set(i, j, Double.parseDouble(weightGradStrings[j]));
            }
        }
        values.add(weightGrads);
        return values;
        
    }
    
    public static void printVector(SimpleMatrix vector) {
        double[] vectorArray = new double[vector.getNumRows()];
        for (int i = 0; i < vector.getNumRows(); i++) {
            vectorArray[i] = vector.get(i);
        }
        System.out.println(Arrays.toString(vectorArray));
    }
    
    /**
     * Applies a sigmoid function to all of the values in a matrix.
     * @param input A matrix.
     * @return A matrix, with sigmoid function applied.
     */
    public static SimpleMatrix sigmoid (SimpleMatrix input) {
        for (int i = 0; i < input.getNumRows(); i++) {
            for (int j = 0; j < input.getNumCols(); j++) {
                input.set(i, j, 1 / (1 + Math.exp(-(input.get(i,j)))));
            }
        }
        return input;
    }

    /**
     * Calculates the mean square loss from two lists of vectors.
     * i.e. Finds the difference between each pair of values and squares it. Then, finds the mean result across all pairs.
     * @param correctAnswers one list of vectors.
     * @param predictedAnswers the other list of vectors.
     * @return the mean square loss
     */
    public static Double meanSquareLoss (List<SimpleMatrix> correctAnswers, List<SimpleMatrix> predictedAnswers) {
        Double sumSquare = 0d;

        for (int i = 0; i < correctAnswers.size(); i++) {
            SimpleMatrix error = correctAnswers.get(i).minus(predictedAnswers.get(i));
            sumSquare += error.elementPower(2d).elementSum();
        }
        return sumSquare / (correctAnswers.size());
    }

    /**
     * Fills a matrix with random values.
     * @param rows Number of rows in desired matrix.
     * @param cols Number of columns in desired matrix.
     * @param minimum Lower bound for generation of random values.
     * @param maximum Upper bound for generation of random values.
     * @return Randomised Matrix.
     */
    public static SimpleMatrix randomiseMatrix(int rows, int cols, Double minimum, Double maximum) {
        SimpleMatrix randomMatrix = new SimpleMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                randomMatrix.set(i, j, ThreadLocalRandom.current().nextDouble(minimum, maximum));
            }
        }
        return randomMatrix;
    }
}
