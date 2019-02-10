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
//			if(midpoints.get(m)-(Camera.IMAGE_WIDTH/2)<min){
//				closestMI=m;
//				min= midpoints.get(m)-(Camera.IMAGE_WIDTH/2);
//			}
        }
        
        double targetMidpoint = 0.0;
		if(closestMI!=-1){
			targetMidpoint=midpoints.get(closestMI);
		}

        return targetMidpoint;
    }

    // This routine finds a target (tape pair) and returns the X to the center and approx distance
    // If no complete target is found it returns null
    public static TargetInfo findTargetLockInfoJames( List<MatOfPoint> inputContours, int frameWidth, int frameHeight ) 
    {
        TargetInfo ti = null; // This is null until we lock on to a target

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
    
        // Calculate rotatedRectangles for each contour
        // Because we are processing them in order, rects will also be in descending size order.
        ArrayList<RotatedRect> rotatedRects = new ArrayList<RotatedRect>();
        MatOfPoint2f mat2f = new MatOfPoint2f();
        for( int i = 0; i < inputContours.size(); i++ ) {
            inputContours.get(i).convertTo( mat2f, CvType.CV_32F );
            rotatedRects.add( Imgproc.minAreaRect( mat2f ) );
        }

        for( int i=0; i< rotatedRects.size(); i++ ) {
            RotatedRect rrect1 = rotatedRects.get(i);
            int bestMatchIndex = -1;  // We'll be looking for the target that is the best match, -1, none yet.

            double angle = ( rrect1.size.width < rrect1.size.height ) ? rrect1.angle + 90 : rrect1.angle;
            if( angle > 0.0 ) {
                // We have a right side, look for the closest left side with x less than this x
                double maxX = 0.0;
                for( int j=0; j<rotatedRects.size(); j++ ) {  // j is the target number that is the next candidate
                    RotatedRect rrect2 = rotatedRects.get(j);

                    double angle2 = ( rrect2.size.width < rrect2.size.height ) ? rrect2.angle + 90 : rrect2.angle;
                    if( angle2 < 0.0 && rrect2.center.x < rrect1.center.x && rrect2.center.x > maxX ) {
                        // Found a closer left side
                        maxX = rrect2.center.x;
                        bestMatchIndex = j;
                    }
                }
            } else if( angle < 0.0 ) {
                // We have a left side, look for the closest right side with x greater than this x
                double minX = 9999.0; // wider than any image
                for( int j=0; j<rotatedRects.size(); j++ ) {  // j is the target number that is the next candidate
                    RotatedRect rrect2 = rotatedRects.get(j);

                    double angle2 = ( rrect2.size.width < rrect2.size.height ) ? rrect2.angle + 90 : rrect2.angle;
                    if( angle2 > 0.0 && rrect2.center.x > rrect1.center.x && rrect2.center.x < minX ) {
                        // Found a closer left side
                        minX = rrect2.center.x;
                        bestMatchIndex = j;
                    }
                }
            }

            if( bestMatchIndex >= 0 ) {  // we found a match for target i!

                double centerX = (rotatedRects.get(i).center.x + rotatedRects.get(bestMatchIndex).center.x) / 2;
                double centerY = (rotatedRects.get(i).center.y + rotatedRects.get(bestMatchIndex).center.y) / 2;

                // for height we don't want to use rotated rect since the angle throws off the height.
                // calculate the bounding rectangle on the original contour instead.
                Rect rect1 = Imgproc.boundingRect( inputContours.get(i) );
                Rect rect2 = Imgproc.boundingRect( inputContours.get(bestMatchIndex) );
                double centerHeight = (rect1.height + rect2.height) / 2;

                double distance = Camera.estimateDistance(TARGET_HEIGHT_INCHES, centerHeight, frameHeight );
                double yaw      = Camera.yawToHorizPixel( centerX, frameWidth );

                ti = new TargetInfo( centerX, centerY, centerHeight, distance, yaw );
                break;                
            }
        }

        return ti;
    }
}
