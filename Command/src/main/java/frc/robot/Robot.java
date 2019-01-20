/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.commands.*;
import frc.robot.pipelines.tapePipeline;
import frc.robot.subsystems.*;
import edu.wpi.cscore.UsbCamera;
import java.util.ArrayList;
import org.opencv.core.RotatedRect;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
/* Oh hi Sans
░░░░░░░░██████████████████
░░░░████░░░░░░░░░░░░░░░░░░████
░░██░░░░░░░░░░░░░░░░░░░░░░░░░░██
░░██░░░░░░░░░░░░░░░░░░░░░░░░░░██
██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░██
██░░░░░░░░░░░░░░░░░░░░██████░░░░██
██░░░░░░░░░░░░░░░░░░░░██████░░░░██
██░░░░██████░░░░██░░░░██████░░░░██
░░██░░░░░░░░░░██████░░░░░░░░░░██
████░░██░░░░░░░░░░░░░░░░░░██░░████
██░░░░██████████████████████░░░░██
██░░░░░░██░░██░░██░░██░░██░░░░░░██
░░████░░░░██████████████░░░░████
░░░░░░████░░░░░░░░░░░░░░████
░░░░░░░░░░██████████████

*/
public class Robot extends TimedRobot {
  public static drivetrain drive = new drivetrain();
  public static elevator upper = new elevator();
  public static scoringMech score=new scoringMech();
  public static DriverStation ds = DriverStation.getInstance();
  public XboxController xbC= new XboxController(0);
  public static Command m_autonomousCommand;
  public static teleOpDrive teleOp;
  public static turnAngle turn;
  public static driveDistance goDistance;
  public static elevatorPosition moveElev;
  public ArrayList<RotatedRect> contourRects;
  public VisionThread visionThread;
  NetworkTableInstance inst;
  NetworkTable table;

  
  SendableChooser<Command> m_chooser = new SendableChooser<>();


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    inst=NetworkTableInstance.getDefault();
    table= inst.getTable("Contour Rects");
    teleOp= new teleOpDrive(xbC);
    turn=new turnAngle();
    goDistance= new driveDistance();
    moveElev= new elevatorPosition();
    contourRects= new ArrayList<RotatedRect>();
    //m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    SmartDashboard.putData("Auto mode", m_chooser);
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setResolution(256, 144);
    // Thank you WPILIB
    visionThread = new VisionThread(camera, new tapePipeline(), pipeline -> {
      MatOfPoint2f dst = new MatOfPoint2f();
      if (!pipeline.findContoursOutput().isEmpty()) {
        
            for(int i=0;i<pipeline.findContoursOutput().size();i++){
              pipeline.findContoursOutput().get(i).convertTo(dst, CvType.CV_32F);
              RotatedRect r = Imgproc.minAreaRect(dst);
              contourRects.add(r);
            }
        }
    });
    visionThread.start();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    drive.findGyroVals();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   * You can use it to reset any subsystem information you want to clear when
   * the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString code to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons
   * to the switch structure below with additional strings & commands.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_chooser.getSelected();
    drive.resetGyro();
    turn.setSetpoint(0);
    turn.start();
    
    /*
     * String autoSelected = SmartDashboard.getString("Auto Selector",
     * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
     * = new MyAutoCommand(); break; case "Default Auto": default:
     * autonomousCommand = new ExampleCommand(); break; }
     */

    // schedule the autonomous command (example)
    //if (m_autonomousCommand != null) {
      //m_autonomousCommand.start();
    //}
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    
    
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    teleOp.start();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    System.out.println(drive.getYaw());
    
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testInit() {
    super.testInit();
    teleOp.start();
  }
  @Override
  public void testPeriodic() {
    if(xbC.getAButtonPressed()){
      for(int i=0;i<contourRects.size();i++){
        System.out.println("Angle of r #" + i);
        System.out.println(contourRects.get(i).angle);
        System.out.println(contourRects.get(i).center.x+ ", "+ contourRects.get(i).center.y);
        NetworkTableEntry xEntry = table.getEntry("x of rect " + i);
        NetworkTableEntry yEntry = table.getEntry("y of rect " + i);
        NetworkTableEntry angleEntry = table.getEntry("angle of rect " + i);
        xEntry.setDouble(contourRects.get(i).center.x);
        yEntry.setDouble(contourRects.get(i).center.y);
        angleEntry.setDouble(contourRects.get(i).angle);
      }  
    }
  }
}
