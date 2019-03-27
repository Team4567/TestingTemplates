/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;
import frc.robot.commands.*;
import frc.robot.subsystems.*;





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
  //Subsystems
  public static Drivetrain drive;
  public static Elevator upper;
  public static ScoringMech score;
  public static PlatformClimber platformer;
  //Commands
  public static CommandGroup m_autonomousCommand;
  public static TeleOpDrive teleOp;
  //Interfaces/Controllers
  public static DriverStation ds = DriverStation.getInstance();
  public static XboxController xbC = new XboxController( 0 );
  public static boolean teleOpStarted = false;
  //NetworkTables
  private NetworkTableInstance inst = NetworkTableInstance.getDefault();
  private NetworkTable cmds = inst.getTable( "RobotCmds" );
  private NetworkTableEntry resetEncoder, fixGyro, gyroVal;
  //public static UsbCamera cam;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    //Subsystems/Commands
    drive = new Drivetrain();
    upper= new Elevator();
    platformer = new PlatformClimber();
    score = new ScoringMech();
    teleOp = new TeleOpDrive( xbC );
    resetEncoder = cmds.getEntry( "Reset Encoder" );
    fixGyro = cmds.getEntry( "Fix Gyro" );
    gyroVal = cmds.getEntry( "Gyro Value" );
    //cam = CameraServer.getInstance().startAutomaticCapture();
    //cam.setResolution(320, 240);
    //cam.setFPS( 15 );
    
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

  @Override
  public void autonomousInit() {
    drive.resetGyro();
    teleOp.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
    if( resetEncoder.getBoolean( false ) ){
      drive.leftMain.setSelectedSensorPosition( 0 );
      resetEncoder.setBoolean( false );
    }
    if( fixGyro.getBoolean( false ) ){
      drive.gyro.setYaw( gyroVal.getDouble( drive.getYaw() ) );
      fixGyro.setBoolean( false );
    }
  }

  @Override
  public void teleopInit() {
    System.out.println("TeleOpInit");
    if( !teleOpStarted ){
      teleOp.start();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    //System.out.println( teleOp.isRunning() );  
    if( resetEncoder.getBoolean( false ) ){
      drive.leftMain.setSelectedSensorPosition( 0 );
      resetEncoder.setBoolean( true );
    }
    if( fixGyro.getBoolean( false ) ){
      drive.gyro.setYaw( gyroVal.getDouble( drive.getYaw() ) );
      fixGyro.setBoolean( false );
    }
  }

  /**
   * This function is called periodically
   * 
   * 
   * 
   *  during test mode.
   *  Test Mode is Dumb
   */
  @Override
  public void testInit() {
    super.testInit();
  }
  @Override
  public void testPeriodic() {
    
  }

}
