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
  int level = 1,prevLevel;
  int encoderLevel = 0;
  ElevatorPos pos, prevPos;
  public TeleOpDrive( XboxController controller ) {
    // Use requires() here to declare subsystem dependencies
    requires( Robot.drive );
    requires( Robot.upper );
    xbC = controller;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println("Starting TeleOpDrive!");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    //if(useLineFollow){
      //Robot.drive.drive(xbC.getY(Hand.kLeft), lineCalc.turn());
    //}else{
      Robot.drive.drive( xbC );
      elevOutput = ( xbC.getY( Hand.kRight ) > .1 ) ? xbC.getY( Hand.kRight ) : 0;
      Robot.upper.manualMove( xbC.getY( Hand.kRight ) );
      
    //}
    /*if( xbC.getAButtonPressed() ) level--;
    if( xbC.getYButtonPressed() ) level++;
    if( xbC.getBButtonPressed() ) prevLevel=level;
    if( xbC.getBButton() ) level = 71;
    if( xbC.getBButtonReleased() ) level=prevLevel;
    switch( level ){
      case 0:
        level = 1;
        pos = ElevatorPos.ballLow;
      break;
      case 1:
        pos = ElevatorPos.ballLow;
      break;
      case 2:
        pos = ElevatorPos.ballMed;
      break;
      case 3:
        pos = ElevatorPos.ballHigh;
      break;
      case 71:
        pos = ElevatorPos.cargoShip;
    }
    if( pos != prevPos ){
      //Robot.upper.move(pos);
    }
    */
    if( xbC.getXButton() ){
      //Robot.drive.rightMain.set(ControlMode.PercentOutput,.5);
      //Robot.drive.rightSlave.follow(Robot.drive.rightSlave);
    }
    /*
    if( xbC.getBackButtonPressed() ) Robot.platformer.setBack( Value. kForward );
    if( xbC.getBackButtonReleased() ) Robot.platformer.setBack( Value.kReverse );
    if( xbC.getStartButtonPressed() ) Robot.platformer.setFronts( Value.kForward );
    if( xbC.getStartButtonReleased() ) Robot.platformer.setBack( Value. kReverse );
    if( xbC.getTriggerAxis( Hand.kLeft ) > .5 ){
      
    }
    if( xbC.getTriggerAxis( Hand.kRight ) > .5 ){
      
    } */                                                   
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
