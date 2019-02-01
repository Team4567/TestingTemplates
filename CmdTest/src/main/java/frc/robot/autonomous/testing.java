/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.autonomous;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.commands.*;
import frc.robot.enums.want;
import frc.robot.Constants;

public class testing extends CommandGroup {
  /**
   * Add your docs here.
   */
  public testing() {
    requires(Robot.drive);

    // parameter to SimpleMotorP: maxOutChange, Kp, maxOut, minOut, closeEnough
    addSequential(new DriveDistance(10*12,new SimpleMotorP( 0.10, Constants.motorP, 0.5, Constants.minValY, Constants.closeEnough ) ));
    addSequential(new turnAngle(180,new simpleTurnP(Robot.drive.gyro,true)));
    addSequential(new DriveDistance(10*12,new SimpleMotorP( 0.10, Constants.motorP, 0.5, Constants.minValY, Constants.closeEnough )));
    //addSequential(new driveDistance(((10*12)/constants.wheelCirc)*4096));
    //addSequential(new turnAngle(180,false));
    //addSequential(new driveDistance(((10*12)/constants.wheelCirc)*4096));
    // Add Commands here:
    //addSequential(new alignVision(want.tape,false));
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
