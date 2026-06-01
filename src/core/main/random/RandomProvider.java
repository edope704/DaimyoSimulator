package core.random;

public interface RandomProvider {
    int nextInt(int bound);

    double nextDouble();

    default boolean chance(double probability) {
        if (probability <= 0) {
            return false;
        }
        if (probability >= 1) {
            return true;
        }
        return nextDouble() < probability;
    }
}
