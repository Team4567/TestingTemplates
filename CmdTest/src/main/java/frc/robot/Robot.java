/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

import frc.robot.autonomous.*;
import frc.robot.commands.*;
import frc.robot.pipelines.*;
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
  public static drivetrain drive;
  public static elevator upper;
  public static scoringMech score;
  //Commands
  public static teleOpDrive teleOp;
  public static turnAngle turn,turnOut;
  public static driveDistance goDistance;
  public static elevatorPosition moveElev;
  private static testing test;
  public static alignVision align,alignOut;
  public static CommandGroup m_autonomousCommand;
  //Interfaces/Controllers
  public static DriverStation ds = DriverStation.getInstance();
  public static XboxController xbC= new XboxController(0);
  SendableChooser<CommandGroup> m_chooser = new SendableChooser<>();
  //NetworkTables
  NetworkTableInstance inst;
  private static NetworkTable tablePID,chickenVision;
  private NetworkTableEntry ngP,ngI,ngD,nmP,nmI,nmD,minValY,minValX;
  private NetworkTableEntry driveWanted,cargoWanted,tapeWanted,tapeYaw,cargoYaw;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    //Subsystems
    drive= new drivetrain();
    upper= new elevator();
    score= new scoringMech();
    //Commands
    teleOp= new teleOpDrive(xbC);
    turn=new turnAngle(false);
    turnOut= new turnAngle(true);
    align= new alignVision(false);
    alignOut= new alignVision(true);
    goDistance= new driveDistance();
    moveElev= new elevatorPosition();
    test=new testing();
    //Interfaces/Controllers
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
    //NetworkTables
    inst=NetworkTableInstance.getDefault();
    tablePID=inst.getTable("PID/Testing Values");
    ngP= tablePID.getEntry("Test Gyro P");
    ngI= tablePID.getEntry("Test Gyro I");
    ngD= tablePID.getEntry("Test Gyro D");
    nmP= tablePID.getEntry("Test Motor P");
    nmI= tablePID.getEntry("Test Motor I");
    nmD= tablePID.getEntry("Test Motor D");
    minValY= tablePID.getEntry("Min Val Test Y");
    minValX= tablePID.getEntry("Min Val Test X");
    ngP.setDouble(constants.gyroP);
    ngI.setDouble(constants.gyroI);
    ngD.setDouble(constants.gyroD);
    nmP.setDouble(constants.motorP);
    nmI.setDouble(constants.motorI);
    nmD.setDouble(constants.motorD);
    minValY.setDouble(0.0);
    minValX.setDouble(0.0);
    chickenVision=inst.getTable("ChickenVision");
    driveWanted = chickenVision.getEntry("Driver");
		tapeWanted = chickenVision.getEntry("Tape");
		cargoWanted = chickenVision.getEntry("Cargo");
    tapeYaw=chickenVision.getEntry("tapeYaw");
    cargoYaw=chickenVision.getEntry("cargoYaw");
    
    
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
    SmartDashboard.putNumber("Gyro Val", drive.getYaw());
    SmartDashboard.putNumber("Encoder Pos", drive.rightMain.getSelectedSensorPosition());
    SmartDashboard.putNumber("Turn Setpoint",turn.getSetpoint());
    SmartDashboard.putNumber("Turn Output",turn.getOutput());
    SmartDashboard.putNumber("Distance Setpoint Raw", goDistance.setpoint);
    SmartDashboard.putNumber("Distance Setpoint Inches", goDistance.setpoint/4096*constants.wheelCirc);
    SmartDashboard.putNumber("Distance Output",goDistance.output);
    SmartDashboard.putNumber("Encoder Speed",((drive.rightMain.getSelectedSensorVelocity()*10)/4096)*constants.wheelCirc);
    
    //Init Distance
    if(xbC.getAButtonPressed()){
      goDistance.setSetpoint((20*12)/constants.wheelCirc*4096);
      goDistance.start();
    }
    //EMERGENCY CANCEL ANY ACTIVE COMMAND THRU TELEOP. MAKE SURE CANCEL IS SET UP IN THE COMMANDS
    if(xbC.getBButtonPressed()){
      turn.cancel();
      goDistance.cancel();
      test.cancel();
    }
    //Steady Driving Testing, probably no longer needed
    if(xbC.getXButton()){
      drive.drive(minValY.getDouble(0.0),0);
    }
    // Reset Turning Setpoint(For 45 Degree Increment Testing)
    if(xbC.getYButtonPressed()){
      drive.drive(0,minValX.getDouble(0.0));
    }
    // Reset Positioning Devices
    if(xbC.getBackButtonPressed()){
      drive.resetGyro();
      drive.rightMain.setSelectedSensorPosition(0);
    }
    //Init turning
    if(xbC.getStartButtonPressed()){
      turn.start();
    }
    // Switch Vision Modes
    if(xbC.getTriggerAxis(Hand.kLeft)>.5){
      driveWanted.setBoolean(false);
      cargoWanted.setBoolean(true);
      tapeWanted.setBoolean(false); 
    }
    if(xbC.getTriggerAxis(Hand.kRight)>.5){
      driveWanted.setBoolean(false);
      cargoWanted.setBoolean(false);
      tapeWanted.setBoolean(true); 
    }
    //Probably Not Going to Have driveWanted as an option during comp
    //Extra button press not needed
    if(xbC.getBumperPressed(Hand.kRight)){
      driveWanted.setBoolean(true);
      cargoWanted.setBoolean(false);
      tapeWanted.setBoolean(false); 
    }
    if(xbC.getBumperPressed(Hand.kLeft)){
      if(cargoWanted.getBoolean(false)){
        
      }else if(tapeWanted.getBoolean(false)){
        turn.setSetpoint(tapeYaw.getDouble(123456789));
        if(turn.getSetpoint()!=123456789){
          turn.start();
        }else{
          System.out.println("Yaw at the Default/None Found Value- 123456789");
        }
        turn.start();
      }
    }
    // Allow PID Values to be adjusted without redeploy
    turn.P=ngP.getDouble(0);
    turn.I=ngI.getDouble(0);
    turn.D=ngD.getDouble(0);
    goDistance.P=nmP.getDouble(0);
    goDistance.I=nmI.getDouble(0);
    goDistance.D=nmD.getDouble(0);
    
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
    teleOp.start();
  }
  @Override
  public void testPeriodic() {
    
  }
}
