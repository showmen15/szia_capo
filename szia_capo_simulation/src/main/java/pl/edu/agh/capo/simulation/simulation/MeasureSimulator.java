package pl.edu.agh.capo.simulation.simulation;

import pl.edu.agh.capo.robot.IMeasureReader;
import pl.edu.agh.capo.robot.Measure;

import java.util.Iterator;

public class MeasureSimulator implements IMeasureReader {
    private final Iterator<Measure> measures;
    private boolean updateMeasures = false;

    public MeasureSimulator(Iterator<Measure> measures) {
        this.measures = measures;
    }

    public void setUpdateMeasures(boolean updateMeasures) {
        this.updateMeasures = updateMeasures;
    }

    @Override
    public Measure read() {
        return measures.next();
    }

    @Override
    public boolean isFinished() {
        return !measures.hasNext();
    }

    @Override
    public boolean isIdle() {
        return !updateMeasures;
    }
}
