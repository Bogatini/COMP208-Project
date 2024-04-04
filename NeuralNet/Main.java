import java.util.*;
import org.ejml.simple.*;


public class Main {

    /**
     * Method called from the command line to test the neural network.
     * @param args
     */
    public static void main(String[] args) {
        int[] structure = {2, 10, 10, 10, 2};
        Network network = new Network(structure);
        
        Double[] inputs = {124d, 35d};
        network.predict(inputs);
        
    }
}
