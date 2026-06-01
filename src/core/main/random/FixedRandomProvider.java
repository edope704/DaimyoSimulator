package core.random;

public final class FixedRandomProvider implements RandomProvider {
    private final double fixedDouble;
    private final int fixedInt;

    public FixedRandomProvider(double fixedDouble, int fixedInt) {
        this.fixedDouble = fixedDouble;
        this.fixedInt = fixedInt;
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        }
        return Math.floorMod(fixedInt, bound);
    }

    @Override
    public double nextDouble() {
        return fixedDouble;
    }
}
