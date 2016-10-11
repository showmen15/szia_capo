package pl.edu.agh.capo.common;

import math.geom2d.Point2D;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.capo.maze.Coordinates;

import static org.junit.Assert.assertEquals;

public class SectionTest {
    private static final double START_X = 88;
    private static final double START_Y = 88;
    private static final double END_X = 0;
    private static final double END_Y = -88;

    private static final double TRANSLATED_START_X = 2.5;
    private static final double TRANSLATED_START_Y = -2.5;
    private static final double TRANSLATED_END_X = 0;
    private static final double TRANSLATED_END_Y = 2.5;

    private Section section;

    @Before
    public void setUp() throws Exception {
        section = new Section(0, 0, buildCoordinates());
    }

    @Test
    public void testAdjust() throws Exception {
        Point2D point2D = section.adjust(buildCoordinates(START_X, START_Y));
        assertEquals(TRANSLATED_START_X, point2D.x(), 0.02);
        assertEquals(TRANSLATED_START_Y, point2D.y(), 0.02);
        point2D = section.adjust(buildCoordinates(END_X, END_Y));
        assertEquals(TRANSLATED_END_X, point2D.x(), 0.02);
        assertEquals(TRANSLATED_END_Y, point2D.y(), 0.02);
    }

    @Test
    public void testCreatingVector() throws Exception {
        assertEquals(TRANSLATED_START_X, section.vector[0].x(), 0.02);
        assertEquals(TRANSLATED_START_Y, section.vector[0].y(), 0.02);
        assertEquals(TRANSLATED_END_X, section.vector[1].x(), 0.02);
        assertEquals(TRANSLATED_END_Y, section.vector[1].y(), 0.02);
    }

    @Test
    public void testRotate() throws Exception {
        Point2D rotated = section.rotate(new Point2D(TRANSLATED_START_X, TRANSLATED_START_Y), -90);
        assertEquals(2.5, rotated.x(), 0.02);
        assertEquals(2.5, rotated.y(), 0.02);
        rotated = section.rotate(new Point2D(TRANSLATED_END_X, TRANSLATED_END_Y), -90);
        assertEquals(-2.5, rotated.x(), 0.02);
        assertEquals(0.0, rotated.y(), 0.02);
    }

    @Test
    public void testTranslatedVector() throws Exception {
        Point2D[] translated = section.getTranslatedVector(buildCoordinates(-1, 3).toPoint2D(), -90);
        assertEquals(1.5, translated[0].x(), 0.02);
        assertEquals(5.5, translated[0].y(), 0.02);
        assertEquals(-3.5, translated[1].x(), 0.02);
        assertEquals(3.0, translated[1].y(), 0.02);
    }

    private static Coordinates[] buildCoordinates() {
        Coordinates[] coords = new Coordinates[2];
        coords[0] = new Coordinates();
        coords[1] = new Coordinates();
        coords[0].setX(START_X);
        coords[0].setY(START_Y);
        coords[1].setX(END_X);
        coords[1].setY(END_Y);
        return coords;
    }

    private Coordinates buildCoordinates(double x, double y) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(x);
        coordinates.setY(y);
        return coordinates;
    }
}
