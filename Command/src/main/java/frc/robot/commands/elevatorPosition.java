/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;
import frc.robot.constants;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class elevatorPosition extends Command {
  double P,I,D;
  double integral, previous_error, setpoint, error, derivative, output;
  public elevatorPosition() {
    P=constants.elevP;
    I=constants.elevI;
    D=constants.elevD;
    requires(Robot.upper);
  }
  public elevatorPosition(int setpoint) {
    P=constants.elevP;
    I=constants.elevI;
    D=constants.elevD;
    this.setpoint=setpoint;
    requires(Robot.upper);
  }
  public void PID(){
    error = setpoint-Robot.upper.t1.getSelectedSensorPosition();
    integral+= (error*.02);
    derivative= (error-previous_error)/.02;
    previous_error=error;
    output=Math.max(Math.min(P*error+I*integral+D*derivative,-0.5),0.5);
  }
  public void setSetpoint(int setpoint){
    this.setpoint=setpoint;
  }
  public void setSetpointFromPos(int inc){
    setpoint+=inc;
  }
  public void setSetpointToCurrent(){
    setpoint=Robot.upper.t1.getSelectedSensorPosition();
  }
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    PID();
    Robot.upper.move(output);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
