package pl.edu.agh.capo.logic.fitness;

import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.common.Vision;
import pl.edu.agh.capo.maze.Coordinates;

import java.util.List;

public abstract class AbstractFitnessEstimator {
    public abstract double estimateFitnessByTries(Coordinates coords, Double angle, List<Vision> visions, int tries, int matches);

    public abstract double estimateFitness(Location location, List<Vision> visions);
}
