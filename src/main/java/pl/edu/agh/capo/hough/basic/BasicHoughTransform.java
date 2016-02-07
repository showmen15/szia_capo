package pl.edu.agh.capo.hough.basic;

import pl.edu.agh.capo.hough.HoughTransform;
import pl.edu.agh.capo.hough.common.Line;
import pl.edu.agh.capo.logic.common.Vision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicHoughTransform implements HoughTransform {
    private final static int THETA_COUNT = 180;
    public final static int SIZE = 100;


    private final static double THETA_STEP = Math.PI / THETA_COUNT;
    private final static int HALF_SIZE = SIZE / 2;
    private final static int HOUGH_SIZE = (int) (Math.sqrt(2) * HALF_SIZE);
    private final static int DOUBLE_HOUGH_SIZE = 2 * HOUGH_SIZE;
    private static final double NORMALIZATION_CONST = 6;

    private final double[] sinCache;
    private final double[] cosCache;


    private int[][] houghValues;

    public BasicHoughTransform() {
        sinCache = new double[THETA_COUNT];
        cosCache = new double[THETA_COUNT];
        for (int t = 0; t < THETA_COUNT; t++) {
            double realTheta = t * THETA_STEP;
            sinCache[t] = Math.sin(realTheta);
            cosCache[t] = Math.cos(realTheta);
        }
        houghValues = new int[THETA_COUNT][DOUBLE_HOUGH_SIZE];
    }

    public void run(List<Vision> visions) {
        visions.forEach(this::addPoint);
    }

    public void addPoint(Vision vision) {
        double x = HALF_SIZE - (((Math.cos(Math.toRadians(vision.getAngle())) * vision.getDistance()) * HALF_SIZE) / NORMALIZATION_CONST);
        double y = ((Math.sin(Math.toRadians(vision.getAngle())) * vision.getDistance() * HALF_SIZE) / NORMALIZATION_CONST) + HALF_SIZE;
        addPoint((int) x, (int) y);
    }

    private void addPoint(int x, int y) {
        for (int t = 0; t < THETA_COUNT; t++) {
            int r = (int) (((x - HALF_SIZE) * cosCache[t]) + ((y - HALF_SIZE) * sinCache[t]));
            r += HOUGH_SIZE;
            if (r < 0 || r >= DOUBLE_HOUGH_SIZE) {
                System.out.println("Should not happened: r = " + r);
                continue;
            }
            houghValues[t][r]++;
        }
    }

    public List<Line> getLines(int threshold, int max) {
        List<Result> results = new ArrayList<>();
        for (int t = 0; t < THETA_COUNT; t++) {
            for (int r = 0; r < DOUBLE_HOUGH_SIZE; r++) {
                Result result = new Result(t, r, houghValues[t][r]);
                results.add(result);
            }
        }
        List<Line> lines = new ArrayList<>();
        Collections.sort(results, (e1, e2) -> Integer.compare(e2.count, e1.count));

        while (results.get(0).count >= threshold && lines.size() < max) {
            lines.add(results.remove(0).toLine());
        }
        houghValues = new int[THETA_COUNT][DOUBLE_HOUGH_SIZE];
        return lines;
    }

    private class Result {
        private int theta;
        private int radius;
        private int count;

        public Result(int theta, int radius, int count) {
            this.theta = theta;
            this.radius = radius;
            this.count = count;
        }

        public Line toLine() {
            double realTheta = Math.toDegrees(theta * THETA_STEP);
            double realRadius = radius; //(double)(NORMALIZATION_CONST * (radius + HALF_SIZE)) / (double)HALF_SIZE;
            return new Line(realTheta, realRadius);
        }
    }
}
