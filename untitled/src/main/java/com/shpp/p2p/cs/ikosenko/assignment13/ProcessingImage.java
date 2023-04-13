package com.shpp.p2p.cs.ikosenko.assignment13;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is designed to process the input image: determine the background color,
 * convert the image to grayscale, create an array of colors, and create a binary array from it.
 */

public class ProcessingImage {
    /**
     * A variable that represents the silhouette in the array of pixels.
     */
    private static final int SILHOUETTE = 1;
    /**
     * Garbage coefficient.
     */
    private static final int MAX_COLOR_VALUE = 255;
    /**
     * A variable that shows the range in which similar colors are found.
     */
    private static final int RANGE_SIMILAR_COLOR = 60;
    /**
     * The width of the image.
     */
    public final int widthImage;
    /**
     * The height of the image.
     */
    public final int heightImage;
    private int colorLighter;
    private int colorDarker;

    public ProcessingImage(BufferedImage image) {
        heightImage = image.getHeight();
        widthImage = image.getWidth();
    }

    /**
     * The method determines the background color of the entered picture.
     *
     * @param pixelsColor an array of pixel colors
     * @return a background color.
     */
    public Color defineColorBackground(Color[][] pixelsColor) {
        /* Create a new collection where the keys are colors, and the values are their number in the picture. */
        HashMap<Color, Integer> colorsCount = new HashMap<>();
        /* If color in the edge pixels are the same. */
        if (pixelsColor[0][0].equals(pixelsColor[0][widthImage - 1]) &&
                pixelsColor[heightImage - 1][0].equals(pixelsColor[heightImage - 1][widthImage - 1])) {
            /* Create new array for edge pixel. */
            ArrayList<Color> edgePixelColor = getEdgePixelColor(pixelsColor);
            for (Color color : edgePixelColor) {
                fillMapOfColorsAndQuantity(colorsCount, color);
            }
        }
        /* Else count which color occurs most often on the image. */
        else {
            for (int i = 0; i < heightImage; i++) {
                for (int j = 0; j < widthImage; j++) {
                    fillMapOfColorsAndQuantity(colorsCount, pixelsColor[i][j]);
                }
            }
        }
        return searchMaxCommonColor(colorsCount);
    }

    /**
     * The method looks for the most common color to determine the background color.
     *
     * @param colorsCount a collection where the keys are colors, and the values are their number in the picture.
     * @return the background color.
     */

    private static Color searchMaxCommonColor(HashMap<Color, Integer> colorsCount) {
        Color colorBackground = null;
        int maxCounter = 0;
        for (Integer i : colorsCount.values()) {
            maxCounter = Math.max(i, maxCounter);
        }
        for (Map.Entry<Color, Integer> entry : colorsCount.entrySet()) {
            if (entry.getValue().equals(maxCounter)) {
                colorBackground = entry.getKey();
            }
        }
        return colorBackground;
    }

    /**
     * The method creates a collection of all colors from the image and their quantity.
     *
     * @param colorsCount a collection of colors and their quantity.
     * @param color       a color of pixel.
     */
    private static void fillMapOfColorsAndQuantity(HashMap<Color, Integer> colorsCount, Color color) {
        if (!colorsCount.containsKey(color)) {
            colorsCount.put(color, 1);
        } else {
            colorsCount.put(color, colorsCount.get(color) + 1);
        }
    }

    /**
     * The method creates an array of edge pixel colors to define the background.
     *
     * @param pixelsColor an array of pixel colors.
     * @return a collection of edge pixel colors.
     */
    private ArrayList<Color> getEdgePixelColor(Color[][] pixelsColor) {
        /* Create new array for edge pixel: from below, from above, from the right, from the left. */
        ArrayList<Color> edgePixelColor = new ArrayList<>();
        for (int i = 0; i < heightImage; i++) {
            edgePixelColor.add(pixelsColor[i][0]);
            edgePixelColor.add(pixelsColor[i][widthImage - 1]);
        }
        for (int j = 1; j < widthImage - 1; j++) {
            edgePixelColor.add(pixelsColor[0][j]);
            edgePixelColor.add(pixelsColor[heightImage - 1][j]);
        }
        return edgePixelColor;
    }

    /**
     * The method creates an array of the colors from the picture.
     *
     * @param image the entered image.
     * @return the picture in gray color.
     */

    public Color[][] createGrayColorArray(BufferedImage image) {
        Color[][] pixelsColor = new Color[heightImage][widthImage];
        for (int i = 0; i < heightImage; i++) {
            for (int j = 0; j < widthImage; j++) {
                Color tempColor = new Color(image.getRGB(j, i), true);
                /* Create an integer value from each pixel for gray color. */
                int gray = (tempColor.getRed() + tempColor.getGreen() + tempColor.getBlue()) / 3;
                Color grayColor = new Color(gray, gray, gray, tempColor.getAlpha());
                /* Checking if background is transparent */
                if (grayColor.getAlpha() >= 0 && grayColor.getAlpha() <= RANGE_SIMILAR_COLOR) {
                    pixelsColor[i][j] = new Color(MAX_COLOR_VALUE, MAX_COLOR_VALUE, MAX_COLOR_VALUE, MAX_COLOR_VALUE);
                } else {
                    pixelsColor[i][j] = grayColor;
                }
            }
        }
        return pixelsColor;
    }

    /**
     * The method creates an array of zeros and ones from a color array,
     * where the zero represents the background and the one represents the silhouette,
     * using the specified background color.
     */
    public int[][] createBinaryArray(BufferedImage image, int background) {
        Color[][] pixelColors = createGrayColorArray(image);
        Color colorBackground = defineColorBackground(pixelColors);
        defineSimilarColor(colorBackground);
        int[][] arrayPixelsBinary = new int[heightImage][widthImage];
        for (int i = 0; i < heightImage; i++) {
            for (int j = 0; j < widthImage; j++) {
                int redPixel = pixelColors[i][j].getRed();
                if (pixelColors[i][j].equals(colorBackground) || (redPixel <= colorLighter && redPixel >= colorDarker)) {
                    arrayPixelsBinary[i][j] = background;
                } else {
                    arrayPixelsBinary[i][j] = SILHOUETTE;
                }
            }
        }
        return arrayPixelsBinary;
    }

    /**
     * The method determines which color is similar to the background color.
     *
     * @param colorBackground a background color.
     */

    private void defineSimilarColor(Color colorBackground) {
        int redBackground = colorBackground.getRed();
        /* Define a color lighter and darker than the background color that is similar to the background color. */
        colorLighter = redBackground + RANGE_SIMILAR_COLOR;
        if (colorLighter > MAX_COLOR_VALUE)
            colorLighter = MAX_COLOR_VALUE;
        colorDarker = redBackground - RANGE_SIMILAR_COLOR;
        if (colorDarker < 0)
            colorDarker = 0;
    }
}
