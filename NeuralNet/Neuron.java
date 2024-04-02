import java.util.Random;

public class Neuron {
    Random random = new Random();

    // initialise bias, and the two weights as random doubles between -1 and 1
    private Double bias = randomFraction();
    public Double weight1 = randomFraction();
    private Double weight2 = randomFraction();

    // "old" versions of weights and bias, to trach changes during training.

    private Double oldBias = randomFraction();
    public Double oldWeight1 = randomFraction();
    private Double oldWeight2 = randomFraction();

    // returns a random value between -1 and 1 exclusive.
    public double randomFraction() {
        // 0-1 < x < 1-0
        return random.nextDouble() - random.nextDouble();
    }

    // picks a property at random, and adds a value between -1 and 1 at random to it.
    public void mutate() {
        int propertyToChange = random.nextInt(3);
        Double changeFactor = randomFraction();

        switch (propertyToChange) {
            case 1:
                weight1 += changeFactor;
                break;
            case 2:
                weight2 += changeFactor;
                break;
            default:
                bias += changeFactor;
        }
    }

    // rolls back the most recent change.
    public void forget() {
        bias = oldBias;
        weight1 = oldWeight1;
        weight2 = oldWeight2;
    }

    // copies the current value to the buffer.
    public void remember() {
        oldBias = bias;
        oldWeight1 = weight1;
        oldWeight2 = weight2;
    }

    // compute an output from two inputs, using this node's attributes.
    public double compute(double input1, double input2) {
        double preActivation = (weight1 * input1) + (weight2 * input2) + bias;
        double output = Util.sigmoid(preActivation);
        return output;
    }
}
