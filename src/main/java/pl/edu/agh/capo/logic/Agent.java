package pl.edu.agh.capo.logic;

import pl.edu.agh.capo.logic.common.MeasureResult;
import pl.edu.agh.capo.logic.common.MeasurementReader;
import pl.edu.agh.capo.logic.exception.AngleOutOfRangeException;
import pl.edu.agh.capo.logic.exception.CoordinateOutOfRoomException;

import java.util.HashMap;
import java.util.Map;

public class Agent {

    private double x;
    private double y;
    private double alpha;
    private Map<Double, Double> vision;
    private Map<Double, MeasureResult> measureResults;
    private Map<MeasureResult, Integer> measureCounts;
    private Room room;

    public Agent(Room room) {
        this.alpha = 0;
        this.room = room;
        x = room.getMinX() + ((room.getMaxX() - room.getMinX())/2);
        y = room.getMinY() + ((room.getMaxY() - room.getMinY())/2);
    }

    public void setMeasure(MeasurementReader.Measure measure){
        this.vision = new HashMap<Double, Double>();
        for (Map.Entry<Double, Double> singleVision : measure.getVision().entrySet()){
            vision.put(singleVision.getKey(), singleVision.getValue() / 1000);
        }
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
        this.alpha = alpha;
    }

    public Map<Double, Double> getVision() {
        return vision;
    }

    public Map<Double, MeasureResult> getMeasureResults() {
        return measureResults;
    }

    public Map<MeasureResult, Integer> getMeasureCounts() {
        return measureCounts;
    }

    public Room getRoom(){
        return room;
    }

    public void analyzeMeasure() {
        measureResults = new HashMap<Double, MeasureResult>();
        measureCounts = new HashMap<MeasureResult, Integer>();

        measureCounts.put(MeasureResult.VALID, 0);
        measureCounts.put(MeasureResult.INVALID, 0);
        measureCounts.put(MeasureResult.IGNORE, 0);

        try {
            MeasureAnalyzer counter = new MeasureAnalyzer(room, x, y);
            for (Map.Entry<Double, Double> singleVision : vision.entrySet()) {
                MeasureResult measureResult = counter.isMeasureFit(singleVision.getKey() + alpha, singleVision.getValue());
                measureResults.put(singleVision.getKey(), measureResult);
                measureCounts.put(measureResult, measureCounts.get(measureResult) + 1);
            }
        } catch (CoordinateOutOfRoomException e) {
            measureCounts.put(MeasureResult.VALID, -1);
            measureCounts.put(MeasureResult.INVALID, -1);
            measureCounts.put(MeasureResult.IGNORE, -1);
        } catch (AngleOutOfRangeException e) {
            measureCounts.put(MeasureResult.VALID, -2);
            measureCounts.put(MeasureResult.INVALID, -2);
            measureCounts.put(MeasureResult.IGNORE, -2);
        }
    }
}
