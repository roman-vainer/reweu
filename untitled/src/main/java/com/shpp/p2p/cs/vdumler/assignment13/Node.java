package com.shpp.p2p.cs.vdumler.assignment13;

import java.awt.*;

/**
 * Contains particular pixel and different information about it.
 */
class Node {
    /**
     * x and y represent position of pixel in a picture
     */
    private final int x,y;
    private Color color = new Color(0, true);
    private boolean checked = false;
    private boolean silhouetteNode = false;
    int deletionMarker = 0;

    Node(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    Color getColor() {
        return color;
    }

    boolean isChecked() {
        return this.checked;
    }

    void markAsChecked() {
        this.checked = true;
    }

    void markAsUnChecked() {
        this.checked = false;
    }

    void markAsSilhouetteNode() {
        this.silhouetteNode = true;
    }

    boolean isSilhouetteNode() {
        return silhouetteNode;
    }

    void setDeletionMarker(int deletionMarker) {
        this.deletionMarker = deletionMarker;
    }

    int getDeletionMarker() {
        return this.deletionMarker;
    }
}
