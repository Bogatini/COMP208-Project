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
    
    
    // Train the network
    public void train(List<SimpleMatrix> data, List<SimpleMatrix> answers, int epochCount) {
        Double bestEpochLoss = null;
        int epoch = 0;

        // Iterate for as many epochs as specified.
        for (int layer = 1; layer < layers.size() && epoch < epochCount; layer++) {
            Layer epochLayer = layers.get(layer);
            int neurons = epochLayer.getSize();
            for (int neuron = 0; neuron < neurons && epoch < epochCount; neuron++) {
                epochLayer.mutateNeuron(neuron);

                List<SimpleMatrix> predictions = new ArrayList<SimpleMatrix>();
                for (int i = 0; i < data.size(); i++) {
                    predictions.add(predict(data.get(i)));
                }
                
                Double thisEpochLoss = Util.meanSquareLoss(answers, predictions);

                if(bestEpochLoss == null || thisEpochLoss < bestEpochLoss) {
                    bestEpochLoss = thisEpochLoss;
                    epochLayer.remember(neuron);
                } else {
                    epochLayer.forget(neuron);
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

                epoch ++;
            }
        }
    }
    
    
    // make a prediction
    public SimpleMatrix predict(SimpleMatrix inputs) {
        return outputLayer.compute(inputs);
    }
}
