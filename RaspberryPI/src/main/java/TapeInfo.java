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
    private double frameWidth;
    private double frameHeight;

    TapeInfo( double centerX, double centerY, double centerHeight, double distance, double angle, double minX, double maxX, double frameWidth, double frameHeight )
    {
        init( centerX, centerY, centerHeight, distance, angle, minX, maxX, frameWidth, frameHeight );
    }

    // Allows for reuse of the object to save a "new"
    void init( double centerX, double centerY, double centerHeight, double distance, double angle, double minX, double maxX, double frameWidth, double frameHeight )
    {
        this.centerX      = centerX;
        this.centerY      = centerY;
        this.centerHeight = centerHeight;
        this.distance     = distance;
        this.angle        = angle;
        this.minX         = minX;
        this.maxX         = maxX;
        this.frameWidth   = frameWidth;
        this.frameHeight  = frameHeight;
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
    double getFrameWidth() { return frameWidth; }
    double getFrameHeight() { return frameHeight; }
}
