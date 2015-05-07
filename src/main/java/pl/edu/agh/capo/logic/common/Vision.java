package pl.edu.agh.capo.logic.common;

public class Vision {

    private final double distance;
    private final double angle;
    private double fitness;

    public Vision(double angle, double distance) {
        this.distance = distance;
        this.angle = angle;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getDistance() {
        return distance;
    }

    public double getAngle() {
        return angle;
    }

    public double getFitness() {
        return fitness;
    }
}
