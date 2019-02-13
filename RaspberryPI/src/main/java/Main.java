
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionThread;

/*
   JSON format:
   {
       "team": <team number>,
       "ntmode": <"client" or "server", "client" if unspecified>
       "cameras": [
           {
               "name": <camera name>
               "path": <path, e.g. "/dev/video0">
               "pixel format": <"MJPEG", "YUYV", etc>   // optional
               "width": <video mode width>              // optional
               "height": <video mode height>            // optional
               "fps": <video mode fps>                  // optional
               "brightness": <percentage brightness>    // optional
               "white balance": <"auto", "hold", value> // optional
               "exposure": <"auto", "hold", value>      // optional
               "properties": [                          // optional
                   {
                       "name": <property name>
                       "value": <property value>
                   }
               ],
               "stream": {                              // optional
                   "properties": [
                       {
                           "name": <stream property name>
                           "value": <stream property value>
                       }
                   ]
               }
           }
       ]
   }
 */

public final class Main {
  private static String  configFile = "/boot/frc.json";
  private static boolean debug = false;
  private static double  testHeight = 28.5;
  private static int     frameCount=0;

//  @SuppressWarnings("MemberName")
  public static class CameraConfig {
    public String name;
    public String path;
    public JsonObject config;
    public JsonElement streamConfig;
  }

  public static int team;
  public static boolean server;
  public static List<CameraConfig> cameraConfigs = new ArrayList<>();

  private Main() {
  }

  /**
   * Report parse error.
   */
  public static void parseError(String str) {
    System.err.println("config error in '" + configFile + "': " + str);
  }

  /**
   * Read single camera configuration.
   */
  public static boolean readCameraConfig(JsonObject config) {
    CameraConfig cam = new CameraConfig();

    // name
    JsonElement nameElement = config.get("name");
    if (nameElement == null) {
      parseError("could not read camera name");
      return false;
    }
    cam.name = nameElement.getAsString();

    // path
    JsonElement pathElement = config.get("path");
    if (pathElement == null) {
      parseError("camera '" + cam.name + "': could not read path");
      return false;
    }
    cam.path = pathElement.getAsString();

    // stream properties
    cam.streamConfig = config.get("stream");

    cam.config = config;

    cameraConfigs.add(cam);
    return true;
  }

