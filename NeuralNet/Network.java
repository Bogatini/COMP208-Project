/**
 * Class representing a Neural Network.
 * train() is used to train the network on a set of training data.
 * predict() is used to make a prediction of outputs from given inputs.
 */

import java.util.*;
import org.ejml.simple.*;

public class Network {

    // Instantiate the list of layers, that is used as the network.
    List<Layer> layers = new ArrayList<Layer>();
    private Layer outputLayer;


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
     * Training Method for the Network.
     * @param data The inputs of the Training Data, given as a list of vectors, where each vector is a set of inputs.
     * @param answers The outputs of the Training Data, given as a list of vectors, where each vector is a set of outputs
     * @param epochCount The number of epochs to train the data for. 
     *                   Note that each epoch will change one weight or the bias of one neuron in the network.
     */
    public void train(List<SimpleMatrix> data, List<SimpleMatrix> answers, int epochCount) {
        // initialise minimum loss and epoch counter
        Double bestEpochLoss = null;
        int epoch = 0;
        
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
                    epochLayer.mutateNeuron(neuron);

                    // test the accuracy of the network with this neuron mutated on the entire training data.
                    List<SimpleMatrix> predictions = new ArrayList<SimpleMatrix>();
                    for (int i = 0; i < data.size(); i++) {
                        predictions.add(predict(data.get(i)));
                    }
                    
                    // calculate the loss of this version of the network.
                    Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);

                    // compare this to previous most accurate network, decide to whether to save or revert the change.
                    if(bestEpochLoss == null || thisEpochLoss < bestEpochLoss) {
                        bestEpochLoss = thisEpochLoss;
                        epochLayer.remember(neuron);
                    } else {
                        epochLayer.forget(neuron);
                    }

                    // log the training progress every 10 epochs.
                    if (epoch % 10 == 0) {
                        System.out.println(
                            String.format(
                                    "Epoch: %s | bestEpochLoss: %.15f | thisEpochLoss: %.15f", 
                                epoch, bestEpochLoss, thisEpochLoss
                            )
                        );
                    }
                    
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
    public SimpleMatrix predict(SimpleMatrix inputs) {
        return outputLayer.compute(inputs);
    }
}
