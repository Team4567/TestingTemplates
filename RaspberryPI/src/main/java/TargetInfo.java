// A class (really just an immutable data structure) to hold found target info
// This will include all sorts of good stuff like x position on screen to height...

class TargetInfo {

    public double centerX;              // average X of two parts of tape target
    public double centerY;              // average Y of two parts of tape target
    public double centerHeight;         // average Height of two parts of tape target
    public double distance;
    public double yaw;

    TargetInfo( double centerX, double centerY, double centerHeight, double distance, double yaw ) {
        this.centerX      = centerX;
        this.centerY      = centerY;
        this.centerHeight = centerHeight;
        this.distance     = distance;
        this.yaw          = yaw;

    }

    public double getCenterX() {
        return centerX;
    }
    public double getCenterY() {
        return centerY;
    }
    public double getCenterHeight() {
        return centerHeight;
    }
    public double getDistance() {
        return distance;
    }
    public double getYaw() {
        return yaw;
    }
}
