
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
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

	// inputs
	private Rect crop = null;
	//Outputs
	private Mat input = new Mat();
	private Mat hslThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContours = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filteredContours = new ArrayList<MatOfPoint>();
	private ArrayList<RotatedRect> filteredRotatedRects = new ArrayList<RotatedRect>();

	private TapeInfo tapeInfo = null;  // null when no lock

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
		this.crop = new Rect( 0, 0, source0.width(), source0.height()/2 );
		Mat subImage = source0.submat(crop);

		// Step HSL_Threshold0:
		Mat hslThresholdInput = subImage;
		hsvThreshold(hslThresholdInput, hslThresholdHue, hslThresholdSaturation, hslThresholdValue, hslThresholdOutput);

		// Step Find_Contours0:
		Mat findContoursInput = hslThresholdOutput;
		boolean findContoursExternalOnly = false;
		findContours(findContoursInput, findContoursExternalOnly, findContours);

		// Populate RotatedRects from the contours and filter out those that have a bad ratio or too small.
		MatOfPoint2f mat2f = new MatOfPoint2f();
		filteredContours.clear();
		filteredRotatedRects.clear();
		for( int i = 0; i < findContours.size(); i++) {
			findContours.get(i).convertTo( mat2f, CvType.CV_32F );
			RotatedRect rect = Imgproc.minAreaRect( mat2f );
			double ratio = (rect.size.width < rect.size.height) ? rect.size.width/rect.size.height : rect.size.height/rect.size.width;

			// If ratio is good and big enough, add the Contour and RotatedRect to output
			if( ratio >= rectRatio[0] && ratio <= rectRatio[1] && (rect.size.width*rect.size.height) > filterContoursMinArea ) {
				filteredContours.add( findContours.get(i) );
				filteredRotatedRects.add(rect);
			}
		}

		tapeInfo = TapeFinder.findTargetLockInfo(filteredContours, input.width(), input.height() );
	}

	public Mat getInput() {
		return input;
	}

	public Mat hslThresholdOutput() {
		return hslThresholdOutput;
	}

	public ArrayList<MatOfPoint> getFindContours() {
		return findContours;
	}

	public ArrayList<MatOfPoint> getFilteredContours() {
		return filteredContours;
	}

	public ArrayList<RotatedRect> getfilteredRotatedRects() {
		return filteredRotatedRects;
	}

	public TapeInfo getTapeInfo() {
		return tapeInfo;
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

	public void renderContours( List<RotatedRect> rotatedRects, Mat output, boolean debug ) {
		// Scalar white = new Scalar(255,255,255);
		Scalar red   = new Scalar(0,0,255);
		Scalar white = new Scalar(255,255,255);
		double fontScale = (output.width() > 320 ? 1.0 : 0.7);
		
		for( RotatedRect rotatedRect : rotatedRects ) {
			Point[] vertices = new Point[4];
			rotatedRect.points(vertices);
			for( int j=0; j<4; j++ )
				Imgproc.line( output, vertices[j], vertices[(j+1)%4], red );

			if( debug ) {
				Point p = rotatedRect.center.clone();
				double angle = ( rotatedRect.size.width < rotatedRect.size.height ) ? rotatedRect.angle + 90 : rotatedRect.angle;
				double yaw = (rotatedRect.center.x - output.width()/2) / (output.width() / Camera.HORIZONTAL_FOV); 

				double dy = 15; // (Math.signum(angle) * 15);
				p.y += dy;
				Imgproc.putText( output, "/"+ Math.round(angle*10.0)/10.0, p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
				p.y += dy;
				Imgproc.putText( output, "("+ Math.round(rotatedRect.center.x*10.0)/10.0+","+Math.floor(rotatedRect.center.y)+")", p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
				p.y += dy;
				Imgproc.putText( output, "Y: "+ Math.round(yaw*10.0)/10.0, p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
			}
		}

		if( tapeInfo != null )  // We have a lock
		{ 
			double lineX = tapeInfo.centerX;
			double targetYaw = (tapeInfo.centerX - output.width()/2) / (output.width() / Camera.HORIZONTAL_FOV);

			Imgproc.putText( output, "Yaw: "+ Math.round(targetYaw*10.0)/10.0, new Point(lineX+3, 10), Core.FONT_HERSHEY_PLAIN, fontScale, white );
			Imgproc.line(output, new Point(lineX, 0), new Point(lineX, output.height()), white);
	
			Imgproc.putText( output, "Distance: "+ Math.round(tapeInfo.distance*10.0)/10.0, new Point(lineX+3, 20), Core.FONT_HERSHEY_PLAIN, fontScale, white );

			if( debug ) {
				String info = "("+ Math.round(tapeInfo.centerX*10.0)/10.0 + "," + Math.round(tapeInfo.centerY*10.0)/10.0 + "," + Math.round(tapeInfo.centerHeight*10.0)/10.0 + ")";
				Imgproc.putText( output, info, new Point(lineX+3, 30), Core.FONT_HERSHEY_PLAIN, fontScale, white );
			}
		}
		else {
			Imgproc.putText( output, "No Target Lock", new Point(3, output.height()-10 ), Core.FONT_HERSHEY_PLAIN, fontScale*1.25, red );
		}
	}
}
