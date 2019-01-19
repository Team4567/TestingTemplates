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
 * Driving Controls for TeleOp
 */
public class teleOpDrive extends Command {
  XboxController xbC;
  int level=1;
  public teleOpDrive(XboxController controller) {
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    requires(Robot.upper);
    xbC=controller;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.drive.drive(xbC);
    if(xbC.getYButtonPressed()){
      level++;
    } else if(xbC.getAButtonPressed()){
      level--;
    }
    if(level>3){
      level=3;
    }
    if(level<1){
      level=1;
    }
    switch(level){
      case 1:
        Robot.moveElev.setSetpoint(0);
      break;
      case 2:
        Robot.moveElev.setSetpoint(0);
      break;
      case 3:
        Robot.moveElev.setSetpoint(0);
      break;
      default:
        Robot.moveElev.setSetpoint(0);
      break;
    }
    
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
