
class PathInfo {
    private static final int DISTANCE_TO_TARGET = 36;

    private double distanceToTape;
    private double lineAngle;
    private double a;
    private double aSign;

    private double angleToPerp = Double.NaN;
    private double distanceToPerp = Double.NaN;
    private double angleToTarget = Double.NaN;
    private double distanceToTarget = Double.NaN;
    private boolean validPath = false;

    PathInfo( double distanceToTape, double lineAngle )
    {
        this.distanceToTape = distanceToTape;
        this.lineAngle = lineAngle;
        this.a = 90 - Math.abs(lineAngle);
        this.aSign = Math.signum(lineAngle);

        try {
            distanceToPerp = Math.sqrt( (DISTANCE_TO_TARGET*DISTANCE_TO_TARGET) + distanceToTape*distanceToTape 
                                        - ( 2 * DISTANCE_TO_TARGET * distanceToTape * Math.cos(a) ) );
            
            angleToPerp = aSign * Math.acos( ( DISTANCE_TO_TARGET*DISTANCE_TO_TARGET - (distanceToTape*distanceToTape) - (distanceToPerp*distanceToPerp) )
                                             / ( 2 * distanceToTape * distanceToPerp ) );
            
            angleToTarget = -aSign * (a + angleToPerp);
            
            distanceToTarget = DISTANCE_TO_TARGET;

            validPath = true;
        } catch (Exception e) {
            // lots of things can go wrong with the match.
            // If an exception is thrown just set validPath to false;
            validPath = false;
        }    
    }

    public double detDistanceToTape() {
        return distanceToTape;
    }
    public double getLineAngle() {
        return lineAngle;
    }
    public double getAngleToPerp() {
        return angleToPerp;
    }
    public double getDistanceToPerp() {
        return distanceToPerp;
    }
    public double getAngleToTarget() {
        return angleToTarget;
    }
    public double getDistanceToTarget() {
        return distanceToTarget;
    }
    public boolean isValidPath() {
        return validPath;
    }
}