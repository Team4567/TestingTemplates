/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;


public class turnAngle extends Command {
  double integral=0, previous_error=0, setpoint, error, derivative; 
  
  PigeonIMU pidgey;
  boolean done;
  turnCalculator tc;
  public turnAngle(turnCalculator tc) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);

    pidgey= Robot.drive.gyro;
    
    this.tc=tc;
    
  }
  public turnAngle(double setpoint,turnCalculator tc) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    
      requires(Robot.drive);
    
    pidgey= Robot.drive.gyro;
    setSetpoint(setpoint);
    this.tc=tc;
    
    
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
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
   done=false;
   tc.setDone(false);
   tc.setSetpoint(setpoint);
   tc.start();
  }
  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    
      Robot.drive.drive(0,tc.getOutput());
    
    
  }
  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
       if(Robot.drive.getYaw()>setpoint-1&&Robot.drive.getYaw()<setpoint+1){
         return true;
       }else{
         return false;
      }
    
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    done=true;
    tc.setDone(true);
  }
  

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    done=true;
    tc.setDone(true);
  }
}
