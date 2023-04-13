package com.shpp.p2p.cs.mkruchok.assignment13;

import java.util.*;

import acm.graphics.GImage;
import acm.util.ErrorException;

/**
 * This program calculates amount of silhouettes in .jpg and .png pictures using BFS.
 */
public class Assignment13Part {

    // default picture
    static final String TEST_PICTURE = "images/test.jpg";
    // average garbage percentage (could be changed)
    static final double AVERAGE_GARBAGE_PERCENTAGE = 0.03;
    // maximum size of the picture
    static final int MAX_SIZE = 1000000;
    // half of color-value in RGB values
    static final int HALF_OF_RGB = 127;                 //?
    // maximum value of RGB-color
    static final int RGB_MAX = 255;
    // comparator for similar light gray color values (could be changed)
    static final int NOTICEABLE_DIFFERENCE_BETWEEN_LIGHT_GREYS = 27;
    // comparator for similar dark gray color values (could be changed)
    static final int NOTICEABLE_DIFFERENCE_BETWEEN_DARK_GREYS = 5;
    // all pixels in the picture
    static int[][] pixels = new int[MAX_SIZE][];        //?
    // value for alpha-channel for png pictures
    static int[][] alphaChannel;
    // the smallest silhouette size
    static int minimumOfPixelsPerObject = 0;            //зачем 0?
    // amount of silhouette pixels
    static int pixelAmount = 0;
    // total number of silhouette pixels
    static int totalNumberOfSilhouettePixels = 0;


    public static void main(String[] args) {

        GImage image;                                           //try/catch не отрабатывает
        String name = "";
        try {
            name = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please enter picture name!");
            System.exit(0);
        }
        try {
            image = new GImage(name);
        } catch (ErrorException errorException) {
            System.out.println("Can`t find file " + name);
            name = TEST_PICTURE;
            image = new GImage(name);
        }

        System.out.println("Picture: " + name);
        pixels = image.getPixelArray();
        calculateImageGarbageAmount(image);

        int silhouettes;
        if (pictureHasOpacity()) {
            silhouettes = workWithPNG();
        } else {
            silhouettes = workWithJPGOrNullPNGOpacity();
        }
        System.out.println("Silhouettes = " + silhouettes + "\r\n");
        //return silhouettes;

    }

    /**
     * This method works with .jpg or with .png files, that have only 255 in every pixel by alpha-channel.
     * Algorithm: recreate photo in gray tones, define background and silhouettes, get rid of garbage pixels (artefacts),
     * and find amount of silhouettes.
     */
    private static int workWithJPGOrNullPNGOpacity() {
        doBlackAndWhitePhoto();
        int[][] objectsAndBackground = defineBackground();
        return countSilhouettes(objectsAndBackground);
    }

    /**
     * This method works with .png files, that have different alpha channel values in pixels.
     * Silhouettes in here are detected by finding difference in alpha.
     */
    private static int workWithPNG() {
        int backgroundAlpha = GImage.getAlpha(pixels[0][0]);                    //значение фона только по одному прикселю
        int[][] objectsAndBackground = new int[alphaChannel.length][alphaChannel[0].length];

        for (int i = 0; i < alphaChannel.length; i++) {
            for (int j = 0; j < alphaChannel[0].length; j++) {

                if (alphaChannel[i][j] != backgroundAlpha) {                    //лучше использовать ==
                    objectsAndBackground[i][j] = 1;
                } else {
                    objectsAndBackground[i][j] = 0;
                }
            }
        }
        return countSilhouettes(objectsAndBackground);
    }

    /**
     * Checks every pixel in the picture to define if it has pixels with different alpha channel values.
     * Also writes values to "alphaChannel" for further silhouettes searching.
     *
     * @return if picture has different alpha channel values
     */
    private static boolean pictureHasOpacity() {
        boolean hasTransparentPixels = false;
        alphaChannel = new int[pixels.length][pixels[0].length];

        for (int i = 0; i < pixels.length; i++) {                   //зачем цикл?
            for (int j = 0; j < pixels[0].length; j++) {
                alphaChannel[i][j] = GImage.getAlpha(pixels[i][j]);
                if (alphaChannel[i][j] != RGB_MAX) {
                    hasTransparentPixels = true;
                }
            }
        }
        return hasTransparentPixels;
    }

