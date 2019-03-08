/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.DoubleSolenoid;
/**
 * Add your docs here.
 */
public class PlatformClimber extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  DoubleSolenoid front1, front2, back;
  public PlatformClimber(){
    front1 = new DoubleSolenoid( Constants.platformPCM, 0, 7 );
    front2 = new DoubleSolenoid( Constants.platformPCM, 1, 6 );
    back = new DoubleSolenoid( Constants.platformPCM, 2, 5 );
  }
  public void setFronts( DoubleSolenoid.Value v ){
    front1.set( v );
    front2.set( v );
  }
  public void setBack( DoubleSolenoid.Value v ){
    back.set( v );
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
