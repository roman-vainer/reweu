package com.shpp.p2p.cs.iartomov.assignment13;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implements counting the number of silhouettes in an image
 * using the breadth-first search method
 */
public class SilhouetteFounderBFS implements Config {

    /**
     * Sizes of found silhouettes are saved
     */
    private ArrayList<Integer> silhouettes;

    /**
     * The number of pixels in the silhouette
     */
    private int silhouetteSize;

    /**
     * Finds the number of silhouettes in an image
     * considering garbage silhouettes
     *
     * @param bitmap     An array of image pixel color values in black and white.
     * @param background Image background value.
     * @return The number of silhouettes in the image.
     */
    public int run(byte[][] bitmap, byte background) {
        silhouettes = new ArrayList<>();
        findSilhouettes(bitmap, background);
        return new Silhouettes().getSilhouettesOnImage(silhouettes);
    }

    /**
     * The method implements counting the number of silhouettes in an image
     * using breadth-first search
     *
     * @param bitmap     An array of image pixel color values in black and white
     * @param background Image background value
     */
    private void findSilhouettes(byte[][] bitmap, byte background) {
        /* An array to track visited pixels is created */
        boolean[][] visited = new boolean[bitmap.length][bitmap[0].length];

        for (int y = 0; y < bitmap[0].length; y++) {
            for (int x = 0; x < bitmap.length; x++) {
                if (isSilhouette(x, y, visited, bitmap, background)) {
                    /* Found a new silhouette */
                    silhouetteSize = 0;
                    /* breadth-first search is started */
                    bfs(x, y, visited, bitmap, background);
                    /* The silhouette size is added to the list of silhouettes */
                    silhouettes.add(silhouetteSize);
                }
            }
        }
    }

    /**
     * The method implements the breadth-first search algorithm
     *
     * @param x          Pixel x coordinate
     * @param y          Pixel y coordinate
     * @param visited    Array of visited image pixels
     * @param bitmap     An array of image pixel color values in black and white
     * @param background Image background value
     */
    private void bfs(int x, int y, boolean[][] visited, byte[][] bitmap, byte background) {

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] currentPixelCoordinates = queue.poll();
            x = currentPixelCoordinates[0];
            y = currentPixelCoordinates[1];

            if (isVisitedOrBackground(x, y, visited, bitmap, background)) {
                continue;
            }
            /* Pixel is marked as visited */
            visited[x][y] = true;
            /* The size of the found silhouette increases */
            silhouetteSize++;

            for (byte[] direction : NEIGHBOR_PIXELS) {
                int neighborX = x + direction[0];
                int neighborY = y + direction[1];

                if (isPixelInsideImage(neighborX, neighborY, bitmap)) {
                    queue.offer(new int[]{neighborX, neighborY});
                }
            }
        }
    }

    /**
     * Checks if the pixel has already been visited or is the background.
     *
     * @param x          The x coordinate of the pixel.
     * @param y          The y coordinate of the pixel.
     * @param visited    Array of visited image pixels.
     * @param bitmap     An array of image pixel color values in black and white.
     * @param background Image background value.
     * @return True if the pixel has already been visited or is the background.
     */
    private boolean isVisitedOrBackground(int x, int y, boolean[][] visited, byte[][] bitmap, byte background) {
        return (visited[x][y] || bitmap[x][y] == background);
    }

    /**
     * Checks if a pixel is inside an image.
     *
     * @param x      The x coordinate of the pixel.
     * @param y      The y coordinate of the pixel.
     * @param bitmap An array of image pixel color values in black and white.
     * @return True if a pixel is inside an image.
     */
    private boolean isPixelInsideImage(int x, int y, byte[][] bitmap) {
        return (x >= 0 && x < bitmap.length && y >= 0 && y < bitmap[0].length);
    }

    /**
     * Checks if the pixel has not been visited and is silhouette.
     *
     * @param x          The x coordinate of the pixel.
     * @param y          The y coordinate of the pixel.
     * @param visited    Array of visited image pixels.
     * @param bitmap     An array of image pixel color values in black and white.
     * @param background Image background  value
     * @return true if the pixel has not been visited and is silhouette.
     */
    private boolean isSilhouette(int x, int y, boolean[][] visited, byte[][] bitmap, byte background) {
        return (!visited[x][y] && bitmap[x][y] != background);
    }
}