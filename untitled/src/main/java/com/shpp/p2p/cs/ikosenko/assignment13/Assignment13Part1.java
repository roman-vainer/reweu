package com.shpp.p2p.cs.ikosenko.assignment13;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The class finds the silhouettes in the image and shows their number on the console.
 */
public class Assignment13Part1 {
    /**
     * The path to the image file.
     */
    private static final String PATH = "assets/test.jpg";

    public static int main(String[] args) {

        FindingSilhouettes findingSilhouettes = new FindingSilhouettes();

        try {
            if (args.length == 0) {
                args = new String[]{PATH};
            }
            BufferedImage image = ImageIO.read(new File("images/" + args[0]));
            int res = findingSilhouettes.findSilhouettes(image);
            System.out.println(" Image: " + args[0].substring(args[0].lastIndexOf('/') + 1)
                    + " - silhouettes on image is " + res);
            return res;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

