package pt.pa.view;

import java.util.Objects;

/**
 * Store the coordinates of a stop in the map
 */
public class Coordinate {
    /** The x position of the stop */
    private int posX;
    /** The y position of the stop */
    private int posY;

    /**
     * Constructor for Coordinate
     * @param posX int The x position of the stop
     * @param posY int The y position of the stop
     */
    public Coordinate(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Get the x position of the stop
     * @return int The x position of the stop
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Set the x position of the stop
     * @param posX int The x position of the stop
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Get the y position of the stop
     * @return int The y position of the stop
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Set the y position of the stop
     * @param posY int The y position of the stop
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Check if two coordinates are equal
     * @param o Object The other object to compare
     * @return true if the coordinates are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate coordinate = (Coordinate) o;
        return Objects.equals(posX, coordinate.getPosX()) &&
                Objects.equals(posY, coordinate.getPosY());
    }

    /**
     * Generate a hash code for the coordinates
     * @return int The hash code for the coordinates
     */
    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }

    /**
     * Get the string representation of the coordinates
     * @return String The string representation of the coordinates
     */
    @Override
    public String toString() {
        return new StringBuilder()
                .append("(")
                .append(posX)
                .append(", ")
                .append(posY)
                .append(")")
                .toString();
    }
}
