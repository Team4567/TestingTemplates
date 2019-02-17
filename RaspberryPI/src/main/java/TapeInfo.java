// A class (really just an immutable data structure) to hold found target info
// This will include all sorts of good stuff like x position on screen to height...

class TapeInfo
{
    private double centerX;              // average X of two parts of tape target
    private double centerY;              // average Y of two parts of tape target
    private double centerHeight;         // average Height of two parts of tape target
    private double distance;
    private double angle;
    private double minX;                 // Min and Max X are useful for creating a submat for line search.
    private double maxX;

    TapeInfo( double centerX, double centerY, double centerHeight, double distance, double angle, double minX, double maxX ) 
    {
        init( centerX, centerY, centerHeight, distance, angle, minX, maxX );
    }

    // Allows for reuse of the object to save a "new"
    void init( double centerX, double centerY, double centerHeight, double distance, double angle, double minX, double maxX )
    {
        this.centerX      = centerX;
        this.centerY      = centerY;
        this.centerHeight = centerHeight;
        this.distance     = distance;
        this.angle        = angle;
        this.minX         = minX;
        this.maxX         = maxX;
    }

    double getCenterX() {
        return centerX;
    }
    double getCenterY() {
        return centerY;
    }
    double getCenterHeight() {
        return centerHeight;
    }
    double getDistance() {
        return distance;
    }
    double getAngle() {
        return angle;
    }
    double getMinX() {
        return minX;
    }
    double getMaxX() {
        return maxX;
    }
}
