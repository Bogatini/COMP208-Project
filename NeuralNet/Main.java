import java.util.*;
import org.ejml.simple.*;


public class Main {

    /**
     * Method called from the command line to test the neural network.
     * @param args
     */
    public static void main(String[] args) {
        
        List<Double> heights = new ArrayList<Double>();
        List<Double> weights = new ArrayList<Double>();
        List<Double> sexes = new ArrayList<Double>();
        String[] trainingArray = rawTrainingData.split("\r\n");
        for (int i = 0; i < trainingArray.length; i += 3) {
            heights.add(Double.valueOf(trainingArray[i]));
            weights.add(Double.valueOf(trainingArray[i+1]));
            sexes.add(trainingArray[i+2].equals("Male") ? 1d : 0d);
        }

        List<SimpleMatrix> trainingData = new ArrayList<SimpleMatrix>();
        List<SimpleMatrix> trainingAnswers = new ArrayList<SimpleMatrix>();

        for (int i = 0; i < heights.size(); i++) {
            trainingData.add(new SimpleMatrix(new double[][]{{heights.get(i)},{weights.get(i)}}));
            trainingAnswers.add(new SimpleMatrix(new double[][]{{sexes.get(i)}}));
        }

        System.out.println(trainingData.size() + " inputs and " + trainingAnswers.size() + " outputs.");
        System.out.println("inputs have dimensions: ");
        trainingData.get(0).printDimensions();
        System.out.println("outputs have dimensions: ");
        trainingAnswers.get(0).printDimensions();

        int[] structure = {2, 10, 10, 10, 1};
        Network network = new Network(structure);

        network.train(trainingData, trainingAnswers, 1000);
        

        
        // network.predict(inputs);
        
    }

