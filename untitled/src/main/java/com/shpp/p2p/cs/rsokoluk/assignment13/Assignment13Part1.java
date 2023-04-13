package com.shpp.p2p.cs.rsokoluk.assignment13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;

/**
 * Assignment13Part1
 * A program that finds silhouettes, ignores too small ones, calculates their number and
 * runs a recursive method to go around the silhouette around the perimeter.
 * You can set program constants through arguments.
 * Argument 0 - tse naming the file. Default - "test.jpg"
 * Argument 1 - the minimum size of the silhouette is visible to the whole little one. Default - 0.01
 * Argument 2 - no trick is allowed for the background. Default - 0.05
 * Argument 3 - save it in the format ".txt",files with designated silhouettes and
 * cleanups in the form of a smaller minimum size. Default - false
 * For example "test.png 0.01 0.05 false"
 */
public class Assignment13Part1 {
    /**
     * The file that opens by default
     */
    private static final String BASE_PNG = "images/test.jpg";
    /**
     * the maximum deviation of the fund from the average value
     * given as a percentage
     */
    private static double POSSIBLE_BACKGROUND_DEFLECTION = 0.2;        //final
    /**
     * The minimum size of the silhouette from the entire picture
     * given as a percentage
     */
    private static double MINIMAL_SILHOUETTE_SIZE = 0.001;               //final
    /**
     * The size of the picture
     * Argument 0 - height
     * Argument 1 - width
     */
    private static final int[] PICTURE_SIZE = new int[2]; // как я должен помнить, что из этого ширина, а что высота
    /**
     * what are the gaze directions from one pixel to neighboring pixels
     */                                                                                        // поля
    private static final int[][] EXISTING_DIRECTIONS_OF_VIEW = new int[][]{{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};

    /**
     * main class that is executed
     *
     * @param args - arguments
     * @return
     */                                                                     //куда передается exeption
    public static int main(String[] args) throws IOException {              //логика в мейн, нейминг
        File file;
        boolean save = false;
        if (args.length == 0) {
            file = new File(BASE_PNG);
        } else {
            file = new File("images/" + args[0]);
 //???...
            /*if (args.length > 1) {
                MINIMAL_SILHOUETTE_SIZE = Double.parseDouble(args[1]);
                if (args.length > 2) {
                    POSSIBLE_BACKGROUND_DEFLECTION = Double.parseDouble(args[2]);
                    if (args.length > 3) {
                        save = Boolean.parseBoolean(args[3]);
                    }
                }
            }*/
//...
        }
        int[][][] points = extractingAnImageFromFile(file);
        int[] phonePoint = getPhonePoint(points);
        boolean[][] pointTF1 = obtainingAnArrayWithNoBackground(points, phonePoint);
        if (save) {                                                 //naming,
            svTxt(pointTF1, "sv1");                           //naming, что делает метод,
        }
        boolean[][] pointTF2 = cleaningSilhouettes(pointTF1);
        if (save) {
            svTxt(pointTF2, "sv2");                            //naming
        }
        return findSilhouettes(pointTF2);

        //System.out.println(findSilhouettes(pointTF2));
    }

    /**
     * A method that finds silhouettes, counts them
     * and also runs a method with recursive silhouette traversal on them               //рекурсия?
     *
     * @param picture - array with silhouette
     * @return - number of silhouettes
     */
    private static int findSilhouettes(boolean[][] picture) {
        int Silhouettes = 0;
        for (int ny1 = 0; ny1 < PICTURE_SIZE[0]; ny1++) {                   //naming
            for (int nx1 = 0; nx1 < PICTURE_SIZE[1]; nx1++) {
                if (picture[ny1][nx1]) {
                    Silhouettes += 1;
                    int[] n1 = new int[]{ny1, nx1};                         //массив с координатами??
                    //bypassing the silhouette                                          // что такое 2
                                                                                        //что делает метод
                    goAroundSilhouette(picture, n1, 2, new int[]{ny1, nx1});  //зачем передавать еще один массив, где используется
                    //silhouette search in width
                    silhouetteSearchInWidth(picture, n1);
                }
            }
        }
        return Silhouettes;
    }

    /**
     * a method that passes the silhouette in width
     *
     * @param picture - picture from boolean[][]
     * @param n1      - starting point
     */
    private static void silhouetteSearchInWidth(boolean[][] picture, int[] n1) {
        ArrayDeque<int[]> states1 = new ArrayDeque<>();                         //naming
        states1.add(n1);
        picture[n1[0]][n1[1]] = false;
        while (true) {
            int[] point = states1.poll();
            if (point == null) {
                break;
            }
            int[][] seaPoint = EXISTING_DIRECTIONS_OF_VIEW.clone();
            removeStraightIntoWall(seaPoint, point);
            for (int[] sP : seaPoint) {
                if (sP != null && picture[point[0] + sP[0]][point[1] + sP[1]]) {
                    states1.add(new int[]{point[0] + sP[0], point[1] + sP[1]});
                    picture[point[0] + sP[0]][point[1] + sP[1]] = false;
                }
            }
        }
    }

    /**
     * the maximum view value available
     */
    private static final int MAXIMUM_VIEW_VALUE = EXISTING_DIRECTIONS_OF_VIEW.length - 1;          //то здесь делает константа

    /**
     * A method that bypasses the silhouette by a recursive method
     *
     * @param picture   - array with silhouette
     * @param position  - position while traversing
     * @param direction - direction of movement
     * @param start     - the starting position to which you need to go to stop the recursion
     */
    private static void goAroundSilhouette(boolean[][] picture, int[] position, int direction, int[] start) {           // рекурсивный поиск в глубину
        int[][] seaPoint = new int[4][1];                   //просто 4 точки
        if (direction > MAXIMUM_VIEW_VALUE) {                       //direction always == 2???
            direction = direction % MAXIMUM_VIEW_VALUE;             //magic numbers
        }                                                           //как узнать где что в массиве
        if (direction == 0) {
            seaPoint[0] = EXISTING_DIRECTIONS_OF_VIEW[6];
            seaPoint[1] = EXISTING_DIRECTIONS_OF_VIEW[7];
            seaPoint[2] = EXISTING_DIRECTIONS_OF_VIEW[0];
            seaPoint[3] = EXISTING_DIRECTIONS_OF_VIEW[1];
        } else if (direction == 1) {
            seaPoint[0] = EXISTING_DIRECTIONS_OF_VIEW[7];
            seaPoint[1] = EXISTING_DIRECTIONS_OF_VIEW[0];
            seaPoint[2] = EXISTING_DIRECTIONS_OF_VIEW[1];
            seaPoint[3] = EXISTING_DIRECTIONS_OF_VIEW[2];
        } else if (direction == MAXIMUM_VIEW_VALUE) {
            seaPoint[0] = EXISTING_DIRECTIONS_OF_VIEW[5];
            seaPoint[1] = EXISTING_DIRECTIONS_OF_VIEW[6];
            seaPoint[2] = EXISTING_DIRECTIONS_OF_VIEW[7];
            seaPoint[3] = EXISTING_DIRECTIONS_OF_VIEW[0];
        } else {
            seaPoint[0] = EXISTING_DIRECTIONS_OF_VIEW[direction - 2];
            seaPoint[1] = EXISTING_DIRECTIONS_OF_VIEW[direction - 1];
            seaPoint[2] = EXISTING_DIRECTIONS_OF_VIEW[direction];
            seaPoint[3] = EXISTING_DIRECTIONS_OF_VIEW[direction + 1];
        }
        removeStraightIntoWall(seaPoint, position);
        if (seaPoint[0] != null && picture[position[0] + seaPoint[0][0]][position[1] + seaPoint[0][1]]) {
            if (position[0] + seaPoint[0][0] != start[0] || position[1] + seaPoint[0][1] != start[1]) {
                goAroundSilhouette(picture, new int[]{position[0] + seaPoint[0][0], position[1] + seaPoint[0][1]},
                        direction < 2 ? MAXIMUM_VIEW_VALUE : direction - 2, start);
            }
        } else if (seaPoint[1] != null && picture[position[0] + seaPoint[1][0]][position[1] + seaPoint[1][1]]) {
            if (position[0] + seaPoint[1][0] != start[0] || position[1] + seaPoint[1][1] != start[1]) {
                goAroundSilhouette(picture, new int[]{position[0] + seaPoint[1][0], position[1] + seaPoint[1][1]},
                        direction == 0 ? MAXIMUM_VIEW_VALUE : direction - 1, start);
            }
        } else if (seaPoint[2] != null && picture[position[0] + seaPoint[2][0]][position[1] + seaPoint[2][1]]) {
            if (position[0] + seaPoint[2][0] != start[0] || position[1] + seaPoint[2][1] != start[1]) {
                goAroundSilhouette(picture, new int[]{position[0] + seaPoint[2][0], position[1] + seaPoint[2][1]},
                        direction, start);
            }
        } else if (seaPoint[3] != null && picture[position[0] + seaPoint[3][0]][position[1] + seaPoint[3][1]]) {
            if (position[0] + seaPoint[3][0] != start[0] || position[1] + seaPoint[3][1] != start[1]) {
                goAroundSilhouette(picture, new int[]{position[0] + seaPoint[3][0], position[1] + seaPoint[3][1]},
                        direction == MAXIMUM_VIEW_VALUE ? 0 : direction + 1, start);
            }
        } else {
            goAroundSilhouette(picture, new int[]{position[0], position[1]},
                    direction > MAXIMUM_VIEW_VALUE - 1 ? 0 : direction + 2, start);
        }
    }

    /**
     * A method that removes all silhouettes of a size smaller than a given one
     *
     * @param notCleaningSilhouettes - an array in which small silhouettes have not been removed
     * @return - an array in which small silhouettes have been removed
     */
    private static boolean[][] cleaningSilhouettes(boolean[][] notCleaningSilhouettes) {                    //декомпозиция
        int needPixelInSilhouettes = (int) (PICTURE_SIZE[0] * PICTURE_SIZE[1] * MINIMAL_SILHOUETTE_SIZE);
        boolean[][] deletedSilhouettes = new boolean[PICTURE_SIZE[0]][PICTURE_SIZE[1]];
        for (int ny = 0; ny < PICTURE_SIZE[0]; ny++) {
            deletedSilhouettes[ny] = notCleaningSilhouettes[ny].clone();                    //зачем
        }
        for (int ny1 = 0; ny1 < PICTURE_SIZE[0]; ny1++) {                                   //naming
            for (int nx1 = 0; nx1 < PICTURE_SIZE[1]; nx1++) {
                int pixelInSilhouettes = 0;
                if (deletedSilhouettes[ny1][nx1]) {
                    ArrayDeque<int[]> states1 = new ArrayDeque<>();
                    ArrayDeque<int[]> states2 = new ArrayDeque<>();
                    states1.add(new int[]{ny1, nx1});
                    states2.add(new int[]{ny1, nx1});
                    deletedSilhouettes[ny1][nx1] = false;
                    while (true) {
                        int[] point = states1.poll();
                        if (point == null) {
                            break;
                        }
                        pixelInSilhouettes++;
                        int[][] seaPoint = EXISTING_DIRECTIONS_OF_VIEW.clone();
                        removeStraightIntoWall(seaPoint, point);
                        for (int[] sP : seaPoint) {
                            if (sP != null && deletedSilhouettes[point[0] + sP[0]][point[1] + sP[1]]) {
                                states1.add(new int[]{point[0] + sP[0], point[1] + sP[1]});
                                states2.add(new int[]{point[0] + sP[0], point[1] + sP[1]});
                                deletedSilhouettes[point[0] + sP[0]][point[1] + sP[1]] = false;
                            }
                        }
                    }
                    if (pixelInSilhouettes < needPixelInSilhouettes) {
                        while (true) {
                            int[] point = states2.pollFirst();
                            if (point == null) {
                                break;
                            }
                            notCleaningSilhouettes[point[0]][point[1]] = false;
                        }
                    }
                }
            }
        }
        return notCleaningSilhouettes;
    }

    /**
     * A method that finds the average pixel color of a 4-pixel-thick picture frame
     *
     * @param points - an array of image pixels
     * @return - averaged pixel
     */
    private static int[] getPhonePoint(int[][][] points) {              //method 66 strings
        ArrayList<int[]> phonePoints = new ArrayList<>();
        int[] averagePoint = new int[4];                                //magic number
        for (int nx = 0; nx < 4; nx++) {                                //naming nx?
            for (int ny = 0; ny < PICTURE_SIZE[0]; ny++) {              //decomposition
                phonePoints.add(points[ny][nx]);                        //повторяющийся код
                addAveragePoint(averagePoint, points[ny][nx]);
            }
        }
        for (int nx = PICTURE_SIZE[1] - 4; nx < PICTURE_SIZE[1]; nx++) {
            for (int ny = 0; ny < PICTURE_SIZE[0]; ny++) {
                phonePoints.add(points[ny][nx]);
                addAveragePoint(averagePoint, points[ny][nx]);
            }
        }
        for (int ny = 0; ny < 4; ny++) {
            for (int nx = 0; nx < PICTURE_SIZE[1]; nx++) {
                phonePoints.add(points[ny][nx]);
                addAveragePoint(averagePoint, points[ny][nx]);
            }
        }
        for (int ny = PICTURE_SIZE[0] - 4; ny < PICTURE_SIZE[0]; ny++) {
            for (int nx = 0; nx < PICTURE_SIZE[1]; nx++) {
                phonePoints.add(points[ny][nx]);
                addAveragePoint(averagePoint, points[ny][nx]);
            }
        }
        averagingAveragePoint(phonePoints, averagePoint);                   //naming
        boolean ust = true;                                                 //naming
        for (int[] point : phonePoints) {
            ust = similarityPixels(averagePoint, point);
            if (!ust) {
                break;
            }
        }
        if (ust) {
            return averagePoint;
        } else {
            HashMap<int[], Integer> states = new HashMap<>();
            for (int[] point : phonePoints) {
                Set<int[]> keys = states.keySet();
                boolean ust1 = true;
                for (int[] key : keys) {
                    ust = similarityPixels(key, point);
                    if (ust) {
                        states.replace(key, states.get(key) + 1);
                        ust1 = false;
                        break;
                    }
                }
                if (ust1) {
                    states.put(point, 1);
                }
            }
            Set<int[]> keys = states.keySet();
            int[] max = null;
            for (int[] key : keys) {
                if (max == null) {
                    max = key;
                }
                if (states.get(key) > states.get(max)) {
                    max = key;
                }
            }
            return max;
        }
    }

    /**
     * divides the value average point by the number of points in the frame
     * @param phonePoints - list with background points
     * @param averagePoint - average point
     */
    private static void averagingAveragePoint(ArrayList<int[]> phonePoints, int[] averagePoint) {
        averagePoint[0] /= phonePoints.size();                  //изменение первоначального смысла переменной
        averagePoint[1] /= phonePoints.size();                  //создать новую переменную, в которую записать результат
        averagePoint[2] /= phonePoints.size();
        averagePoint[3] /= phonePoints.size();
    }

    /**
     * adds to the averaged point the value of the selected point
     * @param averagePoint - average point
     * @param point - point which is added
     */
    private static void addAveragePoint(int[] averagePoint,int[] point) {
        averagePoint[0] += point[0];
        averagePoint[1] += point[1];
        averagePoint[2] += point[2];
        averagePoint[3] += point[3];
    }

    /**
     * A method that determines the similarity of two pixels according to POSSIBLE_BACKGROUND_DEFLECTION
     *
     * @param point1 - the first pixel to be compared
     * @param point2 - the second pixel to be compared
     * @return - true if the pixels are similar enough otherwise false
     */
    private static boolean similarityPixels(int[] point1, int[] point2) {               //метод - глагол
        int deflection = (int) (256 * POSSIBLE_BACKGROUND_DEFLECTION);              //magic number
        return Math.abs(point1[0] - point2[0]) <= deflection && Math.abs(point1[1] - point2[1]) <= deflection &&        //в переменную или в метод. Не ясно что возвращается
                Math.abs(point1[2] - point2[2]) <= deflection && Math.abs(point1[3] - point2[3]) <= deflection;
    }

    /**
     * The method that replaces the directions of gaze with null
     * if they are directed outside the picture
     *
     * @param seaPoint - directions of sight
     * @param Position - position in the picture
     */
    private static void removeStraightIntoWall(int[][] seaPoint, int[] Position) {
        if (Position[0] == 0) {
            for (int n = 0; n < seaPoint.length; n++) {
                if (seaPoint[n] != null && seaPoint[n][0] == -1) {
                    seaPoint[n] = null;
                }
            }
        }
        if (Position[1] == 0) {
            for (int n = 0; n < seaPoint.length; n++) {
                if (seaPoint[n] != null && seaPoint[n][1] == -1) {
                    seaPoint[n] = null;
                }
            }
        }
        if (Position[0] == PICTURE_SIZE[0] - 1) {
            for (int n = 0; n < seaPoint.length; n++) {
                if (seaPoint[n] != null && seaPoint[n][0] == 1) {
                    seaPoint[n] = null;
                }
            }
        }
        if (Position[1] == PICTURE_SIZE[1] - 1) {
            for (int n = 0; n < seaPoint.length; n++) {
                if (seaPoint[n] != null && seaPoint[n][1] == 1) {
                    seaPoint[n] = null;
                }
            }
        }
    }

    /**
     * replace in 2D array, array with colors to false if it matches background
     * otherwise true
     * @param points - 2D array, array with colors
     * @param phonePoint - array with background value (3 colors and brightness)
     * @return - an array where pixels similar to the background are false and not similar true
     */
    private static boolean[][] obtainingAnArrayWithNoBackground(int[][][] points, int[] phonePoint) {           //naming
        boolean[][] pointTF1 = new boolean[PICTURE_SIZE[0]][PICTURE_SIZE[1]];
        for (int ny = 0; ny < PICTURE_SIZE[0]; ny++) {
            for (int nx = 0; nx < PICTURE_SIZE[1]; nx++) {
                if (!similarityPixels(phonePoint, points[ny][nx])) {
                    pointTF1[ny][nx] = true;
                }
            }
        }
        return pointTF1;
    }

    /**
     * A method that extracts an image from a file
     *
     * @param file - the file from which it is extracted
     * @return - two-dimensional array of point arrays (3 colors and brightness)
     * @throws IOException - file reading errors are possible
     */
    private static int[][][] extractingAnImageFromFile(File file) throws IOException {  //naming method: what is returns?
        BufferedImage image = ImageIO.read(file);
        PICTURE_SIZE[0] = image.getHeight();
        PICTURE_SIZE[1] = image.getWidth();
        Raster rast = image.getRaster();
        int[][][] points = new int[PICTURE_SIZE[0]][PICTURE_SIZE[1]][4];        //3-х мерный массив...
        for (int ny = 0; ny < PICTURE_SIZE[0]; ny++) {                          // magic numbers
            for (int nx = 0; nx < PICTURE_SIZE[1]; nx++) {                      //naming - ny, nx, points
                points[ny][nx] = rast.getPixel(nx, ny, new int[4]);
            }
        }
        return points;
    }

    /**
     * A method that saves a Boolean array to a ".txt" file
     *
     * @param array - the array that is stored
     * @param name  - the name of the file to be saved
     */
    private static void svTxt(boolean[][] array, String name) {
        try (FileWriter writer = new FileWriter(name + ".txt", false)) {
            for (int ny = 0; ny < PICTURE_SIZE[0]; ny++) {
                for (int nx = 0; nx < PICTURE_SIZE[1]; nx++) {
                    if (array[ny][nx]) {
                        writer.append("1");
                    } else {
                        writer.append("0");
                    }
                }
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}