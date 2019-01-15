/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.Robot;

/**
 * An example command.  You can replace me with your own command.
 */
public class teleOpDrive extends Command {
  XboxController xbC;
  public teleOpDrive(XboxController controller) {
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    xbC=controller;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    double y= xbC.getY(Hand.kLeft);
    double x= xbC.getX(Hand.kLeft);
    double leftMotors,rightMotors;
    if (y > 0.0) {
        if (x > 0.0) {
          leftMotors = y - x;
          rightMotors = Math.max(y, x);
        } else {
          leftMotors = Math.max(y, -x);
          rightMotors = y + x;
        }
      } else {
        if (x > 0.0) {
          leftMotors = -Math.max(-y, x);
          rightMotors = y + x;
        } else {
          leftMotors = y - x;
          rightMotors = -Math.max(-y, -x);
        }
      }
      Robot.drive.rightMain.set(ControlMode.PercentOutput, rightMotors);
      Robot.drive.leftMain.set(ControlMode.PercentOutput,leftMotors);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(Robot.ds.isOperatorControl()){
        return false;
    }else{
        return true;
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.drive(0,0);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
