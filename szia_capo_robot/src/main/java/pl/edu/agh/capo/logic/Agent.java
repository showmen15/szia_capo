package pl.edu.agh.capo.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.common.Line;
import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.logic.estimator.PerpendicularLinesLocationEstimator;
import pl.edu.agh.capo.logic.fitness.AbstractFitnessEstimator;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.maze.Gate;
import pl.edu.agh.capo.robot.CapoRobotConstants;
import pl.edu.agh.capo.robot.CapoRobotMotionModel;
import pl.edu.agh.capo.robot.Measure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class Agent {
    private static final Logger logger = LoggerFactory.getLogger(Agent.class);
    private static final int FITNESS_QUEUE_MAX_SIZE = 15;

    private final Random random = new Random();
    private final CapoRobotMotionModel motionModel;

    private final Class<? extends AbstractFitnessEstimator> fitnessEstimatorClass;

    private PerpendicularLinesLocationEstimator locationEstimator;
    private AbstractFitnessEstimator fitnessEstimator;

    private List<Double> angles = new CopyOnWriteArrayList<>();
    private Measure measure;

    private double fitness;
    private Room room;
    private boolean isTheBest;

    private Queue<Double> fitnesses = new LinkedList<>();
    private double energy;

    public Agent(Class<? extends AbstractFitnessEstimator> fitnessEstimatorClass, Room room) {
        this.fitnessEstimatorClass = fitnessEstimatorClass;
        this.room = room;
        this.motionModel = new CapoRobotMotionModel(room.getCenter(), 0);
    }

    private AbstractFitnessEstimator buildEstimator() {
        try {
            Constructor constructor = fitnessEstimatorClass.getDeclaredConstructor(Room.class, Measure.class);
            return (AbstractFitnessEstimator) constructor.newInstance(room, measure);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            logger.error("Could not initialize FitnessEstimator, counstuctor with Room.class param is required");
            System.exit(-1);
        }
        return null;
    }

    public void setIsTheBest(boolean isTheBest) {
        this.isTheBest = isTheBest;
    }

    public void setMeasure(Measure measure, double deltaTimeInMillis) {
        angles.clear();
        this.measure = measure;
        locationEstimator = new PerpendicularLinesLocationEstimator(room);
        fitnessEstimator = buildEstimator();
        measure.getLines().forEach(this::prepareAngles);

        if (deltaTimeInMillis > 0) {
            applyMotion(measure, deltaTimeInMillis);
        }
    }

    public void prepareCalculations() {
        locationEstimator.prepareLocations(measure);
    }

    public void prepareAngles(Line line) {
        double theta = line.getTheta();
        angles.add(Location.normalizeAlpha(90 - theta));
        angles.add(Location.normalizeAlpha(180 - theta));
        angles.add(Location.normalizeAlpha(270 - theta));
        angles.add(Location.normalizeAlpha(-theta));
    }

    private synchronized void applyMotion(Measure measure, double deltaTimeInMillis) {
        Location location = motionModel.getLocationAfterTime(measure, deltaTimeInMillis);
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
            //printIfBest(measure.toString());
            return;
        }
        if (gate != null) {
            motionModel.applyLocation(location, measure, deltaTimeInMillis);
            this.room = room.getRoomBehindGate(gate);
            //printIfBEst("Changed to " + room.getSpaceId());
        }
    }

    private void printIfBest(String msg) {
        if (isTheBest) {
            logger.info(msg);
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

    public double getFitness() {
        return fitness;
    }

    public Room getRoom() {
        return room;
    }

    public boolean calculate() {
        if (locationEstimator.size() > 0) {
            Location location = locationEstimator.pop();
            //double fitness = getFitness();
            tryAndChangePositionIfBetterEstimation(location.getCoordinates(), location.alpha);
/*            if (fitness != getFitness() && getFitness() > 0.7) {
                System.out.println("Changed**************************************************************************");
            }*/
            return true;
        } else {
            return false;
        }
    }

    public void estimateRandom() {
        estimate(room.getRandomPosition());
    }

    public void estimateInNeighbourhood() {
        estimate(room.getRandomPositionInNeighbourhoodOf(getLocation()));
    }

    private void estimate(Coordinates coordinates) {
        if (angles.size() == 0) {
            double angle = random.nextDouble() * 360 - 180;
            tryAndChangePositionIfBetterEstimation(coordinates, angle);
        } else {
            updateAlphaWithVisionAngles(coordinates);
        }
    }

    private void tryAndChangePositionIfBetterEstimation(Coordinates coords, Double angle) {
        double estimated = fitnessEstimator.estimateFitnessByTries(coords, angle,
                CapoRobotConstants.ESTIMATION_TRIES, CapoRobotConstants.ESTIMATION_MATCHED_TRIES);
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
        if (fitnesses.size() > FITNESS_QUEUE_MAX_SIZE) {
            fitnesses.poll();
        }

        int i = 0;
        double sum = 0.0;
        double sum_i = 0.0;
        for (Double fitness : fitnesses) {
            i++;
            sum_i += i;
            sum += fitness * i;
        }
        energy = sum / sum_i;
    }

    public void estimateFitness() {
        fitness = fitnessEstimator.estimateFitness(getLocation());
        updateAlphaWithVisionAngles(getLocation().getCoordinates());
    }

    public double getEnergy() {
        return fitnesses.size() > 0 ? energy : 0.0;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "room=" + room +
                '}';
    }

    public void resetEnergy() {
        fitnesses.clear();
        energy = 0.0;
    }

    public Measure getMeasure() {
        return measure;
    }

}
