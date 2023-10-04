package com.wikimedia.payment.utils;

import java.util.Random;

public class RandomUtils {

    public static String generateRandomNumber() {
        Random random = new Random();
        double probability = random.nextDouble(); // Generate a random number between 0 and 1

        if (probability < 0.9) {
            return "SUCCESS"; // Return 1 with a 90% probability
        } else {
            return "FAIL"; // Return 0 with a 10% probability
        }
    }

}
