package pl.edu.agh.capo.logic.robot;

public class CapoRobotConstants {
    public static final double MAX_LINEAR_VELOCITY = 5;//  //m/s
    public static final double MAX_ACCELERATION = 1; // m/s^2
    public static final int INTERVAL_TIME = 200;      //200 ms
    public static final double INTERVAL_TIME_IN_SECONDS = 0.2;      //200 ms
    public static final double WHEELS_HALF_DISTANCE = 0.14;
    public static final double MAX_INTERVAL_DISTANCE = MAX_LINEAR_VELOCITY * INTERVAL_TIME_IN_SECONDS;
}
