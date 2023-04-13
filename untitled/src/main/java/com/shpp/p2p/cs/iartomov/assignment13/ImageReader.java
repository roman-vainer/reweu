package com.shpp.p2p.cs.iartomov.assignment13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Implements reading an image from a file.
 * */
public class ImageReader {

    /**
     * The method implements reading an image from a file
     *
     * @param fileName The name of the file with the image to search
     *                 for silhouettes in it
     * @return Image from file
     */
    public BufferedImage readImage(String fileName) throws Exception {
        return ImageIO.read(new File(fileName));                        //close
    }
}