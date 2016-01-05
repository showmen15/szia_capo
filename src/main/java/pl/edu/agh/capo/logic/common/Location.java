package pl.edu.agh.capo.logic.common;

import com.vividsolutions.jts.math.Vector2D;
import pl.edu.agh.capo.maze.Coordinates;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 1067476091479732881L;
    public double positionX, positionY, direction;

    public Location(double positionX, double positionY, double direction) {
        super();
        this.positionX = positionX;
        this.positionY = positionY;
        this.direction = direction;
    }

    public Vector2D getPositionVector() {
        return new Vector2D(positionX, positionY);
    }

    public double getDistance(Location l) {
        return Math.sqrt((positionX - l.positionX) * (positionX - l.positionX) + (positionY - l.positionY) * (positionY - l.positionY));
    }

    public Coordinates getCoordinates() {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(positionX);
        coordinates.setY(positionY);
        return coordinates;
    }
}
