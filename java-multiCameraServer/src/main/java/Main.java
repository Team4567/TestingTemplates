
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

    NetworkTable nt = ntinst.getTable("TapePipeline");
    NetworkTableEntry eNumContours = nt.getEntry("NumContours");
    NetworkTableEntry eTargetYaw = nt.getEntry("Yaw");
    NetworkTableEntry eTargetLock = nt.getEntry("Lock");
    
    nt.getEntry("MinHue").setDouble( TapePipelineNew.getThresholdHue()[0] );
    nt.getEntry("MinHue").addListener( event -> { 
        TapePipelineNew.setThresholdHue( event.value.getDouble(), TapePipelineNew.getThresholdHue()[1]); 
      }, EntryListenerFlags.kUpdate );

    nt.getEntry("MaxHue").setDouble( TapePipelineNew.getThresholdHue()[1] );
    nt.getEntry("MaxHue").addListener( event -> { 
        TapePipelineNew.setThresholdHue( TapePipelineNew.getThresholdHue()[0], event.value.getDouble() ); 
      }, EntryListenerFlags.kUpdate );

    nt.getEntry("MinSaturation").setDouble( TapePipelineNew.getThresholdSaturation()[0] );
    nt.getEntry("MinSaturation").addListener( event -> { 
        TapePipelineNew.setThresholdSaturation( event.value.getDouble(), TapePipelineNew.getThresholdSaturation()[1]); 
      }, EntryListenerFlags.kUpdate );

    nt.getEntry("MaxSaturation").setDouble( TapePipelineNew.getThresholdSaturation()[1] );
    nt.getEntry("MaxSaturation").addListener( event -> { 
        TapePipelineNew.setThresholdSaturation( TapePipelineNew.getThresholdSaturation()[0], event.value.getDouble() ); 
      }, EntryListenerFlags.kUpdate );

    nt.getEntry("MinLuminance").setDouble( TapePipelineNew.getThresholdLuminance()[0] );
    nt.getEntry("MinLuminance").addListener( event -> { 
        TapePipelineNew.setThresholdLuminance( event.value.getDouble(), TapePipelineNew.getThresholdLuminance()[1] ); 
      }, EntryListenerFlags.kUpdate );

    nt.getEntry("MaxLuminance").setDouble( TapePipelineNew.getThresholdLuminance()[1] );
    nt.getEntry("MaxLuminance").addListener( event -> { 
        TapePipelineNew.setThresholdLuminance( TapePipelineNew.getThresholdLuminance()[0], event.value.getDouble() ); 
      }, EntryListenerFlags.kUpdate );

    nt.getEntry("ContoursMinArea").setDouble( TapePipelineNew.getfilterContoursMinArea() );
    nt.getEntry("ContoursMinArea").addListener( event -> { 
        TapePipelineNew.setContoursMinArea( event.value.getDouble() ); 
      }, EntryListenerFlags.kUpdate );
  
    // start cameras
    List<VideoSource> cameras = new ArrayList<>();
    for (CameraConfig cameraConfig : cameraConfigs) {
      cameras.add(startCamera(cameraConfig));
    }

    // start image processing on camera 0 if present
    if (cameras.size() >= 1) {
      VideoMode m = cameras.get(0).getVideoMode();
      CvSource threshold = CameraServer.getInstance().putVideo("Threshold", m.width, m.height);
      CvSource output    = CameraServer.getInstance().putVideo("Output", m.width, m.height);

      /*
      VisionThread visionThread = new VisionThread(cameras.get(0),
              new MyPipeline(), pipeline -> {
        // do something with pipeline results
//                System.out.println("Hi, I'm running");
      });
      */
      VisionThread visionThread = new VisionThread(cameras.get(0),
              new TapePipelineNew(), pipeline -> {
                threshold.putFrame( pipeline.hslThresholdOutput() );
                output.putFrame( pipeline.output() );
                // System.out.println("Found contours: " + pipeline.findContoursOutput().size() );
                eNumContours.setDouble( pipeline.filterContoursOutput().size() );
                eTargetYaw.setDouble( pipeline.getTargetYaw() );
                eTargetLock.setBoolean( !Double.isNaN( pipeline.getTargetYaw() ) );
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
}
