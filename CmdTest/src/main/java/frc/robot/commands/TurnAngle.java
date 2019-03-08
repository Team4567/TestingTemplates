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


public class TurnAngle extends Command {
  double integral = 0, previous_error = 0, setpoint, error, derivative; 
  boolean done;
  TurnCalculator tc;
  public TurnAngle( TurnCalculator tc ) {
    init( tc );
  }

  public TurnAngle( double setpoint, TurnCalculator tc ) {
    init( tc );
    setSetpoint( setpoint );
  }
  
  private void init( TurnCalculator tc ){
    requires( Robot.drive );
    this.tc = tc;
  }
  
  public void setSetpoint( double setpoint ){
    this.setpoint = setpoint;
  }

  @Override
  protected void initialize() {
    done = false;
    tc.setSetpoint( setpoint );
  }

  @Override
  protected void execute() {
    done = ( tc.getOutput( Robot.drive.getYaw() ) == 0 );
    Robot.drive.drive( 0, tc.getOutput( Robot.drive.getYaw() ) );
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
       return done;  
  }

  @Override
  protected void end() {
    Robot.drive.stop();
  }
  
  @Override
  protected void interrupted() {
    Robot.drive.stop();
  }
}
