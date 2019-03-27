/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.robot.Constants;
import frc.robot.calculators.*;
import frc.robot.commands.*;
public class VisionBackup extends Command {
  private double ang1, d1, ang2, d2;
  private boolean trans1, trans2, trans3;
  private TurnAngle turn; 
  private DriveDistance drive;
  private NetworkTableEntry eAngleToPerp, eDistanceToPerp, eAngleToTarget, eDistanceToTarget, eTargetPathValid;
  private NetworkTableInstance inst = NetworkTableInstance.getDefault();
  private NetworkTable nt = inst.getTable( "TargetInfo" );
  public VisionBackup() {
    requires( Robot.drive );
    requires( Robot.upper );
    requires( Robot.platformer );
    requires( Robot.score );
    turn = new TurnAngle( new SimpleTurnP( .05, Constants.gyroP , .3 , .15 , 1 ) );
    drive = new DriveDistance( new SimpleMotorP( .02, Constants.motorP, .3, Constants.minValY, 200 ) );
    eAngleToPerp = nt.getEntry( "AngleToPerp" );     // Perpendicular from hatch wall
    eDistanceToPerp = nt.getEntry( "DistanceToPerp" );  // Perpendicular from hatch wall
    eAngleToTarget = nt.getEntry( "AngleToTarget" );   // Turn to face target
    eDistanceToTarget = nt.getEntry( "DistanceToTarget" );
    eTargetPathValid = nt.getEntry( "TargetPathValid" );
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    if( eTargetPathValid.getBoolean( false ) ){
      ang1 = eAngleToPerp.getDouble( 0 );
      d1 = eDistanceToPerp.getDouble( 0 );
      ang2 = eAngleToTarget.getDouble( 0 );
      d2 = eDistanceToTarget.getDouble( 0 );
      turn.setSetpoint( ang1 );
      turn.start();
      drive.setSetpointInches( d1 );
    } else {
      System.out.println( "Invalid Path" );
      cancel(); 
    }
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    if( turn.isFinished() && !trans1 ){
      drive.start();
      turn.setSetpoint( ang2 );
      trans1 = true;
    }
    if( drive.isFinished() && !trans2 ){
      turn.start();
      drive.setSetpointInches( d2 );
      trans2 = true;
    }
    if( turn.isFinished() && !trans3 ){
      drive.start();
      trans3 = true;
    }

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return trans3 && drive.isFinished();
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
