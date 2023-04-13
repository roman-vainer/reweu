//package com.shpp.p2p.cs.ikosenko.assignment13;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ProcessingImage extends Constants {
//    /**
//     * The width of the image.
//     */
//    private final int widthImage;
//    /**
//     * The height of the image.
//     */
//    private final int heightImage;
//
//    public ProcessingImage(BufferedImage image) {
//        heightImage = image.getHeight();
//        widthImage = image.getWidth();
//    }
//
//    /**
//     * The method returns height of image in pixels.
//     *
//     * @return number of pixels for height of image
//     */
//    public int getHeightImage() {
//        return heightImage;
//    }
//
//    /**
//     * The method returns width of image in pixels.
//     *
//     * @return number of pixels for width of image
//     */
//    public int getWidthImage() {
//        return widthImage;
//    }
//
//    /**
//     * The method determines the background color of the entered picture.
//     */
//    public Color defineColorBackground(Color[][] pixelsColor) {
//
//        Color colorBackground = null;
//        HashMap<Color, Integer> colorsCount = new HashMap<>();
//        /* If color in the edge pixels are the same. */
//        if (pixelsColor[0][0].equals(pixelsColor[0][widthImage - 1]) &&
//                pixelsColor[heightImage - 1][0].equals(pixelsColor[heightImage - 1][widthImage - 1])) {
//
//            ArrayList<Color> edgePixelColor = getEdgePixelColor(pixelsColor);
//            for (Color color : edgePixelColor) {
//                getNumberOfColors(colorsCount, color);
//            }
//        }
//        /* Count which color occurs most often on the image. */
//        else {
//            for (int i = 0; i < heightImage; i++) {
//                for (int j = 0; j < widthImage; j++) {
//                    getNumberOfColors(colorsCount, pixelsColor[i][j]);
//                }
//            }
//        }
//        int maxCounter = 0;
//        for (Integer i : colorsCount.values()) {
//            maxCounter = Math.max(i, maxCounter);
//        }
//        for (Map.Entry<Color, Integer> entry : colorsCount.entrySet()) {
//            if (entry.getValue().equals(maxCounter)) {
//                colorBackground = entry.getKey();
//            }
//        }
//        return colorBackground;
//    }
//
//    /**
//     * The method counts how many times each color occurs in the image.
//     *
//     * @param colorsCount a collection of colors and their quantity.
//     * @param color       a color of pixel.
//     */
//    private static void getNumberOfColors(HashMap<Color, Integer> colorsCount, Color color) {
//        if (!colorsCount.containsKey(color)) {
//            colorsCount.put(color, 1);
//        } else {
//            colorsCount.put(color, colorsCount.get(color) + 1);
//        }
//    }
//
//    /**
//     * The method creates an array of edge pixel colors to define the background.
//     *
//     * @param pixelsColor an array of pixel colors.
//     * @return a collection of edge pixel colors.
//     */
//    private ArrayList<Color> getEdgePixelColor(Color[][] pixelsColor) {
//
//        ArrayList<Color> edgePixelColor = new ArrayList<>();
//        for (int i = 0; i < heightImage; i++) {
//            edgePixelColor.add(pixelsColor[i][0]);
//            edgePixelColor.add(pixelsColor[i][widthImage - 1]);
//        }
//        for (int j = 1; j < widthImage - 1; j++) {
//            edgePixelColor.add(pixelsColor[0][j]);
//            edgePixelColor.add(pixelsColor[heightImage - 1][j]);
//        }
//        return edgePixelColor;
//    }
//
//    /**
//     * The method returns the input image into a grayscale image.
//     *
//     * @param image the entered image.
//     * @return the picture in gray color.
//     */
//
//    public Color[][] createGrayImage(BufferedImage image) {
////        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        Color[][] col = new Color[heightImage][widthImage];
//        for (int i = 0; i < heightImage; i++) {
//            for (int j = 0; j < widthImage; j++) {
//
//                Color tempColor = new Color(image.getRGB(j, i), true);
//                int gray = (tempColor.getRed() + tempColor.getGreen() + tempColor.getBlue()) / 3;
//                Color grayColor = new Color(gray, gray, gray, tempColor.getAlpha());
//
//                if (grayColor.getAlpha() >= 0 && grayColor.getAlpha() <= RANGE_SIMILAR_COLOR) {
//                    col[i][j] = new Color(255, 255, 255, 255);
//                } else {
//                    col[i][j] = grayColor;
//                }
//            }
//        }
//        return col;
//    }
//
//
//    /**
//     * The method creates an array of the colors from the picture.
//     *
//     * @param image the entered image.
//     */
//    public Color[][] createColorArray(BufferedImage image) {            //naming color? image?
//        Color[][] pixelsColor;
//        pixelsColor = new Color[heightImage][widthImage];
//        for (int i = 0; i < heightImage; i++) {
//            for (int j = 0; j < widthImage; j++) {
//                pixelsColor[i][j] = new Color(image.getRGB(j, i), true);
//                if (pixelsColor[i][j].getAlpha() >= 0 && pixelsColor[i][j].getAlpha() <= RANGE_SIMILAR_COLOR) {
//                    pixelsColor[i][j] = new Color(255, 255, 255, 255);
//                }
//            }
//        }
//        return pixelsColor;
//    }
//
//    /**
//     * The method creates an array of zeros and ones from a color array,        //color array?
//     * where the zero represents the background and the one represents the silhouette,
//     * using the specified background color.
//     */
//
//    public int[][] createBinaryArray(Color[][] pixelsColor) {       // вынести логику SRP
//        Color colorBackground = defineColorBackground(pixelsColor);
//        int redBackground = colorBackground.getRed();
//        /* Define a color lighter and darker than the background color that is similar to the background color. */
//        int colorLighter, colorDarker;                      // в одну строчку
//        colorLighter = redBackground + RANGE_SIMILAR_COLOR;
//        if (colorLighter > 255)
//            colorLighter = 255;
//        colorDarker = redBackground - RANGE_SIMILAR_COLOR;
//        if (colorDarker < 0)
//            colorDarker = 0;
//
//        int[][] arrayPixelsBinary = new int[heightImage][widthImage];
//        for (int i = 0; i < heightImage; i++) {
//            for (int j = 0; j < widthImage; j++) {
//                int redPixel = pixelsColor[i][j].getRed();
//                if (pixelsColor[i][j].equals(colorBackground) || (redPixel <= colorLighter && redPixel >= colorDarker)) {
//                    arrayPixelsBinary[i][j] = BACKGROUND;
//                } else {
//                    arrayPixelsBinary[i][j] = SILHOUETTE;
//                }
//            }
//        }
//        return arrayPixelsBinary;
//    }
//}
