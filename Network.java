/**
 * Class representing a Neural Network.
 * train() is used to train the network on a set of training data.
 * predict() is used to make a prediction of outputs from given inputs.
 */

import java.util.*;
import org.ejml.simple.*;
import java.io.*;

public class Network {

    // Instantiate the list of layers, that is used as the network.
    List<Layer> layers = new ArrayList<Layer>();
    private Layer outputLayer;
    private String FOLDER = new String("");
    public File networkFile;
    public File trainingFile;


    /**
     * Constructor for a Network.
     * @param structure An array of integers, representing the number of neurons in each layer of the network. 
     *                  e.g. [2, 3, 3, 1] would create a network with 2 inputs, 2 hidden layers with 3 neurons each, and 1 output neuron.
     */
    public Network(int[] structure) {
        layers.add(new Layer(structure[0]));
        for (int i = 1; i < structure.length; i++) {
            layers.add(new Layer(layers.get(i-1), structure[i]));
        }
        outputLayer = layers.get(layers.size()-1);
    }

    /**
     * Constructor for a Network being loaded / to be loaded from a save file.
     * @param fileName The name of the file the network is/will be stored in.
     * @param numberOfInputs The number of inputs to the network.
     */
    public Network(String fileName, int numberOfInputs) {
        trainingFile = new File(FOLDER + fileName + "TrainingSet.txt");
        try {
            networkFile = new File(FOLDER + fileName + ".txt");
            if (networkFile.createNewFile()) {
                System.out.println("Creating New Network.");
                layers.add(new Layer(numberOfInputs));
                layers.add(new Layer(layers.get(layers.size() - 1), 4));
                layers.add(new Layer(layers.get(layers.size() - 1), 4));
                layers.add(new Layer(layers.get(layers.size() - 1), 1));
            } else {
                System.out.println("Network Save Found.");
                Scanner sc = new Scanner(networkFile);
                List<List<SimpleMatrix>> layerValues = new ArrayList<List<SimpleMatrix>>();
                while (sc.hasNextLine()) {
                    layerValues.add(Util.decodeSaveString(sc.nextLine()));
                }
                System.out.println("Read in " + layerValues.size() + " layers.");
                sc.close();
                layers.add(new Layer(numberOfInputs));
                for (int i = 1; i <= layerValues.size(); i++) {
                    layers.add(new Layer(layerValues.get(i-1), layers.get(i-1)));
                }
                System.out.println("Created " + layers.size() + " layers.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputLayer = layers.get(layers.size() - 1);
    }
    
    /**
     * Saves the network to the file.
     */
    public void save() {
        try {
            FileWriter wr = new FileWriter(networkFile, false);
            System.out.println("Network has " + layers.size() + " layers.");
            for (int i = 1; i < layers.size(); i++) {
                String layerString = Util.encodeSaveString(layers.get(i).getValues());
                wr.write(layerString + "\n");
                System.out.println("Wrote to file:\n" + layerString);
            }
            wr.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new piece of data to the training set.
     * @param inputs Starting state of the puzzle.
     * @param time Time taken to complete the puzzle.
     */
    public void addTrainingData(Double[] inputs, Double time) {
        String newEntry = new String("");
        for (Double input : inputs) {
            newEntry += String.valueOf(input) + ",";
        }
        if (time < 30d) {
            newEntry += String.valueOf(0d);
        } else if (time < 60) {
            newEntry += String.valueOf(0.25d);
        } else if (time < 300) {
            newEntry += String.valueOf(0.5d);
        } else if (time < 600) {
            newEntry += String.valueOf(0.75d);
        } else {
            newEntry += String.valueOf(1d);
        }
        try {
            FileWriter wr = new FileWriter(trainingFile, true);
            wr.write("\n" + newEntry);
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outward-facing method to train the network, based on the saved training data file.
     * @param duration The number of seconds to train for.
     */
    public void train(Double duration) {
        List<List<Double>> inputs = new ArrayList<List<Double>>();
        List<Double> outputs = new ArrayList<Double>();
        
        try {
            Scanner sc = new Scanner(trainingFile);
            while (sc.hasNextLine()) {
                String readString = sc.nextLine();
                String[] splitString = readString.split(",");
                List<Double> input = new ArrayList<Double>();
                for (int i = 0; i < splitString.length - 1; i++) {
                    input.add(Double.parseDouble(splitString[i]));
                }
                inputs.add(input);
                outputs.add(Double.parseDouble(splitString[splitString.length-1]));
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SimpleMatrix> data = new ArrayList<SimpleMatrix>();
        List<SimpleMatrix> answers = new ArrayList<SimpleMatrix>();

        for (int i = 0; i < inputs.size(); i++) {
            List<Double> input = inputs.get(i);
            SimpleMatrix inputMatrix = new SimpleMatrix(input.size(), 1);
            for (int j = 0; j < input.size(); j++) {
                inputMatrix.set(j, input.get(j));
            }
            inputMatrix.print();
            data.add(inputMatrix);
            answers.add(new SimpleMatrix(new double[]{outputs.get(i)}));
        }

        System.out.println("Loaded " + data.size() + " training examples, with " + answers.size() + " answers.");

        Double startTime = (double) (System.nanoTime() / 1000000000l);
        Double currentTime = startTime;
        System.out.println("Network.java: Starting Train for " + duration + " seconds.");
        while (currentTime < startTime + duration) {
            train(data, answers, 100, 0.1d);
            currentTime = (double) (System.nanoTime() / 1000000000l);
        }
        System.out.println("Network.java: Train complete.");
    }
    
    /**
     * Training Method for the Network.
     * @param data The inputs of the Training Data, given as a list of vectors, where each vector is a set of inputs.
     * @param answers The outputs of the Training Data, given as a list of vectors, where each vector is a set of outputs
     * @param epochCount The number of epochs to train the data for. 
     *                   Note that each epoch will change one weight or the bias of one neuron in the network.
     * @param learningRate The factor by which a neuron's property is increased or decreased with each epoch.
     */
    public void train(List<SimpleMatrix> data, List<SimpleMatrix> answers, int epochCount, Double learningRate) {
        // initialise epoch counter
        int epoch = 0;
        
        // test the initial performance of the network.
        List<SimpleMatrix> predictions = new ArrayList<SimpleMatrix>();
        for (int i = 0; i < data.size(); i++) {
            predictions.add(predict(data.get(i), false));
        }

        // calculate the loss of this version of the network.
        Double bestEpochLoss = Util.meanSquareLoss(answers, predictions);

        // repeat until epochCount hit.
        while (epoch < epochCount) {

            // iterate through the layers
            for (int layer = 1; layer < layers.size() && epoch < epochCount; layer++) {
                // set the layer for this epoch.
                Layer epochLayer = layers.get(layer);
                // find the number of neurons in this layer.
                int neurons = epochLayer.getSize();
                // iterate through every neuron in the layer.
                for (int neuron = 0; neuron < neurons && epoch < epochCount; neuron++) {
                    // randomly mutate the neuron.
                    epochLayer.mutateNeuron(neuron, learningRate);

                    // test the accuracy of the network with this neuron mutated on the entire training data.
                    predictions = new ArrayList<SimpleMatrix>();
                    for (int i = 0; i < data.size(); i++) {
                        predictions.add(predict(data.get(i), false));
                    }
                    
                    // calculate the loss of this version of the network.
                    Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);

                    // compare this to network before this mutation decide to whether to save or revert the change.
                    if(thisEpochLoss < bestEpochLoss) {
                        //System.out.println("Improved the Network by mutating neuron " + layer + "." + neuron +  ". Loss reduced from " + bestEpochLoss + " to " + thisEpochLoss);
                        bestEpochLoss = thisEpochLoss;
                        epochLayer.remember(neuron);
                    } else {
                        epochLayer.forget(neuron);
                    }

                    // log the training progress every 1000 epochs.
                    /* if (epoch % 1000 == 0) {
                        System.out.println(
                            String.format(
                                    "Epoch: %s | Learning Rate: %s | bestEpochLoss: %.15f | thisEpochLoss: %.15f", 
                                epoch, learningRate, bestEpochLoss, thisEpochLoss
                            )
                        );
                    } */
                    
                    // increment the epoch counter.
                    epoch ++;
                }
            }
        }
    }
    
    
    /**
     * Predicts outputs based on given outputs.
     * Done by calling Layer.compute() on the output layer of the network, starting the recursive process.
     * @param inputs A vector of the inputs.
     * @return A vector of the outputs.
     */
    public SimpleMatrix predict(SimpleMatrix inputs, boolean print) {
        return outputLayer.compute(inputs, print);
    }

    /**
     * Predicts the difficulty of a puzzle, using the network.
     * @param input Double[]
     * @return String representing the difficulty: "Trivial", "Easy", "Intermediate", "Challenging", or "Hard"
     */
    public String predict(Double[] input) {
        if (input.length != layers.get(0).getSize()) {
            System.out.println("Wrong input size.");
            return new String("Error");
        }
        else {
            SimpleMatrix inputMatrix = new SimpleMatrix(input.length, 1);
            for (int i = 0; i < input.length; i++) {
                inputMatrix.set(i, input[i]);
            }
            SimpleMatrix prediction = predict(inputMatrix, false);
            Double predictionValue = prediction.get(0);
            if (predictionValue < 0.125d) {
                return new String("Trivial");
            } else if (predictionValue < 0.375d) {
                return new String("Easy");
            } else if (predictionValue < 0.625d) {
                return new String("Intermediate");
            } else if (predictionValue < 0.875d) {
                return new String("Challenging");
            } else {
                return new String("Hard");
            }
        }
    
    }
}
