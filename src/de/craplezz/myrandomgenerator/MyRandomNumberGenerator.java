package de.craplezz.myrandomgenerator;

import java.util.Arrays;

class MyRandomNumberGenerator implements Generator {

    private static final long m = 281474976710656L; // 2 to the power of 48
    private static final long a = 25214903917L;
    private static final long c = 11L;

    private long seed;

    public MyRandomNumberGenerator(long seed) {
        this.seed = seed;
    }

    public long next() {
        return (seed = Math.floorMod(seed * a + c, m));
    }

    public int rollDice() {
        return (int) (next() * 6.0 / m + 1);
    }

    public int rollDice(int times) {
        int total = 0;
        for (int i = 0; i < times; i++) {
            total += rollDice();
        }
        return total;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            MyRandomNumberGenerator generator = new MyRandomNumberGenerator(System.currentTimeMillis());
            int[] hits = new int[6];
            for (int i = 0; i < 1000; i++) {
                hits[generator.rollDice() - 1]++;
            }
            System.out.println(Arrays.toString(hits));
            return;
        }

        // Expectation

        int n = 1000;
        double p = 1.0 / 6.0;
        double expectedValue = n * p;
        double expectedDeviation = Math.sqrt(expectedValue * (1 - p));
        double environment = 1.97; // 95% deviation
        double lowX = expectedValue - environment * expectedDeviation;
        double highX = expectedValue + environment * expectedDeviation;

        System.out.println("Erwartungswert: " + expectedValue);
        System.out.println("Erwartete Abweichung: " + expectedDeviation);
        System.out.println("Umgebung: [" + lowX + ";" + highX + "]");

        int missed = 0;

        // Seed

        long seed = 1L;

        for (int testId = 0; testId < 100; testId++) {
            Generator generator = new MyRandomNumberGenerator(seed);

            int[] results = new int[n]; // Generating 1000 dice rolls

            // Generate results

            System.out.println("\n Generating! (ID: " + testId + ") \n");

            for (int i = 0; i < results.length; i++) {
                results[i] = generator.rollDice();
            }

            // Checking results

            int[] hits = new int[6];

            for (int i = 0; i < results.length; i++) {
                hits[results[i] - 1]++;
            }

            System.out.println("Hits: " + Arrays.toString(hits));

            for (int i = 0; i < hits.length; i++) {
                int hit = hits[i];
                boolean test = lowX < hit && hit < highX;

                System.out.println("Test " + (i + 1) + ": " + lowX + " < " + hit + " < " + highX + " = " + test);

                if (!test) {
                    missed++;
                }
            }

            seed++;
        }

        System.out.println();
        System.out.println("Fehlschläge: " + missed + " / 600");
    }

}