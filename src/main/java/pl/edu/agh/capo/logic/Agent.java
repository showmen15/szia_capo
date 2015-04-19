package pl.edu.agh.capo.logic;

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
    private Map<Double, Double> measureResults;
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
        while (alpha < -180.0){
            alpha += 360.0;
        }
        while (alpha > 180.0){
            alpha -= 360.0;
        }
        this.alpha = alpha;
    }

    public Map<Double, Double> getVision() {
        return vision;
    }

    public Map<Double, Double> getMeasureResults() {
        return measureResults;
    }

    public double getAverageMeasureResult(){
        double sum = 0.0;
        int count = 0;
        for (double result : measureResults.values()){
            if (result >= 0){
                sum += result;
                count ++;
            }
        }
        return sum / count;
    }

    public Room getRoom(){
        return room;
    }

    public boolean analyzeMeasure() {
        measureResults = new HashMap<Double, Double>();
        try {
            MeasureAnalyzer counter = new MeasureAnalyzer(room, x, y);
            for (Map.Entry<Double, Double> singleVision : vision.entrySet()) {
                double result = counter.isMeasureFit(singleVision.getKey() + alpha, singleVision.getValue());
                measureResults.put(singleVision.getKey(), result);
            }
            return true;
        } catch (CoordinateOutOfRoomException e) {
            return false;
        } catch (AngleOutOfRangeException e) {
            return false;
        }
    }
}
