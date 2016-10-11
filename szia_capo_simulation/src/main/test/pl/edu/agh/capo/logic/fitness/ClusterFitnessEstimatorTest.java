package pl.edu.agh.capo.logic.fitness;

import math.geom2d.Point2D;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.capo.common.Section;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.maze.Wall;
import pl.edu.agh.capo.robot.Measure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClusterFitnessEstimatorTest {
    private Room room286;
    private Room room260;
    private Room room297;
    private Room room342;
    private Room room333;
    private Room room309;
    private Room room322;
    private Room room272;

    private ClusterFitnessEstimator estimator;

    @Before
    public void setUp() throws Exception {
        room333 = create333RoomMock();
        room342 = create342RoomMock();
        room297 = create297RoomMock();
        room286 = create286RoomMock();
        room260 = create260RoomMock();

        room309 = create309RoomMock();
        room322 = create322RoomMock();
        room272 = create272RoomMock();

        updateRoomMock(room333, build333Rooms(), build333Gates());
        updateRoomMock(room342, build342Rooms(), build342Gates());
        updateRoomMock(room297, build297Rooms(), build297Gates());
        updateRoomMock(room286, build286Rooms(), build286Gates());
        updateRoomMock(room260, build260Rooms(), build260Gates());

        updateRoomMock(room309, build309Rooms(), build309Gates());
        updateRoomMock(room322, build322Rooms(), build322Gates());
        updateRoomMock(room272, build272Rooms(), build272Gates());
    }

    @Test
    public void testGetVisionSection() throws Exception {
        setupEstimator(room260, buildFirstSections());
        Coordinates center = buildCoordinates(1.8, 2.8);
        Point2D[] visionSection = estimator.getVisionSection(
                buildSection(1.9428571428571428, 0.05714285714285714, 1.6285714285714286, 1.3142857142857143), center.toPoint2D(), -167.5);
        Point2D expectedStart = Section.translated(new Point2D(1.9428571428571428, 0.05714285714285714), center.toPoint2D(), -167.5);
        assertPoint2D(expectedStart, visionSection[0]);
        Point2D expectedEnd = Section.translated(new Point2D(1.6285714285714286, 1.3142857142857143), center.toPoint2D(), -167.5);
        assertPoint2D(visionSection[1], expectedEnd);
    }

//    @Test
//    public void testGetAngle() throws Exception {
//        setup286Estimator();
//        double angle = estimator.getAngle(buildVector(5, 5, -5, -5));
//        assertEquals(225, angle, 0.0000001);
//        angle = estimator.getAngle(buildVector(-5, -5, 5, 5));
//        assertEquals(45, angle, 0.0000001);
//        angle = estimator.getAngle(buildVector(5, -5, -5, 5));
//        assertEquals(135, angle, 0.000001);
//        angle = estimator.getAngle(buildVector(-5, 5, 5, -5));
//        assertEquals(315, angle, 0.0000001);
//        angle = estimator.getAngle(buildVector(-1, 3, -3, 1));
//        assertEquals(225, angle, 0.0000001);
//        angle = estimator.getAngle(buildVector(1.9428571428571428, 0.05714285714285714,
//                1.6285714285714286, 1.3142857142857143));
//        assertEquals(104, angle, 0.1);
//        angle = estimator.getAngle(buildVector(2.35, 2.5, 2.35, 3.26));
//        assertEquals(90, angle, 0.0);
//    }
//
//    @Test
//    public void testSectionAngle() throws Exception {
//        setup286Estimator();
//        Coordinates center = buildCoordinates(1.8, 2.8);
//        Point2D start = Section.translated(new Point2D(1.9428571428571428, 0.05714285714285714), center.toPoint2D(), -167.5);
//        Point2D end = Section.translated(new Point2D(1.6285714285714286, 1.3142857142857143), center.toPoint2D(), -167.5);
//        double angle = estimator.getAngle(new Point2D[]{start, end});
//        assertEquals(88.4, angle, 0.1);
//    }
//
//    @Test
//    public void testCalculateAngleDiff() throws Exception {
//        setup286Estimator();
//        double diff = estimator.calculateAngleDiff(90, 271.5);
//        assertEquals(diff, 1.5, 0.0);
//        diff = estimator.calculateAngleDiff(45, 45);
//        assertEquals(diff, 0, 0.0);
//        diff = estimator.calculateAngleDiff(45, 60);
//        assertEquals(diff, 15, 0.0);
//        diff = estimator.calculateAngleDiff(60, 45);
//        assertEquals(diff, 15, 0.0);
//        diff = estimator.calculateAngleDiff(45, 314);
//        assertEquals(diff, 89, 0.0);
//        diff = estimator.calculateAngleDiff(90, 271.5);
//        assertEquals(diff, 1.5, 0.0);
//        diff = estimator.calculateAngleDiff(0, 351);
//        assertEquals(diff, 9, 0.0);
//        diff = estimator.calculateAngleDiff(350, 10);
//        assertEquals(diff, 20, 0.0);
//    }
//
//    @Test
//    public void testBuildWallVector() throws Exception {
//        setup286Estimator();
//        Point2D[] wallVector = estimator.buildWallVector(buildWall("test", 2.25, 2.5, 2.35, 3.26));
//        assertEquals(wallVector[0].x(), 2.25, 0.0);
//        assertEquals(wallVector[0].y(), 2.5, 0.0);
//        assertEquals(wallVector[1].x(), 2.35, 0.0);
//        assertEquals(wallVector[1].y(), 3.26, 0.0);
//    }
//
//    @Test
//    public void testAnglesFitEnough() throws Exception {
//        setup286Estimator();
//        assertTrue(estimator.anglesFitEnough(90, 271.5));
//        assertTrue(estimator.anglesFitEnough(45, 45));
//        assertTrue(estimator.anglesFitEnough(45, 54));
//        assertTrue(estimator.anglesFitEnough(45, 36));
//        assertTrue(estimator.anglesFitEnough(0, 9));
//        assertTrue(estimator.anglesFitEnough(0, 351));
//
//        assertFalse(estimator.anglesFitEnough(90, 100));
//        assertFalse(estimator.anglesFitEnough(90, 80));
//        assertFalse(estimator.anglesFitEnough(90, 260));
//        assertFalse(estimator.anglesFitEnough(90, 280));
//        assertFalse(estimator.anglesFitEnough(0, 11));
//        assertFalse(estimator.anglesFitEnough(1, 351));
//    }
//
//    @Test
//    public void testInTheSameDirection() throws Exception {
//        setup286Estimator();
//        assertTrue(estimator.inSameDirection(new Vector2D(1, 1), new Vector2D(1, -1)));
//        assertTrue(estimator.inSameDirection(new Vector2D(1, 1), new Vector2D(-1, 1)));
//        assertFalse(estimator.inSameDirection(new Vector2D(1, 1), new Vector2D(-1, -1)));
//        assertTrue((estimator.inSameDirection(new Vector2D(1, 1), new Vector2D(1, -1), new Vector2D(8, 0))));
//        assertFalse((estimator.inSameDirection(new Vector2D(1, 1), new Vector2D(1, -1), new Vector2D(0, 8))));
//        assertTrue(estimator.inSameDirection(new Vector2D(1, 1), new Vector2D(2, 2)));
//        assertTrue(estimator.inSameDirection(new Vector2D(2, 2), new Vector2D(1, 1)));
//
//        Vector2D vectorSS = new Vector2D(-0.07443127562825502, -0.6306445238012881);
//        Vector2D vectorSE = new Vector2D(-0.1091716489151906, 0.664722906672889);
//        Vector2D vectorES = new Vector2D(-0.07443127562825502, -1.390644523801288);
//        Vector2D vectorEE = new Vector2D(-0.1091716489151906, -0.09527709332711076);
//        assertFalse(estimator.inSameDirection(vectorSE, vectorSS, vectorES, vectorEE));
//
//
//    }

    @Test
    @SuppressWarnings("AccessStaticViaInstance")
    public void testComputeIntersectionOfSegments() throws Exception {
        setupEstimator(room286, buildFirstSections());
        assertNull(estimator.computeIntersectionOfSegments(
                buildVector(1, 1, 0, 0),
                buildVector(0.0, 0.0, -1, -1)));
        assertNull(estimator.computeIntersectionOfSegments(
                buildVector(1, 1, 0, 0),
                buildVector(0.1, 0.1, -1, -1)));
        assertNull(estimator.computeIntersectionOfSegments(
                buildVector(1, 1, 0, 0),
                buildVector(0.0, 0.1, -1, -1)));
        assertNull(estimator.computeIntersectionOfSegments(
                buildVector(1, 1, 0, 0),
                buildVector(0.0, 0.1, -1, -1)));
        Point2D intersectionPoint = estimator.computeIntersectionOfSegments(
                buildVector(1, 1, -1, -1),
                buildVector(1, -1, -1, 1));
        assertNotNull(intersectionPoint);
        assertEquals(0.0, intersectionPoint.x(), 0.0);
        assertEquals(0.0, intersectionPoint.y(), 0.0);
        intersectionPoint = estimator.computeIntersectionOfSegments(
                buildVector(1, 1, 0, 0),
                buildVector(1, 1, -1, 1));
        assertNotNull(intersectionPoint);
        assertEquals(1.0, intersectionPoint.x(), 0.0);
        assertEquals(1.0, intersectionPoint.y(), 0.0);
    }

//    @Test
//    public void testIsVisible() throws Exception {
//        assertFalse(estimator.isInvisible(buildCoordinates(1.0, 1.0), -90, buildVector(1,2, 1,3)));
//        assertTrue(estimator.isInvisible(buildCoordinates(1.0, 1.0), -90, buildVector(2, 1, 3, 1)));
//        assertFalse(estimator.isInvisible(buildCoordinates(1.0, 1.0), -90, buildVector(2, 1, 1, 2)));
//        assertTrue(estimator.isInvisible(buildCoordinates(1.0, 0.0), 90, buildVector(0, 0, -1, 0)));
//        assertTrue(estimator.isInvisible(buildCoordinates(1.0, 0.0), 90, buildVector(-1, -1, -1, 0)));
//        assertFalse(estimator.isInvisible(buildCoordinates(1.0, 0.0), 90, buildVector(1, 1, 1, -10)));
//        assertFalse(estimator.isInvisible(buildCoordinates(1.9365, 2.8718), -167.5, buildVector(2.35, 2.5, 2.35, 3.26)));
//        assertFalse(estimator.isInvisible(buildCoordinates(1.9365, 2.8718), -167.5, buildVector(1.1, 3.26, 2.35, 3.26)));
//    }

//    @Test
//    public void testInDifferentDirection() throws Exception {
//        setup286Estimator();
//        assertTrue(estimator.inDifferentDirection(new Vector2D(1, 1), new Vector2D(-1, -1)));
//        assertTrue(estimator.inDifferentDirection(new Vector2D(1, 1), new Vector2D(0, -1), new Vector2D(-0.8, -1)));
//        assertFalse(estimator.inDifferentDirection(new Vector2D(1, 1), new Vector2D(0, 1)));
//        assertFalse(estimator.inDifferentDirection(new Vector2D(1, 1), new Vector2D(1, 3)));
//    }


//    ildWall("Wall276", 2.35, 2.5, 2.35, 3.26),
//    buildWall("Wall278", 0.92, 2.5, 1.7, 2.5),
//    buildWall("Wall280", 0.0, 2.5, 0.3, 2.5),
//    buildWall("Wall281", 0.0, 2.5, 0.0, 3.26),
//    buildWall("Wall283", 1.1, 3.26, 2.35, 3.26))
//            .stream().map(ClusterFitnessEstimatorTest::build

    @Test
    public void testEstimateFitnessForRoom286() throws Exception {
        setupEstimator(room286, buildFirstSections());
        assertEquals(0.7, estimator.estimateFitness(buildCoordinates(1.9152856784534364, 2.8625201265991542).toPoint2D(), -167.5), 0.1);

        double bestFit = estimator.estimateFitness(buildCoordinates(1.9, 2.9).toPoint2D(), -165.5);
        double semiBestFit = estimator.estimateFitness(buildCoordinates(1.8, 2.8).toPoint2D(), -167.5);
        assertTrue(bestFit > semiBestFit);
        assertTrue(bestFit > 0);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(1.6, 2.8).toPoint2D(), -167.5), 0.1);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(1.9, 3.1).toPoint2D(), 60.5), 0.0);
    }

    @Test
    public void testEstimateFitnessForRoom260() throws Exception {
        setupEstimator(room260, buildFirstSections());
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(1.247, 0.6).toPoint2D(), 14.5), 0.1);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(1.819, 0.901).toPoint2D(), 14.5), 0.1);

        //1.607914062357216,4.222353344233306,-77.5
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(2.35, 2.5).toPoint2D(), 104.5), 0.0);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(2.35, 3.26).toPoint2D(), 104.5), 0.0);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(0.0, 2.5).toPoint2D(), 104.5), 0.1);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(0.0, 2.5).toPoint2D(), 104.5), 0.1);
        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(1.1, 3.26).toPoint2D(), -104.5), 0.0);

        assertEquals(0.0, estimator.estimateFitness(buildCoordinates(1.4252884348565698, 4.8991083043091175).toPoint2D(), 104.5), 0.0);
        // assertEquals(0.25, estimator.estimateFitness(buildCoordinates(1.4791210067335123,4.158168530592393),-70.0), 0.0);
    }

    @Test
    public void testBestFirstSectionForRoom272() throws Exception {
        setupEstimator(room297, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(0.375767, 0.411399).toPoint2D(), 102.500000);
        double vision = estimator.estimateFitness(buildCoordinates(0.351506, 0.484043).toPoint2D(), 102.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision > cluster);
    }

    @Test
    public void testBestFirstSectionForRoom260() throws Exception {
        setupEstimator(room260, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(2.008213, 1.007484).toPoint2D(), 104.500000);
        double vision = estimator.estimateFitness(buildCoordinates(1.961286, 0.883312).toPoint2D(), 102.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision >= cluster);
    }

    //todo: blad
    @Test
    public void testBestFirstSectionForRoom322() throws Exception {
        setupEstimator(room322, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(0.440822, 1.749294).toPoint2D(), 12.500000);
        double vision = estimator.estimateFitness(buildCoordinates(0.387773, 2.014517).toPoint2D(), -77.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision > cluster);
    }

    @Test
    public void testBestFirstSectionForRoom309() throws Exception {
        setupEstimator(room309, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(1.859942, 2.303277).toPoint2D(), -75.500000);
        double vision = estimator.estimateFitness(buildCoordinates(1.961106, 1.856269).toPoint2D(), 102.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision > cluster);
    }

    @Test
    public void testBestFirstSectionForRoom286() throws Exception {
        setupEstimator(room286, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(1.870654, 2.925311).toPoint2D(), -165.500000);
        double vision = estimator.estimateFitness(buildCoordinates(1.882426, 2.871315).toPoint2D(), -167.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision > cluster);
    }

    @Test
    public void testBestFirstSectionForRoom297() throws Exception {
        setupEstimator(room297, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(1.352726, 4.560552).toPoint2D(), -173.000000);
        double vision = estimator.estimateFitness(buildCoordinates(0.387705, 4.416153).toPoint2D(), -77.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision > cluster);
    }

    @Test
    public void testBestFirstSectionForRoom342() throws Exception {
        setupEstimator(room342, buildFirstSections());
        double cluster = estimator.estimateFitness(buildCoordinates(1.901430, 3.741065).toPoint2D(), -165.500000);
        double vision = estimator.estimateFitness(buildCoordinates(1.918040, 4.490630).toPoint2D(), -165.500000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision > cluster);
    }

    @Test
    public void testBestFirstSectionForRoom333() throws Exception {
        setupEstimator(room333, buildFirstSections());
        double vision = estimator.estimateFitness(buildCoordinates(2.611179, 3.743852).toPoint2D(), 102.500000);
        double cluster = estimator.estimateFitness(buildCoordinates(3.000000, 4.837594).toPoint2D(), -83.000000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision >= cluster);
    }

    @Test
    public void testErrorForRoom286() throws Exception {
        setupEstimator(room286, Arrays.asList(
                buildSection(0.5428571428571428, 1.7714285714285714, -0.9142857142857143, 1.5714285714285714),
                buildSection(-0.9142857142857143, 1.5714285714285714, -0.8857142857142857, 1.2285714285714284),
                buildSection(0.8571428571428571, 0.34285714285714286, 0.6285714285714286, 1.5999999999999999),
                buildSection(-0.7714285714285714, -0.3142857142857143, -0.8285714285714285, 0.4857142857142857),
                buildSection(-0.8285714285714285, 0.4857142857142857, -0.45714285714285713, 0.6),
                buildSection(0.9714285714285714, -0.5142857142857142, 0.7999999999999999, 0.19999999999999998)));
        assertEquals(0.25, estimator.estimateFitness(buildCoordinates(1.691380, 2.500044).toPoint2D(), -83.000000), 0.1);
    }

    @Test
    public void testBestSectionForRoom286() throws Exception {
        setupEstimator(room286, Arrays.asList(buildSection(0.5714285714285714, 2.8, -0.14285714285714285, 2.8285714285714283),
                buildSection(0.8571428571428571, 2.7714285714285714, 0.6285714285714286, 2.8),
                buildSection(1.1428571428571428, 1.2571428571428571, 1.1428571428571428, 1.5428571428571427),
                buildSection(-0.45714285714285713, 1.3428571428571427, -0.08571428571428572, 1.4),
                buildSection(-0.34285714285714286, -0.19999999999999998, -0.45714285714285713, 1.2285714285714284),
                buildSection(1.0571428571428572, -0.2571428571428571, 1.1142857142857143, 0.7714285714285714),
                buildSection(1.1142857142857143, 0.7714285714285714, 0.7428571428571429, 0.8285714285714285),
                buildSection(0.8571428571428571, -0.22857142857142856, 0.39999999999999997, -0.22857142857142856)));
        double vision = estimator.estimateFitness(buildCoordinates(2.800560, 4.359590).toPoint2D(), -92.000000);
        double cluster = estimator.estimateFitness(buildCoordinates(2.783106, 4.429289).toPoint2D(), -90.000000);
        assertTrue(String.format("vision[%f] is not higher than cluster[%f]", vision, cluster), vision >= cluster);

    }

    private void setupEstimator(Room room, List<Section> sections) {
        Measure measureMock = mock(Measure.class);
        when(measureMock.getSections()).thenReturn(sections);
        estimator = new ClusterFitnessEstimator(room, measureMock);
    }

    private void updateRoomMock(Room roomMock, List<Room> rooms, List<Point2D[]> gates) {
        when(roomMock.getRooms()).thenReturn(rooms);
        when(roomMock.getGateVectors()).thenReturn(gates);
        for (Point2D[] gate : gates) {
            int index = gates.indexOf(gate);
            when(roomMock.getRoomBehindGate(gate)).thenReturn(rooms.get(index));
        }
    }

    private Room createRoomMock(String name, List<Point2D[]> walls) {
        Room roomMock = mock(Room.class);
        when(roomMock.getSpaceId()).thenReturn(name);
        when(roomMock.getWallVectors()).thenReturn(walls);
        return roomMock;
    }

    private Room create286RoomMock() {
        return createRoomMock("286", build286Walls());
    }

    private Room create260RoomMock() {
        return createRoomMock("260", build260Walls());
    }

    private Room create297RoomMock() {
        return createRoomMock("297", build297Walls());
    }

    private Room create342RoomMock() {
        return createRoomMock("342", build342Walls());
    }

    private Room create333RoomMock() {
        return createRoomMock("333", build333Walls());
    }

    private Room create272RoomMock() {
        return createRoomMock("272", build272Walls());
    }

    private Room create322RoomMock() {
        return createRoomMock("322", build322Walls());

    }

    private Room create309RoomMock() {
        return createRoomMock("309", build309Walls());

    }

    private List<Room> build260Rooms() {
        return Arrays.asList(
                room272,
                room309
        );
    }


    private List<Room> build342Rooms() {
        return Arrays.asList(
                room333,
                room297
        );
    }

    private List<Room> build297Rooms() {
        return Arrays.asList(
                room342,
                room286
        );
    }

    private List<Room> build286Rooms() {
        return Arrays.asList(
                room309,
                room322,
                room297
        );
    }

    private List<Room> build333Rooms() {
        return Collections.singletonList(
                room342
        );
    }

    private List<Room> build272Rooms() {
        return Arrays.asList(
                room322,
                room260
        );
    }

    private List<Room> build322Rooms() {
        return Arrays.asList(
                room309,
                room272,
                room286
        );
    }

    private List<Room> build309Rooms() {
        return Arrays.asList(
                room260,
                room322,
                room286
        );
    }

    private Point2D[] buildVector(double startX, double startY, double endX, double endY) {
        return new Point2D[]{new Point2D(startX, startY), new Point2D(endX, endY)};
    }

    private void assertPoint2D(Point2D expected, Point2D point) {
        assertEquals(expected.x(), point.x(), 0.02);
        assertEquals(expected.y(), point.y(), 0.02);

    }

    private List<Point2D[]> build342Gates() {
        return Arrays.asList(buildVector(2.0, 3.7, 2.0, 4.9),
                buildVector(1.4, 3.26, 1.4, 4.5));
    }

    private List<Point2D[]> build333Gates() {
        return Collections.singletonList(
                buildVector(2.0, 3.7, 2.0, 4.9)
        );
    }

    private List<Point2D[]> build297Gates() {
        return Arrays.asList(buildVector(1.4, 3.26, 1.4, 4.5),
                buildVector(0.0, 3.26, 1.1, 3.26));
    }

    private List<Point2D[]> build286Gates() {
        return Arrays.asList(
                buildVector(1.7, 2.5, 2.35, 2.5),
                buildVector(0.3, 2.5, 0.92, 2.5),
                buildVector(0.0, 3.26, 1.1, 3.26));
    }

    private List<Point2D[]> build260Gates() {
        return Arrays.asList(buildVector(0.74, 0.69, 0.74, 1.2),
                buildVector(1.2, 1.38, 2.07, 1.38));
    }

    private List<Point2D[]> build272Gates() {
        return Arrays.asList(buildVector(0.0, 1.38, 0.55, 1.38),
                buildVector(0.74, 0.69, 0.74, 1.2));
    }

    private List<Point2D[]> build322Gates() {
        return Arrays.asList(buildVector(0.92, 1.38, 0.92, 2.3),
                buildVector(0.0, 1.38, 0.55, 1.38),
                buildVector(0.3, 2.5, 0.92, 2.5));
    }

    private List<Point2D[]> build309Gates() {
        return Arrays.asList(buildVector(1.2, 1.38, 2.07, 1.38),
                buildVector(0.92, 1.38, 0.92, 2.3),
                buildVector(1.7, 2.5, 2.35, 2.5));
    }

    private List<Point2D[]> build333Walls() {
        return Arrays.asList(
                buildWall("Wall327", 2.0, 3.26, 3.0, 3.26),
                buildWall("Wall329", 2.0, 3.26, 2.0, 3.7),
                buildWall("Wall331", 2.0, 4.9, 3.0, 4.9),
                buildWall("Wall332", 3.0, 3.26, 3.0, 4.9)).stream().map(ClusterFitnessEstimatorTest::buildWallVector)
                .collect(Collectors.toList());
    }

    private List<Point2D[]> build342Walls() {
        return Arrays.asList(
                buildWall("Wall336", 1.4, 4.9, 2.0, 4.9),
                buildWall("Wall329", 2.0, 3.26, 2.0, 3.7),
                buildWall("Wall284", 1.4, 3.26, 2.0, 3.26),
                buildWall("Wall292", 1.4, 4.5, 1.4, 4.9)
        ).stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    private static List<Point2D[]> build297Walls() {
        return Arrays.asList(
                buildWall("Wall291", 0.0, 4.9, 1.4, 4.9),
                buildWall("Wall292", 1.4, 4.5, 1.4, 4.9),
                buildWall("Wall283", 1.1, 3.26, 1.4, 3.26),
                buildWall("Wall296", 0.0, 3.26, 0.0, 4.9)).stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    private static List<Point2D[]> build260Walls() {
        return Arrays.asList(
                buildWall("Wall251", 0.74, 0.4, 2.35, 0.4),
                buildWall("Wall252", 0.74, 0.4, 0.74, 0.69),
                buildWall("Wall254", 0.74, 1.2, 0.74, 1.38),
                buildWall("Wall255", 0.74, 1.38, 1.2, 1.38),
                buildWall("Wall258", 2.07, 1.38, 2.35, 1.38),
                buildWall("Wall259", 2.35, 0.4, 2.35, 1.38)).stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    private List<Point2D[]> build309Walls() {
        return Arrays.asList(buildWall("Wall258", 2.07, 1.38, 2.35, 1.38),
                buildWall("Wall256", 0.92, 1.38, 1.2, 1.38),
                buildWall("Wall305", 0.92, 2.3, 0.92, 2.5),
                buildWall("Wall278", 0.92, 2.5, 1.7, 2.5),
                buildWall("Wall308", 2.35, 1.38, 2.35, 2.5)).stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    private List<Point2D[]> build322Walls() {
        return Arrays.asList(buildWall("Wall305", 0.92, 2.3, 0.92, 2.5),
                buildWall("Wall255", 0.55, 1.38, 0.92, 1.38),
                buildWall("Wall319", 0.0, 1.38, 0.0, 2.5),
                buildWall("Wall280", 0.0, 2.5, 0.3, 2.5)).stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    private List<Point2D[]> build272Walls() {
        return Arrays.asList(buildWall("Wall264", 0.0, 0.0, 0.74, 0.0),
                buildWall("Wall265", 0.0, 0.0, 0.0, 1.38),
                buildWall("Wall267", 0.55, 1.38, 0.74, 1.38),
                buildWall("Wall254", 0.74, 1.2, 0.74, 1.38),
                buildWall("Wall252", 0.74, 0.0, 0.74, 0.69)
        ).stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    private static List<Section> buildFirstSections() {
        return Arrays.asList(buildSection(1.9428571428571428, 0.05714285714285714, 1.6285714285714286, 1.3142857142857143),
                buildSection(-0.42857142857142855, -0.2571428571428571, -0.4857142857142857, 0.2857142857142857),
                buildSection(-0.4857142857142857, 0.2857142857142857, 0.6571428571428571, 0.5428571428571428),
                buildSection(2.0285714285714285, -0.34285714285714286, 1.9714285714285713, -0.02857142857142857),
                buildSection(0.6, -0.34285714285714286, 1.0285714285714285, -0.17142857142857143));
    }

    private static Section buildSection(double... pixels) {
        Point2D[] coordinates = new Point2D[pixels.length / 2];
        for (int i = 0; i < pixels.length - 1; i += 2) {
            coordinates[i / 2] = new Point2D(pixels[i], pixels[i + 1]);
        }
        return new Section(coordinates);
    }

    private static List<Point2D[]> build286Walls() {
        return Arrays.asList(
                buildWall("Wall276", 2.35, 2.5, 2.35, 3.26),
                buildWall("Wall278", 0.92, 2.5, 1.7, 2.5),
                buildWall("Wall280", 0.0, 2.5, 0.3, 2.5),
                buildWall("Wall281", 0.0, 2.5, 0.0, 3.26),
                buildWall("Wall283", 1.1, 3.26, 2.35, 3.26))
                .stream().map(ClusterFitnessEstimatorTest::buildWallVector).collect(Collectors.toList());
    }

    protected static Point2D[] buildWallVector(Wall wall) {
        Point2D wallStart = new Point2D(wall.getFrom().getX(), wall.getFrom().getY());
        Point2D wallEnd = new Point2D(wall.getTo().getX(), wall.getTo().getY());
        return new Point2D[]{wallStart, wallEnd};
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
