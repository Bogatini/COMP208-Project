import java.util.Random;

public class Neuron {
    Random random = new Random();

    // initialise bias, and the two weights as random doubles between -1 and 1;
    private Double bias = random.nextDouble() * 2 - 1;
    public Double weight1 = random.nextDouble() * 2 - 1;
    private Double weight2 = random.nextDouble() * 2 - 1;

    public double compute (double input1, double input2) {
        double preActivation = (this.weight1 * input1) + (this.weight2 * input2) + this.bias;
        double output = Util.sigmoid(preActivation);
        return output;
    }
}
