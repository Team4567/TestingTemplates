/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Relay.Value;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Compressor;
/**
 * Add your docs here.
 */
public class ScoringMech extends Subsystem {
  DoubleSolenoid backBall, frontHatch;
  public ScoringMech(){
    backBall = new DoubleSolenoid( Constants.scoringPCM, 0, 1);
    frontHatch = new DoubleSolenoid( Constants.scoringPCM, 2, 3);
  }
  public void moveBackBall( DoubleSolenoid.Value v ){
    backBall.set( v );
  }
  public void moveFrontHatch( DoubleSolenoid.Value v ){
    frontHatch.set ( v );
  }
  public void dropAllPistons(){
    boolean done = false;
    for( int i = 0; i <= 20; i++ ){
      moveBackBall( DoubleSolenoid.Value.kReverse );
      moveFrontHatch( DoubleSolenoid.Value.kReverse );
      if( i == 20 ){
        done = true;
      }
    }
    if( done ){
      moveBackBall( DoubleSolenoid.Value.kOff );
      moveFrontHatch( DoubleSolenoid.Value.kOff );
    }
  }
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
