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
  turnCalculator straight;
  public boolean done;
  boolean incPhase;
  
  motorCalculator mc;
  public driveDistance(motorCalculator mc){
    requires(Robot.drive);
    this.mc=mc;
    straight= new simpleTurnP(Robot.drive.gyro,false);
    tR=Robot.drive.rightMain;
    tL=Robot.drive.leftMain;
  }
  public driveDistance(double setpointI, motorCalculator mc) {
    
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    this.mc=mc;
    straight= new simpleTurnP(Robot.drive.gyro,false);
    tR=Robot.drive.rightMain;
    tL=Robot.drive.leftMain;
    setSetpointInches(setpointI);
    
  }
  public void setSetpointInches(double setpoint){
    mc.setSetpointInches(setpoint);
    this.setpoint=setpoint;
  }
  
  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    done=false;
    tR.setSelectedSensorPosition(0);
    mc.setDone(false);
    mc.start();
    straight.setSetpoint(Robot.drive.getYaw());
    straight.start();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    Robot.drive.drive(mc.getOutput(),straight.getOutput());
    System.out.println(straight.getOutput());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(mc.isDone()){
      return true;
    }else{
      return false;
    }
  }  


  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    straight.setDone(true);
    mc.setDone(true);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    mc.setDone(true);
    straight.setDone(true);
  }
}
