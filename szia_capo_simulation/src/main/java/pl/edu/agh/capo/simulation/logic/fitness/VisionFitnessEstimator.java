package pl.edu.agh.capo.simulation.logic.fitness;

import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.common.Vision;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.logic.fitness.AbstractFitnessEstimator;
import pl.edu.agh.capo.maze.Coordinates;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisionFitnessEstimator extends AbstractFitnessEstimator {
    private final Room room;

    public VisionFitnessEstimator(Room room) {
        this.room = room;
    }

    /**
     * estimates fitness of position based on current visions
     */
    private double estimateFitness(Coordinates coords, Double angle, List<Vision> visions) {
        VisionFitnessAnalyzer analyzer = new VisionFitnessAnalyzer(room, coords.getX(), coords.getY(), angle);
        for (Vision vision : visions) {
            double result = analyzer.estimate(vision.getAngle(), vision.getDistance());
            vision.setFitness(result);
        }

        return countFitness(visions);
    }

    /**
     * To save computation time we first try few visions to check whether calculating fitness of all visions
     * is sensible
     *
     * @param tries   nr of visions to check first
     * @param matches nr of visions that need to check out to continue computation
     */
    @Override
    public double estimateFitnessByTries(Coordinates coords, Double angle, List<Vision> visions, int tries, int matches) {
        if (tries > visions.size()) {
            return estimateFitness(coords, angle, visions);
        }

        VisionFitnessAnalyzer analyzer = new VisionFitnessAnalyzer(room, coords.getX(), coords.getY(), angle);
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

        return countFitness(visions);
    }

    @Override
    public double estimateFitness(Location location, List<Vision> visions) {
        return estimateFitness(location.getCoordinates(), location.alpha, visions);
    }

    private double countFitness(List<Vision> visions) {
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
}
