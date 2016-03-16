package pl.edu.agh.capo.logic.common;

import com.vividsolutions.jts.math.Vector2D;
import pl.edu.agh.capo.maze.Coordinates;

import java.io.Serializable;

public class Location implements Serializable {
    private static final long serialVersionUID = 1067476091479732881L;
    public double positionX, positionY, alpha;

    public Location(double positionX, double positionY, double alpha) {
        super();
        this.positionX = positionX;
        this.positionY = positionY;
        this.alpha = alpha;
    }

    public Location(Coordinates coords, double angle) {
        this.positionX = coords.getX();
        this.positionY = coords.getY();
        this.alpha = angle;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.positionX, positionX) != 0) return false;
        if (Double.compare(location.positionY, positionY) != 0) return false;
        return Double.compare(location.alpha, alpha) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(positionX);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(positionY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(alpha);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "positionX=" + positionX +
                ", positionY=" + positionY +
                ", alpha=" + alpha +
                '}';
    }
}
