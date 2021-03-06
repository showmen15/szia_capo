package pl.edu.agh.capo.simulation.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.capo.common.Location;
import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.scheduler.divider.AbstractTimeDivider;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.robot.CapoRobotConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StatisticsPrinter implements IStatisticsPrinter {
    private static final String COLUMN_SEPARATOR = "\t";
    private static final Logger logger = LoggerFactory.getLogger(StatisticsPrinter.class);

    //Statistics
    private double factorMedium = 0.0;
    private double bestFactorMedium = 0.0;
    private long intervalCount = 0;
    private Coordinates bestCoordinates;
    private int jumpsCount;
    private double locationErrorSum = 0.0;
    private double alphaErrorSum = 0.0;
    private int agentCount;
    private final BufferedReader reader;

    public StatisticsPrinter(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
        try {
            reader.mark(stream.available());
        } catch (IOException e) {
            logger.error("Could not mark ideal path reader, readings will not be restarted", e);
        }
        System.out.println(buildStatisticsHeader());
    }

    private int count = 0;

    @Override
    public void printAndReset() {
        if (count == 9) {
            System.out.println(missed + COLUMN_SEPARATOR + acknoledged);
            System.exit(1);
        }
        count++;
//        System.out.println(buildStatisticsText());
        try {
            reader.reset();
        } catch (IOException e) {
            logger.error("Could not reset ideal path reader", e);
        }
//        factorMedium = 0.0;
//        intervalCount = 0;
//        bestFactorMedium = 0.0;
//        jumpsCount = 0;
//        locationErrorSum = 0.0;
//        alphaErrorSum = 0.0;
    }

    private String buildStatisticsHeader() {
        StringBuilder builder = new StringBuilder();
        //builder.append("Średnia energii/fitnesu").append(COLUMN_SEPARATOR);
        // builder.append("Średnia najlepszej energii/fitnesu").append(COLUMN_SEPARATOR);
        builder.append("Teleportacja").append(COLUMN_SEPARATOR);
        // builder.append("Ilość agentów").append(COLUMN_SEPARATOR);
        builder.append("Błąd pozycji").append(COLUMN_SEPARATOR);
        builder.append("Błąd kąta").append(COLUMN_SEPARATOR);
        return builder.toString();
    }

    private String buildStatisticsText() {
        StringBuilder builder = new StringBuilder();
        builder.append(factorMedium).append(COLUMN_SEPARATOR);
        builder.append(bestFactorMedium).append(COLUMN_SEPARATOR);
        builder.append(jumpsCount).append(COLUMN_SEPARATOR);
        builder.append(agentCount).append(COLUMN_SEPARATOR);
        builder.append(locationErrorSum / intervalCount).append(COLUMN_SEPARATOR);
        builder.append(alphaErrorSum / intervalCount).append(COLUMN_SEPARATOR);
        return builder.toString().replace('.', ',');
    }

    @Override
    public void update(AbstractTimeDivider.AgentFactorInfo best, double intervalFactorSum, int agentCount) {

        calculateError(best.getAgent());

//        this.agentCount = agentCount;
//        intervalCount++;
//        double intervalFactorMedium = intervalFactorSum / agentCount;
//        factorMedium += (intervalFactorMedium - factorMedium) / intervalCount;
//        //calculateError(best.getAgent());
//        bestFactorMedium += (best.getFactor() - bestFactorMedium) / intervalCount;
//        updateJumps(best.getAgent());
    }


    private boolean isJump(Location location, Location previousLocation) {
        Coordinates coordinates = location.getCoordinates();
        double dx = coordinates.getX() - previousLocation.positionX;
        double dy = coordinates.getY() - previousLocation.positionY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance > CapoRobotConstants.MAX_INTERVAL_DISTANCE;
    }

    private long acknoledged = 0;
    private long missed = 0;

    private void calculateError(Agent best) {
        try {
            String line = reader.readLine();
            Location bestLocation = best.getLocation();
            String[] attributes = line.split(",");
            double fitness = Double.valueOf(attributes[3]);
            Location location = fromString(attributes);
            if (fitness > 0.6) {

                locationErrorSum = location.calculateDistanceTo(bestLocation);
                alphaErrorSum = Math.abs(location.alpha - bestLocation.alpha);
                acknoledged++;
                String msg = String.format("%d\t%f\t%f", isJump(bestLocation, location) ? 1 : 0,
                        locationErrorSum, alphaErrorSum);
                System.out.println(msg);
            } else {
                missed++;
            }
        } catch (IOException e) {
            logger.error("Could not calculate error", e);
        }
    }

    public static Location fromString(String[] attributes) {
        double positionX = Double.parseDouble(attributes[0]);
        double positionY = Double.parseDouble(attributes[1]);
        double alpha = Double.parseDouble(attributes[2]);
        return new Location(positionX, positionY, alpha);
    }

    private double calculateFitness(double value, double accuracy) {
        if (value > accuracy) {
            return 0.0;
        }
        return 1.0 - (value / accuracy);
    }


    private void updateJumps(Agent best) {
        Coordinates coordinates = best.getLocation().getCoordinates();
        if (bestCoordinates != null) {
            double dx = coordinates.getX() - bestCoordinates.getX();
            double dy = coordinates.getY() - bestCoordinates.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > CapoRobotConstants.MAX_INTERVAL_DISTANCE) {
                jumpsCount++;
            }
        }
        bestCoordinates = coordinates;
    }


}
