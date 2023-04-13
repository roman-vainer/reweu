/*
package com.shpp.p2p.cs.ikosenko.assignment13;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

public class FindingSilhouettes extends Constants {
    */
/**
     * The number of pixels in the silhouette.
     *//*

    private int  countPixels = 0;
    */
/**
     * An array containing zeros and ones.
     *//*

    private int[][] arrayPixelsBinary;       //static?

    */
/**
     * The method creates an array of pixels, iterates over them,
     * and calls the breadth-first search method if the pixel is not background and has not been visited.
     *
     * @param image an entered image.
     * @return a number of the silhouette in the picture.
     *//*

    public int findSilhouettes(BufferedImage image) {                       //многократный вызов логики одного класса
        ProcessingImage processingImage = new ProcessingImage(image);       //constructor
        int heightImage = processingImage.getHeightImage();
        int widthImage = processingImage.getWidthImage();

        Color[][] grayImage = processingImage.createGrayImage(image);
        arrayPixelsBinary = processingImage.createBinaryArray(grayImage); //лишний метод, static
        ArrayList<Integer> arraySilhouettes = new ArrayList<>();

        for (int i = 0; i < heightImage; i++) {                         //декомпо
            for (int j = 0; j < widthImage; j++) {
                if (arrayPixelsBinary[i][j] != BACKGROUND && arrayPixelsBinary[i][j] != VISITED) {
                    searchInWidth(i, j, heightImage, widthImage);
                    arraySilhouettes.add(countPixels);
                    countPixels = 0;
                }
            }
        }
        return getCountSilhouettes(arraySilhouettes);
    }

    */
/**
     * The method goes through all silhouette pixels,
     * marks them as visited and counts the number of silhouette pixels.
     *
     * @param i the position of the pixel by the height.
     * @param j the position of the pixel by the weight.
     *//*


    public void searchInWidth(int i, int j, int heightImage, int widthImage) { //naming
        ArrayDeque<int[]> pixelsQueue = new ArrayDeque<>();
        countPixels++;
        pixelsQueue.add(new int[]{i, j});
        arrayPixelsBinary[i][j] = VISITED;

        while (!pixelsQueue.isEmpty()) {
            int[] coordinatePixel = pixelsQueue.remove();
            i = coordinatePixel[0];
            j = coordinatePixel[1];

            if (j != widthImage - 1 && arrayPixelsBinary[i][j + 1] != BACKGROUND && arrayPixelsBinary[i][j + 1] != VISITED) {
                pixelsQueue.add(new int[]{i, j + 1});
                arrayPixelsBinary[i][j + 1] = VISITED;
                countPixels++;
            }
            if (j != 0 && arrayPixelsBinary[i][j - 1] != BACKGROUND && arrayPixelsBinary[i][j - 1] != VISITED) {
                pixelsQueue.add(new int[]{i, j - 1});
                arrayPixelsBinary[i][j - 1] = VISITED;
                countPixels++;
            }
            if (i != 0 && arrayPixelsBinary[i - 1][j] != BACKGROUND && arrayPixelsBinary[i - 1][j] != VISITED) {
                pixelsQueue.add(new int[]{i - 1, j});
                arrayPixelsBinary[i - 1][j] = VISITED;
                countPixels++;
            }
            if (i != heightImage - 1 && arrayPixelsBinary[i + 1][j] != BACKGROUND && arrayPixelsBinary[i + 1][j] != VISITED) {
                pixelsQueue.add(new int[]{i + 1, j});
                arrayPixelsBinary[i + 1][j] = VISITED;
                countPixels++;
            }
        }
    }

    */
/**
     * The method returns the number of large silhouettes without garbage.
     *
     * @param arraySilhouettes the array  of all silhouettes
     * @return a number of silhouettes without garbage
     *//*

    private int getCountSilhouettes(ArrayList<Integer> arraySilhouettes) {
        int countSilhouettes = 0;
        int maxSilhouette = Collections.max(arraySilhouettes);
        double garbage = maxSilhouette * GARBAGE_COEFFICIENT;
        ArrayList<Integer> finalSilhouettes = new ArrayList<>();
        for (Integer silhouette : arraySilhouettes) {
            if (silhouette > garbage) {
                countSilhouettes++;
                finalSilhouettes.add(silhouette);
            }
        }
        Collections.sort(finalSilhouettes);
        System.out.print(finalSilhouettes);
        return countSilhouettes;
    }

}
*/
