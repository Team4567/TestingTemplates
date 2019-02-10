import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

class TargetFinder {
    public static final double TARGET_HEIGHT_INCHES = 6.0;

    public static double findXToTargetCenterJoe(List<MatOfPoint> inputContours ) {
        ArrayList<RotatedRect> lefts = new ArrayList<RotatedRect>();
        ArrayList<RotatedRect> rights = new ArrayList<RotatedRect>();
        ArrayList<Double>      midpoints= new ArrayList<Double>();
 
		MatOfPoint2f mat2f = new MatOfPoint2f();
		
		for (int i = 0; i < inputContours.size(); i++) {
			inputContours.get(i).convertTo( mat2f, CvType.CV_32F );
			RotatedRect rotatedRect = Imgproc.minAreaRect( mat2f );

            double angle = ( rotatedRect.size.width < rotatedRect.size.height ) ? rotatedRect.angle + 90 : rotatedRect.angle;

    		if( angle > 0.0 ) {
                // Positive angle have right side lifted, tilting left
                // That is the right side of a target
				rights.add( rotatedRect );
			} else {
				lefts.add( rotatedRect );
			}
        }
        
        // Avoid using l for a variable, looks like a one
        // Convention is that a single letter should i, j, or k
        // midway is the avg of the two x's, you want the min avg?
		for( int l=0; l < lefts.size(); l++ ) {
			int closestRI=-1;
			double min=9999999;
			for( int r=0; r < rights.size(); r++) {
				if( lefts.get(l).center.x < rights.get(r).center.x && rights.get(r).center.x - lefts.get(r).center.x < min) {
					closestRI=r;
					min = rights.get(r).center.x - lefts.get(r).center.x;
				}
			}
			midpoints.add( rights.get(closestRI).center.x-lefts.get(l).center.x );
		}
		int closestMI=-1;
		for( int m=0; m < midpoints.size(); m++ ) {
			double min=9999999;
			if(midpoints.get(m)-(Camera.IMAGE_WIDTH/2)<min){
				closestMI=m;
				min= midpoints.get(m)-(Camera.IMAGE_WIDTH/2);
			}
        }
        
        double targetMidpoint = 0.0;
		if(closestMI!=-1){
			targetMidpoint=midpoints.get(closestMI);
		}

        return targetMidpoint;
    }

    // This routine finds a target (tape pair) and returns the X to the center and approx distance
    // If no complete target is found it returns null
    public static double[] findTargetLockInfoJames( List<MatOfPoint> inputContours ) 
    {
        double[] ti = null;  // This is null until we lock on to a target

        // return nothing if less than 2 contours or more than 8 (too much noise)
        if( inputContours.size() < 2 || inputContours.size() > 8 ) {
            return ti;
        }

        // Sort from largest to smallest area contour
        Collections.sort(inputContours, new Comparator<MatOfPoint>() {
            @Override
            public int compare( MatOfPoint o1, MatOfPoint o2 ) {
                int area1 = (int)Math.floor( Imgproc.contourArea(o1) );
                int area2 = (int)Math.floor( Imgproc.contourArea(o2) );
                return -( area1 - area2 );  // Sort Reverse
            }
        });
    
        // Calculate Bounding Rectangles for each contour
        // Because we are processing them in order, rects will also be in descending size order.
        ArrayList<Rect> rects = new ArrayList<Rect>();
        for( int i = 0; i < inputContours.size(); i++ ) {
            rects.add( Imgproc.boundingRect( inputContours.get(i) ) );
        }

        // Calculate rotatedRectangles for each contour
        // Because we are processing them in order, rects will also be in descending size order.
        ArrayList<RotatedRect> rotatedRects = new ArrayList<RotatedRect>();
        MatOfPoint2f mat2f = new MatOfPoint2f();
        for( int i = 0; i < inputContours.size(); i++ ) {
            inputContours.get(i).convertTo( mat2f, CvType.CV_32F );
            rotatedRects.add( Imgproc.minAreaRect( mat2f ) );
        }

        double avgX = -1.0;
        for( int i=0; i< rotatedRects.size(); i++ ) {
            RotatedRect rr = rotatedRects.get(i);

            double angle = ( rr.size.width < rr.size.height ) ? rr.angle + 90 : rr.angle;
            if( angle > 0.0 ) {
                // We have a right side, look for the closest left side with x less than this x
                double maxX = 0.0;
                for (RotatedRect rect2 : rotatedRects ) {
                    double angle2 = ( rect2.size.width < rect2.size.height ) ? rect2.angle + 90 : rect2.angle;
                    if( angle2 < 0.0 && rect2.center.x < rr.center.x && rect2.center.x > maxX ) {
                        // Found a closer left side
                        maxX = rect2.center.x;
                    }
                }

                if( maxX != 0.0 ) {
                    // Found other half!
                    avgX = (rr.center.x + maxX)/2;
                }
            } else if( angle < 0.0 ) {
                // We have a left side, look for the closest right side with x greater than this x
                double minX = 9999.0; // wider than any image
                for (RotatedRect rect2 : rotatedRects ) {
                    double angle2 = ( rect2.size.width < rect2.size.height ) ? rect2.angle + 90 : rect2.angle;
                    if( angle2 > 0.0 && rect2.center.x > rr.center.x && rect2.center.x < minX ) {
                        // Found a closer left side
                        minX = rect2.center.x;
                    }
                }

                if( minX != 9999.0 ) {
                    // Found other half!
                    avgX = (rr.center.x + minX)/2;
                }
            }

            if( avgX != -1.0 ) {
                // Found match for target i
                // Use height of bounding rectagle i to calculate distance
                ti = new double[6];
                ti[0] = avgX;
                ti[1] = (TARGET_HEIGHT_INCHES/2.0) / Math.tan( Math.toRadians( (rects.get(i).height * Camera.VERTICAL_FOV)/480.0 ) );
                ti[2] = rects.get(i).x;
                ti[3] = rects.get(i).y;
                ti[4] = rects.get(i).width;
                ti[5] = rects.get(i).height;
                break;                
            }
        }

        return ti;
    }
}
