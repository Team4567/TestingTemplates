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
  public TeleOpDrive(XboxController controller) {
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
    //if(xbC.getBumperPressed(Hand.kRight)){
      //Robot.alignOut.start();
    //}
    //if(xbC.getBumper(Hand.kRight)){
    //  Robot.drive.drive(xbC.getY(Hand.kLeft),Robot.alignOut.getOutput());
      
      
    //}
    /*if(xbC.getYButtonPressed()){
      level++;
    } else if(xbC.getAButtonPressed()){
      level--;
    }
    if(level>6){
      level=3;
    }
    if(level<1){
      level=1;
    }
    // 1= HatchL, 2= CargoL,3= HatchM, 4= CargoM, 5= HatchH, 6= CargoL
    switch(level){
      case 1:
        encoderLevel=0;
      break;
      case 2:
        encoderLevel=0;
      break;
      case 3:
        encoderLevel=0;
      break;
      case 4:
        encoderLevel=0;
      break;
      case 5:
        encoderLevel=0;
      break;
      case 6:
        encoderLevel=0;
      break;
      default:
        System.out.println("Invalid");
      break;
    }
      Robot.moveElev.setSetpoint(encoderLevel);
    */
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(Robot.ds.isOperatorControl() || Robot.ds.isTest()){
        return false;
    }else{
        return true;
    }
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.drive.stop();
    Robot.upper.move(0);
    
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
