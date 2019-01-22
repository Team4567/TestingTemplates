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
import frc.robot.constants;


public class turnAngleTest extends Command {
  public double P,I,D;
  double integral=0, previous_error=0, setpoint, error, derivative; 
  public double output;
  PigeonIMU pidgey;

  public turnAngleTest() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.drive);
    pidgey= Robot.drive.gyro;
    P= constants.gyroP;
    I= constants.gyroI;
    D= constants.gyroD; 
  }
  public turnAngleTest(double setpoint) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.drive);
    pidgey= Robot.drive.gyro;
    P= constants.gyroP;
    I= constants.gyroI;
    D= constants.gyroD; 
    this.setpoint=setpoint;
  }
  public void setSetpoint(int setpoint){
    this.setpoint=setpoint;
  }
  public void setSetpointFromPos(int inc){
    setpoint+=inc;
  }
  public void setSetpointToCurrent(){
    setpoint=Robot.drive.getYaw();
  }
  public void PID(){
    error = setpoint-Robot.drive.getYaw();
    integral+= (error*.02);
    derivative= (error-previous_error)/.02;
    previous_error=error;
    output=-1*Math.min(Math.max(P*error+I*integral+D*derivative,-.5),.5);
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
   
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
   PID();
   Robot.drive.drive(0,output);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(Robot.drive.getYaw()>setpoint-10 && Robot.drive.getYaw()<setpoint+10&&Math.abs(output)<.01){
      return true;
    } else{
      return false;
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    //if(Robot.ds.isOperatorControl()){
      //Robot.teleOp.start();
    //}
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    if(Robot.ds.isOperatorControl()){
      Robot.teleOp.start();
    }
  }
}
