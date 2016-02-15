package pl.edu.agh.capo.logic.robot;

import pl.edu.agh.capo.logic.common.Vision;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Measure {
    private final double leftVelocity;
    private final double rightVelocity;
    private final Date datetime;
    private final List<Vision> visions;

    public Measure(Date datetime, double rightVelocity, double leftVelocity, List<Vision> visions) {
        this.leftVelocity = milistoMeters(leftVelocity);
        this.rightVelocity = milistoMeters(rightVelocity);
        this.datetime = datetime;
        this.visions = visions;
    }

    private double milistoMeters(double velocity) {
        return velocity / 1000;
    }

    public double getLeftVelocity() {
        return leftVelocity;
    }

    public double getRightVelocity() {
        return rightVelocity;
    }

    public List<Vision> getVisions() {
        return new CopyOnWriteArrayList<>(visions);
    }

    public long getDatetime() {
        return datetime.getTime();
    }
}
