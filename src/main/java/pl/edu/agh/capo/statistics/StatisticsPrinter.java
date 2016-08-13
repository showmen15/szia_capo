package pl.edu.agh.capo.statistics;

import pl.edu.agh.capo.logic.Agent;
import pl.edu.agh.capo.logic.common.Location;
import pl.edu.agh.capo.logic.robot.CapoRobotConstants;
import pl.edu.agh.capo.maze.Coordinates;
import pl.edu.agh.capo.scheduler.divider.TimeDivider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StatisticsPrinter implements IStatisticsPrinter {

    private final InputStream stream;
    //Statistics
    private double factorMedium = 0.0;
    private double bestFactorMedium = 0.0;
    private long intervalCount = 0;
    private Coordinates bestCoordinates;
    private int jumpsCount;
    private double locationErrorSum = 0.0;
    private double alphaErrorSum = 0.0;
    private int agentCount;
    private BufferedReader reader;

    public StatisticsPrinter(InputStream stream) {
        this.stream = stream;
        reader = new BufferedReader(new InputStreamReader(this.stream));
    }

    @Override
    public void printAndReset() {
        System.out.println(Double.toString(factorMedium).replace('.', ',') + "\t" + Double.toString(bestFactorMedium).replace('.', ',') +
                "\t" + jumpsCount + "\t" + agentCount + "\t" + Double.toString(locationErrorSum / intervalCount).replace('.', ',') + "\t" +
                Double.toString(alphaErrorSum / intervalCount).replace('.', ','));
        try {
            reader.close();
            reader = new BufferedReader(new InputStreamReader(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        factorMedium = 0.0;
        intervalCount = 0;
        bestFactorMedium = 0.0;
        jumpsCount = 0;
        locationErrorSum = 0.0;
        alphaErrorSum = 0.0;
    }

    @Override
    public void update(TimeDivider.AgentFactorInfo best, double intervalFactorSum, int agentCount) {
        this.agentCount = agentCount;
        intervalCount++;
        double intervalFactorMedium = intervalFactorSum / agentCount;
        factorMedium += (intervalFactorMedium - factorMedium) / intervalCount;
        calculateError(best.getAgent());
        bestFactorMedium += (best.getFactor() - bestFactorMedium) / intervalCount;
        updateJumps(best.getAgent());
    }


    private void calculateError(Agent best) {
        try {
            String line = reader.readLine();
            Location bestLocation = best.getLocation();
            Location location = Location.fromString(line);
            locationErrorSum += location.calculateDistanceTo(bestLocation);
            alphaErrorSum += Math.abs(location.alpha - bestLocation.alpha);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
