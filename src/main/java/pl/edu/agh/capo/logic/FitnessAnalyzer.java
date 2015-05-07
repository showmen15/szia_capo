package pl.edu.agh.capo.logic;

import pl.edu.agh.capo.maze.Gate;

import java.util.List;

public class FitnessAnalyzer {

    private final static double ACCURACY = 0.2;

    private Room room;
    private double x;
    private double y;
    private double angle;

    private double angleNW;
    private double angleNE;
    private double angleSE;
    private double angleSW;

    public FitnessAnalyzer(Room room, double x, double y, double angle) {
        this.room = room;
        this.x = x;
        this.y = y;
        this.angle = angle;

        findLimitAngle();
    }

    private void findLimitAngle() {
        angleNW = -Math.toDegrees(Math.atan((x - room.getMinX()) / (y - room.getMinY())));
        angleNE = Math.toDegrees(Math.atan((room.getMaxX() - x) / (y - room.getMinY())));
        angleSE = 90.0 + Math.toDegrees(Math.atan((room.getMaxY() - y) / (room.getMaxX() - x)));
        angleSW = -(90.0 + Math.toDegrees(Math.atan((room.getMaxY() - y) / (x - room.getMinX()))));
    }


    private double normalizeAngle(double angle) {
        double result = angle;
        if (angle < -180.0) {
            result = angle + 360.0;
        }
        if (angle > 180) {
            result = angle - 360.0;
        }
        return result;
    }

    public double estimate(double angle, double distance) {
        double alpha = normalizeAngle(angle + this.angle);
        if (alpha > 0) {
            if (alpha == 180.0) {
                return checkMeasureHorizontally(x, y, x, room.getMaxY(), distance, room.getSouthGates());
            } else if (alpha > angleSE) {
                double x1 = x - (Math.tan(Math.toRadians(alpha)) * (room.getMaxY() - y));
                return checkMeasureHorizontally(x, y, x1, room.getMaxY(), distance, room.getSouthGates());
            } else if (alpha == angleSE) {
                return isMeasureWithAccuracy(distance, getDistance(x, y, room.getMaxX(), room.getMaxY()));
            } else if (alpha > 90.0) {
                double y1 = y + (Math.tan(Math.toRadians(alpha - 90.0)) * (room.getMaxX() - x));
                return checkMeasureVerically(x, y, room.getMaxX(), y1, distance, room.getEastGates());
            } else if (alpha == 90.0) {
                return checkMeasureHorizontally(x, y, room.getMaxX(), y, distance, room.getEastGates());
            } else if (alpha > angleNE) {
                double y1 = y - (Math.tan(Math.toRadians(90.0 - alpha)) * (room.getMaxX() - x));
                return checkMeasureVerically(x, y, room.getMaxX(), y1, distance, room.getEastGates());
            } else if (alpha == angleNE) {
                return isMeasureWithAccuracy(distance, getDistance(x, y, room.getMaxX(), room.getMinY()));
            } else {
                double x1 = (Math.tan(Math.toRadians(alpha)) * (y - room.getMinY())) + x;
                return checkMeasureHorizontally(x, y, x1, room.getMinY(), distance, room.getNorthGates());
            }
        } else if (alpha < 0) {
            if (alpha == -180.0) {
                return checkMeasureHorizontally(x, y, x, room.getMaxY(), distance, room.getSouthGates());
            } else if (alpha < angleSW) {
                double x1 = x - (Math.tan(Math.toRadians(alpha)) * (room.getMaxY() - y));
                return checkMeasureHorizontally(x, y, x1, room.getMaxY(), distance, room.getSouthGates());
            } else if (alpha == angleSW) {
                return isMeasureWithAccuracy(distance, getDistance(x, y, room.getMinX(), room.getMaxY()));
            } else if (alpha < -90.0) {
                double y1 = y - (Math.tan(Math.toRadians(90 + alpha)) * (x - room.getMinX()));
                return checkMeasureVerically(x, y, room.getMinX(), y1, distance, room.getWestGates());
            } else if (alpha == -90.0) {
                return checkMeasureHorizontally(x, y, room.getMinX(), y, distance, room.getWestGates());
            } else if (alpha < angleNW) {
                double y1 = y - (Math.tan(Math.toRadians(90 + alpha)) * (x - room.getMinX()));
                return checkMeasureVerically(x, y, room.getMinX(), y1, distance, room.getWestGates());
            } else if (alpha == angleNW) {
                return isMeasureWithAccuracy(distance, getDistance(x, y, room.getMinX(), room.getMinY()));
            } else {
                double x1 = (Math.tan(Math.toRadians(alpha)) * (y - room.getMinY())) + x;
                return checkMeasureHorizontally(x, y, x1, room.getMinY(), distance, room.getNorthGates());
            }
        } else {
            return checkMeasureHorizontally(x, y, x, room.getMinY(), distance, room.getNorthGates());
        }
    }

    private double checkMeasureHorizontally(double agentX, double agentY, double roomX, double roomY, double distance, List<Gate> gates) {
        if (isMeasureInGateHorizontally(roomX, gates)) {
            return -1.0;
        }
        double realDistance = getDistance(agentX, agentY, roomX, roomY);
        return isMeasureWithAccuracy(distance, realDistance);
    }

    private double checkMeasureVerically(double agentX, double agentY, double roomX, double roomY, double distance, List<Gate> gates) {
        if (isMeasureInGateVertically(roomY, gates)) {
            return -1.0;
        }
        double realDistance = getDistance(agentX, agentY, roomX, roomY);
        return isMeasureWithAccuracy(distance, realDistance);
    }

    private boolean isMeasureInGateHorizontally(double x, List<Gate> gates) {
        for (Gate gate : gates) {
            double start = Math.min(gate.getFrom().getX(), gate.getTo().getX());
            double end = Math.max(gate.getFrom().getX(), gate.getTo().getX());
            if (x > start && x < end) {
                return true;
            }
        }
        return false;
    }

    private boolean isMeasureInGateVertically(double y, List<Gate> gates) {
        for (Gate gate : gates) {
            double start = Math.min(gate.getFrom().getY(), gate.getTo().getY());
            double end = Math.max(gate.getFrom().getY(), gate.getTo().getY());
            if (y > start && y < end) {
                return true;
            }
        }
        return false;
    }

    private double getDistance(double x1, double y1, double x2, double y2) {
        double xDiff = x2 - x1;
        double yDiff = y2 - y1;
        return Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
    }

    private double isMeasureWithAccuracy(double x, double y) {
        double diff = Math.abs(x - y);
        if (Math.abs(x - y) > ACCURACY) {
            return 0.0;
        }
        return 1.0 - (diff / ACCURACY);
    }
}
