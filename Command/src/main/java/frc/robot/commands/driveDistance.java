/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.constants;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
/**
 * An example command.  You can replace me with your own command.
 */
public class driveDistance extends Command {
  double P,I,D;
  double integral=0, previous_error=0, setpoint, error, derivative, output;
  double avgEncoder;
  TalonSRX tL, tR;
  turnAngle straight;
  public driveDistance() {
    
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    straight= new turnAngle(); 
    tL= Robot.drive.leftMain;
    tR= Robot.drive.rightMain;
    avgEncoder=tL.getSelectedSensorPosition()+tR.getSelectedSensorPosition();
  }
  public driveDistance(double setpoint,boolean fromCurrent) {
    
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    straight= new turnAngle(); 
    tL= Robot.drive.leftMain;
    tR= Robot.drive.rightMain;
    avgEncoder=(tL.getSelectedSensorPosition()+tR.getSelectedSensorPosition())/2;
    if(!fromCurrent){
      this.setpoint=setpoint;
    } else{
      this.setpoint+=setpoint;
    }
  }
  public driveDistance(double setpoint){
    requires(Robot.drive);
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    straight= new turnAngle(); 
    tL= Robot.drive.leftMain;
    tR= Robot.drive.rightMain;
    avgEncoder=(tL.getSelectedSensorPosition()+tR.getSelectedSensorPosition())/2;
    this.setpoint=setpoint;
    
  }
  public void setSetpoint(int setpoint){
    this.setpoint=setpoint;
  }
  public void setSetpointFromPos(int inc){
    setpoint+=inc;
  }
  public void PID(){
    error = setpoint-avgEncoder;
    integral+= (error*.02);
    derivative= (error-previous_error)/.02;
    previous_error=error;
    output=Math.max(Math.min(P*error+I*integral+D*derivative,-0.5),0.5);
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    straight.setSetpointToCurrent();
    straight.PID();
    PID();
    Robot.drive.drive(output,straight.output);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(output<0.2 && avgEncoder>setpoint-100 && avgEncoder<setpoint+100){
    return true;
  } else{
    return false;
  
  }  
}

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {

  }
}
