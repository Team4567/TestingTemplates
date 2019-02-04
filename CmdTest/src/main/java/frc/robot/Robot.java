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
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEntry;

import frc.robot.autonomous.*;
import frc.robot.commands.*;
import frc.robot.enums.Want;
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
  public static Drivetrain drive;
  public static Elevator upper;
  public static ScoringMech score;
  //Commands
  public static TeleOpDrive teleOp;
  public static TurnAngle turn,turnOut;
  public static MotorCalculator simpleMotorP;
  public static DriveDistance goDistance,goNew;
  public static ElevatorPosition moveElev;
  private static Testing test;
  public static GoVision go;
  public static CommandGroup m_autonomousCommand;
  //Interfaces/Controllers
  public static DriverStation ds = DriverStation.getInstance();
  public static XboxController xbC= new XboxController(0);
  SendableChooser<CommandGroup> m_chooser = new SendableChooser<>();
  //NetworkTables
  NetworkTableInstance inst;
  private static NetworkTable chickenVision;
  private NetworkTableEntry nmP,nmI,nmD;
  private NetworkTableEntry driveWanted,cargoWanted,tapeWanted,tapeYaw,cargoYaw;

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    
    //Subsystems
    drive= new Drivetrain();
    upper= new Elevator();
    score= new ScoringMech();
    //Commands
    teleOp= new TeleOpDrive(xbC);
    turn=new TurnAngle(new SimpleTurnP(.02, .002, .4, .1, 1 ) );
    goDistance= new DriveDistance(new SimpleMotorP( 0.10, Constants.motorP, 0.5, Constants.minValY, Constants.closeEnough) );
    moveElev= new ElevatorPosition();
    test=new Testing();
    go=new GoVision(xbC,Want.tape);
    //Interfaces/Controllers
    m_chooser.setDefaultOption("Default Auto, No Movement", new NoMovement());
    m_chooser.addOption("Start: Left, Target: Cargo", new LeftCargo());
    m_chooser.addOption("Start: Left, Target: Rocket", new LeftRocket());
    m_chooser.addOption("Start: Right, Target: Cargo", new RightCargo());
    m_chooser.addOption("Start: Right, Target: Rocket", new RightRocket());
    m_chooser.addOption("Start: Center, Target: Left-Side Cargo", new CenterLCargo());
    m_chooser.addOption("Start: Center, Target: Left-Side Rocket", new CenterLRocket());
    m_chooser.addOption("Start: Center, Target: Right-side Rocket", new CenterRCargo());
    m_chooser.addOption("Start: Center, Target: Right-Side Rocket", new CenterRRocket());
    SmartDashboard.putData("Auto mode", m_chooser);
    //NetworkTables
    inst=NetworkTableInstance.getDefault();
    
    
  
    
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
    System.out.println(drive.rangeFinderDistance());
    //Init Distance
    if(xbC.getAButtonPressed()){
      goDistance.setSetpointInches(10*12);
      goDistance.start();
    }
    
    //EMERGENCY CANCEL ANY ACTIVE COMMAND THRU TELEOP. MAKE SURE CANCEL IS SET UP IN THE COMMANDS
    if(xbC.getBButtonPressed()){
      turn.cancel();
      goDistance.cancel();
      test.cancel();
    }
    //Steady Driving Testing, probably no longer needed
    if(xbC.getYButton()){
      turn.setSetpoint(5);
      turn.start();

    }
    // Reset Turning Setpoint(For 45 Degree Increment Testing)
    if(xbC.getXButton()){
      drive.drive(0.075,0);
    }
    // Reset Positioning Devices
    if(xbC.getBackButtonPressed()){
      drive.resetGyro();
      drive.rightMain.setSelectedSensorPosition(0);
    }
    //Init turning
    if(xbC.getStartButtonPressed()){
      test.start();
    }
    if(xbC.getStartButtonReleased()){
      //go.setDone(true);
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
    /*if(xbC.getBumperPressed(Hand.kLeft)){
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
    }*/
    
    
    
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
