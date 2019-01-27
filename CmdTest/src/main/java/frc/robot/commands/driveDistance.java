/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.constants;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
/**
 * An example command.  You can replace me with your own command.
 */
public class driveDistance extends Command {
  public double P,I,D;
  public double integral=0, previous_error=0, setpoint, error, derivative;
  public double output=0;
  double avgEncoder;
  public TalonSRX tL, tR;
  Timer t;
  turnAngle straight;
  public boolean done;
  boolean incPhase;
  public driveDistance() {
    
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    t= new Timer();
    straight= new turnAngle(true); 
    tR= Robot.drive.rightMain;
    avgEncoder=tR.getSelectedSensorPosition();
  }
  public driveDistance(double setpoint) {
    
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    P= constants.motorP;
    I= constants.motorI;
    D= constants.motorD;
    t= new Timer();
    straight= new turnAngle(true); 
    tL= Robot.drive.leftMain;
    tR= Robot.drive.rightMain;
    avgEncoder=tR.getSelectedSensorPosition();
    this.setpoint=setpoint;
  }
  public void setSetpoint(double setpoint){
    this.setpoint=setpoint;
  }
  public void setSetpointFromPos(double inc){
    setpoint+=inc;
  }
  public void setSetpointToCurrent(){
    setpoint=tR.getSelectedSensorPosition();
  }
  public double calcError(){
    return setpoint-tR.getSelectedSensorPosition();
  }
  public double desiredOut(){
    return P*calcError();
  }
  public void PID(){
    error = calcError();
    integral+= (error*.02);
    derivative= (error-previous_error)/.02;
    previous_error=error;
    output=(Math.min(Math.max(P*error+I*integral+D*derivative,-0.5),0.5));
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    output=0;
    t.start();
    tR.setSelectedSensorPosition(0);
    done=false;
    incPhase=true;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    straight.setSetpointToCurrent();
    straight.PID();
    if(t.get()<=1&&output<=desiredOut()&&incPhase){
      output+=.01;

    }else{
      incPhase=false;
      PID();
    }
    if(output<constants.minValY){
      output=constants.minValY;
    }
    Robot.drive.drive(output,straight.getOutput());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(tR.getSelectedSensorPosition()> setpoint-200 && tR.getSelectedSensorPosition()<setpoint+200){
      return true;
    }else{
      return false;
    }
  }  


  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    straight.done=true;
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    
  }
}
