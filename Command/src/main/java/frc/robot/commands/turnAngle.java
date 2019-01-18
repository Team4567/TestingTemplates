/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.PID;
import edu.wpi.first.wpilibj.Timer;

public class turnAngle extends Command {
  int angle;
  PID ang;
  Timer time;
  boolean isDone;
  public turnAngle(int a) {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(Robot.drive);
    angle=a;
    PID ang= new PID(Robot.drive.gyro);;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    isDone=false;
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.drive.resetGyro();
      ang.setSetpoint(angle);
      if(ang.angle()>0.2){
        Robot.drive.drive(0,ang.angle());
      }else {
        if(Robot.drive.getYaw()>ang.setpoint-3 && Robot.drive.getYaw()<ang.setpoint+3){
            time.reset();
            time.start();
            
            if(time.get()<=2){
              Robot.drive.drive(0,.25*ang.angle());
            }else{
              isDone=true;
            }
        }else{
          Robot.drive.drive(0,ang.angle());
        }
      }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if(isDone){
      return true;
    }else{
    return false;
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
