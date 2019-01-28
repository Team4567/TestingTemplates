/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.sensors.PigeonIMU;
import java.util.ArrayList; 
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.constants;


public class turnAngle extends Command {
  public double P,I,D;
  double integral=0, previous_error=0, setpoint, error, derivative; 
  private double output,previous_output;
  PigeonIMU pidgey;
  boolean done;
  boolean outputOnly;
  Timer t;
  public turnAngle(boolean outputOnly) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    if(!outputOnly){
      requires(Robot.drive);
    }
    pidgey= Robot.drive.gyro;
    P= constants.gyroP;
    I= constants.gyroI;
    D= constants.gyroD; 
    this.outputOnly=outputOnly;
    t= new Timer();
  }
  public turnAngle(double setpoint,boolean outputOnly) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    if(!outputOnly){
      requires(Robot.drive);
    }
    pidgey= Robot.drive.gyro;
    P= constants.gyroP;
    I= constants.gyroI;
    D= constants.gyroD; 
    this.setpoint=setpoint;
    this.outputOnly=outputOnly;
    t= new Timer();
  }
  
  public double calcError(){
    return setpoint-Robot.drive.getYaw();
  }
  public double desiredOut(){
    return P*calcError();
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
  public void PID(){
    error = calcError();
    integral+= (error*.02);
    derivative= (error-previous_error)/.02;
    previous_error=error;
    output=Math.min(Math.max(P*error+I*integral+D*derivative,-.5),.5);
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
   done=false;
   
  }
  public void calculate() {
    previous_output=output;
    output=Math.max(Math.min(P*(setpoint-Robot.drive.getYaw()),0.5),-0.5);
  }
  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    calculate();
    if(output-previous_output<=.02){

    }else{
      if(output-previous_output>0){
        output=previous_output+.02;
      }else if(output-previous_output<0){
        output=previous_output-.02;
        if(output<constants.minValY){
          output=constants.minValY;
        }
      }else{
        
      }
    }
    if(Math.abs(output)<constants.minValX){
      output=constants.minValX;
    }
    if(!outputOnly){
      Robot.drive.drive(0,output);
    }
  }
  public double getOutput(){
    return output;
  }
  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(!outputOnly){
       if(Robot.drive.getYaw()>setpoint-.5&&Robot.drive.getYaw()<setpoint+.5){
         return true;
       }else{
         return false;
      }
    }else{
      //Since I am just proving an output, I shall continue until the upper command changes my done boolean!
      if(done){
        return true;
      }else{
        return false;
      }
    } 
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    if(!outputOnly){
      Robot.drive.stop();
    }
    done=true;
    
  }
  

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    if(!outputOnly){
      Robot.drive.stop();
    }
    done=true;
  }
}
