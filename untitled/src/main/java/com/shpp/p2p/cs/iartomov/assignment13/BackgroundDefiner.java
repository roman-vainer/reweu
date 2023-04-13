package com.shpp.p2p.cs.iartomov.assignment13;

/**
 * Implements image background color search
 * */
public class BackgroundDefiner {
    private static final byte BLACK = 0;
    private static final byte WHITE = 1;

    /**
     * The method implements the definition of the background color
     * as more pixels of a certain brightness in the image
     *
     * @param bitmap An array of image pixel color values in black and white
     * @return Image background value
     */
    public byte defineBackground(byte[][] bitmap) {
        int blackPixels = 0;
        int whitePixels = 0;

        for (int y = 0; y < bitmap[0].length; y++) {
            for (int x = 0; x < bitmap.length; x++) {
                if (bitmap[x][y] == BLACK) {
                    blackPixels++;
                } else {
                    whitePixels++;
                }
            }
        }
        return isBackgroundBlack(blackPixels, whitePixels) ? BLACK : WHITE;
    }

    /***
     * @return True if background is black.
     */
    private boolean isBackgroundBlack(int blackPixels, int whitePixels) {
        return blackPixels > whitePixels;
    }
}