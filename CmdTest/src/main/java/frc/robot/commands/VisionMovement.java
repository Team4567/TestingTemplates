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
public class VisionMovement extends CommandGroup {
  /**
   * Add your docs here.
   */
  private double ang1,d1,ang2,d2;
  public VisionMovement() {
    requires(Robot.drive);
    NetworkTableInstance inst= NetworkTableInstance.getDefault();
    NetworkTable nt= inst.getTable("TargetInfo");
    NetworkTableEntry eAngleToPerp      = nt.getEntry("AngleToPerp");     // Perpendicular from hatch wall
    NetworkTableEntry eDistanceToPerp   = nt.getEntry("DistanceToPerp");  // Perpendicular from hatch wall
    NetworkTableEntry eAngleToTarget    = nt.getEntry("AngleToTarget");   // Turn to face target
    NetworkTableEntry eDistanceToTarget = nt.getEntry("DistanceToTarget");
    NetworkTableEntry eTargetPathValid  = nt.getEntry("TargetPathValid");
   
    if(eTargetPathValid.getBoolean(false)){
      addSequential( new TurnAngle( 1, new SimpleTurnP( .05, Constants.gyroP , .5 , .15 , 1 ) ) );
      
      addSequential( new DriveDistance( 1, new SimpleMotorP( .02, Constants.motorP, .5, Constants.minValY, 200 ) ) );
      
      addSequential( new TurnAngle( 1, new SimpleTurnP( .05, Constants.gyroP , .5 , .15 , 1 ) ) );
      
      addSequential( new DriveDistance( 1, new SimpleMotorP( .02, Constants.motorP, .5, Constants.minValY, 200 ) ) );
      
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
}
