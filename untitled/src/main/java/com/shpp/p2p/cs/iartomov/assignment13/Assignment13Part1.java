package com.shpp.p2p.cs.iartomov.assignment13;


import java.awt.image.BufferedImage;

/**
 * Implements counting the number of silhouettes in an image
 * using the breadth-first search method.
 */
public class Assignment13Part1 implements Config {

    public static int main(String[] args) {

        String fileName = (args.length == 0) ? DEFAULT_FILENAME : "images/" + args[0];
        BufferedImage image;
        try {
            image = new ImageReader().readImage(fileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
        byte[][] bitmapBlackAndWhite = new ImageToBlackAndWhite().run(image);
        byte background = new BackgroundDefiner().defineBackground(bitmapBlackAndWhite);
        int silhouettesNumber = new SilhouetteFounderBFS().run(bitmapBlackAndWhite, background);
//        System.out.println("Silhouettes " + silhouettesNumber);
        return silhouettesNumber;
    }
}