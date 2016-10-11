package pl.edu.agh.capo.logic.fitness.analyzer;

import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.robot.CapoRobotConstants;

import java.util.List;

public class SectionExtensionClusterFitnessAnalyzer extends ClusterFitnessAnalyzer {
    public SectionExtensionClusterFitnessAnalyzer(Room room, List<Point2D[]> wallVectors, List<Point2D[]> gateVectors, Point2D coordinates, double angle) {
        super(room, wallVectors, gateVectors, coordinates, angle);
    }

    @Override
    protected boolean wallCoordinatesMatchVisionSection(Point2D[] visionSection, Point2D[] wall, boolean wallStartInvisible, boolean wallEndInvisible) {
        return true;
    }

    @Override
    protected double sectionShiftInEndDirectionAward(Point2D[] visionSection, Point2D[] wall, boolean wallStartInvisible) {
        Point2D[] missingEndSection = {wall[1], visionSection[1]}; //todo: too simple?
        boolean endExceeds = new Vector2D(missingEndSection[0], missingEndSection[1]).norm() > CapoRobotConstants.VECTOR_ACCURANCY;
        if (!endExceeds) {
            return calculateAward(vectorEE.norm(), perdicularVectorNorm(vectorSS, wall));
        } else {
            double middleAward = calculateAward(perdicularVectorNorm(vectorSS, wall), perdicularVectorNorm(vectorEE, wall));
            return divideAndCalculateStartAward(missingEndSection, middleAward, getLength(wall));
        }
    }

    @Override
    protected double sectionShiftInStartDirectionAward(Point2D[] visionSection, Point2D[] wall, boolean wallEndInvisible) {
        Point2D[] missingStartSection = {visionSection[0], wall[0]}; //todo: too simple?
        boolean startExceeds = new Vector2D(missingStartSection[0], missingStartSection[1]).norm() > CapoRobotConstants.VECTOR_ACCURANCY;
        if (!startExceeds) {
            return calculateAward(vectorSS.norm(), perdicularVectorNorm(vectorEE, wall));
        } else {
            double middleAward = calculateAward(perdicularVectorNorm(vectorSS, wall), perdicularVectorNorm(vectorEE, wall));
            return divideAndCalculateStartAward(missingStartSection, middleAward, getLength(wall));
        }
    }

    @Override
    protected double wallIncludesSectionAward(Point2D[] wall, boolean wallEndInvisible, boolean wallEndInvisible2) {
        return calculateAward(perdicularVectorNorm(vectorSS, wall), perdicularVectorNorm(vectorEE, wall));
    }

}
