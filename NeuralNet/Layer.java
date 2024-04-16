/**
 * Class representing Layer of a Neural Network.
 * Neurons within the layer are not objects, but can be referenced by index within the layer.
 * e.g. each Neuron's bias and weights can be found in the rows in the bias vector and weights matrix at its respective index.
 * Matrices are used, as use of matrix multiplication increases speed of prediction.
 * The Efficient Java Matrix Library (EJML) is used for these matrix objects, specifically the SimpleMatrix object.
 * Documentation for this is availabel here: https://ejml.org/javadoc/org/ejml/simple/SimpleMatrix.html
 */

import java.util.*;

import org.ejml.simple.*;

public class Layer {
    Random random = new Random();
    
    private Layer prevLayer;
    private int size;
    private SimpleMatrix biases;
    private SimpleMatrix weights;

    private SimpleMatrix oldBiases;
    private SimpleMatrix oldWeights;

    private int propertyToChange;

    /* ************************************************************************************************ */
    /*                                     GENERAL METHODS                                              */
    /* ************************************************************************************************ */
    
    /**
     * Constructor for a Hidden or Output (e.g. non-input) Layer.
     * @param previousLayer Reference to the layer that this layer follows in the network.
     * @param numberOfNeurons Number of neurons to create in this layer.
     */
    public Layer(Layer previousLayer, int numberOfNeurons) {
        prevLayer = previousLayer;
        size = numberOfNeurons;
        biases = Util.randomiseMatrix(size, 1, -1d, 1d);
        weights = Util.randomiseMatrix(size, prevLayer.getSize(), -1d, 1d);

        oldBiases = biases;
        oldWeights = weights;
    }

    /**
     * Constructor for an input Layer.
     * @param numberOfInputs Number of input neurons.
     */
    public Layer(int numberOfInputs) {
        prevLayer = null;
        size = numberOfInputs;
    }

    /**
     * Getter method for the number of neurons in this layer.
     * @return the number of neurons in the layer.
     */
    public int getSize() {
        return size;
    }

    /* ************************************************************************************************ */
    /*                                     TRAINING METHODS                                             */
    /* ************************************************************************************************ */

    /**
     * Method to pick a random property of a neuron (one of its weights, or its bias) and change it by a random amount between -1 and 1.
     * @param neuron The index of the neuron to be mutated within the layer.
     */
    public void mutateNeuron(int neuron, Double learningRate) {
        propertyToChange = random.nextInt(weights.getNumCols() + 1);
        if (random.nextInt(2) == 0) {learningRate = -learningRate;}
        if (propertyToChange == weights.getNumCols()) {
            biases.set(neuron, biases.get(neuron) + learningRate);
        } else {
            weights.set(neuron, propertyToChange, weights.get(neuron, propertyToChange) + learningRate);
        }
    }

    /**
     * Method to tell a neuron to revert the most recent mutation.
     * @param neuron The index of the neuron within the layer.
     */
    public void forget(int neuron) {
        if (propertyToChange == weights.getNumCols()) {
            biases.set(neuron, oldBiases.get(neuron));
        } else {
            weights.set(neuron, propertyToChange, oldWeights.get(neuron, propertyToChange));
        }
    }

    /**
     * Method to tell a neuron to save the most recent mutation.
     * @param neuron The index of the neuron within the layer.
     */
    public void remember(int neuron) {
        if (propertyToChange == weights.getNumCols()) {
            oldBiases.set(neuron, biases.get(neuron));
        } else {
            oldWeights.set(neuron, propertyToChange, weights.get(neuron, propertyToChange));
        }
    }

    /* ************************************************************************************************ */
    /*                                     PREDICTING METHODS                                           */
    /* ************************************************************************************************ */


    /**
     * Recursive method used to predict values.
     * For the input layer, the activations are set to the inputs that are passed in to the function. (Base Case)
     * For non-input layers, the activations are computed based on the activations of the previous layer. (Recursive Step)
     * @param inputs Vector containing the inputs to be passed to the input layer.
     * @return Vector of the activations of the layer this method was called upon.
     */
    public SimpleMatrix compute(SimpleMatrix inputs, boolean print) {
        SimpleMatrix activations = new SimpleMatrix(size, 1);
        
        // if input layer (base case)
        if (prevLayer == null) {
            activations = inputs;
            if (print) {Util.printVector(activations);}
        }
        // otherwise (recursive step):
        else {
            SimpleMatrix previousActivations = prevLayer.compute(inputs, print);
            SimpleMatrix preActivation = weights.mult(previousActivations).plus(biases);
            activations = Util.sigmoid(preActivation);
            if (print) {Util.printVector(activations);}
        }
        
        return activations;
    }
}
