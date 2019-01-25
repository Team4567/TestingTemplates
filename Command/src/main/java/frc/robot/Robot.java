/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.cscore.UsbCamera;


import frc.robot.autonomous.*;
import frc.robot.commands.*;
import frc.robot.pipelines.*;
import frc.robot.subsystems.*;

import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
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
  public static teleOpDrive teleOp;
  public static turnAngleTest turn;
  public static driveDistanceTest goDistance;
  public static elevatorPosition moveElev;
  public static testing test;
  public ArrayList<RotatedRect> contourRectsL;
  public ArrayList<RotatedRect> contourRectsR;
  public VisionThread visionThread;
  NetworkTableInstance inst;
  public static NetworkTable tableL,tableR,tableb,tableO;
  public NetworkTableEntry ngP,ngI,ngD,nmP,nmI,nmD;
  public static CommandGroup m_autonomousCommand;
  double sizeCont;
  pairTape pair;
  
  SendableChooser<CommandGroup> m_chooser = new SendableChooser<>();


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    inst=NetworkTableInstance.getDefault();
    tableL= inst.getTable("Left Contour Rects");
    tableb=inst.getTable("PID Values");
    tableO=inst.getTable("Ball Values");
    tableR=inst.getTable("Right Contour Rects");
    teleOp= new teleOpDrive(xbC);
    turn=new turnAngleTest();
    goDistance= new driveDistanceTest();
    moveElev= new elevatorPosition();
    test=new testing();
    contourRectsL= new ArrayList<RotatedRect>();
    contourRectsR= new ArrayList<RotatedRect>();
    pair= new pairTape(contourRectsL,contourRectsR);
    ngP= tableb.getEntry("Test Gyro P");
    ngI= tableb.getEntry("Test Gyro I");
    ngD= tableb.getEntry("Test Gyro D");
    nmP= tableb.getEntry("Test Motor P");
    nmI= tableb.getEntry("Test Motor I");
    nmD= tableb.getEntry("Test Motor D");
    ngP.setDouble(constants.gyroP);
    ngI.setDouble(constants.gyroI);
    ngD.setDouble(constants.gyroD);
    nmP.setDouble(constants.motorP);
    nmI.setDouble(constants.motorI);
    nmD.setDouble(constants.motorD);
    m_chooser.setDefaultOption("Default Auto, No Movement", new noMovement());
    m_chooser.addOption("Start: Left, Target: Cargo", new leftCargo());
    m_chooser.addOption("Start: Left, Target: Rocket", new leftRocket());
    m_chooser.addOption("Start: Right, Target: Cargo", new rightCargo());
    m_chooser.addOption("Start: Right, Target: Rocket", new rightRocket());
    m_chooser.addOption("Start: Center, Target: Left-Side Cargo", new centerLCargo());
    m_chooser.addOption("Start: Center, Target: Left-Side Rocket", new centerLRocket());
    m_chooser.addOption("Start: Center, Target: Right-side Rocket", new centerRCargo());
    m_chooser.addOption("Start: Center, Target: Right-Side Rocket", new centerRRocket());
    SmartDashboard.putData("Auto mode", m_chooser);
    UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
    camera.setResolution(constants.camW, constants.camH);
    camera.setFPS(constants.camFPS);
    // Thank you WPILIB
    visionThread = new VisionThread(camera, new BallPipeline(), pipeline -> {
      
      if (!pipeline.filterContoursOutput().isEmpty()) {
          int i;
          sizeCont=pipeline.filterContoursOutput().size();
            for(i=0;i<pipeline.filterContoursOutput().size();i++){
              NetworkTableEntry xEntry= tableO.getEntry("x of "+i);
              NetworkTableEntry yEntry= tableO.getEntry("y of "+i);
              Moments m= Imgproc.moments(pipeline.filterContoursOutput().get(i),false);
              xEntry.setDouble((int)(m.get_m10()/m.get_m00()));
              yEntry.setDouble((int)(m.get_m01()/m.get_m00()));
              //MatOfPoint2f dst = new MatOfPoint2f();
              //pipeline.findContoursOutput().get(i).convertTo(dst, CvType.CV_32F);
              //RotatedRect r = Imgproc.minAreaRect(dst);
              /*if(r.angle>44){
                contourRectsL.add(r);
              }else{
                contourRectsR.add(r);
              }*/
              
            }
          
        } else {
          //System.out.println("No contours found!");
        }
        System.out.println(pipeline.filterContoursOutput().size());
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
    /*if(m_autonomousCommand !=null){
      m_autonomousCommand.start();
    }else{
      CommandGroup emerg= new noMovement();
      emerg.start();
    }*/

    drive.resetGyro();
    turn.setSetpoint(0);
    turn.start();
    
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
    for(int i=0;i<sizeCont;i++){
      NetworkTableEntry xEntry=tableO.getEntry("x of "+i);
      NetworkTableEntry yEntry=tableO.getEntry("y of "+i);
      double error= -xEntry.getDouble(0.0);
      if(xbC.getAButton()){
        teleOp.cancel();
        drive.drive(0,.00625*error);
      } else{
        teleOp.start();
      }
    }
    
    /*if(xbC.getAButtonPressed()){
      turn.setSetpoint(180);
      turn.start();
    }
    if(xbC.getBButtonPressed()){
      turn.cancel();
      goDistance.cancel();
      test.cancel();
    }
    if(xbC.getXButtonPressed()){
      goDistance.setSetpoint((int)(((10*12)/constants.wheelCirc)*4096));
      goDistance.start();
    }
    if(xbC.getYButtonPressed()){
      goDistance.cancel();
    }
    if(xbC.getBackButtonPressed()){
      drive.resetGyro();
      drive.rightMain.setSelectedSensorPosition(0);
    }
    if(xbC.getStartButtonPressed()){
      test.start();
    }
    */
    turn.P=ngP.getDouble(0);
    turn.I=ngI.getDouble(0);
    turn.D=ngD.getDouble(0);
    goDistance.P=nmP.getDouble(0);
    goDistance.I=nmI.getDouble(0);
    goDistance.D=nmD.getDouble(0);
    //System.out.println(goDistance.tR.getSelectedSensorPosition() + ", "+ drive.getYaw());
  }

  /**
   * This function is called periodically
   * 
   * 
   * 
   *  during test mode.
   */
  @Override
  public void testInit() {
    super.testInit();
    teleOp.start();
  }
  @Override
  public void testPeriodic() {
    
  }
}
