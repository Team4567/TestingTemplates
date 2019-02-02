/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.Constants;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class DriveDistance extends Command {
  public double P,I,D;
  public double integral=0, previous_error=0, setpoint, error, derivative;
  public double output=0;
  double avgEncoder;
  public TalonSRX tL, tR;
  TurnCalculator straight;
  private boolean done;
  boolean incPhase;
  
  private MotorCalculator mc;

  public DriveDistance( MotorCalculator mc ) {
    init( mc );
  }

  public DriveDistance(double setpointI, MotorCalculator mc) {
    init( mc );
    setSetpointInches(setpointI);
  }

  private void init( MotorCalculator mc ) {
    requires(Robot.drive);
    this.mc=mc;
    straight= new SimpleTurnP( .02, .0015, .4, .1, 1 );
    tR=Robot.drive.rightMain;
    tL=Robot.drive.leftMain;
  }

  public void setSetpointInches( double setpoint ) {
    mc.setSetpoint( setpoint / Constants.wheelCirc * 4096 );
  }
  
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    done=false;
    tR.setSelectedSensorPosition(0);
    straight.setSetpoint(Robot.drive.getYaw());
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    done = (mc.getOutput( tR.getSelectedSensorPosition() ) == 0.0 );
    Robot.drive.drive( mc.getOutput( tR.getSelectedSensorPosition() ), straight.getOutput( Robot.drive.getYaw() ) );
    System.out.println( straight.getOutput( Robot.drive.getYaw() ) );
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
      return done;
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
    Robot.drive.stop();

  }
}
