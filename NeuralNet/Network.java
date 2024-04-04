import java.util.*;
import org.ejml.simple.*;

public class Network {

    // Instantiate the list of layers, that is used as the network.
    List<Layer> layers = new ArrayList<Layer>();
    private Layer outputLayer;

    public Network(int[] structure) {
        layers.add(new Layer(structure[0]));
        for (int i = 1; i < structure.length; i++) {
            layers.add(new Layer(layers.get(i-1), structure[i]));
        }
        outputLayer = layers.get(layers.size()-1);
    }
    
    /*
    // Train the network
    public void train(List<List<Integer>> data, List<Double> answers, int epochCount) {
        Double bestEpochLoss = null;

        // Iterate for as many epochs as specified.
        for (int epoch = 0; epoch < epochCount; epoch++) {
            
            // Adapt Neuron.
            Neuron epochNeuron = neurons.get(epoch % 6);
            epochNeuron.mutate();

            List<Double> predictions = new ArrayList<Double>();
            for (int i = 0; i < data.size(); i++) {
                predictions.add(
                    i, 
                    predict(
                        data.get(i).get(0), 
                        data.get(i).get(1)
                    )
                );
            }
            
            Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);

            if(bestEpochLoss == null || thisEpochLoss < bestEpochLoss) {
                bestEpochLoss = thisEpochLoss;
                epochNeuron.remember();
            } else {
                epochNeuron.forget();
            }

            // Logging:
            if (epoch % 10 == 0) {
                System.out.println(
                    String.format(
                            "Epoch: %s | bestEpochLoss: %.15f | thisEpochLoss: %.15f", 
                        epoch, bestEpochLoss, thisEpochLoss
                    )
                );
            }
        }
    }
    */
    
    // make a prediction (this needs to be re-written to be recursive)
    public void predict(Double[] inputs) {
        SimpleMatrix predictions = outputLayer.compute(inputs);
        predictions.print();
    }
}
