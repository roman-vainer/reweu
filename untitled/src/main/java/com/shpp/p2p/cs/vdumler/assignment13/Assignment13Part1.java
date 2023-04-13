package com.shpp.p2p.cs.vdumler.assignment13;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class Assignment13Part1 finds silhouettes on a picture. Even if they slightly merged.
 */
public class Assignment13Part1 {
    private static final String DEFAULT_FILE_LOCATION = "test.jpg";
    // amount of color's ARGB grades to be considered as the base color
    private static final int MARGIN = 30;
    // percentage of image's pixels to be considered as rubbish
    private static final double RUBBISH_SCALE = 0.4;
    // defines how many layers of silhouettes to delete
    private static final int EROSION_COEFFICIENT = 4;

    /**
     * Starts whole program.
     * @param args file location is expected to be provided here
     */
    public static int main(String[] args) {

        BufferedImage img = readImage(args);

        Node[][] nodeMap = fillNodeMap(img);

        Color backgroundColor = findBackgroundColor(nodeMap);

        markSilhouettesNodes(nodeMap, backgroundColor);

        submitToErosion(nodeMap);

        int amountOfSilhouettes = findSilhouettes(nodeMap, backgroundColor);

        System.out.println(amountOfSilhouettes);
        return amountOfSilhouettes;
    }