  /**
   * Read configuration file.
   */
//  @SuppressWarnings("PMD.CyclomaticComplexity")
  public static boolean readConfig() {
    // parse file
    JsonElement top;
    try {
      top = new JsonParser().parse(Files.newBufferedReader(Paths.get(configFile)));
    } catch (IOException ex) {
      System.err.println("could not open '" + configFile + "': " + ex);
      return false;
    }

    // top level must be an object
    if (!top.isJsonObject()) {
      parseError("must be JSON object");
      return false;
    }
    JsonObject obj = top.getAsJsonObject();

    // team number
    JsonElement teamElement = obj.get("team");
    if (teamElement == null) {
      parseError("could not read team number");
      return false;
    }
    team = teamElement.getAsInt();

    // ntmode (optional)
    if (obj.has("ntmode")) {
      String str = obj.get("ntmode").getAsString();
      if ("client".equalsIgnoreCase(str)) {
        server = false;
      } else if ("server".equalsIgnoreCase(str)) {
        server = true;
      } else {
        parseError("could not understand ntmode value '" + str + "'");
      }
    }

    // cameras
    JsonElement camerasElement = obj.get("cameras");
    if (camerasElement == null) {
      parseError("could not read cameras");
      return false;
    }
    JsonArray cameras = camerasElement.getAsJsonArray();
    for (JsonElement camera : cameras) {
      if (!readCameraConfig(camera.getAsJsonObject())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Start running the camera.
   */
  public static VideoSource startCamera(CameraConfig config) {
    System.out.println("Starting camera '" + config.name + "' on " + config.path);
    CameraServer inst = CameraServer.getInstance();
    UsbCamera camera = new UsbCamera(config.name, config.path);
    MjpegServer server = inst.startAutomaticCapture(camera);

    Gson gson = new GsonBuilder().create();

    camera.setConfigJson(gson.toJson(config.config));
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen);

    if (config.streamConfig != null) {
      server.setConfigJson(gson.toJson(config.streamConfig));
    }

    return camera;
  }

  /**
   * Main.
   */
  public static void main(String... args) {
    if (args.length > 0) {
      configFile = args[0];
    }

    // read configuration
    if (!readConfig()) {
      return;
    }

    // start NetworkTables
    NetworkTableInstance ntinst = NetworkTableInstance.getDefault();
    if (server) {
      System.out.println("Setting up NetworkTables server");
      ntinst.startServer();
    } else {
      System.out.println("Setting up NetworkTables client for team " + team);
      ntinst.startClientTeam(team);
    }

    NetworkTableEntry eDebug = ntinst.getEntry("Debug");
    eDebug.setBoolean(debug);
    eDebug.addListener( event -> { 
      debug = event.value.getBoolean(); 
    }, EntryListenerFlags.kUpdate );

    NetworkTableEntry eTestHeight = ntinst.getEntry("TestHeight");
    eTestHeight.setDouble( testHeight );
    eTestHeight.addListener( event -> { 
      testHeight = event.value.getDouble(); 
    }, EntryListenerFlags.kUpdate );

    NetworkTable nt = ntinst.getTable("TargetInfo");

    // All values will be Double.NaN if not valid
    NetworkTableEntry eAngleToTape      = nt.getEntry("AngleToTape");         
    NetworkTableEntry eDistanceToTape   = nt.getEntry("DistanceToTape");
    NetworkTableEntry eLineAngle        = nt.getEntry("LineAngle");
    NetworkTableEntry eAngleToPerp      = nt.getEntry("AngleToPerp");     // Perpendicular from hatch wall
    NetworkTableEntry eDistanceToPerp   = nt.getEntry("DistanceToPerp");  // Perpendicular from hatch wall
    NetworkTableEntry eAngleToTarget    = nt.getEntry("AngleToTarget");   // Turn to face target
    NetworkTableEntry eDistanceToTarget = nt.getEntry("DistanceToTarget");
    NetworkTableEntry eTargetPathValid  = nt.getEntry("TargetPathValid"); // AngleToPerp, DistanceToPerp and AngelToTarget all good.
    NetworkTableEntry eNewDistance      = nt.getEntry("NewDistance");
    NetworkTableEntry eNewHeight        = nt.getEntry("NewHeight");

    initializePiplineParmsNetTable( ntinst );

    // start cameras
    List<VideoSource> cameras = new ArrayList<>();
    for (CameraConfig cameraConfig : cameraConfigs) {
      cameras.add(startCamera(cameraConfig));
    }

    // start image processing on camera 0 if present
    if (cameras.size() >= 1) {
      VideoMode m = cameras.get(0).getVideoMode();
//      CvSource debugOut = CameraServer.getInstance().putVideo("Debug", m.width, m.height);
      CvSource output   = CameraServer.getInstance().putVideo("Output", m.width, m.height);
      LinePipeline linePipeline = new LinePipeline();
      PathInfo pathInfo = new PathInfo();

      VisionThread visionThread = new VisionThread(cameras.get(0),
              new TapePipeline(), pipeline -> {
                Mat inFrame = pipeline.getInput();
//                debugOut.putFrame( pipeline.hslThresholdOutput() );

                TapeInfo tapeInfo = pipeline.getTapeInfo();
                if( tapeInfo != null ) {
                  eAngleToTape.setDouble( Math.round(tapeInfo.getAngle()*10.0)/10.0 );
                  eDistanceToTape.setDouble( Math.round(tapeInfo.getDistance()*10.0)/10.0 );

                  linePipeline.process( inFrame, tapeInfo );

//                  debugOut.putFrame( linePipeline.hsvThresholdOutput() );
                  eLineAngle.setDouble( Math.round(linePipeline.getLineAngle()*10.0)/10.0 );

                  pathInfo.init( tapeInfo.getDistance(), linePipeline.getLineAngle() ); 
                  if( pathInfo.isValidPath() ) {
                    double heightInPixels = Math.abs(tapeInfo.centerY - linePipeline.getLineMinY());
                    double newDistance = Camera.estimateDistance(testHeight, heightInPixels, pipeline.getInput().height() );
 
                    eNewHeight.setDouble(linePipeline.getLineMinY());
                    eNewDistance.setDouble(newDistance);

                    eAngleToPerp.setDouble( Math.round(pathInfo.getAngleToPerp()*10.0)/10.0 );
                    eDistanceToPerp.setDouble( Math.round(pathInfo.getDistanceToPerp()*10.0)/10.0 );
                    eAngleToTarget.setDouble( Math.round(pathInfo.getAngleToTarget()*10.0)/10.0 );
                    eDistanceToTarget.setDouble( Math.round(pathInfo.getDistanceToTarget()*10.0)/10.0 );
                    eTargetPathValid.setBoolean( true );
                  }
                }
                else {
                  eAngleToTape.setDouble( Double.NaN );
                  eDistanceToTape.setDouble( Double.NaN );
                  eLineAngle.setDouble( Double.NaN );
                  eAngleToPerp.setDouble( Double.NaN );
                  eDistanceToPerp.setDouble( Double.NaN );
                  eAngleToTarget.setDouble( Double.NaN );
                  eTargetPathValid.setBoolean( false );
                }

                pipeline.renderContours( pipeline.getfilteredRotatedRects(), inFrame, debug );
                linePipeline.renderContours(linePipeline.findRotatedRectsOutput(), inFrame, linePipeline.getCrop().x, linePipeline.getCrop().y, debug );
                if( pathInfo.isValidPath() ) 
                  renderPathInfo( inFrame, pathInfo );
                  
                output.putFrame( inFrame );

                if( (frameCount % 30) == 0 )
                  System.gc();
       });
      visionThread.start();
    }

    // loop forever
    for (;;) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) {
        return;
      }
    }
  }

