import java.util.HashMap;

class Camera {

	private static HashMap<Integer, Double> hfov = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> vfov = new HashMap<Integer, Double>();

	static {
		// FOV Measurements at difference resolutions
		vfov.put( 240, 43.9823 );
		vfov.put( 600, 40.2884 );

		hfov.put( 320, 52.7926 );
		hfov.put( 800, 55.9186 );
	}

	static double getHFOV( int width ) {
		return hfov.get(width) != null ? hfov.get(width) : hfov.get(320);
	}

	static double getVFOV( int height ) {
		return vfov.get(height) != null ? vfov.get(height) : vfov.get(240);
	}

	// Returns the angle that points to the pixel offset from center
	// Could be positive or negative for left or right of center
	// The Horizontal FOV was measured for 320x240
	// It is approximately correct for other resolutions only
	static double yawToHorizontalPixel(double pixOffset, int width ) {
		return (pixOffset - width/2.0) / (width / getHFOV(width) );
	}

	static double estimateDistance( double knownHeightInches, int heightInPixels, int frameHeightPixels ) {
		return  (knownHeightInches/2.0) / Math.tan( Math.toRadians( (heightInPixels * getVFOV(frameHeightPixels)/frameHeightPixels/2.0 ) ) );
	}
	
}
