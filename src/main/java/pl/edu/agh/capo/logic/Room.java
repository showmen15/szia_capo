package pl.edu.agh.capo.logic;

import pl.edu.agh.capo.logic.common.Location;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.maze.Wall;
import pl.edu.agh.capo.maze.helper.MazeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Room {

    private static final int NEIGHBOURHOOD_FACTOR = 6;

    private final List<Wall> walls;
    private final List<Gate> gates;

    private final double minY;
    private final double maxY;
    private final double minX;
    private final double maxX;

    private final List<Gate> northGates;
    private final List<Gate> southGates;
    private final List<Gate> westGates;
    private final List<Gate> eastGates;

    private final double neighbourhoodX;
    private final double neighbourhoodY;

    private final String spaceId;

    private final Random random = new Random();
    private Map<String, Room> gateRooms;

    public Room(List<Wall> walls, List<Gate> gates, String spaceId) {
        this.walls = walls;
        this.gates = gates;
        this.spaceId = spaceId;

        northGates = new ArrayList<>();
        southGates = new ArrayList<>();
        westGates = new ArrayList<>();
        eastGates = new ArrayList<>();

        minY = MazeHelper.getMinY(walls);
        maxY = MazeHelper.getMaxY(walls);
        minX = MazeHelper.getMinX(walls);
        maxX = MazeHelper.getMaxX(walls);

        neighbourhoodX = (maxX - minX) / (2 * NEIGHBOURHOOD_FACTOR);
        neighbourhoodY = (maxY - minY) / (2 * NEIGHBOURHOOD_FACTOR);

        splitGates();
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public List<Gate> getNorthGates() {
        return northGates;
    }

    public List<Gate> getSouthGates() {
        return southGates;
    }

    public List<Gate> getWestGates() {
        return westGates;
    }

    public List<Gate> getEastGates() {
        return eastGates;
    }

    public String getSpaceId() {
        return spaceId;
    }

    private void splitGates() {
        for (Gate gate : gates) {
            if (gate.getFrom().getX() == gate.getTo().getX()) {
                if (Math.abs(minX - gate.getFrom().getX()) < Math.abs(maxX - gate.getFrom().getX())) {
                    westGates.add(gate);
                } else {
                    eastGates.add(gate);
                }
            } else {
                if (Math.abs(minY - gate.getFrom().getY()) < Math.abs(maxY - gate.getFrom().getY())) {
                    northGates.add(gate);
                } else {
                    southGates.add(gate);
                }
            }
        }
    }

    public boolean coordinatesMatches(double x, double y) {
        return !(x < minX || x > maxX || y < minY || y > maxY);
    }

    public Coordinates getRandomPosition() {
        return getRandom(minX, maxX, minY, maxY);
    }

    public Coordinates getRandomPositionInNeighbourhoodOf(Location location) {
        double minX = location.positionX - neighbourhoodX;
        double maxX = location.positionX + neighbourhoodX;
        double minY = location.positionY - neighbourhoodY;
        double maxY = location.positionY + neighbourhoodY;
        return getRandom(minX, maxX, minY, maxY);
    }

    private Coordinates getRandom(double minX, double maxX, double minY, double maxY) {
        Coordinates coordinates = new Coordinates();
        double x = random.nextDouble() * (maxX - minX) + minX;
        double y = random.nextDouble() * (maxY - minY) + minY;
        coordinates.setX(x);
        coordinates.setY(y);
        return coordinates;
    }

    public void setGateRooms(Map<String, Room> gateRooms) {
        this.gateRooms = gateRooms;
    }

    public Room getRoomBehindGate(Gate gate) {
        return gateRooms.get(gate.getId());
    }

    @Override
    public String toString() {
        return "Room{" +
                "spaceId='" + spaceId + '\'' +
                '}';
    }


}
