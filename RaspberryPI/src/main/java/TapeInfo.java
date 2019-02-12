// A class (really just an immutable data structure) to hold found target info
// This will include all sorts of good stuff like x position on screen to height...

class TapeInfo {

    public double centerX;              // average X of two parts of tape target
    public double centerY;              // average Y of two parts of tape target
    public double centerHeight;         // average Height of two parts of tape target
    public double distance;
    public double angle;
    public double minX;                 // Min and Max X are useful for creating a submat for line search.
    public double maxX;

    TapeInfo( double centerX, double centerY, double centerHeight, double distance, double angle, double minX, double maxX ) {
        this.centerX      = centerX;
        this.centerY      = centerY;
        this.centerHeight = centerHeight;
        this.distance     = distance;
        this.angle        = angle;
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
    public double getAngle() {
        return angle;
    }
    public double getMinX() {
        return minX;
    }
    public double getMaxX() {
        return maxX;
    }
}
