package pl.edu.agh.capo.ui;

import java.util.HashMap;
import java.util.Map;

public class Agent {

    private double x;
    private double y;
    private double alpha;
    private Map<Double, Double> vision;

    public Agent(double x, double y, double alpha) {
        this.x = x;
        this.y = y;
        this.alpha = alpha;

        this.vision = new HashMap<Double, Double>();
        for (double i = -120.0; i <= 120.0; i += 8){
            vision.put(i, 0.4);
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

    public void setPosition(double x, double y, double alpha){
        this.x = x;
        this.y = y;
        this.alpha = alpha;
    }

    public Map<Double, Double> getVision() {
        return vision;
    }

    public void setVision(Map<Double, Double> vision) {
        this.vision = vision;
    }
}
