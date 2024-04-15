import java.util.*;
import java.io.*;
import org.ejml.simple.*;


public class Main {

    /**
     * Method called from the command line to test the neural network.
     * @param args
     */
    public static void main(String[] args) {
        
        // create the network
        int[] structure = {2, 5, 5, 5, 1};
        Network network = new Network(structure);
        
        // read in training set from file.
        List<Double> heights = new ArrayList<Double>();
        List<Double> weights = new ArrayList<Double>();
        List<Double> sexes = new ArrayList<Double>();
        
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("NeuralNet/TrainingSet.csv"));
            // skip the headings.
            br.readLine();
            // read all the data.
            while ((line = br.readLine()) != null) {
                String[] person = line.split(new String(","));
                heights.add(Double.valueOf(person[0]));
                weights.add(Double.valueOf(person[1]));
                sexes.add(person[2].equals("Male") ? 1d : 0d);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SimpleMatrix> trainingData = new ArrayList<SimpleMatrix>();
        List<SimpleMatrix> trainingAnswers = new ArrayList<SimpleMatrix>();

        for (int i = 0; i < heights.size(); i++) {
            trainingData.add(new SimpleMatrix(new double[][]{{heights.get(i)},{weights.get(i)}}));
            trainingAnswers.add(new SimpleMatrix(new double[][]{{sexes.get(i)}}));
        }

        System.out.println("\nTraining Config:");
        System.out.println(trainingData.size() + " inputs and " + trainingAnswers.size() + " outputs.");
        System.out.println("inputs have dimensions: ");
        trainingData.get(0).printDimensions();
        System.out.println("outputs have dimensions: ");
        trainingAnswers.get(0).printDimensions();
        System.out.println("\nBeginning Training.");

        //network.train(trainingData, trainingAnswers, 100000);
        System.out.println("Training complete.\n");

        // read in test dataset from file.

        heights = new ArrayList<Double>();
        weights = new ArrayList<Double>();
        sexes = new ArrayList<Double>();
        
        line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("NeuralNet/TestSet.csv"));
            // skip the headings.
            br.readLine();
            // read all the data.
            while ((line = br.readLine()) != null) {
                String[] person = line.split(new String(","));
                heights.add(Double.valueOf(person[0]));
                weights.add(Double.valueOf(person[1]));
                sexes.add(person[2].equals("Male") ? 1d : 0d);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SimpleMatrix> testData = new ArrayList<SimpleMatrix>();

        for (int i = 0; i < heights.size(); i++) {
            testData.add(new SimpleMatrix(new double[][]{{heights.get(i)},{weights.get(i)}}));
        }
        
        // test trained network.
        int tests = testData.size();
        int correct = 0;
        int incorrect = 0;

        for (int test = 0; test < tests; test++) {
            SimpleMatrix prediction = network.predict(testData.get(test), false);
            Double predictedValue = prediction.get(0);
            int answer = (int) Math.round(sexes.get(test));
            int guess = (int) Math.round(predictedValue);
            System.out.println("Test " + test + ": " + predictedValue + "; answer: " + answer + "; guess: " + guess + "; correct: " + (guess == answer));
            if (guess == answer) {
                correct ++;
            } else {
                incorrect ++;
            }
        }
        System.out.println("\nTesting Complete.\n" + tests + " tests: " + correct + " correct, " + incorrect + " incorrect.\nAccuracy: " + (Math.round((double)correct/((double)tests) * 100d)) + "%");

        // test again, printing activations:
        SimpleMatrix prediction = network.predict(new SimpleMatrix(new double[]{175.14d,107.25d}), true);
        
        
    }
}
