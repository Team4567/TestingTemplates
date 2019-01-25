/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.constants;
import frc.robot.enums.*;
/**
 * An example subsystem.  You can replace me with your own Subsystem.
 */
public class drivetrain extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
    public TalonSRX rightMain,leftMain;
    public TalonSRX rightSlave, leftSlave;
    private AnalogInput range;
    private double scaleR;
    public PigeonIMU gyro;
    Timer time;
    boolean hasLeft=false;
    double[] ypr;
    
    public drivetrain(){
        rightMain= new TalonSRX(constants.rightMainMC);
        rightMain.setNeutralMode(NeutralMode.Brake);
        rightMain.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        leftMain= new TalonSRX(constants.leftMainMC);
        leftMain.setNeutralMode(NeutralMode.Brake);
        rightMain.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightSlave= new TalonSRX(constants.rightSlaveMC);
        rightSlave.follow(rightMain);
        rightSlave.setNeutralMode(NeutralMode.Brake);
        leftSlave= new TalonSRX(constants.leftSlaveMC);
        leftSlave.follow(leftMain);
        leftSlave.setNeutralMode(NeutralMode.Brake);
        range = new AnalogInput(0);
        gyro= new PigeonIMU(rightSlave);
        ypr= new double[3];
    }
    public double applyDeadband(double value, double deadband) {
      if (Math.abs(value) > deadband) {
          return value;
      } else {
        return 0.0;
      }
    }
    public void stop(){
      drive(0,0);
    }
    public void findGyroVals(){
      gyro.getYawPitchRoll(ypr);
    }
    public void resetGyro(){
      gyro.setYaw(0);
    }
    public double getYaw(){
      return ypr[0];
    }
    public double rangeFinderDistance(distanceUnit unit){
      final float Vi=5/1024;
      
      //Vi= Volts per 5 mm
      if(unit== distanceUnit.centimeters){
        scaleR = (Vi/5)*100;

      }
      if(unit==distanceUnit.inches){
        scaleR = ((Vi/5)*100)/2.54;

      }
      return (range.getVoltage()*scaleR);
    }
    public double encoderDistanceInInches(TalonSRX t){
      return t.getSelectedSensorPosition()*((1/4096)*(constants.wheelCirc));
    }
    public double encoderDistanceInCentimenters(TalonSRX t){
      return t.getSelectedSensorPosition()*((1/4096)*(constants.wheelCirc)*2.54);
    }
    public void drive(double y,double x){
      double leftMotors,rightMotors;
      if (y > 0.0) {
        if (x > 0.0) {
          leftMotors = y - x;
          rightMotors = Math.max(y, x);
        } else {
          leftMotors = Math.max(y, -x);
          rightMotors = y + x;
        }
      } else {
        if (x > 0.0) {
          leftMotors = -Math.max(-y, x);
          rightMotors = y + x;
        } else {
          leftMotors = y - x;
          rightMotors = -Math.max(-y, -x);
        }
        
      }
      rightMain.set(ControlMode.PercentOutput,rightMotors);
        leftMain.set(ControlMode.PercentOutput,-1*leftMotors);
        rightSlave.follow(rightMain);
        leftSlave.follow(leftMain);
    }
    public void drive(XboxController controller){
      double y= applyDeadband(.75*controller.getY(Hand.kLeft),0.1);
      double x= applyDeadband(.75*controller.getX(Hand.kLeft),0.1);
      double leftMotors,rightMotors;
      if (y > 0.0) {
        if (x > 0.0) {
          leftMotors = y - x;
          rightMotors = Math.max(y, x);
        } else {
          leftMotors = Math.max(y, -x);
          rightMotors = y + x;
        }
      } else {
        if (x > 0.0) {
          leftMotors = -Math.max(-y, x);
          rightMotors = y + x;
        } else {
          leftMotors = y - x;
          rightMotors = -Math.max(-y, -x);
        }
        
      }
      rightMain.set(ControlMode.PercentOutput,rightMotors);
        leftMain.set(ControlMode.PercentOutput,-1*leftMotors);
        rightSlave.follow(rightMain);
        leftSlave.follow(leftMain);
        
    }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
