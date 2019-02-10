
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

public class LinePipeline implements VisionPipeline 
{
	// Dynamic setting of Threshold values;
	private static double[] hsvThresholdHue = {40.0, 112.0};
	private static double[] hsvThresholdSaturation = {0.0, 29.0};
	private static double[] hsvThresholdValue = {204.0, 255.0};

	private static double filterContoursMinArea = 42.0;

	public static void setThresholdHue( double min, double max ) {
		hsvThresholdHue[0] = min;
		hsvThresholdHue[1] = max;
	}
	public static double[] getThresholdHue() {
		return hsvThresholdHue;
	}

	public static void setThresholdSaturation( double min, double max ) {
		hsvThresholdSaturation[0] = min;
		hsvThresholdSaturation[1] = max;
	}
	public static double[] getThresholdSaturation() {
		return hsvThresholdSaturation;
	}

	public static void setThresholdValue( double min, double max ) {
		hsvThresholdValue[0] = min;
		hsvThresholdValue[1] = max;
	}
	public static double[] getThresholdValue() {
		return hsvThresholdValue;
	}

	public static void setContoursMinArea( double min) {
		filterContoursMinArea = min;
	}
	public static double getfilterContoursMinArea() {
		return filterContoursMinArea;
	}
	
	//Outputs
	private static Mat hsvThresholdOutput = new Mat();
	private static ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private static ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<RotatedRect> findRotatedRectsOutput = new ArrayList<RotatedRect>();
	private static Mat output = new Mat();

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public void process( Mat source0, Rect crop ) {
		Mat subImage = source0.submat(crop);
		process(subImage);
	}

	public void process(Mat source0)
	{
		Mat hsvThresholdInput = source0;
		hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);

		Mat findContoursInput = hsvThresholdOutput;
		boolean findContoursExternalOnly = true;
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

		// Step Filter_Contours0:
		ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
		double filterContoursMinArea = 0;
		double filterContoursMinPerimeter = 0;
		double filterContoursMinWidth = 0;
		double filterContoursMaxWidth = 1000;
		double filterContoursMinHeight = 25.0;
		double filterContoursMaxHeight = 1000;
		double[] filterContoursSolidity = {0, 100};
		double filterContoursMaxVertices = 1000000;
		double filterContoursMinVertices = 0;
		double filterContoursMinRatio = 0;
		double filterContoursMaxRatio = 1000;
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);

		findRotatedRects( filterContoursOutput, findRotatedRectsOutput );

		output = source0.clone();
		renderContours(findRotatedRectsOutput, output);
	}

	public Mat hsvThresholdOutput() {
		return hsvThresholdOutput;
	}

	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}

	public ArrayList<MatOfPoint> filterContoursOutput() {
		return filterContoursOutput;
	}

	public ArrayList<RotatedRect> findRotatedRectsOutput() {
		return findRotatedRectsOutput;
	}

	public Mat output() {
		return output;
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


	private static void findContours(Mat input, boolean externalOnly,
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
	private static void filterContours(List<MatOfPoint> inputContours, double minArea,
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

	private void findRotatedRects( List<MatOfPoint> inputContours, List<RotatedRect> outputRotatedRects ) 
	{
		MatOfPoint2f mat2f = new MatOfPoint2f();
		for (int i = 0; i < inputContours.size(); i++) {
			inputContours.get(i).convertTo( mat2f, CvType.CV_32F );
			RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );
			outputRotatedRects.add(rotatedRect);
		}
	}

	private void renderContours( List<RotatedRect> rects, Mat output ) {
		// Scalar white = new Scalar(255,255,255);
		Scalar red   = new Scalar(0,0,255);
		Scalar white = new Scalar(255,255,255);
		
		for( RotatedRect rect : rects ) 
		{
			Point[] vertices = new Point[4];
			rect.points(vertices);
			for( int j=0; j<4; j++ )
				Imgproc.line( output, vertices[j], vertices[(j+1)%4], red );

			Point p = rect.center.clone();
			double angle = ( rect.size.width < rect.size.height ) ? rect.angle + 90 : rect.angle;
			double yaw = (rect.center.x - output.width()/2) / (output.width() / Camera.HORIZONTAL_FOV); 

			double dy = (Math.signum(angle) * 15);
			p.y += dy;
			Imgproc.putText( output, "/"+Math.floor(angle), p, Core.FONT_HERSHEY_PLAIN, 0.7, white );
			p.y += dy;
			Imgproc.putText( output, "("+Math.floor(rect.center.x)+","+Math.floor(rect.center.y)+")", p, Core.FONT_HERSHEY_PLAIN, 0.7, white );
			p.y += dy;
			Imgproc.putText( output, "Y: "+Math.floor(yaw), p, Core.FONT_HERSHEY_PLAIN, 0.7, white );
		}
	}
}

