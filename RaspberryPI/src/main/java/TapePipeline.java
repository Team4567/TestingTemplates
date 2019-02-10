
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
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

	//Outputs
	private Mat input = new Mat();
	private Mat hslThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput2 = new ArrayList<MatOfPoint>();  // ratio applied
	private ArrayList<RotatedRect> filteredRotatedRects = new ArrayList<RotatedRect>();
	private Mat output = new Mat();

	private TargetInfo targetInfo = null;  // null when no lock

	// Dynamic setting of Threshold values;
	private static double[] hslThresholdHue = {80.0, 100.0};
	private static double[] hslThresholdSaturation = {0.0, 135.0};
	private static double[] hslThresholdValue = {100.0, 255.0};

	private static double filterContoursMinArea = 500.0;
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

		// Step Filter_Contours0:
		ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
//		double filterContoursMinArea = 42.0;
		double filterContoursMinPerimeter = 0;
		double filterContoursMinWidth = 0;
		double filterContoursMaxWidth = 1000;
		double filterContoursMinHeight = 0;
		double filterContoursMaxHeight = 1000;
		double[] filterContoursSolidity = {0, 100};
		double filterContoursMaxVertices = 1000000;
		double filterContoursMinVertices = 0;
		double filterContoursMinRatio = 0;
		double filterContoursMaxRatio = 1000;
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);

		// Populate RotatedRects from the contours and filter out those that have a bad ratio.
		MatOfPoint2f mat2f = new MatOfPoint2f();
		filterContoursOutput2.clear();
		filteredRotatedRects.clear();
		for( int i = 0; i < filterContoursOutput.size(); i++) {
			filterContoursOutput.get(i).convertTo( mat2f, CvType.CV_32F );
			RotatedRect rect = Imgproc.minAreaRect( mat2f );
			double ratio = (rect.size.width < rect.size.height) ? rect.size.width/rect.size.height : rect.size.height/rect.size.width;

			// If ratio is good add the Contour and RotatedRect to output
			if( ratio >= rectRatio[0] && ratio <= rectRatio[1] ) {
				filterContoursOutput2.add( filterContoursOutput.get(i) );
				filteredRotatedRects.add(rect);
			}
		}

		output = source0.clone();
		targetInfo = TargetFinder.findTargetLockInfo(filterContoursOutput2, output.width(), output.height() );

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


	/**
	 * Filters out contours that do not meet certain criteria.
	 * @param inputContours is the input list of contours
	 * @param output is the the output list of contours
	 * @param minArea is the minimum area of a contour that will be kept
	 * @param minPerimeter is the minimum perimeter of a contour that will be kept
	 * @param minWidth minimum width of a contour
	 * @param maxWidth maximum width
	 * @param minHeight minimum height
	 * @param maxHeight maximimum height
	 * @param Solidity the minimum and maximum solidity of a contour
	 * @param minVertexCount minimum vertex Count of the contours
	 * @param maxVertexCount maximum vertex Count
	 * @param minRatio minimum ratio of width to height
	 * @param maxRatio maximum ratio of width to height
	 */
	private void filterContours(List<MatOfPoint> inputContours, double minArea,
		double minPerimeter, double minWidth, double maxWidth, double minHeight, double
		maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
		minRatio, double maxRatio, List<MatOfPoint> output) {
		final MatOfInt hull = new MatOfInt();
		output.clear();
		//operation
		for (int i = 0; i < inputContours.size(); i++) {
			final MatOfPoint contour = inputContours.get(i);
			final Rect bb = Imgproc.boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			final double area = Imgproc.contourArea(contour);
			if (area < minArea) continue;
			if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
			Imgproc.convexHull(contour, hull);
			MatOfPoint mopHull = new MatOfPoint();
			mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
			for (int j = 0; j < hull.size().height; j++) {
				int index = (int)hull.get(j, 0)[0];
				double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
				mopHull.put(j, 0, point);
			}
			final double solid = 100 * area / Imgproc.contourArea(mopHull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
			final double ratio = bb.width / (double)bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.add(contour);
		}
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
