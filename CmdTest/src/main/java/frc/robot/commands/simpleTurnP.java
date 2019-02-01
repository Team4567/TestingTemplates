/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.sensors.PigeonIMU;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;

public class simpleTurnP extends Command implements turnCalculator{
  public static boolean done;
  private double output,previous_output;
  public double setpoint;
  public double P,I,D;
  private PigeonIMU gyro;
  private boolean doAccel;
  public simpleTurnP(PigeonIMU gyro,boolean a) {
    done=false;
    P= Constants.gyroP;
    I= Constants.gyroI;
    D= Constants.gyroD;
    this.gyro=gyro;
    doAccel=a;
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }
  public void setSetpoint(double setpoint){
    this.setpoint=setpoint;
  }
  public double getSetpoint(){
    return setpoint;
  }
  public void setSetpointFromPos(int inc){
    setpoint=Robot.drive.getYaw()+inc;
  }
  public void setSetpointToCurrent(){
    setpoint=Robot.drive.getYaw();
  }
  public void calculate() {
    previous_output=output;
    output=Math.max(Math.min(P*(setpoint-Robot.drive.getYaw()),0.4),-0.4);
  }
  public void giveOutput(double out){
    previous_output=output;
    this.output=out;
  }
  private void accelCheck(){
    if(output-previous_output<=.02){

    }else{
      if(output-previous_output>0){
        output=previous_output+.02;
      }else if(output-previous_output<0){
        output=previous_output-.02;
        
      }else{
        
      }
    }
  }
  private void minCheck(){
    if(Math.abs(output)<Constants.minValX){
      output=Math.signum(output)*Constants.minValX;
    }
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    calculate();
    if(doAccel){
      accelCheck();
    }
    minCheck();
    
    
  }
  @Override
  public double getOutput() {
    return output;
  }
  @Override
  public void setDone(boolean set) {
    done=set;
  }
  @Override
  public boolean isDone() {
    return done;
  }
  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(done){
      return true;  
    }else{
      return false;
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    done=true;
    output=0;
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    done=true;
    output=0;
  }
}
