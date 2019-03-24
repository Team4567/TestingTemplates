import java.util.HashMap;

class Camera
{
    private static final HashMap<Integer, Double> hfov = new HashMap<>();
    private static final HashMap<Integer, Double> vfov = new HashMap<>();

    static {
        // FOV Measurements at difference resolutions
        vfov.put(240, 37.2671);
        vfov.put(288, 43.2514);
        vfov.put(600, 39.4171);

        hfov.put(320, 58.8701);
        hfov.put(352, 54.5442);
        hfov.put(800, 59.2790);
    }

    private static double getHFOV(int width)
    {
        return hfov.get(width) != null ? hfov.get(width) : hfov.get(320);
    }

    private static double getVFOV(int height)
    {
        return vfov.get(height) != null ? vfov.get(height) : vfov.get(240);
    }

    // Returns the angle that points to the pixel offset from center
    // Could be positive or negative for left or right of center
    // This is approximate so rounded to tenths.
    static double yawToHorizontalPixel(double pixOffset, int width)
    {
        return Math.round((pixOffset - width / 2.0) / (width / getHFOV(width)) * 10.0) / 10.0;
    }

    // This is approximate so rounded to tenths.
    static double estimateDistance(@SuppressWarnings("SameParameterValue") double knownHeightInches, int heightInPixels, int frameHeightPixels)
    {
        return Math.round((knownHeightInches / 2.0) / Math.tan(Math.toRadians((heightInPixels * getVFOV(frameHeightPixels) / frameHeightPixels / 2.0))) * 10.0) / 10.0;
    }
}
