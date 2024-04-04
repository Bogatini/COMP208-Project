import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.ejml.simple.*;


public class Layer {
    Random random = new Random();
    
    private Layer prevLayer;
    private int size;
    private SimpleMatrix biases;
    private SimpleMatrix weights;

    private SimpleMatrix oldBiases;
    private SimpleMatrix oldWeights;

    // constructor for a hidden/output layer
    public Layer(Layer previousLayer, int numberOfNeurons) {
        prevLayer = previousLayer;
        size = numberOfNeurons;
        biases = Util.randomiseMatrix(size, 1, -1d, 1d);
        weights = Util.randomiseMatrix(size, prevLayer.getSize(), -1d, 1d);

        oldBiases = biases;
        oldWeights = weights;
    }

    // constructor for the input layer
    public Layer(int numberOfInputs) {
        prevLayer = null;
        size = numberOfInputs;
    }

    public int getSize() {
        return size;
    }

    // training functions
    public void mutateNeuron(int neuron) {
        int propertyToChange = random.nextInt(weights.getNumCols() + 1);
        Double changeFactor = ThreadLocalRandom.current().nextDouble(-1d, 1d);
        if (propertyToChange == weights.getNumCols()) {
            Double newBias = biases.get(neuron) + changeFactor;
            biases.set(neuron, newBias);
        } else {
            Double newWeight = weights.get(neuron, propertyToChange) + changeFactor;
            weights.set(neuron, propertyToChange, newWeight);
        }
    }

    public void forget(int neuron) {
        biases.set(neuron, oldBiases.get(neuron));
        for (int i = 0; i < weights.getNumCols(); i++) {
            weights.set(neuron, i, oldWeights.get(neuron, i));
        }
    }

    public void remember(int neuron) {
        oldBiases.set(neuron, biases.get(neuron));
        for (int i = 0; i < weights.getNumCols(); i++) {
            oldWeights.set(neuron, i, weights.get(neuron, i));
        }
    }

    // compute layer, for predictions
    public SimpleMatrix compute(Double[] inputs) {
        SimpleMatrix activations = new SimpleMatrix(size, 1);
        
        // if input layer
        if (prevLayer == null) {
            for (int i = 0; i < size; i++) {
                activations.set(i, inputs[i]);
            }
        }
        // otherwise:
        else {
            SimpleMatrix previousActivations = prevLayer.compute(inputs);
            SimpleMatrix preActivation = weights.mult(previousActivations).plus(biases);
            activations = Util.sigmoid(preActivation);
        }
        
        return activations;
    }
}
