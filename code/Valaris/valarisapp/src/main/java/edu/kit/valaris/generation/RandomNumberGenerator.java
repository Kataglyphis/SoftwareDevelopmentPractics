package edu.kit.valaris.generation;

import java.util.Random;

public class RandomNumberGenerator {

    private Random random;

    /**
     * Creates a new {@link RandomNumberGenerator}
     *
     * @param seed used to calculate random numbers
     */
    public RandomNumberGenerator(int seed) {
        random = new Random(seed);
    }

    /**
     * Returns a new Seed.
     *
     * @return new Seed
     */
    public int getNewSeed() {
        return  ("" + random.nextInt()).hashCode();
    }

    /**
     * Returns a random float between 0.0 and 1.0
     *
     * @return float between 0.0 and 1.0
     */
    public float random() {
        return random.nextFloat();
    }
}
