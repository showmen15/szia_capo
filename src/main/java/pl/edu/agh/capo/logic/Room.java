package pl.edu.agh.capo.logic;

import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.maze.Wall;
import pl.edu.agh.capo.maze.helper.MazeHelper;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private List<Wall> walls;
    private List<Gate> gates;

    private double minY;
    private double maxY;
    private double minX;
    private double maxX;

    private List<Gate> northGates;
    private List<Gate> southGates;
    private List<Gate> westGates;
    private List<Gate> eastGates;

    public Room(List<Wall> walls, List<Gate> gates) {
        this.walls = walls;
        this.gates = gates;

        northGates = new ArrayList<Gate>();
        southGates = new ArrayList<Gate>();
        westGates = new ArrayList<Gate>();
        eastGates = new ArrayList<Gate>();

        findCorners();
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

    private void findCorners() {
        minY = MazeHelper.getMinY(walls);
        maxY = MazeHelper.getMaxY(walls);
        minX = MazeHelper.getMinX(walls);
        maxX = MazeHelper.getMaxX(walls);
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
}
