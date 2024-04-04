import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.ejml.simple.*;

public class Util {
    
    public static SimpleMatrix sigmoid (SimpleMatrix input) {
        for (int i = 0; i < input.getNumRows(); i++) {
            for (int j = 0; j < input.getNumCols(); j++) {
                input.set(i, j, 1 / (1 + Math.exp(-(input.get(i,j)))));
            }
        }
        return input;
    }

    public static Double meanSquareLoss (List<SimpleMatrix> correctAnswers, List<SimpleMatrix> predictedAnswers) {
        Double sumSquare = 0d;

        for (int i = 0; i < correctAnswers.size(); i++) {
            SimpleMatrix error = correctAnswers.get(i).minus(predictedAnswers.get(i));
            sumSquare += error.elementPower(2d).elementSum();
        }
        return sumSquare / (correctAnswers.size());
    }

    public static SimpleMatrix randomiseMatrix(int rows, int cols, Double minimum, Double maximum) {
        SimpleMatrix randomMatrix = new SimpleMatrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                randomMatrix.set(i, j, ThreadLocalRandom.current().nextDouble(minimum, maximum));
            }
        }
        return randomMatrix;
    }

    public static void printMatrix(SimpleMatrix matrix) {
        for (int i = 0; i < matrix.getNumRows(); i++) {
            for (int j = 0; j < matrix.getNumCols(); j++) {
                System.out.print(matrix.get(i, j) + " ");
            }
            System.out.println();
        }
    }
}
