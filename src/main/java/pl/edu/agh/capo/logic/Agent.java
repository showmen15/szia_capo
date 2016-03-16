package pl.edu.agh.capo.logic;

import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.common.Location;
import pl.edu.agh.capo.logic.common.Vision;
import pl.edu.agh.capo.logic.robot.CapoRobotMotionModel;
import pl.edu.agh.capo.logic.robot.Measure;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.maze.Gate;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class Agent {

    private static final int FITNESS_QUEUE_MAX_SIZE = 10;
    private final Random random = new Random();
    private final CapoRobotMotionModel motionModel;
    private List<Vision> visions = new ArrayList<>();
    private List<Double> angles = new CopyOnWriteArrayList<>();
    private double fitness;
    private double energySum;
    private Room room;
    private boolean isTheBest;

    private Queue<Double> fitnesses = new LinkedList<>();
    private Set<Location> locations = new HashSet<>();

    public Agent(Room room) {
        this.room = room;
        this.motionModel = createCapoRobotMotionModel();
    }

    private CapoRobotMotionModel createCapoRobotMotionModel() {
        double x = room.getMinX() + ((room.getMaxX() - room.getMinX()) / 2);
        double y = room.getMinY() + ((room.getMaxY() - room.getMinY()) / 2);
        return new CapoRobotMotionModel(x, y, 0);
    }

    public void setIsTheBest(boolean isTheBest) {
        this.isTheBest = isTheBest;
    }

    public void setMeasure(Measure measure, List<Line> lines, double deltaTimeInMillis) {
        this.visions = measure.getVisionsProbe();
        angles.clear();
        locations.clear();
        int index = 0;
        for (Line line : lines) {
            double theta = line.getTheta();
            angles.add(normalizeAlpha(theta));
            angles.add(normalizeAlpha(theta + 90));
            angles.add(normalizeAlpha(theta + 180));
            angles.add(normalizeAlpha(theta + 270));
            prepareLocations(lines, line, ++index);
        }

        if (deltaTimeInMillis > 0) {
            applyMotion(measure, deltaTimeInMillis);
        }
    }

    private synchronized void applyMotion(Measure measure, double deltaTimeInMillis) {
        Location location = motionModel.getLocationAfterTime(measure, deltaTimeInMillis);
        location.alpha = normalizeAlpha(location.alpha);
        updateLocationAndRoomIfNeeded(measure, location, deltaTimeInMillis);
    }

    private void updateLocationAndRoomIfNeeded(Measure measure, Location location, double deltaTimeInMillis) {
        Gate gate;
        if (location.positionX <= room.getMinX()) {
            gate = checkWestGates(location);
        } else if (location.positionX >= room.getMaxX()) {
            gate = checkEastGates(location);
        } else if (location.positionY <= room.getMinY()) {
            gate = checkNorthGates(location);
        } else if (location.positionY >= room.getMaxY()) {
            gate = checkSouthGates(location);
        } else {
            motionModel.applyLocation(location, measure, deltaTimeInMillis);
            //printIfBEst(measure.toString());
            return;
        }
        if (gate != null) {
            motionModel.applyLocation(location, measure, deltaTimeInMillis);
            this.room = room.getRoomBehindGate(gate);
            //rintIfBEst("Changed to " + room.getSpaceId());
        }
    }

    private void printIfBEst(String msg) {
        if (isTheBest) {
            System.out.println(msg);
        }
    }

    private Gate checkSouthGates(Location location) {
        return checkGates(room.getSouthGates(), location.positionX, this::horizontalGateStart, this::horizontalGateEnd);
    }

    private Gate checkEastGates(Location location) {
        return checkGates(room.getEastGates(), location.positionY, this::verticalGateStart, this::verticalGateEnd);
    }

    private Gate checkNorthGates(Location location) {
        return checkGates(room.getNorthGates(), location.positionX, this::horizontalGateStart, this::horizontalGateEnd);
    }

    private Gate checkWestGates(Location location) {
        return checkGates(room.getWestGates(), location.positionY, this::verticalGateStart, this::verticalGateEnd);
    }

    private double horizontalGateStart(Gate gate) {
        return Math.min(gate.getFrom().getX(), gate.getTo().getX());
    }

    private double horizontalGateEnd(Gate gate) {
        return Math.max(gate.getFrom().getX(), gate.getTo().getX());
    }

    private double verticalGateStart(Gate gate) {
        return Math.min(gate.getFrom().getY(), gate.getTo().getY());
    }

    private double verticalGateEnd(Gate gate) {
        return Math.max(gate.getFrom().getY(), gate.getTo().getY());
    }

    private Gate checkGates(List<Gate> gatesToCheck, double coordinate, Function<Gate, Double> getStart, Function<Gate, Double> getEnd) {
        for (Gate gate : gatesToCheck) {
            double start = getStart.apply(gate);
            double end = getEnd.apply(gate);
            if (numberInRange(coordinate, start, end)) {
                return gate;
            }
        }
        return null;
    }

    private boolean numberInRange(double number, double start, double end) {
        return number > start && number < end;
    }

    public Location getLocation() {
        return motionModel.getLocation();
    }

    public void setLocation(Location location) {
        motionModel.applyLocation(location);
    }

    private double normalizeAlpha(double alpha) {
        while (alpha < -180.0) {
            alpha += 360.0;
        }
        while (alpha > 180.0) {
            alpha -= 360.0;
        }
        return alpha;
    }

    public List<Vision> getVisions() {
        return visions;
    }

    public double getFitness() {
        return fitness;
    }

    public Room getRoom() {
        return room;
    }

    /**
     * estimates fitness of position based on current visions
     */
    private double estimateFitness(FitnessAnalyzer analyzer) {
        for (Vision vision : visions) {
            double result = analyzer.estimate(vision.getAngle(), vision.getDistance());
            vision.setFitness(result);
        }

        return countFitness();
    }

    /**
     * To save computation time we first try few visions to check whether calculating fitness of all visions
     * is sensible
     *
     * @param tries   nr of visions to check first
     * @param matches nr of visions that need to check out to continue computation
     */
    private double estimateFitnessByTries(FitnessAnalyzer analyzer, int tries, int matches) {
        if (tries > visions.size()) {
            return estimateFitness(analyzer);
        }

        Set<Integer> visionTriesIndexes = new HashSet<>();
        int currMatches = 0;

        int step = visions.size() / tries;
        for (int i = 0; i < visions.size(); i += step) {
            Vision vision = visions.get(i);
            double result = analyzer.estimate(vision.getAngle(), vision.getDistance());
            if (result > 0) {
                currMatches++;
            }
            vision.setFitness(result);
            visionTriesIndexes.add(i);
        }

        if (matches > currMatches) {
            return -1.0;
        }

        for (int i = 0; i < visions.size(); i++) {
            if (visionTriesIndexes.contains(i)) {
                continue;
            }

            Vision vision = visions.get(i);
            double result = analyzer.estimate(vision.getAngle(), vision.getDistance());
            vision.setFitness(result);
        }

        return countFitness();
    }

    private double countFitness() {
        double sum = 0.0;
        int count = 0;
        for (Vision vision : visions) {
            if (vision.getFitness() >= 0) {
                sum += vision.getFitness();
                count++;
            }
        }
        return sum / count;
    }

    public void estimateRandom() {
        if (locations.size() > 0) {
            Location location = locations.stream().findAny().get();
            locations.remove(location);
            //double fitness = getFitness();
            tryAndChangePositionIfBetterEstimation(location.getCoordinates(), location.alpha);
/*            if (fitness != getFitness() && getFitness() > 0.7) {
                System.out.println("Changed**************************************************************************");
            }*/
        } else {
            Coordinates coords = findRandomCoordinates();
            if (angles.size() == 0) {
                double angle = random.nextDouble() * 360 - 180;
                tryAndChangePositionIfBetterEstimation(coords, angle);
            } else {
                updateAlphaWithVisionAngles(coords);
            }
        }
    }

    private void prepareLocations(List<Line> lines, Line next, int index) {
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
        addLocation(new Location(room.getMaxX() - right.getRho(), room.getMinY() + left.getRho(), normalizeAlpha(angle)));
        addLocation(new Location(room.getMaxX() - left.getRho(), room.getMaxY() - right.getRho(), normalizeAlpha(angle)));
        addLocation(new Location(room.getMinX() + right.getRho(), room.getMaxY() - left.getRho(), normalizeAlpha(angle)));
        addLocation(new Location(room.getMinX() + left.getRho(), room.getMinY() + right.getRho(), normalizeAlpha(angle)));
    }

    private void addLocation(Location location) {
        if (room.coordinatesMatches(location.positionX, location.positionY)) {
            locations.add(location);
        }
    }

    private Coordinates findRandomCoordinates() {
        if (isTheBest) {
            return room.getRandomPositionInNeighbourhoodOf(getLocation());
        }
        return room.getRandomPosition();
    }

    private void tryAndChangePositionIfBetterEstimation(Coordinates coords, Double angle) {
        double estimated = estimateFitnessByTries(new FitnessAnalyzer(room, coords.getX(), coords.getY(), angle), 3, 2);
        changePositionIfBetterEstimation(estimated, coords, angle);
    }

    /**
     * Match coordinates with vision angles
     */
    private void updateAlphaWithVisionAngles(Coordinates coords) {
        for (Double angle : angles) {
            tryAndChangePositionIfBetterEstimation(coords, angle);
        }
    }

    private void changePositionIfBetterEstimation(double estimated, Coordinates coords, double angle) {
        if (estimated > fitness) {
            this.fitness = estimated;
            Location location = new Location(coords, angle);
            motionModel.applyLocation(location);
        }
    }

    public void recalculateEnergy() {
        fitnesses.add(fitness);
        energySum += fitness;
        if (fitnesses.size() > FITNESS_QUEUE_MAX_SIZE) {
            energySum -= fitnesses.poll();
        }
        //System.out.println(energy);
    }

    public double estimateFitness() {
        double fitness = estimateFitness(new FitnessAnalyzer(room, getLocation()));
        this.fitness = fitness;
        updateAlphaWithVisionAngles(getLocation().getCoordinates());
        return fitness;
    }

    public double getEnergy() {
        return fitnesses.size() > 0 ? energySum / fitnesses.size() : 0.0;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "room=" + room +
                '}';
    }
}
