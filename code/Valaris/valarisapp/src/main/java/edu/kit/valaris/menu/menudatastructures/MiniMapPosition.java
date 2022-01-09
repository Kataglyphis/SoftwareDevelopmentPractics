package edu.kit.valaris.menu.menudatastructures;

/**
 * This class provides a data structure for the players mini map position. This position contains an m_posX and m_posY variable.
 * @author Artur Wesner
 */
public class MiniMapPosition {
    /**
     * X-Coordinate.
     */
    private int m_posX;

    /**
     * Y-Coordinate.
     */
    private int m_posY;

    /**
     * Constructor settind the coordinates.
     * @param x X-Coordinate to be set.
     * @param y Y-Coordinate to be set.
     */
    public MiniMapPosition(int x, int y) {
        this.m_posX = x;
        this.m_posY = y;
    }

    /**
     * Getter for X-Coordinate.
     * @return The X-Coordinate.
     */
    public int getXCoord() {
        return m_posX;
    }

    /**
     * Getter for Y-Coordinate.
     * @return The Y-Coordinate.
     */
    public int getYCoord() {
        return m_posY;
    }

    /**
     * Setter for X-Coordinate.
     * @param x The X-Coordinate.
     */
    public void setXCoord(int x) {
        this.m_posX = x;
    }

    /**
     * Setter for Y-Coordinate.
     * @param y The Y-Coordinate.
     */
    public void setYCoord(int y) {
        this.m_posY = y;
    }
}
