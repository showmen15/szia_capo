package pl.edu.agh.capo.hough;

public class Line {
    private double theta;
    private double radius;

    public Line(double theta, double radius) {
        this.theta = theta;
        this.radius = radius;
    }

    public double getTheta() {
        return theta;
    }

    public double getRadius() {
        return radius;
    }
}
