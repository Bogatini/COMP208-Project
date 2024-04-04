import java.util.*;

public class Neuron {
    Random random = new Random();

    private Double bias;
    private List<Double> weights = new ArrayList<Double>();

    private Double oldBias;
    private List<Double> oldWeights = new ArrayList<Double>();

    public Neuron(int connections) {
        // randomise bias

        bias = randomFraction();
        oldBias = bias;
        
        // fill the weights list with random values.
        for (int i = 0; i < connections; i++) {
            weights.add(randomFraction());
        }

        oldWeights = weights;
    }


    // returns a random value between -1 and 1 exclusive.
    public double randomFraction() {
        // 0-1 < x < 1-0
        return random.nextDouble() - random.nextDouble();
    }

    // picks a property at random, and adds a value between -1 and 1 at random to it.
    public void mutate() {
        int propertyToChange = random.nextInt(weights.size() + 1);
        Double changeFactor = randomFraction();
        if (propertyToChange == weights.size()) {
            bias += changeFactor;
        } else {
            Double newWeight = weights.get(propertyToChange) + changeFactor;
            weights.set(propertyToChange, newWeight);
        }
    }

    // rolls back the most recent change.
    public void forget() {
        bias = oldBias;
        weights = oldWeights;
    }

    // copies the current value to the buffer.
    public void remember() {
        oldBias = bias;
        oldWeights = weights;
    }

    /* // compute an output from two inputs, using this node's attributes.
    public double compute(double input1, double input2) {
        double preActivation = (weight1 * input1) + (weight2 * input2) + bias;
        double output = Util.sigmoid(preActivation);
        return output;
    } */
}
