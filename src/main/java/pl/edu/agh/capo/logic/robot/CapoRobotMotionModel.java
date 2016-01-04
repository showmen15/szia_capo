package pl.edu.agh.capo.logic.robot;


import com.vividsolutions.jts.math.Vector2D;
import pl.edu.agh.capo.logic.common.Location;

public class CapoRobotMotionModel {


    public static final double wheelsHalfDistance = 0.14;
    public static final double robotDiameter = 0.5;
    ;
    public static final double robotHalfDiameter = robotDiameter / 2;

    protected static final double verySmallDouble = 0.0001;  //	nedded in some calculations; 0.1 milimeter is assumed smaller than accuracy

    protected double maxLinearVelocity = 1;

    protected Location location = new Location(0, 0, 0);
    protected double velocityLeft, velocityRight;

    public double getMaxLinearVelocity() {
        return maxLinearVelocity;
    }

    public CapoRobotMotionModel(double maxLinearVelocity) {
        this.maxLinearVelocity = maxLinearVelocity;
    }


    public Location getLocation() {
        return location;
    }


    public double getVelocityLeft() {
        return velocityLeft;
    }

    public double getVelocityRight() {
        return velocityRight;
    }

    public void setLocation(double x, double y, double angle) {
        this.location.positionX = x;
        this.location.positionY = y;
        this.location.direction = angle;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean setVelocity(double velocityLeft, double velocityRight) {
        this.velocityLeft = velocityLeft;
        this.velocityRight = velocityRight;
        return checkFeasibility();
    }

    public Vector2D getVersor() {
        return new Vector2D(Math.cos(location.direction), Math.sin(location.direction));
    }

    public double getLinearVelocity() {
        return (velocityLeft + velocityRight) / 2;
    }

    public double getAngularVelocity() {
        return (velocityLeft - velocityRight) / (2 * wheelsHalfDistance);
    }

    public double getArcRadius() {
        if (velocityLeft == velocityRight)
            return Double.POSITIVE_INFINITY;

        return wheelsHalfDistance * (velocityLeft + velocityRight) / (velocityLeft - velocityRight);
    }

    /// <summary>
    /// Makes the robot move straight, with specified linearVelocity.
    ///
    /// If physical constraints are exceeded, linear velocity will be lowered, arc radius should be preserved.
    ///
    /// </summary>
    /// <param name="linearVelocity"></param>
    /// <returns>true, if the order is physically feasible</returns>
    public boolean setVelocity_MoveStraight(double linearVelocity) {
        velocityLeft = linearVelocity;
        velocityRight = linearVelocity;
        return checkFeasibility();
    }

    /// <summary>
    /// Makes robot turn around with specified angularVelocity.
    ///
    /// If physical constraints are exceeded, linear velocity will be lowered, arc radius should be preserved.
    ///
    /// Positive value of angularVelocity causes angle to inrease
    /// Equations are:
    /// leftSpeed = angularVelocity * wheelsHalfDistance;
    /// rightSpeed = - angularVelocity * wheelsHalfDistance;
    /// </summary>
    /// <param name="angularVelocity"></param>
    /// <returns>true, if the order is physically feasible</returns>
    public boolean setVelocity_TurnAround(double angularVelocity) {
        velocityLeft = angularVelocity * wheelsHalfDistance;
        velocityRight = -angularVelocity * wheelsHalfDistance;
        return checkFeasibility();
    }

    /// <summary>
    /// Makes robot go along an arc of specified arcRadius with specified linearVelocity.
    ///
    /// If physical constraints are exceeded, linear velocity will be lowered, arc radius should be preserved.
    ///
    /// Equations are:
    /// 1)  arcRadius = wheelsHalfDistance * (rightSpeed + leftSpeed) / (rightSpeed - leftSpeed)
    /// 2)  linearVelocity = (rightSpeed + leftSpeed) / 2
    /// which give:
    /// leftSpeed = linearVelocity * (1 + (wheelsHalfDistance / arcRadius))
    /// rightSpeed = linearVelocity * (1 - (wheelsHalfDistance / arcRadius))
    /// </summary>
    /// <param name="linearVelocity"></param>
    /// <param name="arcRadius"></param>
    /// <returns>true, if the order is physically feasible</returns>
    public boolean setVelocity_LinearVelocity_ArcRadius(double linearVelocity, double arcRadius) {
        if (Math.abs(arcRadius) < verySmallDouble) {
            arcRadius = Math.signum(arcRadius) * verySmallDouble;
        }
        velocityLeft = linearVelocity * (1 + (wheelsHalfDistance / arcRadius));
        velocityRight = linearVelocity * (1 - (wheelsHalfDistance / arcRadius));
        return checkFeasibility();
    }

    /// <summary>
    /// Makes robot move with specified linearVelocity and angularVelocity.
    ///
    /// If physical constraints are exceeded, linear velocity will be lowered, arc radius should be preserved.
    ///
    /// Equations are:
    /// 1) linearVelocity / angularVelocity = wheelsHalfDistance * (rightSpeed + leftSpeed) / (rightSpeed - leftSpeed)    - compare Move_LinearVelocity_ArcRadius
    /// 2) linearVelocity = (rightSpeed + leftSpeed) / 2
    /// which give:
    /// leftSpeed = linearVelocity * (1 + ((angularVelocity * wheelsHalfDistance) / linearVelocity))
    /// rightSpeed = linearVelocity * (1 - ((angularVelocity * wheelsHalfDistance) / linearVelocity))
    /// </summary>
    /// <param name="linearVelocity"></param>
    /// <param name="angularVelocity"></param>
    /// <returns>true, if the order is physically feasible</returns>
    public boolean setVelocity_LinearVelocity_AngularVelocity(double linearVelocity, double angularVelocity) {
        if (Math.abs(linearVelocity) < verySmallDouble) {
            return setVelocity_TurnAround(angularVelocity);
        } else {
            //velocityLeft = linearVelocity * (1 + ((angularVelocity * wheelsHalfDistance) / linearVelocity));
            velocityLeft = linearVelocity + angularVelocity * wheelsHalfDistance;
            //velocityRight = linearVelocity * (1 - ((angularVelocity * wheelsHalfDistance) / linearVelocity));
            velocityRight = linearVelocity - angularVelocity * wheelsHalfDistance;
        }
        return checkFeasibility();
    }

    /// <summary>
    /// Makes robot go along an arc of specified arcRadius with specified angularVelocity.
    ///
    /// If physical constraints are exceeded, linear velocity will be lowered, arc radius should be preserved.
    ///
    /// Equations are:
    /// 1) arcRadius = wheelsHalfDistance * (rightSpeed + leftSpeed) / (rightSpeed - leftSpeed)
    /// 2) arcRadius * angularVelocity = (rightSpeed + leftSpeed) / 2
    /// which give:
    /// leftSpeed = (arcRadius * angularVelocity) * (1 + (wheelsHalfDistance / arcRadius))
    /// rightSpeed = (arcRadius * angularVelocity) * (1 - (wheelsHalfDistance / arcRadius))
    /// </summary>
    /// <param name="arcRadius"></param>
    /// <param name="angularVelocity"></param>
    /// <returns>true, if the order is physically feasible</returns>
    public boolean setVelocity_ArcRadius_AngularVelocity(double arcRadius, double angularVelocity) {
        if (Math.abs(arcRadius) < verySmallDouble) {
            return setVelocity_TurnAround(angularVelocity);
        } else {
            //	velocityLeft = (arcRadius * angularVelocity) * (1 + (wheelsHalfDistance / arcRadius));
            velocityLeft = angularVelocity * (arcRadius + wheelsHalfDistance);
            //	velocityRight = (arcRadius * angularVelocity) * (1 - (wheelsHalfDistance / arcRadius));
            velocityRight = angularVelocity * (arcRadius - wheelsHalfDistance);
        }
        return checkFeasibility();
    }

    /**
     * First calculate the Center of the circle:
     * arcCenter = [x − R sin(θ) , y + R cos(θ)]
     * Then calculate new location:
     * rotating a distance R about its ICC with an angular velocity of ω.
     * <p>
     * http://chess.eecs.berkeley.edu/eecs149/documentation/differentialDrive.pdf
     *
     * @param deltaTime in seconds
     */
    public Location getLocationAfterTime(double deltaTime) {
        double radius = getArcRadius();

        if (radius == Double.POSITIVE_INFINITY) {
            return new Location(location.positionX + getLinearVelocity() * Math.cos(location.direction * deltaTime),
                    location.positionY + getLinearVelocity() * Math.sin(location.direction * deltaTime),
                    location.direction);
        } else {
            double arcCenterX = location.positionX - radius * Math.sin(location.direction);
            double arcCenterY = location.positionY + radius * Math.cos(location.direction);

            double angularVelocityDeltaTime = getAngularVelocity() * deltaTime;

            double newX = Math.cos(angularVelocityDeltaTime) * (location.positionX - arcCenterX)
                    - Math.sin(angularVelocityDeltaTime) * (location.positionY - arcCenterY)
                    + arcCenterX;
            double newY = Math.sin(angularVelocityDeltaTime) * (location.positionX - arcCenterX)
                    + Math.cos(angularVelocityDeltaTime) * (location.positionY - arcCenterY)
                    + arcCenterY;

            return new Location(newX, newY, location.direction + angularVelocityDeltaTime);
        }

    }

    public void moveRobot(double deltaTime) {
        this.location = getLocationAfterTime(deltaTime);
    }


    /// <summary>
    /// Corrects robot velocity, if it exceedes maxLinearVelocity value.
    ///
    /// TODO - cannot just restrict wheels velocity, linear v of the robot is to be restricted
    /// </summary>
    /// <returns></returns>
    private boolean checkFeasibility() {
        if (Math.abs(velocityLeft) < maxLinearVelocity
                && Math.abs(velocityRight) < maxLinearVelocity) {
            return true;
        }
        double divider = Math.max(Math.abs(velocityLeft), Math.abs(velocityRight)) / maxLinearVelocity;
        velocityLeft /= divider;
        velocityRight /= divider;
        return false;
    }

}


