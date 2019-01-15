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
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.AnalogInput;
import frc.robot.constants;
import frc.robot.enums.*;
import frc.robot.PID;
/**
 * An example subsystem.  You can replace me with your own Subsystem.
 */
public class drivetrain extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  private static drivetrain starter = new drivetrain();
    public TalonSRX rightMain,leftMain;
    private TalonSRX rightSlave, leftSlave;
    private AnalogInput range;
    private double scaleR;
    public PigeonIMU gyro;
    Timer time;
    boolean hasLeft=false;
    public static drivetrain start(){
       return starter;
    }
    
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
        gyro= new PigeonIMU(5);
        
    }
    public void resetGyro(){
      gyro.setYaw(0);
    }
    public double getYaw(){
      double[] yaw=new double[3];
      gyro.getYawPitchRoll(yaw);
      return yaw[0];
    }
    /*public void teleOpDrive(XboxController controller){
        // Version of arcadeDrive from edu.wpi.first.wpilibj.RobotDrive, lines 401-417
        // Joe Lange (Driver) is used to this naturally from years past, maybe we can change it.
        double y= controller.getY(Hand.kLeft);
        double x= controller.getX(Hand.kLeft);
        double leftMotors, rightMotors;
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
        rightMain.set(ControlMode.PercentOutput, rightMotors);
        leftMain.set(ControlMode.PercentOutput, leftMotors);
        if(controller.getAButtonPressed()){
          driveDistanceInches(5*12);
        }
        if(controller.getBButtonPressed()){
          turnAngle(90);
        }
        if(controller.getStartButton()){
          resetGyro();
        }
    }*/
    public void autoDrive(){
        // Have plenty of ideas, need event details
    }
    /*public void stop(){
        rightMain.set(ControlMode.PercentOutput, 0);
        leftMain.set(ControlMode.PercentOutput, 0);
    }*/
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
        rightMain.set(ControlMode.PercentOutput,rightMotors);
        leftMain.set(ControlMode.PercentOutput,leftMotors);
      }
    }
    public void driveDistanceInches(double inches){
      resetGyro();
      PID pidSpeed= new PID(leftMain,rightMain);
      PID pidAngle= new PID(gyro);
      pidSpeed.setSetpoint((int)((inches/constants.wheelDiameter)*4096));
      pidAngle.setSetpoint(0);
      if(pidSpeed.distanceSpeed()>0.2){
        drive(pidSpeed.distanceSpeed(),pidAngle.angle());
      }else {
        if(pidSpeed.avgEncoder>pidSpeed.setpoint-60 && pidSpeed.avgEncoder<pidSpeed.setpoint+60){
            time.reset();
            time.start();
            hasLeft=false;
            if(time.get()<=2){
              drive(0.2,pidAngle.angle());
            }else{
              drive(0,0);
            }
        }else{
          drive(pidSpeed.distanceSpeed(),pidAngle.angle());
        }
      }
    }
    public void turnAngle(int angle){
      resetGyro();
      PID angleSet= new PID(gyro);
      angleSet.setSetpoint(angle);
      if(angleSet.angle()>0.2){
        drive(0,angleSet.angle());
      }else {
        if(angleSet.ypr[0]>angleSet.setpoint-3 && angleSet.ypr[0]<angleSet.setpoint+3){
            time.reset();
            time.start();
            hasLeft=false;
            if(time.get()<=2){
              drive(0,0.2);
            }else{
              drive(0,0);
            }
        }else{
          drive(0,angleSet.angle());
        }
      }
    }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