    /**
     * Reads image using buffered image
     *
     * @param args a link to the image in a first argument
     * @return buffered image
     */
    private static BufferedImage readImage(String[] args) {
        // default value
        if (args.length == 0) args = new String[]{DEFAULT_FILE_LOCATION};
        try {
            return ImageIO.read(new File("images/" + args[0]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds all colors in picture and transfer pixels into nodes list
     *
     * @param img given image
     * @return an 2D-array of nodes
     */
    private static Node[][] fillNodeMap(BufferedImage img) {
        Node[][] nodeMap = new Node[img.getWidth()][img.getHeight()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                nodeMap[x][y] = new Node(x, y, new Color(img.getRGB(x, y), true));
            }
        }
        return nodeMap;
    }

    /**
     * Finds background color of an image. The background is considered to be the color that occurs most along the
     * contour of the picture.
     *
     * @param nodeMap an 2D-array that represents image's pixels.
     * @return color of background
     */
    private static Color findBackgroundColor(Node[][] nodeMap) {
        HashMap<Color, Integer> frameColors = getFrameColors(nodeMap);
        return findMostCommonColor(frameColors);
    }

    /**
     * Goes through all pixels along the contour and counts their colors.
     *
     * @param nodeMap an 2D-array of nodes
     * @return a hash map with colors and the number of their appearances in the picture
     */
    private static HashMap<Color, Integer> getFrameColors(Node[][] nodeMap) {
        HashMap<Color, Integer> frameColors = new HashMap<>();
        for (int x = 0; x < nodeMap.length; x++) {
            for (int y = 0; y < nodeMap[0].length; y++) {
                if (x == 0 || x == nodeMap.length - 1 || y == 0 || y == nodeMap[0].length - 1) {
                    addPixelColorToList(nodeMap, x, y, frameColors);
                }
            }
        }
        return frameColors;
    }

    /**
     * Gets a pixel's color. If it is new one, it will be added to list, otherwise it will increase counter for the
     * particular color.
     *
     * @param nodeMap     an 2D-array of nodes
     * @param x           horizontal coordinate
     * @param y           vertical coordinate
     * @param frameColors a hash map with all known colors and how many times do they occur.
     */
    private static void addPixelColorToList(Node[][] nodeMap, int x, int y,
                                            HashMap<Color, Integer> frameColors) {
        Color colorOfNode = nodeMap[x][y].getColor();
        if (frameColors.containsKey(colorOfNode)) {
            frameColors.put(colorOfNode, frameColors.get(colorOfNode) + 1);
        } else {
            frameColors.put(colorOfNode, 1);
        }
    }

    /**
     * Finds color which most often appears in the picture.
     *
     * @param colorsSet a hash map with colors and the number of their appearances in the picture
     * @return color which most often appears in the picture
     */
    private static Color findMostCommonColor(HashMap<Color, Integer> colorsSet) {
        Color mostCommonColor = new Color(0, true);
        int highestValue = 0;
        for (Color color : colorsSet.keySet()) {
            if (colorsSet.get(color) > highestValue) {
                highestValue = colorsSet.get(color);
                mostCommonColor = color;
            }
        }
        return mostCommonColor;
    }

    /**
     * Marks all silhouettes' nodes in node map
     * @param nodeMap an 2D-array of nodes
     * @param backgroundColor a color of the picture background
     */
    private static void markSilhouettesNodes(Node[][] nodeMap, Color backgroundColor) {
        for (int x = 0; x < nodeMap.length; x++) {
            for (int y = 0; y < nodeMap[0].length; y++) {
                Node nodeInQuestion = nodeMap[x][y];
                if (!nodeInQuestion.isChecked()) {
                    nodeInQuestion.markAsChecked();
                    // checks node's color
                    if (!isApproximatelySameColor(backgroundColor, nodeInQuestion.getColor())) {
                        markAllNodesInSilhouette(nodeInQuestion, backgroundColor, nodeMap);
                    }
                }
            }
        }
    }

    /**
     * Marks all nodes in a silhouette
     * @param startingNode a first node in a silhouette found
     * @param backgroundColor a color of the picture background
     * @param nodeMap an 2D-array of nodes
     */
    private static void markAllNodesInSilhouette(Node startingNode, Color backgroundColor, Node[][] nodeMap) {
        // a queue which keeps nodes to check
        Queue<Node> nodeQueue = new LinkedList<>();
        nodeQueue.add(startingNode);

        while (!nodeQueue.isEmpty()) {
            Node nodeInQuestion = nodeQueue.poll();
            nodeInQuestion.markAsSilhouetteNode();
            // goes through all surrounding node
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    // limits set not to cross the boundaries of the image and not to chose same node
                    if (!nodeIsNearMapsEdge(nodeInQuestion, x, y, nodeMap) && !choseItself(nodeInQuestion, x, y)) {
                        Node linkedNode = nodeMap[nodeInQuestion.getX() + x][nodeInQuestion.getY() + y];
                        if (!linkedNode.isChecked()) {
                            linkedNode.markAsChecked();
                            if (!isApproximatelySameColor(backgroundColor, linkedNode.getColor()) &&
                                    isApproximatelySameColor(nodeInQuestion.getColor(), linkedNode.getColor())) {
                                nodeQueue.add(linkedNode);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes layers of pixels on the edges of silhouettes
     * @param nodeMap an 2D-array of nodes
     */
    private static void submitToErosion(Node[][] nodeMap) {
        defineNodesForErosion(nodeMap);
        commitErosion(nodeMap);
    }

    /**
     * Defines which nodes in silhouette to "delete"
     * @param nodeMap an 2D-array of nodes
     */
    private static void defineNodesForErosion(Node[][] nodeMap) {
        // makes iterations of erosion
        for (int erosionIteration = 1; erosionIteration <= EROSION_COEFFICIENT; erosionIteration++) {       //итерация через i
            // goes through all nodes in the map
            for (int x = 0; x < nodeMap.length; x++) {
                for (int y = 0; y < nodeMap[0].length; y++) {
                    Node nodeInQuestion = nodeMap[x][y];
                    if (nodeInQuestion.isSilhouetteNode()) {
                        markNodesToDelete(nodeInQuestion, nodeMap, erosionIteration);
                    }
                }
            }
        }
    }

    /**
     * Defines to "delete" or not a particular node
     * @param nodeInQuestion a particular node
     * @param nodeMap an 2D-array of nodes
     * @param erosionIteration time erosion happens
     */
    private static void markNodesToDelete(Node nodeInQuestion, Node[][] nodeMap, int erosionIteration) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                // limits set not to cross the boundaries of the image
                if (!choseItself(nodeInQuestion, x, y)) {
                    if (nodeIsNearMapsEdge(nodeInQuestion, x, y, nodeMap)) {
                        nodeInQuestion.setDeletionMarker(erosionIteration);
                    } else {
                        Node linkedNode = nodeMap[nodeInQuestion.getX() + x][nodeInQuestion.getY() + y];
                        if (!linkedNode.isSilhouetteNode() ||
                                (linkedNode.getDeletionMarker() > nodeInQuestion.getDeletionMarker() &&
                                        linkedNode.getDeletionMarker() != erosionIteration)) {
                            nodeInQuestion.setDeletionMarker(erosionIteration);
                        }
                    }
                }
            }
        }
    }

    /**
     * Uncheck all nodes that were not defined for erosion. Nodes which were defined to erosion will stay checked, so
     * they will not be considered as nodes of any silhouette.
     * @param nodeMap an 2D-array of nodes
     */
    private static void commitErosion(Node[][] nodeMap) {
        for (int x = 0; x < nodeMap.length; x++) {
            for (int y = 0; y < nodeMap[0].length; y++) {
                Node nodeInQuestion = nodeMap[x][y];
                if (nodeInQuestion.isChecked() && nodeInQuestion.getDeletionMarker() == 0) {
                    nodeInQuestion.markAsUnChecked();
                }
            }
        }
    }

    /**
     * Finds silhouettes on the image
     *
     * @param nodeMap         an 2D-array of nodes
     * @param backgroundColor a color of the picture background
     * @return amount of silhouettes
     */
    private static int findSilhouettes(Node[][] nodeMap, Color backgroundColor) {
        int amountOfSilhouettes = 0;
        int rubbishLimit = (int) ((nodeMap.length * nodeMap[0].length) / 100 * RUBBISH_SCALE);
        for (int x = 0; x < nodeMap.length; x++) {
            for (int y = 0; y < nodeMap[0].length; y++) {
                Node nodeInQuestion = nodeMap[x][y];
                amountOfSilhouettes += findAppropriateSilhouette(nodeInQuestion, backgroundColor, nodeMap, rubbishLimit);
            }
        }
        return amountOfSilhouettes;
    }

    /**
     * Finds a silhouette which meets requirements.
     *
     * @param nodeInQuestion  node to be checked whether it is a start of a silhouette or not.
     * @param backgroundColor A color of the picture background
     * @param nodeMap         an 2D-array of nodes
     * @param rubbishLimit    a number that represents a limit above which amount of nodes is considered as a silhouette
     *                        and not a "rubbish".
     * @return 1 - it is a silhouette, 0 - it is not a beginning, or it is rubbish.
     */
    private static int findAppropriateSilhouette(Node nodeInQuestion, Color backgroundColor,
                                                 Node[][] nodeMap, int rubbishLimit) {
        if (!nodeInQuestion.isChecked()) {
            nodeInQuestion.markAsChecked();
            // checks node's color
            if (!isApproximatelySameColor(backgroundColor, nodeInQuestion.getColor())) {
                int nodesInSilhouette = countNodesInSilhouette(nodeInQuestion, backgroundColor, nodeMap);
                if (nodesInSilhouette > rubbishLimit) return 1;
            }
        }
        return 0;
    }

    /**
     * Counts nodes in a silhouette using breadth-first search
     *
     * @param startingNode    a node to start from
     * @param backgroundColor A color of the picture background
     * @param nodeMap         an 2D-array of nodes
     * @return amount of nodes in a silhouette
     */
    private static int countNodesInSilhouette(Node startingNode, Color backgroundColor, Node[][] nodeMap) {
        // startingNode counts
        int nodesInSilhouette = 1;
        // a queue which keeps nodes to check
        Queue<Node> nodeQueue = new LinkedList<>();
        nodeQueue.add(startingNode);

        while (!nodeQueue.isEmpty()) {
            Node nodeInQuestion = nodeQueue.poll();
            // goes through all surrounding node

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    // limits set not to cross the boundaries of the image
                    if (!nodeIsNearMapsEdge(nodeInQuestion, x, y, nodeMap) && !choseItself(nodeInQuestion, x, y)) {
                        Node linkedNode = nodeMap[nodeInQuestion.getX() + x][nodeInQuestion.getY() + y];
                        if (!linkedNode.isChecked()) {
                            linkedNode.markAsChecked();
                            if (!isApproximatelySameColor(backgroundColor, linkedNode.getColor()) &&
                                    isApproximatelySameColor(nodeInQuestion.getColor(), linkedNode.getColor())) {
                                nodeQueue.add(linkedNode);
                                nodesInSilhouette++;
                            }
                        }
                    }
                }
            }
        }
        return nodesInSilhouette;
    }

    /**
     * Checks whether color is partially same
     *
     * @param baseColor       first color
     * @param colorInQuestion second color
     * @return boolean
     */
    private static boolean isApproximatelySameColor(Color baseColor, Color colorInQuestion) {
        int[] baseColorARGB = new int[]{baseColor.getAlpha(), baseColor.getRed(),
                baseColor.getGreen(), baseColor.getBlue()};
        int[] colorInQuestionARGB = new int[]{colorInQuestion.getAlpha(), colorInQuestion.getRed(),
                colorInQuestion.getGreen(), colorInQuestion.getBlue()};

        boolean isApproximatelySameColor = ( (baseColorARGB[0] - MARGIN < colorInQuestionARGB[0] &&
                colorInQuestionARGB[0] < baseColorARGB[0] + MARGIN) &&
                (baseColorARGB[1] - MARGIN < colorInQuestionARGB[1] &&
                        colorInQuestionARGB[1] < baseColorARGB[1] + MARGIN) &&
                (baseColorARGB[2] - MARGIN < colorInQuestionARGB[2] &&
                        colorInQuestionARGB[2] < baseColorARGB[2] + MARGIN) &&
                (baseColorARGB[3] - MARGIN < colorInQuestionARGB[3] &&
                        colorInQuestionARGB[3] < baseColorARGB[3] + MARGIN) );

        return isApproximatelySameColor;
    }

    /**
     * Checks whether through an iteration the same node was chosen
     * @param nodeInQuestion a particular node
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return boolean
     */
    private static boolean choseItself(Node nodeInQuestion, int x, int y) {
        return (nodeInQuestion.getX() + x == 0 && nodeInQuestion.getY() + y >= 0);
    }

    /**
     * Checks whether a node is near the node map border
     * @param nodeInQuestion a particular node
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param nodeMap an 2D-array of nodes
     * @return boolean
     */
    private static boolean nodeIsNearMapsEdge(Node nodeInQuestion, int x, int y, Node[][] nodeMap) {
        boolean crossRight = nodeInQuestion.getX() + x >= nodeMap.length;
        boolean crossLeft = nodeInQuestion.getX() + x < 0;
        boolean crossBottom = nodeInQuestion.getY() + y >= nodeMap[0].length;
        boolean crossCelling = nodeInQuestion.getY() + y < 0;
        return (crossRight || crossLeft || crossBottom || crossCelling);
    }
}
