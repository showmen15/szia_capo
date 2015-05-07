package pl.edu.agh.capo.logic.common;

import java.util.List;

public class Measure {
    private double leftVelocity;
    private double rightVelocity;
    private List<Vision> visions;

    public Measure(double leftVelocity, double rightVelocity, List<Vision> visions) {
        this.leftVelocity = leftVelocity;
        this.rightVelocity = rightVelocity;
        this.visions = visions;
    }

    public double getLeftVelocity() {
        return leftVelocity;
    }

    public double getRightVelocity() {
        return rightVelocity;
    }

    public List<Vision> getVisions() {
        return visions;
    }
}
