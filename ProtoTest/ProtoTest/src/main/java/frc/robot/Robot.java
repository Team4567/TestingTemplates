/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.distanceUnit;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  XboxController xbC;
  //TalonSRX t;
  Spark spark;
  VictorSP vsp;
  TalonSRX vL, vR;
  AnalogInput range= new AnalogInput(0);
  double scale;
  


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
   //t= new TalonSRX(2);
   //t.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
   spark= new Spark(1);
   vsp= new VictorSP(0);
   xbC= new XboxController(0);
   vL= new TalonSRX(3);
   vR= new TalonSRX(2);
   vL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
   vR.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
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
    
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    
   
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    

  /**
   * This function is called periodically during operator control.
   */
  }
  @Override
  public void teleopPeriodic() {
    if(xbC.getTriggerAxis(Hand.kLeft)>0.2 || xbC.getTriggerAxis(Hand.kRight)>0.2){
      if(xbC.getTriggerAxis(Hand.kLeft)>0.2){
        spark.set(xbC.getTriggerAxis(Hand.kLeft));
        vsp.set(xbC.getTriggerAxis(Hand.kLeft));
      } else if(xbC.getTriggerAxis(Hand.kRight)>0.2){
        spark.set(xbC.getTriggerAxis(Hand.kRight));
        vsp.set(xbC.getTriggerAxis(Hand.kRight));
      }
    } else{
      if(xbC.getBumper(Hand.kLeft)){
        spark.set(-1);
        vsp.set(-1);
        vL.set(ControlMode.PercentOutput,-1);
        vR.set(ControlMode.PercentOutput,-1);

      } else if(xbC.getBumper(Hand.kRight)){
        spark.set(1);
        vsp.set(1);
        vL.set(ControlMode.PercentOutput,1);
        vR.set(ControlMode.PercentOutput,1);
      }else if(xbC.getXButton()){
        spark.set(-0.5);
        vsp.set(-0.5);
        vL.set(ControlMode.PercentOutput,-0.5);
        vR.set(ControlMode.PercentOutput,-0.5);
      } else if(xbC.getBButton()){
        spark.set(0.5);
        vsp.set(0.5);
        vL.set(ControlMode.PercentOutput,0.5);
        vR.set(ControlMode.PercentOutput,0.5);
      } else if(xbC.getAButton()){
        vsp.set(0.5);
      }else{
        spark.set(0);
        vsp.set(0);
        vL.set(ControlMode.PercentOutput, 0);
        vR.set(ControlMode.PercentOutput, 0);
      }
    }
  }
  /**
   * This function is called periodically during test mode.
   */
  private double rangeFinderDistance(distanceUnit unit){
    final float Vi=5/1024;
    //Vi= Volts per 5 mm
    switch(unit){
      case centimeters:
        scale = (Vi/5)*100;
      break;
      case inches:
        scale = ((Vi/5)*100)/2.54;
      break;
      default:
        scale = ((Vi/5)*100)/2.54;
      break;
    }
    return (range.getVoltage()*scale);
  }
  
  @Override
  public void testPeriodic() {
    System.out.println(rangeFinderDistance(distanceUnit.inches));
    //System.out.println(t.getSelectedSensorPosition()/4096 + " revolutions.");
  }
}
