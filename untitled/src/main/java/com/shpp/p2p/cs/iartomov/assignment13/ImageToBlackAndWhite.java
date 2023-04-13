package com.shpp.p2p.cs.iartomov.assignment13;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Implements image conversion to black and white.
 * */
public class ImageToBlackAndWhite {
    private static final byte BLACK = 0;
    private static final byte WHITE = 1;
    private BufferedImage image;

    public byte[][] run(BufferedImage image) {
        this.image = image;
        int[] threshold = setThreshold();
        return binarizeImage(threshold);
    }

    /**
     * The method implements the conversion of an image to black and white
     *
     * @param threshold A threshold value between the background and silhouette colors,
     *                  taking into account transparency, if the image file supports an
     *                  alpha channel
     * @return An array of image pixel color values in black and white
     */

    private byte[][] binarizeImage(int[] threshold) {
        byte[][] bitmap = new byte[image.getWidth()][image.getHeight()];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getColorModel().hasAlpha()) {
                    bitmap[x][y] = isPixelBlackAlpha(threshold, x, y) ? BLACK : WHITE;
                } else {
                    bitmap[x][y] = isPixelBlack(threshold, x, y) ? BLACK : WHITE;
                }
            }
        }
        return bitmap;
    }

    /**
     * Specifies the color of a pixel based on the threshold between background
     * and silhouette in an image with an alpha channel.
     *
     * @param threshold Threshold value for background and silhouette colors.
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     *
     * @return True if color is black.
     * */
    private boolean isPixelBlackAlpha(int[] threshold, int x, int y) {
        int luminance = getPixelLuminance(image.getRGB(x, y));
        int transparency = new Color(image.getRGB(x, y), true).getAlpha();
        return (transparency > threshold[1]) || (luminance < threshold[0]);
    }

    /**
     * Specifies the color of a pixel based on the threshold between background
     * and silhouette in an image without an alpha channel.
     *
     * @param threshold Threshold value for background and silhouette colors.
     * @param x The x coordinate of the pixel.
     * @param y The y coordinate of the pixel.
     *
     * @return True if color is black.
     * */
    private boolean isPixelBlack(int[] threshold, int x, int y) {
        int luminance = getPixelLuminance(image.getRGB(x, y));
        return (luminance < threshold[0]);
    }

    /**
     * The method implements the search for a threshold value
     * between the background and silhouette colors, taking into
     * account transparency, if the image file supports an alpha channel
     *
     * @return Array of color and transparency thresholds
     */
    private int[] setThreshold() {
        int sumRGB = 0;
        int sumAlpha = 0;
        int[] threshold = new int[2];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                sumRGB += getPixelLuminance(image.getRGB(x, y));
                if (image.getColorModel().hasAlpha()) {
                    sumAlpha += new Color(image.getRGB(x, y), true).getAlpha();
                }
            }
        }
        threshold[0] = sumRGB / (image.getWidth() * image.getHeight());
        threshold[1] = sumAlpha / (image.getWidth() * image.getHeight());
        return threshold;
    }

    /**
     * The method returns the luminance value of a pixel
     *
     * @param pixelColor Pixel color code
     */
    private int getPixelLuminance(int pixelColor) {
        Color pixel = new Color(pixelColor);
        return (int) (0.2125 * pixel.getRed() + 0.7154 * pixel.getGreen() + 0.0721 * pixel.getBlue());
    }
}