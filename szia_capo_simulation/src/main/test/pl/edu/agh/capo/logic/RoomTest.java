package pl.edu.agh.capo.logic;

import org.junit.Test;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.maze.Wall;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class RoomTest {

    @Test
    public void testGroupWalls() throws Exception {
        Room room = new Room(Arrays.asList(
                buildWall("Wall251", 0.74, 0.4, 2.35, 0.4),
                buildWall("Wall252", 0.74, 0.4, 0.74, 0.69),
                buildWall("Wall254", 0.74, 1.2, 0.74, 1.38),
                buildWall("Wall255", 0.74, 1.38, 0.92, 1.38),
                buildWall("Wall256", 0.92, 1.38, 1.2, 1.38),
                buildWall("Wall258", 2.07, 1.38, 2.35, 1.38),
                buildWall("Wall259", 2.35, 0.4, 2.35, 1.38)), new ArrayList<>(), "260");
        assertEquals(6, room.getWallVectors().size());
        assertEquals(7, room.getWalls().size());

        room = new Room(Arrays.asList(
                buildWall("Wall264", 0.0, 0.0, 0.74, 0.0),
                buildWall("Wall265", 0.0, 0.0, 0.0, 1.38),
                buildWall("Wall267", 0.55, 1.38, 0.74, 1.38),
                buildWall("Wall254", 0.74, 1.2, 0.74, 1.38),
                buildWall("Wall252", 0.74, 0.4, 0.74, 0.69),
                buildWall("Wall271", 0.74, 0.0, 0.74, 0.4)), new ArrayList<>(), "272");
        assertEquals(5, room.getWallVectors().size());
        assertEquals(6, room.getWalls().size());

        room = new Room(Arrays.asList(buildWall("Wall276", 2.35, 2.5, 2.35, 3.26),
                buildWall("Wall278", 0.92, 2.5, 1.7, 2.5),
                buildWall("Wall280", 0.0, 2.5, 0.3, 2.5),
                buildWall("Wall281", 0.0, 2.5, 0.0, 3.26),
                buildWall("Wall283", 1.1, 3.26, 1.4, 3.26),
                buildWall("Wall284", 1.4, 3.26, 2.0, 3.26),
                buildWall("Wall285", 2.0, 3.26, 2.35, 3.26)), new ArrayList<>(), "Space286");
        assertEquals(5, room.getWallVectors().size());
        assertEquals(7, room.getWalls().size());

        room = new Room(Arrays.asList(buildWall("Wall291", 0.0, 4.9, 1.4, 4.9),
                buildWall("Wall292", 1.4, 4.5, 1.4, 4.9),
                buildWall("Wall283", 1.1, 3.26, 1.4, 3.26),
                buildWall("Wall296", 0.0, 3.26, 0.0, 4.9)), new ArrayList<>(), "Space297");
        assertEquals(4, room.getWallVectors().size());
        assertEquals(4, room.getWalls().size());

        room = new Room(Arrays.asList(buildWall("Wall258", 2.07, 1.38, 2.35, 1.38),
                buildWall("Wall256", 0.92, 1.38, 1.2, 1.38),
                buildWall("Wall305", 0.92, 2.3, 0.92, 2.5),
                buildWall("Wall278", 0.92, 2.5, 1.7, 2.5),
                buildWall("Wall308", 2.35, 1.38, 2.35, 2.5)), new ArrayList<>(), "Space309");
        assertEquals(5, room.getWallVectors().size());
        assertEquals(5, room.getWalls().size());

        room = new Room(Arrays.asList(buildWall("Wall305", 0.92, 2.3, 0.92, 2.5),
                buildWall("Wall255", 0.74, 1.38, 0.92, 1.38),
                buildWall("Wall267", 0.55, 1.38, 0.74, 1.38),
                buildWall("Wall319", 0.0, 1.38, 0.0, 2.5),
                buildWall("Wall280", 0.0, 2.5, 0.3, 2.5)), new ArrayList<>(), "Space322");
        assertEquals(4, room.getWallVectors().size());
        assertEquals(5, room.getWalls().size());

        room = new Room(Arrays.asList(buildWall("Wall327", 2.35, 3.26, 3.0, 3.26),
                buildWall("Wall285", 2.0, 3.26, 2.35, 3.26),
                buildWall("Wall329", 2.0, 3.26, 2.0, 3.7),
                buildWall("Wall331", 2.0, 4.9, 3.0, 4.9),
                buildWall("Wall332", 3.0, 3.26, 3.0, 4.9)), new ArrayList<>(), "Space333");
        assertEquals(4, room.getWallVectors().size());
        assertEquals(5, room.getWalls().size());

        room = new Room(Arrays.asList(buildWall("Wall336", 1.4, 4.9, 2.0, 4.9),
                buildWall("Wall329", 2.0, 3.26, 2.0, 3.7),
                buildWall("Wall284", 1.4, 3.26, 2.0, 3.26),
                buildWall("Wall292", 1.4, 4.5, 1.4, 4.9)), new ArrayList<>(), "Space342");
        assertEquals(4, room.getWallVectors().size());
        assertEquals(4, room.getWalls().size());
    }

    private static Wall buildWall(String name, double fromX, double fromY, double toX, double toY) {
        Wall wall = new Wall();
        wall.setFrom(buildCoordinates(fromX, fromY));
        wall.setTo(buildCoordinates(toX, toY));
        wall.setId(name);
        return wall;
    }

    private static Coordinates buildCoordinates(double x, double y) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(x);
        coordinates.setY(y);
        return coordinates;
    }
}
