package com.shpp.p2p.cs.iartomov.assignment13;

import java.util.ArrayList;

/**
 * Implements the determination of the size of the garbage on the image relative to the size
 * of the silhouettes and the search for sticky silhouettes by comparing the sizes of silhouettes
 * with the arithmetic mean size of all silhouettes.
 */
public class Silhouettes implements Config {

    /**
     * The arraylist of sizes of all founding silhouettes.
     * */
    private ArrayList<Integer> silhouettes;

    /**
     * @return The number of silhouettes in the image without the garbage silhouettes
     * and taking into account sticky silhouettes.
     */
    public int getSilhouettesOnImage(ArrayList<Integer> silhouettes) {
        this.silhouettes = silhouettes;
        removeGarbageSilhouettes(getSilhouetteSizeMax());
        return silhouettes.size() + getStickSilhouettes();
    }

    /**
     * Finds the size of the largest silhouette in the image
     *
     * @return The size of the largest silhouette in the image.
     */
    private int getSilhouetteSizeMax() {
        int max = 0;
        /* Determines the maximum value in the array of silhouette sizes */
        for (Integer silhouetteSize : silhouettes) {
            if (silhouetteSize > max) {
                max = silhouetteSize;
            }
        }
        return max;
    }

    /**
     * The method implements the determination of the size of the garbage
     * on the image relative to the size of the silhouettes.
     *
     * @param max The size of the largest silhouette in the image.
     */
    private void removeGarbageSilhouettes(int max) {
        /* The amount of garbage in the image is determined */
        for (int i = 0; i < silhouettes.size(); i++) {
            if (silhouettes.get(i) < max * GARBAGE_RATIO) {
                silhouettes.remove(i);
                i--;
            }
        }
    }

    /**
     * Implements the search for sticky silhouettes by comparing the sizes of silhouettes
     * with the arithmetic mean size of all silhouettes.
     *
     * @return The number of the sticky silhouettes.
     */
    private int getStickSilhouettes() {
        int stickSilhouettesNumber = 0;
        double averageSilhouettesSize = 0;
        /* Average silhouettes size is calculated */
        for (Integer silhouetteSize : silhouettes) {
            averageSilhouettesSize += (double) silhouetteSize / silhouettes.size();
        }
        /* Stickly silhouettes are found */
        for (Integer silhouetteSize : silhouettes) {
            if (isSilhouettesStick(silhouetteSize, averageSilhouettesSize)) {
                stickSilhouettesNumber++;
            }
        }
        return stickSilhouettesNumber;
    }

    /**
     * Checks stiсkly silhouettes.
     *
     * @param silhouetteSize                The silhouette size in pixels.
     * @param averageSilhouettesSize The arithmetic mean size of all silhouettes.
     */
    private boolean isSilhouettesStick(int silhouetteSize, double averageSilhouettesSize) {
        return silhouetteSize > averageSilhouettesSize * STICK_COEF;
    }
}