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
 */ // 0145 left
public class PlatformClimber extends Subsystem {
  // Put methods for controlling this subsystem
  // here. Call these from Commands.
  DoubleSolenoid front1, front2, back;
  public PlatformClimber(){
    // Left
    //front1 = new DoubleSolenoid( 42, 0, 1 );
    // Left
    front2 = new DoubleSolenoid( 42, 2, 3 );
    // Right
    back = new DoubleSolenoid( 10, 0, 1 );
  }
  public void setFronts( DoubleSolenoid.Value v ){
    //front1.set( v );
    front2.set( v );
  }
  public void setBack( DoubleSolenoid.Value v ){
    back.set( v );
  }
  public void dropAllPistons(){
    boolean done = false;
    for( int i = 0; i <= 20; i++ ){
      setFronts( DoubleSolenoid.Value.kReverse );
      setBack( DoubleSolenoid.Value.kReverse );
      if( i == 20 ){
        done = true;
      }
    }
    if( done ){
      setFronts( DoubleSolenoid.Value.kOff );
      setBack( DoubleSolenoid.Value.kOff );
    }
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
