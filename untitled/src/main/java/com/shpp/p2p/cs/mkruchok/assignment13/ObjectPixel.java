package com.shpp.p2p.cs.mkruchok.assignment13;

/**
 * This class takes one pixel as object and contains its unique value, x-axis, y-axis and if this pixel was
 * visited during BFS process.
 */
public class ObjectPixel {
    public int pixelID;
    public int x;
    public int y;
    public boolean visited;


    public ObjectPixel(int pixelID, int x, int y) {
        this.x = x;
        this.y = y;
        this.pixelID = pixelID;
    }

    public void isVisited(){
        visited = true;
    }
}
