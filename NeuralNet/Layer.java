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
    private SimpleMatrix weightGradients;
    private SimpleMatrix biasGradients;

    private SimpleMatrix oldBiases;
    private SimpleMatrix oldWeights;

    private int propertyToChange;

    /* ************************************************************************************************ */
    /*                                     GENERAL METHODS                                              */
    /* ************************************************************************************************ */
    
    /**
     * Constructor to restore from save a Hidden or Output Layer.
     * @param loadBiases          the biases           matrix loaded from file
     * @param loadWeights         "   weights          "
     * @param loadWeightGradients "   weight gradients "
     * @param loadBiasGradients   "   bias gradients   "
     * @param previousLayer reference to the layer that this layer follows in the network.
     */
    public Layer( List<SimpleMatrix> values, Layer previousLayer) {
        prevLayer = previousLayer;

        biases = values.get(0);
        weights = values.get(1);
        biasGradients = values.get(2);
        weightGradients = values.get(3);
        
        size = biases.getNumRows();
    }
    
    /**
     * Constructor for a new Hidden or Output (e.g. non-input) Layer.
     * @param previousLayer Reference to the layer that this layer follows in the network.
     * @param numberOfNeurons Number of neurons to create in this layer.
     */
    public Layer(Layer previousLayer, int numberOfNeurons) {
        prevLayer = previousLayer;
        size = numberOfNeurons;
        biases = Util.randomiseMatrix(size, 1, -1d, 1d);
        biasGradients = SimpleMatrix.filled(size, 1, 0d);
        weights = Util.randomiseMatrix(size, prevLayer.getSize(), -1d, 1d);
        weightGradients = SimpleMatrix.filled(size, prevLayer.getSize(), 0d);

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

    public List<SimpleMatrix> getValues() {
        List<SimpleMatrix> values = new ArrayList<SimpleMatrix>();
        values.add(biases);
        values.add(weights);
        values.add(biasGradients);
        values.add(weightGradients);
        return values;
    }

    /* ************************************************************************************************ */
    /*                                     TRAINING METHODS                                             */
    /* ************************************************************************************************ */

    /**
     * Method to pick a random property of a neuron (one of its weights, or its bias) and change it by a random amount between -1 and 1.
     * @param neuron The index of the neuron to be mutated within the layer.
     * @param learningRate The factor to increase or decrease a parameter by with each mutation.
     */
    public void mutateNeuron(int neuron, Double learningRate) {
        // randomly choose a neuron parameter to change with this mutation.
        // if propertyToChange equals the number of weights the neuron has, then the bias is adjusted.
        // otherwise, propertyToChange refers to the index of the weight being adjusted.
        propertyToChange = random.nextInt(weights.getNumCols() + 1);
        // if the bias is being adjusted:
        if (propertyToChange == weights.getNumCols()) {
            // find the current gradient of change for this neuron's bias. (-1, 0, or 1)
            Double grad = biasGradients.get(neuron);
            // if gradient is currently zero (== does not work for doubles):
            if (-0.1d < grad && grad < 0.1d) {
                // randomly assign a gradient of either -1 or 1
                grad = random.nextBoolean() ? 1d : -1d;
            }
            // adjust the bias, and save the gradient.
            biases.set(neuron, biases.get(neuron) + (learningRate * grad));
            biasGradients.set(neuron, grad);
        } 
        // if a weight is being adjusted
        else {
            // find the current gradient of change for this weight. (-1, 0, or 1)
            Double grad = weightGradients.get(neuron, propertyToChange);
            // if gradient is currently zero (== does not work for doubles): 
            if (-0.1d < grad && grad < 0.1d) {
                // randomly assign a gradient of either -1 or 1
                grad = random.nextBoolean() ? 1d : -1d;
            }
            // adjust the weight, and save the gradient.
            weights.set(neuron, propertyToChange, weights.get(neuron, propertyToChange) + (learningRate * grad));
            weightGradients.set(neuron, propertyToChange, grad);
        }
    }

    /**
     * Method to tell a neuron to revert the most recent mutation.
     * @param neuron The index of the neuron within the layer.
     */
    public void forget(int neuron) {
        // if the bias was changed:
        if (propertyToChange == weights.getNumCols()) {
            // invert gradient (next time this property is adjusted, it will be changed in the other direction).
            biasGradients.set(neuron, -1d * biasGradients.get(neuron));
            // set bias back to old value held in oldBiases.
            biases.set(neuron, oldBiases.get(neuron));
        } else {
            // invert gradient.
            weightGradients.set(neuron, propertyToChange, -1d * weightGradients.get(neuron, propertyToChange));
            // set weight back to old value held in oldWeights.
            weights.set(neuron, propertyToChange, oldWeights.get(neuron, propertyToChange));
        }
    }

    /**
     * Method to tell a neuron to save the most recent mutation.
     * Note that the biases, weights and gradients are not changed, so are left in their experimental state set in the mutate() function.
     * @param neuron The index of the neuron within the layer.
     */
    public void remember(int neuron) {
        // if the bias was changed:
        if (propertyToChange == weights.getNumCols()) {
            // update oldBiases to represent the current mutation.
            oldBiases.set(neuron, biases.get(neuron));
        } else {
            // update oldWeights to represent the current mutation.
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
     * @param print Whether to print the activations (for debugging).
     * @return Vector of the activations of the layer this method was called upon.
     */
    public SimpleMatrix compute(SimpleMatrix inputs, boolean print) {
        SimpleMatrix activations = new SimpleMatrix(size, 1);
        
        // if input layer (base case)
        if (prevLayer == null) {
            // set the activations to the inputs given.
            activations = inputs;
            if (print) {Util.printVector(activations);}
        }
        // otherwise (recursive step):
        else {
            // recursively calculate previous layer's activations.
            SimpleMatrix previousActivations = prevLayer.compute(inputs, print);
            // multiply the weights matrix by the previous layer's activations vector and add the biases vector.
            // this has the same effect as, for each neuron, calculating pre-activation as (w_1 * a_1 + w_2 * a_2 + ... + w_n * a_n) + b
            SimpleMatrix preActivation = weights.mult(previousActivations).plus(biases);
            // apply the sigmoid function to all values in the the preActivation vector, so that they lie between 0 and 1.
            activations = Util.sigmoid(preActivation);
            if (print) {Util.printVector(activations);}
        }
        
        // return the activations vector, for the next layer, or for the output.
        return activations;
    }
}
