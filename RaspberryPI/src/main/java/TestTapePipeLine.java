
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


class TestTapePipeLine {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

    private static BufferedImage Mat2BufferedImage(Mat m) {
		// Fastest code
		// output can be assigned either to a BufferedImage or to an Image
	
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		//DataBufferByte a;
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;
	}

	private static void displayImage(Image img2) {

		//BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
		ImageIcon icon=new ImageIcon(img2);
		JFrame frame=new JFrame();
		frame.setLayout(new FlowLayout());        
		frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
		JLabel lbl=new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	public static void main(String[] args) {
		Scalar white = new Scalar(255,255,255);
		Scalar black = new Scalar(0,0,0);
		Scalar red   = new Scalar(0,0,255);  // BGR - Ugh!

		String path = "C:/Users/Mary/Documents/GitHub/TestingTemplates/GRIP Files/2019VisionImages/Green/Straight/RocketPanelStraightDark48in.jpg";
		Mat logo = Imgcodecs.imread(path);

		BufferedImage bi1 = Mat2BufferedImage(logo);
		displayImage( bi1 );

		TapePipeline tp = new TapePipeline();

		tp.process(logo);

		BufferedImage bi2 = Mat2BufferedImage( tp.hslThresholdOutput() );
		displayImage( bi2 );

//		ArrayList<MatOfPoint> contours = tp.findContoursOutput();
		ArrayList<MatOfPoint> contours = tp.getFilteredContours();

		Mat contourImg = new Mat(logo.size(), logo.type(), black );


		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(contourImg, contours, i, white, -1 );

			Rect rect = Imgproc.boundingRect(contours.get(i));
			Imgproc.rectangle(contourImg, rect.tl(), rect.br(), red );
		}

		BufferedImage bi3 = Mat2BufferedImage(contourImg);
		displayImage( bi3 );

		TapeFinder.findTapeLockInfo( contours, contourImg.width(), contourImg.height(), null );
	}

}

