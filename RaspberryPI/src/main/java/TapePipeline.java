
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.vision.VisionPipeline;

/**
* TapePipelineNew class.
*
* <p>An OpenCV pipeline generated by GRIP.
*
* @author GRIP
*/
public class TapePipeline implements VisionPipeline {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	//Outputs
	private Mat input = new Mat();
	private Mat hslThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<RotatedRect> filteredRotatedRects = new ArrayList<RotatedRect>();
	private Mat output = new Mat();

	private TargetInfo targetInfo = null;  // null when no lock

	// Dynamic setting of Threshold values;
	private static double[] hslThresholdHue = {80.0, 100.0};
	private static double[] hslThresholdSaturation = {0.0, 135.0};
	private static double[] hslThresholdValue = {100.0, 255.0};

	private static double filterContoursMinArea = 50.0;
	private static double[] rectRatio = {0.25, 0.50};

	public static void setThresholdHue( double min, double max ) {
		hslThresholdHue[0] = min;
		hslThresholdHue[1] = max;
	}
	public static double[] getThresholdHue() {
		return hslThresholdHue;
	}

	public static void setThresholdSaturation( double min, double max ) {
		hslThresholdSaturation[0] = min;
		hslThresholdSaturation[1] = max;
	}
	public static double[] getThresholdSaturation() {
		return hslThresholdSaturation;
	}

	public static void setThresholdValue( double min, double max ) {
		hslThresholdValue[0] = min;
		hslThresholdValue[1] = max;
	}
	public static double[] getThresholdValue() {
		return hslThresholdValue;
	}

	public static void setContoursMinArea( double min) {
		filterContoursMinArea = min;
	}
	public static double getfilterContoursMinArea() {
		return filterContoursMinArea;
	}

	public static void setRotatedRectRatio( double min, double max ) {
		rectRatio[0] = min;
		rectRatio[1] = max;
	}
	public static double[] getRotatedRectRatio() {
		return rectRatio;
	}

	/**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 */
	@Override	
	public void process(Mat source0) {
		input = source0;

		// Step HSL_Threshold0:
		Mat hslThresholdInput = source0;
		hsvThreshold(hslThresholdInput, hslThresholdHue, hslThresholdSaturation, hslThresholdValue, hslThresholdOutput);

		// Step Find_Contours0:
		Mat findContoursInput = hslThresholdOutput;
		boolean findContoursExternalOnly = false;
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

		// Populate RotatedRects from the contours and filter out those that have a bad ratio or too small.
		MatOfPoint2f mat2f = new MatOfPoint2f();
		filterContoursOutput.clear();
		filteredRotatedRects.clear();
		for( int i = 0; i < findContoursOutput.size(); i++) {
			findContoursOutput.get(i).convertTo( mat2f, CvType.CV_32F );
			RotatedRect rect = Imgproc.minAreaRect( mat2f );
			double ratio = (rect.size.width < rect.size.height) ? rect.size.width/rect.size.height : rect.size.height/rect.size.width;

			// If ratio is good and big enough, add the Contour and RotatedRect to output
			if( ratio >= rectRatio[0] && ratio <= rectRatio[1] && (rect.size.width*rect.size.height) > filterContoursMinArea ) {
				filterContoursOutput.add( findContoursOutput.get(i) );
				filteredRotatedRects.add(rect);
			}
		}

		output = source0.clone();
		targetInfo = TargetFinder.findTargetLockInfo(filterContoursOutput, output.width(), output.height() );

		renderContours( filteredRotatedRects, output);
	}

	public Mat input() {
		return input;
	}

	public Mat hslThresholdOutput() {
		return hslThresholdOutput;
	}

	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}

	public ArrayList<MatOfPoint> filterContoursOutput() {
		return filterContoursOutput;
	}

	public Mat output() {
		return output;
	}

	public TargetInfo getTargetInfo() {
		return targetInfo;
	}

	/**
	 * Segment an image based on hue, saturation, and value ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param val The min and max value
	 * @param output The image in which to store the output.
	 */
	private static void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
	    Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
		Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
			new Scalar(hue[1], sat[1], val[1]), out);
	}

	/**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	private void findContours(Mat input, boolean externalOnly,
		List<MatOfPoint> contours) {
		Mat hierarchy = new Mat();
		contours.clear();
		int mode;
		if (externalOnly) {
			mode = Imgproc.RETR_EXTERNAL;
		}
		else {
			mode = Imgproc.RETR_LIST;
		}
		int method = Imgproc.CHAIN_APPROX_SIMPLE;
		Imgproc.findContours(input, contours, hierarchy, mode, method);
	}

	private void renderContours( List<RotatedRect> rotatedRects, Mat output ) {
		// Scalar white = new Scalar(255,255,255);
		Scalar red   = new Scalar(0,0,255);
		Scalar white = new Scalar(255,255,255);
		double fontScale = (output.width() > 320 ? 1.0 : 0.5);
		
		for( RotatedRect rotatedRect : rotatedRects ) {
			Point[] vertices = new Point[4];
			rotatedRect.points(vertices);
			for( int j=0; j<4; j++ )
				Imgproc.line( output, vertices[j], vertices[(j+1)%4], red );

			Point p = rotatedRect.center.clone();
			double angle = ( rotatedRect.size.width < rotatedRect.size.height ) ? rotatedRect.angle + 90 : rotatedRect.angle;
			double yaw = (rotatedRect.center.x - output.width()/2) / (output.width() / Camera.HORIZONTAL_FOV); 

			double dy = 15; // (Math.signum(angle) * 15);
			p.y += dy;
			Imgproc.putText( output, "/"+Math.floor(angle), p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
			p.y += dy;
			Imgproc.putText( output, "("+Math.floor(rotatedRect.center.x)+","+Math.floor(rotatedRect.center.y)+")", p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
			p.y += dy;
			Imgproc.putText( output, "Y: "+Math.floor(yaw), p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
		}

		if( targetInfo != null )  // We have a lock
		{ 
			double lineX = targetInfo.centerX;
			double targetYaw = (targetInfo.centerX - output.width()/2) / (output.width() / Camera.HORIZONTAL_FOV);

			Imgproc.putText( output, "Yaw: "+ Math.floor(targetYaw), new Point(lineX+3, 10), Core.FONT_HERSHEY_PLAIN, fontScale, white );
			Imgproc.line(output, new Point(lineX, 0), new Point(lineX, output.height()), white);
	
			Imgproc.putText( output, "Distance: "+ Math.floor(targetInfo.distance), new Point(lineX+3, 20), Core.FONT_HERSHEY_PLAIN, fontScale, white );
			String info = "("+ Math.floor(targetInfo.centerX) + "," + Math.floor(targetInfo.centerY) + "," + Math.floor(targetInfo.centerHeight ) + ")";
			Imgproc.putText( output, info, new Point(lineX+3, 30), Core.FONT_HERSHEY_PLAIN, fontScale, white );
		}
		else {
			Imgproc.putText( output, "No Target Lock", new Point(3, output.height()-10 ), Core.FONT_HERSHEY_PLAIN, fontScale, white );
		}
	}
}
