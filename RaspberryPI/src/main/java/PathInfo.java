
class PathInfo
{
    private static final int DISTANCE_TO_TARGET = 36;

    private double angleToPerp = Double.NaN;
    private double distanceToPerp = Double.NaN;
    private double angleToTarget = Double.NaN;
    private double distanceToTarget = Double.NaN;

    // for Debug
    private double distanceToTape = Double.NaN;
    private double lineAngle = Double.NaN;
    private double downAngle = Double.NaN;
    private double adjustedAngle = Double.NaN;

    private boolean validPath = false;

    PathInfo()
    {
    }

    void calculate(double distanceToTape, double lineAngle)
    {
        this.distanceToTape = distanceToTape;
        this.lineAngle = lineAngle;

        try {
            double aSign = Math.signum(lineAngle);
            double aR = Math.toRadians(Math.abs(lineAngle));  // Raw Angle in radians

            // Angle needs to be correct for looking down at it
            // Change To Actual Height?
            double downAngleR = Math.atan(19.0 / distanceToTape);  // in radians
            double adjustedAngleR = Math.atan(Math.tan(aR) / Math.sin(downAngleR)); // Adjusted Angle in radians

            distanceToPerp = Math.round(Math.sqrt((DISTANCE_TO_TARGET * DISTANCE_TO_TARGET) + distanceToTape * distanceToTape
                    - (2 * DISTANCE_TO_TARGET * distanceToTape * Math.cos(Math.PI / 2 - adjustedAngleR))) * 10.0) / 10.0;

            double angle = Math.toDegrees(Math.acos((DISTANCE_TO_TARGET * DISTANCE_TO_TARGET
                    - (distanceToTape * distanceToTape)
                    - (distanceToPerp * distanceToPerp))
                    / (-2.0 * distanceToTape * distanceToPerp)));

            angleToPerp = aSign * Math.round(angle * 10.0) / 10.0;
            angleToTarget = -aSign * Math.round((Math.toDegrees(Math.PI / 2 - adjustedAngleR) + angle) * 10.0) / 10.0;

            distanceToTarget = DISTANCE_TO_TARGET;

            downAngle = Math.round(Math.toDegrees(downAngleR) * 10.0) / 10.0;
            adjustedAngle = Math.round(Math.toDegrees(adjustedAngleR) * 10.0) / 10.0;
            validPath = true;

        } catch (Exception e) {
            // lots of things can go wrong with the math.
            // If an exception is thrown just set validPath to false;
            validPath = false;
        }
    }

    double getAngleToPerp()
    {
        return angleToPerp;
    }

    double getDistanceToPerp()
    {
        return distanceToPerp;
    }

    double getAngleToTarget()
    {
        return angleToTarget;
    }

    double getDistanceToTarget()
    {
        return distanceToTarget;
    }

    // Debug
    double getDistanceToTape()
    {
        return distanceToTape;
    }

    double getLineAngle()
    {
        return lineAngle;
    }

    double getDownAngle()
    {
        return downAngle;
    }

    double getAdjustedAngle()
    {
        return adjustedAngle;
    }

    boolean isValidPath()
    {
        return validPath;
    }

    void invalidate()
    {
        validPath = false;
    }
}
