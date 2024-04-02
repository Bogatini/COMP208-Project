import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Network network = new Network();

        // List of training data, where values are weight and height in lb and ft (I think)
        List<List<Integer>> data = new ArrayList<List<Integer>>();
        data.add(Arrays.asList(115, 66));
        data.add(Arrays.asList(175, 78));
        data.add(Arrays.asList(205, 72));
        data.add(Arrays.asList(120, 67));
        
        // List of training answers, where 1.0 means female and 0.0 means male.
        List<Double> answers = Arrays.asList(1.0,0.0,0.0,1.0);

        System.out.println(answers);

        network.train(data, answers);

        Double prediction = network.predict(115, 66);
        System.out.println("prediction: " + prediction);
    }
}
