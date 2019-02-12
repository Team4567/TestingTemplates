
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

import org.opencv.core.Rect;

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
  private static String configFile = "/boot/frc.json";

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

    NetworkTable nt = ntinst.getTable("Pipelines");
    NetworkTableEntry eNumContours = nt.getEntry("NumContours");
    NetworkTableEntry eTargetYaw = nt.getEntry("Yaw");
    NetworkTableEntry eLineAngle = nt.getEntry("LineAngle");
    NetworkTableEntry eTargetLock = nt.getEntry("Lock");

    initializeNetworkTables( nt );

    // start cameras
    List<VideoSource> cameras = new ArrayList<>();
    for (CameraConfig cameraConfig : cameraConfigs) {
      cameras.add(startCamera(cameraConfig));
    }

    // start image processing on camera 0 if present
    if (cameras.size() >= 1) {
      VideoMode m = cameras.get(0).getVideoMode();
//      CvSource lineOutput = CameraServer.getInstance().putVideo("Line", m.width, m.height);
      CvSource output    = CameraServer.getInstance().putVideo("Output", m.width, m.height);
      LinePipeline linePipeline = new LinePipeline();

      VisionThread visionThread = new VisionThread(cameras.get(0),
              new TapePipeline(), pipeline -> {
//                threshold.putFrame( pipeline.hslThresholdOutput() );
                eNumContours.setDouble( pipeline.filterContoursOutput().size() );

                TargetInfo ti = pipeline.getTargetInfo();
                if( ti != null ) {
                  eTargetYaw.setDouble( pipeline.getTargetInfo().getYaw() );
                  eTargetLock.setBoolean( true );

                  // Have a target, now look for the line.
                  // We only need to look at the lower half of the screen, crop image 
                  // Render over the output from the main pipeline.
//                  int width = pipeline.input().width();
                  int height = pipeline.input().height();
//                  linePipeline.process( pipeline.input(), new Rect( 0, (int)height/2, width, height/2 ), ti );
                  linePipeline.process( pipeline.input(), new Rect( (int)ti.minX, (int)height/2, (int)(ti.maxX-ti.minX), height/2 ), ti );
//                  lineOutput.putFrame( linePipeline.hsvThresholdOutput() );
//                  linePipeline.renderContours(linePipeline.findRotatedRectsOutput(), pipeline.output(), 0, height/2 );
                  linePipeline.renderContours(linePipeline.findRotatedRectsOutput(), pipeline.output(), (int)ti.minX, height/2 );
                  eLineAngle.setDouble( linePipeline.getLineAngle() );
                }
                else {
                  eTargetYaw.setDouble( Double.NaN );
                  eTargetLock.setBoolean( false );
                }
                output.putFrame( pipeline.output() );
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

  private static void initializeNetworkTables( NetworkTable pTable ) 
  {
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
