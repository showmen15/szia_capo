package pl.edu.agh.capo.hough.common;

public class Line {
    private double theta;
    private double rho;

    public Line(double theta, double rho) {
        this.theta = theta;
        this.rho = rho;
    }

    public double getTheta() {
        return theta;
    }

    /**
     * Hough uses mirrored coordinate system
     */
    public double getMirroredTheta() {
        return 180 - theta;
    }

    public double getRho() {
        return rho;
    }
}
