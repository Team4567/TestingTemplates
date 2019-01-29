/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.constants;

public class simpleMotorP extends Command implements MotorCalculator {
  public static boolean done;
  private double output,previous_output;
  public double setpoint;
  public double P,I,D;
  TalonSRX tR,tL;
  public simpleMotorP(double setpointInches, TalonSRX tR, TalonSRX tL) {
    done=false;
    setpoint=setpointInches/constants.wheelCirc*4096;
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    this.tR=tR;
    this.tL=tL;
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }
  public simpleMotorP(TalonSRX tR, TalonSRX tL) {
    done=false;
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    this.tR=tR;
    this.tL=tL;
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }
  public void setSetpointInches(double setpoint){
    this.setpoint=setpoint/constants.wheelCirc*4096;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    done=false;
    tR.setSelectedSensorPosition(0);
    previous_output=0;
    output=0;
  }

  // Called repeatedly when this Command is scheduled to run
  
  @Override
  public void calculate() {
    previous_output=output;
    output=Math.max(Math.min(P*(setpoint-tR.getSelectedSensorPosition()),0.5),-0.5);
  }
  @Override
  protected void execute() {
    calculate();
    if(Math.abs(output-previous_output)<.02){

    }else{
      if(output-previous_output>0){
        output=previous_output+.01;
      }else if(output-previous_output<0){
        output=previous_output-.01;
      }else{
        
      }
      
    }
    if(Math.abs(output)<constants.minValY){
      if(output>0){
        output=constants.minValY;
      }else if(output<0){
        output=-1*constants.minValY;
      }else{
        output=0;
      }
    }
  }
  
  @Override
  public double getOutput() {
    return output;
  }
  public void setDone(boolean set){
    done=set;
  }
  public boolean isDone(){
    return done;
  }
  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(done||(tR.getSelectedSensorPosition()>setpoint-200&&tR.getSelectedSensorPosition()<setpoint+200)){
      return true;
    }else{
    return false;
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    output=0;
    done=true;
    System.out.println("Target Reached");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    output=0;
    done=true;
    System.out.println("SimpleMotorP was interrupted!");
  }
}
