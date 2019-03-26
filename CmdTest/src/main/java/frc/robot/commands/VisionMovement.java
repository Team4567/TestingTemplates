/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.commands.*;
import frc.robot.calculators.*;
public class VisionMovement extends CommandGroup {
  /**
   * Add your docs here.
   */
  NetworkTableEntry eAngleToPerp, eDistanceToPerp, eAngleToTarget, eDistanceToTarget, eTargetPathValid;
  private double ang1, d1, ang2, d2;
  public VisionMovement() {
    requires( Robot.drive );
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable nt = inst.getTable( "TargetInfo" );
    eAngleToPerp = nt.getEntry( "AngleToPerp" );     // Perpendicular from hatch wall
    eDistanceToPerp = nt.getEntry( "DistanceToPerp" );  // Perpendicular from hatch wall
    eAngleToTarget = nt.getEntry( "AngleToTarget" );   // Turn to face target
    eDistanceToTarget = nt.getEntry( "DistanceToTarget" );
    eTargetPathValid = nt.getEntry( "TargetPathValid" );
    
    if( eTargetPathValid.getBoolean( false ) ){
      addSequential( new TurnAngle( -1*ang1, new SimpleTurnP( .05, Constants.gyroP , .3 , .15 , 1 ) ) );
      
      addSequential( new DriveDistance( -1*d1, new SimpleMotorP( .02, Constants.motorP, .3, Constants.minValY, 200 ) ) );
      
      addSequential( new TurnAngle( -1*ang2, new SimpleTurnP( .05, Constants.gyroP , .3 , .15 , 1 ) ) );
      
      addSequential( new DriveDistance( -1*d2, new SimpleMotorP( .02, Constants.motorP, .3, Constants.minValY, 200 ) ) );
      
    } else {
      
    }
    
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.
  }
  @Override
  protected void initialize() {
    super.initialize();
    ang1 = eAngleToPerp.getDouble( 0 );
    d1 = eDistanceToPerp.getDouble( 0 );
    ang2 = eAngleToTarget.getDouble( 0 );
    d2 = eDistanceToTarget.getDouble( 0 );
    if( ang1 == 0 || d1 == 0 || ang2 == 0 || d2 == 0 ){
      System.out.println( "Code failed, value = 0" );
      cancel();
    }
  }
}
