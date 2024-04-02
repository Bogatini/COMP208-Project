import java.util.*;

public class Util {
    public static double sigmoid (double input) {
        return 1 / (1 + Math.exp(-input));
    }

    public static Double meanSquareLoss (List<Double> correctAnswers, List<Double> predictedAnswers) {
        double sumSquare = 0;

        for (int i = 0; i < correctAnswers.size(); i++) {
            double error = correctAnswers.get(i) - predictedAnswers.get(i);
            sumSquare += (error * error);
        }
        return sumSquare / (correctAnswers.size());
    }
}
