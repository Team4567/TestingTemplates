
class Camera {

	// This is all very interesting but this isn't reality.
	// I measured the Horizontal and Vertical FOV at 320x240 resolution
	// I'll keep it here commected out for now, but, we're going with the true measurements

	/*
    	//image size ratioed to 16:9
	public static final double IMAGE_WIDTH  = 1280.0;
	public static final double IMAGE_HEIGHT = 720.0;
			
	//Lifecam 3000 from datasheet
	//Datasheet: https://dl2jx7zfbtwvr.cloudfront.net/specsheets/WEBC1010.pdf
	public  static final double DIAGONAL_FOV = Math.toRadians(68.5);
			
	//16:9 aspect ratio
	public static final double HORIZONTAL_ASPECT = 16;
	public static final double VERTICAL_ASPECT   = 9;
			
	//Reasons for using diagonal aspect is to calculate horizontal field of view.
	public static final double DIAGONAL_ASPECT = Math.hypot(HORIZONTAL_ASPECT, VERTICAL_ASPECT);
	//Calculations: http://vrguy.blogspot.com/2013/04/converting-diagonal-field-of-view-and.html
	public static final double HORIZONTAL_FOV = Math.atan(Math.tan(DIAGONAL_FOV/2) * (HORIZONTAL_ASPECT / DIAGONAL_ASPECT)) * 2;
	public static final double PIXEL_TO_ANGLE = IMAGE_WIDTH/HORIZONTAL_FOV;
	//Focal Length calculations: https://docs.google.com/presentation/d/1ediRsI-oR3-kwawFJZ34_ZTlQS2SDBLjZasjzZ-eXbQ/pub?start=false&loop=false&slide=id.g12c083cffa_0_165
	public static final double H_FOCAL_LENGTH = IMAGE_WIDTH / (2*Math.tan((HORIZONTAL_FOV/2)));
	public static double targetMidpoint=IMAGE_WIDTH/2;
	*/

	public static final double HORIZONTAL_FOV = 52.7926;  // degrees, measured
	public static final double VERTICAL_FOV   = 43.9823;  // degrees, measured

	// Returns the angle that points to the pixel offset from center
	// Could be positive or negative for left or right of center
	// The Horizontal FOV was measured for 320x240
	// It is approximately correct for other resolutions only
	public static double yawToHorizPixel( double pixOffet, double width ) {
		return (pixOffet - width/2) / (width / Camera.HORIZONTAL_FOV);
	}

	public static double estimateDistance( double knownHeightInches, double heightInPixels, double frameHeightPixels ) {
		return  (knownHeightInches/2.0) / Math.tan( Math.toRadians( (heightInPixels * Camera.VERTICAL_FOV)/frameHeightPixels/2 ) );
	}
	
}
