
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
	private static double[] hsvThresholdHue = {0.0, 255.0};
	private static double[] hsvThresholdSaturation = {0.0, 29.0};
	private static double[] hsvThresholdValue = {100.0, 255.0};

	private static double filterContoursMinArea = 50.0;
	private static double[] rectRatio = {0.0, 0.15};

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
	public static void setRotatedRectRatio( double min, double max ) {
		rectRatio[0] = min;
		rectRatio[1] = max;
	}
	public static double[] getRotatedRectRatio() {
		return rectRatio;
	}
	
	//Outputs
	private Mat hsvThresholdOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
	private ArrayList<RotatedRect> findRotatedRectsOutput = new ArrayList<RotatedRect>();
	private double lineAngle = Double.NaN;

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
		boolean findContoursExternalOnly = false;
		findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

		// Step Filter_Contours0:
		ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
//		double filterContoursMinArea = 0;
		double filterContoursMinPerimeter = 0;
		double filterContoursMinWidth = 0;
		double filterContoursMaxWidth = 1000;
		double filterContoursMinHeight = 0.0;
		double filterContoursMaxHeight = 1000;
		double[] filterContoursSolidity = {0, 100};
		double filterContoursMaxVertices = 1000000;
		double filterContoursMinVertices = 0;
		double filterContoursMinRatio = 0;
		double filterContoursMaxRatio = 1000;
		filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);

		findRotatedRects( filterContoursOutput, findRotatedRectsOutput );
//		findRotatedRects( findContoursOutput, findRotatedRectsOutput );
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

	public double getLineAngle() {
		return lineAngle;
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


	private static void findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) 
	{
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
		double angle = Double.NaN;
		MatOfPoint2f mat2f = new MatOfPoint2f();

		for (int i = 0; i < inputContours.size(); i++) 
		{
			inputContours.get(i).convertTo( mat2f, CvType.CV_32F );
			RotatedRect rect = Imgproc.minAreaRect( mat2f );

			double ratio = (rect.size.width < rect.size.height) ? rect.size.width/rect.size.height : rect.size.height/rect.size.width;

			// If ratio is good add the RotatedRect to output
			if( ratio >= rectRatio[0] && ratio <= rectRatio[1] ) {
				outputRotatedRects.add(rect);
				angle = ( rect.size.width < rect.size.height ) ? rect.angle + 90 : rect.angle;
			}
		}

		// We have a good line only if there is only one found.
		lineAngle = (outputRotatedRects.size() == 1) ? angle : Double.NaN;
	}

	public void renderContours( List<RotatedRect> rects, Mat output, int offsetX, int offsetY ) 
	{
		Scalar red   = new Scalar(0,0,255);
		Scalar white = new Scalar(255,255,255);
		double fontScale = (output.width() > 320 ? 1.0 : 0.5);
		
		for( RotatedRect rect : rects ) 
		{
			Point[] vertices = new Point[4];
			rect.points(vertices);
			// add offsets first
			for( int i=0; i<4; i++ ) {
				vertices[i].x += offsetX;
				vertices[i].y += offsetY;
			}

			for( int i=0; i<4; i++ )
				Imgproc.line( output, vertices[i], vertices[(i+1)%4], red );

			Point p = rect.center.clone();
			p.x += offsetX;
			p.y += offsetY;

			double angle = ( rect.size.width < rect.size.height ) ? rect.angle + 90 : rect.angle;

			double dy = (Math.signum(angle) * 15);
			p.y += dy;
			Imgproc.putText( output, "/"+(int)angle, p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
			p.y += dy;
			Imgproc.putText( output, "("+(int)rect.center.x+","+(int)rect.center.y+")", p, Core.FONT_HERSHEY_PLAIN, fontScale, white );
		}
	}
}