    /**
     * Calculates amount of pixels, that should be not included in silhouettes counting.
     *
     * @param image incoming picture
     */
    private static void calculateImageGarbageAmount(GImage image) {
        double wight = image.getWidth();
        double height = image.getHeight();
        double square = wight * height;
        minimumOfPixelsPerObject = (int) (square * AVERAGE_GARBAGE_PERCENTAGE) / 100;
    }

    /**
     * Calculates amount of silhouettes and minuses objects that has less than minimum pixels.
     *
     * @param backgroundAndObjects matrix that indicates which pixel is a background, and which is object.
     * @return amount of silhouettes.
     */
    private static int countSilhouettes(int[][] backgroundAndObjects) {
        int row = backgroundAndObjects.length;
        int column = backgroundAndObjects[0].length;             // лучше не декларировать в одну строчку
        int silhouettes = 0;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (backgroundAndObjects[i][j] == 1) {
                    pixelAmount = 0;
                    silhouettes++;

                    ObjectPixel pixel = new ObjectPixel(pixels[i][j], i, j);
                    bfs(pixel, backgroundAndObjects);

                    if (pixelAmount <= minimumOfPixelsPerObject) {
                        silhouettes--;
                    }
                }
            }
        }
        return silhouettes;
    }

    /**
     * Breadth first search.
     * Non-recursive function that checks which pixels consist in silhouette.
     *
     * @param pixel                object where silhouette starts
     * @param backgroundAndObjects matrix that indicates which pixel is a background, and which is silhouette
     */
    public static void bfs(ObjectPixel pixel, int[][] backgroundAndObjects) {

        Queue<ObjectPixel> queue = new ArrayDeque<>();
        // mark starting pixel as visited
        pixel.isVisited();
        // and send ti to the queue
        queue.add(pixel);

        while (!queue.isEmpty()) {
            // get first value in queue
            pixel = queue.poll();

            ArrayList<Integer> moves = getStepsToFindNeighbourPixels(pixel);

            for (int i = 0; i < moves.size(); i += 2) {                             //это регулировка шагов?
                if (pixelsIsOutOfBounds(moves, backgroundAndObjects, i)) {
                    break;
                }

                ObjectPixel neighbourPixel = new ObjectPixel(backgroundAndObjects[moves.get(i)][moves.get(i + 1)],
                        moves.get(i), moves.get(i + 1));

                if (backgroundAndObjects[neighbourPixel.x][neighbourPixel.y] == 1 && !neighbourPixel.visited) {
                    // mark as visited and send to queue
                    neighbourPixel.isVisited();
                    queue.add(neighbourPixel);
                    backgroundAndObjects[moves.get(i)][moves.get(i + 1)] = 0;
                    pixelAmount++;
                }
            }
        }
    }

    /**
     * Checks if pixel isn`t near edges of the picture
     *
     * @param moves                pixel values to be checked further
     * @param backgroundAndObjects matrix that indicates which pixel is a background, and which is silhouette
     * @param i                    side where next pixel should be checked
     * @return if pixel isn`t out of bounds
     */
    private static boolean pixelsIsOutOfBounds(ArrayList<Integer> moves, int[][] backgroundAndObjects, int i) {
        return (moves.get(i) < 0 || moves.get(i + 1) < 0 || moves.get(i) > backgroundAndObjects.length - 1
                || moves.get(i + 1) > backgroundAndObjects[0].length - 1);
    }

    /**
     * Creates ArrayList with sides to move to check pixels (up, down, to the right, to the left)
     *
     * @param pixel pixel to be checked around
     * @return coordinates where neighbour pixels are located
     */
    private static ArrayList<Integer> getStepsToFindNeighbourPixels(ObjectPixel pixel) {
        ArrayList<Integer> moves = new ArrayList<>();

        moves.add(pixel.x);
        moves.add(pixel.y + 1);

        moves.add(pixel.x + 1);
        moves.add(pixel.y);

        moves.add(pixel.x);
        moves.add(pixel.y - 1);

        moves.add(pixel.x - 1);
        moves.add(pixel.y);
        return moves;
    }


    /**
     * Calculates which pixel responds for back or objects.                 //фон И объекты?
     *
     * @return matrix that indicates which pixel is a background, and which is object.
     */
    private static int[][] defineBackground() {

        int[][] backAndObjects = new int[pixels.length][pixels[0].length];
        HashMap<Integer, Integer> backgroundColors = new HashMap<>();
        // get colors from 2-pixel frame of the image
        getFramePixelsColors(backgroundColors);
        // max() - find the biggest value in hash, which means the biggest amount of specific color, that`s background
        int maxValueInMap = (Collections.max(backgroundColors.values())), background = 0, deviation;
        // finds background color
        for (Map.Entry<Integer, Integer> entry : backgroundColors.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
                background = entry.getKey();
                break;
            }
        }
        // checks deviation before looking for silhouettes to not count similar colors to background as objects
        deviation = countDeviationOfColor(background);
        // initialize in backAndObjects 1 - for silhouette and 0 - for background
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {

                if (pixels[i][j] != background &&
                        (Math.abs(GImage.getRed(background) - GImage.getRed(pixels[i][j])) >= deviation)) {
                    backAndObjects[i][j] = 1;
                    totalNumberOfSilhouettePixels++;
                } else {
                    backAndObjects[i][j] = 0;
                }
            }
        }
        return backAndObjects;
    }

    /**
     * Depending on grade of gray color, chose appropriate deviation of colors
     *
     * @param background background color
     * @return deviation
     */
    private static int countDeviationOfColor(int background) {
        if (GImage.getRed(background) == 0) {
            return HALF_OF_RGB;
        } else if (GImage.getRed(background) > HALF_OF_RGB) {
            return NOTICEABLE_DIFFERENCE_BETWEEN_LIGHT_GREYS;
        } else {
            return NOTICEABLE_DIFFERENCE_BETWEEN_DARK_GREYS;
        }
    }

    /**
     * Runs through frame of the picture taking first and second rows/columns from edges
     *
     * @param backgroundColors HashMap that contains pixel-color and amount of it`s color
     */
    private static void getFramePixelsColors(HashMap<Integer, Integer> backgroundColors) {              //рассказать
        ArrayList<Integer> frame = new ArrayList<>();
        // 1 pixels by frame


        frame.add(0);
        frame.add(pixels[0].length);
        frame.add(pixels.length - 1);
        frame.add(pixels[0].length);
        frame.add(pixels.length - 1);
        frame.add(0);
        frame.add(pixels.length - 1);
        frame.add(pixels[0].length);
        // 2 pixels by frame
        frame.add(1);
        frame.add(pixels[0].length);
        frame.add(pixels.length - 2);
        frame.add(pixels[0].length);
        frame.add(pixels.length - 2);
        frame.add(0);
        frame.add(pixels.length - 2);
        frame.add(pixels[0].length);

        for (int k = 0; k < frame.size(); k += 2) {             //2?

            int i = frame.get(k);
            for (int j = 0; j < frame.get(k + 1); j++) {
                // add amount of same color
                if (backgroundColors.containsKey(pixels[i][j])) {
                    backgroundColors.replace(pixels[i][j], backgroundColors.get(pixels[i][j]), (backgroundColors.get(pixels[i][j]) + 1));       // границы
                } else {
                    backgroundColors.put(pixels[i][j], 0);
                }
            }
        }
    }

    /**
     * Create new photo in gray tones.
     */
    private static void doBlackAndWhitePhoto() {
        int grey;
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                grey = (GImage.getRed(pixels[i][j]) + GImage.getGreen(pixels[i][j]) + GImage.getBlue(pixels[i][j])) / 3;        //почему так?
                pixels[i][j] = GImage.createRGBPixel(grey, grey, grey);
            }
        }
    }
}