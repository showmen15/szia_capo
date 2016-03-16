package pl.edu.agh.capo.logic.robot;

public class CapoRobotConstants {
    public static final int INTERVAL_TIME = 20;      //200 ms

    //Motion model
    public static final double MAX_LINEAR_VELOCITY = 5;//  //m/s
    public static final double MAX_ACCELERATION = 2; // m/s^2
    public static final double INTERVAL_TIME_IN_SECONDS = 0.2; //INTERVAL_TIME/ 1000; //created for setting flexibility
    public static final double MAX_INTERVAL_DISTANCE = MAX_LINEAR_VELOCITY * INTERVAL_TIME_IN_SECONDS;
    public static final double WHEELS_HALF_DISTANCE = 0.14;

    //Vision model
    public static final double MAX_VISION_DISTANCE = 5.0; //m
    public static final int VISION_IMAGE_SIZE = 250;
    public final static double VISION_ACCURACY = 0.2; //m

    //Estimation
    public static final double NEIGHBOURHOOD_SCOPE = 0.3;
    public static final double PERPENDICULARITY_ACCURANCY = 5;  // in degrees

    //Hough Transforamtion
    public static final long KHT_CLUSTER_MIN_SIZE = 5;
    public static final double KHT_CLUSTER_MIN_DEVIATION = 1.0;
    public static final double KHT_DELTA = 0.5;
    public static final double KHT_KERNEL_MIN_HEIGHT = 0.002;
    public static final double KHT_N_SIGMAS = 2.0;
    public static final int HOUGH_MAX_LINES_COUNT = 6;
    public static final int HOUGH_THRESHOLD = 8;
}
