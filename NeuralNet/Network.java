import java.util.*;

public class Network {

    List<Neuron> neurons = Arrays.asList(
        new Neuron(), new Neuron(), new Neuron(), // input nodes
        new Neuron(), new Neuron(), // hidden nodes
        new Neuron() // output node
    );

    // train the network
    public void train(List<List<Integer>> data, List<Double> answers) {
        Double bestEpochLoss = null;

        for (int epoch = 0; epoch < 1000; epoch++) {
            
            // adapt neuron
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
    
    // make a prediction
    public Double predict(Integer input1, Integer input2) {
        return neurons.get(5).compute(
            neurons.get(4).compute(
                neurons.get(2).compute(input1, input2), 
                neurons.get(1).compute(input1, input2)
            ),
            neurons.get(3).compute(
                neurons.get(1).compute(input1, input2),
                neurons.get(0).compute(input1, input2)
            )
        );
    }
}
