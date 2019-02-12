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

class TapeFinder {
    public static final double TARGET_HEIGHT_INCHES = 6.0;

    // This routine finds a target (tape pair) and returns the X to the center and approx distance
    // If no complete target is found it returns null
    public static TapeInfo findTargetLockInfo( List<MatOfPoint> inputContours, int frameWidth, int frameHeight ) 
    {
        TapeInfo ti = null; // This is null until we lock on to a target

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

        // Found a bug that makes this quite a bit more complicated.
        // If we start with the largest rect, looking for match might skip past a rect that would be
        // closer to the match.  Consider:  / \ \
        // This is certainly not expected by noise can make this happen.
        // Once we find the bestMatch we then look for the best match for that side.
        // We'd hope to get back the one we started with but we'll take the closest even if not.

        for( int i=0; i< rotatedRects.size(); i++ ) {

            int bestMatchIndex1 = findMatch( rotatedRects.get(i), rotatedRects );
            if( bestMatchIndex1 >= 0 ) {  // we found a match for target i!
                int bestMatchIndex2 = findMatch( rotatedRects.get(bestMatchIndex1), rotatedRects );
                if( bestMatchIndex2 <= 0 ) {
                    // Something really bad going on here. We know there is a match, we started with it.
                    // We'll just punt on this rect.
                    continue;
                }

                double centerX = (rotatedRects.get(bestMatchIndex1).center.x + rotatedRects.get(bestMatchIndex2).center.x) / 2;
                double centerY = (rotatedRects.get(bestMatchIndex1).center.y + rotatedRects.get(bestMatchIndex2).center.y) / 2;

                // for height we don't want to use rotated rect since the angle throws off the height.
                // calculate the bounding rectangle on the original contour instead.
                Rect rect1 = Imgproc.boundingRect( inputContours.get(bestMatchIndex1) );
                Rect rect2 = Imgproc.boundingRect( inputContours.get(bestMatchIndex2) );
                double centerHeight = (rect1.height + rect2.height) / 2;

                double distance = Camera.estimateDistance(TARGET_HEIGHT_INCHES, centerHeight, frameHeight );
                double yaw      = Camera.yawToHorizPixel( centerX, frameWidth );

                double minX = Math.min( rect1.x, rect2.x );
                double maxX = Math.max( rect1.x+rect1.width, rect2.x+rect2.width );

                ti = new TapeInfo( centerX, centerY, centerHeight, distance, yaw, minX, maxX );
                break;                
            }
        }

        return ti;
    }

    private static int findMatch( RotatedRect rrect1, List<RotatedRect> rotatedRects ) 
    {
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

        return bestMatchIndex;
    }
}
