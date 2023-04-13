package com.shpp.p2p.cs.ikosenko.assignment13;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class is designed to find silhouettes in an image, count their number
 * and separate the silhouettes if necessary.
 */
public class FindingSilhouettes {
    /**
     * A variable that represents the background in the array of pixels.
     */
    private static final int BACKGROUND = 0;
    /**
     * A variable that represents the visited pixels in the array of pixels.
     */
    private static final int VISITED = 2;
    /**
     * Garbage coefficient.
     */
    private static final double GARBAGE_COEFFICIENT = 0.4;
    /**
     * Erosion coefficient, the depth to which neighboring pixels are checked.
     * If silhouettes are combined in the picture, then it is necessary to increase the erosion coefficient.
     */
    private static final int EROSION_COEFFICIENT = 9;
    /**
     * An array of values by which the value of the current pixel is increased by the X coordinate.
     */
    private static final int[] NEXT_X = {0, 0, -1, 1, 1, -1, -1, 1};
    /**
     * An array of values by which the value of the current pixel is increased by the Y coordinate.
     */
    private static final int[] NEXT_Y = {1, -1, 0, 0, -1, -1, 1, 1};
    /**
     * The total number of pixels in the silhouette.
     */
    private int countPixels;
    /**
     * An array containing zeros and ones, where zeros is background and ones are silhouettes.
     */
    private int[][] arrayPixelsBinary;
    /**
     * A new instance of ProcessingImage Class.
     */
    ProcessingImage processingImage;

    /**
     * The method creates an array of pixels, iterates over them,
     * and calls the breadth-first search method if the pixel is not background and has not been visited.
     *
     * @param image an entered image.
     * @return a number of the silhouette in the picture.
     */
    public int findSilhouettes(BufferedImage image) {
        processingImage = new ProcessingImage(image);

        /* Creating an array of zeros and ones from a grayscale image. */
        arrayPixelsBinary = processingImage.createBinaryArray(image, BACKGROUND);

        /* If silhouettes are combined in the picture, then it is necessary to increase the erosion coefficient and call this method.*/
        if (EROSION_COEFFICIENT > 0)
            separationConnectedSilhouettes();

        /* Creating an array where the objects are the number of pixels in each silhouette. */
        ArrayList<Integer> arraySilhouettes = new ArrayList<>();
        for (int i = 0; i < processingImage.heightImage; i++) {
            for (int j = 0; j < processingImage.widthImage; j++) {
                if (arrayPixelsBinary[i][j] != BACKGROUND && arrayPixelsBinary[i][j] != VISITED) {
                    /* Calling the breadth-first-search method. */
                    searchInWidth(i, j);
                    arraySilhouettes.add(countPixels);
                    countPixels = 0;
                }
            }
        }
        /* Calculation and disposal of garbage and calculation of the final number of silhouettes. */
        return getCountSilhouettes(arraySilhouettes);
    }

    /**
     * The method goes through all silhouette pixels,
     * marks them as visited and counts the number of silhouette pixels.
     *
     * @param currentY the position of the pixel by the height.
     * @param currentX the position of the pixel by the weight.
     */

    public void searchInWidth(int currentY, int currentX) {
        ArrayDeque<int[]> pixelsQueue = new ArrayDeque<>();
        countPixels++;
        pixelsQueue.add(new int[]{currentY, currentX});
        arrayPixelsBinary[currentY][currentX] = VISITED;
        while (!pixelsQueue.isEmpty()) {
            int[] coordinatePixel = pixelsQueue.remove();
            currentY = coordinatePixel[0];
            currentX = coordinatePixel[1];
            if (isNotBorder(currentY, currentX)) {
                for (int k = 0; k < NEXT_X.length; k++) {
                    if (arrayPixelsBinary[currentY + NEXT_Y[k]][currentX + NEXT_X[k]] != BACKGROUND
                            && arrayPixelsBinary[currentY + NEXT_Y[k]][currentX + NEXT_X[k]] != VISITED) {

                        pixelsQueue.add(new int[]{currentY + NEXT_Y[k], currentX + NEXT_X[k]});
                        arrayPixelsBinary[currentY + NEXT_Y[k]][currentX + NEXT_X[k]] = VISITED;
                        countPixels++;
                    }
                }
            }
        }
    }

    /**
     * The method checks neighboring pixels for erosion coefficient up, down,
     * to the right and to the left of the current position.
     * If the condition is true, then all these pixels belong to the background, the silhouettes are separated.
     */

    private void separationConnectedSilhouettes() {
        for (int i = 0; i < processingImage.heightImage; i++) {
            for (int j = 0; j < processingImage.widthImage; j++) {
                if (arrayPixelsBinary[i][j] == 1) {
                    if (i > EROSION_COEFFICIENT && i < processingImage.heightImage - EROSION_COEFFICIENT &&
                            j > EROSION_COEFFICIENT && j < processingImage.widthImage - EROSION_COEFFICIENT) {
                        /* Checking neighboring pixels for erosion coefficient up and down of the current position. */
                        if (arrayPixelsBinary[i - EROSION_COEFFICIENT][j] == 0 && arrayPixelsBinary[i + EROSION_COEFFICIENT][j] == 0) {
                            for (int k = -EROSION_COEFFICIENT; k <= EROSION_COEFFICIENT; k++) {
                                arrayPixelsBinary[i + k][j] = 0;
                            }
                        }
                        /* Checking neighboring pixels for erosion coefficient to the right and left of the current position.*/
                        if (arrayPixelsBinary[i][j - EROSION_COEFFICIENT] == 0 && arrayPixelsBinary[i][j + EROSION_COEFFICIENT] == 0) {
                            for (int k = -EROSION_COEFFICIENT; k <= EROSION_COEFFICIENT; k++) {
                                arrayPixelsBinary[i][j + k] = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * The method returns true if the current pixel position is not on the boundary.
     *
     * @param i the position of the pixel by the height.
     * @param j the position of the pixel by the weight.
     * @return true if the current pixel position is not on the boundary.
     */

    private boolean isNotBorder(int i, int j) {
        return j < processingImage.widthImage - 1 && j > 0 && i > 0 && i < processingImage.heightImage - 1;
    }

    /**
     * The method returns the number of large silhouettes without little object.
     * Calculation and disposal of garbage and calculation of the final number of silhouettes.
     *
     * @param arraySilhouettes the array  of all silhouettes
     * @return a number of silhouettes without garbage
     */
    public int getCountSilhouettes(ArrayList<Integer> arraySilhouettes) {
        int countSilhouettes = 0;
        int maxSilhouette = 0;
        /* Checking if the array is empty. */
        try {
            maxSilhouette = Collections.max(arraySilhouettes);
        } catch (Exception e) {
            System.out.println("Array is empty! " + e);
        }
        /* Calculation of the value of garbage. */
        double valueGarbage = maxSilhouette * GARBAGE_COEFFICIENT;

        for (Integer silhouette : arraySilhouettes) {
            if (silhouette > valueGarbage) {
                countSilhouettes++;
            }
        }
        return countSilhouettes;
    }
}