    private static String rawTrainingData = new String(
        "146.3232413\r\n" + //
        "59.861065\r\n" + //
        "Female\r\n" + //
        "175.6954121\r\n" + //
        "77.86368697\r\n" + //
        "Male\r\n" + //
        "183.2161637\r\n" + //
        "72.13199162\r\n" + //
        "Male\r\n" + //
        "184.2452685\r\n" + //
        "77.54599954\r\n" + //
        "Male\r\n" + //
        "132.3022607\r\n" + //
        "55.18849649\r\n" + //
        "Female\r\n" + //
        "149.8639136\r\n" + //
        "66.05565459\r\n" + //
        "Female\r\n" + //
        "191.1730884\r\n" + //
        "83.63133864\r\n" + //
        "Male\r\n" + //
        "135.4071744\r\n" + //
        "47.01445695\r\n" + //
        "Female\r\n" + //
        "184.9957625\r\n" + //
        "71.93582824\r\n" + //
        "Male\r\n" + //
        "179.9908867\r\n" + //
        "65.92079489\r\n" + //
        "Male\r\n" + //
        "186.1618374\r\n" + //
        "83.72019383\r\n" + //
        "Male\r\n" + //
        "177.784335\r\n" + //
        "51.59443143\r\n" + //
        "Male\r\n" + //
        "124.5761544\r\n" + //
        "49.49065937\r\n" + //
        "Female\r\n" + //
        "135.0845874\r\n" + //
        "57.01715684\r\n" + //
        "Female\r\n" + //
        "161.783083\r\n" + //
        "63.11811732\r\n" + //
        "Female\r\n" + //
        "147.1984523\r\n" + //
        "74.90954714\r\n" + //
        "Female\r\n" + //
        "168.6195333\r\n" + //
        "68.71086764\r\n" + //
        "Male\r\n" + //
        "180.3799948\r\n" + //
        "58.5831375\r\n" + //
        "Male\r\n" + //
        "172.9806484\r\n" + //
        "91.76506667\r\n" + //
        "Male\r\n" + //
        "183.2740005\r\n" + //
        "83.53897404\r\n" + //
        "Male\r\n" + //
        "135.8408238\r\n" + //
        "75.15031468\r\n" + //
        "Female\r\n" + //
        "177.3010401\r\n" + //
        "61.54651\r\n" + //
        "Male\r\n" + //
        "170.228071\r\n" + //
        "63.16246219\r\n" + //
        "Male\r\n" + //
        "141.8363614\r\n" + //
        "45.4665998\r\n" + //
        "Female\r\n" + //
        "151.4646936\r\n" + //
        "51.1065089\r\n" + //
        "Female\r\n" + //
        "150.2536868\r\n" + //
        "56.38212115\r\n" + //
        "Female\r\n" + //
        "123.0070061\r\n" + //
        "66.68550969\r\n" + //
        "Female\r\n" + //
        "170.8142259\r\n" + //
        "72.87114003\r\n" + //
        "Male\r\n" + //
        "179.5604019\r\n" + //
        "93.3795254\r\n" + //
        "Male\r\n" + //
        "146.7985959\r\n" + //
        "58.96877805\r\n" + //
        "Female\r\n" + //
        "145.830298\r\n" + //
        "91.71072835\r\n" + //
        "Female\r\n" + //
        "144.981023\r\n" + //
        "70.54812663\r\n" + //
        "Male\r\n" + //
        "139.3907781\r\n" + //
        "58.18591881\r\n" + //
        "Female\r\n" + //
        "182.6131284\r\n" + //
        "90.55450022\r\n" + //
        "Male\r\n" + //
        "178.4496356\r\n" + //
        "65.99284671\r\n" + //
        "Male\r\n" + //
        "173.649627\r\n" + //
        "70.06223697\r\n" + //
        "Male\r\n" + //
        "149.6358087\r\n" + //
        "66.96144199\r\n" + //
        "Female\r\n" + //
        "166.950541\r\n" + //
        "68.60650747\r\n" + //
        "Male\r\n" + //
        "152.9447652\r\n" + //
        "47.80660514\r\n" + //
        "Female\r\n" + //
        "170.0438094\r\n" + //
        "69.52377418\r\n" + //
        "Male\r\n" + //
        "135.7625193\r\n" + //
        "39.98973038\r\n" + //
        "Female\r\n" + //
        "146.4209564\r\n" + //
        "50.66569887\r\n" + //
        "Female\r\n" + //
        "165.4572847\r\n" + //
        "76.9860487\r\n" + //
        "Female\r\n" + //
        "159.9314963\r\n" + //
        "69.20584106\r\n" + //
        "Male\r\n" + //
        "173.6478179\r\n" + //
        "85.91706134\r\n" + //
        "Male\r\n" + //
        "162.2114966\r\n" + //
        "53.98530301\r\n" + //
        "Female\r\n" + //
        "171.1970616\r\n" + //
        "96.909219\r\n" + //
        "Male\r\n" + //
        "154.2079222\r\n" + //
        "64.10437771\r\n" + //
        "Male\r\n" + //
        "163.8065453\r\n" + //
        "76.4823852\r\n" + //
        "Male\r\n" + //
        "152.0791785\r\n" + //
        "70.49920032\r\n" + //
        "Female\r\n" + //
        "173.0748152\r\n" + //
        "77.17787716\r\n" + //
        "Male\r\n" + //
        "164.2692232\r\n" + //
        "61.26559153\r\n" + //
        "Male\r\n" + //
        "156.7976193\r\n" + //
        "68.21134945\r\n" + //
        "Female\r\n" + //
        "157.8895769\r\n" + //
        "92.24610011\r\n" + //
        "Male\r\n" + //
        "180.9764798\r\n" + //
        "95.59478219\r\n" + //
        "Male\r\n" + //
        "175.2433961\r\n" + //
        "77.13018201\r\n" + //
        "Male\r\n" + //
        "155.754493\r\n" + //
        "86.9498469\r\n" + //
        "Male\r\n" + //
        "180.0229055\r\n" + //
        "70.90164462\r\n" + //
        "Male\r\n" + //
        "180.3403211\r\n" + //
        "74.10095369\r\n" + //
        "Male\r\n" + //
        "144.4119408\r\n" + //
        "54.01444259\r\n" + //
        "Female\r\n" + //
        "147.2221773\r\n" + //
        "76.70733287\r\n" + //
        "Female\r\n" + //
        "152.7088563\r\n" + //
        "62.79598838\r\n" + //
        "Female\r\n" + //
        "142.3098204\r\n" + //
        "81.7159541\r\n" + //
        "Male\r\n" + //
        "166.3135265\r\n" + //
        "88.13290343\r\n" + //
        "Male\r\n" + //
        "163.0913943\r\n" + //
        "78.37616668\r\n" + //
        "Male\r\n" + //
        "167.0077238\r\n" + //
        "62.09138124\r\n" + //
        "Male\r\n" + //
        "143.3310166\r\n" + //
        "71.15222384\r\n" + //
        "Female\r\n" + //
        "161.3907099\r\n" + //
        "75.69117549\r\n" + //
        "Female\r\n" + //
        "167.5388812\r\n" + //
        "99.60316557\r\n" + //
        "Male\r\n" + //
        "169.4368352\r\n" + //
        "107.3581389\r\n" + //
        "Male\r\n" + //
        "183.2013182\r\n" + //
        "96.94715589\r\n" + //
        "Male\r\n" + //
        "156.8690279\r\n" + //
        "53.62286242\r\n" + //
        "Male\r\n" + //
        "144.4131961\r\n" + //
        "57.58668498\r\n" + //
        "Female\r\n" + //
        "150.0416855\r\n" + //
        "79.01636867\r\n" + //
        "Female\r\n" + //
        "164.4833362\r\n" + //
        "61.48739535\r\n" + //
        "Male\r\n" + //
        "155.0623811\r\n" + //
        "79.42228181\r\n" + //
        "Female\r\n" + //
        "142.5393447\r\n" + //
        "71.64057998\r\n" + //
        "Female\r\n" + //
        "176.7140956\r\n" + //
        "76.14865542\r\n" + //
        "Male\r\n" + //
        "168.4181785\r\n" + //
        "45.96840161\r\n" + //
        "Female\r\n" + //
        "141.8980545\r\n" + //
        "64.07728905\r\n" + //
        "Female\r\n" + //
        "174.3396578\r\n" + //
        "87.15967177\r\n" + //
        "Male"
    );
}
