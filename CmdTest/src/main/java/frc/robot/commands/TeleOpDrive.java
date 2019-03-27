/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
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
  boolean dumpTruckUp, platformBackUp, platformFrontUp, scoreOut, mechOut;
  int prevPOV=-1;
  VisionMovement vision;
  public TeleOpDrive( XboxController controller ) {
    // Use requires() here to declare subsystem dependencies
    requires( Robot.drive );
    requires( Robot.upper );
    requires( Robot.platformer );
    requires( Robot.score );
    xbC = controller;
    VisionBackup vision = new VisionBackup();
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println( "Starting TeleOpDrive!" );
    Robot.teleOpStarted = true;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //System.out.println("exec");
    // Scoring Front
    if( xbC.getYButton() ){
      
    } 
    if( xbC.getAButtonPressed() ){
      mechOut = !mechOut;
    }
    if( !mechOut ){
      Robot.score.frontP1.set( DoubleSolenoid.Value.kForward );
    } else {
      Robot.score.frontP1.set( DoubleSolenoid.Value.kReverse);
    }
    if( xbC.getXButtonPressed() ){
      scoreOut = !scoreOut;
    }
    if ( scoreOut ){
      Robot.score.frontP2.set( DoubleSolenoid.Value.kForward );
    } else {
      Robot.score.frontP2.set( DoubleSolenoid.Value.kReverse );
    }
    // Dump Truck
    if( xbC.getBButtonPressed() ){
      dumpTruckUp = !dumpTruckUp;
    }
    if( dumpTruckUp ){
      Robot.score.moveBackBall( DoubleSolenoid.Value.kForward );
    }else{
      Robot.score.moveBackBall( DoubleSolenoid.Value.kReverse );
    }
    
    // Vision Cancelling
    if( xbC.getBumper( Hand.kLeft ) ){
      vision.cancel();
    } 
    if( xbC.getTriggerAxis( Hand.kLeft ) > .5 ){
      
    }
    // Elevator
    if( xbC.getBumper( Hand.kRight ) ){
      Robot.upper.move( .5 );
    } 
    if( xbC.getTriggerAxis( Hand.kRight ) > .1 ){
      Robot.upper.move( xbC.getTriggerAxis( Hand.kRight ) );
    }          
    // Drive Inverter     
    if( xbC.getStartButtonPressed() ){
      invert = !invert;
      System.out.println( invert );
    }
    // Vision Assistance
    if( xbC.getBackButtonPressed() ){
      System.out.println("Vision");
      vision.start();
    }
    // Anything to do with a joystick
    Robot.drive.drive( xbC, invert );     
    // D-Pad
    if( xbC.getPOV() == 0 && prevPOV != 0 ){
      platformFrontUp = !platformFrontUp;
      prevPOV = 0;
    }
    if( xbC.getPOV() == 180 && prevPOV != 180 ){
      platformBackUp = !platformBackUp;
      prevPOV = 180;
    }   
    /*if( !platformFrontUp ){
      Robot.platformer.setFronts( DoubleSolenoid.Value.kForward );
    } else {
      
      Robot.platformer.setFronts( DoubleSolenoid.Value.kReverse );
      
    }*/
    if( !platformBackUp ){
     // Robot.platformer.setBack( DoubleSolenoid.Value.kForward );
    } else {
     // Robot.platformer.setBack( DoubleSolenoid.Value.kReverse );
    }
    if( xbC.getPOV() == 90 ){
      
    }   
    
    if( xbC.getPOV() == 270 ){
      
    }      
    if( xbC.getPOV() == -1 ){
      prevPOV = -1;
    }    
    //Robot.platformer.lockFront();                
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
