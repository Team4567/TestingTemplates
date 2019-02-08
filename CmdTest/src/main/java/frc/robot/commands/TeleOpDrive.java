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
public class TeleOpDrive extends Command {
  XboxController xbC;
  int level=1;
  int encoderLevel=0;
  LineFollow lineCalc= new LineFollow(0,0);
  public boolean useLineFollow=false;
  public TeleOpDrive(XboxController controller) {
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
    //requires(Robot.upper);
    xbC=controller;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //if(useLineFollow){
      //Robot.drive.drive(xbC.getY(Hand.kLeft), lineCalc.turn());
    //}else{
      Robot.drive.drive(xbC);
    //}
    if(xbC.getAButtonPressed()){
      
    }
    if(xbC.getBButtonPressed()){
      
    }
    if(xbC.getYButton()){
      //Robot.drive.leftMain.set(ControlMode.PercentOutput,.5);
      //Robot.drive.leftSlave.follow(Robot.drive.leftSlave);

      
    }
    if(xbC.getXButton()){
      //Robot.drive.rightMain.set(ControlMode.PercentOutput,.5);
      //Robot.drive.rightSlave.follow(Robot.drive.rightSlave);
    }
    if(xbC.getBackButtonPressed()){
     
    }
    if(xbC.getStartButtonPressed()){
     
    }
    if(xbC.getStartButtonReleased()){
      
    }
   
    if(xbC.getTriggerAxis(Hand.kLeft)>.5){
      
    }
    if(xbC.getTriggerAxis(Hand.kRight)>.5){
      
    }                                                    
    useLineFollow=xbC.getBumper(Hand.kRight);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
    
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    //Robot.upper.move(0);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    //Robot.upper.move(0);
  }
}
