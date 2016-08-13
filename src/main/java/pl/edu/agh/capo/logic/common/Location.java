package pl.edu.agh.capo.logic.common;

import pl.edu.agh.capo.logic.robot.CapoRobotConstants;
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

    public double getDistance(Location l) {
        return Math.sqrt((positionX - l.positionX) * (positionX - l.positionX) + (positionY - l.positionY) * (positionY - l.positionY));
    }

    public Coordinates getCoordinates() {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(positionX);
        coordinates.setY(positionY);
        return coordinates;
    }

    public boolean inNeighbourhoodOf(Location location) {
        return (Math.abs(location.positionX - positionX) < CapoRobotConstants.NEIGHBOURHOOD_SCOPE &&
                Math.abs(location.positionY - positionY) < CapoRobotConstants.NEIGHBOURHOOD_SCOPE);
    }

    public double calculateDistanceTo(Location location) {
        double dx = location.positionX - positionX;
        double dy = location.positionY - positionY;
        return Math.sqrt(dx * dx + dy * dy);
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
        return positionX + "," + positionY + "," + alpha;
    }

    public static Location fromString(String line) {
        String[] attributes = line.split(",");
        double positionX = Double.parseDouble(attributes[0]);
        double positionY = Double.parseDouble(attributes[1]);
        double alpha = Double.parseDouble(attributes[2]);
        return new Location(positionX, positionY, alpha);
    }
}
