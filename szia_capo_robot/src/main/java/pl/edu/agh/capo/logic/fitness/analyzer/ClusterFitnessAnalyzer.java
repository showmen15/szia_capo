package pl.edu.agh.capo.logic.fitness.analyzer;

import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import org.apache.commons.lang.ArrayUtils;
import pl.edu.agh.capo.logic.Room;
import pl.edu.agh.capo.robot.CapoRobotConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ClusterFitnessAnalyzer {
    protected final Room room;
    private final List<Point2D[]> wallVectors;
    private final List<Point2D[]> gateVectors;
    protected final Point2D coordinates;
    protected final double angle;

    protected Vector2D vectorSS;  //start wall -> start reading
    protected Vector2D vectorSE;
    protected Vector2D vectorES;
    protected Vector2D vectorEE;  //end wall -> end reading

    protected double vectorSSAngle;
    protected double vectorSEAngle;
    protected double vectorESAngle;
    protected double vectorEEAngle;


    public ClusterFitnessAnalyzer(Room room, List<Point2D[]> wallVectors, List<Point2D[]> gateVectors, Point2D coordinates, double angle) {
        this.room = room;
        this.wallVectors = wallVectors;
        this.gateVectors = gateVectors;
        this.coordinates = coordinates;
        this.angle = angle;
    }

    public static double estimateFitness(Room room, List<Point2D[]> visionSections, List<Point2D[]> wallVectors,
                                         List<Point2D[]> gateVectors, Point2D coordinates, double horizontalAngle,
                                         boolean extendsSection) {
        ClusterFitnessAnalyzer analyzer = extendsSection ?
                new SectionExtensionClusterFitnessAnalyzer(room, wallVectors, gateVectors, coordinates, horizontalAngle) :
                new ClusterFitnessAnalyzer(room, wallVectors, gateVectors, coordinates, horizontalAngle);

        double sum = 0.0, sumLength = 0.0;
        for (Point2D[] visionSection : visionSections) {
            double estimate = analyzer.checkWalls(visionSection);
            estimate = Math.max(estimate, analyzer.checkGates(visionSection));
            double visionSectionLength = getLength(visionSection);
            sum += estimate * visionSectionLength;
            sumLength += visionSectionLength;
        }

        return sum / sumLength;
    }

    public double checkGates(Point2D[] visionSection) {
        double best = 0.0;
        for (Point2D[] gate : gateVectors) {
            Point2D[] visionStartVector = {coordinates, visionSection[0]};
            Point2D[] visionEndVector = {coordinates, visionSection[1]};
            if (segmentsIntersect(gate, visionStartVector) && segmentsIntersect(gate, visionEndVector)) {
                Room nextRoom = room.getRoomBehindGate(gate);
                List<Point2D[]> filteredGates = nextRoom.getGateVectors().stream().filter(nextGate -> !Arrays.equals(gate, nextGate))
                        .collect(Collectors.toList());
                best = Math.max(best, estimateFitness(nextRoom, Collections.singletonList(visionSection), nextRoom.getWallVectors(),
                        filteredGates, coordinates, angle, true));
            }
        }
        return best;
    }

    private static boolean segmentsIntersect(Point2D[] seg1, Point2D[] seg2) {
        double a0x = seg1[0].x(), a0y = seg1[0].y(), a1x = seg1[1].x(), a1y = seg1[1].y(),
                b0x = seg2[0].x(), b0y = seg2[0].y(), b1x = seg2[1].x(), b1y = seg2[1].y();

        double d = (b1y - b0y) * (a1x - a0x) - (b1x - b0x) * (a1y - a0y);

        if (d == 0.0)
            return false; // Parallel lines

        double uA = ((b1x - b0x) * (a0y - b0y) - (b1y - b0y) * (a0x - b0x)) / d;
        double uB = ((a1x - a0x) * (a0y - b0y) - (a1y - a0y) * (a0x - b0x)) / d;

        return !(uA < 0 || uA > 1 || uB < 0 || uB > 1);

    }

    public double checkWalls(Point2D[] visionSection) {
        double best = 0.0;
        double sectionAngle = getAngle(visionSection);
        for (Point2D[] wall : wallVectors) {
            boolean wallStartInvisible = !isPointVisible(coordinates, angle, wall[0]);
            boolean wallEndInvisible = !isPointVisible(coordinates, angle, wall[1]);
            if (wallEndInvisible && wallStartInvisible) {
                continue;
            }

            double wallAngle = getAngle(wall);
            double angleDiff = angleBetweenTwoVectors(wallAngle, sectionAngle);
            double invertedSectionAngle = sectionAngle + 180;
            invertedSectionAngle = invertedSectionAngle > 360 ? invertedSectionAngle - 360 : invertedSectionAngle;
            double invertedAngleDiff = angleBetweenTwoVectors(wallAngle, invertedSectionAngle);

            if (invertedAngleDiff < angleDiff) {
                sectionAngle = invertedSectionAngle;
                angleDiff = invertedAngleDiff;
                ArrayUtils.reverse(visionSection);
            }

            if (angleDiff < CapoRobotConstants.ANGLE_ACCURANCY && wallMatchesVisionSection(visionSection, wall, wallStartInvisible, wallEndInvisible)) {
                double award = 0.0;
                boolean visionStartEqualsWall = wall[0].equals(visionSection[0]);
                boolean visionEndEqualsWall = wall[1].equals(visionSection[1]);
                if (visionEndEqualsWall && visionStartEqualsWall) {
                    return 1.0;
                }
                setupVectors(visionSection, wall);
                //print(wall[0], wall[1], visionSection);

                if (inDifferentDirection(vectorESAngle, vectorSEAngle)) {
                    if (wallIncludesSection(visionStartEqualsWall, visionEndEqualsWall)) {
                        //#1
                        award = wallIncludesSectionAward(wall, wallStartInvisible, wallEndInvisible);
                    } else if (sectionIncludesWall(visionStartEqualsWall, visionEndEqualsWall)) {
                        //#2
                        award = sectionIncludesWallAward(visionSection, wall);
                    } else if (sectionShiftInStartDirection()) {
                        //#3
                        award = sectionShiftInStartDirectionAward(visionSection, wall, wallEndInvisible);
                    } else if (sectionShiftInEndDirection()) {
                        //#3 reversed
                        award = sectionShiftInEndDirectionAward(visionSection, wall, wallStartInvisible);
                    }
                }
                if (award > best) {
                    best = award;
                }
            }
        }
        return best;
    }

    protected double wallIncludesSectionAward(Point2D[] wall, boolean wallStartInvisible, boolean wallEndInvisible) {
        if (wallEndInvisible) {
            return calculateAward(vectorSS.norm(), perdicularVectorNorm(vectorEE, wall));
        } else if (wallStartInvisible) {
            return calculateAward(vectorEE.norm(), perdicularVectorNorm(vectorSS, wall));
        }
        return calculateAward(vectorSS.norm(), vectorEE.norm());
    }

    private double sectionIncludesWallAward(Point2D[] visionSection, Point2D[] wall) {
        return divideAndCalculateAward(visionSection, wall);
    }

    protected double sectionShiftInEndDirectionAward(Point2D[] visionSection, Point2D[] wall, boolean wallStartInvisible) {
        if (wallStartInvisible) {
            Point2D[] missingEndSection = {wall[1], visionSection[1]}; //todo: too simple?
            boolean endExceeds = new Vector2D(missingEndSection[0], missingEndSection[1]).norm() > CapoRobotConstants.VECTOR_ACCURANCY;
            if (!endExceeds) {
                return calculateAward(vectorEE.norm(), perdicularVectorNorm(vectorSS, wall));
            } else {
                double middleAward = calculateAward(perdicularVectorNorm(vectorEE, wall), perdicularVectorNorm(vectorSS, wall));
                return divideAndCalculateStartAward(missingEndSection, middleAward, getLength(wall));
            }
        } else {
            Point2D[] missingEndSection = {wall[1], visionSection[1]}; //todo: too simple?
            boolean endExceeds = new Vector2D(missingEndSection[0], missingEndSection[1]).norm() > CapoRobotConstants.VECTOR_ACCURANCY;
            if (!endExceeds) {
                return calculateAward(vectorSS.norm(), vectorEE.norm());
            } else {
                Point2D[] middleSection = new Point2D[]{wall[0], visionSection[1]};
                double middleAward = calculateAward(vectorSS.norm(), perdicularVectorNorm(vectorEE, wall));
                return divideAndCalculateStartAward(missingEndSection, middleAward, getLength(middleSection));
            }
        }
    }

    protected double sectionShiftInStartDirectionAward(Point2D[] visionSection, Point2D[] wall, boolean wallEndInvisible) {
        //todo: visibility vector
        if (wallEndInvisible) {
            Point2D[] missingStartSection = {visionSection[0], wall[0]}; //todo: too simple?
            boolean startExceeds = new Vector2D(missingStartSection[0], missingStartSection[1]).norm() > CapoRobotConstants.VECTOR_ACCURANCY;
            if (!startExceeds) {
                return calculateAward(vectorSS.norm(), perdicularVectorNorm(vectorEE, wall));
            } else {
                double middleAward = calculateAward(perdicularVectorNorm(vectorSS, wall), perdicularVectorNorm(vectorEE, wall));
                return divideAndCalculateStartAward(missingStartSection, middleAward, getLength(wall));
            }
        } else {
            Point2D[] missingStartSection = {visionSection[0], wall[0]}; //todo: too simple?
            boolean startExceeds = new Vector2D(missingStartSection[0], missingStartSection[1]).norm() > CapoRobotConstants.VECTOR_ACCURANCY;
            if (!startExceeds) {
                return calculateAward(vectorSS.norm(), vectorEE.norm());
            } else {
                Point2D[] middleSection = new Point2D[]{wall[0], visionSection[1]};
                double middleAward = calculateAward(perdicularVectorNorm(vectorSS, wall), vectorEE.norm());
                return divideAndCalculateStartAward(missingStartSection, middleAward, getLength(middleSection));
            }
        }
    }

    protected double divideAndCalculateStartAward(Point2D[] missingStartSection, double middleAward, double middleNorm) {
        double[] awards = estimateVectors(missingStartSection);
        double startNorm = distanceBetween(missingStartSection[0], missingStartSection[1]);
        return (awards[0] * startNorm + middleAward * middleNorm) / (startNorm + middleNorm);
    }

    protected static double getLength(Point2D[] vector) {
        return distanceBetween(vector[0], vector[1]);
    }

    private static double distanceBetween(Point2D coordinates1, Point2D coordinates2) {
        double dx = coordinates1.x() - coordinates2.x();
        double dy = coordinates1.y() - coordinates2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double divideAndCalculateAward(Point2D[] visionSection, Point2D[] wall) {
        Point2D[] missingStartSection = {visionSection[0], wall[0]}; //todo: too simple?
        Point2D[] missingEndSection = {wall[1], visionSection[1]}; //todo: too simple?

        double startNorm = new Vector2D(missingStartSection[0], missingStartSection[1]).norm();
        boolean startExceeds = startNorm > 0.4;
        double endNorm = new Vector2D(missingEndSection[0], missingEndSection[1]).norm();
        boolean endExceeds = endNorm > 0.4;

        double[] awards;
        double middleAward, normSum;

        if (startExceeds) {
            if (endExceeds) {
                awards = estimateVectors(missingStartSection, missingEndSection);
                awards[0] *= startNorm;
                awards[1] *= endNorm;
                normSum = startNorm + endNorm;
                middleAward = calculateAward(perdicularVectorNorm(vectorSS, wall), perdicularVectorNorm(vectorEE, wall));
            } else {
                awards = estimateVectors(missingStartSection);
                awards[0] *= startNorm;
                normSum = startNorm;
                middleAward = calculateAward(vectorSS.norm(), perdicularVectorNorm(vectorEE, wall));
            }
        } else if (endExceeds) {
            awards = estimateVectors(missingEndSection);
            awards[0] *= endNorm;
            normSum = endNorm;
            middleAward = calculateAward(perdicularVectorNorm(vectorSS, wall), vectorEE.norm());
        } else {
            return calculateAward(vectorSS.norm(), vectorEE.norm());
        }

        double sum = 0.0;
        for (double award : awards) {
            sum += award;
        }
        double middleNorm = getLength(wall);
        return (sum + middleAward * middleNorm) / (normSum + middleNorm);
    }

    protected double[] estimateVectors(Point2D[]... sections) {
        double[] awards = new double[sections.length];
        for (Room nextRoom : room.getRooms()) {
            for (int i = 0; i < sections.length; i++) {
                awards[i] = Math.max(awards[i], estimateFitness(nextRoom, Collections.singletonList(sections[i]),
                        nextRoom.getWallVectors(), nextRoom.getGateVectors(), coordinates, angle, true));
            }
        }
        return awards;
    }

    protected static double calculateAward(double... vectorNorms) {
        double sum = 0.0;
        for (double norm : vectorNorms) {
            sum += norm;
        }
        if (sum > CapoRobotConstants.VECTOR_ACCURANCY) {
            return 0.0;
        }
        return 1.0 - sum / CapoRobotConstants.VECTOR_ACCURANCY;
    }

    protected static double perdicularVectorNorm(Vector2D vector, Point2D[] wall) {
        double angleBetween = Math.toRadians(calculateAngleDiff(getAngle(vector), getAngle(wall)));
        return Math.sin(angleBetween) * vector.norm();
    }

    private static double calculateAngleDiff(double angle1, double angle2) {
        double invertedAngle = angle1 + 180;
        invertedAngle = invertedAngle > 360 ? invertedAngle - 360 : invertedAngle;
        double closeToZeroDiff = angle1 > angle2 ? angle1 - angle2 - 360 : angle2 - angle1 - 360;
        return Math.min(Math.abs(closeToZeroDiff), Math.min(Math.abs(angle1 - angle2),
                Math.abs(invertedAngle - angle2)));
    }

    private boolean sectionShiftInStartDirection() {
        return inSameDirection(vectorSSAngle, vectorEEAngle, vectorESAngle) && inDifferentDirection(vectorSEAngle, vectorSSAngle, vectorEEAngle);
    }

    private boolean sectionShiftInEndDirection() {
        return inSameDirection(vectorSSAngle, vectorEEAngle, vectorSEAngle) && inDifferentDirection(vectorESAngle, vectorSSAngle, vectorEEAngle);
    }

    private boolean sectionIncludesWall(boolean visionStartEqualsWall, boolean visionEndEqualsWall) {
        return (visionStartEqualsWall || (inSameDirection(vectorSSAngle, vectorESAngle) && inDifferentDirection(vectorSEAngle, vectorSSAngle))) &&
                (visionEndEqualsWall || (inSameDirection(vectorSEAngle, vectorEEAngle) && inDifferentDirection(vectorESAngle, vectorEEAngle))) &&
                ((visionEndEqualsWall || visionStartEqualsWall) || inDifferentDirection(vectorEEAngle, vectorSSAngle));
    }

    private void setupVectors(Point2D[] visionSection, Point2D[] wall) {
        vectorSS = new Vector2D(wall[0], visionSection[0]);   //start wall -> start reading
        vectorSE = new Vector2D(wall[0], visionSection[1]);
        vectorES = new Vector2D(wall[1], visionSection[0]);
        vectorEE = new Vector2D(wall[1], visionSection[1]);    //end wall -> end reading
        vectorSSAngle = getAngle(vectorSS);
        vectorSEAngle = getAngle(vectorSE);
        vectorESAngle = getAngle(vectorES);
        vectorEEAngle = getAngle(vectorEE);
    }

    private boolean wallIncludesSection(boolean visionStartEqualsWall, boolean visionEndEqualsWall) {
        return (visionStartEqualsWall || (inSameDirection(vectorSSAngle, vectorSEAngle) && inDifferentDirection(vectorSSAngle, vectorESAngle))) &&
                (visionEndEqualsWall || (inSameDirection(vectorEEAngle, vectorESAngle) && inDifferentDirection(vectorEEAngle, vectorSEAngle))) &&
                ((visionEndEqualsWall || visionStartEqualsWall) || inDifferentDirection(vectorSSAngle, vectorEEAngle));
    }

    private static boolean inDifferentDirection(double angle1, double... angles) {
        for (double angle2 : angles) {
            double angleDiff = angleBetweenTwoVectors(angle1, angle2);
            if (angleDiff <= 90) {
                return false;
            }
        }
        return true;
    }

    protected static boolean inSameDirection(double... angles) {
        int size = angles.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                double angleDiff = angleBetweenTwoVectors(angles[i], angles[j]);
                if (angleDiff > 90) {
                    return false;
                }
            }
        }
        return true;
    }

    private static double angleBetweenTwoVectors(double angle1, double angle2) {
        double closeToZeroDiff = angle1 > angle2 ? angle1 - angle2 - 360 : angle2 - angle1 - 360;
        return Math.min(Math.abs(closeToZeroDiff), Math.abs(angle1 - angle2));
    }

    private static boolean isPointVisible(Point2D coordinates, double angle, Point2D point2D) {
        double vectorAngle = getAngle(new Point2D[]{coordinates, point2D});
        return angleBetweenTwoVectors(vectorAngle, angle) < 120;
    }

    private static double getAngle(Point2D[] visionSection) {
        Vector2D lineVector = new Vector2D(visionSection[0], visionSection[1]);
        return getAngle(lineVector);
    }

    private static double getAngle(Vector2D vector2D) {
        return 360 - Math.toDegrees(vector2D.angle());
    }

    private boolean wallMatchesVisionSection(Point2D[] visionSection, Point2D[] wall, boolean wallStartInvisible, boolean wallEndInvisible) {
        return !(wall[0].equals(visionSection[1]) || wall[1].equals(visionSection[0])) &&
                wallCoordinatesMatchVisionSection(visionSection, wall, wallStartInvisible, wallEndInvisible);

    }

    //todo: if still OR needed
    protected boolean wallCoordinatesMatchVisionSection(Point2D[] visionSection, Point2D[] wall, boolean wallStartInvisible, boolean wallEndInvisible) {
        return ((wallStartInvisible || coordinatesDiffMatchesAccuracy(wall[0], visionSection[0])) ||
                (wallEndInvisible || coordinatesDiffMatchesAccuracy(wall[1], visionSection[1])));
    }

    private static boolean coordinatesDiffMatchesAccuracy(Point2D point2D, Point2D point2D1) {
        return (Math.abs(point2D.x() - point2D1.x()) < CapoRobotConstants.VECTOR_ACCURANCY) &&
                (Math.abs(point2D.y() - point2D1.y()) < CapoRobotConstants.VECTOR_ACCURANCY);
    }

    private static void print(Point2D wallStart, Point2D wallEnd, Point2D[] visionSection) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("****************************************");
        stringBuilder.append("\n");
        stringBuilder.append(wallStart.x() + "\t" + wallStart.y());
        stringBuilder.append("\n");
        stringBuilder.append(wallEnd.x() + "\t" + wallEnd.y());
        stringBuilder.append("\n");
        stringBuilder.append(visionSection[0].x() + "\t" + visionSection[0].y());
        stringBuilder.append("\n");
        stringBuilder.append(visionSection[1].x() + "\t" + visionSection[1].y());
        stringBuilder.append("\n");
        stringBuilder.append(wallStart.x() + "\t" + wallStart.y());
        stringBuilder.append("\n");
        stringBuilder.append(visionSection[0].x() + "\t" + visionSection[0].y());   //start wall -> start reading
        stringBuilder.append("\n");
        stringBuilder.append(wallStart.x() + "\t" + wallStart.y());
        stringBuilder.append("\n");
        stringBuilder.append(visionSection[1].x() + "\t" + visionSection[1].y());   //start wall -> start reading
        stringBuilder.append("\n");
        stringBuilder.append(wallEnd.x() + "\t" + wallEnd.y());
        stringBuilder.append("\n");
        stringBuilder.append(visionSection[0].x() + "\t" + visionSection[0].y());   //start wall -> start reading
        stringBuilder.append("\n");
        stringBuilder.append(wallEnd.x() + "\t" + wallEnd.y());
        stringBuilder.append("\n");
        stringBuilder.append(visionSection[1].x() + "\t" + visionSection[1].y());   //start wall -> start reading
        stringBuilder.append("\n");
        String text = stringBuilder.toString().replaceAll("\\.", ",");
        System.out.println(text);
    }
}
