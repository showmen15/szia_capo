package pl.edu.agh.capo.robot;

import pl.edu.agh.capo.logic.fitness.AbstractFitnessEstimator;
import pl.edu.agh.capo.logic.fitness.ClusterFitnessEstimator;

public class CapoRobotConstants {
    public static final int INTERVAL_TIME = 1;      //200 ms
    public static final double INTERVAL_TIME_IN_SECONDS = 0.2; //INTERVAL_TIME/ 1000; //created for setting flexibility

    //Motion model
    public static final double MAX_LINEAR_VELOCITY = 5;//  //m/s
    public static final double MAX_ACCELERATION = 2; // m/s^2
    public static final double MAX_INTERVAL_DISTANCE = MAX_LINEAR_VELOCITY * INTERVAL_TIME_IN_SECONDS;
    public static final double WHEELS_HALF_DISTANCE = 0.14;

    //Vision model
    public static final double MAX_VISION_DISTANCE = 5.0; //m

    private static final int VISION_IMAGE_SIZE = 350;
    public static final double VISION_PER_PIXEL = (2 * MAX_VISION_DISTANCE) / VISION_IMAGE_SIZE;
    public final static double VISION_ESTIMATOR_DISTANCE_ACCURACY = 0.2; //m
    //Estimation
    public static final double NEIGHBOURHOOD_SCOPE = 0.1;

    public static final double PERPENDICULARITY_ACCURANCY = 5;  // in degrees
    public static final Class<? extends AbstractFitnessEstimator> FITNESS_ESTIMATOR_CLASS = ClusterFitnessEstimator.class;
    public static final int ESTIMATION_TRIES = 3;
    public static final int ESTIMATION_MATCHED_TRIES = 2;
    public static final double COUNT_TIME_FACTOR_MIN = 0.1;
    public static final double COUNT_TIME_FACTOR_MAX = 0.8;
    public static final double COUNT_TIME_FACTOR_RANGE_SIZE = COUNT_TIME_FACTOR_MAX - COUNT_TIME_FACTOR_MIN;
    //Line estimation
    public static final double LINE_ESTIMATOR_ANGLE_ACCURACY = 5.0;

    public static final double LINE_ESTIMATOR_RHO_ACCURACY = 0.1;
    //Cluster estimation
    public static final double ANGLE_ACCURANCY = 10.0;

    public static final double VECTOR_ACCURANCY = 0.4;
    //Hough Transforamtion
    public static final long KHT_CLUSTER_MIN_SIZE = 10;
    public static final double KHT_CLUSTER_MIN_DEVIATION = 2.0;
    public static final double KHT_DELTA = 0.5;
    public static final double KHT_KERNEL_MIN_HEIGHT = 0.002;
    public static final double KHT_N_SIGMAS = 2.0;
    public static final int HOUGH_MAX_LINES_COUNT = 6;
    public static final int HOUGH_THRESHOLD = 8;
    //Statistics
    public static final double STATISTICS_DISTANCE_ACCURANCY = 0.2;

    public static final double STATISTICS_ANGLE_ACCURANCY = 10.0;

}
