package com.shpp.p2p.cs.iartomov.assignment13;

public interface Config {
    /**
     * Default silhouette search filename.
     */
    public static final String DEFAULT_FILENAME = "test.jpg";

    /**
     * Offset of pixel coordinates to visit neighboring pixels. The first index is
     * the x-coordinate, the second is the y-coordinate. Can take the values -1, 0, 1.
     */
    public static final byte[][] NEIGHBOR_PIXELS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    /**
     * Garbage size relative to silhouette size. Can take the values
     * from 0 (no garbage) to 1 (garbage size the is silhouette max size).
     */
    public static final double GARBAGE_RATIO = 0.25;

    /**
     * Coefficient for finding sticky silhouettes. If the size of the silhouette
     * is greater than the arithmetic mean of the sizes of all the silhouettes in
     * the image multiplied by this coefficient, we can assume that the silhouettes
     * are stuck together.
     * */
    public static final double STICK_COEF = 1.3;
}