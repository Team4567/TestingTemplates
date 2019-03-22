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
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import frc.robot.Robot;
import frc.robot.enums.ElevatorPos;
import frc.robot.Constants;
/**
 * Driving Controls for TeleOp
 */
public class TeleOpDrive extends Command {
  XboxController xbC;
  double elevOutput;
  boolean invert = false;
  public TeleOpDrive( XboxController controller ) {
    // Use requires() here to declare subsystem dependencies
    requires( Robot.drive );
    requires( Robot.upper );
    requires( Robot.platformer );
    requires( Robot.score );
    xbC = controller;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println( "Starting TeleOpDrive!" );
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if( xbC.getAButton() ){

    }
    if( xbC.getBButton() ){

    }
    if ( xbC.getXButton() ){

    }
    if( xbC.getYButton() ){
        
    }
    if( xbC.getBumper( Hand.kLeft ) ){

    } 
    if( xbC.getBumper( Hand.kRight ) ){

    } 
    if( xbC.getTriggerAxis( Hand.kLeft ) > .1 ){

    }
    if( xbC.getTriggerAxis( Hand.kRight ) > .1 ){

    }               
    if( xbC.getStartButtonPressed() ){
      invert = !invert;
    }
    if( xbC.getBackButton() ){
      VisionMovement vision = new VisionMovement();
      vision.start();
    }
    Robot.drive.drive( xbC, invert );     
    elevOutput = ( xbC.getY( Hand.kRight ) > .1 ) ? xbC.getY( Hand.kRight ) : 0;
    Robot.upper.move( elevOutput );                         
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
    Robot.upper.stop();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    Robot.drive.stop();
    Robot.upper.stop();
  }
}
