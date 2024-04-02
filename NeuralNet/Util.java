public class Util {
    public static double sigmoid (double input) {
        return 1 / (1 + Math.exp(-input));
    }
}
