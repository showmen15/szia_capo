package pl.edu.agh.capo.logic;

import pl.edu.agh.capo.logic.common.Measure;
import pl.edu.agh.capo.logic.common.Vision;
import pl.edu.agh.capo.maze.Coordinates;

import java.util.*;

public class Agent {

    private double x;
    private double y;
    private double alpha;

    private List<Vision> visions = new ArrayList<>();

    private double fitness;
    private Room room;

    private final Random random = new Random();

    public Agent(Room room) {
        this.alpha = 0;
        this.room = room;
        x = room.getMinX() + ((room.getMaxX() - room.getMinX()) / 2);
        y = room.getMinY() + ((room.getMaxY() - room.getMinY()) / 2);
    }

    public void setMeasure(Measure measure) {
        this.visions = measure.getVisions();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        while (alpha < -180.0) {
            alpha += 360.0;
        }
        while (alpha > 180.0) {
            alpha -= 360.0;
        }
        this.alpha = alpha;
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

    private double estimateFitnessByTries(FitnessAnalyzer analyzer, int tries, int matches) {
        if (tries > visions.size()){
            return estimateFitness(analyzer);
        }

        Set<Integer> visionTriesIndexes = new HashSet<>();
        int currMatches = 0;

        int step = visions.size() / tries;
        for (int i = 0; i < visions.size(); i += step){
            Vision vision = visions.get(i);
            double result = analyzer.estimate(vision.getAngle(), vision.getDistance());
            if (result > 0){
                currMatches ++;
            }
            vision.setFitness(result);
            visionTriesIndexes.add(i);
        }

        if (matches > currMatches){
            return -1.0;
        }

        for (int i = 0; i < visions.size(); i++){
            if (visionTriesIndexes.contains(i)){
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

    public double estimateRandom() {
        Coordinates coords = room.getRandomPosition();
        double angle = random.nextDouble() * 360 - 180;
        double estimated = estimateFitnessByTries(new FitnessAnalyzer(room, coords.getX(), coords.getY(), angle), 3, 2);
        if (estimated > fitness) {
            fitness = estimated;
            x = coords.getX();
            y = coords.getY();
            alpha = angle;
        }
        return fitness;
    }

    public double estimateFitness() {
        fitness = estimateFitness(new FitnessAnalyzer(room, x, y, alpha));
        return fitness;
    }

}
