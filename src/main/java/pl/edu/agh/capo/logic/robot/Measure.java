package pl.edu.agh.capo.logic.robot;

import pl.edu.agh.capo.logic.common.Vision;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Measure {
    private static final int VISION_JUMP = 24;     //24 for 30 readings

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

    public List<Vision> getVisionsProbe() {
        List<Vision> filteredVisions = new ArrayList<>();
        for (int i = 0; i < visions.size(); i += VISION_JUMP) {
            filteredVisions.add(visions.get(i));
        }
        return filteredVisions;
    }

    public long getDatetime() {
        return datetime.getTime();
    }

    @Override
    public String toString() {
        return "Measure{" +
                "leftVelocity=" + leftVelocity +
                ", rightVelocity=" + rightVelocity +
                ", datetime=" + datetime +
                ", visions=" + visions +
                '}';
    }
}
