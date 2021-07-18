package com.dolphln.npcfindminigame.utils;

import java.util.Random;

public class RandomUtils {

    public static int generateRandomNumber(int min, int max) {
        if (min > max) {
            int rMin = min;
            int rMax = max;
            max = rMin;
            min = rMax;
        }

        Random r = new Random();
        int upperBound = max - min + 1;
        return min + r.nextInt(upperBound);
    }

}
