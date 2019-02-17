
class PathInfo
{
    private static final int DISTANCE_TO_TARGET = 36;

    private double distanceToTape = Double.NaN;
    private double lineAngle = Double.NaN;

    private double angleToPerp = Double.NaN;
    private double distanceToPerp = Double.NaN;
    private double angleToTarget = Double.NaN;
    private double distanceToTarget = Double.NaN;
    private boolean validPath = false;

    PathInfo() {
    }

    void calculate( double distanceToTape, double lineAngle )
    {
        this.distanceToTape = distanceToTape;
        this.lineAngle = lineAngle;

        double a = 90 - Math.abs(lineAngle);
        double aSign = Math.signum(lineAngle);

        try {
            distanceToPerp = Math.sqrt( (DISTANCE_TO_TARGET*DISTANCE_TO_TARGET) + distanceToTape*distanceToTape 
                                        - ( 2 * DISTANCE_TO_TARGET * distanceToTape * Math.cos(a) ) );
            
            angleToPerp = aSign * Math.acos( ( DISTANCE_TO_TARGET*DISTANCE_TO_TARGET - (distanceToTape*distanceToTape) - (distanceToPerp*distanceToPerp) )
                                             / ( 2 * distanceToTape * distanceToPerp ) );
            
            angleToTarget = -aSign * (a + angleToPerp);
            
            distanceToTarget = DISTANCE_TO_TARGET;

            validPath = true;
        } catch (Exception e) {
            // lots of things can go wrong with the math.
            // If an exception is thrown just set validPath to false;
            validPath = false;
        }    
    }

    double getDistanceToTape() {
        return distanceToTape;
    }
    double getLineAngle() {
        return lineAngle;
    }
    double getAngleToPerp() {
        return angleToPerp;
    }
    double getDistanceToPerp() {
        return distanceToPerp;
    }
    double getAngleToTarget() {
        return angleToTarget;
    }
    double getDistanceToTarget() {
        return distanceToTarget;
    }
    boolean isValidPath() {
        return validPath;
    }
}
