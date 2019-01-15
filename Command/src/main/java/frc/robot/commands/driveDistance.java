/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.PID;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.constants;
/**
 * An example command.  You can replace me with your own command.
 */
public class driveDistance extends Command {
  int inches;
  public driveDistance(int i) {
    inches=i;
    // Use requires() here to declare subsystem dependencies
    requires(Robot.drive);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    PID dist= new PID(Robot.drive.leftMain,Robot.drive.rightMain);
    PID ang= new PID(Robot.drive.gyro);
    Timer time= new Timer();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.drive.resetGyro();
      PID pidSpeed= new PID(Robot.drive.leftMain,Robot.drive.rightMain);
      PID pidAngle= new PID(Robot.drive.gyro);
      pidSpeed.setSetpoint((int)((inches/constants.wheelDiameter)*4096));
      pidAngle.setSetpoint(0);
      if(pidSpeed.distanceSpeed()>0.2){
        drive(pidSpeed.distanceSpeed(),pidAngle.angle());
      }else {
        if(pidSpeed.avgEncoder>pidSpeed.setpoint-60 && pidSpeed.avgEncoder<pidSpeed.setpoint+60){
            time.reset();
            time.start();
            hasLeft=false;
            if(time.get()<=2){
              drive(0.2,pidAngle.angle());
            }else{
              drive(0,0);
            }
        }else{
          drive(pidSpeed.distanceSpeed(),pidAngle.angle());
        }
      }
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
