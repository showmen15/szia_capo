package pl.edu.agh.capo.logic.estimator;

import pl.edu.agh.capo.common.Line;
import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.logic.Room;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerpendicularLinesLocationEstimator {
    private final Room room;
    private final Set<Location> locations = new HashSet<>();

    public PerpendicularLinesLocationEstimator(Room room) {
        this.room = room;
    }

    public void prepareLocations(List<Line> lines, Line next, int index) {
        for (int i = index; i < lines.size(); i++) {
            Line line = lines.get(i);
            if (line.isPerpendicularTo(next)) {
                if (line.getTheta() < next.getTheta()) {
                    addCornerLocations(next, line);
                } else {
                    addCornerLocations(line, next);
                }
            }
        }
    }

    private void addCornerLocations(Line left, Line right) {
        double theta = right.getTheta();
        //int size = locations.size();
        addCornerLocations(left, right, theta);
        //System.out.println("THETA: " + (locations.size() - size));

        //size = locations.size();
        addCornerLocations(left, right, theta + 90);
/*        if(locations.size() - size > 0) {
            System.out.println(left.getTheta() - right.getTheta());
        }*/
        //System.out.println("THETA + 90: " + (locations.size() - size));

        //size = locations.size();
        addCornerLocations(left, right, theta + 180);
        //System.out.println("THETA + 180: " + (locations.size() - size));

        //size = locations.size();
        addCornerLocations(left, right, theta - 90);
/*        if(locations.size() - size > 0) {
            System.out.println(left.getTheta() - right.getTheta());
        }*/
        //System.out.println("THETA + -90: " + (locations.size() -size));
    }

    private void addCornerLocations(Line left, Line right, double angle) {
        addLocation(new Location(room.getMaxX() - right.getRho(), room.getMinY() + left.getRho(), angle));
        addLocation(new Location(room.getMaxX() - left.getRho(), room.getMaxY() - right.getRho(), angle));
        addLocation(new Location(room.getMinX() + right.getRho(), room.getMaxY() - left.getRho(), angle));
        addLocation(new Location(room.getMinX() + left.getRho(), room.getMinY() + right.getRho(), angle));
    }

    private void addLocation(Location location) {
        if (room.coordinatesMatches(location.positionX, location.positionY)) {
            locations.add(location);
        }
    }

    public int size() {
        return locations.size();
    }

    public Location pop() {
        Location location = locations.stream().findAny().get();
        locations.remove(location);
        return location;
    }
}