  private static void renderPathInfo( Mat output, PathInfo pi ) 
  {
      Scalar color = new Scalar(255,255,255);
      double fontScale = (output.height() > 320 ? 1.0 : 0.7);
      double rowHeight = fontScale * 15;

      Point p = new Point( 3, (output.height()/2) - fontScale*15*2 );
      Imgproc.putText( output, "Ap: " + Math.round(pi.getAngleToPerp()*10.0)/10.0, p, Core.FONT_HERSHEY_PLAIN, fontScale, color );
      p.y += rowHeight;
      Imgproc.putText( output, "Dp: " + Math.round(pi.getDistanceToPerp()*10.0)/10.0, p, Core.FONT_HERSHEY_PLAIN, fontScale, color );
      p.y += rowHeight;
      Imgproc.putText( output, "At: " + Math.round(pi.getAngleToTarget()*10.0)/10.0, p, Core.FONT_HERSHEY_PLAIN, fontScale, color );
      p.y += rowHeight;
      Imgproc.putText( output, "Dt: " + Math.round(pi.getDistanceToTarget()*10.0)/10.0, p, Core.FONT_HERSHEY_PLAIN, fontScale, color );
  }

  private static void initializePiplineParmsNetTable( NetworkTableInstance ntinst ) 
  {
      NetworkTable pTable = ntinst.getTable("Pipelines");

      // TapePipeline Filter criteria
      NetworkTable nt = pTable.getSubTable("TapePipeline");

      nt.getEntry("TapeMinHue").setDouble( TapePipeline.getThresholdHue()[0] );
      nt.getEntry("TapeMinHue").addListener( event -> { 
          TapePipeline.setThresholdHue( event.value.getDouble(), TapePipeline.getThresholdHue()[1]); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeMaxHue").setDouble( TapePipeline.getThresholdHue()[1] );
      nt.getEntry("TapeMaxHue").addListener( event -> { 
          TapePipeline.setThresholdHue( TapePipeline.getThresholdHue()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeMinSaturation").setDouble( TapePipeline.getThresholdSaturation()[0] );
      nt.getEntry("TapeMinSaturation").addListener( event -> { 
          TapePipeline.setThresholdSaturation( event.value.getDouble(), TapePipeline.getThresholdSaturation()[1]); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeMaxSaturation").setDouble( TapePipeline.getThresholdSaturation()[1] );
      nt.getEntry("TapeMaxSaturation").addListener( event -> { 
          TapePipeline.setThresholdSaturation( TapePipeline.getThresholdSaturation()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeMinValue").setDouble( TapePipeline.getThresholdValue()[0] );
      nt.getEntry("TapeMinValue").addListener( event -> { 
          TapePipeline.setThresholdValue( event.value.getDouble(), TapePipeline.getThresholdValue()[1] ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeMaxValue").setDouble( TapePipeline.getThresholdValue()[1] );
      nt.getEntry("TapeMaxValue").addListener( event -> { 
          TapePipeline.setThresholdValue( TapePipeline.getThresholdValue()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeContoursMinArea").setDouble( TapePipeline.getfilterContoursMinArea() );
      nt.getEntry("TapeContoursMinArea").addListener( event -> { 
          TapePipeline.setContoursMinArea( event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeRotatedRectMinRatio").setDouble( TapePipeline.getRotatedRectRatio()[0] );
      nt.getEntry("TapeRotatedRectMinRatio").addListener( event -> { 
          TapePipeline.setRotatedRectRatio( event.value.getDouble(), TapePipeline.getRotatedRectRatio()[1] ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("TapeRotatedRectMaxRatio").setDouble( TapePipeline.getRotatedRectRatio()[1] );
      nt.getEntry("TapeRotatedRectMaxRatio").addListener( event -> { 
          TapePipeline.setRotatedRectRatio( TapePipeline.getRotatedRectRatio()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );
          
        // LinePipeline Filter criteria
      nt = pTable.getSubTable("LinePipeline");

      nt.getEntry("LineMinHue").setDouble( LinePipeline.getThresholdHue()[0] );
      nt.getEntry("LineMinHue").addListener( event -> { 
          LinePipeline.setThresholdHue( event.value.getDouble(), LinePipeline.getThresholdHue()[1]); 
        }, EntryListenerFlags.kUpdate );
  
      nt.getEntry("LineMaxHue").setDouble( LinePipeline.getThresholdHue()[1] );
      nt.getEntry("LineMaxHue").addListener( event -> { 
          LinePipeline.setThresholdHue( LinePipeline.getThresholdHue()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );
  
      nt.getEntry("LineMinSaturation").setDouble( LinePipeline.getThresholdSaturation()[0] );
      nt.getEntry("LineMinSaturation").addListener( event -> { 
          LinePipeline.setThresholdSaturation( event.value.getDouble(), LinePipeline.getThresholdSaturation()[1]); 
        }, EntryListenerFlags.kUpdate );
  
      nt.getEntry("LineMaxSaturation").setDouble( LinePipeline.getThresholdSaturation()[1] );
      nt.getEntry("LineMaxSaturation").addListener( event -> { 
          LinePipeline.setThresholdSaturation( LinePipeline.getThresholdSaturation()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );
  
      nt.getEntry("LineMinValue").setDouble( LinePipeline.getThresholdValue()[0] );
      nt.getEntry("LineMinValue").addListener( event -> { 
          LinePipeline.setThresholdValue( event.value.getDouble(), LinePipeline.getThresholdValue()[1] ); 
        }, EntryListenerFlags.kUpdate );
  
      nt.getEntry("LineMaxValue").setDouble( LinePipeline.getThresholdValue()[1] );
      nt.getEntry("LineMaxValue").addListener( event -> { 
          LinePipeline.setThresholdValue( LinePipeline.getThresholdValue()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );
  
      nt.getEntry("LineContoursMinArea").setDouble( LinePipeline.getfilterContoursMinArea() );
      nt.getEntry("LineContoursMinArea").addListener( event -> { 
          LinePipeline.setContoursMinArea( event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("LineRotatedRectMinRatio").setDouble( LinePipeline.getRotatedRectRatio()[0] );
      nt.getEntry("LineRotatedRectMinRatio").addListener( event -> { 
          LinePipeline.setRotatedRectRatio( event.value.getDouble(), LinePipeline.getRotatedRectRatio()[1] ); 
        }, EntryListenerFlags.kUpdate );

      nt.getEntry("LineRotatedRectMaxRatio").setDouble( LinePipeline.getRotatedRectRatio()[1] );
      nt.getEntry("LineRotatedRectMaxRatio").addListener( event -> { 
          LinePipeline.setRotatedRectRatio( LinePipeline.getRotatedRectRatio()[0], event.value.getDouble() ); 
        }, EntryListenerFlags.kUpdate );
            
      }
}
