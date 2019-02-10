// A class (really just an immutable data structure) to hold found target info
// This will include all sorts of good stuff like x position on screen to height...

class TargetInfo {

    public double centerX;              // average X of two parts of tape target
    public double centerY;              // average Y of two parts of tape target
    public double centerHeight;         // average Height of two parts of tape target
    public double distance;
    public double yaw;
    public double minX;                 // Min and Max X are useful for creating a submat for line search.
    public double maxX;

    TargetInfo( double centerX, double centerY, double centerHeight, double distance, double yaw, double minX, double maxX ) {
        this.centerX      = centerX;
        this.centerY      = centerY;
        this.centerHeight = centerHeight;
        this.distance     = distance;
        this.yaw          = yaw;
        this.minX         = minX;
        this.maxX         = maxX;
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
    public double getMinX() {
        return minX;
    }
    public double getMaxX() {
        return maxX;
    }
}
